package utils.nexus;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;


public class PositionsArray {
	private static final Logger logger = Logger.getLogger(PositionsArray.class);
	private int [] backend;
	private int length;
	
	public PositionsArray(int length) {
		this.length = length;
	}

	public int getLength() {
		if(backend != null){
			return backend.length;
		}else{
			return length;
		}
	}

	public int getPos(int pos) {
		if(backend != null){
			return backend[pos];
		}
		else{
			return (pos % 3) + 1;
		}
	}

	public PositionsArray getCopy() {
		PositionsArray copy = new PositionsArray(this.length);
		if(backend != null){
			copy.backend = ArrayUtils.clone(this.backend);
		}
		return copy;
	}

	public int get(int x) {
		return getPos(x);
	}

	public void set(int pos, int val) {
		if(backend != null){
			backend[pos] = val;
		}
		else{
			createNewBackend();
			backend[pos] = val;
		}
	}

	private void createNewBackend() {
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

	public void append(PositionsArray otherPos) {
		if(this.backend != null && otherPos.backend != null){
			int newSize = this.getLength() + otherPos.getLength();
			int[] newPositions = new int[newSize];
			System.arraycopy(backend, 0, newPositions, 0, backend.length);
			System.arraycopy(otherPos.backend, 0, newPositions,backend.length, otherPos.backend.length);
			this.backend = newPositions;
		}
		else{
			// do nothing
		}
	
	}

	public void remove(int n) {
		logger.info("remove" + n);
		if(backend == null){
			createNewBackend();
		}
		backend = ArrayUtils.remove(backend, n);
	}
	
	
	
	public void insert(int n) {
		if(backend == null){
			createNewBackend();
		}
		backend = ArrayUtils.add(backend, n, 0);
	}

	private boolean isBackendAnythingBut123(){
		boolean isAnythingBut123 = false; 
		if(backend != null){
			for(int n = 0; n < backend.length; n++){
				int defaltVal = (n % 3) + 1;
				if(backend[n] != defaltVal){
					isAnythingBut123 = true;
					break;
				}
			}
		}
		return isAnythingBut123;
	}
	
	public boolean isAnythingButDefault() {
		boolean isAnythingButDefault = false;
		if(backend == null){
			isAnythingButDefault = false;
		}else{
			isAnythingButDefault = isBackendAnythingBut123();
		}
		return isAnythingButDefault;
	}

	public void resize(int len) {
		this.length = len;
		if(backend != null){
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
	}

	

}
