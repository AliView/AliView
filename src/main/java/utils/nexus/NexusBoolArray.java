package utils.nexus;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;


public class NexusBoolArray{
	private static final Logger logger = Logger.getLogger(NexusBoolArray.class);

	private boolean[] backend;
	
	public NexusBoolArray(int length) {
		backend = new boolean[length];
	}
	
	public NexusBoolArray(boolean[] positions) {
		this.backend = positions;
	}
	
	public boolean[] getBooleanArray() {
		return backend;
	}

	public NexusBoolArray getCopy() {
		return new NexusBoolArray(ArrayUtils.clone(backend));
	}


	public void reverse() {
		ArrayUtils.reverse(backend);
		
	}
	
	public int getLength() {
		if(backend != null){
			return backend.length;
		}else{
			return 0;
		}
	}

	public boolean valueAt(int position) {
		if(!rangeCheck(position)){
			return false;
		}
		return backend[position];
	}

	public boolean containsValue(boolean value) {
		if(backend != null){
			for(boolean position: backend){
				if(position == value){
					return true;
				}
			}
		}
		return false;
	}
	
	public void removePosition(int index){
		if(rangeCheck(index)){
			backend = ArrayUtils.remove(backend, index);
		}
		
	}
	
	public void set(int pos, boolean b) {
		backend[pos] = b;
	}

	private boolean rangeCheck(int index) {
		if(backend == null){
			return false;
		}
		if(backend.length <= index){
			return false;
		}
		return true;
		
	}

	public int countValue(boolean value) {
		int size = 0;
		if(backend != null){
			for(boolean position: backend){
				if(position == value){
					size ++;
				}
			}
		}
		return size;
	}

	public void setTrueFromNexusRange(NexusRange range) {
		for(int n = range.getMinimumInt(); n <= range.getMaximumInt(); n++){
			if(rangeCheck(n - 1)){
				backend[n - 1] = true; // always minus one because alignment start with pos 1 in exset (but 0 internally in program)
			}
		}	
	}

	public void debug() {
		logger.info(backend);
		for(boolean val: backend){
//			logger.info(val);
		}
		
	}

	public void append(NexusBoolArray morePositions) {
		int newSize = backend.length + morePositions.getBooleanArray().length;
		boolean[] newPositions = new boolean[newSize];
		System.arraycopy(backend, 0, newPositions, 0, backend.length);
		System.arraycopy(morePositions.getBooleanArray(), 0, newPositions, backend.length, morePositions.getBooleanArray().length);
		this.backend = newPositions;
	}
	
	public void insertPosition(int n) {
		if(this.backend != null){
			this.backend = ArrayUtils.add(this.backend, n, false);
		}
	}

	public boolean isTrueValContinous() {
		boolean searchVal = true;
		boolean firstFound = false;
		boolean isInterruptedOnce = false;
		boolean isContinous = false;
		if(backend != null){
			for(int n = 0; n + 1 < backend.length; n++){
				boolean positionVal = backend[n];
				boolean nextPosVal = backend[n + 1];
				
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
