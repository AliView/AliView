package aliview.aligner;

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
import javax.swing.ButtonGroup;
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
import aliview.externalcommands.CommandItem;
import aliview.old.MyScrollPane;
import aliview.settings.Settings;

public class AlignerADDItemsFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(AlignerADDItemsFrame.class);
	JPanel mainPanel;

	public AlignerADDItemsFrame(final AliViewWindow aliViewWin){
		final AlignerADDItemsFrame alignItemsFrame = this;


		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		getContentPane().add(mainPanel, BorderLayout.CENTER);

		//JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//getContentPane().add(scrollPane, BorderLayout.CENTER);

		// get saved commands (or default will be served by settings if none)
		final ArrayList<CommandItem> alignItems = Settings.getAlignADDCommands();

		ButtonGroup bg = new ButtonGroup();
		for(CommandItem alignItem: alignItems){
			AliItemPanel itemPanel = new AliItemPanel(alignItem);
			//itemsPanel.add(itemPanel);
			bg.add(itemPanel.getRadioIsActivated());
		}

		// OK - Cancel Buttton
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
		bottomButtonPanel.add(Box.createHorizontalGlue());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				alignItemsFrame.dispose();
			}
		});
		bottomButtonPanel.add(cancelButton);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e){
				Settings.putAlignALLCommands(alignItems);
				alignItemsFrame.dispose();
			}
		});
		bottomButtonPanel.add(okButton);

		mainPanel.add(bottomButtonPanel);

		// Arrange Frame
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {		
			public void windowClosing(WindowEvent e){
				// Dont save here - only close
				alignItemsFrame.dispose();
			}
		});	
		this.setTitle("Alignment program settings");
		this.setPreferredSize(new Dimension(650,600));
		this.pack();
		this.centerLocationToThisComponent(aliViewWin);
		this.setVisible(true);
	}

	public void centerLocationToThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - this.getWidth()/2;
			int newY = parent.getY() + parent.getHeight()/2 - this.getHeight()/2;

			if(newX > parent.getX() && newX > parent.getY()){
				this.setLocation(newX, newY);
			}
		}
	}
}