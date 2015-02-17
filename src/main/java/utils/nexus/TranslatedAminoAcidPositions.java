package utils.nexus;

import java.util.ArrayList;

import org.apache.log4j.Logger;


public class TranslatedAminoAcidPositions {
	private static final Logger logger = Logger.getLogger(TranslatedAminoAcidPositions.class);
	private NexusRangePositionsArray positionsArray;
	private int readingFrame = 1;
	
	public TranslatedAminoAcidPositions(NexusRangePositionsArray positionsArray, int readingFrame) {
		this.positionsArray = positionsArray;
		this.readingFrame = readingFrame;
	}

	
	private CodonPos getTranslatedAminoAcidPositionsAt(int x){

		CodonPos pos = null;
			
			if(isFullCodingCodonStartingAt(x)){
				pos = new CodonPos(x,  x + 2, true);

			}
			else if(isFullCodingCodonStartingAt(x + 1)){
				// noncoding orphan
				pos = new CodonPos(x, x, false);

			}
			else if(isFullCodingCodonStartingAt(x + 2)){
				// noncoding orphan
				pos = new CodonPos(x, x + 1, false);

			}
			else{
				// noncoding triplet
				pos = new CodonPos(x, x + 2, false);
			}		

			return pos;

	}
	
	public boolean isFullCodingCodonStartingAt(int x) {
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

		return isFullCodon;
	}

	private int getReadingFrame() {
		return readingFrame;
	}

	private NexusRangePositionsArray getPositionsArray() {
		return positionsArray;
	}
	
	public CodonPos getCodonAtNucleotidePos(int pos) {
		return getTranslatedAminoAcidPositionsAt(pos);
	}
		
	public int getAAPosAtNucleotidePos(int pos){	
		int aaPos = getPositionsArray().aaPosFromNucPos(pos, readingFrame);
//		logger.info("pos=" + pos + " aaPos=" + aaPos);
		return aaPos;
	}

	 public CodonPos getCodonAtTranslatedPos(int pos) {
		 int nucPos = getPositionsArray().nucPosFromAAPos(pos, readingFrame);
		// logger.info("nucPos" + nucPos + " searchAAPos=" + pos + " readingFrame=" + readingFrame);
		 CodonPos codpos = getCodonAtNucleotidePos(nucPos);
		// logger.info("codpos.start" + codpos.startPos + " codpos.end" + codpos.endPos + " codPos.isorf" + codpos.isOrfan());
		 return getCodonAtNucleotidePos(nucPos);
	 }
	 
}
