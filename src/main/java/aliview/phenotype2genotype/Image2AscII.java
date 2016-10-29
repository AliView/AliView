package aliview.phenotype2genotype;


	import java.awt.Color;
	import java.awt.image.BufferedImage;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.IOException;

	import javax.imageio.ImageIO;
	import javax.imageio.ImageReader;
	import javax.imageio.stream.ImageInputStream;

	import org.apache.commons.io.FileUtils;
	import org.apache.log4j.Logger;

	import aliview.AminoAcid;

	public class Image2AscII {
		private static final Logger logger = Logger.getLogger(Image2AscII.class);
		
		private static Color[] aminoAcidBackgroundColor;
		private static final String LF = System.getProperty("line.separator");
		
		static{
			
			aminoAcidBackgroundColor = new Color[255];
			aminoAcidBackgroundColor[(byte)'A'] = new Color(0x276eb7);
			aminoAcidBackgroundColor[(byte)'C'] = new Color(0xe68080);
			aminoAcidBackgroundColor[(byte)'D'] = new Color(0xcc4dcc);
			aminoAcidBackgroundColor[(byte)'E'] = new Color(0x984097);
			aminoAcidBackgroundColor[(byte)'F'] = new Color(0x1980e6);
			aminoAcidBackgroundColor[(byte)'G'] = new Color(0xe6994d);
			aminoAcidBackgroundColor[(byte)'H'] = new Color(0x19b3b3);
			aminoAcidBackgroundColor[(byte)'I'] = new Color(0x4ea0f3);
			aminoAcidBackgroundColor[(byte)'K'] = new Color(0xe63319);
			aminoAcidBackgroundColor[(byte)'L'] = new Color(0x78a6d5);
			aminoAcidBackgroundColor[(byte)'M'] = new Color(0x0f549b);
			aminoAcidBackgroundColor[(byte)'N'] = new Color(0x19cc19);
			aminoAcidBackgroundColor[(byte)'P'] = new Color(0xcccc00);
			aminoAcidBackgroundColor[(byte)'Q'] = new Color(0x5ced5c);
			aminoAcidBackgroundColor[(byte)'R'] = new Color(0xf6442c);
			aminoAcidBackgroundColor[(byte)'S'] = new Color(0x029602);
			aminoAcidBackgroundColor[(byte)'T'] = new Color(0x45c945);
			aminoAcidBackgroundColor[(byte)'V'] = new Color(0x047df9);
			aminoAcidBackgroundColor[(byte)'W'] = new Color(0x0355a9);
			aminoAcidBackgroundColor[(byte)'Y'] = new Color(0x14c6c8);
			aminoAcidBackgroundColor[(byte)'x'] = Color.DARK_GRAY;
			aminoAcidBackgroundColor[(byte)'-'] = new Color(230,230,230);
		//	aminoAcidBackgroundColor[AminoAcid'GAP'] = Color'white;
		//	aminoAcidBackgroundColor[(byte)'?'] = Color.white;
			
			/*
			aminoAcidBackgroundColor = new Color[255];
			aminoAcidBackgroundColor[(byte)'A'] = new Color(0x276eb7);
			aminoAcidBackgroundColor[(byte)'C'] = new Color(0xe68080);
			aminoAcidBackgroundColor[(byte)'D'] = new Color(0xcc4dcc);
			aminoAcidBackgroundColor[(byte)'E'] = new Color(0x984097);
			aminoAcidBackgroundColor[(byte)'F'] = new Color(0x1980e6);
			aminoAcidBackgroundColor[(byte)'G'] = new Color(0xe6994d);
			aminoAcidBackgroundColor[(byte)'H'] = new Color(0x19b3b3);
			aminoAcidBackgroundColor[(byte)'I'] = new Color(0x4ea0f3);
			aminoAcidBackgroundColor[(byte)'K'] = new Color(0xe63319);
			aminoAcidBackgroundColor[(byte)'L'] = new Color(0x78a6d5);
			aminoAcidBackgroundColor[(byte)'M'] = new Color(0x0f549b);
			aminoAcidBackgroundColor[(byte)'N'] = new Color(0x19cc19);
			aminoAcidBackgroundColor[(byte)'P'] = new Color(0xcccc00);
			aminoAcidBackgroundColor[(byte)'Q'] = new Color(0x5ced5c);
			aminoAcidBackgroundColor[(byte)'R'] = new Color(0xf6442c);
			aminoAcidBackgroundColor[(byte)'S'] = new Color(0x029602);
			aminoAcidBackgroundColor[(byte)'T'] = new Color(0x45c945);
			aminoAcidBackgroundColor[(byte)'V'] = new Color(0x047df9);
			aminoAcidBackgroundColor[(byte)'W'] = new Color(0x0355a9);
			aminoAcidBackgroundColor[(byte)'Y'] = new Color(0x14c6c8);
		//	aminoAcidBackgroundColor[(byte)'x'] = Color.DARK_GRAY;
//			aminoAcidBackgroundColor[(byte)'-'] = new Color(230,230,230);
			//aminoAcidBackgroundColor[AminoAcid'GAP'] = Color'white;
			aminoAcidBackgroundColor[(byte)'?'] = Color.white;
			*/
		}
		
		public static void main(String[] args) {
			
		//	ImageToAscII.createAscFile(new File("/home/anders/bilder/nude_protein.png"), new File("/home/anders/bilder/nude_protein.fasta"));
			//ImageToAscII.createAscFile(new File("/home/anders/bilder/anders/anders_0795_v5_rot_crop_800.png"), new File("/home/anders/bilder/anders/anders_0795_v5_rot_crop_800.png.fasta"));
			//ImageToAscII.createAscFile(new File("/home/anders/bilder/allison_cat/IMG_1850_low_col2.png"), new File("/home/anders/bilder/allison_cat/IMG_1850_low_col2.fasta"));
			try {
				Image2AscII.createAscFile(new File("/home/anders/projekt/image2asc/IMG_1850.jpeg"), new File("/home/anders/projekt/image2asc/IMG_1850.fasta"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public static void createAscFile(File imageFile, File outputFile) throws IOException{

				BufferedImage image = Image2AscII.readImage(imageFile);
				StringBuilder output = new StringBuilder();
				
				output.append(">protein" + LF + "QQXX" + LF);
				
				for(int y = 0; y < image.getHeight(); y++){
					output.append(">Seq_" + y + LF);
					for(int x = 0; x < image.getWidth(); x++){
						int rgb = image.getRGB(x,y);
						
//						Color closeCol = getClosestColor(new Color(rgb));
//						char charVal = getByteFromRGBColor(closeCol);
						
						char charVal = getAminoAcidFromColor(rgb);
						
						output.append(charVal);
					}
					output.append(LF);
				}
				
				
				FileUtils.writeStringToFile(outputFile, output.toString());
				
		}
		
		private static char getByteFromRGBColor(Color inColor){
			for(int n = 0; n < aminoAcidBackgroundColor.length; n++){
				Color col = aminoAcidBackgroundColor[n];
				if(col != null){
					if(col.equals(inColor)){
						return (char) n;
					}
				}
			}
			return '.';
		}
		
		
		public static BufferedImage readImage(File imageFile) throws IOException{
			FileInputStream in = new FileInputStream(imageFile);
			ImageInputStream iin = ImageIO.createImageInputStream(in);
			ImageReader reader = ImageIO.getImageReaders(iin).next();
			reader.setInput(iin, true, true);
			BufferedImage image = reader.read(0);
			return image;
		}
		
		public static char getAminoAcidFromColor(int rgbColor){

			int maxVal = Color.green.getRGB();
		//	logger.info(new Color(254,254,254).getRGB());
			
		//	logger.info("maxVal" + maxVal);
			int numberOfAA = AminoAcid.GROUP_ALL.length - 1;
			
			int fraction = (maxVal/numberOfAA);
		//	logger.info("fraction" + fraction);
			
			int aaVal = (rgbColor / fraction);
			
			
			return AminoAcid.GROUP_ALL[aaVal].getCodeCharVal();
			
			
		}
		
		
		public static Color getClosestColor(Color target){

			//Color[] constantColors = new Color[] { Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.yellow };
			Color nearestColor = null;
			double nearestDistance = Double.MAX_VALUE;
		
			for(int n = 0; n < aminoAcidBackgroundColor.length; n++){
				Color constantColor = aminoAcidBackgroundColor[n];
				if(constantColor != null){
					
		//			logger.info("compare " + target + " with " + constantColor );
					
					double testDistance = Math.pow(target.getRed() - constantColor.getRed(), 2) + 
								          Math.pow(target.getGreen() - constantColor.getGreen(), 2) +
								          Math.pow(target.getBlue() - constantColor.getBlue(), 2);
					 
					
				    if(testDistance < nearestDistance){
				    	nearestDistance = testDistance;
//				    	logger.info("new closest " + constantColor + " old " + nearestColor );
				        nearestColor = constantColor;
				    }
				}
			}
//			logger.info("result nearest " + nearestColor + " target " + target );
			return nearestColor;
		}

	}
