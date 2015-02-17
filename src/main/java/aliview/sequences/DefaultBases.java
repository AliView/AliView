package aliview.sequences;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import aliview.NucleotideUtilities;
import aliview.utils.ArrayUtilities;

public class DefaultBases implements Bases {
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	byte[] backend;

	public DefaultBases(byte[] bytes) {
		this.backend = bytes;
	}
	
	public byte[] getBackend(){
		return backend;
	}

	public DefaultBases getCopy(){
		return new DefaultBases(ArrayUtils.clone(backend));
	}
	
	public int getLength(){
		// or translated
		return backend.length;
	}

	public byte get(int n) {
		// or translated
		return backend[n];
	}

	public char charAt(int n) {
		// or translated
		return (char) backend[n];
	}

	public byte[] toByteArray() {
		// or translated
		return backend;
	}
	
	public byte[] toByteArray(int startIndexInclusive, int endIndexInclusive) {
		byte[] subArray = ArrayUtils.subarray(backend, startIndexInclusive, endIndexInclusive + 1);
		return subArray;	
	}
	
	@Override
	public String toString() {
		String asString = null;
		// or translated
		try {
			asString = new String(backend, TEXT_FILE_BYTE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return asString;
	}

	public void set(int n, byte newBase) {
		// or translated
		backend[n] = newBase;
	}
	
	public void moveBaseLeft(int n) {
		set(n - 1, get(n));	
	}

	public void moveBaseRight(int n) {
		set(n + 1, get(n));	
	}
	
	
	public void insertAt(int n, byte[] newBytes) {
		// or translated
		
		
		byte[] newArray = ArrayUtilities.insertAt(backend, n, newBytes);
		backend = newArray;
	}

	public void replace(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases) {
		// or translated
		
		// translate start stop and insert
		
		
		int newLength = backend.length - (stopReplaceIndex + 1 - startReplaceIndex) + insertBases.length;

		// TODO could check if length is less - then just clear and insert
		byte[] newBases = new byte[newLength];

		// copy first untouched part of sequence
		System.arraycopy(backend, 0, newBases, 0, startReplaceIndex);

		// copy insert bases
		System.arraycopy(insertBases, 0, newBases, startReplaceIndex, insertBases.length);

		// copy last untouched part of sequence - if there is one
		if(stopReplaceIndex < backend.length - 1){
			System.arraycopy(backend, stopReplaceIndex + 1, newBases, startReplaceIndex + insertBases.length, backend.length - (stopReplaceIndex + 1));
		}

		backend = newBases;
		
	}
	
	public void delete(int[] toDelete) {
		// or translated
		
		// translate toDelete
		
		
		// create new array size removed selected bases
		byte[] newBases = new byte[backend.length - toDelete.length];

		int newIndex = 0;
		
		for(int n = 0;n < backend.length ;n++){
			
			if(ArrayUtils.contains(toDelete, n)){
				// dont copy this one
			}
			else{
				newBases[newIndex] = backend[n];
				newIndex ++;
			}
		}
		
		backend = newBases;
		
	}
	
	// ?????
	public void complement() {
		NucleotideUtilities.complement(backend);		
	}
	
	// ?????
	public void reverse() {
		ArrayUtils.reverse(backend);	
	}

	
	
	// convenience method
	public void set(int n, char c) {
		set(n, (byte) c);
	}
		
	// convenience
	public void delete(int pos) {
		delete(new int[]{pos});
	}
		
	// convenience
	public void insertAt(int n, byte newByte) {
		insertAt(n, new byte[]{newByte});
	}
		
	// convenience
	public void append(byte[] newBytes) {
		insertAt(backend.length, newBytes);
	}


}
