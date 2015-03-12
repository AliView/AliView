package utils.nexus;

import java.util.ArrayList;
import org.apache.log4j.Logger;

public class NexusRangesTranslator {
		
	private static final Logger logger = Logger.getLogger(NexusRangesTranslator.class);
	private ArrayList<NexusRange> nexusRanges = new ArrayList<NexusRange>();

	public void addNexusRanges(ArrayList<NexusRange> allRanges) {
		nexusRanges.addAll(allRanges);
	}
	
	public Ranges convertToCodonRanges(){
		Ranges allRanges = new Ranges();
		allRanges.addRange(CodonRange.newDefaultRange());

		int min = getMinPos() - 1; // minus one because nexus index starts at 1
		int max = getMaxPos() - 1; // minus one because nexus index starts at 1
		int startVal = getPosVal(min -1);
		
		CodonRange codonRange = new CodonRange(min - 1, max - 1, startVal);
//		logger.info("codonRange" + codonRange);
		
		int defaultStartVal = 1;
		int startValOffset = startVal - defaultStartVal; // one is default startVal
		
		for(int n = min; n <= max; n++){ // minus one because Nexus 
				
			int target = (n % 3) + 1;
			
			int targetModifiedWithStartValOffset = target + startValOffset;
			if(targetModifiedWithStartValOffset > 3){
				targetModifiedWithStartValOffset = targetModifiedWithStartValOffset - 3;
			}
			
//			logger.info("n=" + n + " startVal=" + startVal + " target=" + target + "getPosVal(n)=" + getPosVal(n));
			
			startVal = getPosVal(n);
			if(startVal != targetModifiedWithStartValOffset){
				

				if(startVal == 0 && codonRange.startVal == 0){
					// do nothing - same as before
				}
				else{
					codonRange.end = n - 1; // la
					
					
					codonRange = new CodonRange(n, max, startVal);
					allRanges.addRange(codonRange);
					
					startValOffset = startVal - target;
					if(startValOffset < 0){
						startValOffset = startValOffset + 3;
					}

				}
			}
			
			
		}
		allRanges.debug();
		return allRanges;
	}
	
	public Ranges convertToCharsetRanges(){
		Ranges allRanges = new Ranges();
		allRanges.addRange(CodonRange.newDefaultRange());

		int min = getMinPos() - 1; // minus one because nexus index starts at 1
		int max = getMaxPos() - 1; // minus one because nexus index starts at 1
		int startVal = getPosVal(min -1);
		
		CodonRange codonRange = new CodonRange(min - 1, max - 1, startVal);
//		logger.info("codonRange" + codonRange);
		
		int defaultStartVal = 1;
		int startValOffset = startVal - defaultStartVal; // one is default startVal
		
		for(int n = min; n <= max; n++){ // minus one because Nexus 
				
			int target = (n % 3) + 1;
			
			int targetModifiedWithStartValOffset = target + startValOffset;
			if(targetModifiedWithStartValOffset > 3){
				targetModifiedWithStartValOffset = targetModifiedWithStartValOffset - 3;
			}
			
//			logger.info("n=" + n + " startVal=" + startVal + " target=" + target + "getPosVal(n)=" + getPosVal(n));
			
			startVal = getPosVal(n);
			if(startVal != targetModifiedWithStartValOffset){
				

				if(startVal == 0 && codonRange.startVal == 0){
					// do nothing - same as before
				}
				else{
					codonRange.end = n - 1; // la
					
					
					codonRange = new CodonRange(n, max, startVal);
					allRanges.addRange(codonRange);
					
					startValOffset = startVal - target;
					if(startValOffset < 0){
						startValOffset = startValOffset + 3;
					}

				}
			}
			
			
		}
		allRanges.debug();
		return allRanges;
	}
	

	private int getMinPos() {
		int minVal = Integer.MAX_VALUE;
		
		for(NexusRange range : nexusRanges){
			minVal = Math.min(minVal, range.getMinimumInt());
		}
		return minVal;	
	}
	
	private int getMaxPos() {
		int maxVal = Integer.MIN_VALUE;
		
		for(NexusRange range : nexusRanges){
			maxVal = Math.max(maxVal, range.getMaximumInt());
		}
		return maxVal;	
	}
	
	private int getPosVal(int n) {
		
		n = n + 1; // nexus ranges are using index 1 as first pos
		
		for(NexusRange range : nexusRanges){
			if(n >= range.getMinimumInt() && n <= range.getMaximumInt()){
				
				int posInRange = n - range.getMinimumInt();
				int rest = (posInRange) % range.getSteps();
				
//				logger.info("rest=" + rest);
				if(rest == 0){
	//				logger.info("n=" + n + " range.positionVal=" + range.positionVal);
					return range.positionVal;
				}

			}
		}
		// default to 0 for positions not in range
		return 0;
	}
	
	

}
