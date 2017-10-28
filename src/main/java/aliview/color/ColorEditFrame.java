package aliview.color;

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
import aliview.gui.TextEditFrame;

public class ColorEditFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(ColorEditFrame.class);
	private Component parentFrame;

	public static void main(String[] args) {
		new ColorEditFrame(null);
	}

	public ColorEditFrame(Component parent){
		this.parentFrame = parent;
		logger.info("constructor");
		ColorEditPanel cep = new ColorEditPanel(this);
		cep.setPreferredSize(new Dimension(500, 500));

		init(cep);
	}

	public void init(JPanel mainPanel){
		this.getContentPane().add(mainPanel);
		this.setIconImage(AppIcons.getProgramIconImage());
		this.setTitle("Edit");
		this.setPreferredSize(new Dimension(550,400));
		this.setAlwaysOnTop(true);
		this.pack();
		this.setVisible(true);
		//this.centerLocationToThisComponent(parentFrame);
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
