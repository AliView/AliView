package aliview.sequencelist;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
public class NotUsedSequenceListSelectionListener implements ListSelectionListener{
	private static final Logger logger = Logger.getLogger(NotUsedSequenceListSelectionListener.class);


	public void valueChanged(ListSelectionEvent e) {

		// Skip if is adjusting
		if(e.getValueIsAdjusting() == true){
			return;
		}

	}

}
