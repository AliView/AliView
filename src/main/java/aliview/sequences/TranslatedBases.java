package aliview.sequences;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;
import aliview.AliView;
import aliview.AminoAcid;
import aliview.GeneticCode;
import aliview.NucleotideUtilities;
import aliview.alignment.NotUsed_AATranslator;
import aliview.sequencelist.AlignmentListModel;
import aliview.utils.ArrayUtilities;

public class TranslatedBases implements Bases{
	private static final Logger logger = Logger.getLogger(TranslatedBases.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	private Bases delegate;
	private Sequence parentSequence;

	public TranslatedBases(Bases delegate, Sequence sequence) {
		this.delegate = delegate;
		this.parentSequence = sequence;
	}

	public TranslatedBases getCopy(){

		logger.debug("delegate.getLength()" + delegate.getLength());
		logger.debug("delegate.getCopy().getLength()" + delegate.getCopy().getLength());

		return new TranslatedBases(delegate.getCopy(), parentSequence);
	}


	public int getLength(){
		// or translated
		return getTranslatedAminAcidSequenceLength();
		//return backend.length;
	}

	public byte get(int n) {
		// or translated
		return getAAinTranslatedPos(n).getCodeByteVal();
		//return backend[n];
	}

	public void moveBaseLeft(int n) {
		int nTrans = getCodonPosInTranslatedPos(n).startPos;	
		delegate.set(nTrans - 3, delegate.get(nTrans));
		delegate.set(nTrans - 2, delegate.get(nTrans + 1));
		delegate.set(nTrans - 1, delegate.get(nTrans + 2));	
	}

	public void moveBaseRight(int n) {
		int nTrans = getCodonPosInTranslatedPos(n).startPos;
		delegate.set(nTrans + 3, delegate.get(nTrans));
		delegate.set(nTrans + 4, delegate.get(nTrans + 1));
		delegate.set(nTrans + 5, delegate.get(nTrans + 2));
	}

	public char charAt(int n) {
		// or translated
		return getAAinTranslatedPos(n).getCodeCharVal();
		//ureturn (char) backend[n];
	}

	public byte[] toByteArray() {
		// or translated
		return getTranslatedAsString().getBytes();
		//return backend;
	}

	public byte[] toByteArray(int startIndexInclusive, int endIndexInclusive){
		byte[] subArray = ArrayUtils.subarray(this.toByteArray(), startIndexInclusive, endIndexInclusive + 1);
		return subArray;	
	}

	@Override
	public String toString() {
		String asString = null;
		// or translated
		return getTranslatedAsString();
		/*
		try {
			asString = new String(backend, TEXT_FILE_BYTE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return asString;
		 */
	}

	public void set(int n, byte aaAsByte) {
		// or translated
		replace(n, n, new byte[]{aaAsByte});

		//backend[n] = newBase;
	}



	// helper method
	private byte[] getTripletsFromAA(byte[] inAAs){
		StringBuilder triplets = new StringBuilder(inAAs.length * 3);


		for(byte aaAsByte: inAAs){
			byte[] triplet = getTripletFromAA(aaAsByte);
			triplets.append(new String(triplet));
		}
		return triplets.toString().getBytes();
	}

	// helper method
	private byte[] getTripletFromAA(byte aaAsByte) {
		// TODO Auto-generated method stub
		byte bytes[] = "---".getBytes();
		//		logger.info("bytes:");
		//		for(byte aByte: bytes){
		//			logger.info("aByte=" + aByte);
		//		}
		return bytes;
	}

	public void insertAt(int n, byte[] aminoAAs) {
		byte[] triplets = getTripletsFromAA(aminoAAs);

		int nucleotidePos = getCodonPosInTranslatedPos(n).startPos;

		delegate.insertAt(nucleotidePos, triplets);


		//byte[] newArray = ArrayUtilities.insertAt(backend, n, newBytes);
		//backend = newArray;
	}

	public void replace(int startReplaceIndex, int stopReplaceIndex, byte[] insertAAs) {
		// or translated
		byte[] triplets = getTripletsFromAA(insertAAs);

		int nucleotidePos = getCodonPosInTranslatedPos(startReplaceIndex).startPos;

		delegate.replace(nucleotidePos, nucleotidePos + triplets.length - 1, triplets);

	}

	public void delete(int[] toDeletePos) {
		// or translated	
		// translate toDelete

		ArrayList<Integer> toDeleteNucleotidePos = new ArrayList<Integer>();

		for(int aaPos : toDeletePos){		
			CodonPos codon = getCodonPosInTranslatedPos(aaPos);
			for(int n = codon.startPos; n <= codon.endPos; n++){
				toDeleteNucleotidePos.add(new Integer(n));
			}
		}

		int[] toDeleteArray = ArrayUtils.toPrimitive(toDeleteNucleotidePos.toArray(new Integer[0]));

		for(int toDel: toDeleteArray){
			logger.info("toDel" + toDel);
		}

		delegate.delete(toDeleteArray);	
	}

	public void deleteAll(byte val) {
		// TODO Auto-generated method stub	
	}


	// ?????
	public void complement() {
		delegate.complement();		
	}

	// ?????
	public void reverse() {
		delegate.reverse();
	}



	// convenience method
	public void set(int n, char c) {
		set(n, (byte) c);
	}

	// convenience
	public void delete(int pos) {
		delete(new int[]{pos});
	}

	// convenience
	public void insertAt(int n, byte newByte) {
		insertAt(n, new byte[]{newByte});
	}

	// convenience
	public void append(byte[] newBytes) {
		insertAt(getLength(), newBytes);
	}





	// Translating help
	private int cachedClosestTranslatedNucleotideStartPos = -1;
	private int cachedAminoTripletAcidPos = -1;
	private AminoAcid cachedAminoAcid;


	private CodonPositions getCodonPositions() {
		return parentSequence.getAlignmentModel().getAlignmentMeta().getCodonPositions();
	}

	private GeneticCode getGeneticCode() {
		return parentSequence.getAlignmentModel().getAlignmentMeta().getGeneticCode();
	}

	private boolean isFullCodonStartingAt(int x){
		return getCodonPositions().isFullCodonStartingAt(x);
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
			if(x + n < delegate.getLength()){
				codon[n] = delegate.get(x + n);
			}
			else{
				codon[n] = SequenceUtils.GAP_SYMBOL;
			}
		}
		return codon;
	}

