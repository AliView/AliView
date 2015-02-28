package aliview.alignment;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import aliview.GeneticCode;
import utils.nexus.CharSet;
import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;
import utils.nexus.Excludes;

public class AlignmentMeta {
	private static final Logger logger = Logger.getLogger(AlignmentMeta.class);
	private Excludes excludes;
	private CodonPositions codonPositions;
	private ArrayList<CharSet> charsets;
	private GeneticCode geneticCode;

	
	public AlignmentMeta(){
		this(GeneticCode.DEFAULT);
	}

	public AlignmentMeta(GeneticCode genCode){
		this(new Excludes(), new CodonPositions(), new ArrayList<CharSet>(), genCode);
	}

	public AlignmentMeta(Excludes excludes, CodonPositions codonPos, ArrayList<CharSet> charsets, GeneticCode genCode) {
		this.excludes = excludes;
		this.codonPositions = codonPos;
		this.charsets = charsets;
		this.geneticCode = genCode;
	}

	public boolean isMetaOutputNeeded(){
		boolean isMetaNeeded = false;
		if(excludes.isAnythingExcluded()){
			isMetaNeeded = true;
		}
		if(codonPositions.isAnythingButDefault()){
			isMetaNeeded = true;
		}
		if(charsets.size() > 0){
			isMetaNeeded = true;
		}
		return isMetaNeeded;
	}

	public Excludes getExcludes() {
		return this.excludes;
	}

	public CodonPositions getCodonPositions() {
		return this.codonPositions;
	}

	public boolean isFullCodonStartingAt(int x) {
		return this.codonPositions.isFullCodonStartingAt(x);
	}

	public void reverse() {
		this.getExcludes().reverse();
		this.getCodonPositions().reverse();
	}

	public int countIncludedPositionsBefore(int minimumInteger) {
		int count = 0;
		for(int n = 0; n < minimumInteger; n++){
			if(! this.excludes.isExcluded(n)){
				count ++;
			}
		}
		return count;
	}

	public void setReadingFrame(int readingFrame) {
		this.codonPositions.setReadingFrame(readingFrame);
	}


	public boolean isExcluded(int x) {
		return this.excludes.isExcluded(x);
	}


	public int getCodonPosAt(int x) {
		return codonPositions.getPosAt(x);
	}


	public AlignmentMeta getCopy() {
		ArrayList<CharSet> copyOfCharsets = new ArrayList<CharSet>();
		for(CharSet charset: this.charsets){
			copyOfCharsets.add(charset.getCopy());
		}
		return new AlignmentMeta(excludes.getCopy(), codonPositions.getCopy(), copyOfCharsets, this.geneticCode);
	}

	public ArrayList<Integer> getAllCodonPositions(int wanted, boolean removeExcluded, int startPos, int endPosInclusive) {
		ArrayList<Integer> positions = codonPositions.getAllPositions(wanted, startPos, endPosInclusive);
		if(removeExcluded){
			excludes.removeExcludedPositionsFromList(positions);
		}

		return positions;
	}


	public void removeFromMask(boolean[] deleteMask) {
		// Null check
		if(deleteMask == null || deleteMask.length == 0){
			return;
		}

		// remove reverse
		for(int n = deleteMask.length - 1; n>= 0; n--){
			if(deleteMask[n] == true){
				removePosition(n);
			}		
		}
	}
	
	public void removePosition(int n) {	
		excludes.removePosition(n);
		if(codonPositions.size() != 0){
			codonPositions.removePosition(n);
			for(CharSet charset: charsets){
				charset.removePosition(n); 
			}
		}
	}
	
	public void insertPosition(int n) {	
		excludes.insertPosition(n);
		codonPositions.insertPosition(n);
		// dont do anything with charset
//		for(CharSet charset: charsets){
//			charset.removePosition(n); 
//		}
	}
	
	public void excludePositions(int start, int end) {
		for(int n = start; n <= end; n++){
			this.excludes.set(n, true);
		}
	}
	
	public void excludesRemovePositions(int start, int end) {
		for(int n = start; n <= end; n++){
			this.excludes.set(n, false);
		}
	}
	
	public boolean excludesIntersectsPositions(int start, int end) {
		for(int n = start; n <= end; n++){
			if(this.excludes.isExcluded(n)){
				return true;
			}
		}
		return false;
	}
	

	public ArrayList<CharSet> getCharsets() {
		return this.charsets;
	}

	public int[] translatePositions(int[] selection) {
		return codonPositions.translatePositions(selection);
	}
	
	public int[] reTranslatePositions(int[] selection) {
		return codonPositions.reTranslatePositions(selection);
	}

	public Rectangle reTranslatePositions(Rectangle bounds) {
		return codonPositions.reTranslatePositions(bounds);
	}

	public GeneticCode getGeneticCode() {
		return geneticCode;
	}

	public void setGeneticCode(GeneticCode genCode) {
		this.geneticCode = genCode;
	}

	/*
	public boolean verifyLength(int len){
		boolean chandged = false;
		if(excludes != null){
			if(excludes.getLength() != len){
//				excludes.insertPosition(excludes.getLength());
			}
		}
		if(codonPositions != null){
			if(codonPositions.getLength() != len){
				//codonPositions.insertPosition(codonPositions.getLength());
				codonPositions.resize(len);
				chandged = true;
			}
		}
		
		return chandged;
	}
	*/


}
