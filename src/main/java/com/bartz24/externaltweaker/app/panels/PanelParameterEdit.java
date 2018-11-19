package com.bartz24.externaltweaker.app.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import com.bartz24.externaltweaker.app.AppFrame;
import com.bartz24.externaltweaker.app.JPlaceholderTextField;
import com.bartz24.externaltweaker.app.Strings;

public class PanelParameterEdit extends JPanel {
	public int paramNum;
	public String paramType;
	public PanelData editPanel;
	public JPlaceholderTextField txtName;
	public AppFrame mainFrame;
	protected boolean importing;
	private JButton btnCopy;
	public JButton btnPaste;
	private JCheckBox chkOptional;


	public PanelParameterEdit(int parameterNum, String parameterType, AppFrame mainframe, boolean disableParameterName) {
		this(parameterNum, parameterType, mainframe);
		txtName.setEnabled(false);
	}
	
	public PanelParameterEdit(int parameterNum, String parameterType, AppFrame mainframe) {
		paramNum = parameterNum;
		paramType = parameterType;
		mainFrame = mainframe;
		this.setPreferredSize(new Dimension(this.getPreferredSize().width, getPanelHeight()));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, getPanelHeight()));
		this.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		JLabel lblParamType = new JLabel(paramType.replace("optional.", "(Optional) "));
		lblParamType.setHorizontalAlignment(SwingConstants.LEFT);

		editPanel = getNewPanelData();

		txtName = new JPlaceholderTextField("Parameter " + paramNum);
		txtName.setDisabledTextColor(UIManager.getColor("Button.disabledText"));
		txtName.setColumns(10);

		JPanel panel = new JPanel();

		btnCopy = new JButton("Copy");

		btnPaste = new JButton("Paste");
		btnPaste.setEnabled(paramType.equals(mainFrame.copyType));

		chkOptional = new JCheckBox("");
		chkOptional.setToolTipText("(Optional Only) Allow");
		chkOptional.setEnabled(isOptional());
		chkOptional.setSelected(!isOptional() ? true : false);
		GroupLayout gl_panel = new GroupLayout(this);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup().addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(Alignment.LEADING)
												.addComponent(editPanel, GroupLayout.DEFAULT_SIZE, 421,
														Short.MAX_VALUE)
												.addGroup(gl_panel.createSequentialGroup()
														.addComponent(txtName, GroupLayout.DEFAULT_SIZE, 38,
																Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(chkOptional).addGap(10)
														.addComponent(lblParamType, GroupLayout.DEFAULT_SIZE, 62,
																Short.MAX_VALUE)
														.addGap(5)
														.addComponent(panel, GroupLayout.PREFERRED_SIZE, 126,
																GroupLayout.PREFERRED_SIZE)
														.addGap(8).addComponent(btnCopy)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnPaste, GroupLayout.PREFERRED_SIZE, 81,
																GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel
										.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
												.createSequentialGroup().addGap(14).addGroup(gl_panel
														.createParallelGroup(Alignment.LEADING).addGroup(
																Alignment.TRAILING, gl_panel
																		.createSequentialGroup().addGroup(gl_panel
																				.createParallelGroup(Alignment.TRAILING)
																				.addComponent(panel,
																						GroupLayout.PREFERRED_SIZE, 24,
																						GroupLayout.PREFERRED_SIZE)
																				.addGroup(gl_panel
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(btnPaste)
																						.addComponent(btnCopy)))
																		.addGap(7))
														.addGroup(Alignment.TRAILING,
																gl_panel.createSequentialGroup()
																		.addComponent(lblParamType).addGap(12))
														.addGroup(Alignment.TRAILING,
																gl_panel.createSequentialGroup()
																		.addComponent(chkOptional).addGap(9))))
										.addGroup(gl_panel.createSequentialGroup().addGap(17)
												.addComponent(txtName, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)))
								.addComponent(editPanel, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
								.addContainerGap()));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		this.setLayout(gl_panel);
	}

	public PanelData getNewPanelData(String type) {
		if (type.equals("int") || type.equals("double") || type.equals("float"))
			return new PanelNumParam(this);
		else if (type.equals("IItemStack") || type.equals("ILiquidStack") || type.equals("IIngredient"))
			return new PanelStackParam(this);
		else if (type.equals("String"))
			return new PanelStringParam(this);
		else if (type.equals("boolean"))
			return new PanelBooleanParam(this);
		else if (type.endsWith("[]"))
			return new PanelArrayParam(this);
		else
			return new PanelUnsupportedParam(this);
	}

	public PanelData getNewPanelData() {
		return getNewPanelData(getSubtype());
	}

	public int getPanelHeight() {
		if (getSubtype().equals("int") || getSubtype().equals("double") || getSubtype().equals("float"))
			return 100;
		else if (getSubtype().equals("IItemStack") || getSubtype().equals("ILiquidStack")
				|| getSubtype().equals("IIngredient"))
			return 150;
		else if (getSubtype().equals("String"))
			return 100;
		else if (getSubtype().equals("boolean"))
			return 100;
		else if (getSubtype().endsWith("[]"))
			return 500;
		else
			return 100;

	}

	public String getSubtype() {
		return isOptional() ? paramType.substring("optional.".length()) : paramType;
	}

	public boolean isOptional() {
		return paramType.startsWith("optional.");
	}

	public String getParameterName() {
		return txtName.getText();
	}

	public String exportData() {
		if (isOptional() && !chkOptional.isSelected())
			return "~";
		return editPanel.exportData();
	}

	public void importData(String input) {
		if (!Strings.isNullOrEmpty(input) && !(isOptional() && input.equals("~")))
			editPanel.importData(input);
		if (!Strings.isNullOrEmpty(input) && isOptional())
			chkOptional.setSelected(!input.equals("~"));
	}

	public void setListeners() {
		txtName.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				int comboIndex = mainFrame.comboRecipes.getSelectedIndex();
				int index = mainFrame.indexOfRecipeFormat(mainFrame.getCurrentScript().recipes.get(comboIndex).getRecipeFormat());
				if (index >= 0) {
					mainFrame.recipeData.get(index).setParamName(paramNum - 1, txtName.getText());
					((DefaultListModel) (mainFrame.listMethods.getModel()))
							.setElementAt(mainFrame.recipeData.get(index).getRecipeDisplay(), index);
					mainFrame.updateRecipesList(true);
				}
			}

			public void focusGained(FocusEvent e) {
			}
		});

		chkOptional.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!importing) {
					int comboIndex = mainFrame.comboRecipes.getSelectedIndex();
					mainFrame.getCurrentScript().recipes.get(comboIndex).setParameterData(paramNum - 1,
							!chkOptional.isSelected() ? "~" : exportData());
					mainFrame.updateRecipesList(true);
				}
			}
		});

		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.copyData = exportData();
				mainFrame.copyType = paramType;
				mainFrame.lblCopying
						.setText("Currently Copying: " + mainFrame.copyData + " [" + mainFrame.copyType + "]");
				mainFrame.updateParameters();
			}
		});

		btnPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importData(mainFrame.copyData);
				if (!importing) {
					mainFrame.updateParameters();
					mainFrame.updateRecipesList(true);
				}
			}
		});

		editPanel.setListeners();
	}
}
