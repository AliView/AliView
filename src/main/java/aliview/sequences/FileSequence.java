package aliview.sequences;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
// import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequencelist.Interval;
import aliview.sequencelist.MemoryMappedSequencesFile;

public class FileSequence extends BasicSequence {
	private static final Logger logger = Logger.getLogger(FileSequence.class);

	public FileSequence(MemoryMappedSequencesFile sequencesFile, long startPointer) {
		this(new FileSequenceBases(sequencesFile, startPointer));
	}

	public FileSequence(FileSequenceBases fileSequenceBases) {
		super(fileSequenceBases);
	}

	public FileSequence(FileSequence template) {
		super(template);
	}

	/*
	 * This is called when creating sequences from an .fai index file
	 */
	public FileSequence(MemoryMappedSequencesFile sequencesFile, int seqIndex, String name, int seqWithoutWhitespaceLength, long seqAfterNameStartPointer, long endPointer, int lineCharLength, int lineAbsoluteLength) {
		this(new FileSequenceBases(sequencesFile, seqAfterNameStartPointer, endPointer, seqAfterNameStartPointer));
		this.name = name;
		// These are not used currently
		//this.seqWithoutWhitespaceLength = seqWithoutWhitespaceLength;
		//this.sequenceAfterNameStartPointer = seqAfterNameStartPointer;
		//this.lineCharLength = lineCharLength;
		//this.lineAbsoluteLength = lineAbsoluteLength;
	}

	public FileSequence getCopy() {
		return new FileSequence(this);
	}

	public long getEndPointer(){
		return getFileSequenceBases().getEndPointer();
	}

	public void setEndPointer(long pointer) {
		getFileSequenceBases().setEndPointer(pointer);
	}

	public void setSequenceAfterNameStartPointer(long pointer) {
		getFileSequenceBases().setSequenceAfterNameStartPointer(pointer);
	}

	public long getSequenceAfterNameStartPointer(){
		return getFileSequenceBases().getSequenceAfterNameStartPointer();
	}

	private FileSequenceBases getFileSequenceBases(){
		return (FileSequenceBases) bases;
	}


	public byte getBaseAtPos(int n) {
		return (byte) getBases().get(n);
	}

	public int getBaseAsIntAtPos(int n) {
		return getBases().get(n);
	}

	// TO DO this is not working if seq is to large
	public byte[] getAllBasesAsByteArray(){
		return getBases().toByteArray();
	}

	public void writeBases(OutputStream out) throws IOException{
		for(int n = 0; n < getLength(); n++){
			int base = getBaseAsIntAtPos(n);
			if(base == ' ' || base == '\n' || base =='\r'){
			}
			else{
				out.write(base);
			}
		}	
	}

	public void writeBases(Writer out) throws IOException {
		for(int n = 0; n < getLength(); n++){
			int base = getBaseAsIntAtPos(n);
			if(base == ' ' || base == '\n' || base =='\r'){
			}
			else{
				out.write((char)base);
			}
		}	
	}


	public Interval find(Pattern pattern, int startPos) {

		Interval foundInterval = null;
		// split into chunks length = 5MB
		int buffSize = 5000*1000;
		for(int buffStart = startPos; buffStart < getLength(); buffStart += buffSize){

			StringBuilder buff = new StringBuilder();
			for(int n = buffStart; n < getLength() && n < (buffStart + buffSize); n++){
				byte next = (byte)getBaseAsIntAtPos(n);
				if(next != NucleotideUtilities.GAP){	
					buff.append((char)next);
				}
			}

			// Allocate a Matcher object from the compiled regexe pattern,
			// and provide the input to the Matcher
			Matcher matcher = pattern.matcher(buff);
			boolean wasFound = matcher.find(0);

			if(wasFound){
				int foundStart = matcher.start();
				int foundEnd = matcher.end() - 1;
				foundInterval = new Interval(foundStart+buffStart, foundEnd+buffStart);
			}

			else{
				// 	logger.info("not found");
			}			
		}
		return foundInterval;
	}


	public void reverseComplement() {
		// TODO Auto-generated method stub
	}

	public void complement() {
		// TODO Auto-generated method stub
	}

	public void replaceBases(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases) {
		// TODO Auto-generated method stub
	}

	public void replaceSelectedBasesWithGap() {
		// TODO Auto-generated method stub
	}

	public void deleteSelectedBases() {
		// TODO Auto-generated method stub
	}

	public void deleteBasesFromMask(boolean[] deleteMask) {
		// TODO Auto-generated method stub
	}

	public void clearBase(int n) {
		// TODO Auto-generated method stub
	}


	public void rightPadSequenceWithGaps(int diffLen) {
		// TODO Auto-generated method stub
	}

	public void leftPadSequenceWithGaps(int diffLen) {
		// TODO Auto-generated method stub
	}


	public void setSelectionOffset(int selectionOffset) {
		// TODO Auto-generated method stub
	}

	public void moveSelectedResiduesRightIfGapIsPresent() {
		// TODO Auto-generated method stub

	}

	public void moveSelectedResiduesLeftIfGapIsPresent() {
		// TODO Auto-generated method stub
	}

	public void moveSelectedResiduesRightIfGapOrEndIsPresent() {
		// TODO Auto-generated method stub
	}

	public boolean isGapOrEndRightOfSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isGapRightOfSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isGapLeftOfSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	public void deleteGapLeftOfSelection() {
		// TODO Auto-generated method stub
	}

	public void deleteGapRightOfSelection() {
		// TODO Auto-generated method stub	
	}

	public void replaceSelectedBasesWithChar(char newChar) {
		// TODO Auto-generated method stub
	}

	public void realignNucleotidesUseThisAASequenceAsTemplate(byte[] allBasesAsByteArray) {
		// TODO Auto-generated method stub
	}

	public void selectAllBasesUntilGap(int x) {
		// TODO Auto-generated method stub	
	}

	public int indexOf(char testChar) {
		logger.warn("This might take a long time");
		return super.indexOf(testChar);
	}

	public int countChar(char targetChar, int startpos, int endpos) {	
		logger.warn("This might take a long time");
		return super.countChar(targetChar, startpos, endpos);

	}

	public int getUngapedLength() {
		logger.warn("this could take a lot of time and ruin memory");
		return -1;
	}

	public int[] getSequenceAsBaseVals() {
		logger.warn("this could take a lot of time and ruin memory");
		return super.getSequenceAsBaseVals();
	}

	public int getUngapedPos(int position) {
		logger.warn("this could take a lot of time");
		return super.getUngapedPos(position);
	}


}
