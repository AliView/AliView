package aliview.primer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;

public class Dimer {

	private static int minDimerReportLength = 5;
	private static final int NUMBER_OF_ONE_BASE_GAPS_ALLOWED = 1;

	public static void main(String[] args) {

		String seq1 =   "CCCATGGGGTGTGCAAGTTCGTTGTG";		           
		String seq2 =                         "ACACWGCAACTTGCACACCATA";

		//new Dimer(seq1,seq2).get3EndDimer();
		new Dimer(seq1,seq2).getAllDimers();
	}

	private static final Logger logger = Logger.getLogger(Dimer.class);
	String sequence1;
	String sequence2;

	public Dimer(String seq1, String seq2) {
		super();
		this.sequence1 = seq1;
		this.sequence2 = seq2;
	}


	public static int getDimerLengthThreashold() {
		return minDimerReportLength;

	}

	public ArrayList<DimerResult> get3EndDimer(){

		ArrayList<DimerResult> end3Dimers = new ArrayList<DimerResult>();
		ArrayList<DimerResult> allDimers = getAllDimers(this.sequence1, this.sequence2, getDimerLengthThreashold(), NUMBER_OF_ONE_BASE_GAPS_ALLOWED);
		for(DimerResult dimer: allDimers){
			if(dimer.is3EndDimer()){
				end3Dimers.add(dimer);
			}
		}

		/*
		for(DimerResult dimer: end3Dimers){
			logger.info("3 end dimerlen=" + dimer.getDimerLength());
			for(String dimerTextLine:dimer.getDimerAsText()){
				logger.info(dimerTextLine);
			}
		}
		 */



		return end3Dimers;
	}


	public ArrayList<DimerResult> getAllDimers(){
		ArrayList<DimerResult> allDimers = getAllDimers(this.sequence1, this.sequence2, getDimerLengthThreashold(), NUMBER_OF_ONE_BASE_GAPS_ALLOWED);

		/*
		for(DimerResult dimer: allDimers){
			logger.info("dimerlength:" + dimer.getDimerLength());
			for(String dimerTextLine:dimer.getDimerAsText()){
				logger.info(dimerTextLine);
			}
			logger.info("");
		}
		 */

		return allDimers;
	}

	public int get3EndDimerMaxLength() {
		ArrayList<DimerResult> all3Dimers = get3EndDimer();
		int maxLen = 0;
		for(DimerResult dimer: all3Dimers){
			maxLen = Math.max(dimer.getDimerLength(), maxLen);
		}
		return maxLen;
	}

	public int getDimerMaxLength() {
		ArrayList<DimerResult> allDimers = getAllDimers();
		int maxLen = 0;
		for(DimerResult dimer: allDimers){
			maxLen = Math.max(dimer.getDimerLength(), maxLen);
		}
		return maxLen;
	}

	public ArrayList<String> getAllDimersAsText(){
		ArrayList<DimerResult> allDimers = getAllDimers();
		ArrayList<String> allText = new ArrayList<String>();
		for(DimerResult dimer: allDimers){
			String[] dimerText = dimer.getDimerAsText();
			for(String text: dimerText){
				allText.add(text);
			}
			// two blank rows
			allText.add("");
			allText.add("");
		}
		return allText;
	}


