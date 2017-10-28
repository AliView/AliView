package aliview.color;

import java.awt.Color;

import org.apache.commons.lang.StringUtils;

public class ColorUtils {


	public static int getGolorVal(int r, int g, int b, int a) {
		int rgba = ((a & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF) << 0);
		return rgba;
	}

	public static int grayFromRGB(int rgb) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb) & 0xFF;

		int gray = (r + g + b) / 3;

		return gray;
	}

	public static int darkerRGB(int rgb) {

		//		Color col = new Color(rgb);
		//		Color darker = col.darker().darker();
		//		return darker.getRGB();
		//		
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb) & 0xFF;

		return new Color( (int)(r*0.7) ,(int)(g*0.7),(int)(b*0.7)).getRGB();

		//		int darker =(int)(r * 0.7);
		//		darker = (darker << 8) + (int)(g * 0.7);
		//		darker = (darker << 8) + (int)(b * 0.7);
		//	
		//		return darker;
	}


	public static int addTranspGrey(int inVal, double transp){

		int a = 255;//getAlpha(inVal);
		int r = (int)(getRed(inVal) * transp);
		int g = (int)(getGreen(inVal) * transp);
		int b = (int)(getBlue(inVal) * transp);

		int rgba = ((a & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF) << 0);
		return rgba;
	}


	public static int getRed(int colVal) {
		return (colVal >> 16) & 0xFF;
	}

	/**
	 * Returns the green component in the range 0-255 in the default sRGB
	 * space.
	 * @return the green component.
	 * @see #getRGB
	 */
	public static int getGreen(int colVal) {
		return (colVal >> 8) & 0xFF;
	}

	/**
	 * Returns the blue component in the range 0-255 in the default sRGB
	 * space.
	 * @return the blue component.
	 * @see #getRGB
	 */
	public static int getBlue(int colVal) {
		return (colVal >> 0) & 0xFF;
	}

	/**
	 * Returns the alpha component in the range 0-255.
	 * @return the alpha component.
	 * @see #getRGB
	 */
	public static int getAlpha(int colVal) {
		return (colVal >> 24) & 0xff;
	}

	public static NamedColor parseColor(String text){
		NamedColor color = null;

		if(text.contains("(")){
			color = parseJavaSyntax(text);
		}
		else{
			color = parseClustalSyntax(text);
		}
		return color;
	}

	private static NamedColor parseClustalSyntax(String text) throws NumberFormatException {

		String[] splitted = StringUtils.split(text);
		String name = splitted[0];
		float r = Float.parseFloat(splitted[1]);
		float g = Float.parseFloat(splitted[2]);
		float b = Float.parseFloat(splitted[3]);

		return new NamedColor(name, new Color(r, g, b));

	}

	private static NamedColor parseJavaSyntax(String text) {
		if(text.contains("0x")){

		}
		return null;
	}




}
