package aliview.sequences;

public class PositionToPointer {
	
	private int startPos;
	private int endPos;
	private long startPointer;
	private long endPointer;

	public PositionToPointer(int startPos, int endPos, long startPointer, long endPointer) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.startPointer = startPointer;
		this.endPointer = endPointer;
	}

	public PositionToPointer(int seqPosition, long startPointer, long endPointer) {
		this(seqPosition, seqPosition + (int)(endPointer - startPointer), startPointer, endPointer);
	}
	
	public PositionToPointer getCopy() {
		return new PositionToPointer(startPos, endPos, startPointer, endPointer);
	}
	

	public long getPointer(int askedPos) {
		long pointer = -1;
		if(containsPos(askedPos)){
			int offset = askedPos - startPos; 
			pointer = startPointer + offset;
		}
		return pointer;
	}

	public boolean containsPos(int askedPos) {
		if(askedPos >= startPos && askedPos <= endPos){
			return true;
		}
		else{
			return false;
		}
	}

	public int getEndPos() {
		return endPos;
	}

	public long getEndPointer() {
		return endPointer;
	}

}
