package aliview.sequences;

import org.apache.log4j.Logger;

import aliview.importer.ClustalFileIndexer;
import aliview.sequencelist.FileMMSequenceList;

public class PositionsToPointerFileSequence extends FileSequence {
	private static final Logger logger = Logger.getLogger(PositionsToPointerFileSequence.class);
	PositionsToPointer positionsToPointer = new PositionsToPointer();
	
	public PositionsToPointerFileSequence(FileMMSequenceList fileSeqList, int seqIndex,long startPointer){
		super(fileSeqList, seqIndex, startPointer);
	}
	
	public void add(PositionToPointer posToPoint){
		positionsToPointer.add(posToPoint);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public long getEndPointer() {
		return positionsToPointer.getMaxPointer(); 
	}
	
	public byte getBaseAtPos(int n) {
		return (byte)getBaseAsIntAtPos(n);
	}

	public int getBaseAsIntAtPos(int n) {
		long pos = positionsToPointer.getPointerFromPos(n);
		return fileSeqList.readInFile(pos);
	}

	public int getBasesAt(int x, int i, byte[] bytes){
		// TODO difficult if spanning multiple....
		//long pos = positionsToPointer.getPointerFromPos(x);
		return fileSeqList.readBytesInFile( (getSequenceAfterNameStartPointer() + x), i, bytes);
	}

	public void addName(String name){
		this.name = name;
	}
	
	public int getLength(){
		int length = positionsToPointer.getMaxPosition() + 1; // +1 since that is length
		return length;
	}
}
