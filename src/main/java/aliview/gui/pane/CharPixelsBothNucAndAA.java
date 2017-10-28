package aliview.gui.pane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import org.apache.log4j.Logger;


public class CharPixelsBothNucAndAA extends CharPixels{
	private static final Logger logger = Logger.getLogger(CharPixelsBothNucAndAA.class);

	public static final int CASE_UNTOUCHED = 0;
	public static final int CASE_UPPER = 1;
	public static final int CASE_LOWER = 2;

	private Color color;
	private RGBArray rgbArray;
	private int width;
	private int height;
	private BufferedImage bi;
	private Graphics2D g2;

	private Font font;
	private int minFontSize;
	private int fontCase;
	private Color nucFgColor;
	private Color nucBgColor;
	private Color aaBgColor;
	private Color aaFgColor;
	private char chAA;
	private char chNuc;

	public CharPixelsBothNucAndAA(char chNuc, char chAA, int width, int height, Color nucFgColor, Color nucBgColor, Color aaFgColor, Color aaBgColor, Font font, int minFontSize, int fontCase){
		super(chAA, width, height, aaFgColor, aaBgColor, font, minFontSize, fontCase);

		if(width < 1){
			width = 1;
		}

		if(height < 1){
			height = 1;
		}

		this.chAA = chAA;
		this.chNuc = chNuc;
		this.width = width;
		this.height = height;
		this.font = font;
		this.nucFgColor = nucFgColor;
		this.nucBgColor = nucBgColor;
		this.aaFgColor = aaFgColor;
		this.aaBgColor = aaBgColor;
		this.minFontSize = minFontSize;
		this.fontCase = fontCase;
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



		g2.setColor(aaBgColor);
		g2.fillRect(0, 0, width, height/2);

		// Only draw if font is a good size
		if(width > minFontSize){
			g2.setColor(aaFgColor);	
			int leftCharOffset = (int)(0.15 * width);
			int bottomCharOffset = (int)(0.2 * height/2);

			char displayChar = chAA;
			if(fontCase == CASE_UPPER){
				displayChar = Character.toUpperCase(chAA);
			}else if(fontCase == CASE_LOWER){
				displayChar = Character.toLowerCase(chAA);
			}

			g2.drawString("" + displayChar, leftCharOffset, height/2 - bottomCharOffset);
		}

		g2.setColor(nucBgColor);
		g2.fillRect(0, 0 + height/2, width, height/2);

		// Only draw if font is a good size
		if(width > minFontSize){
			g2.setColor(nucFgColor);	
			int leftCharOffset = (int)(0.15 * width);
			int bottomCharOffset = (int)(0.2 * height/2);

			char displayChar = chNuc;
			if(fontCase == CASE_UPPER){
				displayChar = Character.toUpperCase(chNuc);
			}else if(fontCase == CASE_LOWER){
				displayChar = Character.toLowerCase(chNuc);
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
