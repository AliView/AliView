package utils.nexus;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.BALOAD;


public class NexusRangePositionsArray {
	private static final Logger logger = Logger.getLogger(NexusRangePositionsArray.class);
	private Ranges ranges = new Ranges();

	public NexusRangePositionsArray() {
		logger.info("new");
		ranges.add(new CodonRange(0, Integer.MAX_VALUE - 1000, 1)); // - 1000 on MaxVal to make sure there is room for adding one or two
	}
	
	private NexusRangePositionsArray(Ranges ranges) {
		this.ranges = ranges;
	}
	
	public int size() {
		return 0;//1000000000;
	}
	
	
	public int getPos(int pos){
		
		CodonRange range = getRange(pos);
		
		int posVal = range.getPosVal(pos);
		
		return posVal;
		
	}


	
	private CodonRange getRange(int pos) {
		return ranges.getRange(pos);
	}

	public NexusRangePositionsArray getCopy() {
		return new NexusRangePositionsArray(ranges.getCopy());
		
	}

	public int get(int x) {
		return getPos(x);
	}

	public void addRange(CodonRange range){
		logger.info("addRange");
		this.ranges.add(range);
	}
	
	/*
	public void set(int pos, int val) {
		if(backend == null){
			createNewBackend(pos + 1);
		}
		else if(pos >= backend.length){
			resizeBackend(pos + 1);
		}
		backend[pos] = val;
	}

	private void createNewBackend(int length) {
		backend = new int[length];
		fillArrayWith123(backend);
	}
	
	private void fillArrayWith123(int[] array){
		fillArrayWith123(array, 1);
	}
	
	private void fillArrayWith123(int[] array, int startVal){
		for(int n = 0; n < array.length; n++){
			int posVal = ((n + startVal - 1) % 3) + 1;
			array[n] = posVal;
		}
	}

	*/
	
	/*

	public void reverse() {
		if(backend != null){
			ArrayUtils.reverse(backend);
			// turn posval 3 into 1 and 1 into 3
			for(int n = 0; n < backend.length; n++){
				if(backend[n] == 3){
					backend[n] = 1;
				}
				else if(backend[n] == 1){
					backend[n] = 3;
				}
			}
		}
		else{
			// do nothing
		}
	}

	public void append(NexusRangePositionsArray otherPos) {
		if(this.backend != null && otherPos.backend != null){
			int newSize = backend.length + otherPos.backend.length;
			int[] newPositions = new int[newSize];
			System.arraycopy(backend, 0, newPositions, 0, backend.length);
			System.arraycopy(otherPos.backend, 0, newPositions,backend.length, otherPos.backend.length);
			this.backend = newPositions;
		}
		else{
			// do nothing
		}
	
	}
	
	

	public void remove(int pos) {
		logger.info("remove" + pos);
		if(backend == null){
			// should not happen... do nothing
			return;
			//createNewBackend(pos + 1);
		}
		else if(pos > backend.length){
			// do nothing
			return;
			// resizeBackend(pos + 1);
		}

		backend = ArrayUtils.remove(backend, pos);
	}
	
	
	
	public void insert(int pos) {
		if(backend == null){
			// should not happen... do nothing
			return;
			//createNewBackend(pos + 1);
		}
		else if(pos > backend.length){
			// do nothing
			return;
			// resizeBackend(pos + 1);
		}
		backend = ArrayUtils.add(backend, pos, 0);
	}
*/

	private boolean isBackendAnythingBut123(){
		return ranges.size() != 1;
	}
	
	public boolean isAnythingButDefault() {
		return ranges.size() != 1;
	}

	public void reverse() {
		// TODO Auto-generated method stub
		
	}

	public void remove(int n) {
		// TODO Auto-generated method stub
		
	}

	public void insert(int n) {
		// TODO Auto-generated method stub
		
	}

	public int nucPosFromAAPos(int pos, int readingFrame) {
		return ranges.nucPosFromAAPos(pos, readingFrame);
	}

	public int aaPosFromNucPos(int pos, int readingFrame) {
		return ranges.aaPosFromNucPos(pos, readingFrame);
	}

	/*
	public void resizeBackend(int len) {
		if(backend == null){
			createNewBackend(len);
		}
		if(backend.length > len){
			backend = ArrayUtils.subarray(backend, 0, len);
		}
		if(backend.length < len){
			
			int[] newVals = new int[len - backend.length];
			int lastPos = getPos(backend.length - 1);
			if(lastPos != 0){
				logger.info("lastPos" + lastPos);
				fillArrayWith123(newVals, lastPos + 1);
			}
			logger.info("newVals.length" + newVals.length);
			logger.info("backend.length" + backend.length);
			backend = ArrayUtils.addAll(backend, newVals);
			

		}
	}
	*/

	

}
