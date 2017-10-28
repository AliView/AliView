package aliview.sequencelist;

import java.awt.Rectangle;

import javax.swing.event.ListSelectionEvent;

public class AlignmentSelectionEvent extends ListSelectionEvent {

	private Rectangle bounds;

	/*
	public AlignmentSelectionEvent(Object source, int firstIndex, int lastIndex, boolean isAdjusting) {
		this(source, new Rectangle(0, Math.min(firstIndex, lastIndex), 0, Math.abs(firstIndex - lastIndex)), isAdjusting);
	}
	 */
	public AlignmentSelectionEvent(Object source, Rectangle bounds, boolean isAdjusting) {
		super(source, bounds.y, bounds.y + bounds.height, isAdjusting);
		this.bounds = bounds;
	}

	/*
	public int getFirstResiduePosition() {
		return return;
	}

	public int getLastResiduePosition() {
		return lastResiduePosition;
	}
	 */

	public Rectangle getBounds() {
		return bounds;
	}

}
