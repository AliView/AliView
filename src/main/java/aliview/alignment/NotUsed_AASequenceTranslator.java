package aliview.alignment;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;
import aliview.AminoAcid;
import aliview.GeneticCode;
import aliview.NucleotideUtilities;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;

public class NotUsed_AASequenceTranslator{
	/*
	private static final Logger logger = Logger.getLogger(NotUsed_AASequenceTranslator.class);
	CodonPositions codonPositions;
	private Sequence sequence;
	private GeneticCode genCode;
	private int cachedClosestStartPos = -1;
	private int cachedAminoTripletAcidPos = -1;
	private AminoAcid cachedAminoAcid;
	
	public NotUsed_AASequenceTranslator(CodonPositions codonPositions, GeneticCode genCode) {
		this.codonPositions = codonPositions;
		this.genCode = genCode;
	}

	public void setCodonPos(CodonPositions codonPos) {
		this.codonPositions = codonPos;
	}
	
	public void setSequence(Sequence seq){
		this.sequence = seq;
		this.cachedClosestStartPos = -1;
		this.cachedAminoTripletAcidPos = -1;
	}
	
	public void setGeneticCode(GeneticCode genCode) {
		this.genCode = genCode;
	}	
	
	public boolean isFullCodonStartingAt(int x){
		return codonPositions.isFullCodonStartingAt(x);
	}
	
	public AminoAcid getAminoAcidAtNucleotidePos(int x){
		if(isFullCodonStartingAt(x)){
			return getAminoAcidFromTripletStartingAt(x);
		}else if(isFullCodonStartingAt(x - 1)){
			return getAminoAcidFromTripletStartingAt(x - 1);
		}else if(isFullCodonStartingAt(x - 2)){
			return getAminoAcidFromTripletStartingAt(x - 2);
		}else{
			return AminoAcid.GAP;
		}
	}
	
	public boolean isCodonSecondPos(int x){
		if(isFullCodonStartingAt(x - 1)){
			return true;
		}else{
			return false;
		}
	}
	
	public byte[] getTripletAt(int x){
		byte[] codon = new byte[3];
		for(int n = 0; n < 3; n ++){
			if(x + n < sequence.getLength()){
				codon[n] = sequence.getBaseAtPos(x + n);
			}
			else{
				codon[n] = SequenceUtils.GAP_SYMBOL;
			}
		}
		return codon;
	}
	
	public int getCachedClosestStartPos(){
		return cachedClosestStartPos;
	}

	
	public AminoAcid getNoGapAminoAcidAtNucleotidePos(int target){
		int tripCount = 0;
		byte[] triplet = new byte[3];
		int seqLen = sequence.getLength();
		
		
		// skip pos depending on ReadingFrame
		int startPos = 0;
		
		// if cached pos not is set, get a start pos
		if(cachedClosestStartPos == -1){
			int skipCount = 0;
			int readingFrame = codonPositions.getReadingFrame();
			if(readingFrame > 1){
				for(int n = 0; n < seqLen; n ++){
					byte base = sequence.getBaseAtPos(n);
					if(! NucleotideUtilities.isGap(base) && codonPositions.isCoding(n)){
						skipCount ++;
					}
					if(skipCount == readingFrame){
						startPos = n;
					}
				}
			}
		}else{
			startPos = cachedClosestStartPos;
		}
		
		if(target >= startPos){
			for(int n = startPos; n < seqLen; n++){
				byte base = sequence.getBaseAtPos(n);
				if(NucleotideUtilities.isGap(base) || codonPositions.isNonCoding(n)){			
					if(n >= target && tripCount == 0){
						return AminoAcid.GAP;
					}			
				}else{
					
					tripCount ++;
					triplet[tripCount - 1] = base;
					
					if(tripCount == 1){
						cachedClosestStartPos = n;
					}
					
					if(tripCount == 3){				
						if(n >= target){
							AminoAcid aa = AminoAcid.getAminoAcidFromCodon(triplet, genCode);
							return aa;
						}
						triplet = new byte[3];
						tripCount = 0;
					}			
				}
			}
			return AminoAcid.X;
		}else{
			return AminoAcid.GAP;
		}
	}
	
	
	public AminoAcid getAminoAcidFromTripletStartingAt(int x){
		if(cachedAminoTripletAcidPos != x){
			cachedAminoTripletAcidPos = x;
			cachedAminoAcid = AminoAcid.getAminoAcidFromCodon(getTripletAt(cachedAminoTripletAcidPos),genCode);
		}
		return cachedAminoAcid;
	}
	
	public String getTranslatedAsString(){
		StringWriter out = new StringWriter(sequence.getLength()/3);
		writeTranslation(out);
		return out.toString();
	}
	
	public void writeTranslation(Writer out){
		int x = 0;
		int gap = 0;
		
		try {
			while(x < sequence.getLength()){
				if(isFullCodonStartingAt(x)){
					byte[] codon = getTripletAt(x);
					out.append(AminoAcid.getAminoAcidFromCodon(codon).getCodeCharVal());
					// clear gap
					gap = 0;
					x = x + 3; // move one frame ahead (this is a full codon)
				}
				else{
					// there is a orfan in protein translation
					gap ++;
					// Add a protein X in sequence for every 3 gaps
					if(gap % 3 == 1){
						out.append(AminoAcid.X.getCodeCharVal());
					}
					x = x + 1;	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getTranslatedAminAcidSequenceLength(){
		return codonPositions.getAminoAcidPosFromNucleotidePos(sequence.getLength() - 1);
	}
	
	public int getMaximumTranslationLength(){
		return codonPositions.getLengthOfTranslatedPos();
	}
	
	public AminoAcid getAAinNoGapTranslatedPos(int x) {
		return getNoGapAminoAcidAtNucleotidePos(x * 3);
	}
	
	public AminoAcid getAAinTranslatedPos(int x) {
		CodonPos codonPos = codonPositions.getCodonInTranslatedPos(x);
		if(codonPos == null){
			return null;
		}
		if(codonPos.isOrfan()){
			return AminoAcid.X;
		}else{
			byte[] codon = getTripletAt(codonPos.startPos);
			AminoAcid aa = AminoAcid.getAminoAcidFromCodon(codon, genCode);
			return aa;
		}
	}
	
	public byte[] getGapPaddedCodonInTranslatedPos(int x) {
		byte[] codon = getCodonInTranslatedPos(x);
		byte[] padded = new byte[3];
		Arrays.fill(padded, SequenceUtils.GAP_SYMBOL);
		for(int n = 0; n < codon.length; n++){
			padded[n] = codon[n];
		}
		return padded;
	}
	
	public byte[] getCodonInTranslatedPos(int x) {
		CodonPos codonPos = codonPositions.getCodonInTranslatedPos(x);
		if(codonPos.isOrfan()){
			byte[] codon = sequence.getBasesBetween(codonPos.startPos, codonPos.endPos);
			return codon;
			
		}else{
			byte[] codon = getTripletAt(codonPos.startPos);
			return codon;
		}
	}


	public int findFistPosSkipStop(int nextFindPos, AminoAcid target){
		for(int n = nextFindPos; n < codonPositions.getLengthOfTranslatedPos(); n++){
			AminoAcid nextAA = getAAinTranslatedPos(n);
			
			if(nextAA != null && nextAA == AminoAcid.STOP){
				// Skip to next
			}	
			else if(nextAA != null && nextAA.getCodeCharVal() == target.getCodeCharVal()){
				return n;
			}
		}
		return -1;
	}
	
	public int findFistPos(int nextFindPos, AminoAcid target){
		for(int n = nextFindPos; n < codonPositions.getLengthOfTranslatedPos(); n++){
			AminoAcid nextAA = getAAinTranslatedPos(n);
			
			if(nextAA != null && nextAA.getCodeCharVal() == target.getCodeCharVal()){
				return n;
			}
		}
		return -1;
	}

	public int countStopCodon(){
		int counter = 0;
		int transLen = getTranslatedAminAcidSequenceLength();
//		logger.info("transLen" + transLen);
		for(int n = 0; n < transLen; n++){
			AminoAcid aa = getAAinTranslatedPos(n);
			if(aa == AminoAcid.STOP){
				//logger.info(sequence.getName() + "n=" + n);
				counter ++;
			}
		}
		return counter;
	}
	
	*/
}

