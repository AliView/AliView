package aliview.sequencelist;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;

public class MyDropTarget extends DropTarget {
	
	private static final Logger logger = Logger.getLogger(SequenceJList.class);

	
	public MyDropTarget() {
		super();
		logger.info("Create MyDT");
	}
	
	protected void initializeAutoscrolling(Point p) {
		
		logger.info("this.getComponent() instanceof Autoscroll" + (this.getComponent() instanceof Autoscroll));
		System.out.println("init Autoscrolling");
		//autoScroller = createDropTargetAutoScroller(component, p);
		super.initializeAutoscrolling(p);
		/*
	        if (component == null || !(component instanceof Autoscroll)) return;

	        autoScroller = createDropTargetAutoScroller(component, p);
	     */

    }
	
	
	protected DropTargetAutoScroller createDropTargetAutoScroller( Component c, Point p) {
		logger.info("Create MyDT Autoscroll");
		return new MyDropTargetAutoScroller(c, p);
	}


	protected static class MyDropTargetAutoScroller extends DropTargetAutoScroller {

		protected MyDropTargetAutoScroller(Component c, Point p) {
			super(c, p);
			super.stop();
			
			
			
			System.out.println("Created Autoscroller");

			component  = c;
			autoScroll = (Autoscroll)component;

			java.awt.Toolkit t  = java.awt.Toolkit.getDefaultToolkit();

			Integer    initial  = new Integer(100);
			Integer    interval = new Integer(100);

			try {
				initial = (Integer)t.getDesktopProperty("DnD.Autoscroll.initialDelay");
			} catch (Exception e) {
				// ignore
			}

			try {
				interval =
						(Integer)t.getDesktopProperty("DnD.Autoscroll.interval");
			} catch (Exception e) {
				// ignore
			}

			timer  = new Timer(interval.intValue(), this);

			timer.setCoalesce(true);
			timer.setInitialDelay(initial.intValue());

			locn = p;
			prev = p;
			screenLocation = new Point(p);

			SwingUtilities.convertPointToScreen(screenLocation,
					c);

			try {
				hysteresis =
						((Integer)t.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
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

			if (size.width != outer.width || size.height !=
					outer.height)
				outer.reshape(0, 0, size.width, size.height);

			if (inner.x != i.left || inner.y != i.top)
				inner.setLocation(i.left, i.top);

			int newWidth  = size.width -  (i.left + i.right);
			int newHeight = size.height - (i.top  + i.bottom);

			if (newWidth != inner.width || newHeight !=
					inner.height)
				inner.setSize(newWidth, newHeight);

		}

		/**
		 * cause autoscroll to occur
		 * <P>
		 * @param newLocn the <code>Point</code>
		 */
		protected synchronized void updateLocation(Point newLocn)
		{

			prev = locn;
			locn = newLocn;

			screenLocation = new Point(locn);
			SwingUtilities.convertPointToScreen(screenLocation,
					component);

			if (Math.abs(locn.x - prev.x) > hysteresis ||
					Math.abs(locn.y - prev.y) > hysteresis) {
				if (timer.isRunning()) {

					timer.stop();
				}
			} else {
				if (!timer.isRunning()) {

					timer.start();
				}
			};
		}

		/**
		 * cause autoscrolling to stop
		 */

		protected void stop() { 

			timer.stop(); 
		}

		/**
		 * cause autoscroll to occur
		 * <P>
		 * @param e the <code>ActionEvent</code>
		 */

		public synchronized void actionPerformed(ActionEvent e) {

			//System.out.println("autoscrolling actionPerformed");
			// updateRegion();
			
			Point componentLocation = new Point(screenLocation);
			SwingUtilities.convertPointFromScreen(componentLocation, component);
			autoScroll.autoscroll(componentLocation);

			/*
			if (outer.contains(componentLocation) &&
					!inner.contains(componentLocation)) {


				autoScroll.autoscroll(componentLocation);
			}
			*/
			


		}

		/*
		 * fields
		 */
		private Window window;
		private Canvas canvas;

		private Component  component;
		private Autoscroll autoScroll;

		private Timer      timer;

		private Point	   locn;
		private Point	   prev;
		private Point screenLocation;

		private Rectangle  outer = new Rectangle();
		private Rectangle  inner = new Rectangle();

		private int	   hysteresis = 10;
	}
};
