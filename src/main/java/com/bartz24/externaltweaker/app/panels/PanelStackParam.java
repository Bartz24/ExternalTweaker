package com.bartz24.externaltweaker.app.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bartz24.externaltweaker.app.Strings;

public class PanelStackParam extends PanelData {

	private JButton btnCurSel;
	private JCheckBox chkMeta;
	public JTextField stackEdit;
	private JSpinner spinnerCount;

	public PanelStackParam(PanelParameterEdit parent) {
		super(parent);
		stackEdit = new JTextField();

		btnCurSel = new JButton("Use Current Selection");

		JPanel countPanel = new JPanel();
		FlowLayout fl_countPanel = (FlowLayout) countPanel.getLayout();

		JLabel lblCount = new JLabel("Count");
		countPanel.add(lblCount);

		spinnerCount = new JSpinner();
		spinnerCount.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinnerCount.setPreferredSize(new Dimension(60, 20));
		countPanel.add(spinnerCount);

		chkMeta = new JCheckBox("Any Metadata/Damage");
		chkMeta.setEnabled(!parentPanel.getSubtype().equals("ILiquidStack"));
		chkMeta.setHorizontalAlignment(SwingConstants.CENTER);

		JButton btnAdvOption = new JButton("Open Advanced Options (WIP)");
		btnAdvOption.setEnabled(false);
		GroupLayout groupLayout_2 = new GroupLayout(this);
		groupLayout_2.setHorizontalGroup(
			groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout_2.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout_2.createSequentialGroup()
							.addComponent(btnCurSel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(stackEdit, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
						.addGroup(groupLayout_2.createSequentialGroup()
							.addGap(5)
							.addComponent(countPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chkMeta)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAdvOption, GroupLayout.PREFERRED_SIZE, 184, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout_2.setVerticalGroup(
			groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout_2.createSequentialGroup()
					.addGroup(groupLayout_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCurSel)
						.addComponent(stackEdit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout_2.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout_2.createParallelGroup(Alignment.BASELINE)
							.addComponent(chkMeta)
							.addComponent(btnAdvOption))
						.addComponent(countPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(84, Short.MAX_VALUE))
		);
		this.setLayout(groupLayout_2);
	}

	public void update() {
		updateCurSelBtn((parentPanel.mainFrame.table.getSelectedRow() >= 0
				&& parentPanel.mainFrame.table.getSelectedColumn() >= 0)
						? parentPanel.mainFrame.table.getValueAt(parentPanel.mainFrame.table.getSelectedRow(), 0)
								.toString()
						: "");
	}

	public void updateCurSelBtn(String curSel) {
		String s = curSel.replace("<", "").replace(">", "");
		boolean enabled = true;
		if (parentPanel.getSubtype().equals("IItemStack") && (s.startsWith("ore") || s.startsWith("liquid")))
			enabled = false;
		else if (parentPanel.getSubtype().equals("IIngredient") && s.startsWith("liquid"))
			enabled = false;
		else if (parentPanel.getSubtype().equals("ILiquidStack") && !s.startsWith("liquid"))
			enabled = false;
		else if (Strings.isNullOrEmpty(s))
			enabled = false;
		this.btnCurSel.setEnabled(enabled);
	}

	public String exportData() {
		if (!Strings.isNullOrEmpty(stackEdit.getText())) {
			try {
				String base = stackEdit.getText();
				String[] stackData = base.substring(base.indexOf("<"), base.indexOf(">") + 1).replace("<", "")
						.replace(">", "").split(":");

				if (parentPanel.getSubtype().equals("IItemStack") && chkMeta.isSelected()) {
					if (stackData.length < 3) {
						String[] newData = new String[3];
						System.arraycopy(stackData, 0, newData, 0, stackData.length);
						stackData = newData;
					}
					stackData[2] = "*";
				}
				String stackNew = "<" + stackData[0] + ":" + stackData[1]
						+ (stackData.length > 2 ? ":" + stackData[2] : "") + ">";
				base = stackNew + base.substring(base.indexOf(">") + 1)
						+ ((Integer) spinnerCount.getValue() > 1 ? " * " + spinnerCount.getValue() : "");
				return base;
			} catch (Exception e) {
				return "null";
			}
		}
		return "null";
	}

	public void importData(String input) {
		if(Strings.isNullOrEmpty(input) || input.equals("null"))
			stackEdit.setText("null");
		if (!Strings.isNullOrEmpty(input)) {
			parentPanel.importing = true;
			try {
				input = input.trim();
				String outside = input.substring(input.indexOf(">") + 1);
				if (outside.contains("*"))
					spinnerCount
							.setValue(Integer.parseInt(outside.substring(outside.indexOf("*") + 1).replace(" ", "")));

				String[] stackData = input.substring(input.indexOf("<"), input.indexOf(">") + 1).replace("<", "")
						.replace(">", "").split(":");
				if (stackData.length > 2)
					chkMeta.setSelected(stackData[2].equals("*"));
				String text = "";
				for (int i = 0; i < stackData.length; i++) {
					text += stackData[i];
					if (i < stackData.length - 1)
						text += ":";
				}
				if (outside.contains("*"))
					outside = outside.substring(0, outside.indexOf("*") - 1).trim();
				text = "<" + text + ">" + outside;
				stackEdit.setText(text);
			} catch (Exception e) {
				stackEdit.setText(input);
			}
			parentPanel.importing = false;
		}
	}

	public void setListeners() {
		stackEdit.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				if (!parentPanel.importing) {
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
				}
			}

			public void focusGained(FocusEvent e) {
			}
		});
		btnCurSel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!parentPanel.importing && parentPanel.mainFrame.table.getSelectedRow() >= 0
						&& parentPanel.mainFrame.table.getSelectedColumn() >= 0) {
					stackEdit.setText(parentPanel.mainFrame.table
							.getValueAt(parentPanel.mainFrame.table.getSelectedRow(), 0).toString());
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
				}
			}
		});
		spinnerCount.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				if (!parentPanel.importing) {
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
				}
			}
		});

		chkMeta.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				if (!Strings.isNullOrEmpty(stackEdit.getText()) && !stackEdit.getText().startsWith("<ore:")) {
					if (!parentPanel.importing) {
						parentPanel.mainFrame.updateParameters();
						parentPanel.mainFrame.updateRecipesList(true);
					}
				}
			}
		});
	}
}
