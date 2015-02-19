package utils.nexus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;

public class Ranges {
	private static final Logger logger = Logger.getLogger(Ranges.class);
	private ArrayList<CodonRange> backend;

	public Ranges() {
		this(new ArrayList<CodonRange>());
	}
	
	public Ranges(ArrayList<CodonRange> backend) {
		this.backend = backend;
	}

	public void add(CodonRange additionalRange) {
		
		logger.info("add=" + additionalRange);
		
		// first remove completely containing ones
		ArrayList<CodonRange> toDelete = new ArrayList<CodonRange>();
		for(CodonRange range: backend){
			if(additionalRange.containsRange(range)){
				toDelete.add(range);
			}
		}
		
		backend.removeAll(toDelete);
		
		// if it within merge if same frame, otherwise cut out
		ArrayList<CodonRange> additionalParts = new ArrayList<CodonRange>();
		for(CodonRange range: backend){
			if(additionalRange.within(range)){
				// if same reading frame then skip additional
				if(additionalRange.startVal == range.getPosVal(additionalRange.start)){
					return;
				}
				else{
					CodonRange restPart = range.cutOut(additionalRange);
					additionalParts.add(restPart);
				}
			}
		}
		
		backend.addAll(additionalParts);
		
		
		// now adjust the existing ones making new one fit in between
		// or merge together with overlapping existing if same reading frame
		ArrayList<CodonRange> toRemove = new ArrayList<CodonRange>();
		for(CodonRange range: backend){
			if(additionalRange.intersects(range)){
				// if same reading frame then merge and remove the overlapping
				if(additionalRange.startVal == range.getPosVal(additionalRange.start)){
					additionalRange.merge(range);
					toRemove.add(range);
				}
				// if not same reading frame then crop
				else{
					range.crop(additionalRange);
				}
			}
		}
		// remove overlapping and same frame
		backend.removeAll(toRemove);
		// and add the new one
		backend.add(additionalRange);
		
		// and sort it
		Collections.sort(backend);
		
		this.debug();
			
	}
	

	
	public int nucPosFromAAPos(int codonPos, int readingFrame) {
		int codonOffset = 0;
		int nucPos = -1;
		
		for(CodonRange range: backend){
			if(range.containsCodonPos(codonPos, codonOffset, readingFrame)){
				nucPos = range.getPosAtCodonPos(codonPos - codonOffset, readingFrame);
				break;
			}else{
				codonOffset = codonOffset + range.countAllCodons(readingFrame);
			}
		}
		return nucPos;
	}
	
	public int aaPosFromNucPos(int pos, int readingFrame) {
		int aaPosCount = 0;
		for(CodonRange range: backend){
			if(range.contains(pos)){
				aaPosCount = aaPosCount + range.countCodonsUntil(pos, readingFrame);
				break;
			}else{
				aaPosCount = aaPosCount + range.countAllCodons(readingFrame);
			}
		}
		return aaPosCount - 1; // because first pos is 0 and position is one less than count
	}
	
	
	public void debug() {
		int count = 0;
		for(CodonRange range: backend){
//			logger.info("count=" + count + " " + range.toString());
//			logger.info("codoncount = " + range.countAllCodons(1));
			count ++;
		}
	}

	public CodonRange getRange(int pos){
		
		for(int n = this.backend.size() - 1; n >= 0; n--){
			CodonRange range = backend.get(n);
			if(range.contains(pos)){
				return range;
			}
		}	
		// should not happen
		//return null;
		return new CodonRange(0, Integer.MAX_VALUE, 1);
	}

	public Ranges getCopy() {
		logger.info("");
		ArrayList<CodonRange> copy = new ArrayList<CodonRange>(backend.size());
		for(CodonRange range: backend){
			copy.add(range.getCopy());
		}
		return new Ranges(copy);
	}

	public int size() {
		return backend.size();
	}

}
