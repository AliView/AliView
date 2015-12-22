package aliview.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.log4j.Logger;


public class ListTopOffsetJPanel extends JPanel{
	private static final Logger logger = Logger.getLogger(ListTopOffsetJPanel.class);
	Component watchedComponent;
	int preferredWidth = 100;
	
	public ListTopOffsetJPanel(Component componentWhichHeightToFollow) {
		this.watchedComponent = componentWhichHeightToFollow;
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = null;
		if(watchedComponent == null){
			prefSize = new Dimension(preferredWidth, 0);
		}
		else{
			prefSize = new Dimension(preferredWidth, watchedComponent.getPreferredSize().height);
		}
		logger.info(prefSize);
		return prefSize;
	}
}
