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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.externalcommands.CommandItem;
import aliview.old.MyScrollPane;
import aliview.settings.Settings;

public class AlignerALLItemsFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(AlignerALLItemsFrame.class);
	JPanel mainPanel;

	public AlignerALLItemsFrame(final AliViewWindow aliViewWin){
		
		getContentPane().add(new AlignerALLItemsPanel(this), BorderLayout.CENTER);
	
		// Arrange Frame
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {		
			public void windowClosing(WindowEvent e){
				// Dont save here - only close
				dispose();
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