package utils.nexus;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;


public class PositionsArray {
	private static final Logger logger = Logger.getLogger(PositionsArray.class);
	private int [] backend;


	public PositionsArray() {
	}
	
	private PositionsArray(int[] backend) {
		this.backend = backend;
	}
/*
	public int getLength() {
		if(backend != null){
			return backend.length;
		}else{
			return 0;
		}
	}
*/
	
	public int size() {
		if(backend != null){
			return backend.length;
		}else{
			return 0;
		}
	}
	
	public int getPos(int pos) {
		
		if(backend == null || backend.length == 0){
			return (pos % 3) + 1;
		}
		else if(pos < backend.length){
			return backend[pos];
		}
		else{
			int lastVal = backend[backend.length - 1];
			if(lastVal == 0){
				return 0;
			}
			else{
				return (pos % 3) + lastVal;
			}
		}		
	}


	public PositionsArray getCopy() {
		if(backend == null){
			return new PositionsArray();
		}
		else{
			int[] clonedBackend = backend = ArrayUtils.clone(this.backend);
			return new PositionsArray(clonedBackend);
		}
	}

	public int get(int x) {
		return getPos(x);
	}

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

	

}
