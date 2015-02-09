package aliview.sequencelist;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JList;

import org.apache.log4j.Logger;

import sun.swing.SwingUtilities2;
import aliview.AliView;
import aliview.AliViewWindow;

/*
 * 
 * These mouse listener events should be handled before the default List Mouse listener
 * events, this way we add some specific select functionality
 * 
 * Extra functionality provided by this class is:
 * 
 * 1. Rubber band select in list
 * 2. Rename by slow double click
 *
 */
public class SequenceListMouseListener implements MouseListener, MouseMotionListener{
	private static final Logger logger = Logger.getLogger(SequenceListMouseListener.class);

	private int startIndex = -1;
	private long lastReleaseTime;
	private int lastReleaseIndex;
	private long minReleaseInterval = 500;
	private long maxReleaseInterval = 2000;
	private AliViewWindow aliWin;
	
	
	public SequenceListMouseListener(AliViewWindow aliWin) {
		super();
		this.aliWin = aliWin;
	}


	public void mousePressed(MouseEvent e){
	
		logger.info("mousePressed");
		if(e.isAltDown() || e.isControlDown() || e.isShiftDown() || e.isMetaDown()){
			return;
		}
		
		JList list = (JList) e.getSource();
		int clickIndex = list.locationToIndex(e.getPoint());
		
		// if already selected return and let default mouseListener in JList
		// take care about event - could for example be a drag event
		if(list.isSelectedIndex(clickIndex)){
			return;
		}
		// if not selected this could be the start of a rubber-band select in the list
		else{
			list.setSelectedIndex(clickIndex);
			startIndex = clickIndex;
			// give list focus
			SwingUtilities2.adjustFocus(list);
			e.consume();
		}
	}

	
	public void mouseReleased(MouseEvent e) {
	
		logger.info("mouseReleased");
		
		// check if this release is part of a rename-trigger-event
		if(isRenameTrigger(e)){
			aliWin.renameFirstSelected();		
			e.consume();
		}
		
		if(startIndex != -1){
			startIndex = -1;
			e.consume();
		}	
		
	}


	private boolean isRenameTrigger(MouseEvent e) {

		long releaseTime = e.getWhen();
		JList list = (JList) e.getSource();
		int releaseIndex = list.locationToIndex(e.getPoint());
		
		boolean isRenameTrigger = false;
		if(lastReleaseIndex == releaseIndex){
			if(list.isSelectedIndex(releaseIndex)){
				long timeBetweenReleases = releaseTime - lastReleaseTime;
				if(timeBetweenReleases > minReleaseInterval && timeBetweenReleases < maxReleaseInterval){
					isRenameTrigger = true;
				}
				
			}
		}

		lastReleaseIndex = releaseIndex;
		lastReleaseTime = releaseTime;
		
		return isRenameTrigger;
		
	}

	//
	// Select all index between start and this if mouse is dragged ("rubber-band" select)
	//
	public void mouseDragged(MouseEvent e) {
		logger.info("mousePressed");
		if(startIndex == -1){
			return;
		}
		if(e.isAltDown() || e.isControlDown() || e.isShiftDown() || e.isMetaDown()){
			return;
		}
		
		JList list = (JList) e.getSource();
		int pointerIndex = list.locationToIndex(e.getPoint());
		logger.info(pointerIndex);
		list.getSelectionModel().setSelectionInterval(startIndex, pointerIndex);
		list.ensureIndexIsVisible(pointerIndex);
		e.consume();	
	}


	public void mouseMoved(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseClicked(MouseEvent e) {	
	}
}