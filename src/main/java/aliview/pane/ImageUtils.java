package aliview.pane;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sun.misc.Unsafe;

public class ImageUtils {
	private static final Logger logger = Logger.getLogger(ImageUtils.class);
	
	@SuppressWarnings("deprecation")
	public static void insertRGBArrayAt(int targetX, int targetY, RGBArray newPiece, RGBArray origPiece) throws Exception{
		
		try{
			//UnSafe unsafe = Unsafe.getUnsafe();
		int offset = targetY * origPiece.getScanWidth() + targetX; // first pos in target array
		int target;
		
		// Outer loop is each scanline
		
		int counter = 0;
		for(int scanCount = 0; scanCount < newPiece.getHeight(); scanCount ++){
			// Inner loop is each pixel per scan
		//	int posOrig = offset + scanCount * origPiece.getScanWidth();
		//	int posNewPiece = scanCount * newPiece.getScanWidth();
			//System.arraycopy(origPiece.backend, posOrig, newPiece.backend, posNewPiece, newPiece.getScanWidth());
			
			for(int n = 0; n < newPiece.getScanWidth(); n++){
				int posOrig = offset + scanCount * origPiece.getScanWidth() + n;
				int posNewPiece = scanCount * newPiece.getScanWidth() + n;
				origPiece.backend[posOrig] = newPiece.backend[posNewPiece];		
				counter++;
			}	
		}
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

}
