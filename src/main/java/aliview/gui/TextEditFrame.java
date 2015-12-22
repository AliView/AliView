package aliview.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import aliview.aligner.AlignerADDItemsPanel;
import aliview.aligner.AlignerALLItemsPanel;
import aliview.alignment.Alignment;
import aliview.externalcommands.CmdItemsPanel;
import aliview.gui.AppIcons;

public class TextEditFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(TextEditFrame.class);
	private Component parentFrame;
	
	public TextEditFrame(Component parent){
		this.parentFrame = parent;
		logger.info("constructor");
	}
	
	public void init(JPanel mainPanel){
		this.getContentPane().add(mainPanel);
		this.setIconImage(AppIcons.getProgramIconImage());
		this.setTitle("Edit");
		this.setPreferredSize(new Dimension(550,400));
		this.setAlwaysOnTop(true);
		this.pack();
		this.centerLocationToThisComponent(parentFrame);
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
