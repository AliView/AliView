package aliview.sequencelist;

import java.util.EventListener;

	public interface AlignmentSelectionListener extends EventListener
	{
	  /**
	   * Called whenever the value of the selection changes.
	   * @param e the event that characterizes the change.
	   */
	  void selectionChanged(AlignmentSelectionEvent e);
	}

