package aliview.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

import aliview.sequencelist.AlignmentListModel;
import aliview.sequences.Sequence;

public class NotUsedReorderListener extends MouseAdapter {

	/*

	   private JList list;
	   private int pressIndex = 0;
	   private int releaseIndex = 0;

	   public ReorderListener(JList list) {
	      if (!(list.getModel() instanceof AlignmentListModel)) {
	         throw new IllegalArgumentException("List must have a SequenceListModel");
	      }
	      this.list = list;
	   }


	   public void mousePressed(MouseEvent e) {
	      pressIndex = list.locationToIndex(e.getPoint());
	   }


	   public void mouseReleased(MouseEvent e) {
	      releaseIndex = list.locationToIndex(e.getPoint());
	      if (releaseIndex != pressIndex && releaseIndex != -1) {
	         reorder();
	      }
	   }


	   public void mouseDragged(MouseEvent e) {
	      mouseReleased(e);
	      pressIndex = releaseIndex;      
	   }

	   private void reorder() {
		   AlignmentListModel model = (AlignmentListModel) list.getModel();
	      Sequence dragee = model.get(pressIndex);
	      model.removeAt(pressIndex);
	      model.insertAt(dragee, releaseIndex);
	   }
	 */
}

