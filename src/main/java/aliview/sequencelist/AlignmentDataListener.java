package aliview.sequencelist;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public interface AlignmentDataListener extends ListDataListener{

	// ListDataListener
	public void contentsChanged(ListDataEvent e);

	// ListDataListener
	public void intervalAdded(ListDataEvent e);

	// ListDataListener
	public void intervalRemoved(ListDataEvent e);

}
