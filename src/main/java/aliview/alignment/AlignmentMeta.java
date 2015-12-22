package aliview.alignment;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import aliview.GeneticCode;
import utils.nexus.CharSet;
import utils.nexus.CharSets;
import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;
import utils.nexus.Excludes;

public class AlignmentMeta {
	private static final Logger logger = Logger.getLogger(AlignmentMeta.class);
	private Excludes excludes;
	private CodonPositions codonPositions;
	private CharSets charsets;
	private GeneticCode geneticCode;

	
	public AlignmentMeta(){
		this(GeneticCode.DEFAULT);
	}

	public AlignmentMeta(GeneticCode genCode){
		this(new Excludes(), new CodonPositions(), new CharSets(), genCode);
	}

	public AlignmentMeta(Excludes excludes, CodonPositions codonPos, CharSets charsets, GeneticCode genCode) {
		this.excludes = excludes;
		this.codonPositions = codonPos;
		this.charsets = charsets;
		this.geneticCode = genCode;
	}

	public boolean isMetaOutputNeeded(){
		boolean isMetaNeeded = false;
		if(excludes.containsAnyPosition()){
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

	public void reverse(int length) {
		this.getExcludes().reverse(length);
		this.getCodonPositions().reverse(length);
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
		return new AlignmentMeta(excludes.getCopy(), codonPositions.getCopy(), charsets.getCopy(), this.geneticCode);
	}

	public ArrayList<Integer> getAllCodonPositions(int wanted, boolean removeExcluded, int startPos, int endPosInclusive) {
		ArrayList<Integer> positions = codonPositions.getAllPositions(wanted, startPos, endPosInclusive);
		
		// delete positions from list if they are excluded
		if(removeExcluded){
			Iterator<Integer> iter = positions.iterator();
			while(iter.hasNext()){
				Integer pos = iter.next();
				if(isExcluded(pos.intValue())){
					iter.remove();
				}	
			}
		}

		return positions;
	}


	public void deleteFromMask(boolean[] deleteMask) {
		// Null check
		if(deleteMask == null || deleteMask.length == 0){
			return;
		}

		// remove reverse
		for(int n = deleteMask.length - 1; n>= 0; n--){
			if(deleteMask[n] == true){
				deletePosition(n);
			}		
		}
	}
	
	public void deletePosition(int n) {	
		excludes.deletePosition(n);
		codonPositions.deletePosition(n);
		charsets.deletePosition(n);
	}
	
	public void insertPosition(int n) {	
		excludes.insertPosition(n);
		codonPositions.insertPosition(n);
		charsets.insertPosition(n);
		
	}
	
	public void excludePositions(int start, int end) {
		for(int n = start; n <= end; n++){
			this.excludes.set(n, true);
		}
	}
	
	public void excludeRange(int start, int stop) {
		this.excludes.addRange(start, stop);
	}
	
	public void removeExcludeRange(int start, int stop) {
		this.excludes.clearRange(start, stop);
		
	}
	
	
	public boolean excludesIntersectsPositions(int start, int end) {
		for(int n = start; n <= end; n++){
			if(this.excludes.isExcluded(n)){
				return true;
			}
		}
		return false;
	}

	public CharSets getCharsets() {
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

	public void setCharsets(CharSets charsets) {
		this.charsets = charsets;
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
