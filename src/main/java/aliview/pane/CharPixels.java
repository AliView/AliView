package aliview.pane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import org.apache.log4j.Logger;


public class CharPixels {
	private static final Logger logger = Logger.getLogger(CharPixels.class);

	public static final int CASE_UNTOUCHED = 0;
	public static final int CASE_UPPER = 1;
	public static final int CASE_LOWER = 2;

	private char ch;
	private Color color;
	// RGB array has to be volatile so no problems araise with the double lock in getPixels(),
	// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
	private volatile RGBArray rgbArray;
	private int width;
	private int height;
	private Graphics2D g2;
	private Color bgColor;
	private Color fgColor;
	private Font font;
	private int minFontSize;
	private int fontCase;

	public CharPixels(char ch, int width, int height, Color fgColor, Color bgColor, Font font, int minFontSize, int fontCase){

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
		this.fontCase = fontCase;
	}

	public int[] getPixels() {
		return getRGBArray().getBackend();
	}

	public RGBArray getRGBArray(){
		if(rgbArray == null){
			// this is double locked to avoid synchronized block after the lazy initialization of RGBArray object
			// RGBArray has to be declared volatile above
			// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html and http://en.wikipedia.org/wiki/Double-checked_locking
			synchronized(this){
				// double check if rgbArray has been created while thread was waiting
				if(rgbArray == null){
					BufferedImage bi = createPixelImageBufferedImage();
					int[] origData = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
					// copy array
					int[] pixCopy = Arrays.copyOf(origData, origData.length);
					rgbArray = new RGBArray(pixCopy, width, height);
				}
			}
		}
		return rgbArray;	
	}


	private BufferedImage createPixelImageBufferedImage(){

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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


		g2.setColor(bgColor);
		g2.fillRect(0, 0, width, height);

		// Only draw if font is a good size
		if(width > minFontSize){
			g2.setColor(fgColor);	
			int leftCharOffset = (int)(0.15 * width);
			int bottomCharOffset = (int)(0.2 * height);

			char displayChar = ch;
			if(fontCase == CASE_UPPER){
				displayChar = Character.toUpperCase(ch);
			}else if(fontCase == CASE_LOWER){
				displayChar = Character.toLowerCase(ch);
			}

			g2.drawString("" + displayChar, leftCharOffset, height - bottomCharOffset);
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
