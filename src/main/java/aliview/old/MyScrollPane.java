package aliview.old;

import javax.swing.JScrollPane;

import aliview.gui.AlignmentPane;

public class MyScrollPane extends JScrollPane {
//
//	
//	private static final Logger logger = Logger.getLogger(MyScrollPane.class);
//	private Point viewPoint;
//	private boolean ensurePoint;
	
	public MyScrollPane(AlignmentPane alignmentPane,
			int verticalScrollbarAsNeeded, int horizontalScrollbarAlways) {
		super(alignmentPane,verticalScrollbarAsNeeded,horizontalScrollbarAlways);
	}
//
//	@Override
//	protected void paintChildren(Graphics g) {
//		logger.info("paintChildren");
//		if(ensurePoint && viewPoint != null){
//			this.getViewport().setViewPosition(viewPoint);
//		}
//		super.paintChildren(g);
//		ensurePoint = false;
//	}
//	/*
//	@Override
//	protected void paintComponent(Graphics g) {
//		// TODO Auto-generated method stub
//		logger.info("paintComponent");
//		if(ensurePoint && viewPoint != null){
//			this.getViewport().setViewPosition(viewPoint);
//		}
//		super.paintComponent(g);
//		ensurePoint = false;
//	}
//	*/
//
//	public void ensureViewPoint(Point newViewPoint) {
//		this.ensurePoint = true;
//		this.viewPoint = newViewPoint;
//		
//	}
//	
}

