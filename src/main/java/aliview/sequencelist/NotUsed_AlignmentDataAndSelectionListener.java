package aliview.sequencelist;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.alignment.Alignment;
import aliview.gui.pane.AlignmentPane;

public class NotUsed_AlignmentDataAndSelectionListener implements AlignmentSelectionListener, AlignmentDataListener{

	private Logger logger = Logger.getLogger(NotUsed_AlignmentDataAndSelectionListener.class);
	private AlignmentPane alignmentPane;
	private AliViewWindow aliViewWindow;
	private SequenceJList aliList;

	public NotUsed_AlignmentDataAndSelectionListener(AlignmentPane aliPane, AliViewWindow aliWindow, SequenceJList aliList) {
		this.alignmentPane = aliPane;
		this.aliViewWindow = aliWindow;
		this.aliList = aliList;
	}

	public void intervalAdded(ListDataEvent e) {
		logger.info("intervalAdded");
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}

	public void intervalRemoved(ListDataEvent e) {
		logger.info("intervalRemoved");
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}  	

	public void contentsChanged(ListDataEvent e) {
		logger.info("contentsChanged");
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}

	public void contentsChanged(AlignmentDataEvent e) {
		logger.info("contentsChanged");
		if(e.getSource() instanceof FileSequenceAlignmentListModel){
			//	aliViewWindow.fileSequencesChanged();
		}

		Rectangle grown = new Rectangle(e.getBounds().x - 3, e.getBounds().y - 1, e.getBounds().width + 6, e.getBounds().height + 2);
		Rectangle paneBounds = alignmentPane.matrixCoordToPaneCoord(grown);

		alignmentPane.validateSize();
		alignmentPane.validateSequenceOrder();

		//alignmentPane.paintImmediately(paneBounds);
		//aliList.paintImmediately(aliList.getVisibleRect());
		//alignmentPane.scrollRectToVisible(paneBounds);
		//logger.info("paneBounds" + paneBounds);
		alignmentPane.repaint(paneBounds);

		//alignmentPane.repaint();
		//aliList.repaint();

		Rectangle visiRect = aliList.getVisibleRect();
		Rectangle drawListBounds = new Rectangle(visiRect.x,paneBounds.y, visiRect.width, paneBounds.height);
		aliList.scrollRectToVisible(drawListBounds);
		aliList.repaint(visiRect.x,paneBounds.y, visiRect.width, paneBounds.height);

	}

	public void selectionChanged(AlignmentSelectionEvent e) {
		logger.info("selectionChanged");
		//alignmentPane.paintImmediately(0, 0, alignmentPane.getWidth(), alignmentPane.getHeight());
		Rectangle grown = new Rectangle(e.getBounds().x - 3, e.getBounds().y - 1, e.getBounds().width + 6, e.getBounds().height + 2);
		Rectangle paneBounds = alignmentPane.matrixCoordToPaneCoord(grown);

		alignmentPane.repaint(paneBounds);

		Rectangle visiRect = aliList.getVisibleRect();
		Rectangle drawListBounds = new Rectangle(visiRect.x,paneBounds.y, visiRect.width, paneBounds.height);
		logger.info("drawListBounds" + drawListBounds);
		aliList.repaint(drawListBounds);

		//logger.info("paneBounds" + paneBounds);

	}

}