	/*
	 private int get3EndDimer(String seq1, String seq2){
		int max3Size = 0;
		DimerResult dimerResult = null;
		// todo very ugly create a dimer result
		DimerResult maxDimerResult = null;
		// loop seq 1 backwards and check how long uninterrupted stack there is
		for(int n1 = seq1.length() -1; n1 >= 0 ; n1--){

			int overlap = seq1.length() -n1;
			logger.info("overlap" + overlap);
			int size3end = 0;

			// Start a new DimerResult
			dimerResult = new DimerResult(seq1.length()-1,0);

			// overlap -1 because of index 0 in string
			for(int n2 = overlap - 1; n2 >= 0 && n2 < seq2.length(); n2--){

				logger.info("n1=" + n1);
				logger.info("n2=" + n2);
				logger.info("seq1.length()" + seq1.length());

				char n1Char = seq1.charAt(n1 + n2);
				char n2Char = seq2.charAt(n2);

				dimerResult.setPrimer1EndPos(n1 + n2);
				dimerResult.setPrimer2EndPos(n2);

				int charDimerVal = NucleotideUtilities.getDimerBinding(n1Char, n2Char);	
				if(charDimerVal >= 1){
					dimerResult.addCharDimerValue(charDimerVal);
					size3end ++;
				}
				// stacking is interrupted by one non-stacking pos
				else{
					// check how many interruptions is allowed or if it is time to break
					if(false){

					}
					else{
						// stacking is interrupted by a non-binding-pos, since it is only 3end value we want clear size
						size3end = 0;
						break;
					}
				}				
			}
			// it is finished
			if(maxDimerResult == null || maxDimerResult.getDimerLength() < dimerResult.getDimerLength()){
				logger.info("newDimerResult");
				maxDimerResult = dimerResult;
			}


			//DimerResult end3Dimer = new DimerResult(primer1StartPos, primer1EndPos, primer2StartPos, primer2EndPos)

			max3Size = Math.max(max3Size, size3end);

		}

		logger.info("3\"-dimer-stackSize: " + max3Size);
		logger.info("maxDimerResult.getDimerLength()" + maxDimerResult.getDimerLength());
		logger.info("maxDimerResult.getPrimer1StartPos()" + maxDimerResult.getPrimer1StartPos());
		logger.info("maxDimerResult.getPrimer1EndPos()" + maxDimerResult.getPrimer1EndPos());

		return max3Size;
	}

	 */


