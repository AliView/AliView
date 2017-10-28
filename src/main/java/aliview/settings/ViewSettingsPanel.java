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
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class ViewSettingsPanel extends JPanel{
	JCheckBox cbxReverseHorizontalMouseWheel;
	JCheckBox cbxReverseVerticalMouseWheel;
	private JTextField txtHWheelMod;
	private JTextField txtVWheelMod;
	private JTextField txtLargeFileIndexingl;
	private JTextField txtFontSize;
	private JTextField txtMaxHistogramLargeFiles;
	private JCheckBox chckbxOverrideDefaultFont;

	public ViewSettingsPanel(final JFrame parFrame) {
		/*
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{324, 100, 100, 0};
		gridBagLayout.rowHeights = new int[]{23, 23, 23, 23, 0, 23, 23, 0, 0, 23, 23, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
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
		gbc_cbxReverseHorizontalMouseWheel.gridwidth = 2;
		gbc_cbxReverseHorizontalMouseWheel.fill = GridBagConstraints.BOTH;
		gbc_cbxReverseHorizontalMouseWheel.insets = new Insets(0, 0, 5, 0);
		gbc_cbxReverseHorizontalMouseWheel.gridx = 1;
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
		gbc_cbxReverseVerticalMouseWheel.gridwidth = 2;
		gbc_cbxReverseVerticalMouseWheel.fill = GridBagConstraints.BOTH;
		gbc_cbxReverseVerticalMouseWheel.insets = new Insets(0, 0, 5, 0);
		gbc_cbxReverseVerticalMouseWheel.gridx = 1;
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
		gbc_txtHWheelMod.insets = new Insets(0, 0, 5, 5);
		gbc_txtHWheelMod.gridx = 1;
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
		gbc_txtVWheelMod.insets = new Insets(0, 0, 5, 5);
		gbc_txtVWheelMod.gridx = 1;
		gbc_txtVWheelMod.gridy = 3;
		add(txtVWheelMod, gbc_txtVWheelMod);

		txtFontSize = new JTextField();
		txtFontSize.setText("" + Settings.getCustomFontSize().getIntValue());
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 4;
		add(txtFontSize, gbc_textField);
		txtFontSize.setColumns(10);


		chckbxOverrideDefaultFont = new JCheckBox("Override default program font size");
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
		if(chckbxOverrideDefaultFont.isSelected()){
			txtFontSize.setEnabled(true);
		}else{
			txtFontSize.setEnabled(false);
		}
		chckbxOverrideDefaultFont.setSelected(Settings.getUseCustomFontSize().getBooleanValue());
		GridBagConstraints gbc_chckbxOverrideDefaultProgram = new GridBagConstraints();
		gbc_chckbxOverrideDefaultProgram.anchor = GridBagConstraints.WEST;
		gbc_chckbxOverrideDefaultProgram.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxOverrideDefaultProgram.gridx = 0;
		gbc_chckbxOverrideDefaultProgram.gridy = 4;
		add(chckbxOverrideDefaultFont, gbc_chckbxOverrideDefaultProgram);


		GridBagConstraints gbc_lblNumberOfFiles = new GridBagConstraints();
		gbc_lblNumberOfFiles.fill = GridBagConstraints.BOTH;
		gbc_lblNumberOfFiles.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberOfFiles.gridx = 0;
		gbc_lblNumberOfFiles.gridy = 6;
		JLabel lblNumberOfFiles = new JLabel("Number of sequences to Index at a time in very large files");
		add(lblNumberOfFiles, gbc_lblNumberOfFiles);
		GridBagConstraints gbc_txtLargeFileIndexingl = new GridBagConstraints();
		gbc_txtLargeFileIndexingl.fill = GridBagConstraints.BOTH;
		gbc_txtLargeFileIndexingl.insets = new Insets(0, 0, 5, 5);
		gbc_txtLargeFileIndexingl.gridx = 1;
		gbc_txtLargeFileIndexingl.gridy = 6;
		txtLargeFileIndexingl = new JTextField();
		txtLargeFileIndexingl.setText("" + Settings.getLargeFileIndexing().getIntValue());
		add(txtLargeFileIndexingl, gbc_txtLargeFileIndexingl);

		JLabel lblNumberOfSequences = new JLabel("Number of sequences to count when calculating ClustalX-");
		GridBagConstraints gbc_lblNumberOfSequences = new GridBagConstraints();
		gbc_lblNumberOfSequences.anchor = GridBagConstraints.WEST;
		gbc_lblNumberOfSequences.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberOfSequences.gridx = 0;
		gbc_lblNumberOfSequences.gridy = 7;
		add(lblNumberOfSequences, gbc_lblNumberOfSequences);
		JLabel lblNewLabel = new JLabel("consensus on large files");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 8;
		add(lblNewLabel, gbc_lblNewLabel);

		txtMaxHistogramLargeFiles = new JTextField();
		txtMaxHistogramLargeFiles.setText("" + Settings.getMaxFileHistogramSequences().getIntValue());
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 7;
		add(txtMaxHistogramLargeFiles, gbc_textField_1);


		GridBagConstraints gbc_4 = new GridBagConstraints();
		gbc_4.fill = GridBagConstraints.BOTH;
		gbc_4.insets = new Insets(0, 0, 5, 5);
		gbc_4.gridx = 0;
		gbc_4.gridy = 9;
		JLabel label_1 = new JLabel("");
		add(label_1, gbc_4);
		GridBagConstraints gbc_5 = new GridBagConstraints();
		gbc_5.gridwidth = 2;
		gbc_5.fill = GridBagConstraints.BOTH;
		gbc_5.insets = new Insets(0, 0, 5, 0);
		gbc_5.gridx = 1;
		gbc_5.gridy = 9;
		JLabel label_4 = new JLabel("");
		add(label_4, gbc_5);
		JButton btnOk = new JButton("OK");
		btnOk.setPreferredSize(new Dimension(100, 25));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				parFrame.dispose();
			}
		});

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(100, 25));
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 10;
		add(btnCancel, gbc_btnCancel);
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.fill = GridBagConstraints.VERTICAL;
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 10;
		add(btnOk, gbc_btnOk);
		 */

	}


	public void saveSettings(){

		Settings.putBooleanValue(Settings.getReverseHorizontalMouseWheel(), cbxReverseHorizontalMouseWheel.isSelected());

		Settings.putBooleanValue(Settings.getReverseVerticalMouseWheel(), cbxReverseVerticalMouseWheel.isSelected());

		Settings.putBooleanValue(Settings.getUseCustomFontSize(), chckbxOverrideDefaultFont.isSelected());





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
