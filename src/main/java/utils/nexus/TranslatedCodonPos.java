package utils.nexus;

import java.util.ArrayList;

public class TranslatedCodonPos {
	
	private ArrayList<CodonPos> backend;
	private PositionsArray positionsArray;
	private int readingFrame;
	
	public TranslatedCodonPos(PositionsArray positionsArray) {
		this.positionsArray = positionsArray;
		this.readingFrame = 1;
		if(positionsArray.isAnythingButDefault()){
			createNewBackend(positionsArray);
		}
		
	}
	
	private void createNewBackend(PositionsArray positionsArray) {
	
		int x = 0;
		int gap = 0;
		this.backend = new ArrayList<CodonPos>();
		
		CodonPos orfanPos = null;
		while(x < positionsArray.getLength()){
			if(isFullCodonStartingAt(x)){
				backend.add(new CodonPos(x,  x + 2));
				// clear gap
				gap = 0;
				x = x + 3; // move one frame ahead (this is a full codon)
			}
			else{
				if(gap > 0){
					// add new end pos to the last Pos
					orfanPos.addEndPos(x);
				}
				// there is a gap in protein translation
				gap ++;
				// Add a protein gap in sequence for every 3 gaps (or if it is the last one)
				if(gap % 3 == 1 || x == positionsArray.getLength() -1){
					orfanPos = new CodonPos(x, x);
					backend.add(orfanPos);
				}
				x = x + 1;	
			}
		}
	}
	
	public boolean isFullCodonStartingAt(int x) {
		boolean isFullCodon = false;
		if(x >= 0 && x < getPositionsArray().getLength() - 2){
			
			if(getReadingFrame() == 1){
				if(getPositionsArray().get(x) == 1 && getPositionsArray().get(x+1) == 2 && getPositionsArray().get(x+2) == 3){
					isFullCodon = true;
				}
			}
			
			if(getReadingFrame() == 2){
				if(getPositionsArray().get(x) == 2 && getPositionsArray().get(x+1) == 3 && getPositionsArray().get(x+2) == 1){
					isFullCodon = true;
				}
			}
			
			if(getReadingFrame() == 3){
				if(getPositionsArray().get(x) == 3 && getPositionsArray().get(x+1) == 1 && getPositionsArray().get(x+2) == 2){
					isFullCodon = true;
				}
			}	
		}
		return isFullCodon;
	}

	private int getReadingFrame() {
		return readingFrame;
	}

	private PositionsArray getPositionsArray() {
		return positionsArray;
	}

	public void add(CodonPos codonPos) {
		backend.add(codonPos);
	}

	public int size() {
		if(backend != null && backend.size() > 0){
			return backend.size();
		}else{
			return (int) Math.ceil(positionsArray.getLength() / 3);
		}
	}

	public CodonPos get(int n) {
		if(backend != null && backend.size() > 0){
			return backend.get(n);
		}else{		
			int start = n * 3;
			int end = start + 2;
			return new CodonPos(start, end);
		}
			
		
		
		
	}

}