	public int getCachedClosestStartPos(){
		return cachedClosestTranslatedNucleotideStartPos;
	}

	/*
		public AminoAcid getAAinNoGapTranslatedPos(int x) {
			return getNoGapAminoAcidAtNucleotidePos(x * 3);
		}
	 */

	public AminoAcidAndPosition getNoGapAminoAcidAtNucleotidePos(int target){
		int tripCount = 0;
		byte[] triplet = new byte[3];
		int seqLen = delegate.getLength();


		// skip pos depending on ReadingFrame
		int startPos = 0;

		// if cached pos not is set, get a start pos
		if(cachedClosestTranslatedNucleotideStartPos == -1 || target < cachedClosestTranslatedNucleotideStartPos){ //
			int skipCount = 0;
			int readingFrame = getCodonPositions().getReadingFrame();
			if(readingFrame > 1){
				for(int n = 0; n < seqLen; n ++){
					byte base = delegate.get(n);
					if(! NucleotideUtilities.isGap(base) && getCodonPositions().isCoding(n)){
						skipCount ++;
					}
					if(skipCount == readingFrame){
						startPos = n;
					}
				}
			}
		}else{
			startPos = cachedClosestTranslatedNucleotideStartPos;
		}

		if(target >= startPos){
			for(int n = startPos; n < seqLen; n++){
				byte base = delegate.get(n);
				if(NucleotideUtilities.isGap(base) || getCodonPositions().isNonCoding(n)){			
					if(n >= target && tripCount == 0){
						return new AminoAcidAndPosition(AminoAcid.GAP, startPos); 
					}			
				}else{

					tripCount ++;
					triplet[tripCount - 1] = base;

					if(tripCount == 1){
						cachedClosestTranslatedNucleotideStartPos = n;
					}

					if(tripCount == 3){				
						if(n >= target){
							AminoAcid aa = AminoAcid.getAminoAcidFromCodon(triplet, getGeneticCode());
							return new AminoAcidAndPosition(aa, startPos);
						}
						triplet = new byte[3];
						tripCount = 0;
					}			
				}
			}
			return new AminoAcidAndPosition(AminoAcid.X, startPos);
		}else{
			return new AminoAcidAndPosition(AminoAcid.GAP, startPos);//AminoAcid.GAP;
		}
	}

