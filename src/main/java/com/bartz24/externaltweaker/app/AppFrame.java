package com.bartz24.externaltweaker.app;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.bartz24.externaltweaker.app.data.ETActualRecipe;
import com.bartz24.externaltweaker.app.data.ETRecipeData;
import com.bartz24.externaltweaker.app.data.ETScript;
import com.bartz24.externaltweaker.app.panels.PanelImportExportDialog;
import com.bartz24.externaltweaker.app.panels.PanelParameterEdit;

public class AppFrame extends JFrame {
	Object[][] itemMappings;
	Object[][] fluidMappings;
	Object[][] oreDictMappings;
	public JTable table;
	public JScrollPane tableScroll;
	public JList listMethods;
	public JScrollPane scrollMethods;
	public final ButtonGroup buttonGroup = new ButtonGroup();
	public List<PanelParameterEdit> paramPanels = new ArrayList();
	public List<ETRecipeData> recipeData = new ArrayList();
	public List<ETScript> scripts = new ArrayList();
	public JPanel pnlRecipeEdit;
	public JLabel labelRecipe;
	public JComboBox comboRecipes;
	public JRadioButton rdbtnItems;
	public JRadioButton rdbtnFluids;
	public JRadioButton rdbtnOreDict;
	public JPlaceholderTextField txtSearchTable;
	public JButton btnDeleteRecipe;
	public JButton btnDupeRecipe;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	public JRadioButton btnRecipeRemove;
	public JRadioButton btnRecipeAdd;
	private JTextField recipeDisplay;
	public boolean updatingParameters;
	public String copyData;
	public String copyType;
	public JLabel lblCopying;
	public JComboBox comboScripts;
	private JMenuItem mntmDeleteScript;
	private JMenuItem mntmSaveAllScripts;
	private JMenu menuHelp;
	private JMenuItem mntmHelp;
	private JMenuItem mntmDownload;
	public JButton btnNewRecipe;
	private JMenuItem mntmAbout;
	private JMenu mnOther;
	private JMenuItem mntmExportRecipesTo;
	private JMenuItem mntmExportCurrentTable;
	private JMenuItem mntmRenameCurrentScript;

