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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.HelpUtils;
import aliview.externalcommands.CommandItem;
import aliview.old.MyScrollPane;
import aliview.settings.Settings;
import aliview.settings.SettingsFrame;

public class AlignerALLItemsPanel extends JPanel {
	private static final Logger logger = Logger.getLogger(AlignerALLItemsPanel.class);
	JPanel mainPanel;

	public AlignerALLItemsPanel(final JFrame parentFrame){

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(0,0,0,0));


		JPanel itemsPanel = new JPanel();
		itemsPanel.setBorder(new EmptyBorder(0,0,0,0));
		itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(itemsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportBorder( new EmptyBorder(0,0,0,0) );
		mainPanel.add(scrollPane,BorderLayout.CENTER);
		//mainPanel.add(itemsPanel, BorderLayout.CENTER);

		// get saved commands (or default will be served by settings if none)
		final ArrayList<CommandItem> alignItems = Settings.getAlignALLCommands();

		ButtonGroup bg = new ButtonGroup();
		for(CommandItem alignItem: alignItems){
			AliItemPanel itemPanel = new AliItemPanel(alignItem);
			itemsPanel.add(itemPanel);
			bg.add(itemPanel.getRadioIsActivated());
		}

		// Butttons att bottom
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));

		bottomButtonPanel.add(Box.createHorizontalStrut(20));

		JButton helpButton = new JButton("Help");
		helpButton.setPreferredSize(new Dimension(100,30));
		helpButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				HelpUtils.display(HelpUtils.ALIGNER_SETTINGS_ALL, parentFrame);
			}
		});
		bottomButtonPanel.add(helpButton);



		bottomButtonPanel.add(Box.createHorizontalGlue());

		JButton resetButton = new JButton("Reset defaults");
		resetButton.setPreferredSize(new Dimension(100,30));
		resetButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				Settings.clearAlignALLCommands();
				if(parentFrame instanceof SettingsFrame){
					((SettingsFrame) parentFrame).reload();
				}
			}
		});
		bottomButtonPanel.add(resetButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(70,30));
		cancelButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				parentFrame.dispose();
			}
		});
		bottomButtonPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(70,30));
		okButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e){
				Settings.putAlignALLCommands(alignItems);
				parentFrame.dispose();
			}
		});
		bottomButtonPanel.add(okButton);

		bottomButtonPanel.add(Box.createHorizontalStrut(20));

		mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);

	}

}