package aliview;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;

public class AATranslator{
	private static final Logger logger = Logger.getLogger(AATranslator.class);
	CodonPositions codonPositions;
	private Sequence sequence;
	private GeneticCode genCode;
	
	public AATranslator(CodonPositions codonPositions, GeneticCode genCode) {
		this.codonPositions = codonPositions;
		this.genCode = genCode;
	}

	public void setCodonPos(CodonPositions codonPos) {
		this.codonPositions = codonPos;
	}
	
	public void setSequence(Sequence seq){
		this.sequence = seq;
	}
	
	public void setGeneticCode(GeneticCode genCode) {
		this.genCode = genCode;
	}	
	
	public boolean isFullCodonStartingAt(int x){
		return codonPositions.isFullCodonStartingAt(x);
	}
	
	public AminoAcid getAminoAcidAtNucleotidePos(int x){
		CodonPos cPos = codonPositions.getCodonPosAtNucleotidePos(x);
		if(cPos.isOrfan()){
			return AminoAcid.X;
		}
		else{
			return getAminoAcidFromTripletStartingAt(cPos.startPos);
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
	
	public AminoAcid getAminoAcidFromTripletStartingAt(int x){
		return AminoAcid.getAminoAcidFromCodon(getTripletAt(x), genCode);
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
	
	public AminoAcid getAAinTranslatedPos(int x) {
		CodonPos codonPos = codonPositions.getCodonInTranslatedPos(x);
		if(codonPos == null){
			return null;
		}
		if(codonPos.isOrfan()){
			return AminoAcid.X;
		}else{
			byte[] codon = getTripletAt(codonPos.startPos);
			return AminoAcid.getAminoAcidFromCodon(codon, genCode);
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
	

	public int findFistPos(int nextFindPos, AminoAcid target){
		for(int n = nextFindPos; n < codonPositions.getLengthOfTranslatedPos(); n++){
			AminoAcid nextAA = getAAinTranslatedPos(n);
			if(nextAA != null && nextAA.getCodeCharVal() == target.getCodeCharVal()){
				return n;
			}
		}
		return -1;
	}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
}