	public AppFrame(Object[][] itemMappings, Object[][] fluidMappings, Object[][] oreDictMappings,
			List<String> methods) {
		setTitle("External Tweaker");
		// setIconImage(Toolkit.getDefaultToolkit().getImage(AppFrame.class.getResource("/book_writable.png")));
		this.itemMappings = itemMappings;
		this.fluidMappings = fluidMappings;
		this.oreDictMappings = oreDictMappings;
		this.setPreferredSize(new Dimension(1200, 800));

		for (String s : methods) {
			recipeData.add(new ETRecipeData(s, new String[0], true));
		}
		DefaultListModel model = new DefaultListModel();
		for (String s : methodDisplays()) {
			model.addElement(s);
		}
		listMethods = new JList(model);
		listMethods.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listMethods.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listMethods.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				btnNewRecipe.setEnabled(comboScripts.getSelectedIndex() >= 0 && listMethods.getModel().getSize() > 0);
			}
		});
		scrollMethods = new JScrollPane(listMethods);

		comboRecipes = new JComboBox();
		comboRecipes.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED && !updatingParameters) {
					updateCurrentRecipe();
				}
			}
		});

		labelRecipe = new JLabel("Current Recipe");
		labelRecipe.setHorizontalAlignment(SwingConstants.TRAILING);

		table = new JTable(new Object[0][0], new String[] { "ID", "Name" }) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		loadTable("Items", "");

		table.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if (table.getSelectedRow() >= 0 && table.getSelectedColumn() >= 0)
					table.setToolTipText("Currently Selected: " + table.getValueAt(table.getSelectedRow(), 0) + " ("
							+ table.getValueAt(table.getSelectedRow(), 1) + ")");
				else
					table.setToolTipText("Currently Selected: None");

				updateParameters();
			}
		});

		tableScroll = new JScrollPane(table);

		ActionListener tableSelectListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aBtn = (AbstractButton) actionEvent.getSource();
				loadTable(aBtn.getText(), txtSearchTable.getText());
			}
		};

		rdbtnItems = new JRadioButton("Items");
		rdbtnItems.setSelected(true);
		rdbtnItems.addActionListener(tableSelectListener);
		buttonGroup.add(rdbtnItems);

		rdbtnFluids = new JRadioButton("Fluids");
		rdbtnFluids.addActionListener(tableSelectListener);
		buttonGroup.add(rdbtnFluids);

		rdbtnOreDict = new JRadioButton("Ore Dict");
		rdbtnOreDict.addActionListener(tableSelectListener);
		buttonGroup.add(rdbtnOreDict);

		btnNewRecipe = new JButton("Add New Recipe Using Selected Type");
		btnNewRecipe.setEnabled(false);
		btnNewRecipe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listMethods.getSelectedIndex() >= 0 && getCurrentScript() != null) {
					ETRecipeData data = recipeData.get(listMethods.getSelectedIndex());
					getCurrentScript().recipes
							.add(new ETActualRecipe(data.getRecipeFormat(), new String[data.getParameterCount()]));
					updateRecipesList(false);
					comboRecipes.setSelectedIndex(getCurrentScript().recipes.size() - 1);
					updateCurrentRecipe();
				}
			}
		});

		pnlRecipeEdit = new JPanel();

		JScrollPane scrollPane = new JScrollPane(pnlRecipeEdit);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		txtSearchTable = new JPlaceholderTextField("Search");
		txtSearchTable.setDisabledTextColor(UIManager.getColor("Button.disabledText"));

		JButton btnSearchTable = new JButton("Search Table");
		btnSearchTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadTable(rdbtnItems.isSelected() ? "Items" : rdbtnFluids.isSelected() ? "Fluids" : "Ore Dict",
						txtSearchTable.getText().trim().toLowerCase());
			}
		});

		ActionListener recipeTypeSelect = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (comboRecipes.getSelectedIndex() >= 0) {
					int index = indexOfRecipeFormat(
							getCurrentScript().recipes.get(comboRecipes.getSelectedIndex()).getRecipeFormat());

					if (index >= 0) {
						recipeData.get(index).setAddRecipe(btnRecipeAdd.isSelected());
					}
				}
			}
		};

		btnRecipeRemove = new JRadioButton("Remove");
		btnRecipeRemove.addActionListener(recipeTypeSelect);
		buttonGroup_1.add(btnRecipeRemove);

		btnRecipeAdd = new JRadioButton("Add/Other");
		btnRecipeRemove.addActionListener(recipeTypeSelect);
		buttonGroup_1.add(btnRecipeAdd);

		JPanel panel = new JPanel();

		recipeDisplay = new JTextField();
		recipeDisplay.setEditable(false);
		recipeDisplay.setColumns(10);

		lblCopying = new JLabel("Currently Copying: ");

		comboScripts = new JComboBox();
		comboScripts.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				btnNewRecipe.setEnabled(comboScripts.getSelectedIndex() >= 0 && listMethods.getModel().getSize() > 0);
				if (e.getStateChange() == ItemEvent.SELECTED && !updatingParameters) {
					comboRecipes.setSelectedIndex(-1);
					updateRecipesList(false);
					comboRecipes.setSelectedIndex(-1);
					updateCurrentRecipe();
				}
			}
		});

		JLabel lblCurrentScript = new JLabel("Current Script");
		lblCurrentScript.setHorizontalAlignment(SwingConstants.TRAILING);

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout
								.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
										.createSequentialGroup().addGap(6).addGroup(groupLayout.createParallelGroup(
												Alignment.LEADING, false)
												.addComponent(tableScroll, GroupLayout.PREFERRED_SIZE, 332,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
														.createParallelGroup(Alignment.LEADING)
														.addGroup(groupLayout.createSequentialGroup()
																.addComponent(rdbtnItems, GroupLayout.PREFERRED_SIZE,
																		63, GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(ComponentPlacement.RELATED)
																.addComponent(rdbtnFluids, GroupLayout.PREFERRED_SIZE,
																		63, GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(ComponentPlacement.RELATED)
																.addComponent(rdbtnOreDict, GroupLayout.PREFERRED_SIZE,
																		79, GroupLayout.PREFERRED_SIZE))
														.addComponent(txtSearchTable, GroupLayout.DEFAULT_SIZE, 217,
																Short.MAX_VALUE))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnSearchTable)))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 563,
																Short.MAX_VALUE)
														.addGap(6))
												.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
														.createParallelGroup(Alignment.LEADING)
														.addGroup(groupLayout.createSequentialGroup()
																.addComponent(btnRecipeRemove)
																.addPreferredGap(ComponentPlacement.RELATED)
																.addComponent(btnRecipeAdd)
																.addPreferredGap(ComponentPlacement.RELATED)
																.addComponent(recipeDisplay, GroupLayout.DEFAULT_SIZE,
																		398, Short.MAX_VALUE))
														.addGroup(groupLayout.createSequentialGroup()
																.addGroup(groupLayout
																		.createParallelGroup(Alignment.TRAILING)
																		.addComponent(lblCurrentScript,
																				GroupLayout.PREFERRED_SIZE, 95,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(labelRecipe,
																				GroupLayout.PREFERRED_SIZE, 95,
																				GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(ComponentPlacement.RELATED)
																.addGroup(groupLayout
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(comboScripts, Alignment.TRAILING,
																				0, 446, Short.MAX_VALUE)
																		.addComponent(comboRecipes, 0, 446,
																				Short.MAX_VALUE))))
														.addGap(16))))
								.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(lblCopying)
										.addPreferredGap(ComponentPlacement.RELATED)))
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(btnNewRecipe, GroupLayout.DEFAULT_SIZE, 254,
														Short.MAX_VALUE)
												.addComponent(scrollMethods, Alignment.TRAILING,
														GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
										.addGap(11))
								.addGroup(groupLayout.createSequentialGroup().addGap(0)
										.addComponent(panel, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
										.addContainerGap()))));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addGroup(groupLayout.createSequentialGroup().addComponent(lblCopying)
														.addGap(28))
												.addComponent(panel, GroupLayout.PREFERRED_SIZE, 38,
														GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNewRecipe))
						.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(txtSearchTable, GroupLayout.PREFERRED_SIZE, 19,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(btnSearchTable))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(comboScripts, GroupLayout.PREFERRED_SIZE, 18,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lblCurrentScript).addComponent(rdbtnItems)
										.addComponent(rdbtnFluids).addComponent(rdbtnOreDict))))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollMethods, GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(tableScroll, GroupLayout.DEFAULT_SIZE, 639,
												Short.MAX_VALUE)
										.addGroup(groupLayout.createSequentialGroup()
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(labelRecipe).addComponent(comboRecipes,
																GroupLayout.PREFERRED_SIZE, 18,
																GroupLayout.PREFERRED_SIZE))
												.addGap(6)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(btnRecipeRemove).addComponent(btnRecipeAdd)
														.addComponent(recipeDisplay, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(9).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 582,
														Short.MAX_VALUE))))
						.addGap(0)));

		btnDeleteRecipe = new JButton("Delete Recipe");
		btnDeleteRecipe.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDeleteRecipe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboRecipes.getSelectedIndex() >= 0) {
					getCurrentScript().recipes.remove(comboRecipes.getSelectedIndex());
					comboRecipes.setSelectedIndex(-1);
					updateRecipesList(false);
					comboRecipes.setSelectedIndex(-1);
					updateCurrentRecipe();
				}
			}
		});

		btnDupeRecipe = new JButton("Duplicate Recipe");
		btnDupeRecipe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboRecipes.getSelectedIndex() >= 0) {
					getCurrentScript().recipes
							.add(getCurrentScript().recipes.get(comboRecipes.getSelectedIndex()).clone());
					updateRecipesList(false);
					comboRecipes.setSelectedIndex(getCurrentScript().recipes.size() - 1);
					updateCurrentRecipe();
				}
			}
		});
		btnDupeRecipe.setAlignmentX(Component.CENTER_ALIGNMENT);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addComponent(btnDeleteRecipe, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnDupeRecipe, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE).addGap(0)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup().addGap(6)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(btnDeleteRecipe)
								.addComponent(btnDupeRecipe))));
		panel.setLayout(gl_panel);
		pnlRecipeEdit.setLayout(new BoxLayout(pnlRecipeEdit, BoxLayout.Y_AXIS));
		tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().setLayout(groupLayout);
		this.pack();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenuItem menuItemNew = new JMenuItem("New Script");
		menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuFile.add(menuItemNew);

		menuItemNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				newScript();
			}
		});

		JMenuItem menuItemOpen = new JMenuItem("Open Scripts");
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuFile.add(menuItemOpen);

		menuItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				openScripts();
			}
		});

		JMenuItem menuItemSave = new JMenuItem("Save Current Script");
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuFile.add(menuItemSave);

		menuItemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveScript(getCurrentScript());
			}
		});

		JMenuItem menuItemSaveAs = new JMenuItem("Save Current Script As");
		menuItemSaveAs
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuFile.add(menuItemSaveAs);

		menuItemSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveCurScriptAs();
			}
		});

		mntmSaveAllScripts = new JMenuItem("Save All Scripts");
		mntmSaveAllScripts
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuFile.add(mntmSaveAllScripts);

		mntmSaveAllScripts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveAllScripts();
			}
		});

		mntmDeleteScript = new JMenuItem("Delete Current Script");
		mntmDeleteScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
		menuFile.add(mntmDeleteScript);

		mntmDeleteScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				deleteScript();
			}
		});

		JMenuItem mntmImportData = new JMenuItem("Import Obj Data");
		mntmImportData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		menuFile.add(mntmImportData);

		mntmImportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				importData();
			}
		});

		JMenuItem mntmExportData = new JMenuItem("Export Obj Data");
		mntmExportData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		menuFile.add(mntmExportData);

		mntmExportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				exportData();
			}
		});

		mnOther = new JMenu("Other");
		menuBar.add(mnOther);

		mntmRenameCurrentScript = new JMenuItem("Rename Current Script");
		mnOther.add(mntmRenameCurrentScript);
		mntmRenameCurrentScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				renameFile();
			}
		});

		mntmExportRecipesTo = new JMenuItem("Create Text File Of Recipes");
		mnOther.add(mntmExportRecipesTo);
		mntmExportRecipesTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (recipeData.size() > 0)
					sendRecipesToTextFile();
				else
					JOptionPane.showOptionDialog(AppFrame.this, "No Recipes!", "Message", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, new Object[] { "OK" }, "OK");
			}
		});

		mntmExportCurrentTable = new JMenuItem("Create Text File Of Current Table");
		mnOther.add(mntmExportCurrentTable);
		mntmExportCurrentTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (recipeData.size() > 0)
					sendTableToTextFile();
				else
					JOptionPane.showOptionDialog(AppFrame.this, "Table is Empty!", "Message", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, new Object[] { "OK" }, "OK");
			}
		});

		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		mntmHelp = new JMenuItem("Open Help Wiki");
		menuHelp.add(mntmHelp);

		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				help();
			}
		});

		mntmAbout = new JMenuItem("About External Tweaker");
		menuHelp.add(mntmAbout);

		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				about();
			}
		});

		mntmDownload = new JMenuItem("Download External Tweaker");
		menuHelp.add(mntmDownload);

		mntmDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				download();
			}
		});

		this.setVisible(true);
		updateParameters();
		updateCurrentRecipe();
	}

	public void updateParameters() {
		for (int i = 0; i < paramPanels.size(); i++) {
			paramPanels.get(i).editPanel.update();
			paramPanels.get(i).btnPaste.setEnabled(paramPanels.get(i).paramType.equals(copyType));
			if (comboRecipes.getSelectedIndex() >= 0 && getCurrentScript().recipes.size() > 0)
				getCurrentScript().recipes.get(comboRecipes.getSelectedIndex()).setParameterData(i,
						paramPanels.get(i).exportData());
		}
	}

	public void updateRecipesList(boolean updating) {
		int comboIndex = -1;
		if (updating) {
			comboIndex = comboRecipes.getSelectedIndex();
			updatingParameters = true;
		}
		String[] displays = new String[getCurrentScript() == null ? 0 : getCurrentScript().recipes.size()];
		for (int i = 0; i < (getCurrentScript() == null ? 0 : getCurrentScript().recipes.size()); i++) {
			if (comboIndex < 0 || comboRecipes.getSelectedIndex() == i) {
				int index = indexOfRecipeFormat(getCurrentScript().recipes.get(i).getRecipeFormat());
				if (index >= 0) {

					displays[i] = getCurrentScript().recipes.get(i).recipeToString(recipeData.get(index));
					String[] split = displays[i].substring(0, displays[i].indexOf("(")).split("\\.");
					displays[i] = "#" + Integer.toString(i + 1) + "  " + split[split.length - 2] + "."
							+ split[split.length - 1]
							+ displays[i].substring(displays[i].indexOf("("), displays[i].length());

					List<Object[]> mappings = new ArrayList();
					mappings.addAll(new ArrayList(Arrays.asList(itemMappings)));
					mappings.addAll(new ArrayList(Arrays.asList(fluidMappings)));
					mappings.addAll(new ArrayList(Arrays.asList(oreDictMappings)));
					for (int x = 0; x < mappings.size(); x++) {
						if (index >= 0) {
							List<String> paramTypes = Arrays.asList(recipeData.get(index).getParameterTypes());
							String newVal = "";
							while (!newVal.equals(displays[i])) {
								if (!newVal.equals(""))
									displays[i] = newVal;
								newVal = displays[i].replace(mappings.get(x)[0].toString(),
										mappings.get(x)[1].toString());
							}
						}
					}

				} else
					displays[i] = "#" + Integer.toString(i + 1) + "  "
							+ getCurrentScript().recipes.get(i).getRecipeFormat()
							+ " ERROR: DID NOT FIND RECIPE FORMAT";
			} else {
				displays[i] = (String) comboRecipes.getModel().getElementAt(i);
			}
		}

		if (comboRecipes.getSelectedIndex() >= 0) {
			int index = indexOfRecipeFormat(
					getCurrentScript().recipes.get(comboRecipes.getSelectedIndex()).getRecipeFormat());

			if (index >= 0) {
				recipeDisplay.setText(getCurrentScript().recipes.get(comboRecipes.getSelectedIndex())
						.recipeToString(recipeData.get(index)));
			} else
				recipeDisplay.setText("");
		} else
			recipeDisplay.setText("");

		comboRecipes.setModel(new DefaultComboBoxModel(displays));
		if (updating) {
			comboRecipes.setSelectedIndex(comboIndex);
			updatingParameters = false;
		}
	}

	public void updateScriptsList(boolean updating) {
		int comboIndex = 0;
		if (updating) {
			comboIndex = comboScripts.getSelectedIndex();
			updatingParameters = true;
		}
		String[] displays = new String[scripts.size()];
		for (int i = 0; i < scripts.size(); i++) {
			displays[i] = "#" + Integer.toString(i + 1) + "  " + scripts.get(i).filePath
					+ (Strings.isNullOrEmpty(scripts.get(i).filePath) ? "" : File.separator) + scripts.get(i).fileName;
		}

		comboScripts.setModel(new DefaultComboBoxModel(displays));
		if (updating) {
			comboScripts.setSelectedIndex(comboIndex);
			updatingParameters = false;
		}
	}

	private void updateCurrentRecipe() {

		pnlRecipeEdit.removeAll();
		paramPanels.clear();
		if (comboRecipes.getSelectedIndex() >= 0) {
			int index = indexOfRecipeFormat(
					getCurrentScript().recipes.get(comboRecipes.getSelectedIndex()).getRecipeFormat());

			if (index >= 0) {
				for (String s : recipeData.get(index).getParameterTypes())
					addParameter(s);

				for (int i = 0; i < paramPanels.size(); i++) {
					btnRecipeAdd.setSelected(recipeData.get(index).isAddRecipe());
					btnRecipeRemove.setSelected(!recipeData.get(index).isAddRecipe());
					paramPanels.get(i).txtName.setText(recipeData.get(index).getParamName(i));
					paramPanels.get(i).importData(
							getCurrentScript().recipes.get(comboRecipes.getSelectedIndex()).getParameterData(i));
				}
				updateParameters();
				recipeDisplay.setText(getCurrentScript().recipes.get(comboRecipes.getSelectedIndex())
						.recipeToString(recipeData.get(index)));

			} else
				recipeDisplay.setText("");
		}
		btnDeleteRecipe.setEnabled(comboRecipes.getSelectedIndex() >= 0);
		btnDupeRecipe.setEnabled(comboRecipes.getSelectedIndex() >= 0);
		btnRecipeAdd.setEnabled(comboRecipes.getSelectedIndex() >= 0);
		btnRecipeRemove.setEnabled(comboRecipes.getSelectedIndex() >= 0);
		pnlRecipeEdit.revalidate();
		pnlRecipeEdit.repaint();
	}

	private void loadTable(String type, String filter) {
		List<RowSorter.SortKey> keys = table.getRowSorter() != null
				? ((DefaultRowSorter) table.getRowSorter()).getSortKeys() : new ArrayList();
		Object[][] array = type.equals("Items") ? this.itemMappings
				: type.equals("Fluids") ? this.fluidMappings : type.equals("Ore Dict") ? this.oreDictMappings : null;
		if (array == null)
			return;
		if (!Strings.isNullOrEmpty(filter)) {
			List<Integer> indexesValid = new ArrayList();
			for (int i = 0; i < array.length; i++) {
				if (array[i][0].toString().toLowerCase().contains(filter)
						|| array[i][1].toString().toLowerCase().contains(filter))
					indexesValid.add(i);
			}
			Object[][] newArray = new Object[indexesValid.size()][2];
			for (int i = 0; i < indexesValid.size(); i++) {
				newArray[i][0] = array[indexesValid.get(i)][0];
				newArray[i][1] = array[indexesValid.get(i)][1];
			}
			array = newArray;
		}
		table.setModel(new DefaultTableModel(array, new String[] { "ID", "Name" }));
		if (table.getRowSorter() != null) {
			DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
			sorter.setSortKeys(keys);
			sorter.sort();
		}

		// table.clearSelection();
	}

	private List<String> methodDisplays() {
		List<String> displays = new ArrayList();
		List<ETRecipeData> recipeDataNew = new ArrayList();
		for (ETRecipeData data : recipeData) {
			displays.add(data.getRecipeDisplay());
		}
		Collections.sort(displays);
		for (String s : displays) {
			for (ETRecipeData data : recipeData) {
				if (data.getRecipeDisplay().equals(s))
					recipeDataNew.add(data);
			}
		}
		recipeData = recipeDataNew;
		return displays;
	}

	public int indexOfRecipeFormat(String format) {
		for (int i = 0; i < recipeData.size(); i++) {
			if (recipeData.get(i).getRecipeFormat().equals(format))
				return i;
		}
		return -1;
	}

	private void addParameter(String type) {
		type = type.trim();
		String subtype = type.startsWith("optional.") ? type.substring("optional.".length()) : type;
		PanelParameterEdit p = new PanelParameterEdit(paramPanels.size() + 1, type, this);
		p.setListeners();
		pnlRecipeEdit.add(p);
		paramPanels.add(p);
	}

	public ETScript getCurrentScript() {
		if (comboScripts.getSelectedIndex() >= 0)
			return scripts.get(comboScripts.getSelectedIndex());
		return null;
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				AppFrame frame = new AppFrame(new Object[0][0], new Object[0][0], new Object[0][0], new ArrayList());
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	private void newScript() {
		String fileName = JOptionPane.showInputDialog(this, "New script file name", "New Script.zs");
		if (Strings.isNullOrEmpty(fileName))
			return;
		if (!fileName.trim().endsWith(".zs"))
			fileName = fileName.trim() + ".zs";
		scripts.add(new ETScript("", fileName));
		updateScriptsList(false);
		comboScripts.setSelectedIndex(scripts.size() - 1);
		comboRecipes.setSelectedIndex(-1);
		updateRecipesList(false);
		comboRecipes.setSelectedIndex(-1);
		updateCurrentRecipe();
		btnNewRecipe.setEnabled(comboScripts.getSelectedIndex() >= 0 && listMethods.getModel().getSize() > 0);
	}

	private void deleteScript() {
		if (scripts.size() > 0 && comboScripts.getSelectedIndex() >= 0) {
			int dialogResult = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete this script? \n \n This will also delete the actual file!",
					"Warning", JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				if (!Strings.isNullOrEmpty(getCurrentScript().filePath)) {
					new File(getCurrentScript().filePath + File.separator + getCurrentScript().fileName).delete();
				}
				scripts.remove(comboScripts.getSelectedIndex());
				comboScripts.setSelectedIndex(-1);
				updateScriptsList(false);
				comboScripts.setSelectedIndex(-1);
				comboRecipes.setSelectedIndex(-1);
				updateRecipesList(false);
				comboRecipes.setSelectedIndex(-1);
				updateCurrentRecipe();
			}
		}
	}

	private void exportData() {
		PanelImportExportDialog dataPanel = new PanelImportExportDialog(false);
		int input = JOptionPane.showOptionDialog(this, dataPanel, "Exporting Data", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Export", "Cancel" }, "Export");

		boolean[] settings = dataPanel.getSettings();
		if (input == 0 && (Strings.isNullOrEmpty(dataPanel.txtPath.getText().trim())
				|| !dataPanel.txtPath.getText().trim().endsWith(".etd")
				|| (!settings[0] && !settings[1] && !settings[2] && !settings[3]))) {
			JOptionPane.showOptionDialog(this, "Invalid file path", "Error", JOptionPane.OK_OPTION,
					JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
			return;
		}
		if (input != 0)
			return;
		boolean fileExists = new File(dataPanel.txtPath.getText().trim()).isFile();
		try {

			if (settings[4]) {
				FileOutputStream file = new FileOutputStream(dataPanel.txtPath.getText().trim());
				ObjectOutputStream save = new ObjectOutputStream(file);
				exportOverride(settings, save);
				save.close();
			} else {
				if (fileExists) {
					FileInputStream curFile = new FileInputStream(dataPanel.txtPath.getText().trim());
					ObjectInputStream cur = new ObjectInputStream(curFile);
					exportAdd(settings, dataPanel.txtPath.getText().trim(), cur);
				} else {
					FileOutputStream file = new FileOutputStream(dataPanel.txtPath.getText().trim());
					ObjectOutputStream save = new ObjectOutputStream(file);
					exportOverride(settings, save);
					save.close();
				}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
			JOptionPane.showOptionDialog(this, exc.getLocalizedMessage(),
					"Error! Try exporting from the game again! Report this issue if that fails!", JOptionPane.OK_OPTION,
					JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
		}
		JOptionPane.showOptionDialog(this, "Export Finished!", "Done", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, new Object[] { "OK" }, "OK");
	}

	private void exportOverride(boolean[] settings, ObjectOutputStream save) throws IOException {
		if (settings[1] && itemMappings != null)
			save.writeObject(itemMappings);
		else
			save.writeObject(new Object[0][0]);

		if (settings[2] && fluidMappings != null)
			save.writeObject(fluidMappings);
		else
			save.writeObject(new Object[0][0]);
		if (settings[3] && oreDictMappings != null)
			save.writeObject(oreDictMappings);
		else
			save.writeObject(new Object[0][0]);
		if (settings[0] && recipeData != null)
			save.writeObject(recipeData);
		else
			save.writeObject(new ArrayList());
	}

	private void exportAdd(boolean[] settings, String savePath, ObjectInputStream cur)
			throws IOException, ClassNotFoundException {
		List<Object[]> iMap = new ArrayList(Arrays.asList((Object[][]) cur.readObject()));
		List<Object[]> fMap = new ArrayList(Arrays.asList((Object[][]) cur.readObject()));
		List<Object[]> oMap = new ArrayList(Arrays.asList((Object[][]) cur.readObject()));
		List<ETRecipeData> rList = (ArrayList) cur.readObject();
		cur.close();
		FileOutputStream file = new FileOutputStream(savePath);
		ObjectOutputStream save = new ObjectOutputStream(file);
		if (settings[1]) {
			for (int i = 0; i < itemMappings.length; i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < iMap.size(); i2++) {
					if (itemMappings[i][0].equals(iMap.get(i2)[0])) {
						contains = true;
						break;
					}
				}
				if (!contains)
					iMap.add(itemMappings[i]);
			}
			save.writeObject(iMap.toArray(new Object[iMap.size()][2]));
		} else {
			save.writeObject(iMap.toArray(new Object[iMap.size()][2]));
		}
		if (settings[2]) {
			for (int i = 0; i < fluidMappings.length; i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < fMap.size(); i2++) {
					if (fluidMappings[i][0].equals(fMap.get(i2)[0])) {
						contains = true;
						break;
					}
				}
				if (!contains)
					fMap.add(fluidMappings[i]);
			}
			save.writeObject(fMap.toArray(new Object[fMap.size()][2]));
		} else {
			save.writeObject(fMap.toArray(new Object[fMap.size()][2]));
		}
		if (settings[3]) {
			for (int i = 0; i < oreDictMappings.length; i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < oMap.size(); i2++) {
					if (oreDictMappings[i][0].equals(oMap.get(i2)[0])) {
						contains = true;
						break;
					}
				}
				if (!contains)
					oMap.add(oreDictMappings[i]);
			}
			save.writeObject(oMap.toArray(new Object[oMap.size()][2]));
		} else {
			save.writeObject(oMap.toArray(new Object[oMap.size()][2]));
		}
		if (settings[0]) {
			for (int i = 0; i < recipeData.size(); i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < rList.size(); i2++) {
					if (rList.get(i2).getRecipeFormat().equals(recipeData.get(i).getRecipeFormat())) {
						contains = true;
						for (int x = 0; x < rList.get(i2).getParameterTypes().length; x++) {
							if (Strings.isNullOrEmpty(rList.get(i2).getParamName(x)))
								rList.get(i2).setParamName(x, recipeData.get(i).getParamName(x));
						}
						break;
					}
				}
				if (!contains)
					rList.add(recipeData.get(i));
			}
			save.writeObject(rList);
		} else {
			save.writeObject(rList);
		}
		save.close();
	}

	private void importData() {
		PanelImportExportDialog dataPanel = new PanelImportExportDialog(true);
		int input = JOptionPane.showOptionDialog(this, dataPanel, "Importing Data", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Import", "Cancel" }, "Import");

		boolean[] settings = dataPanel.getSettings();
		if (input == 0 && (Strings.isNullOrEmpty(dataPanel.txtPath.getText().trim())
				|| !dataPanel.txtPath.getText().trim().endsWith(".etd")
				|| (!settings[0] && !settings[1] && !settings[2] && !settings[3]))) {
			JOptionPane.showOptionDialog(this, "Invalid file path", "Error", JOptionPane.OK_OPTION,
					JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
			return;
		}
		if (input != 0)
			return;
		try {
			FileInputStream saveFile = new FileInputStream(dataPanel.txtPath.getText().trim());
			ObjectInputStream save = new ObjectInputStream(saveFile);

			if (settings[4]) {
				importOverride(settings, save);
			} else {
				importAdd(settings, save);
			}

			DefaultListModel model = new DefaultListModel();
			for (String s : methodDisplays()) {
				model.addElement(s);
			}
			listMethods.setModel(model);
			txtSearchTable.setText("");
			loadTable(rdbtnItems.isSelected() ? "Items" : rdbtnFluids.isSelected() ? "Fluids" : "Ore Dict", "");

			save.close();
		} catch (Exception exc) {
			exc.printStackTrace();
			JOptionPane.showOptionDialog(this, exc.getLocalizedMessage(),
					"Error! Try importing from the game again! Especially if you updated this program!\nReport this issue if this fails!",
					JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
		}
		JOptionPane.showOptionDialog(this, "Import Finished!", "Done", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, new Object[] { "OK" }, "OK");
	}

	private void importOverride(boolean[] settings, ObjectInputStream save) throws IOException, ClassNotFoundException {
		if (settings[1])
			itemMappings = (Object[][]) save.readObject();
		else
			save.readObject();
		if (settings[2])
			fluidMappings = (Object[][]) save.readObject();
		else
			save.readObject();
		if (settings[3])
			oreDictMappings = (Object[][]) save.readObject();
		else
			save.readObject();
		if (settings[0])
			recipeData = (ArrayList) save.readObject();
		else
			save.readObject();
	}

	private void importAdd(boolean[] settings, ObjectInputStream save) throws IOException, ClassNotFoundException {
		if (settings[1]) {
			Object[][] iMap = (Object[][]) save.readObject();
			List<Object[]> iMappings = new ArrayList(Arrays.asList(itemMappings));
			for (int i = 0; i < iMap.length; i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < iMappings.size(); i2++) {
					if (iMappings.get(i2)[0].equals(iMap[i][0])) {
						contains = true;
						break;
					}
				}
				if (!contains)
					iMappings.add(iMap[i]);
			}
			itemMappings = iMappings.toArray(new Object[iMappings.size()][2]);
		} else
			save.readObject();
		if (settings[2]) {
			Object[][] fMap = (Object[][]) save.readObject();
			List<Object[]> fMappings = new ArrayList(Arrays.asList(fluidMappings));
			for (int i = 0; i < fMap.length; i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < fMappings.size(); i2++) {
					if (fMappings.get(i2)[0].equals(fMap[i][0])) {
						contains = true;
						break;
					}
				}
				if (!contains)
					fMappings.add(fMap[i]);
			}
			fluidMappings = fMappings.toArray(new Object[fMappings.size()][2]);
		} else
			save.readObject();
		if (settings[3]) {
			Object[][] oMap = (Object[][]) save.readObject();
			List<Object[]> oMappings = new ArrayList(Arrays.asList(oreDictMappings));
			for (int i = 0; i < oMap.length; i++) {
				boolean contains = false;
				for (int i2 = 0; i2 < oMappings.size(); i2++) {
					if (oMappings.get(i2)[0].equals(oMap[i][0])) {
						contains = true;
						break;
					}
				}
				if (!contains)
					oMappings.add(oMap[i]);
			}
			oreDictMappings = oMappings.toArray(new Object[oMappings.size()][2]);
		} else
			save.readObject();
		if (settings[0]) {
			List<ETRecipeData> rList = (ArrayList) save.readObject();
			for (int i2 = 0; i2 < rList.size(); i2++) {
				boolean contains = false;
				for (int i = 0; i < recipeData.size(); i++) {
					if (rList.get(i2).getRecipeFormat().equals(recipeData.get(i).getRecipeFormat())) {
						contains = true;
						for (int x = 0; x < rList.get(i2).getParameterTypes().length; x++) {
							if (Strings.isNullOrEmpty(recipeData.get(i).getParamName(x)))
								recipeData.get(i).setParamName(x, rList.get(i2).getParamName(x));
						}
						break;
					}
				}
				if (!contains)
					recipeData.add(rList.get(i2));
			}
		} else
			save.readObject();
	}

	private void saveScript(ETScript script) {
		if (Strings.isNullOrEmpty(script.filePath)) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(
					!Strings.isNullOrEmpty(script.filePath) ? (script.filePath + File.separator + script.fileName)
							: (System.getProperty("user.dir") + File.separator + script.filePath)));
			fc.setSelectedFile(new File(script.fileName));
			fc.setFileFilter(new FileNameExtensionFilter("ZS Scripts", "zs"));
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				script.filePath = fc.getSelectedFile().getAbsolutePath().substring(0,
						fc.getSelectedFile().getAbsolutePath().lastIndexOf(File.separator));
				script.fileName = fc.getSelectedFile().getAbsolutePath()
						.substring(fc.getSelectedFile().getAbsolutePath().lastIndexOf(File.separator) + 1);
				if (!script.fileName.endsWith(".zs"))
					script.fileName += ".zs";
			} else
				return;
		}

		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(new File(script.filePath + File.separator + script.fileName)));

			writer.write("# CREATED USING EXTERNAL TWEAKER\n");

			for (ETActualRecipe r : script.recipes) {
				int index = indexOfRecipeFormat(r.getRecipeFormat());
				if (index >= 0) {
					if (!recipeData.get(index).isAddRecipe()) {
						writer.write(r.recipeToString(recipeData.get(index)) + "\n");
					}
				}
			}

			writer.write("\n");

			for (ETActualRecipe r : script.recipes) {
				int index = indexOfRecipeFormat(r.getRecipeFormat());
				if (index >= 0) {
					if (recipeData.get(index).isAddRecipe()) {
						writer.write(r.recipeToString(recipeData.get(index)) + "\n");
					}
				} else
					writer.write(r.recipeToString(recipeData.get(index)) + "\n");

			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showOptionDialog(this, e.getLocalizedMessage(), "Error! Report this issue if you can!",
					JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
		}

	}

	private void openScripts() {
		File[] files = null;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(new FileNameExtensionFilter("ZS Scripts", "zs"));
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			files = fc.getSelectedFiles();
		}
		if (files == null)
			return;

		List<File> allScripts = new ArrayList();
		for (File f : files) {
			allScripts.addAll(f.isDirectory() ? getScripts(f) : Collections.singletonList(f));
		}
		boolean ignoreErrors = false;
		for (File f : allScripts) {
			ETScript script = new ETScript(
					f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator)),
					f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(File.separator) + 1));

			List<String> lines = new ArrayList();
			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					lines.add(line);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showOptionDialog(this, e.getLocalizedMessage(), "Error! Report this issue if you can!",
						JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
			}
			boolean skippingScript = false;
			int lineNum = 0;
			String lastLine = "";
			HashMap<String, String> variables = new HashMap();
			for (String string : lines) {
				try {
					lineNum++;
					if (!Strings.isNullOrEmpty(string) && !string.startsWith("#") && !string.startsWith("//")
							&& !string.startsWith("/*") && !string.endsWith("*/")) {
						if (!(lastLine + string).endsWith(";")) {
							lastLine += string;
							continue;
						}
						String s = lastLine + string;
						lastLine = "";
						if (s.startsWith("val ") && s.contains("=")) {
							String[] variable = s.trim().substring(4, s.trim().length() - 1).split("=");
							variables.put(variable[0].trim(), variable[1].trim());
							continue;
						}
						ETRecipeData d = findRecipeDataForRecipe(s);
						if (d != null) {
							String params = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
							HashMap<Integer, String> arrayIndexes = new HashMap();
							List<String> paramList = new ArrayList();
							boolean varGoThroughAgain = true;
							while (varGoThroughAgain) {
								varGoThroughAgain = false;
								boolean changedSomething = true;
								while (changedSomething) {
									changedSomething = false;
									while (params.contains(".withTag(") && params.contains(")")
											&& params.indexOf(".withTag(") < params.indexOf(")")
											&& !params.substring(params.indexOf(".withTag("), params.indexOf(")") + 1)
													.contains(".onlyWithTag(")) {
										String arg = params.substring(params.indexOf(".withTag("),
												params.indexOf(")") + 1);
										params = params.replace(arg, "~" + arrayIndexes.size() + "~");
										arrayIndexes.put(arrayIndexes.size(), arg);
										changedSomething = true;
									}
									if (!changedSomething)
										while (params.contains("\"")
												&& params.substring(params.indexOf("\"") + 1).contains("\"")) {
											String arg = params.substring(params.indexOf("\""),
													params.indexOf("\"", params.indexOf("\"") + 1) + 1);
											params = params.replace(arg, "~" + arrayIndexes.size() + "~");
											arrayIndexes.put(arrayIndexes.size(), arg);
											changedSomething = true;
										}
									if (!changedSomething)
										while (params.contains(".onlyWithTag(") && params.contains(")")
												&& params.indexOf(".onlyWithTag(") < params.indexOf(")")) {
											String arg = params.substring(params.indexOf(".onlyWithTag("),
													params.indexOf(")") + 1);
											params = params.replace(arg, "~" + arrayIndexes.size() + "~");
											arrayIndexes.put(arrayIndexes.size(), arg);
											changedSomething = true;
										}
									if (!changedSomething)
										while (params.contains("<") && params.contains(">")
												&& params.indexOf("<") < params.indexOf(">")) {
											String arg = params.substring(params.indexOf("<"), params.indexOf(">") + 1);
											params = params.replace(arg, "~" + arrayIndexes.size() + "~");
											arrayIndexes.put(arrayIndexes.size(), arg);
											changedSomething = true;
										}
									if (!changedSomething)
										for (String var : variables.keySet()) {
											while (params.contains(var)) {
												varGoThroughAgain = true;
												for (String var2 : variables.keySet()) {
													String[] splitVar = variables.get(var).split("\\.");
													if (!var.equals(var2) && splitVar.length > 1
															&& splitVar[0].equals(var2)) {
														splitVar[0] = variables.get(var2);
														String arg = "";
														for (int i = 0; i < splitVar.length; i++) {
															arg += splitVar[i];
															if (i < splitVar.length - 1)
																arg += ".";
														}
														params = params.replace(var, arg);
														changedSomething = true;
													}
												}
												if (changedSomething)
													break;
												String arg = variables.get(var);
												params = params.replace(var, "~" + arrayIndexes.size() + "~");
												arrayIndexes.put(arrayIndexes.size(), arg);
												changedSomething = true;
											}
											if (changedSomething)
												break;
										}
									if (!changedSomething)
										while (params.contains("[[") && params.contains("]]")
												&& params.indexOf("[[") < params.indexOf("]]")) {
											String arg = params.substring(params.indexOf("[["),
													params.indexOf("]]") + 2);
											params = params.replace(arg, "~" + arrayIndexes.size() + "~");
											arrayIndexes.put(arrayIndexes.size(), arg);
											changedSomething = true;

										}
									if (!changedSomething)
										while (params.contains("[") && params.contains("]")
												&& params.indexOf("[") < params.indexOf("]")) {
											String arg = params.substring(params.indexOf("["), params.indexOf("]") + 1);
											params = params.replace(arg, "~" + arrayIndexes.size() + "~");
											arrayIndexes.put(arrayIndexes.size(), arg);
											changedSomething = true;
										}
								}

								paramList = new ArrayList(Arrays.asList(params.split(",")));
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
							}

							while (paramList.size() < d.getParameterCount()) {
								paramList.add("~");
							}
							script.recipes.add(new ETActualRecipe(findRecipeDataForRecipe(s).getRecipeFormat(),
									paramList.toArray(new String[paramList.size()])));
						} else {
							if (!ignoreErrors) {
								int result = JOptionPane.showOptionDialog(this,
										"The line: " + s + " in " + script.fileName + " at line " + lineNum
												+ " \n could not be loaded into External Tweaker. Make sure to import all the recipes you need first.\n Any recipes not loaded will be lost if you DO save over the script. \n Make a backup of the script if you have parts you want to keep.",
										"Script Loading Error", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null,
										new Object[] { "Cancel Loading Of Scripts", "Skip loading this Script",
												"Ignore Once and Continue", "Ignore Everything and Continue" },
										"Ignore Once and Continue");
								if (result == 0)
									return;
								else if (result == 1) {
									skippingScript = true;
									break;
								} else if (result == 3) {
									ignoreErrors = true;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showOptionDialog(this,
							e.getLocalizedMessage() + " when loading " + script.fileName + " at line " + lineNum,
							"Error! Report this issue if you can!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE,
							null, new Object[] { "OK" }, "OK");
					return;
				}
			}
			if (skippingScript)
				continue;
			boolean replaced = false;
			for (int i = 0; i < scripts.size(); i++) {
				if (scripts.get(i).fileName.equals(script.fileName)
						&& scripts.get(i).filePath.equals(script.filePath)) {
					scripts.set(i, script);
					replaced = true;
				}
			}
			if (!replaced)
				scripts.add(script);
			updateScriptsList(false);
			comboScripts.setSelectedIndex(0);
			comboRecipes.setSelectedIndex(-1);
			updateRecipesList(false);
			comboRecipes.setSelectedIndex(-1);
			updateCurrentRecipe();
			btnNewRecipe.setEnabled(comboScripts.getSelectedIndex() >= 0 && listMethods.getModel().getSize() > 0);

		}
	}

	public ETRecipeData findRecipeDataForRecipe(String recipe) {
		if (!recipe.contains("("))
			return null;
		String begin = recipe.substring(0, recipe.indexOf("("));
		String params = recipe.substring(recipe.indexOf("(") + 1, recipe.lastIndexOf(")"));
		while (params.contains("[[") && params.contains("]]")) {
			params = params.replace(params.substring(params.indexOf("[["), params.indexOf("]]") + 2), " ");
		}
		while (params.contains("[") && params.contains("]")) {
			params = params.replace(params.substring(params.indexOf("["), params.indexOf("]") + 1), " ");
		}
		int numParam = params.split(",").length;
		for (ETRecipeData d : recipeData) {
			if (d.getRecipeFormat().startsWith(begin) && numParam <= d.getParameterCount()
					&& numParam >= d.getParameterCountOptMin()) {
				return d;
			}
		}
		return null;
	}

	public List<File> getScripts(File... files) {
		List<File> scriptFiles = new ArrayList();
		for (File file : files) {
			if (file.isDirectory()) {
				scriptFiles.addAll(getScripts(file.listFiles()));
			} else if (file.getAbsolutePath().endsWith(".zs")) {
				scriptFiles.add(file);
			}
		}
		return scriptFiles;
	}

	private void saveAllScripts() {
		for (ETScript s : scripts)
			saveScript(s);
	}

	private void saveCurScriptAs() {
		ETScript script = getCurrentScript().clone();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(
				new File(!Strings.isNullOrEmpty(script.filePath) ? (script.filePath + File.separator + script.fileName)
						: (System.getProperty("user.dir") + File.separator + script.filePath)));
		fc.setSelectedFile(new File(script.fileName));
		fc.setFileFilter(new FileNameExtensionFilter("ZS Scripts", "zs"));
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			script.filePath = fc.getSelectedFile().getAbsolutePath().substring(0,
					fc.getSelectedFile().getAbsolutePath().lastIndexOf(File.separator));
			script.fileName = fc.getSelectedFile().getAbsolutePath()
					.substring(fc.getSelectedFile().getAbsolutePath().lastIndexOf(File.separator) + 1);
			if (!script.fileName.endsWith(".zs"))
				script.fileName += ".zs";

			saveScript(script);
		}
	}

	private void help() {
		JOptionPane.showOptionDialog(this, "Currently Unimplemented in this version. Coming soon...", "RIP",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[] { "OK" }, "OK");
	}

	private void download() {
		if (JOptionPane.showConfirmDialog(this,
				"Go to download page?\nhttps://minecraft.curseforge.com/projects/external-tweaker/files\nProgram files found under additional files of 1.10.2 versions",
				"Download", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
			try {
				Desktop.getDesktop()
						.browse(new URI("https://minecraft.curseforge.com/projects/external-tweaker/files"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	private void about() {
		JOptionPane.showOptionDialog(this, "Made by Bartz24\nPlease make sure versions are the same in the mod and app",
				"Version 0.4", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[] { "OK" }, "OK");
	}

	private void sendTableToTextFile() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + File.separator + "tableList.txt"));
		fc.setSelectedFile(new File("tableList.txt"));
		fc.setFileFilter(new FileNameExtensionFilter("Text File", "txt"));

		String filePath = "";
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			filePath = fc.getSelectedFile().getAbsolutePath();
			if (!filePath.endsWith(".txt"))
				filePath += ".txt";
		} else
			return;

		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));

			writer.write("TABLE DATA FROM EXTERNAL TWEAKER ("
					+ (rdbtnItems.isSelected() ? "Items" : rdbtnFluids.isSelected() ? "Fluids" : "Ore Dict") + ")\n");

			for (int i = 0; i < table.getRowCount(); i++) {
				writer.write(table.getValueAt(i, 0) + ": " + table.getValueAt(i, 1) + "\n");
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showOptionDialog(this, e.getLocalizedMessage(), "Error! Report this issue if you can!",
					JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
		}
	}

	private void sendRecipesToTextFile() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + File.separator + "recipeList.txt"));
		fc.setSelectedFile(new File("recipeList.txt"));
		fc.setFileFilter(new FileNameExtensionFilter("Text File", "txt"));

		String filePath = "";
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			filePath = fc.getSelectedFile().getAbsolutePath();
			if (!filePath.endsWith(".txt"))
				filePath += ".txt";
		} else
			return;

		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));

			writer.write("RECIPE DATA FROM EXTERNAL TWEAKER\n");

			for (int i = 0; i < recipeData.size(); i++) {
				writer.write(recipeData.get(i).getRecipeFormat() + "\n");
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showOptionDialog(this, e.getLocalizedMessage(), "Error! Report this issue if you can!",
					JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
		}
	}

	private void renameFile() {
		ETScript script = getCurrentScript();
		if (script != null) {
			String fileName = JOptionPane.showInputDialog(this, "Rename script file name", script.fileName);
			if (!fileName.trim().endsWith(".zs"))
				fileName = fileName.trim() + ".zs";
			if (!Strings.isNullOrEmpty(script.filePath)) {
				File file = new File(script.filePath + File.separator + script.fileName);
				if (!file.renameTo(new File(script.filePath + File.separator + fileName)))
					JOptionPane.showOptionDialog(this, "Failed to rename script!", "Error!", JOptionPane.OK_OPTION,
							JOptionPane.ERROR_MESSAGE, null, new Object[] { "OK" }, "OK");
			}
			script.fileName = fileName;
			updateScriptsList(false);
		}
	}
}
