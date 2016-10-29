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
	private CodonRanges codonRanges;
	private int readingFrame;
	private TranslatedAminoAcidPositions translatedAminoAcidPos;
	
	public CodonPositions(){
		this(new CodonRanges(), 1);
	}
	
	public int size() {
		return codonRanges.size();
	}

	private CodonPositions(CodonRanges positionsArray, int readingFrame) {
		this.setCodonRandes(positionsArray);
		this.readingFrame = readingFrame;	
	}

	public boolean isNonCoding(int pos){
		return codonRanges.getPosVal(pos) == 0;
	}
	
	public boolean isCoding(int pos) {
		return codonRanges.getPosVal(pos) != 0;
	}

	
	private TranslatedAminoAcidPositions getTranslatedAminoAcidPositions() {
		if(translatedAminoAcidPos == null){
			translatedAminoAcidPos = new TranslatedAminoAcidPositions(this.codonRanges, this.readingFrame);	
		}
		return translatedAminoAcidPos;
	}
	
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
				if(getCodonRanges().getPosVal(x) == 1 && getCodonRanges().getPosVal(x+1) == 2 && getCodonRanges().getPosVal(x+2) == 3){
					isFullCodon = true;
				}
			}
			
			if(getReadingFrame() == 2){
				if(getCodonRanges().getPosVal(x) == 2 && getCodonRanges().getPosVal(x+1) == 3 && getCodonRanges().getPosVal(x+2) == 1){
					isFullCodon = true;
				}
			}
			
			if(getReadingFrame() == 3){
				if(getCodonRanges().getPosVal(x) == 3 && getCodonRanges().getPosVal(x+1) == 1 && getCodonRanges().getPosVal(x+2) == 2){
					isFullCodon = true;
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
	
	
	public ArrayList<IntRange> getAllNonCodingPositionsAsRanges(int wanted, int startPos, int endPos) {
		ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
		int lastPos = -1;
		int firstPos = -1;
		
		
		// look at uninterupted ranges of the wanted codonInteger - since this in noncoding - they should be next to each other
		// loop all position three times, start in position 0 (offset) and after that start in pos 1 and pos 2
		
		logger.info("endpos"+endPos);
//		logger.info("arrayLen"+getPositionsArray().getLength());
		
			for(int n = startPos; n < endPos; n++){
				if(getCodonRanges().getPosVal(n) == wanted){
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
	
	
	public ArrayList<IntRange> getAllCodingPositionsAsIntRanges(int wanted, int startPos, int endPos) {
		ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
		int lastPos = -1;
		int firstPos = -1;
		
		
		// look at uninterupted ranges of the wanted codonInteger at every third position
		// loop all position three times, start in position 0 (offset) and after that start in pos 1 and pos 2
		
		for(int offset = 0; offset <= 2; offset++){
			for(int n = startPos + offset; n < endPos; n = n +3){
				if(getCodonRanges().getPosVal(n) == wanted){
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
		positionsUpdated();
	}

	public int getReadingFrame() {
		return readingFrame;
	}

	
	public ArrayList<Integer> getAllPositions(int wantedCodonPosInteger, int startPos, int endPosInclusive) {
		ArrayList<Integer> allPos = new ArrayList<Integer>();
		for(int n = startPos; n <= endPosInclusive; n++){
			if(getCodonRanges().getPosVal(n) == wantedCodonPosInteger){
				allPos.add(new Integer(n));
			}	
		}
		return allPos;
	}

	public void addRange(CodonRange range){
		codonRanges.addRange(range);
	}
	
	public void addRange(int start, int end, int firstVal) {
		codonRanges.addRange(new CodonRange(start, end, firstVal));
	}
	
	public void positionsUpdated() {
		translatedAminoAcidPos = null;
	}

	public void reverse(int length) {
		codonRanges.reverse(length);
		positionsUpdated();
	}
	
	public CodonPositions getCopy(){
		return new CodonPositions(this.codonRanges.getCopy(), this.readingFrame);		
	}

	public CodonRanges getCodonRanges() {
		return codonRanges;
	}

	public void setCodonRandes(CodonRanges codonRanges) {
		this.codonRanges = codonRanges;
		positionsUpdated();
	}

	public int getPosAt(int x){
		return this.codonRanges.getPosVal(x);
	}

	public void deletePosition(int n) {
		this.codonRanges.removePosition(n);
		positionsUpdated();
	}
	
	public void insertPosition(int n) {
		this.codonRanges.insert(n);
		positionsUpdated();
	}

	public boolean isAnythingButDefault() {
		return codonRanges.isAnythingButDefault();
	}

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
		codonRanges.setBackend(allRanges);
		positionsUpdated();
	}


	
}