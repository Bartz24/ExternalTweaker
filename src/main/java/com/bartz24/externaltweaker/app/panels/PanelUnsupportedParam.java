package com.bartz24.externaltweaker.app.panels;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.bartz24.externaltweaker.app.AppFrame;

public class PanelUnsupportedParam extends PanelData {
	public String data;

	public PanelUnsupportedParam(PanelParameterEdit parent) {
		super(parent);

		JLabel lblUnsupported = new JLabel("UNSUPPORTED TYPE");
		lblUnsupported.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout_2 = new GroupLayout(this);
		groupLayout_2
				.setHorizontalGroup(groupLayout_2.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
						groupLayout_2.createSequentialGroup().addGap(115)
								.addComponent(lblUnsupported, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
								.addGap(122)));
		groupLayout_2.setVerticalGroup(groupLayout_2.createParallelGroup(Alignment.LEADING).addGroup(groupLayout_2
				.createSequentialGroup().addComponent(lblUnsupported).addContainerGap(229, Short.MAX_VALUE)));
		this.setLayout(groupLayout_2);
	}

	public String exportData() {
		return data;
	}

	public void importData(String input) {
		data = input;
	}

	@Override
	public void setListeners() {
		
	}
}
