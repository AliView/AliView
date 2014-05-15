package aliview.primer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;

public class Primer implements Comparable<Primer> {
	private static final Logger logger = Logger.getLogger(Primer.class);
	private static final String LF = System.getProperty("line.separator");
	DecimalFormat DEC_FORMAT = new DecimalFormat("#.#");
	String sequence;
	private int position;

	public Primer(String sequence, int position) {
		super();
		this.sequence = sequence;
		this.position = position;
	}

	public long getScore(){
		long score = 0;
		
		long foldVal = getDegenerateFold();
		
		score = foldVal;
		
		return score;
	}
	
	public long getDegenerateFold(){
		
		long foldVal = 1;
		for(int n = 0; n < sequence.length(); n++){
			foldVal = foldVal * NucleotideUtilities.degenFoldFromChar(sequence.charAt(n));
		}
			
		return foldVal;
	}
	
	public String getBaseStackingAvgTmAsString(){
		double tm = getBaseStackingAvgTm();
		String tmString = "";
		tmString += DEC_FORMAT.format(tm) + "°C";
		return tmString;
	}
	
	public double getBaseStackingAvgTm(){
		double conc_primer = 200;// nM 
		double conc_salt = 50; //mM
		double conc_mg = 0; //mM
		
		// expand all primers
		ArrayList<String> regenerated = NucleotideUtilities.regenerateDegenerated(sequence);
		
		// loop through and get max and min tm
		double minTM = Double.MAX_VALUE;
		double maxTM = Double.MIN_VALUE;
		for(String thisSeq: regenerated){
			double tm = OligoCalc.getBaseStackingTM(thisSeq, conc_primer, conc_salt, conc_mg);
			//logger.info("tm=" + tm);
			minTM = Math.min(tm, minTM);
			maxTM = Math.max(tm, maxTM);
		}

		return (minTM/2+maxTM/2);
	}
	
	public String getBaseStackingTmAsString(){
		double conc_primer = 200;// nM 
		double conc_salt = 50; //mM
		double conc_mg = 0; //mM
		
		// expand all primers
		ArrayList<String> regenerated = NucleotideUtilities.regenerateDegenerated(sequence);
		
		// loop through and get max and min tm
		double minTM = Double.MAX_VALUE;
		double maxTM = Double.MIN_VALUE;
		for(String thisSeq: regenerated){
			double tm = OligoCalc.getBaseStackingTM(thisSeq, conc_primer, conc_salt, conc_mg);
			//logger.info("tm=" + tm);
			minTM = Math.min(tm, minTM);
			maxTM = Math.max(tm, maxTM);
		}

		String tmString = "";
		if(minTM == maxTM){
			tmString = DEC_FORMAT.format(minTM) + "°C"; 
		}
		else{
			tmString += DEC_FORMAT.format(minTM) + "°C to " + DEC_FORMAT.format(maxTM) + "°C"; 
		}
		
		return tmString;
	}
	
	public String getTmAsString(){
		double conc_primer = 200;// nM 
		double conc_salt = 50; //mM
		double conc_mg = 0; //mM
		
		// expand all primers
		ArrayList<String> regenerated = NucleotideUtilities.regenerateDegenerated(sequence);
		
		// loop through and get max and min tm
		double minTM = Double.MAX_VALUE;
		double maxTM = Double.MIN_VALUE;
		for(String thisSeq: regenerated){
			double tm = OligoCalc.getBaseStackingTM(thisSeq, conc_primer, conc_salt, conc_mg);
			//logger.info("tm=" + tm);
			minTM = Math.min(tm, minTM);
			maxTM = Math.max(tm, maxTM);
		}

		String tmString = "baseStack ";
		if(minTM == maxTM){
			tmString = DEC_FORMAT.format(minTM) + "°C"; 
		}
		else{
			tmString += "avg=" + DEC_FORMAT.format(minTM/2+maxTM/2) + "°C " +  
			          DEC_FORMAT.format(minTM) + "°C to " + DEC_FORMAT.format(maxTM) + "°C"; 
		}
		
		return tmString;
	}

	public int compareTo(Primer o) {
		
		if(this.getScore() == o.getScore()){
			return 0;
		}
		
		if(this.getScore() > o.getScore()){
			return 1;
		}
		else{
			return -1;
		}
		
	}

	public String getSequence() {
		return this.sequence;
	}

	public int getPosition() {
		return this.position;
	}

	public double getGCcontent() {
		double ng = StringUtils.countMatches(this.getSequence(), "G");
		double nc = StringUtils.countMatches(this.getSequence(), "C");
		double gc_contents = (ng+nc)/this.getSequence().length();
		return gc_contents;
	}

	public int getLength() {
		return this.getSequence().length();
	}

	public int get3EndDimerMaxLength() {
		Dimer d = new Dimer(this.getSequence(), NucleotideUtilities.revComp(this.getSequence()));
		return d.get3EndDimerMaxLength();
	}


	public int getDimerMaxLength() {
		Dimer d = new Dimer(this.getSequence(), NucleotideUtilities.revComp(this.getSequence()));
		return d.getDimerMaxLength();
	}
	public ArrayList<String> getAllDimersAsText() {
		Dimer d = new Dimer(this.getSequence(), NucleotideUtilities.revComp(this.getSequence()));
		return d.getAllDimersAsText();
	}

	public String getPrimerDetailsAsText() {
		String data="";
    	data += ">" + this.getPosition() + LF;
    	data += "" + this.getSequence() + LF;
    	data += "revcomp:" + aliview.NucleotideUtilities.revComp(this.getSequence()) + LF;
    	data += "position:" + this.getPosition() + LF;
    	data += "length:" + this.getSequence().length() + LF;
    	data += "degenerate-fold:" + this.getScore() + LF;
    	
    	
    	boolean isFirstLine = true;
    	for(String line: NucleotideUtilities.seqToDeUPACStringArray(this.getSequence())){
    		if(isFirstLine){
    			data += "degenerate-view: " + line + LF;
    			isFirstLine = false;
    		}else{
    			data += "                 " + line + LF;
    		}
    	}
    	
    	data += "Tm(base-stacking):" + this.getBaseStackingTmAsString() + " (avg=" + getBaseStackingAvgTmAsString() + ")" +  LF;
    	data += "gc-content:" + this.getGCcontent() + LF;
    	data += "max 3\"-end-dimer:" + this.get3EndDimerMaxLength() + LF;
    	data += "max dimer length:" + this.getDimerMaxLength() +  LF;
    	data += "below are all dimers above threshold length (" + this.getDimerLengthThreashold() + "):" + LF;
    	data += LF;
    	for(String line: this.getAllDimersAsText()){
    		data += line + LF;
    	}
    	return data;
	}

	private int getDimerLengthThreashold() {
		return Dimer.getDimerLengthThreashold();
	}
	
}
