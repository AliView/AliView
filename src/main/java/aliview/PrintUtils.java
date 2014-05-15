package aliview;

import java.awt.Dimension;

public class PrintUtils {
	// The number of CMs per Inch
		public static final double CM_PER_INCH = 0.393700787d;
		// The number of Inches per CMs
		public static final double INCH_PER_CM = 2.545d;
		// The number of Inches per mm's
		public static final double INCH_PER_MM = 25.45d;

		/**
		 * Converts the given pixels to cm's based on the supplied DPI
		 * @param pixels
		 * @param dpi
		 * @return 
		 */
		public static double pixelsToCms(double pixels, double dpi) {
		    return inchesToCms(pixels / dpi);
		}

		/**
		 * Converts the given cm's to pixels based on the supplied DPI
		 * @param cms
		 * @param dpi
		 * @return 
		 */
		public static double cmsToPixel(double cms, double dpi) {
		    return cmToInches(cms) * dpi;
		}
		
		public static double mmToPixel(double mm, double dpi) {
		    return cmToInches(mm * 10) * dpi;
		}
		
		/**
		 * Converts the given cm's to inches
		 * @param cms
		 * @return 
		 */
		public static double cmToInches(double cms) {
		    return cms * CM_PER_INCH;
		}

		/**
		 * Converts the given inches to cm's 
		 * @param inch
		 * @return 
		 */
		public static double inchesToCms(double inch) {
		    return inch * INCH_PER_CM;
		}
		
		public static double getScaleFactorToFit(Dimension original, Dimension toFit) {
		    double dScale = 1d;
		    if (original != null && toFit != null) {
		        double dScaleWidth = getScaleFactor(original.width, toFit.width);
		        double dScaleHeight = getScaleFactor(original.height, toFit.height);
		        dScale = Math.min(dScaleHeight, dScaleWidth);
		    }
		    return dScale;
		}

		public static double getScaleFactor(int iMasterSize, int iTargetSize) {
		    double dScale = 1;
		    if (iMasterSize > iTargetSize) {
		        dScale = (double) iTargetSize / (double) iMasterSize;
		    } else {
		        dScale = (double) iTargetSize / (double) iMasterSize;
		    }
		    return dScale;
		}
}
