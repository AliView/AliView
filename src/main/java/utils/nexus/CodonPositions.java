package utils.nexus;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.BitSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import utils.RangeUtils;

public class CodonPositions{
	private static final Logger logger = Logger.getLogger(CodonPositions.class);
	private NexusRangePositionsArray positionsArray;
	private int readingFrame;
	private TranslatedAminoAcidPositions translatedAminoAcidPos;	
	private BitSet notused;
	private ArrayList<NexusRange> nexusRanges = new ArrayList<NexusRange>();
	
	public CodonPositions(){
		this(new NexusRangePositionsArray(), 1);
	}
	
	public int size() {
		return positionsArray.size();
	}

	private CodonPositions(NexusRangePositionsArray positionsArray, int readingFrame) {
		this.setPositionsArray(positionsArray);
		this.readingFrame = readingFrame;	
	}

	public boolean isNonCoding(int pos){
		return positionsArray.getPos(pos) == 0;
	}
	
	public boolean isCoding(int pos) {
		return positionsArray.getPos(pos) != 0;
	}

	
	private TranslatedAminoAcidPositions getTranslatedAminoAcidPositions() {
		if(translatedAminoAcidPos == null){
			translatedAminoAcidPos = new TranslatedAminoAcidPositions(this.positionsArray, this.readingFrame);	
		}
		return translatedAminoAcidPos;
	}
	
	/*
	public int getTranslatedAminAcidLength(int nucleotideLength){
		return getTranslatedAminAcidLength(nucleotideLength);
	}
	*/
	
	public int getAminoAcidPosFromNucleotidePos(int pos){
		TranslatedAminoAcidPositions aaPositions = getTranslatedAminoAcidPositions();
		return getTranslatedAminoAcidPositions().getAAPosAtNucleotidePos(pos);
	}
	
	public CodonPos getCodonAtNucleotidePos(int pos){
		return getTranslatedAminoAcidPositions().getCodonAtNucleotidePos(pos);
	}
	
	public CodonPos getCodonInTranslatedPos(int pos) {
		return getTranslatedAminoAcidPositions().getCodonAtTranslatedPos(pos);
	}

	public boolean isFullCodonStartingAt(int x) {
		boolean isFullCodon = false;

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

//		logger.info("isFull" + isFullCodon);
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
	

//	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted) {
//		return getAllNonCodingPositionsAsRanges(wanted, 0, getPositionsArray().getLength());
//	}
	
//	public ArrayList<IntRange> getAllCodingPositionsAsRanges(int wanted) {
//		return getAllNonCodingPositionsAsRanges(wanted, 0, getPositionsArray().getLength());
//	}
	
	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted, int startPos, int endPos) {
		ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
		int lastPos = -1;
		int firstPos = -1;
		
		
		// look at uninterupted ranges of the wanted codonInteger - since this in noncoding - they should be next to each other
		// loop all position three times, start in position 0 (offset) and after that start in pos 1 and pos 2
		
		logger.info("endpos"+endPos);
//		logger.info("arrayLen"+getPositionsArray().getLength());
		
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
	}

	public int getReadingFrame() {
		return readingFrame;
	}

	
	public ArrayList<Integer> getAllPositions(int wantedCodonPosInteger, int startPos, int endPosInclusive) {
		ArrayList<Integer> allPos = new ArrayList<Integer>();
		for(int n = startPos; n <= endPosInclusive; n++){
			if(getPositionsArray().get(n) == wantedCodonPosInteger){
				allPos.add(new Integer(n));
			}	
		}
		return allPos;
	}

	public void addRange(CodonRange range){
		positionsArray.addRange(range);
	}
	
	public void addRange(int start, int end, int firstVal) {
		positionsArray.addRange(new CodonRange(start, end, firstVal));
	}
	
	/*
	public void setPosition(int pos, int val) {
		if(pos >= 0){
			getPositionsArray().set(pos, val);
			positionsUpdated();
		}
	}
	*/
	
//	public void resize(int len) {
//		logger.info("len" + len);
//		logger.info(this.getPositionsArray().getLength());
//		logger.info("this.getTranslatedAminAcidLength()" + this.getTranslatedAminAcidLength());
//		this.getPositionsArray().resize(len);
//		positionsUpdated();
//		logger.info(this.getPositionsArray().getLength());
//		logger.info("this.getTranslatedAminAcidLength()" + this.getTranslatedAminAcidLength());
//	}
	
