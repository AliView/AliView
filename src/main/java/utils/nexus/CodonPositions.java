package utils.nexus;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import utils.RangeUtils;

public class CodonPositions{
	private static final Logger logger = Logger.getLogger(CodonPositions.class);
	private int [] positionsArray;
	private int readingFrame;
	private ArrayList<CodonPos> translatedCodonPos;
	
	public CodonPositions(int length){
		setPositionsArray(new int[length]);
		this.readingFrame = 1;
		fillArrayWith123(getPositionsArray());
		createTranslatedCodonPositions();
	}
	
	private CodonPositions(int[] positionsArray, int readingFrame) {
		this.setPositionsArray(positionsArray);
		this.readingFrame = readingFrame;
		createTranslatedCodonPositions();
		
	}

	private void fillArrayWith123(int[] array){
		for(int n = 0; n < array.length; n++){
			int posVal = (n % 3) + 1;
			array[n] = posVal;
		}
		createTranslatedCodonPositions();
	}
	
	public void createTranslatedCodonPositions(){
		logger.info("creatingtranspositions");
		int x = 0;
		int gap = 0;
		this.translatedCodonPos = new ArrayList<CodonPos>();
		
		CodonPos orfanPos = null;
		while(x < positionsArray.length){
			if(isFullCodonStartingAt(x)){
				translatedCodonPos.add(new CodonPos(x,  x + 2));
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
				// Add a protein gap in sequence for every 3 gaps
				if(gap % 3 == 1){
					orfanPos = new CodonPos(x, x);
					translatedCodonPos.add(orfanPos);
				}
				x = x + 1;	
			}
		}
	}
	
	
	public int getAminoAcidPosFromNucleotidePos(int pos){
		for(int n = 0; n < translatedCodonPos.size(); n++){
			CodonPos cPos = translatedCodonPos.get(n);
			if(pos >= cPos.startPos && pos <= cPos.endPos){
				return n;
			}
		}
		return 0;
	}
	
	public CodonPos getCodonPosAtNucleotidePos(int pos){
		CodonPos foundPos = null;
		for(int n = 0; n < translatedCodonPos.size(); n++){
			CodonPos cPos = translatedCodonPos.get(n);
			if(pos >= cPos.startPos && pos <= cPos.endPos){
				foundPos = cPos;
				break;
			}
		}
		return foundPos;
	}
	
	
	
	public int getTranslatedAminAcidLength(){
		return translatedCodonPos.size();
	}
	

