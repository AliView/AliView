package aliview.sequencelist;

/** 
 * This application demonstrates how one can squeeze some extra
 * performance out of JList. A custom cell renderer is used - which
 * only displays left justified strings - and the list is configured
 * with fixed size cells.  A simple benchmark measures the performance
 * gained with this approach relative to a similarly configured 
 * JList with a default cell renderer.   Tests on Solaris show
 * about 30% improvement.
 * 
 * Tested against swing-1.1, JDK1.1.7.
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.*;

import java.lang.reflect.Method;


/** 
 * A CellRenderer that eliminates any of the overhead that the
 * DefaultListCellRenderer (a JLabel) adds.  Only left justified
 * strings are displayed, and cells have a fixed preferred
 * height and width.   
 */
class NotUsedTextCellRenderer extends JLabel implements ListCellRenderer 
{
    String text;
    final int borderWidth = 1;
    final int width;
    final int height;
    final BufferedImage buffImg = new BufferedImage(200, 20, BufferedImage.TYPE_INT_ARGB);
    

    NotUsedTextCellRenderer(int width, int height, Font font) {
	super();
	this.height = height;
	this.width = width;
	this.setFont(font);
	
    }

    /** 
     * Return the renderers fixed size here.
     */
    public Dimension getPreferredSize() {
	return new Dimension(width, height);
    }

    /**
     * Completely bypass all of the standard JComponent painting machinery.
     * This is a special case: the renderer is guaranteed to be opaque,
     * it has no children, and it's only a child of the JList while
     * it's being used to rubber stamp cells.
     * <p>
     * Clear the background and then draw the text.
     */
    public void paint(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D) g;
    	
//    	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);	
//		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		
		// This need to be off because I use exact font width in createAdjustedDerivedBaseFont
	//	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		}
//		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    	
    //g.setFont(this.getFont());
	//g.setColor(getBackground());
	//g.fillRect(0, 0, getWidth(), getHeight());
	//g.setColor(getForeground());
	g.drawImage(buffImg, 0,0, null);
//	g.drawString(" ", borderWidth, getHeight());
	//g.drawString(text, borderWidth, getHeight());
    }


    /* This is is the ListCellRenderer method.  It just sets
     * the foreground and background properties and updates the
     * local text field.
     */
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) 
    {
	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	    setForeground(list.getSelectionForeground());
	}
	else {
	    setBackground(list.getBackground());
	    setForeground(list.getForeground());
	}
	this.setFont(list.getFont());
	text = value.toString();

	return this;
    }
}