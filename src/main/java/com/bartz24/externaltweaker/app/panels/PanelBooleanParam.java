package com.bartz24.externaltweaker.app.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.bartz24.externaltweaker.app.Strings;

public class PanelBooleanParam extends PanelData {
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton btnTrue;
private JRadioButton btnFalse;

	public PanelBooleanParam(PanelParameterEdit parent) {
		super(parent);
		
		btnTrue = new JRadioButton("True");
		buttonGroup.add(btnTrue);
		btnTrue.setHorizontalAlignment(SwingConstants.RIGHT);
		
		btnFalse = new JRadioButton("False");
		buttonGroup.add(btnFalse);
		btnFalse.setHorizontalAlignment(SwingConstants.LEFT);
		GroupLayout groupLayout_2 = new GroupLayout(this);
		groupLayout_2.setHorizontalGroup(
			groupLayout_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout_2.createSequentialGroup()
					.addGap(177)
					.addComponent(btnTrue, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnFalse, GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
					.addGap(173))
		);
		groupLayout_2.setVerticalGroup(
			groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout_2.createSequentialGroup()
					.addGroup(groupLayout_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnTrue)
						.addComponent(btnFalse))
					.addContainerGap(277, Short.MAX_VALUE))
		);
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
		return Boolean.toString(btnTrue.isSelected());
	}

	public void importData(String input) {
		if (Strings.isNullOrEmpty(input))
			return;
		parentPanel.importing = true;
		btnTrue.setSelected(Boolean.parseBoolean(input));
		btnFalse.setSelected(!Boolean.parseBoolean(input));
		parentPanel.importing = false;
	}

	public void setListeners() {
		btnTrue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!parentPanel.importing) {
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
				}
			}
		});
		btnFalse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!parentPanel.importing) {
					parentPanel.mainFrame.updateParameters();
					parentPanel.mainFrame.updateRecipesList(true);
				}
			}
		});

	}
}
