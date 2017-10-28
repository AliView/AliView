package aliview.settings;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import aliview.HelpUtils;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class PrimerSettingsPanel extends JPanel{
	private JTextField textMinPrimLen;
	private JTextField textMaxPrimLen;
	private JTextField textDimerReportThreashold;
	private JFrame parentFrame;
	private JTextField textMinPrimerTM;
	private JTextField textMaxPrimerTM;

	public PrimerSettingsPanel(JFrame parFrame) {
		this.parentFrame = parFrame;
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{213, 213, 100, 0};
		gridBagLayout.rowHeights = new int[]{32, 32, 32, 32, 32, 0, 32, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblMinPrimerLength = new JLabel("Min primer Length (" + 
				Settings.getMinPrimerLength().getMinIntVal() + "-" + Settings.getMinPrimerLength().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblMinPrimerLength = new GridBagConstraints();
		gbc_lblMinPrimerLength.fill = GridBagConstraints.BOTH;
		gbc_lblMinPrimerLength.insets = new Insets(0, 0, 5, 5);
		gbc_lblMinPrimerLength.gridx = 0;
		gbc_lblMinPrimerLength.gridy = 0;
		add(lblMinPrimerLength, gbc_lblMinPrimerLength);

		textMinPrimLen = new JTextField();
		textMinPrimLen.setText("" + Settings.getMinPrimerLength().getIntValue());
		GridBagConstraints gbc_textMinPrimLen = new GridBagConstraints();
		gbc_textMinPrimLen.gridwidth = 2;
		gbc_textMinPrimLen.fill = GridBagConstraints.BOTH;
		gbc_textMinPrimLen.insets = new Insets(0, 0, 5, 0);
		gbc_textMinPrimLen.gridx = 1;
		gbc_textMinPrimLen.gridy = 0;
		add(textMinPrimLen, gbc_textMinPrimLen);


		JLabel lblMaxPrimerLength = new JLabel("Max primer Length (" + 
				Settings.getMaxPrimerLength().getMinIntVal() + "-" + Settings.getMaxPrimerLength().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblMaxPrimerLength = new GridBagConstraints();
		gbc_lblMaxPrimerLength.fill = GridBagConstraints.BOTH;
		gbc_lblMaxPrimerLength.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxPrimerLength.gridx = 0;
		gbc_lblMaxPrimerLength.gridy = 1;
		add(lblMaxPrimerLength, gbc_lblMaxPrimerLength);

		textMaxPrimLen = new JTextField();
		textMaxPrimLen.setText("" + Settings.getMaxPrimerLength().getIntValue());
		GridBagConstraints gbc_textMaxPrimLen = new GridBagConstraints();
		gbc_textMaxPrimLen.gridwidth = 2;
		gbc_textMaxPrimLen.fill = GridBagConstraints.BOTH;
		gbc_textMaxPrimLen.insets = new Insets(0, 0, 5, 0);
		gbc_textMaxPrimLen.gridx = 1;
		gbc_textMaxPrimLen.gridy = 1;
		add(textMaxPrimLen, gbc_textMaxPrimLen);


		JLabel lblDimerReportThreashold = new JLabel("DimerReportThreashold (" + 
				Settings.getDimerReportThreashold().getMinIntVal() + "-" + Settings.getDimerReportThreashold().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblDimerReportThreashold = new GridBagConstraints();
		gbc_lblDimerReportThreashold.fill = GridBagConstraints.BOTH;
		gbc_lblDimerReportThreashold.insets = new Insets(0, 0, 5, 5);
		gbc_lblDimerReportThreashold.gridx = 0;
		gbc_lblDimerReportThreashold.gridy = 2;
		add(lblDimerReportThreashold, gbc_lblDimerReportThreashold);

		textDimerReportThreashold = new JTextField();
		textDimerReportThreashold.setText("" + Settings.getDimerReportThreashold().getIntValue());
		GridBagConstraints gbc_textDimerReportThreashold = new GridBagConstraints();
		gbc_textDimerReportThreashold.gridwidth = 2;
		gbc_textDimerReportThreashold.fill = GridBagConstraints.BOTH;
		gbc_textDimerReportThreashold.insets = new Insets(0, 0, 5, 0);
		gbc_textDimerReportThreashold.gridx = 1;
		gbc_textDimerReportThreashold.gridy = 2;
		add(textDimerReportThreashold, gbc_textDimerReportThreashold);


		JLabel lblMinTM = new JLabel("Min TM (" + 
				Settings.getPrimerMinTM().getMinIntVal() + "-" + Settings.getPrimerMinTM().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblMinTM = new GridBagConstraints();
		gbc_lblMinTM.fill = GridBagConstraints.BOTH;
		gbc_lblMinTM.insets = new Insets(0, 0, 5, 5);
		gbc_lblMinTM.gridx = 0;
		gbc_lblMinTM.gridy = 3;
		add(lblMinTM, gbc_lblMinTM);

		textMinPrimerTM = new JTextField();
		textMinPrimerTM.setText("" + Settings.getPrimerMinTM().getIntValue());
		GridBagConstraints gbc_textMinPrimerTM = new GridBagConstraints();
		gbc_textMinPrimerTM.gridwidth = 2;
		gbc_textMinPrimerTM.fill = GridBagConstraints.BOTH;
		gbc_textMinPrimerTM.insets = new Insets(0, 0, 5, 0);
		gbc_textMinPrimerTM.gridx = 1;
		gbc_textMinPrimerTM.gridy = 3;
		add(textMinPrimerTM, gbc_textMinPrimerTM);


		JLabel lblMaxTM = new JLabel("Max TM (" + 
				Settings.getPrimerMaxTM().getMinIntVal() + "-" + Settings.getPrimerMaxTM().getMaxIntVal() + ")");
		GridBagConstraints gbc_lblMaxTM = new GridBagConstraints();
		gbc_lblMaxTM.fill = GridBagConstraints.BOTH;
		gbc_lblMaxTM.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxTM.gridx = 0;
		gbc_lblMaxTM.gridy = 4;
		add(lblMaxTM, gbc_lblMaxTM);

		textMaxPrimerTM = new JTextField();
		textMaxPrimerTM.setText("" + Settings.getPrimerMaxTM().getIntValue());
		GridBagConstraints gbc_textMaxPrimerTM = new GridBagConstraints();
		gbc_textMaxPrimerTM.gridwidth = 2;
		gbc_textMaxPrimerTM.fill = GridBagConstraints.BOTH;
		gbc_textMaxPrimerTM.insets = new Insets(0, 0, 5, 0);
		gbc_textMaxPrimerTM.gridx = 1;
		gbc_textMaxPrimerTM.gridy = 4;
		add(textMaxPrimerTM, gbc_textMaxPrimerTM);


		JButton btnOk = new JButton("OK");
		btnOk.setPreferredSize(new Dimension(100, 30));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				parentFrame.dispose();
			}
		});

		JButton btnHelp = new JButton("Help");
		btnHelp.setPreferredSize(new Dimension(100,30));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HelpUtils.display(HelpUtils.Find_Primer_settings, parentFrame);
			}
		});
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.anchor = GridBagConstraints.WEST;
		gbc_btnHelp.insets = new Insets(0, 0, 0, 5);
		gbc_btnHelp.gridx = 0;
		gbc_btnHelp.gridy = 6;
		add(btnHelp, gbc_btnHelp);

		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.anchor = GridBagConstraints.WEST;
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 6;
		add(btnOk, gbc_btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(100, 30));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.dispose();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.anchor = GridBagConstraints.EAST;
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 6;
		add(btnCancel, gbc_btnCancel);








		/*

		add(new JLabel(""));
		add(new JLabel(""));
		add(new JLabel(""));
		add(new JLabel(""));
		 */	

		/*
		Pattern regex = Pattern.compile("10-20");
	    RegexInputVerifier verifier = new RegexInputVerifier(regex, RegexInputVerifier.UseToolTip.FALSE, "20");

	    textMinPrimLen.setToolTipText("Value has to be between...");
		textMinPrimLen.setInputVerifier(verifier);
		 */
	}


	public void saveSettings(){
		try {
			Settings.getMinPrimerLength().putIntValue(Integer.parseInt(textMinPrimLen.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Settings.getMaxPrimerLength().putIntValue(Integer.parseInt(textMaxPrimLen.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Settings.getDimerReportThreashold().putIntValue(Integer.parseInt(textDimerReportThreashold.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		try {
			Settings.getPrimerMinTM().putIntValue(Integer.parseInt(textMinPrimerTM.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Settings.getPrimerMaxTM().putIntValue(Integer.parseInt(textMaxPrimerTM.getText()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
