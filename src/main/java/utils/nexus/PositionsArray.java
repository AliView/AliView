package utils.nexus;

import org.apache.commons.lang.ArrayUtils;

public class PositionsArray {
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
		for(int n = 0; n < array.length; n++){
			int posVal = (n % 3) + 1;
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
		if(backend != null){
			backend = ArrayUtils.remove(backend, n);
		}
	}

	public boolean isAnythingButDefault() {
		if(backend != null){
			return true;
		}else{
			return false;
		}
	}

}
