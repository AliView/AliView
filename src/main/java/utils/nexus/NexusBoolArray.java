package utils.nexus;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;


public class NexusBoolArray{
	private static final Logger logger = Logger.getLogger(NexusBoolArray.class);

	private boolean[] positions;
	
	public NexusBoolArray(int length) {
		positions = new boolean[length];
	}
	
	public NexusBoolArray(boolean[] positions) {
		this.positions = positions;
	}
	
	public boolean[] getBooleanArray() {
		return positions;
	}

	public NexusBoolArray getCopy() {
		return new NexusBoolArray(ArrayUtils.clone(positions));
	}


	public void reverse() {
		ArrayUtils.reverse(positions);
		
	}
	
	public int getLength() {
		if(positions != null){
			return positions.length;
		}else{
			return 0;
		}
	}

	public boolean valueAt(int position) {
		return positions[position];
	}

	public boolean containsValue(boolean value) {
		if(positions != null){
			for(boolean position: positions){
				if(position == value){
					return true;
				}
			}
		}
		return false;
	}
	
	public void removePosition(int index){
		positions = ArrayUtils.remove(positions, index);
	}

	public int countValue(boolean value) {
		int size = 0;
		if(positions != null){
			for(boolean position: positions){
				if(position == value){
					size ++;
				}
			}
		}
		return size;
	}

	public void setTrueFromNexusRange(NexusRange range) {
		for(int n = range.getMinimumInteger(); n <= range.getMaximumInteger(); n++){
			positions[n - 1] = true; // always minus one because alignment start with pos 1 in exset (but 0 internally in program)
		}	
	}

	public void debug() {
		logger.info(positions);
		for(boolean val: positions){
//			logger.info(val);
		}
		
	}

	public void append(NexusBoolArray morePositions) {
		int newSize = positions.length + morePositions.getBooleanArray().length;
		boolean[] newPositions = new boolean[newSize];
		System.arraycopy(positions, 0, newPositions, 0, positions.length);
		System.arraycopy(morePositions.getBooleanArray(), 0, newPositions, positions.length, morePositions.getBooleanArray().length);
		this.positions = newPositions;
	}

	public boolean isTrueValContinous() {
		boolean searchVal = true;
		boolean firstFound = false;
		boolean isInterruptedOnce = false;
		boolean isContinous = false;
		if(positions != null){
			for(int n = 0; n + 1 < positions.length; n++){
				boolean positionVal = positions[n];
				boolean nextPosVal = positions[n + 1];
				
				if(positionVal == searchVal && firstFound == false){
					firstFound = true;
					isContinous = true;
				}			
				
				if(positionVal != searchVal && firstFound == true){
					isInterruptedOnce = true;
				}
				
				if(positionVal == true && firstFound == true && isInterruptedOnce == true){
					isContinous = false;
				}
				
			}
		}
		return isContinous;		
	}

}
