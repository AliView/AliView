package aliview.sequences;

import java.util.Arrays;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;

public final class SequenceUtils {
	private static final Logger logger = Logger.getLogger(SequenceUtils.class);
	public static final byte GAP_SYMBOL = (byte) '-';
	public static int TYPE_AMINO_ACID = 0;
	public static int TYPE_NUCLEIC_ACID = 1;
	public static int TYPE_UNKNOWN = 2;
	
	public static int id_counter;
	
	
	public static byte[] createGapByteArray(int length) {
		byte[] byteSeq = new byte[length];
		Arrays.fill(byteSeq, SequenceUtils.GAP_SYMBOL);
		return byteSeq;
	}
	
	public static int createID() {
	//	logger.info("create ID=" + id_counter);
		id_counter ++;
		return id_counter;
	}
	
	public static int countExactNucleotideOverlap(Sequence seq1, Sequence seq2) {
		int nExactOverlap = 0;
		int nDifferentOverlap = 0;
		
		for(int n = 0; n < seq1.getLength(); n++){
			if(NucleotideUtilities.isAtLeastOneGap(seq1.getBaseAtPos(n),seq2.getBaseAtPos(n))){
				// Nothing to do
			}else{
				if(NucleotideUtilities.baseValFromBase(seq1.getBaseAtPos(n)) == NucleotideUtilities.baseValFromBase(seq2.getBaseAtPos(n))){
					nExactOverlap ++;	
				}
				else{
					nDifferentOverlap ++;
				}
			}
		}
		return nExactOverlap;		
	}
	
	public static boolean isSeqResiduesIdentical(Sequence seq1, Sequence seq2) {
		if(seq1.getLength() != seq2.getLength()){
			return false;
		}
		for(int n = 0; n < seq1.getLength(); n++){
			if(Character.toLowerCase(seq1.getCharAtPos(n)) != Character.toLowerCase(seq2.getCharAtPos(n))){
				return false;
			}
		}	
		return true;
	}
	
	public static int countDifferentNucleotideOverlap(Sequence seq1, Sequence seq2) {
		int nExactOverlap = 0;
		int nDifferentOverlap = 0;
		
		for(int n = 0; n < seq1.getLength(); n++){
			if(NucleotideUtilities.isAtLeastOneGap(seq1.getBaseAtPos(n),seq2.getBaseAtPos(n))){
				// Nothing to do
			}else{
				if(NucleotideUtilities.baseValFromBase(seq1.getBaseAtPos(n)) == NucleotideUtilities.baseValFromBase(seq2.getBaseAtPos(n))){
					nExactOverlap ++;	
				}
				else{
					nDifferentOverlap ++;
				}
			}
		}
		return nDifferentOverlap;		
	}
	
	public static int countDifferentAminoAcidOverlap(Sequence seq1, Sequence seq2) {
		int nExactOverlap = 0;
		int nDifferentOverlap = 0;
		
		for(int n = 0; n < seq1.getLength(); n++){
			if(AminoAcid.isGap(seq1.getBaseAtPos(n)) || AminoAcid.isGap(seq2.getBaseAtPos(n))){
				// nothing to do
			}else{
				if(AminoAcid.getAminoAcidFromByte(seq1.getBaseAtPos(n)) == AminoAcid.getAminoAcidFromByte(seq2.getBaseAtPos(n))){
					nExactOverlap ++;	
				}
				else{
					nDifferentOverlap ++;
				}
			}
		}
		return nDifferentOverlap;		
	}
	
	public static int countExactAminoAcidOverlap(Sequence seq1, Sequence seq2) {
		int nExactOverlap = 0;
		int nDifferentOverlap = 0;
		
		for(int n = 0; n < seq1.getLength(); n++){
			if(AminoAcid.isGap(seq1.getBaseAtPos(n)) || AminoAcid.isGap(seq2.getBaseAtPos(n))){
				// nothing to do
			}else{
				if(AminoAcid.getAminoAcidFromByte(seq1.getBaseAtPos(n)) == AminoAcid.getAminoAcidFromByte(seq2.getBaseAtPos(n))){
					nExactOverlap ++;	
				}
				else{
					nDifferentOverlap ++;
				}
			}
		}
		return nExactOverlap;		
	}
}
