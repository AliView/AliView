package aliview.sequencelist;


import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import aliview.alignment.Alignment;

import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import java.io.Serializable;
import java.util.Map;



/**
 *
 *  Extended version of the DefaultCellRenderer
 *
 */
public class FasterTextCellRenderer extends JLabel
    implements ListCellRenderer, Serializable
{
    
	private static final Logger logger = Logger.getLogger(FasterTextCellRenderer.class);
	
   /**
    * An empty <code>Border</code>. This field might not be used. To change the
    * <code>Border</code> used by this renderer override the 
    * <code>getListCellRendererComponent</code> method and set the border
    * of the returned component directly.
    */
    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
	private String text;
    //private static BufferedImage buffImg;
	private boolean selected;
    
    private static final int LEFT_OFFSET = 2;
    
    
    /**
     * Constructs a default renderer object for an item
     * in a list.
     */
    public FasterTextCellRenderer(){
    		super();
    		/*
    		if(buffImg == null){
    			createStaticBuffImg();
    		}*/

    		setBorder(getNoFocusBorder());
    		setName("List.cellRenderer");
    }
    
    /*
    private void createStaticBuffImg() {
    	buffImg = new BufferedImage(200, 12, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = buffImg.createGraphics();
		
		g2.setColor(Color.green);
		g2.fillRect(0, 0, 100, 10);
		g2.setColor(Color.black);
		g2.setFont(new Font("Monospace", Font.PLAIN, 11));
		g2.drawString("BuffImgGraphics", 2, 10);
		g2.dispose();
	}
	*/
    
    private Border getNoFocusBorder() {
    	
    	return DEFAULT_NO_FOCUS_BORDER;
    	/*
        Border border = DefaultLookup.getBorder(this, ui, "List.cellNoFocusBorder");
        if (System.getSecurityManager() != null) {
            if (border != null) return border;
            return SAFE_NO_FOCUS_BORDER;
        } else {
            if (border != null &&
                    (noFocusBorder == null ||
                    noFocusBorder == DEFAULT_NO_FOCUS_BORDER)) {
                return border;
            }
            return noFocusBorder;
        }
        */
    }

    /** 
     * Return the renderers fixed size here.  
     */
    
    public Dimension getPreferredSize() {
    	return new Dimension(10, 5);
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
    	
	//	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	//	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	
    	
    	if(this.getHeight() > 8){
    		setPlatformDefaultRenderingHints(g2);
    	}
    	else{
    		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    	}

    // if not selected - default transp background
	if(selected){
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
	}
	
	g2.setFont(this.getFont());
	g2.setColor(getForeground());
//	logger.info(buffImg);
//	g2.drawImage(buffImg, 0,0, null);
	
// 	SwingUtilities2.drawString(this, g, text, 2, this.getHeight() - bottomCharOffset);
	
	int bottomCharOffset = (int)(0.2 * this.getHeight());
	g2.drawString(text, LEFT_OFFSET, getHeight() - bottomCharOffset);
	
    }


    private void setPlatformDefaultRenderingHints(Graphics2D g2){
    	try{
	    	Map pdefaults =	(Map)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
	    	Object aaHint  = pdefaults.get(RenderingHints.KEY_TEXT_ANTIALIASING);
	    	if(aaHint != null){
	    		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
	    	}else{
	    		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    	}
	    	Object lcdHint  = pdefaults.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
	    	if(lcdHint != null){
	    		g2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, lcdHint);
	    	}
	    	
    	}catch(Exception exc){
    		exc.printStackTrace();
    	}	
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
	this.selected = isSelected;
	this.setFont(list.getFont());
	this.text = value.toString();

	return this;
    }
    
    public String getText() {
    	return this.text;
    }

    public void setText(String text) {
    	
        String oldAccessibleName = null;
        if (accessibleContext != null) {
            oldAccessibleName = accessibleContext.getAccessibleName();
        }

        String oldValue = this.text;
        this.text = text;
        firePropertyChange("text", oldValue, text);

//        setDisplayedMnemonicIndex(
//                      SwingUtilities.findDisplayedMnemonicIndex(
//                                          text, getDisplayedMnemonic()));

        if ((accessibleContext != null) 
            && (accessibleContext.getAccessibleName() != oldAccessibleName)) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                        oldAccessibleName,
                        accessibleContext.getAccessibleName());
        }
        if (text == null || oldValue == null || !text.equals(oldValue)) {
            revalidate();
            repaint();
        }
    }

	/**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     *
     * @since 1.5
     * @return <code>true</code> if the background is completely opaque
     *         and differs from the JList's background;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean isOpaque() {
    	return selected;
  
    /*
    	
	Color back = getBackground();
	Component p = getParent(); 
	if (p != null) { 
	    p = p.getParent(); 
	}
	// p should now be the JList. 
	boolean colorMatch = (back != null) && (p != null) && 
	    back.equals(p.getBackground()) && 
			p.isOpaque();
	return !colorMatch && super.isOpaque(); 
    }
	*/
    	
    }

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void validate() {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    *
    * @since 1.5
    */
    @Override
    public void invalidate() {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    *
    * @since 1.5
    */
    @Override
    public void repaint() {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void revalidate() {}
   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void repaint(Rectangle r) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	// Strings get interned...
	if (propertyName == "text"
                || ((propertyName == "font" || propertyName == "foreground")
                    && oldValue != newValue
                    && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

	    super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

}
