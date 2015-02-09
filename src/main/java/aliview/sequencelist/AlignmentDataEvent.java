package aliview.sequencelist;

import java.awt.Rectangle;


public class AlignmentDataEvent extends javax.swing.event.ListDataEvent {

	private Rectangle bounds;

	public AlignmentDataEvent(Object source, int type, int index0, int index1) {
		super(source, type, index0, index1);
	}

	public AlignmentDataEvent(Object source, int type, Rectangle bounds) {
		super(source, type, bounds.y, bounds.y + bounds.height);
		this.bounds = bounds;	
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

}