	public void positionsUpdated() {
		translatedAminoAcidPos = null;
	}

//	public String debug() {
//		StringBuilder sb = new StringBuilder(getPositionsArray().getLength());
//		for(int n = 0; n < getPositionsArray().getLength(); n++){
//			sb.append(positionsArray.getPos(n));
//		}
//		return sb.toString();
//	}

	public void reverse() {
		positionsArray.reverse();
		positionsUpdated();
	}
	
	public CodonPositions getCopy(){
		return new CodonPositions(this.positionsArray.getCopy(), this.readingFrame);		
	}

	public NexusRangePositionsArray getPositionsArray() {
		return positionsArray;
	}

	public void setPositionsArray(NexusRangePositionsArray positionsArray) {
		this.positionsArray = positionsArray;
		positionsUpdated();
	}

	public int getPosAt(int x){
		return this.positionsArray.getPos(x);
	}
	
//	public CodonPositions copyCodonPositionsWithExcludedRemoved(Excludes exset){
//
//		CodonPositions codonPosWithout = new CodonPositions(this.getPositionsArray().getLength() - exset.countExcludedSites());
//		
//		int posInNew = 0;
//		for(int n = 0; n < this.getPositionsArray().getLength(); n++){		
//			if(! exset.isExcluded(n)){
//				codonPosWithout.setPosition(posInNew, this.getPositionsArray().get(n));
//				posInNew ++;
//			}	
//		}
//		
//		codonPosWithout.positionsUpdated();
//		
//		return codonPosWithout;
//	}
	
//	public int getLength() {
//		if(positionsArray != null){
//			return positionsArray.getLength();
//		}else{
//			return 0;
//		}
//	}

	/*
	public void append(CodonPositions secondCodonPos) {
		positionsArray.append(getPositionsArray());
		positionsUpdated();
	}
	*/

	public void removePosition(int n) {
		this.positionsArray.remove(n);
		positionsUpdated();
	}
	
	public void insertPosition(int n) {
		this.positionsArray.insert(n);
		positionsUpdated();
	}

	public boolean isAnythingButDefault() {
		return positionsArray.isAnythingButDefault();
	}

	//
	// parse nexus indata
	//
	
	public void addNexusRanges(ArrayList<NexusRange> allRanges) {
		nexusRanges.addAll(allRanges);
	}
	
	//
	// end parse nexus data
	//

	public int[] translatePositions(int[] selection) {
		if(selection == null){
			return null;
		}
		
		int[] translated = new int[selection.length];
		for(int n = 0; n < selection.length; n++){
			translated[n] = getAminoAcidPosFromNucleotidePos(selection[n]);
		}
		return translated;
	}

	public int[] reTranslatePositions(int[] selection) {
		if(selection == null){
			return null;
		}
		
		ArrayList<Integer> reTranslated = new ArrayList<Integer>(selection.length * 3);
		for(int n = 0; n < selection.length; n++){
			CodonPos codon = getCodonInTranslatedPos(selection[n]);
			for(int i = codon.startPos; i <= codon.endPos; i++){
				reTranslated.add(new Integer(i));
			}
		}
		
		return ArrayUtils.toPrimitive(reTranslated.toArray(new Integer[0]));
	}

	public Rectangle reTranslatePositions(Rectangle bounds) {
		
		CodonPos codonX1 = getCodonInTranslatedPos(bounds.x);
		CodonPos codonX2 = getCodonInTranslatedPos((int)bounds.getMaxX());
		CodonPos codonY1 = getCodonInTranslatedPos(bounds.y);
		CodonPos codonY2 = getCodonInTranslatedPos((int)bounds.getMaxY());
		
		int width = codonX2.endPos - codonX1.startPos;
		int height = codonY2.endPos - codonY2.startPos;
		
		Rectangle trans = new Rectangle(codonX1.startPos, codonY1.startPos, width, height);
		
		return trans;
	}

	public void addRanges(Ranges allRanges) {
		positionsArray.setBackend(allRanges);
		positionsUpdated();
	}


	
}