	public boolean isFullCodonStartingAt(int x) {
		boolean isFullCodon = false;
		if(x >= 0 && x < getPositionsArray().length - 2){
			
			if(getReadingFrame() == 1){
				if(getPositionsArray()[x] == 1 && getPositionsArray()[x+1] == 2 && getPositionsArray()[x+2] == 3){
					isFullCodon = true;
				}
			}
			
			if(getReadingFrame() == 2){
				if(getPositionsArray()[x] == 2 && getPositionsArray()[x+1] == 3 && getPositionsArray()[x+2] == 1){
					isFullCodon = true;
				}
			}
			
			if(getReadingFrame() == 3){
				if(getPositionsArray()[x] == 3 && getPositionsArray()[x+1] == 1 && getPositionsArray()[x+2] == 2){
					isFullCodon = true;
				}
			}
			
		}
		return isFullCodon;
	}
	
	
	
	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted) {
		return getAllNonCodingPositionsAsRanges(wanted, 0, getPositionsArray().length);
	}
	
	public ArrayList<IntRange> getAllCodingPositionsAsRanges(int wanted) {
		return getAllNonCodingPositionsAsRanges(wanted, 0, getPositionsArray().length);
	}
	
	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted, int startPos, int endPos) {
		ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
		int lastPos = -1;
		int firstPos = -1;
		
		
		// look at uninterupted ranges of the wanted codonInteger - since this in noncoding - they should be next to each other
		// loop all position three times, start in position 0 (offset) and after that start in pos 1 and pos 2
		
		logger.info("endpos"+endPos);
		logger.info("arrayLen"+getPositionsArray().length);
		
			for(int n = startPos; n < endPos; n++){
				if(getPositionsArray()[n] == wanted){
					if(firstPos == -1){
						firstPos = n;
					}
					lastPos = n;
				}else{
					// if there is a firstPos define range
					if(firstPos != -1){
						IntRange range = new IntRange(firstPos,lastPos);
						allRanges.add(range);
					}
					// start a new range
					firstPos = -1;
					lastPos = -1;
				}
				
			}
			
		// sort them
		RangeUtils.sortIntRangeList(allRanges);
		return allRanges;

	}
	
	
	public ArrayList<IntRange> getAllCodingPositionsAsRanges(int wanted, int startPos, int endPos) {
		ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
		int lastPos = -1;
		int firstPos = -1;
		
		
		// look at uninterupted ranges of the wanted codonInteger at every third position
		// loop all position three times, start in position 0 (offset) and after that start in pos 1 and pos 2
		
		for(int offset = 0; offset <= 2; offset++){
			for(int n = startPos + offset; n <= endPos; n = n +3){
				if(getPositionsArray()[n] == wanted){
					if(firstPos == -1){
						firstPos = n;
					}
					lastPos = n;
				}else{
					// if there is a firstPos define range
					if(firstPos != -1){
						IntRange range = new IntRange(firstPos,lastPos);
						allRanges.add(range);
					}
					// start a new range
					firstPos = -1;
					lastPos = -1;
				}
			}
			// And at end of offset
			// if there is a firstPos define range
			if(firstPos != -1){
				IntRange range = new IntRange(firstPos,lastPos);
				allRanges.add(range);
			}
			// start a new range
			firstPos = -1;
			lastPos = -1;
		}
		
		// sort them
		RangeUtils.sortIntRangeList(allRanges);
		return allRanges;

	}

	public void setReadingFrame(int readingFrame) {
		this.readingFrame = readingFrame;
		createTranslatedCodonPositions();
	}

	public int getReadingFrame() {
		return readingFrame;
	}

	
	public ArrayList<Integer> getAllPositions(int wantedCodonPosInteger) {
		ArrayList<Integer> allPos = new ArrayList<Integer>();
		for(int n = 0; n < getPositionsArray().length; n++){
			if(getPositionsArray()[n] == wantedCodonPosInteger){
				allPos.add(new Integer(n));
			}	
		}
		return allPos;
	}

	public void setPosition(int pos, int val) {
		if(pos >= 0 && pos < getPositionsArray().length){
			getPositionsArray()[pos] = val;
		}
	}
	
	public void fireUpdated() {
		createTranslatedCodonPositions();
	}

	public String debug() {
		StringBuilder sb = new StringBuilder(getPositionsArray().length);
		for(int n = 0; n < getPositionsArray().length; n++){
			sb.append((positionsArray[n]));
		}
		return sb.toString();
		
	}

	public void reverse() {
		ArrayUtils.reverse(getPositionsArray());
		// turn posval 3 into 1 and 1 into 3
		for(int n = 0; n < getPositionsArray().length; n++){
			if(getPositionsArray()[n] == 3){
				getPositionsArray()[n] = 1;
			}
			else if(getPositionsArray()[n] == 1){
				getPositionsArray()[n] = 3;
			}
		}
		createTranslatedCodonPositions();	
	}
	
	public CodonPositions getCopy(){
		return new CodonPositions(ArrayUtils.clone(this.positionsArray), this.readingFrame);		
	}

	public int[] getPositionsArray() {
		return positionsArray;
	}

	public void setPositionsArray(int[] positionsArray) {
		this.positionsArray = positionsArray;
	}

	public int getPosAt(int x){
		return this.positionsArray[x];
	}
	
	public CodonPositions copyCodonPositionsWithExcludedRemoved(Excludes exset){

		CodonPositions codonPosWithout = new CodonPositions(this.getPositionsArray().length - exset.countExcludedSites());
		
		int posInNew = 0;
		for(int n = 0; n < this.getPositionsArray().length; n++){		
			if(! exset.isExcluded(n)){
				codonPosWithout.setPosition(posInNew, this.getPositionsArray()[n]);
				posInNew ++;
			}	
		}
		
		codonPosWithout.fireUpdated();
		
		return codonPosWithout;
	}
	
	public int getLength() {
		if(positionsArray != null){
			return positionsArray.length;
		}else{
			return 0;
		}
	}

	public void append(CodonPositions secondCodonPos) {
		int newSize = this.getLength() + secondCodonPos.getLength();
		int[] newPositions = new int[newSize];
		System.arraycopy(positionsArray, 0, newPositions, 0, positionsArray.length);
		System.arraycopy(secondCodonPos.getPositionsArray(), 0, newPositions, positionsArray.length, secondCodonPos.getPositionsArray().length);
		this.positionsArray = newPositions;
	}

	public void removePosition(int n) {
		this.positionsArray = ArrayUtils.remove(this.positionsArray, n);
	}
	
	public CodonPos getCodonInTranslatedPos(int x) {
		return translatedCodonPos.get(x);
	}

	public int getLengthOfTranslatedPos() {
		int size = translatedCodonPos.size();
		return Math.max(0,size);
	}
}