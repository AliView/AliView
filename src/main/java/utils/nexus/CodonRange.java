package utils.nexus;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class CodonRange extends Range{
	
	private static final Logger logger = Logger.getLogger(CodonRange.class);
	
	public CodonRange(int start, int end, int startVal) {
		super(start, end, startVal);
		// TODO Auto-generated constructor stub
	}
	
	public CodonRange getCopy() {
		return new CodonRange(start, end, startVal);
	}

	
	//
	//
	// CodonRange
	//
	//
	
	
	public static CodonRange newDefaultRange() {
		return new CodonRange(0, Integer.MAX_VALUE - 1000, 1); // - 1000 on MaxVal to make sure there is room for adding one or two
	}

	
	// includes partial at front and end as a position
		public int countAllCodons(int readingFrame){
			return countCodonsUntil(this.end, readingFrame);
		}
		
		// includes partial at front and end as a position
			public int countCodonsUntil(int endPos, int readingFrame){
				
				if(startVal == 0){
					// don't bother about reading frame and startVal
					int positions = end - start;
					int retVal = (int) Math.ceil( (double)(endPos - start + 1) / 3 ); // +1 extra because end is inclusive
					//logger.info("retVal" + retVal);
					return retVal;
				}
				
				
				int startCorrected = getFirstFullFrameStartPos(readingFrame);
		//		logger.info("startCorrected");
				
				
				int count = 0;
				
				// add one orfan at start
				if(start != startCorrected){
					count ++;
				}

				count = count + (int) Math.ceil( (double)(endPos - startCorrected + 1) / 3 ); // +1 extra because end is inclusive
				
		//		logger.info("endPos=" + endPos + " count=" + count);
				
				return count;
				
			}
	
	

	public boolean containsCodonPos(int pos, int offset, int readingFrame) {
		int maxPos = offset + countAllCodons(readingFrame) - 1; // minus 1 because we start at 0
		if(pos <= maxPos){
			return true;
		}
		return false;
	}
	
	
	
	
	public int getPosAtCodonPos(int codonPos, int readingFrame) {
		if(codonPos == 0){
			return start;
		}
		
		int correctedStart = getFirstFullFrameStartPos(readingFrame);
		int pos;
		
		// add one orfan at start
		if(start == correctedStart){		
			pos =  start + codonPos * 3;	
		}
		else{
			pos = correctedStart + (codonPos - 1) * 3;
		}
		return pos;
	}


	private int getFirstFullFrameStartPos(int readingFrame) {
		if(startVal == 0){
			return start;
		}
		
		int startCorrected = start;
		
		if(readingFrame - startVal != 0){

			int codonStartOffset = readingFrame - startVal;
			if(codonStartOffset < 0){
				codonStartOffset = 3 + codonStartOffset;
			}
			
			startCorrected = start + codonStartOffset;
		}
		return startCorrected; 
	}

}
