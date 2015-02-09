package aliview.sequencelist;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;
/*
class RubberBandingListener extends MouseInputAdapter {
	  @Override public void mouseDragged(MouseEvent e) {
	    setFocusable(true);
	    if(srcPoint==null) srcPoint = e.getPoint();
	    Point destPoint = e.getPoint();
	    polygon.reset();
	    polygon.addPoint(srcPoint.x,  srcPoint.y);
	    polygon.addPoint(destPoint.x, srcPoint.y);
	    polygon.addPoint(destPoint.x, destPoint.y);
	    polygon.addPoint(srcPoint.x,  destPoint.y);
	    //setSelectedIndices(getIntersectsIcons(polygon));
	    if(srcPoint.getX()==destPoint.getX() || srcPoint.getY()==destPoint.getY()) {
	      line.setLine(srcPoint.getX(),srcPoint.getY(),destPoint.getX(),destPoint.getY());
	      setSelectedIndices(getIntersectsIcons(line));
	    }else{
	      setSelectedIndices(getIntersectsIcons(polygon));
	    }
	    repaint();
	  }
	  @Override
	  public void mouseReleased(MouseEvent e) {
	    setFocusable(true);
	    srcPoint = null;
	    repaint();
	  }
	  @Override
	  public void mousePressed(MouseEvent e) {
	    int index = locationToIndex(e.getPoint());
	    Rectangle rect = getCellBounds(index,index);
	    if(!rect.contains(e.getPoint())) {
	      getSelectionModel().setLeadSelectionIndex(getModel().getSize());
	      clearSelection();
	      setFocusable(false);
	    }else{
	      setFocusable(true);
	    }
	  }
	  private int[] getIntersectsIcons(Shape p) {
	    ListModel model = getModel();
	    Vector< Integer > list = new Vector< Integer >(model.getSize());
	    for(int i=0;i < model.getSize();i++) {
	      Rectangle r = getCellBounds(i,i);
	      if(p.intersects(r)) {
	        list.add(i);
	      }
	    }
	    int[] il = new int[list.size()];
	    for(int i=0;i < list.size();i++) {
	      il[i] = list.get(i);
	    }
	    return il;
	  }
	}

*/