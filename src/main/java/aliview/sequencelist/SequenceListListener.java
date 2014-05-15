package aliview.sequencelist;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.alignment.Alignment;
import aliview.gui.AlignmentPane;

public class SequenceListListener implements ListDataListener, ListSelectionListener{

	private Logger logger = Logger.getLogger(SequenceListListener.class);
	private AlignmentPane alignmentPane;
	private AliViewWindow aliViewWindow;

	public SequenceListListener(AlignmentPane aliPane, AliViewWindow aliWindow) {
		this.alignmentPane = aliPane;
		this.aliViewWindow = aliWindow;
	}
	
	/*
	 * 
	 * 
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void contentsChanged(ListDataEvent e) {
//		logger.info("source:" + e.getSource());
		if(e.getSource() instanceof FileSequenceListModel){
			aliViewWindow.fileSequencesChanged();
		}
		logger.info("contentsChanged");
		alignmentPane.validateSize();
		alignmentPane.validateSequenceOrder();
		alignmentPane.repaint();
	}

	public void intervalAdded(ListDataEvent e) {
//		logger.info("intervalAdded");
		alignmentPane.validateSize();
		alignmentPane.validateSequenceOrder();
		alignmentPane.repaint();
	
	}

	public void intervalRemoved(ListDataEvent e) {
        logger.info("intervalRemoved");
		alignmentPane.validateSize();
		alignmentPane.validateSequenceOrder();
		alignmentPane.repaint();
	}  	

	public void valueChanged(ListSelectionEvent evt){
		
		// When the user release the mouse button and completes the selection,
		// getValueIsAdjusting() becomes false
		
//		logger.info("valueChanged");
//		logger.info("evt.getValueIsAdjusting()" + evt.getValueIsAdjusting());
		
		if (!evt.getValueIsAdjusting()) {
			logger.info("!evt.getValueIsAdjusting()");
			if(evt.getSource() instanceof SequenceJList){
				SequenceJList srcList = (SequenceJList) evt.getSource();
				int[] selectedIndex = srcList.getSelectedIndices();
				alignmentPane.getAlignment().clearSelection();
				alignmentPane.getAlignment().selectSequencesWithIndex(selectedIndex);
			}
			
			alignmentPane.validateSize();
			alignmentPane.repaint();
		}
		
		
	}
}
