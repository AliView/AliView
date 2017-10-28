package aliview.test;

//-*- mode:java; encoding:utf-8 -*-
//vim:set fileencoding=utf-8:
//http://ateraimemo.com/Swing/DragSelectDropReordering.html
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.activation.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

import org.apache.log4j.Logger;

import aliview.sequencelist.SequenceListSelectionModel;
public class RubberBandingListener extends MouseAdapter {
	private static final Logger logger = Logger.getLogger(RubberBandingListener.class);
	private Point srcPoint;
	private final Polygon polygon = new Polygon();

	@Override
	public void mouseDragged(MouseEvent e) {
		JList list = (JList) e.getComponent();
		if (list.getDragEnabled()) {
			return;
		}
		if (srcPoint == null) {
			srcPoint = e.getPoint();
		}
		Point destPoint = e.getPoint();
		polygon.reset();
		polygon.addPoint(srcPoint.x,  srcPoint.y);
		polygon.addPoint(destPoint.x, srcPoint.y);
		polygon.addPoint(destPoint.x, destPoint.y);
		polygon.addPoint(srcPoint.x,  destPoint.y);
		//list.setSelectedIndices(list.getIntersectsIcons(polygon));
		list.repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		JList list = (JList) e.getComponent();
		list.setFocusable(true);
		if (srcPoint == null || !list.getDragEnabled()) {
			Component glassPane = list.getRootPane().getGlassPane();
			//glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
		}
		srcPoint = null;
		list.setDragEnabled(list.getSelectedIndices().length > 0);
		list.repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		logger.info("mousePressed");
		JList list = (JList) e.getComponent();
		int index = list.locationToIndex(e.getPoint());
		Rectangle rect = list.getCellBounds(index, index);
		if (rect.contains(e.getPoint())) {
			logger.info("rect.contains(e.getPoint()");
			list.setFocusable(true);
			if (list.getDragEnabled()) {
				return;
			} else {
				System.out.println("ccc:");
				list.setSelectedIndex(index);
			}
		} else {
			Component glassPane = list.getRootPane().getGlassPane();
			//glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);

			list.clearSelection();
			list.getSelectionModel().setAnchorSelectionIndex(-1);
			list.getSelectionModel().setLeadSelectionIndex(-1);
			list.setFocusable(false);
			list.setDragEnabled(false);
		}
		list.repaint();
	}
	/*
    private int[] getIntersectsIcons(Shape p) {
    	JList list = (JList) e.getComponent();
        ListModel model = list.getModel();
        List<Integer> intList = new ArrayList<Integer>(model.getSize());
        for (int i = 0; i < model.getSize(); i++) {
            Rectangle r = list.getCellBounds(i, i);
            if (p.intersects(r)) {
            	intList.add(i);
            }
        }
        int[] il = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            il[i] = intList.get(i);
        }
        return il;
    }
	 */
}