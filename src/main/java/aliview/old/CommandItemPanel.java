package aliview.old;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import aliview.AliViewWindow;
import aliview.externalcommands.CommandItem;

public class CommandItemPanel extends JPanel{
	private static final String LF = System.getProperty("line.separator");	
	
	public CommandItemPanel(final CommandItem cmdItem, final AliViewWindow aliWin){
	
		JTextField txtCommandName = new JTextField(cmdItem.getName(), 15);
		txtCommandName.setMaximumSize(new Dimension(130, 25));
		txtCommandName.setMinimumSize(new Dimension(130, 25));
		txtCommandName.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e) {
				JTextField txt = (JTextField) e.getSource();
				cmdItem.setName(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});
		
		JTextArea txtCommand = new JTextArea(cmdItem.getCommand(),2,50);
		txtCommand.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new EmptyBorder(2,2,2,2)));
		txtCommand.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e){
				JTextArea txt = (JTextArea) e.getSource();
				cmdItem.setCommand(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});
		
		JCheckBox cbxIsActivated = new JCheckBox();
		cbxIsActivated.setSelected(cmdItem.isActivated());
		cbxIsActivated.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cbx = (JCheckBox) e.getSource();
				cmdItem.setActivated(cbx.isSelected());
			}
		});
		
		JCheckBox cbxOutputWin = new JCheckBox();
		cbxOutputWin.setSelected(cmdItem.isShowCommandWindow());
		cbxOutputWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cbx = (JCheckBox) e.getSource();
				cmdItem.setShowCommandWindow(cbx.isSelected());
			}
		});
		
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		//GridLayout layout = new GridLayout(1,4,3,3);
		//layout. setColumnExpandRatio(0, 0.33f);
		//this.setLayout(gridLayout);
		this.add(cbxIsActivated);
		this.add(cbxOutputWin);
		this.add(txtCommandName);
		this.add(txtCommand);
		//JButton removeButton = new JButton("Remove");
		//this.add(removeButton);
		JButton testButton = new JButton("Test");			
	//	this.add(testButton);
		testButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				aliWin.runExternalCommand(cmdItem);
			}
		});
		this.setMaximumSize(new Dimension(2000,30));
	}
	
}
