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
	
	
	public AlignmentMeta(int alignmentLength){
		this(new Excludes(alignmentLength), new CodonPositions(alignmentLength), new ArrayList<CharSet>());
	}

	public AlignmentMeta(Excludes excludes, CodonPositions codonPos, ArrayList<CharSet> charsets) {
		this.excludes = excludes;
		this.codonPositions = codonPos;
		this.charsets = charsets;
	}

	public boolean isMetaOutputNeeded(){
		return true;
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
			if(this.excludes.isExcluded(n)){

			}else{
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
				for(int n = excludes.getLength() - 1; n>= 0; n--){
					if(deleteMask[n] == true){
						excludes.removePosition(n);
						codonPositions.removePosition(n);
						for(CharSet charset: charsets){
							charset.removePosition(n); 
						}
					}		
				}
	}


	public void excludePosition(int i) {
		this.excludes.getPositionsBooleanArray()[i] = true;
		
	}


	public ArrayList<CharSet> getCharsets() {
		return this.charsets;
	}


}
