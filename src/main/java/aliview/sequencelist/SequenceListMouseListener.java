package aliview.sequencelist;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JList;

import org.apache.log4j.Logger;

/*
 * 
 * 
 * This class was created to test drag selection of multiple rows - but it is not really working
 * because it is conflicting with drag-drop
 * 
 * 
 * 
 */
public class SequenceListMouseListener implements MouseListener, MouseMotionListener{
	private static final Logger logger = Logger.getLogger(SequenceListMouseListener.class);

	private boolean novelSelection;
	private int startIndex = -1;


	public void mouseClicked(MouseEvent e) {
		JList list = (JList) e.getSource();
		int clickindex = list.locationToIndex(e.getPoint());
		
		
	}

	
	public void mousePressed(MouseEvent e) {
		JList list = (JList) e.getSource();
		int clickindex = list.locationToIndex(e.getPoint());
		if(!list.isSelectedIndex(clickindex)){
			list.setDragEnabled(false);
			startIndex = clickindex;
		}else{
			startIndex = -1;
		}	
	}


	public void mouseReleased(MouseEvent e) {
		startIndex = -1;
		
	}


	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseDragged(MouseEvent e) {
		JList list = (JList) e.getSource();
		int clickindex = list.locationToIndex(e.getPoint());
		if(startIndex > -1){
			int endIndex = list.locationToIndex(e.getPoint());
			int min = Math.min(startIndex, endIndex);
			int max = Math.max(startIndex, endIndex);
			int selSize = max-min + 1;
			int[] indices = new int[selSize];
			for(int n = 0; n < indices.length; n++){
				indices[n] = min + n;
			}
			//logger.info
			list.setSelectedIndices(indices);
			
		}
	}


	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
