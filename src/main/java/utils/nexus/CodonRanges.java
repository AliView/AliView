package utils.nexus;

import org.apache.log4j.Logger;


public class CodonRanges{
	private static final Logger logger = Logger.getLogger(CodonRanges.class);
	private Ranges backend = new Ranges();


	public CodonRanges() {
		logger.info("new");
		backend.addRange(CodonRange.newDefaultRange());
	}
	
	private CodonRanges(Ranges ranges) {
		backend = ranges;
	}
	
	public int getPosVal(int pos){
		CodonRange range = (CodonRange) backend.getRange(pos);
		if(range != null){
			return range.getPosVal(pos);
		}else{
			return 0;
		}
	}
	
	public CodonRanges getCopy() {
		Ranges copy = new Ranges();
		for(Range range: backend){
			CodonRange codonRange = (CodonRange) range;
			copy.addRange(codonRange.getCopy());	
		}
		return new CodonRanges(copy);
	}

	public void addRange(CodonRange range){
		logger.info("addRange");
		backend.addRange(range);
	}
	

	private boolean isBackendAnythingBut123(){
		return backend.size() != 1;
	}
	
	public boolean isAnythingButDefault() {
		return backend.size() != 1;
	}

	public void reverse(int length) {
		backend.reverse(length);
	}

	public void removePosition(int n) {
		backend.deletePosition(n);
		
	}

	public void insert(int n) {
		backend.insertPosition(n);
	}


	public void setBackend(Ranges allRanges) {
		backend = allRanges;
	}
	
	
	//
	//
	//	CodonRanges
	//
	//
	
	public int nucPosFromAAPos(int codonPos, int readingFrame) {
		int codonOffset = 0;
		int nucPos = -1;
		
		for(Range range: backend){
			CodonRange codonRange = (CodonRange) range;
			if(codonRange.containsCodonPos(codonPos, codonOffset, readingFrame)){
				nucPos = codonRange.getPosAtCodonPos(codonPos - codonOffset, readingFrame);
				break;
			}else{
				codonOffset = codonOffset + codonRange.countAllCodons(readingFrame);
			}
		}
		return nucPos;
	}
	
	public int aaPosFromNucPos(int pos, int readingFrame) {
		int aaPosCount = 0;
		for(Range range: backend){
			CodonRange codonRange = (CodonRange) range;
			if(range.contains(pos)){
				aaPosCount = aaPosCount + codonRange.countCodonsUntil(pos, readingFrame);
				break;
			}else{
				aaPosCount = aaPosCount + codonRange.countAllCodons(readingFrame);
			}
		}
		return aaPosCount - 1; // because first pos is 0 and position is one less than count
	}

	public int size() {
		return backend.size();
	}

}
