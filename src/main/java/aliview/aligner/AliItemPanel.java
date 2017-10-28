package aliview.aligner;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.FileUtilities;

import aliview.AliViewWindow;
import aliview.externalcommands.CommandItem;
import aliview.settings.Settings;
import javax.swing.JCheckBox;

import org.apache.log4j.Logger;

public class AliItemPanel extends JPanel{
	private static final Logger logger = Logger.getLogger(AliItemPanel.class);
	JRadioButton radioIsActivated;

	public AliItemPanel(final CommandItem alignItem){

		final JPanel aliItemPanel = this;
		// this.setPreferredSize(new Dimension(650,100));
		// this.setMaximumSize(new Dimension(650,140));
		aliItemPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gbl_aliItemPanel = new GridBagLayout();
		gbl_aliItemPanel.columnWidths = new int[] {0, 100, 400};
		gbl_aliItemPanel.rowHeights = new int[] {0, 25, 30};
		gbl_aliItemPanel.columnWeights = new double[]{0.0, 0.0, 1.0};
		gbl_aliItemPanel.rowWeights = new double[]{0.0, 0.0, 1.0};
		aliItemPanel.setLayout(gbl_aliItemPanel);

		JLabel lblItemName = new JLabel("Display name:");
		lblItemName.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblItemName = new GridBagConstraints();
		gbc_lblItemName.anchor = GridBagConstraints.WEST;
		gbc_lblItemName.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName.gridx = 1;
		gbc_lblItemName.gridy = 0;
		add(lblItemName, gbc_lblItemName);

		JLabel lblProgram = new JLabel("Program:");
		lblProgram.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblProgram = new GridBagConstraints();
		gbc_lblProgram.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblProgram.insets = new Insets(0, 0, 5, 0);
		gbc_lblProgram.gridx = 2;
		gbc_lblProgram.gridy = 0;
		aliItemPanel.add(lblProgram, gbc_lblProgram);

		radioIsActivated = new JRadioButton("");
		GridBagConstraints gbc_radioIsActivated = new GridBagConstraints();
		gbc_radioIsActivated.fill = GridBagConstraints.VERTICAL;
		gbc_radioIsActivated.insets = new Insets(0, 0, 5, 5);
		gbc_radioIsActivated.gridx = 0;
		gbc_radioIsActivated.gridy = 1;
		radioIsActivated.addChangeListener(new ChangeListener() {	
			public void stateChanged(ChangeEvent e) {
				JRadioButton rbtn = (JRadioButton) e.getSource();
				alignItem.setActivated(rbtn.isSelected());
				//alignItem.getAddAlignCmd().setActivated(rbtn.isSelected());
			}
		});
		aliItemPanel.add(radioIsActivated, gbc_radioIsActivated);

		JTextField txtCommandName = new JTextField();
		txtCommandName.setText("mafft");
		GridBagConstraints gbc_txtCommandName = new GridBagConstraints();
		gbc_txtCommandName.insets = new Insets(0, 0, 5, 5);
		gbc_txtCommandName.fill = GridBagConstraints.BOTH;
		gbc_txtCommandName.gridx = 1;
		gbc_txtCommandName.gridy = 1;
		aliItemPanel.add(txtCommandName, gbc_txtCommandName);
		txtCommandName.setColumns(10);
		txtCommandName.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e) {
				JTextField txt = (JTextField) e.getSource();
				alignItem.setName(txt.getText());
				//alignItem.getAddAlignCmd().setName(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});


		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 2;
		gbc_panel_1.gridy = 1;
		aliItemPanel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {400, 70};
		gbl_panel_1.rowHeights = new int[] {25};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0};
		gbl_panel_1.rowWeights = new double[]{0.0};
		panel_1.setLayout(gbl_panel_1);

		final JTextField txtProgramFile = new JTextField();
		GridBagConstraints gbc_txtfilemafft = new GridBagConstraints();
		gbc_txtfilemafft.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtfilemafft.insets = new Insets(0, 0, 0, 5);
		gbc_txtfilemafft.gridx = 0;
		gbc_txtfilemafft.gridy = 0;
		panel_1.add(txtProgramFile, gbc_txtfilemafft);
		txtProgramFile.setColumns(10);
		txtProgramFile.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e){
				JTextField txt = (JTextField) e.getSource();
				alignItem.setProgramPath(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});

		JButton btnBrowseProgram = new JButton("Browse");
		btnBrowseProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String suggestedDir = txtProgramFile.getText();
				File suggestedFile = new File(suggestedDir);
				File selectedFile = FileUtilities.selectOpenFileViaChooser(suggestedFile,aliItemPanel);
				// check result
				if(selectedFile != null){
					txtProgramFile.setText(selectedFile.getAbsolutePath());
					alignItem.setProgramPath(selectedFile.getAbsolutePath());
					Settings.putExternalCommandFileDirectory(selectedFile.getAbsolutePath());	
				}
			}
		});
		GridBagConstraints gbc_btnBrowseProgram = new GridBagConstraints();
		gbc_btnBrowseProgram.insets = new Insets(0, 0, 0, 5);
		gbc_btnBrowseProgram.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBrowseProgram.gridx = 1;
		gbc_btnBrowseProgram.gridy = 0;
		panel_1.add(btnBrowseProgram, gbc_btnBrowseProgram);

		JLabel lblCommandParameters = new JLabel("Command parameters:");
		lblCommandParameters.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblCommandParameters = new GridBagConstraints();
		gbc_lblCommandParameters.fill = GridBagConstraints.VERTICAL;
		gbc_lblCommandParameters.insets = new Insets(0, 0, 0, 5);
		gbc_lblCommandParameters.gridx = 1;
		gbc_lblCommandParameters.gridy = 2;
		aliItemPanel.add(lblCommandParameters, gbc_lblCommandParameters);

		JTextArea txtCommand = new JTextArea();
		txtCommand.setLineWrap(true);
		txtCommand.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtCommand.setRows(2);
		GridBagConstraints gbc_txtCommand = new GridBagConstraints();
		gbc_txtCommand.fill = GridBagConstraints.BOTH;
		gbc_txtCommand.gridx = 2;
		gbc_txtCommand.gridy = 2;
		aliItemPanel.add(txtCommand, gbc_txtCommand);
		txtCommand.setColumns(10);
		txtCommand.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e){
				JTextArea txt = (JTextArea) e.getSource();
				alignItem.setCommand(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});

		logger.info("this"+this);
		logger.info("alignItem"+alignItem);
		logger.info("alignItem.getName()"+alignItem.getName());
		txtCommandName.setText(alignItem.getName());
		txtProgramFile.setText(alignItem.getProgramPath());
		txtCommand.setText(alignItem.getCommand());
		radioIsActivated.setSelected(alignItem.isActivated());
	}

	public JRadioButton getRadioIsActivated() {
		return radioIsActivated;
	}
}
