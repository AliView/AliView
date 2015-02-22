package aliview.sequences;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;
import aliview.sequencelist.MemoryMappedSequencesFile;
import aliview.utils.ArrayUtilities;

public class FileSequenceBases implements Bases {
	private static final Logger logger = Logger.getLogger(FileSequenceBases.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	
	protected MemoryMappedSequencesFile sequencesFile;
	private long startPointer;
	private long endPointer;
	private long sequenceAfterNameStartPointer;

	public FileSequenceBases(MemoryMappedSequencesFile sequencesFile, long startPointer, long endPointer, long sequenceAfterNameStartPointer) {
		this.sequencesFile = sequencesFile;
		this.startPointer = startPointer;
		this.endPointer = endPointer;
		this.sequenceAfterNameStartPointer = sequenceAfterNameStartPointer;
	}
	
	public FileSequenceBases(MemoryMappedSequencesFile sequencesFile, long startPointer) {
		this.sequencesFile = sequencesFile;
		this.startPointer = startPointer;
	}

	public FileSequenceBases getCopy(){
		return new FileSequenceBases(sequencesFile, startPointer, endPointer, sequenceAfterNameStartPointer);
	}
	
	public int getLength(){
		 long len = (getEndPointer() - getStartPointer()) - (getSequenceAfterNameStartPointer() - getStartPointer()); // +1 because seq end pointer is inclusive 
		 return (int)len;
	}
	
	protected long getSequenceAfterNameStartPointer(){
		return sequenceAfterNameStartPointer;
	}

	protected long getStartPointer(){
		return startPointer;
	}

	protected long getEndPointer(){
		return endPointer;
	}

	protected void setEndPointer(long end) {
		this.endPointer = end;		
	}

	protected void setSequenceAfterNameStartPointer(long seqStartPointer) {
		this.sequenceAfterNameStartPointer = seqStartPointer;		
	}

	public byte get(int n) {
		return (byte) sequencesFile.readInFile(getSequenceAfterNameStartPointer() + n);
	}

	public char charAt(int n) {
		return (char) get(n);
	}

	public byte[] toByteArray() {
		return toByteArray(0, this.getLength() - 1);
	}
	
	public byte[] toByteArray(int startIndexInclusive, int endIndexInclusive) {
		// TODO Problem if to big
		if(this.getLength() > 100 * 1000 * 1000){
			return null;
		}
		
		int subSize = endIndexInclusive - startIndexInclusive + 1; // +1 because end index is inclusive
		
		
		if(subSize >= 0){
			byte[] subarray = new byte[subSize];
			int subIndex = 0;
			for(int n = startIndexInclusive; n <= endIndexInclusive; n++){
				subarray[subIndex] = get(n);
				subIndex ++;
			}
			return subarray;
		}
		return null;
	}
	
	@Override
	public String toString() {
		
		logger.warn("Maybe string could be to big....");
		return super.toString();
	}

	public void set(int n, byte newBase) {
		
	}
	
	private void assureSize(int n) {

	}

	private void resize(int n) {
		
	}
	
	// convenience
	public void append(byte[] newBytes) {
		
	}

	public void moveBaseLeft(int n) {
		
	}

	public void moveBaseRight(int n) {
		
	}
	
	
	public void insertAt(int n, byte[] newBytes) {
		
	}

	public void replace(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases) {
		
	}
	
	public void deleteAll(byte target) {
		
	}
	
	public void delete(int[] toDelete) {
		
	}
	
	// ?????
	public void complement() {
		
	}
	
	// ?????
	public void reverse() {
		
	}

	// convenience method
	public void set(int n, char c) {
		
	}
		
	// convenience
	public void delete(int pos) {
		
	}
		
	// convenience
	public void insertAt(int n, byte newByte) {
		
	}
		
	


}
