package aliview.sequences;

import aliview.sequencelist.FileMMSequenceList;

public class PhylipFileSequence extends FileSequence {
	PositionsToPointer positionsToPointer = new PositionsToPointer();
	
	public PhylipFileSequence(FileMMSequenceList fileSeqList, int seqIndex,long startPointer){
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
