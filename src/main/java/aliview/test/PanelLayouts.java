package aliview.test;

import javax.swing.JFrame;

import aliview.aligner.AliItemPanel;
import aliview.aligner.AlignemtItem;
import aliview.externalcommands.CommandItem;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;


public class PanelLayouts extends JFrame{
	private JTextField txtMafft;
	private JTextArea tarea;
	private JTextArea tarea2;
	private JTextField txtfilemafft;
	public PanelLayouts() {
		
		getContentPane().setLayout(new GridLayout(3, 1, 0, 0));		
		AliItemPanel aliItemPanel = new AliItemPanel(new CommandItem("name", "path", "aligm", true, true));
		getContentPane().add(aliItemPanel);	
		
	//	this.setPreferredSize(new Dimension(600,600));
		
	}
	
	public static void main(String[] args) {
		PanelLayouts pl = new PanelLayouts();
		pl.pack();
		pl.setVisible(true);
	}

}
