package utils.nexus;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Range implements Comparable<Range>{
	private static final Logger logger = Logger.getLogger(Range.class);
	
	public int start;
	public int end;
	public int startVal;
	public int step;
	
	public Range(int start, int end, int startVal, int step) {
		this.start = start;
		this.end = end;
		this.startVal = startVal;
		this.step = step;
	}
	
	public Range(int start, int end, int startVal) {
		this(start, end, startVal, 1); // default every pos
	}

	public boolean contains(int pos) {
		if(pos >= start && pos <= end){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean containsRange(Range compare) {
		
		if(this.contains(compare.start) && this.contains(compare.end)){
			return true;
		}
		
		return false;
	}
	
	public boolean intersects(Range compare) {
		if(this.contains(compare.start) || this.contains(compare.end)){
			return true;
		}
		
		return false;
	}
	
	public boolean within(Range compare) {
		if(compare.contains(start) && compare.contains(end)){
			return true;
		}
		return false;
		
	}

	public Range getCopy() {
		return new Range(start, end, startVal);
	}
	
	public Range crop(Range cropTemplate) {
		
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
	
	
	public void merge(Range additionalRange) {
		
		int start = Math.min(this.start, additionalRange.start);
		int end = Math.max(this.end, additionalRange.end);
		
		this.start = start;
		this.end = end;
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

	public Range cutOut(Range additionalRange) {
		Range otherPart = this.getCopy();
		this.moveEnd(additionalRange.start - 1);
		otherPart.moveStart(additionalRange.end + 1);
		return otherPart;
	}

	public int compareTo(Range other) {
		return this.start - other.start; 
	}


	public int getLength() {
		return end - start;
	}
}
