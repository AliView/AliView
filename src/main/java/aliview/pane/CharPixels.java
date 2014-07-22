package aliview.pane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import org.apache.log4j.Logger;

import aliview.gui.AlignmentPane;


public class CharPixels {
	private static final Logger logger = Logger.getLogger(CharPixels.class);
	private char ch;
	private Color color;
	private RGBArray rgbArray;
	private int width;
	private int height;
	private BufferedImage bi;
	private Graphics2D g2;
	private Color bgColor;
	private Color fgColor;
	private Font font;
	private int minFontSize;

	public CharPixels(char ch, int width, int height, Color fgColor, Color bgColor, Font font, int minFontSize){
		
		if(width < 1){
			width = 1;
		}
		
		if(height < 1){
			height = 1;
		}
		
		this.ch = ch;
		this.width = width;
		this.height = height;
		this.font = font;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.minFontSize = minFontSize;
	}
	
	public int[] getPixels() {
		return getRGBArray().getBackend();
	}
	
	public RGBArray getRGBArray(){
		if(rgbArray == null){
			BufferedImage bi = createPixelImageFromStaticBufferedImage();
			int[] origData = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
			// copy array
			int[] pixCopy = Arrays.copyOf(origData, origData.length);
			rgbArray = new RGBArray(pixCopy, width, height);
		}
		return rgbArray;	
	}
	
	
	private BufferedImage createPixelImageFromStaticBufferedImage() {
		// A static image is used instead of creating one for each char
		// BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		// Graphics2D big2 = bi.createGraphics();
		
		if(bi == null){
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			g2 = bi.createGraphics();
			g2.setFont(font);
			
	//		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);	
	//		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			
			// This need to be off because I use exact font width in createAdjustedDerivedBaseFont
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			if(width < 10){
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
//			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		}
		
		
		
		g2.setColor(bgColor);
		g2.fillRect(0, 0, width, height);
		
		// Only draw if font is a good size
		if(width > minFontSize){
			g2.setColor(fgColor);	
			int leftCharOffset = (int)(0.15 * width);
			int bottomCharOffset = (int)(0.2 * height);
			g2.drawString("" + ch, leftCharOffset, height - bottomCharOffset);
		}
		
		
//		int[] origData = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();	
//		logger.info("color" + g2.getColor());		
//		logger.info("beforeA=" + origData[0]);		
//		g2.setColor(new Color(0,0,0,140));
//		g2.fillRect(0, 0, width, height);		
//		origData = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
//		logger.info("after_A=" + origData[0]);
		
		// g2.dispose();
		return bi;
	}
	
}
