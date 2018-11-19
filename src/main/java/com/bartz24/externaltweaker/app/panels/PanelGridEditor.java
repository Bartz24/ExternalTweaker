package com.bartz24.externaltweaker.app.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;

public class PanelGridEditor extends JPanel {
	private boolean importing;
	private JPanel gridPanel;
	private PanelArrayParam array;
	private PanelData editPanel;
	private int curX = -1, curY = -1;

	public PanelGridEditor(PanelArrayParam param) {
		array = param;
		gridPanel = new JPanel();

		editPanel = new PanelData(null);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
				groupLayout.createSequentialGroup().addContainerGap(36, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(editPanel, GroupLayout.PREFERRED_SIZE, 560, GroupLayout.PREFERRED_SIZE)
								.addComponent(gridPanel, GroupLayout.PREFERRED_SIZE, 561, GroupLayout.PREFERRED_SIZE))
						.addGap(27)));
		groupLayout
				.setVerticalGroup(
						groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup().addGap(25)
										.addComponent(gridPanel, GroupLayout.PREFERRED_SIZE, 327,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(editPanel, GroupLayout.PREFERRED_SIZE, 171,
												GroupLayout.PREFERRED_SIZE)
										.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		gridPanel.setLayout(new GridLayout(param.data[0].length, param.data.length, 0, 0));

		setGrid();

		setLayout(groupLayout);
	}

	private void setGrid() {
		gridPanel.removeAll();
		for (int y = 0; y < array.data.length; y++) {
			for (int x = 0; x < array.data[0].length; x++) {
				final int xx = x;
				final int yy = y;
				JButton gridButton = new JButton(array.data[y][x]);
				gridButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						setEditPanel(xx, yy);
					}
				});
				gridPanel.add(gridButton);
			}
		}
	}

	private void setEditPanel(int x, int y) {
		if (curX != -1 && curY != -1) {
			array.data[curX][curY] = editPanel.exportData();
		}
		String type = array.parentPanel.getSubtype().substring(0, array.parentPanel.getSubtype().indexOf("[")).trim();
		String subtype = type.startsWith("optional.") ? type.substring("optional.".length()) : type;
		PanelData p = array.parentPanel.getNewPanelData(subtype);
		p.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		p.setToolTipText("Located at " + (x + 1) + ", " + (y + 1));
		editPanel = p;
		curX = x;
		curY = y;
	}

	public void onClose() {
		if (curX != -1 && curY != -1) {
			array.data[curX][curY] = editPanel.exportData();
		}
	}
}
