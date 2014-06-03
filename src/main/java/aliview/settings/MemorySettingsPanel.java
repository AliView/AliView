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
import javax.swing.JTextPane;

public class MemorySettingsPanel extends JPanel{
	private static final String LF = System.getProperty("line.separator");
	private JFrame parentFrame;

	public MemorySettingsPanel(JFrame parFrame) {
		this.parentFrame = parFrame;
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{213, 213, 100, 0};
		gridBagLayout.rowHeights = new int[]{32, 32, 32, 32, 32, 0, 32, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setEditable(false);
//		textPane.setText("To change memory settings of aliview you have to edit the settings in a textfile on your computer, " +
//		                 "depending on your operating system this file is located differently." + LF + 
//		                 "Please click help button for exact instructions.");
		
		textPane.setText("<html><body><h2><a name=\"memory_settings\">Memory settings</a></h2><p>If you want AliView to read larger alignments in memory and not from file " +
		                 "(this allows for more editing capabilities), then you can change the maximum memory settings for the program." + 
				         "<br>The amount of memory needed for a file to be read into memory is about 2 x file size.</p><p><b>Mac OS X</b> </p><p>Go to " + 
				         "Applications in Finder --&gt;Left click on specific application AliView --&gt; Show Package Content --&gt; Contents --&gt; Then open the file \"Info.Plist\" " + 
		                 "in a text-editor and change the parameter: &lt;string&gt;-Xmx512m -Xms128m&lt;/string&gt; to something different (for example 2GB=2048M):<br> &lt;string&gt;-Xmx2048m" + 
				         " -Xms128m&lt;/string&gt;</p><p><b>Linux</b> </p> <p>/usr/bin/aliview <br> open this file in text-editor and change the parameter -Xmx1024M (default setting = " + 
		                 "1024M memory)</p> <p><b>Windows</b> </p> <p>In the installation folder of AliView (default: 'c:\\Program Files\\AliView\\') open the file \"AliView.l4j.ini\" in " + 
				         "a text editor and change the setting: -Xmx1024m to something you prefer (for example 2GB=2048M) -Xmx2048m</p></body></html>");
		
		
		
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridwidth = 3;
		gbc_textPane.gridheight = 6;
		gbc_textPane.insets = new Insets(0, 0, 5, 5);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 0;
		add(textPane, gbc_textPane);


		JButton btnOk = new JButton("OK");
		btnOk.setPreferredSize(new Dimension(100, 30));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.dispose();
			}
		});
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setPreferredSize(new Dimension(100,30));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HelpUtils.display(HelpUtils.MEMORY_SETTINGS, parentFrame);
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




	}
}
