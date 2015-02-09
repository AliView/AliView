package aliview.sequencelist;


import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;


public class NotUsedSequenceListMouseListener2 implements MouseMotionListener{
	
	private static final Logger logger = Logger.getLogger(NotUsedSequenceListMouseListener2.class);

	public void mouseDragged(MouseEvent e) {
		logger.info("dragged" + e.getLocationOnScreen());
		
	}

	public void mouseMoved(MouseEvent e) {
		logger.info("moved" + e.getLocationOnScreen());
		
	}
	
	

}