	/*

	 private ArrayList<DimerResult> getAny3Dimer(String seq1, String seq2, int minDimerLen, int numberOfOneBaseGapsAllowed){
			int maxDimerSize = 0;
			ArrayList<DimerResult> allDimers = new ArrayList<DimerResult>();
			DimerResult dimerResult = null;



			int n1Start = seq1.length();
			int n2Start = 0;


			n1Start --;
			n2Start ++;

			offset = 40

			seq1.charAt(n1Start + offset);
			seq2.charAt(n2Start + offset);

			// todo very ugly create a dimer result
			// loop seq 1 backwards and check how long uninterrupted stack there is
			for(int n1 = seq1.length() -1; n1 > - seq2.length() ; n1--){

				int overlap = seq1.length() -n1;
				logger.info("overlap" + overlap);
				int dimerSize = 0;
				int numberOfOneBaseGapsFound = 0;

				// overlap -1 because of index 0 in string
				for(int n2 = overlap -1; n2 >= 0 && n2 < seq2.length(); n2--){

					logger.info("n1=" + n1);
					logger.info("n2=" + n2);
					logger.info("seq1.length()" + seq1.length());

					int primer1CharPos = n1 + n2;
					int primer2CharPos = n2;

					// set default non characters
					char n1Char = '-';
					char n2Char = '-';
					if(primer1CharPos >= 0 && primer1CharPos < seq1.length()){
						n1Char = seq1.charAt(primer1CharPos);
					}
					if(primer2CharPos >= 0 && primer2CharPos < seq2.length()){
						n2Char = seq2.charAt(primer2CharPos);
					}


					int charDimerVal = NucleotideUtilities.getDimerBinding(n1Char, n2Char);	
					if(charDimerVal >= 1){
						// start a new dimerResult 
						if(dimerResult == null){
							dimerResult = new DimerResult(seq1, seq2);
							dimerResult.setDimerStartPos(primer1CharPos, primer2CharPos);
						}
						dimerResult.setPrimer1EndPos(primer1CharPos);
						dimerResult.setPrimer2EndPos(primer2CharPos);

						logger.info("Dimer" + dimerResult.getDimerLength());

						dimerResult.addCharDimerValue(charDimerVal);
						dimerSize ++;
					}
					// stacking is interrupted by one non-stacking pos
					else{
						// check how many interruptions is allowed or if it is time to break
						if(numberOfOneBaseGapsAllowed > numberOfOneBaseGapsFound){
							numberOfOneBaseGapsFound ++;
						}
						else{
							// stacking is interrupted by a non-binding-pos
							// save dimer result and start a new one
							if(dimerResult != null && dimerResult.getDimerLength() >= minDimerLen){
								allDimers.add(dimerResult);
							}
							dimerResult = null;
							dimerSize = 0;
							numberOfOneBaseGapsFound = 0;
							//(break if only 3")
						}
					}				
				}


				maxDimerSize = Math.max(maxDimerSize, dimerSize);

			}

			for(DimerResult dimer: allDimers){
				logger.info("dimerlen=" + dimer.getDimerLength());
			}

			logger.info("3\"-dimer-stackSize: " + maxDimerSize);





			return allDimers;

	 }

	 */
	private ArrayList<DimerResult> getAllDimers(String seq1, String seq2, int minDimerLen, int numberOfOneBaseGapsAllowed){
		ArrayList<DimerResult> allDimers = new ArrayList<DimerResult>();



		// loop seq 1 backwards and check how long uninterrupted stack there is
		for(int n1 = -seq2.length(); n1 < seq1.length() ; n1++){

			// start a new dimer result
			DimerResult dimerResult = null;
			int numberOfOneBaseGapsFound = 0;

			for(int n2 = 0; n2 < seq2.length() ; n2++){

				//					logger.info("n1=" + n1);
				//					logger.info("n2=" + n2);
				//					logger.info("seq1.length()" + seq1.length());

				int primer1CharPos = n1 + n2;
				int primer2CharPos = n2;			

				if(primer1CharPos >= 0 && primer1CharPos < seq1.length()){

					// set default non characters
					char n1Char = '-';
					char n2Char = '-';
					if(primer1CharPos >= 0 && primer1CharPos < seq1.length()){
						n1Char = seq1.charAt(primer1CharPos);
					}
					if(primer2CharPos >= 0 && primer2CharPos < seq2.length()){
						n2Char = seq2.charAt(primer2CharPos);
					}

					int charDimerVal = NucleotideUtilities.getDimerBinding(n1Char, n2Char);	
					if(charDimerVal >= 1){
						// start a new dimerResult 
						if(dimerResult == null){
							dimerResult = new DimerResult(seq1, seq2);
							dimerResult.setDimerStartPos(primer1CharPos, primer2CharPos);
						}
						dimerResult.setPrimer1EndPos(primer1CharPos);
						dimerResult.setPrimer2EndPos(primer2CharPos);

						//	logger.info("Dimer" + dimerResult.getDimerLength());

						dimerResult.addCharDimerValue(charDimerVal);
					}
					// stacking is interrupted by one non-stacking pos
					else{
						// check how many interruptions is allowed or if it is time to break
						if(numberOfOneBaseGapsAllowed > numberOfOneBaseGapsFound){
							numberOfOneBaseGapsFound ++;
						}
						else{
							// stacking is interrupted by a non-binding-pos
							// save dimer result and start a new one
							if(dimerResult != null && dimerResult.getDimerLengthWithoutAnyGaps() >= minDimerLen){
								allDimers.add(dimerResult);
							}
							dimerResult = null;
							numberOfOneBaseGapsFound = 0;
						}
					}

				}else{
					// stacking is interrupted by a non-binding-pos
					// save dimer result and start a new one
					if(dimerResult != null && dimerResult.getDimerLengthWithoutAnyGaps() >= minDimerLen){
						allDimers.add(dimerResult);
					}
					dimerResult = null;
					numberOfOneBaseGapsFound = 0;					
				}
			}


		}
		//			
		//			for(DimerResult dimer: allDimers){
		//				logger.info("dimerlen=" + dimer.getDimerLength());
		//			}
		//			
		// reverese result so 3" results come firsy
		Collections.reverse(allDimers);

		return allDimers;	
	}



	class DimerResult{
		int primer1StartPos;
		int primer1EndPos;
		int primer2StartPos;
		int primer2EndPos;
		String sequence1;
		String sequence2;
		double totalDimerValue;
		String dimerBindString = "";
		ArrayList<Double> dimerValues = new ArrayList<Double>();

		public DimerResult(String seq1, String seq2) {
			super();
			this.sequence1 = seq1;
			this.sequence2 = seq2;
		}


		public void setDimerStartPos(int primer1StartPos, int primer2StartPos) {	
			this.primer1StartPos = primer1StartPos;
			this.primer2StartPos = primer2StartPos;
			this.primer1EndPos = primer1StartPos;
			this.primer2EndPos = primer2StartPos;
		}

