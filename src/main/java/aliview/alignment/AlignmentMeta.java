package aliview.alignment;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import utils.nexus.CharSet;
import utils.nexus.CodonPositions;
import utils.nexus.Excludes;

public class AlignmentMeta {
	private static final Logger logger = Logger.getLogger(AlignmentMeta.class);
	private Excludes excludes = new Excludes(0);
	private CodonPositions codonPositions = new CodonPositions(0);
	private ArrayList<CharSet> charsets = new ArrayList<CharSet>();

	public AlignmentMeta(){
		this(0);
	}

	public AlignmentMeta(int alignmentLength){
		//this(new Excludes(alignmentLength), new CodonPositions(alignmentLength), new ArrayList<CharSet>());
		this(new Excludes(), new CodonPositions(alignmentLength), new ArrayList<CharSet>());
	}

	public AlignmentMeta(Excludes excludes, CodonPositions codonPos, ArrayList<CharSet> charsets) {
		this.excludes = excludes;
		this.codonPositions = codonPos;
		this.charsets = charsets;
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
		return new AlignmentMeta(excludes.getCopy(), codonPositions.getCopy(), copyOfCharsets);
	}

	public ArrayList<Integer> getAllCodonPositions(int wanted, boolean removeExcluded) {
		ArrayList<Integer> positions = codonPositions.getAllPositions(wanted);
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
		codonPositions.removePosition(n);
		for(CharSet charset: charsets){
			charset.removePosition(n); 
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
	


	public void excludePosition(int i) {
		this.excludes.getPositionsBooleanArray()[i] = true;

	}


	public ArrayList<CharSet> getCharsets() {
		return this.charsets;
	}

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


}
