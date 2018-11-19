package com.bartz24.externaltweaker.app.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.bartz24.externaltweaker.app.Strings;

public class PanelImportExportDialog extends JPanel {
	public JTextField txtPath;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private boolean importing;
	private JCheckBox chkRecipes;
	private JCheckBox chkItems;
	private JCheckBox chkFluids;
	private JCheckBox chkOreDict;
	private JRadioButton rdbtnOverride;
	private JRadioButton rdbtnAdd;

	public PanelImportExportDialog(boolean importing) {
		this.importing = importing;
		txtPath = new JTextField();
		txtPath.setColumns(10);

		JButton btnPath = new JButton("...");
		btnPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileNameExtensionFilter("External Tweaker Data", "etd"));
				fc.setCurrentDirectory(!Strings.isNullOrEmpty(txtPath.getText()) ? new File(txtPath.getText().trim())
						: (new File(System.getProperty("user.dir")
								+ (!PanelImportExportDialog.this.importing ? File.separator + "externalTweaker.etd" : ""))));
				if ((PanelImportExportDialog.this.importing ? fc.showOpenDialog(PanelImportExportDialog.this)
						: fc.showSaveDialog(PanelImportExportDialog.this)) == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getAbsolutePath();
					if (!path.endsWith(".etd"))
						path += ".etd";
					txtPath.setText(path);
				}
			}
		});

		chkRecipes = new JCheckBox("Recipes");
		chkRecipes.setSelected(true);
		chkRecipes.setHorizontalAlignment(SwingConstants.CENTER);

		chkItems = new JCheckBox("Items");
		chkItems.setSelected(true);
		chkItems.setHorizontalAlignment(SwingConstants.CENTER);

		chkFluids = new JCheckBox("Fluids");
		chkFluids.setSelected(true);
		chkFluids.setHorizontalAlignment(SwingConstants.CENTER);

		chkOreDict = new JCheckBox("Ore Dict");
		chkOreDict.setSelected(true);
		chkOreDict.setHorizontalAlignment(SwingConstants.CENTER);

		rdbtnOverride = new JRadioButton("Override Existing");
		rdbtnOverride.setSelected(true);
		buttonGroup.add(rdbtnOverride);
		rdbtnOverride.setHorizontalAlignment(SwingConstants.CENTER);

		rdbtnAdd = new JRadioButton("Add To Existing");
		buttonGroup.add(rdbtnAdd);
		rdbtnAdd.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout
						.createParallelGroup(Alignment.LEADING).addGroup(
								groupLayout.createSequentialGroup().addComponent(rdbtnOverride,
										GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(rdbtnAdd, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
										.addContainerGap())
						.addGroup(
								groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(chkRecipes, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(chkItems, GroupLayout.DEFAULT_SIZE, 57,
																Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(chkFluids, GroupLayout.DEFAULT_SIZE, 58,
																Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(chkOreDict, GroupLayout.DEFAULT_SIZE, 71,
																Short.MAX_VALUE))
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(txtPath, GroupLayout.DEFAULT_SIZE, 377,
																Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnPath)))
										.addGap(8)))));
		groupLayout
				.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(8)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(txtPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(btnPath))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(chkRecipes)
										.addComponent(chkItems).addComponent(chkFluids).addComponent(chkOreDict))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(rdbtnOverride).addComponent(rdbtnAdd))
								.addContainerGap(216, Short.MAX_VALUE)));
		setLayout(groupLayout);
	}

	public boolean[] getSettings() {
		boolean[] vals = new boolean[5];
		vals[0] = chkRecipes.isSelected();
		vals[1] = chkItems.isSelected();
		vals[2] = chkFluids.isSelected();
		vals[3] = chkOreDict.isSelected();
		vals[4] = rdbtnOverride.isSelected();
		return vals;
	}
}
