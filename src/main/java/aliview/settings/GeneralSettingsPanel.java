package aliview.settings;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import aliview.HelpUtils;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.util.Enumeration;
import javax.swing.SwingConstants;

public class GeneralSettingsPanel extends JPanel{
	JCheckBox cbxReverseHorizontalMouseWheel;
	JCheckBox cbxReverseVerticalMouseWheel;
	JCheckBox checkBoxHideAskBeforeEditMode;
	private JTextField txtHWheelMod;
	private JTextField txtVWheelMod;
	private JTextField txtLargeFileIndexingl;
	private JTextField txtFontSize;
	private JTextField txtMaxHistogramLargeFiles;
	private JCheckBox chckbxOverrideDefaultFont;
	
	static JFrame parFrame;
	
	public GeneralSettingsPanel(final JFrame parFrame) {
		this.parFrame = parFrame;
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{324, 100, 100, 0};
		gridBagLayout.rowHeights = new int[]{23, 23, 23, 23, 0, 23, 0, 0, 23, 0, 0, 23, 23, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
		JLabel lblReverseHorizontal = new JLabel("Reverse horizontal mouse wheel movement");		         
		GridBagConstraints gbc_lblReverseHorizontal = new GridBagConstraints();
		gbc_lblReverseHorizontal.fill = GridBagConstraints.BOTH;
		gbc_lblReverseHorizontal.insets = new Insets(0, 0, 5, 5);
		gbc_lblReverseHorizontal.gridx = 0;
		gbc_lblReverseHorizontal.gridy = 0;
		add(lblReverseHorizontal, gbc_lblReverseHorizontal);
		cbxReverseHorizontalMouseWheel = new JCheckBox();
		cbxReverseHorizontalMouseWheel.setSelected(Settings.getReverseHorizontalMouseWheel().getBooleanValue());
		GridBagConstraints gbc_cbxReverseHorizontalMouseWheel = new GridBagConstraints();
		gbc_cbxReverseHorizontalMouseWheel.fill = GridBagConstraints.BOTH;
		gbc_cbxReverseHorizontalMouseWheel.insets = new Insets(0, 0, 5, 0);
		gbc_cbxReverseHorizontalMouseWheel.gridx = 2;
		gbc_cbxReverseHorizontalMouseWheel.gridy = 0;
		add(cbxReverseHorizontalMouseWheel, gbc_cbxReverseHorizontalMouseWheel);
		
		
		JLabel lblReverseVertical = new JLabel("Reverse vertical mouse wheel movement");		         
		GridBagConstraints gbc_lblReverseVertical = new GridBagConstraints();
		gbc_lblReverseVertical.fill = GridBagConstraints.BOTH;
		gbc_lblReverseVertical.insets = new Insets(0, 0, 5, 5);
		gbc_lblReverseVertical.gridx = 0;
		gbc_lblReverseVertical.gridy = 1;
		add(lblReverseVertical, gbc_lblReverseVertical);
		cbxReverseVerticalMouseWheel = new JCheckBox();
		cbxReverseVerticalMouseWheel.setSelected(Settings.getReverseVerticalMouseWheel().getBooleanValue());
		GridBagConstraints gbc_cbxReverseVerticalMouseWheel = new GridBagConstraints();
		gbc_cbxReverseVerticalMouseWheel.fill = GridBagConstraints.BOTH;
		gbc_cbxReverseVerticalMouseWheel.insets = new Insets(0, 0, 5, 0);
		gbc_cbxReverseVerticalMouseWheel.gridx = 2;
		gbc_cbxReverseVerticalMouseWheel.gridy = 1;
		add(cbxReverseVerticalMouseWheel, gbc_cbxReverseVerticalMouseWheel);
		
		
		JLabel lblHWheelMod = new JLabel("Horizontal scroll speed (" +	         
                Settings.getHorizontalScrollModifier().getMinIntVal() + "-" + Settings.getHorizontalScrollModifier().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblHWheelMod = new GridBagConstraints();
		gbc_lblHWheelMod.fill = GridBagConstraints.BOTH;
		gbc_lblHWheelMod.insets = new Insets(0, 0, 5, 5);
		gbc_lblHWheelMod.gridx = 0;
		gbc_lblHWheelMod.gridy = 2;
		add(lblHWheelMod, gbc_lblHWheelMod);
		txtHWheelMod = new JTextField();
		txtHWheelMod.setText("" + Settings.getHorizontalScrollModifier().getIntValue());
		GridBagConstraints gbc_txtHWheelMod = new GridBagConstraints();
		gbc_txtHWheelMod.fill = GridBagConstraints.BOTH;
		gbc_txtHWheelMod.insets = new Insets(0, 0, 5, 0);
		gbc_txtHWheelMod.gridx = 2;
		gbc_txtHWheelMod.gridy = 2;
		add(txtHWheelMod, gbc_txtHWheelMod);
		
		JLabel lblVWheelMod = new JLabel("Vertical scroll speed (" +	         
                Settings.getVerticalScrollModifier().getMinIntVal() + "-" + Settings.getVerticalScrollModifier().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblVWheelMod = new GridBagConstraints();
		gbc_lblVWheelMod.fill = GridBagConstraints.BOTH;
		gbc_lblVWheelMod.insets = new Insets(0, 0, 5, 5);
		gbc_lblVWheelMod.gridx = 0;
		gbc_lblVWheelMod.gridy = 3;
		add(lblVWheelMod, gbc_lblVWheelMod);
		txtVWheelMod = new JTextField();
		txtVWheelMod.setText("" + Settings.getVerticalScrollModifier().getIntValue());
		GridBagConstraints gbc_txtVWheelMod = new GridBagConstraints();
		gbc_txtVWheelMod.fill = GridBagConstraints.BOTH;
		gbc_txtVWheelMod.insets = new Insets(0, 0, 5, 0);
		gbc_txtVWheelMod.gridx = 2;
		gbc_txtVWheelMod.gridy = 3;
		add(txtVWheelMod, gbc_txtVWheelMod);
		
		txtFontSize = new JTextField();
		txtFontSize.setText("" + Settings.getCustomFontSize().getIntValue());
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 4;
		add(txtFontSize, gbc_textField);
		txtFontSize.setColumns(10);
		txtFontSize.setToolTipText("You will also need to close and reopen program before changes take place");
		txtFontSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//setNewFontSizeInstantly(txtFontSize.getText());
			}
		});
		
		chckbxOverrideDefaultFont = new JCheckBox("Override default program font size");
		chckbxOverrideDefaultFont.setToolTipText("You will also need to close and reopen program before changes take place");
		chckbxOverrideDefaultFont.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				if(box.isSelected()){
					txtFontSize.setEnabled(true);
				}else{
					txtFontSize.setEnabled(false);
				}
			}
		});
		chckbxOverrideDefaultFont.setSelected(Settings.getUseCustomFontSize().getBooleanValue());
		if(chckbxOverrideDefaultFont.isSelected()){
			txtFontSize.setEnabled(true);
		}else{
			txtFontSize.setEnabled(false);
		}
		
		GridBagConstraints gbc_chckbxOverrideDefaultProgram = new GridBagConstraints();
		gbc_chckbxOverrideDefaultProgram.anchor = GridBagConstraints.WEST;
		gbc_chckbxOverrideDefaultProgram.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxOverrideDefaultProgram.gridx = 0;
		gbc_chckbxOverrideDefaultProgram.gridy = 4;
		add(chckbxOverrideDefaultFont, gbc_chckbxOverrideDefaultProgram);
		
		JLabel lblClearAll = new JLabel("Clear all \"Hide this checkbox\" selections");
		lblClearAll.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblClearAll = new GridBagConstraints();
		gbc_lblClearAll.anchor = GridBagConstraints.WEST;
		gbc_lblClearAll.insets = new Insets(0, 0, 5, 5);
		gbc_lblClearAll.gridx = 0;
		gbc_lblClearAll.gridy = 6;
		add(lblClearAll, gbc_lblClearAll);
		
		JButton clearCbxButton = new JButton("Clear");
		clearCbxButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				Settings.clearAllHideThisDialogCheckboxes();
			}
		});
		clearCbxButton.setPreferredSize(new Dimension(100, 30));
		GridBagConstraints gbc_clearCBXbutton = new GridBagConstraints();
		gbc_clearCBXbutton.insets = new Insets(0, 0, 5, 0);
		gbc_clearCBXbutton.gridx = 2;
		gbc_clearCBXbutton.gridy = 6;
		add(clearCbxButton, gbc_clearCBXbutton);
		
		JLabel lblAskBeforeEntering = new JLabel("Hide dialog asking before entering edit mode");
		lblAskBeforeEntering.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblAskBeforeEntering = new GridBagConstraints();
		gbc_lblAskBeforeEntering.anchor = GridBagConstraints.WEST;
		gbc_lblAskBeforeEntering.insets = new Insets(0, 0, 5, 5);
		gbc_lblAskBeforeEntering.gridx = 0;
		gbc_lblAskBeforeEntering.gridy = 7;
		add(lblAskBeforeEntering, gbc_lblAskBeforeEntering);
		
		checkBoxHideAskBeforeEditMode = new JCheckBox("");
		checkBoxHideAskBeforeEditMode.setSelected(Settings.getHideAskBeforeEditMode().getBooleanValue());
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.anchor = GridBagConstraints.WEST;
		gbc_checkBox.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox.gridx = 2;
		gbc_checkBox.gridy = 7;
		add(checkBoxHideAskBeforeEditMode, gbc_checkBox);
		
		GridBagConstraints gbc_lblNumberOfFiles = new GridBagConstraints();
		gbc_lblNumberOfFiles.fill = GridBagConstraints.BOTH;
		gbc_lblNumberOfFiles.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberOfFiles.gridx = 0;
		gbc_lblNumberOfFiles.gridy = 8;
		JLabel lblNumberOfFiles = new JLabel("Number of sequences to Index at a time in very large files");
		add(lblNumberOfFiles, gbc_lblNumberOfFiles);
		GridBagConstraints gbc_txtLargeFileIndexingl = new GridBagConstraints();
		gbc_txtLargeFileIndexingl.fill = GridBagConstraints.BOTH;
		gbc_txtLargeFileIndexingl.insets = new Insets(0, 0, 5, 0);
		gbc_txtLargeFileIndexingl.gridx = 2;
		gbc_txtLargeFileIndexingl.gridy = 8;
		txtLargeFileIndexingl = new JTextField();
		txtLargeFileIndexingl.setText("" + Settings.getLargeFileIndexing().getIntValue());
		add(txtLargeFileIndexingl, gbc_txtLargeFileIndexingl);
		
		JLabel lblNumberOfSequences = new JLabel("Number of sequences to count when calculating ClustalX-");
		GridBagConstraints gbc_lblNumberOfSequences = new GridBagConstraints();
		gbc_lblNumberOfSequences.anchor = GridBagConstraints.WEST;
		gbc_lblNumberOfSequences.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberOfSequences.gridx = 0;
		gbc_lblNumberOfSequences.gridy = 9;
		add(lblNumberOfSequences, gbc_lblNumberOfSequences);
		
		txtMaxHistogramLargeFiles = new JTextField();
		txtMaxHistogramLargeFiles.setText("" + Settings.getMaxFileHistogramSequences().getIntValue());
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 9;
		add(txtMaxHistogramLargeFiles, gbc_textField_1);
		JLabel lblNewLabel = new JLabel("consensus on large files");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 10;
		add(lblNewLabel, gbc_lblNewLabel);
		
		
		GridBagConstraints gbc_4 = new GridBagConstraints();
		gbc_4.fill = GridBagConstraints.BOTH;
		gbc_4.insets = new Insets(0, 0, 5, 5);
		gbc_4.gridx = 0;
		gbc_4.gridy = 11;
		JLabel label_1 = new JLabel("");
		add(label_1, gbc_4);
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setPreferredSize(new Dimension(100,30));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HelpUtils.display(HelpUtils.General_settings, parFrame);
			}
		});
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.anchor = GridBagConstraints.WEST;
		gbc_btnHelp.insets = new Insets(0, 0, 0, 5);
		gbc_btnHelp.gridx = 0;
		gbc_btnHelp.gridy = 12;
		add(btnHelp, gbc_btnHelp);
		
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(100, 30));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parFrame.dispose();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.EAST;
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 12;
		add(btnCancel, gbc_btnCancel);
		JButton btnOk = new JButton("OK");
		btnOk.setPreferredSize(new Dimension(100, 30));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				parFrame.dispose();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.anchor = GridBagConstraints.EAST;
		gbc_btnOk.fill = GridBagConstraints.VERTICAL;
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 12;
		add(btnOk, gbc_btnOk);
		
	}
	/*
	private void setNewFontSizeInstantly(String string)
	{
		float userSize = Float.parseFloat(txtFontSize.getText());
		Object obj = UIManager.getLookAndFeelDefaults().get("defaultFont");
		if(obj != null && obj instanceof Font){
			Font defaultFont = (Font) obj;
			UIManager.getLookAndFeelDefaults().put("defaultFont", defaultFont.deriveFont(userSize));
		}
		// and some more keys
		setUIFontSize(userSize);
		
	}

	
	public static void setUIFontSize (float newSize){
	    Enumeration<Object> keys = UIManager.getLookAndFeelDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value != null && value instanceof Font){
	    	Font derivFont = ((Font)value).deriveFont(newSize);
	    	FontUIResource fontRes = new FontUIResource(derivFont);
	    	UIManager.getLookAndFeelDefaults().put (key, fontRes);
	      }
	    }
	    SwingUtilities.updateComponentTreeUI(parFrame);
	}
	*/
	
	
	public void saveSettings(){

		Settings.putBooleanValue(Settings.getReverseHorizontalMouseWheel(), cbxReverseHorizontalMouseWheel.isSelected());

		Settings.putBooleanValue(Settings.getReverseVerticalMouseWheel(), cbxReverseVerticalMouseWheel.isSelected());
		
		Settings.putBooleanValue(Settings.getUseCustomFontSize(), chckbxOverrideDefaultFont.isSelected());
		
		Settings.putBooleanValue(Settings.getHideAskBeforeEditMode(), checkBoxHideAskBeforeEditMode.isSelected());
		
		try {
			Settings.getCustomFontSize().putIntValue(Integer.parseInt(txtFontSize.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Settings.getMaxFileHistogramSequences().putIntValue(Integer.parseInt(txtMaxHistogramLargeFiles.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Settings.getHorizontalScrollModifier().putIntValue(Integer.parseInt(txtHWheelMod.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Settings.getVerticalScrollModifier().putIntValue(Integer.parseInt(txtVWheelMod.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Settings.getLargeFileIndexing().putIntValue(Integer.parseInt(txtLargeFileIndexingl.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
