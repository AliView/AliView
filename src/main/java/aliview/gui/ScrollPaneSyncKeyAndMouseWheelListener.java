package aliview.gui;

import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicSplitPaneUI.BasicVerticalLayoutManager;

import aliview.AliViewWindow;
import utils.OSNativeUtils;

public class ScrollPaneSyncKeyAndMouseWheelListener implements MouseWheelListener, MouseMotionListener, MouseListener, KeyListener{

	private JScrollPane source;
	private JScrollPane dest;
	private AliViewWindow aliViewWindow;

	public ScrollPaneSyncKeyAndMouseWheelListener(JScrollPane source, JScrollPane dest){ 
		this.source = source;
		this.dest = dest;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		syncViewports();

	}

	public void keyTyped(KeyEvent e) {
		syncViewports();
	}

	public void keyPressed(KeyEvent e) {
		syncViewports();
	}

	public void keyReleased(KeyEvent e) {
		syncViewports();

	}

	public void mouseDragged(MouseEvent e) {
		syncViewports();

	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {
		//	syncViewports();

	}

	public void mousePressed(MouseEvent e) {
		syncViewports();

	}

	public void mouseReleased(MouseEvent e) {
		syncViewports();
	}

	private void syncViewports() {
		// keep x position on the dest viewport - only adjust y
		Point viewPos = new Point( dest.getViewport().getViewPosition().x, source.getViewport().getViewPosition().y );
		dest.getViewport().setViewPosition(viewPos);
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}



}
