package com.bartz24.externaltweaker.app.panels;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bartz24.externaltweaker.app.Strings;

public class PanelNumParam extends PanelData {

	JSpinner spinner;

	public PanelNumParam(PanelParameterEdit parent) {
		super(parent);
		spinner = new JSpinner(getModel());
		GroupLayout groupLayout_2 = new GroupLayout(this);
		groupLayout_2.setHorizontalGroup(groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout_2.createSequentialGroup().addGap(171)
						.addComponent(spinner, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE).addGap(161)));
		groupLayout_2.setVerticalGroup(groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout_2
						.createSequentialGroup().addComponent(spinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(16, Short.MAX_VALUE)));
		this.setLayout(groupLayout_2);
	}

	private SpinnerNumberModel getModel() {
		if (parentPanel.getSubtype().equals("int"))
			return new SpinnerNumberModel(0, null, null, 1);
		else if (parentPanel.getSubtype().equals("double"))
			return new SpinnerNumberModel(0d, null, null, 1d);
		else if (parentPanel.getSubtype().equals("float"))
			return new SpinnerNumberModel(0f, null, null, 1f);
		return new SpinnerNumberModel(0, null, null, 1);
	}

	public String exportData() {
		return spinner.getValue().toString();
	}

	public void importData(String input) {
		if (Strings.isNullOrEmpty(input))
			return;
		parentPanel.importing = true;
		if (parentPanel.getSubtype().equals("int"))
			spinner.setValue(Integer.parseInt(input));
		else if (parentPanel.getSubtype().equals("double"))
			spinner.setValue(Double.parseDouble(input));
		else if (parentPanel.getSubtype().equals("float"))
			spinner.setValue(Float.parseFloat(input));
		parentPanel.importing = false;
	}

	public void setListeners() {
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				if (!parentPanel.importing) {
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
				}
			}
		});

	}
}
