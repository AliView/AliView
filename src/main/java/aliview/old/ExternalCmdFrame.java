package aliview.old;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.externalcommands.CmdItemPanel;
import aliview.externalcommands.CommandItem;
import aliview.settings.Settings;

public class ExternalCmdFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(ExternalCmdFrame.class);
	JPanel mainPanel;

	public ExternalCmdFrame(final AliViewWindow aliViewWin){
		final ExternalCmdFrame commandFrame = this;
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		// get saved commands (or default will be served by settings if none)
		final ArrayList<CommandItem> cmdItems = Settings.getExternalCommands();
		
		//final CommandItem cmdItems[] = Settings.COMMAND_ITEM_DEFAULTS;
		
		//mainPanel.add(new AlignerItem("AliVi", "command", true, false));
		//mainPanel.add(new AlignerItem("AliVjklkjhi", "commahkjh k jhhkjhk klhkj hkjh kjh nd", true, false));
		
		
		for(CommandItem cmdItem: cmdItems){
			mainPanel.add(new CmdItemPanel(cmdItem));
		}
		mainPanel.add(Box.createVerticalGlue());
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		// OK - Cancel Buttton
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
		bottomButtonPanel.add(Box.createHorizontalGlue());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				commandFrame.dispose();
			}
		});
		bottomButtonPanel.add(cancelButton);
		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(100,25));
		okButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				Settings.putExternalCommands(cmdItems);
				commandFrame.dispose();
			}
		});
		bottomButtonPanel.add(okButton);
		
		mainPanel.add(bottomButtonPanel);
				
		// Arrange Frame
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {		
			public void windowClosing(WindowEvent e){
				// Dont save here - only
				Settings.putExternalCommands(cmdItems);
				commandFrame.dispose();
				}
		});	
		this.setTitle("External commands");
		this.setPreferredSize(new Dimension(650,550));
		this.pack();
		this.centerLocationToThisComponent(aliViewWin);
		this.setVisible(true);
		okButton.requestFocus();
	}
	
	public void centerLocationToThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - this.getWidth()/2;
			int newY = parent.getY() + parent.getHeight()/2 - this.getHeight()/2;
			this.setLocation(newX, newY);
		}
	}
}