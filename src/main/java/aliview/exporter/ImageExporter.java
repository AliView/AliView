package aliview.exporter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageExporter {

	public static void writeComponentAsImageToFile(File outFile, String fileFormat, Component comp) throws IOException{
		// Write to file
		ImageIO.write(getBufferedImageFromComponent(comp), fileFormat, outFile);
	}

	
	/**
	 * 
	 */
	private static synchronized BufferedImage getBufferedImageFromComponent(Component comp) {

		// Create a buffered image
		BufferedImage image = new BufferedImage(comp.getWidth(), comp.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = (Graphics2D) image.getGraphics();

		// First draw a background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, comp.getWidth(), comp.getHeight());

		//Then draw component
		comp.paint(g2);

		return image;
	}

}
