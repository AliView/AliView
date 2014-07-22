package aliview.color;

public class ColorUtils {
	
	
	public static int getGolorVal(int r, int g, int b, int a) {
        int rgba = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        return rgba;
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
	    

}