		public int getDimerLength() {
			return Math.abs(primer1StartPos - primer1EndPos) + 1;
		}

		public int getDimerLengthWithoutAnyGaps() {
			String dimerBind = getBindString();
			return StringUtils.countMatches(dimerBind, "|");
		}

		public void addCharDimerValue(double charDimerVal) {
			totalDimerValue += charDimerVal;
			dimerValues.add(new Double(charDimerVal));
		}

		public void setPrimer1EndPos(int primer1EndPos) {
			this.primer1EndPos = primer1EndPos;
		}

		public int getPrimer1EndPos() {
			return primer1EndPos;
		}

		public int getPrimer2EndPos() {
			return primer2EndPos;
		}

		public void setPrimer2EndPos(int primer2EndPos) {
			this.primer2EndPos = primer2EndPos;
		}

		public int getPrimer1StartPos() {
			return primer1StartPos;
		}

		public int getPrimer2StartPos() {
			return primer2StartPos;
		}

		public void setPrimer1StartPos(int primer1StartPos) {
			this.primer1StartPos = primer1StartPos;
		}

		public String[] getDimerAsText(){
			int diff = getPrimer2StartPos() - getPrimer1StartPos();
			//String primer1Line = createNewBlankString(diff) + "5\" " + sequence1 + " 3\"";
			String primer1Line = createNewBlankString(diff) + "" + sequence1 + " 3\"";

			diff = getPrimer1StartPos() - getPrimer2StartPos();
			//String primer2Line = createNewBlankString(diff) + "3\" " + sequence2 + " 5\"";
			String primer2Line = createNewBlankString(diff) + "" + sequence2 + " 5\"";

			diff = Math.max(getPrimer1StartPos(), getPrimer2StartPos());
			//String bindString =  createNewBlankString(diff) + "   " + getBindString(); // the 3 blanks is to compensate for text 3" or 5"
			String bindString =  createNewBlankString(diff) + getBindString(); // the 3 blanks is to compensate for text 3" or 5"

			String[] dimerBindAsText = new String[3];
			dimerBindAsText[0] = primer1Line;
			dimerBindAsText[1] = bindString;
			dimerBindAsText[2] = primer2Line;

			return dimerBindAsText;

		}

		private String createNewBlankString(int n) {
			if(n <= 0){
				return "";
			}	
			char[] blanks = new char[n];
			Arrays.fill(blanks,' ');
			return new String(blanks);
		}


		private String getBindString() {
			StringBuilder binding = new StringBuilder();
			for(int n = 0; n < getDimerLength(); n++){
				int bindVal = NucleotideUtilities.getDimerBinding(
						sequence1.charAt(getPrimer1StartPos() + n),
						sequence2.charAt(getPrimer2StartPos() + n));
				if(bindVal >= 1){
					binding.append('|');
				}
				else{
					binding.append(' ');
				}
			}
			return binding.toString();
		}


		public int getPrimer1MinPos(){
			return Math.min(getPrimer1StartPos(), getPrimer1EndPos());
		}

		public int getPrimer1MaxPos(){
			return Math.max(getPrimer1StartPos(), getPrimer1EndPos());
		}

		public int getPrimer2MinPos(){
			return Math.min(getPrimer2StartPos(), getPrimer2EndPos());
		}

		public int getPrimer2MaxPos(){
			return Math.max(getPrimer2StartPos(), getPrimer2EndPos());
		}

		public boolean is3EndDimer(){
			/*
			logger.info("getPrimer1MinPos()" + getPrimer1MinPos());
			logger.info("getPrimer1MaxPos()" + getPrimer1MaxPos());
			logger.info("getPrimer2MinPos()" + getPrimer2MinPos());
			logger.info("getPrimer2MaxPos()" + getPrimer2MaxPos());
			 */
			if(getPrimer1MaxPos() == sequence1.length() - 1 && getPrimer2MinPos() == 0){
				return true;
			}
			else{
				return false;
			}
		}	
	}



	public static final void setDimerLengthThreashold(int length) {
		minDimerReportLength = length;
	}

}


