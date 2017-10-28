package aliview.sequencelist;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class MyOtherDropTarget extends DropTarget {


	/**
	 * this protected nested class implements autoscrolling
	 */

	protected static class MyOtherDropTargetAutoScroller extends DropTargetAutoScroller {

		/**
		 * construct a DropTargetAutoScroller
		 * <P>
		 * @param c the <code>Component</code>
		 * @param p the <code>Point</code>
		 */

		protected MyOtherDropTargetAutoScroller(Component c, Point p) {
			super(c,p);

			component  = c;
			autoScroll = (Autoscroll)component;

			Toolkit t  = Toolkit.getDefaultToolkit();

			Integer    initial  = Integer.valueOf(100);
			Integer    interval = Integer.valueOf(100);

			try {
				initial = (Integer)t.getDesktopProperty("DnD.Autoscroll.initialDelay");
			} catch (Exception e) {
				// ignore
			}

			try {
				interval = (Integer)t.getDesktopProperty("DnD.Autoscroll.interval");
			} catch (Exception e) {
				// ignore
			}

			timer  = new Timer(interval.intValue(), this);

			timer.setCoalesce(true);
			timer.setInitialDelay(initial.intValue());

			locn = p;
			prev = p;

			try {
				hysteresis = ((Integer)t.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
			} catch (Exception e) {
				// ignore
			}

			timer.start();
		}

		/**
		 * update the geometry of the autoscroll region
		 */

		private void updateRegion() {
			Insets    i    = autoScroll.getAutoscrollInsets();
			Dimension size = component.getSize();

			if (size.width != outer.width || size.height != outer.height)
				outer.reshape(0, 0, size.width, size.height);

			if (inner.x != i.left || inner.y != i.top)
				inner.setLocation(i.left, i.top);

			int newWidth  = size.width -  (i.left + i.right);
			int newHeight = size.height - (i.top  + i.bottom);

			if (newWidth != inner.width || newHeight != inner.height)
				inner.setSize(newWidth, newHeight);

		}

		/**
		 * cause autoscroll to occur
		 * <P>
		 * @param newLocn the <code>Point</code>
		 */

		protected synchronized void updateLocation(Point newLocn) {
			prev = locn;
			locn = newLocn;

			if (Math.abs(locn.x - prev.x) > hysteresis ||
					Math.abs(locn.y - prev.y) > hysteresis) {
				if (timer.isRunning()) timer.stop();
			} else {
				if (!timer.isRunning()) timer.start();
			}
		}

		/**
		 * cause autoscrolling to stop
		 */

		protected void stop() { timer.stop(); }

		/**
		 * cause autoscroll to occur
		 * <P>
		 * @param e the <code>ActionEvent</code>
		 */

		public synchronized void actionPerformed(ActionEvent e) {
			updateRegion();

			if (outer.contains(locn) && !inner.contains(locn))
				autoScroll.autoscroll(locn);
		}

		/*
		 * fields
		 */

		private Component  component;
		private Autoscroll autoScroll;

		private Timer      timer;

		private Point      locn;
		private Point      prev;

		private Rectangle  outer = new Rectangle();
		private Rectangle  inner = new Rectangle();

		private int        hysteresis = 10;
	}

	/*********************************************************************/

	/**
	 * create an embedded autoscroller
	 * <P>
	 * @param c the <code>Component</code>
	 * @param p the <code>Point</code>
	 */

	@Override
	protected DropTargetAutoScroller createDropTargetAutoScroller(Component c,
			Point p) {
		// TODO Auto-generated method stub
		return new MyOtherDropTargetAutoScroller(c, p);
	}

}
