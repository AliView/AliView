package aliview.sequences;

import java.util.ArrayList;

public class PositionsToPointer {

	ArrayList<PositionToPointer> posToPointerList = new ArrayList<PositionToPointer>();
	private PositionToPointer cachedPosToPoint;

	public PositionsToPointer getCopy(){
		PositionsToPointer copy = new PositionsToPointer();
		// Make copy of list and content
		copy.posToPointerList = new ArrayList<PositionToPointer>();
		for(PositionToPointer next: posToPointerList){
			copy.posToPointerList.add(next.getCopy());
		}
		return copy;
	}

	public void add(PositionToPointer posToPoint){
		posToPointerList.add(posToPoint);
	}

	public long getPointerFromPos(int pos){
		long pointer = -1;
		if(cachedPosToPoint != null){
			pointer =  cachedPosToPoint.getPointer(pos);
		}
		if(pointer == -1){
			cachedPosToPoint = getPosToPointerContaining(pos);
			pointer =  cachedPosToPoint.getPointer(pos);
		}
		return pointer;	
	}

	private PositionToPointer getPosToPointerContaining(int pos) {
		for(PositionToPointer posPoint: posToPointerList){
			if(posPoint.containsPos(pos)){
				return posPoint;
			}
		}
		return null;
	}

	public int getMaxPosition() {
		PositionToPointer lastPosPoint = posToPointerList.get(posToPointerList.size() - 1);
		return lastPosPoint.getEndPos();
	}

	public long getMaxPointer() {
		PositionToPointer lastPosPoint = posToPointerList.get(posToPointerList.size() - 1);
		return lastPosPoint.getEndPointer();
	}

}
