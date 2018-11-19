package com.bartz24.externaltweaker.app.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bartz24.externaltweaker.app.Strings;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelArrayParam extends PanelData {

	public String[][] data;

	private JPanel backPanel;
	private JSpinner spinnerRow;
	private JSpinner spinnerSizeX;
	private JSpinner spinnerSizeY;
	public List<PanelData> paramPanels = new ArrayList();
	private static boolean changingSize;

	public PanelArrayParam(PanelParameterEdit parent) {
		super(parent);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		backPanel = new JPanel();
		backPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		scrollPane.setViewportView(backPanel);
		backPanel.setLayout(new BoxLayout(backPanel, BoxLayout.Y_AXIS));

		spinnerSizeX = new JSpinner();
		spinnerSizeX.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));

		spinnerSizeY = new JSpinner();
		spinnerSizeY.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));

		JLabel lblNewLabel = new JLabel("x");

		JLabel lblNewLabel_1 = new JLabel("Grid Size");

		spinnerRow = new JSpinner();
		spinnerRow.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));

		JLabel lblNewLabel_2 = new JLabel("Current Row");
		
		JButton btnOpenGridEditor = new JButton("Grid Editor");
		btnOpenGridEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanelGridEditor gridPanel = new PanelGridEditor(PanelArrayParam.this);
				int input = JOptionPane.showOptionDialog(PanelArrayParam.this, gridPanel, "Grid Editor", JOptionPane.OK_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Close" }, "Close");

				gridPanel.onClose();
				updateCurrentRow();
			}
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
					.addGap(0))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(485, Short.MAX_VALUE)
					.addComponent(btnOpenGridEditor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spinnerRow, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spinnerSizeX, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spinnerSizeY, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerSizeX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(spinnerSizeY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1)
						.addComponent(spinnerRow, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2)
						.addComponent(btnOpenGridEditor))
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
		spinnerRow.setEnabled(isDoubleArray());
		spinnerSizeY.setEnabled(isDoubleArray());
		data = new String[1][1];
		updateCurrentRow();
	}

	private boolean isDoubleArray() {
		String bracketsOnly = parentPanel.getSubtype().substring(parentPanel.getSubtype().indexOf("["),
				parentPanel.getSubtype().length());
		return bracketsOnly.length() / 2 == 2;
	}

	public String exportData() {
		if (data == null)
			return "";
		if (isDoubleArray()) {
			String returning = "[";
			for (int i = 0; i < (Integer) spinnerSizeY.getValue(); i++) {
				returning += Arrays.toString(data[i]);
				if (i < (Integer) spinnerSizeY.getValue() - 1)
					returning += ", ";
			}
			return returning + "]";
		} else {
			return Arrays.toString(data[0]);
		}
	}

	public void importData(String input) {
		if (Strings.isNullOrEmpty(input))
			return;
		parentPanel.importing = true;

		if (!isDoubleArray()) {
			String[] addData = getSingleArray(input);
			data = new String[1][addData.length];
			data[0] = addData;
		} else
			data = getDoubleArray(input);
		if (data.length == 0 || data[0].length == 0)
			data = new String[1][1];

		spinnerSizeX.setValue(data[0].length);
		spinnerSizeY.setValue(data.length);
		((SpinnerNumberModel) spinnerRow.getModel()).setMaximum((Integer) spinnerSizeY.getValue());
		updateCurrentRow();
		parentPanel.importing = false;
	}

	private String[] getSingleArray(String data) {
		String newData = data.substring(1, data.length() - 1);
		
		HashMap<Integer, String> arrayIndexes = new HashMap();
		boolean changedSomething = true;
		while (changedSomething) {
			changedSomething = false;
			while (newData.contains(".withTag(") && newData.contains(")")
					&& newData.indexOf(".withTag(") < newData.indexOf(")")
					&& !newData.substring(newData.indexOf(".withTag("), newData.indexOf(")") + 1)
							.contains(".onlyWithTag(")) {
				String arg = newData.substring(newData.indexOf(".withTag("), newData.indexOf(")") + 1);
				newData = newData.replace(arg, "~" + arrayIndexes.size() + "~");
				arrayIndexes.put(arrayIndexes.size(), arg);
				changedSomething = true;
			}
			if (!changedSomething)
				while (newData.contains(".onlyWithTag(") && newData.contains(")")
						&& newData.indexOf(".onlyWithTag(") < newData.indexOf(")")) {
					String arg = newData.substring(newData.indexOf(".onlyWithTag("),
							newData.indexOf(")") + 1);
					newData = newData.replace(arg, "~" + arrayIndexes.size() + "~");
					arrayIndexes.put(arrayIndexes.size(), arg);
					changedSomething = true;
				}
			if (!changedSomething)
				while (newData.contains("[[") && newData.contains("]]")
						&& newData.indexOf("[[") < newData.indexOf("]]")) {
					String arg = newData.substring(newData.indexOf("[["), newData.indexOf("]]") + 2);
					newData = newData.replace(arg, "~" + arrayIndexes.size() + "~");
					arrayIndexes.put(arrayIndexes.size(), arg);
					changedSomething = true;

				}
			if (!changedSomething)
				while (newData.contains("[") && newData.contains("]")
						&& newData.indexOf("[") < newData.indexOf("]")) {
					String arg = newData.substring(newData.indexOf("["), newData.indexOf("]") + 1);
					newData = newData.replace(arg, "~" + arrayIndexes.size() + "~");
					arrayIndexes.put(arrayIndexes.size(), arg);
					changedSomething = true;
				}
		}

		List<String> paramList = new ArrayList(Arrays.asList(newData.split(",")));
		for (int i = 0; i < paramList.size(); i++) {
			paramList.set(i, paramList.get(i).trim());
			boolean changed = true;
			String p = paramList.get(i).substring(0, paramList.get(i).length());
			while (changed) {
				changed = false;
				for (int i2 = 0; i2 < arrayIndexes.size(); i2++) {
					if (p.contains("~" + i2 + "~")) {
						p = p.replace("~" + i2 + "~", arrayIndexes.get(i2));
						changed = true;
					}
				}
			}
			paramList.set(i, p);
		}
		
		return paramList.toArray(new String[paramList.size()]);
	}

	private String[][] getDoubleArray(String data) {
		String newData = data.substring(1, data.length() - 1);
		String[] array = newData.split(Pattern.quote("], ["));
		List<String[]> listArrays = new ArrayList();
		for (int i = 0; i < array.length; i++) {
			if (i > 0)
				array[i] = "[" + array[i];
			if (i < array.length - 1)
				array[i] += "]";
			listArrays.add(getSingleArray(array[i]));
		}
		return listArrays.toArray(new String[listArrays.size()][listArrays.get(0).length]);
	}

	public void update() {
		for (int i = 0; i < paramPanels.size(); i++) {
			paramPanels.get(i).update();
			data[(Integer) spinnerRow.getValue() - 1][i] = paramPanels.get(i).exportData();
		}

		spinnerSizeX.setValue(data[0].length);
		spinnerSizeY.setValue(data.length);
		((SpinnerNumberModel) spinnerRow.getModel()).setMaximum((Integer) spinnerSizeY.getValue());
	}

	private void updateCurrentRow() {

		backPanel.removeAll();
		paramPanels.clear();
		if (data != null && (Integer) spinnerRow.getValue() - 1 >= 0) {

			for (int i = 0; i < (Integer) spinnerSizeX.getValue(); i++)
				addParameter(parentPanel.getSubtype().substring(0, parentPanel.getSubtype().indexOf("[")), i);

			for (int i = 0; i < paramPanels.size(); i++) {
				paramPanels.get(i).importData(data[(Integer) spinnerRow.getValue() - 1][i]);
			}
			update();
		}
		backPanel.revalidate();
		backPanel.repaint();
	}

	private void addParameter(String type, int index) {
		type = type.trim();
		String subtype = type.startsWith("optional.") ? type.substring("optional.".length()) : type;
		PanelData p = parentPanel.getNewPanelData(subtype);
		p.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p.setToolTipText("Located at " + (index + 1) + ", " + spinnerRow.getValue().toString());
		p.setListeners();
		backPanel.add(p);
		paramPanels.add(p);
	}

	public void setListeners() {
		spinnerRow.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				if (!parentPanel.importing) {
					updateCurrentRow();
				}
			}
		});
		spinnerSizeX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				if (!parentPanel.importing && !changingSize) {
					changingSize = true;
					String[][] newData = new String[(Integer) spinnerSizeY.getValue()][(Integer) spinnerSizeX.getValue()];
					for (int y = 0; y < data.length; y++) {
						for (int x = 0; x < data[0].length; x++) {
							if (y >= newData.length || x >= newData[0].length)
								continue;
							newData[y][x] = data[y][x];
						}
					}
					data = newData;

					updateCurrentRow();
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
					changingSize = false;
				}
			}
		});
		spinnerSizeY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				if (!parentPanel.importing && !changingSize) {
					changingSize = true;
					String[][] newData = new String[(Integer) spinnerSizeY.getValue()][(Integer) spinnerSizeX.getValue()];
					for (int y = 0; y < data.length; y++) {
						for (int x = 0; x < data[0].length; x++) {
							if (y >= newData.length || x >= newData[0].length)
								continue;
							newData[y][x] = data[y][x];
						}
					}
					data = newData;
					if ((Integer) spinnerRow.getValue() > (Integer) spinnerSizeY.getValue())
						spinnerRow.setValue(spinnerSizeY.getValue());

					updateCurrentRow();
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
					changingSize = false;
				}
			}
		});

	}
}
