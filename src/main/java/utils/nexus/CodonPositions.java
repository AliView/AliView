package utils.nexus;

import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import utils.RangeUtils;

public class CodonPositions{
	private static final Logger logger = Logger.getLogger(CodonPositions.class);
	private PositionsArray positionsArray;
	private int readingFrame;
	private TranslatedCodonPos translatedCodonPos;
	
	
	public CodonPositions(int length){
		setPositionsArray(new PositionsArray(length));
		this.readingFrame = 1;
	}
	
	private CodonPositions(PositionsArray positionsArray, int readingFrame) {
		this.setPositionsArray(positionsArray);
		this.readingFrame = readingFrame;	
	}
	
	public void createTranslatedCodonPositions(){
		logger.info("new translated");
		this.translatedCodonPos = new TranslatedCodonPos(positionsArray, this.readingFrame);	
	}

	public boolean isNonCoding(int pos) {
		return positionsArray.getPos(pos) == 0;
	}
	
	public boolean isCoding(int pos) {
		return positionsArray.getPos(pos) != 0;
	}
	
	public int getAminoAcidPosFromNucleotidePos(int pos){
//		logger.info("pos=" + pos);
//		logger.info("getTranslatedCodonPos().size()" + getTranslatedCodonPos().size());
		for(int n = 0; n < getTranslatedCodonPos().size(); n++){
			CodonPos cPos = getTranslatedCodonPos().get(n);
//			logger.info("cPos" + cPos.startPos);
//			logger.info("cEndPos" + cPos.endPos);
			if(pos >= cPos.startPos && pos <= cPos.endPos){
				return n;
			}
		}
		return 0;
	}
	
	private TranslatedCodonPos getTranslatedCodonPos() {
		if(translatedCodonPos == null){
			createTranslatedCodonPositions();
		}
		return translatedCodonPos;
	}

	public CodonPos getCodonPosAtNucleotidePos(int pos){
		CodonPos foundPos = null;
		for(int n = 0; n < getTranslatedCodonPos().size(); n++){
			CodonPos cPos = getTranslatedCodonPos().get(n);
			if(pos >= cPos.startPos && pos <= cPos.endPos){
				foundPos = cPos;
				break;
			}
		}
		return foundPos;
	}
	

	public int getTranslatedAminAcidLength(){
		return getTranslatedCodonPos().size();
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
	
	public boolean isPartOfFullCodon(int x){
		// if full codon check x (x-1 && x -2)
		if(isFullCodonStartingAt(x)){
			return true;
		}else if(isFullCodonStartingAt(x - 1)){
			return true;
		}else if(isFullCodonStartingAt(x - 2)){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted) {
		return getAllNonCodingPositionsAsRanges(wanted, 0, getPositionsArray().getLength());
	}
	
	public ArrayList<IntRange> getAllCodingPositionsAsRanges(int wanted) {
		return getAllNonCodingPositionsAsRanges(wanted, 0, getPositionsArray().getLength());
	}
	
	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted, int startPos, int endPos) {
		ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
		int lastPos = -1;
		int firstPos = -1;
		
		
		// look at uninterupted ranges of the wanted codonInteger - since this in noncoding - they should be next to each other
		// loop all position three times, start in position 0 (offset) and after that start in pos 1 and pos 2
		
		logger.info("endpos"+endPos);
		logger.info("arrayLen"+getPositionsArray().getLength());
		
			for(int n = startPos; n < endPos; n++){
				if(getPositionsArray().get(n) == wanted){
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
				if(getPositionsArray().get(n) == wanted){
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
		for(int n = 0; n < getPositionsArray().getLength(); n++){
			if(getPositionsArray().get(n) == wantedCodonPosInteger){
				allPos.add(new Integer(n));
			}	
		}
		return allPos;
	}

	public void setPosition(int pos, int val) {
		if(pos >= 0 && pos < getPositionsArray().getLength()){
			getPositionsArray().set(pos, val);
			positionsUpdated();
		}
	}
	
	public void resize(int len) {
		logger.info("len" + len);
		logger.info(this.getPositionsArray().getLength());
		logger.info("this.getTranslatedAminAcidLength()" + this.getTranslatedAminAcidLength());
		this.getPositionsArray().resize(len);
		positionsUpdated();
		logger.info(this.getPositionsArray().getLength());
		logger.info("this.getTranslatedAminAcidLength()" + this.getTranslatedAminAcidLength());
	}
	
	public void positionsUpdated() {
		createTranslatedCodonPositions();
	}

	public String debug() {
		StringBuilder sb = new StringBuilder(getPositionsArray().getLength());
		for(int n = 0; n < getPositionsArray().getLength(); n++){
			sb.append(positionsArray.getPos(n));
		}
		return sb.toString();
		
	}

	public void reverse() {
		positionsArray.reverse();
		createTranslatedCodonPositions();	
	}
	
	public CodonPositions getCopy(){
		return new CodonPositions(this.positionsArray.getCopy(), this.readingFrame);		
	}

	public PositionsArray getPositionsArray() {
		return positionsArray;
	}

	public void setPositionsArray(PositionsArray positionsArray) {
		this.positionsArray = positionsArray;
	}

	public int getPosAt(int x){
		return this.positionsArray.getPos(x);
	}
	
	public CodonPositions copyCodonPositionsWithExcludedRemoved(Excludes exset){

		CodonPositions codonPosWithout = new CodonPositions(this.getPositionsArray().getLength() - exset.countExcludedSites());
		
		int posInNew = 0;
		for(int n = 0; n < this.getPositionsArray().getLength(); n++){		
			if(! exset.isExcluded(n)){
				codonPosWithout.setPosition(posInNew, this.getPositionsArray().get(n));
				posInNew ++;
			}	
		}
		
		codonPosWithout.positionsUpdated();
		
		return codonPosWithout;
	}
	
	public int getLength() {
		if(positionsArray != null){
			return positionsArray.getLength();
		}else{
			return 0;
		}
	}

	public void append(CodonPositions secondCodonPos) {
		positionsArray.append(getPositionsArray());
	}

	public void removePosition(int n) {
		this.positionsArray.remove(n);
		positionsUpdated();
	}
	
	public void insertPosition(int n) {
		this.positionsArray.insert(n);
		positionsUpdated();
	}
	
	public CodonPos getCodonInTranslatedPos(int x) {
		return getTranslatedCodonPos().get(x);
	}

	public int getLengthOfTranslatedPos() {
		int size = getTranslatedCodonPos().size();
		return Math.max(0,size);
	}

	public boolean isAnythingButNormal() {
		return positionsArray.isAnythingButDefault();
	}

	

	

	
}