	public AminoAcid getAminoAcidFromTripletStartingAt(int x){
		if(cachedAminoTripletAcidPos != x){
			cachedAminoTripletAcidPos = x;
			cachedAminoAcid = AminoAcid.getAminoAcidFromCodon(getTripletAt(cachedAminoTripletAcidPos),getGeneticCode());
		}
		return cachedAminoAcid;
	}

	public String getTranslatedAsString(){
		StringWriter out = new StringWriter(delegate.getLength()/3);
		writeTranslation(out);
		return out.toString();
	}

	public void writeTranslation(Writer out){
		int x = 0;
		int gap = 0;

		try {
			while(x < delegate.getLength()){
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
		if(delegate.getLength() == 0){
			return 0;
		}
		int lastPos = getCodonPositions().getAminoAcidPosFromNucleotidePos(delegate.getLength() - 1);
		return lastPos + 1;
	}

	public AminoAcid getAAinTranslatedPos(int x) {
		CodonPos codonPos = getCodonPositions().getCodonInTranslatedPos(x);
		if(codonPos == null){
			return null;
		}
		if(! codonPos.isCoding()){
			return AminoAcid.X;
		}else{
			byte[] codon = getTripletAt(codonPos.startPos);
			AminoAcid aa = AminoAcid.getAminoAcidFromCodon(codon, getGeneticCode());
			return aa;
		}
	}

	public byte[] getGapPaddedCodonInTranslatedPos(int x) {
		byte[] codon = getCodonInTranslatedPos(x);
		if(codon.length != 3){
			byte[] padded = new byte[3];
			Arrays.fill(padded, SequenceUtils.GAP_SYMBOL);
			for(int n = 0; n < codon.length; n++){
				padded[n] = codon[n];
			}
			codon = padded;
		}
		return codon;
	}

	private CodonPos getCodonPosInTranslatedPos(int x) {
		return getCodonPositions().getCodonInTranslatedPos(x);
	}

	public byte[] getCodonInTranslatedPos(int x) {
		CodonPos codonPos = getCodonPositions().getCodonInTranslatedPos(x);	
		byte[] codon = getNucleotidesAtCodon(codonPos);
		return codon;
	}


	private byte[] getNucleotidesAtCodon(CodonPos codonPos) {
		if(codonPos == null || codonPos.startPos >= delegate.getLength()){
			return new byte[]{SequenceUtils.GAP_SYMBOL};
		}

		int start = codonPos.startPos;
		int end = Math.min(codonPos.endPos, delegate.getLength() -1);

		byte[] codonBytes = new byte[end - start + 1];
		for(int n = start; n <= end; n++){
			codonBytes[n - start] = delegate.get(n); 
		}


		//byte[] codonBytes = delegate.getBetween(codonPos.startPos, codonPos.endPos + 1);

		return codonBytes;
	}



	/*
		public int findFistPosSkipStop(int nextFindPos, AminoAcid target){
			for(int n = nextFindPos; n < getCodonPositions().getLengthOfTranslatedPos(); n++){
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
			for(int n = nextFindPos; n < getCodonPositions().getLengthOfTranslatedPos(); n++){
				AminoAcid nextAA = getAAinTranslatedPos(n);

				if(nextAA != null && nextAA.getCodeCharVal() == target.getCodeCharVal()){
					return n;
				}
			}
			return -1;
		}
	 */

	public int countStopCodon(){
		int counter = 0;
		int transLen = getTranslatedAminAcidSequenceLength();
		//			logger.info("transLen" + transLen);
		for(int n = 0; n < transLen; n++){
			AminoAcid aa = getAAinTranslatedPos(n);
			if(aa == AminoAcid.STOP){
				//logger.info(sequence.getName() + "n=" + n);
				counter ++;
			}
		}
		return counter;
	}




}

