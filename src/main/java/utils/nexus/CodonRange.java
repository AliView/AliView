package utils.nexus;

import org.apache.log4j.Logger;

public class CodonRange implements Comparable<CodonRange>{
	private static final Logger logger = Logger.getLogger(CodonRange.class);
	
	public int start;
	public int end;
	public int startVal;
	
	public CodonRange(int start, int end, int startVal) {
		this.start = start;
		this.end = end;
		this.startVal = startVal;
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
	
	public int getPosVal(int pos) {
		
		if(startVal == 0){
			return 0;
		}
		
		int diff = pos - start;
		
		int posVal = startVal;
		
		if(diff > 0){
			posVal = ( ( (startVal - 1) + diff) % 3)  + 1;
		}
		
		if(diff < 0){
			diff = Math.abs(diff);		
			posVal = ( ( (startVal - 1) + diff) % 3)  + 1;		
			posVal = 4 - Math.abs(posVal);
		}
		
		return posVal;
		
	}
	
	

	public boolean contains(int pos) {
		if(pos >= start && pos <= end){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean containsRange(CodonRange compare) {
		
		if(this.contains(compare.start) && this.contains(compare.end)){
			return true;
		}
		
		return false;
	}
	
	public boolean intersects(CodonRange compare) {
		if(this.contains(compare.start) || this.contains(compare.end)){
			return true;
		}
		
		return false;
	}
	
	public boolean within(CodonRange compare) {
		if(compare.contains(start) && compare.contains(end)){
			return true;
		}
		return false;
		
	}

	public CodonRange getCopy() {
		return new CodonRange(start, end, startVal);
	}
	

	public CodonRange crop(CodonRange cropTemplate) {
		
		// crop front adjust template end (or not)
		if(this.contains(cropTemplate.end)){
			this.moveStart(cropTemplate.end + 1);
		}
		
		// crop front adjust template end (or not)
		if(this.contains(cropTemplate.start)){
			this.moveEnd(cropTemplate.start -1);
		}
		
		return this;
	}

	private void moveStart(int newStart) {
		int newStartVal = getPosVal(newStart);
		
		this.start = newStart;
		this.startVal = newStartVal;
			
	}
	
	private void moveEnd(int newEnd) {
		this.end = newEnd;
	}
	
	@Override
	public String toString() {
		String out = "start=" + start + " end=" + end + " startVal=" + startVal;
		return out;
	}

	public CodonRange cutOut(CodonRange additionalRange) {
		CodonRange otherPart = this.getCopy();
		this.moveEnd(additionalRange.start - 1);
		otherPart.moveStart(additionalRange.end + 1);
		return otherPart;
	}

	public int compareTo(CodonRange other) {
		return this.start - other.start; 
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


	public void merge(CodonRange additionalRange) {
		// 
		if(this.contains(additionalRange.end)){
			this.moveStart(additionalRange.start);
		}
				
		// 
		if(this.contains(additionalRange.start)){
			this.moveEnd(additionalRange.end);
		}
	}
		

}
