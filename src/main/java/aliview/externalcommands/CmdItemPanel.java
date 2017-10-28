package aliview.externalcommands;

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

public class CmdItemPanel extends JPanel{

	JCheckBox cbxIsActivated;

	public CmdItemPanel(final CommandItem cmdItem){

		final JPanel aliItemPanel = this;
		//  this.setPreferredSize(new Dimension(650, 100));

		aliItemPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gbl_aliItemPanel = new GridBagLayout();
		gbl_aliItemPanel.columnWidths = new int[] {0, 140, 100, 100};
		gbl_aliItemPanel.rowHeights = new int[] {30, 25, 30};
		gbl_aliItemPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0};
		gbl_aliItemPanel.rowWeights = new double[]{0.0, 1.0, 1.0};
		aliItemPanel.setLayout(gbl_aliItemPanel);

		JLabel lblItemName = new JLabel("Display name:");
		lblItemName.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblItemName = new GridBagConstraints();
		gbc_lblItemName.anchor = GridBagConstraints.WEST;
		gbc_lblItemName.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName.gridx = 1;
		gbc_lblItemName.gridy = 0;
		add(lblItemName, gbc_lblItemName);

		JTextField txtCommandName = new JTextField();
		txtCommandName.setText("mafft");
		GridBagConstraints gbc_txtCommandName = new GridBagConstraints();
		gbc_txtCommandName.anchor = GridBagConstraints.NORTH;
		gbc_txtCommandName.insets = new Insets(0, 0, 5, 5);
		gbc_txtCommandName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCommandName.gridx = 1;
		gbc_txtCommandName.gridy = 1;
		aliItemPanel.add(txtCommandName, gbc_txtCommandName);
		txtCommandName.setColumns(10);
		txtCommandName.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e) {
				JTextField txt = (JTextField) e.getSource();
				cmdItem.setName(txt.getText());
				//alignItem.getAddAlignCmd().setName(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});
		txtCommandName.setText(cmdItem.getName());



		JLabel lblCommand = new JLabel("Command:");
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.anchor = GridBagConstraints.WEST;
		gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand.gridx = 2;
		gbc_lblCommand.gridy = 0;
		add(lblCommand, gbc_lblCommand);

		JTextArea txtCommand = new JTextArea();
		txtCommand.setLineWrap(true);
		txtCommand.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtCommand.setRows(2);
		GridBagConstraints gbc_txtCommand = new GridBagConstraints();
		gbc_txtCommand.gridwidth = 2;
		gbc_txtCommand.gridheight = 2;
		gbc_txtCommand.fill = GridBagConstraints.BOTH;
		gbc_txtCommand.gridx = 2;
		gbc_txtCommand.gridy = 1;
		aliItemPanel.add(txtCommand, gbc_txtCommand);
		txtCommand.setColumns(10);
		txtCommand.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e){
				JTextArea txt = (JTextArea) e.getSource();
				cmdItem.setCommand(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});
		txtCommand.setText(cmdItem.getCommand());



		JCheckBox chckbxShowCmdOutput = new JCheckBox("Show command window output");
		GridBagConstraints gbc_chckbxShowCmdOutput = new GridBagConstraints();
		gbc_chckbxShowCmdOutput.anchor = GridBagConstraints.EAST;
		gbc_chckbxShowCmdOutput.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxShowCmdOutput.gridx = 3;
		gbc_chckbxShowCmdOutput.gridy = 0;
		add(chckbxShowCmdOutput, gbc_chckbxShowCmdOutput);
		chckbxShowCmdOutput.setSelected(cmdItem.isShowCommandWindow());
		chckbxShowCmdOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cbx = (JCheckBox) e.getSource();
				cmdItem.setShowCommandWindow(cbx.isSelected());
			}
		});


		cbxIsActivated = new JCheckBox("");
		GridBagConstraints gbc_cbxIsActivated = new GridBagConstraints();
		gbc_cbxIsActivated.fill = GridBagConstraints.VERTICAL;
		gbc_cbxIsActivated.insets = new Insets(0, 0, 5, 5);
		gbc_cbxIsActivated.gridx = 0;
		gbc_cbxIsActivated.gridy = 1;
		aliItemPanel.add(cbxIsActivated, gbc_cbxIsActivated);
		cbxIsActivated.setSelected(cmdItem.isActivated());
		cbxIsActivated.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cbx = (JCheckBox) e.getSource();
				cmdItem.setActivated(cbx.isSelected());
			}
		});

		/*
	JButton btnTest = new JButton("Test");
	GridBagConstraints gbc_btnTest = new GridBagConstraints();
	gbc_btnTest.insets = new Insets(0, 0, 0, 5);
	gbc_btnTest.gridx = 1;
	gbc_btnTest.gridy = 2;
	add(btnTest, gbc_btnTest);
		 */

		/*
	JButton btnBrowseProgram = new JButton("Browse");
	btnBrowseProgram.setPreferredSize(new Dimension(100,25));
	GridBagConstraints gbc_btnBrowseProgram = new GridBagConstraints();
	gbc_btnBrowseProgram.anchor = GridBagConstraints.SOUTHEAST;
	gbc_btnBrowseProgram.gridheight = 3;
	gbc_btnBrowseProgram.insets = new Insets(0, 0, 0, 5);
	gbc_btnBrowseProgram.gridx = 1;
	gbc_btnBrowseProgram.gridy = 2;
	add(btnBrowseProgram, gbc_btnBrowseProgram);
	btnBrowseProgram.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
			String suggestedDir = Settings.getExternalCommandFileDirectory();
			File suggestedFile = new File(suggestedDir);
			File selectedFile = FileUtilities.selectOpenFileViaChooser(suggestedFile,aliItemPanel);
			if(selectedFile != null){
		//		txtProgramFile.setText(selectedFile.getAbsolutePath());
		//		alignItem.setProgramPath(selectedFile.getAbsolutePath());
		//		Settings.putExternalCommandFileDirectory(selectedFile.getAbsolutePath());	
			}
		}
	});
		 */
	}

	public JCheckBox getCbxIsActivated() {
		return cbxIsActivated;
	}

}