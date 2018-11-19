package com.bartz24.externaltweaker.app.panels;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;

import com.bartz24.externaltweaker.app.Strings;

public class PanelStringParam extends PanelData {

	JTextField textField;

	public PanelStringParam(PanelParameterEdit parent) {
		super(parent);
		textField = new JTextField();
		GroupLayout groupLayout_2 = new GroupLayout(this);
		groupLayout_2.setHorizontalGroup(groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout_2.createSequentialGroup().addGap(171)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE).addGap(161)));
		groupLayout_2.setVerticalGroup(groupLayout_2.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout_2
						.createSequentialGroup().addComponent(textField, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(16, Short.MAX_VALUE)));
		this.setLayout(groupLayout_2);
	}

	public String exportData() {
		if (Strings.isNullOrEmpty(textField.getText()))
			return "BLANK";
		return "\"" + textField.getText() + "\"";
	}

	public void importData(String input) {
		if (Strings.isNullOrEmpty(input) || input.equals("BLANK"))
			textField.setText("BLANK");
		else
			textField.setText(input.substring(1, input.length() - 1));
	}

	public void setListeners() {
		textField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				parentPanel.mainFrame.updateParameters();
				parentPanel.mainFrame.updateRecipesList(true);
			}

			public void focusGained(FocusEvent e) {
			}
		});
	}
}
