package aliview.sequences;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

import aliview.NucleotideUtilities;
import aliview.sequencelist.FileMMSequenceList;

public class FileSequence implements Sequence {
	private static final Logger logger = Logger.getLogger(FileSequence.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	private OptimizedRandomAccessFile raf;
	private long startPointer;
	protected String name;
	protected FileMMSequenceList fileSeqList;
	private long sequenceAfterNameStartPointer;
	private int seqIndex;
	private long endPointer;
	private SequenceSelectionModel selectionModel = new DefaultSequenceSelectionModel();
	private int id = SequenceUtils.createID();
	private int seqWithoutWhitespaceLength;
	private int lineCharLength;
	private int lineAbsoluteLength;

	public FileSequence(OptimizedRandomAccessFile raf, long startPointer){
		this.raf = raf;
		this.startPointer = startPointer;
	}

	public FileSequence(FileMMSequenceList fileSeqList, int seqIndex, long startPointer) {
		this.fileSeqList = fileSeqList;
		this.startPointer = startPointer;
		this.seqIndex = seqIndex;
	}
	
	public FileSequence(FileSequence template) {
		this.fileSeqList = template.fileSeqList;
		this.startPointer = template.startPointer;
		this.seqIndex = template.seqIndex;
		this.fileSeqList = template.fileSeqList;
		this.sequenceAfterNameStartPointer = template.sequenceAfterNameStartPointer;
		this.endPointer = template.endPointer;
		this.raf = template.raf;
		this.name = template.name;
	}
	
	public FileSequence(FileMMSequenceList seqList, int seqIndex, String name, int seqWithoutWhitespaceLength, long seqAfterNameStartPointer, long endPointer,
			int lineCharLength, int lineAbsoluteLength) {
		this.fileSeqList = seqList;
		this.seqIndex = seqIndex;
		this.name = name;
		this.seqWithoutWhitespaceLength = seqWithoutWhitespaceLength;
		this.sequenceAfterNameStartPointer = seqAfterNameStartPointer;
		this.endPointer = endPointer;
		this.lineCharLength = lineCharLength;
		this.lineAbsoluteLength = lineAbsoluteLength;
	}

	public Sequence getCopy() {
		return new FileSequence(this);
	}

	public long getSequenceAfterNameStartPointer(){
		return sequenceAfterNameStartPointer;
	}

	public long getStartPointer(){
		return startPointer;
	}

	public long getEndPointer(){
		return endPointer;
	}

	public void setEndPointer(long end) {
		this.endPointer = end;		
	}

	public void setSequenceAfterNameStartPointer(long seqStartPointer) {
		this.sequenceAfterNameStartPointer = seqStartPointer;		
	}

	public void addName(String name){
		this.name = name;
	}

	public int getIndex() {
		return seqIndex;
	}

	public int getLength(){
		//logger.info(getSeqEndPointer());
		//logger.info(getSeqStartPointer());
        long len = (getEndPointer() - getStartPointer()) - (getSequenceAfterNameStartPointer() - getStartPointer()); // +1 because seq end pointer is inclusive 

		return (int)len;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String toString(){
		return getName();
	}

	public String getSimpleName() {
		return getName();
	}

	public byte getBaseAtPos(int n) {
		return (byte)getBaseAsIntAtPos(n);
	}

	public int getBaseAsIntAtPos(int n) {
		return fileSeqList.readInFile(getSequenceAfterNameStartPointer() + n);
	}

	public int getBasesAt(int x, int i, byte[] bytes) {
		return fileSeqList.readBytesInFile( (getSequenceAfterNameStartPointer() + x), i, bytes);
	}

	public byte[] getBasesAt(int x, int i) {
		return null;
	}

	// TO DO this is not working if seq is to large
	public byte[] getAllBasesAsByteArray(){
		if(this.getLength() > 100 * 1000 * 1000){
			return null;
		}
		byte[] allBases = new byte[this.getLength()];
		for(int n = 0; n < allBases.length; n++){
			allBases[n] = getBaseAtPos(n);
		}
		return allBases;
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

	public String getBasesAsString() {
		byte[] allBases = getAllBasesAsByteArray();
		return new String(allBases);
	}

	public int getUngapedLength() {

		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public int[] getSequenceAsBaseVals() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getUngapedPos(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getBasesAtThesePosAsString(ArrayList<Integer> allPositions) {
		// TODO Auto-generated method stub
		return null;
	}

	public void reverseComplement() {
		// TODO Auto-generated method stub
	}

	public void complement() {
		// TODO Auto-generated method stub
	}

	public void replaceBases(int startReplaceIndex, int stopReplaceIndex,
			byte[] insertBases) {
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

	public void setBases(byte[] bases) {
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


	public int findAndSelect(Pattern pattern, int startPos) {

		int findPos = -1;
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
				int foundEnd = matcher.end();
				
				// select
				selectionModel.setSelection(foundStart+buffStart+1,foundEnd+buffStart -1,true);
				
				findPos = foundStart+buffStart;
			}

			else{
				// 	logger.info("not found");
			}			
		}
		return findPos;
	}



	public void setSelectionAt(int n, boolean selected){
		selectionModel.setSelectionAt(n, selected);
	}

	public void selectBases(int startPos, int endPos) {
		selectionModel.setSelection(startPos, endPos, true);
	}

	public boolean isBaseSelected(int position) {
		return selectionModel.isBaseSelected(position);
	}

	public void clearAllSelection() {
		selectionModel.clearAll();
	}

	public void selectAllBases() {
		selectionModel.selectAll();
	}

	public boolean hasSelection() {
		return selectionModel.hasSelection();
	}

	public int[] getSelectedPositions() {
		return selectionModel.getSelectedPositions(0, this.getLength() - 1);
	}

	public String getSelectedBasesAsString(){
		StringBuilder selection = new StringBuilder();
		if(selectionModel.hasSelection()){
			int[] selectedPos = getSelectedPositions();
			for(int n = 0;n < selectedPos.length;n++){
				selection.append((char)getBaseAtPos(selectedPos[n]));
			}
		}
		return selection.toString();
	}


	public byte[] getSelectedBasesAsByte(){
		byte[] bases = null;
		try {
			bases = getSelectedBasesAsString().getBytes(TEXT_FILE_BYTE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bases;
	}



	/*
	public String getBasesAsString(){
		String baseString = "";
		try {
			baseString = new String(getBases(), TEXT_FILE_BYTE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baseString;
	}
	 */

	public int getFirstSelectedPosition() {
		return selectionModel.getFirstSelectedPosition();
	}

	//
	//  End Standard selection
	//




	public void setSelectionOffset(int selectionOffset) {
		// TODO Auto-generated method stub

	}

	public void moveSelectionRightIfGapIsPresent() {
		// TODO Auto-generated method stub

	}

	public void moveSelectionLeftIfGapIsPresent() {
		// TODO Auto-generated method stub

	}


	public boolean contains(char testChar) {
		for(int n = 0; n < getLength(); n++){
			int base = getBaseAsIntAtPos(n);
			if((char)base == testChar){
				return true;
			}
		}
		return false;
	}
	
	public int countChar(char targetChar) {
		int count = 0;
		for(int n = 0; n < getLength(); n++){
			int base = getBaseAsIntAtPos(n);
			if((char)base == targetChar){
				count++;
			}
		}
		return count;
	}

	public long countSelectedPositions(int startIndex, int endIndex) {
		return selectionModel.countSelectedPositions(startIndex, endIndex);
	}

	public void moveSelectionRightIfGapIsPresent(int steps) {
		// TODO Auto-generated method stub
		
	}

	public void moveSelectionLeftIfGapIsPresent(int steps) {
		// TODO Auto-generated method stub
		
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

	public byte[] getBasesBetween(int startIndexInclusive, int endIndexInclusive) {
		// TODO Auto-generated method stub
		return null;
	}

	public void replaceSelectedBasesWithChar(char newChar) {
		// TODO Auto-generated method stub
	}

	public void realignNucleotidesUseThisAASequenceAsTemplate(
			byte[] allBasesAsByteArray) {
		// TODO Auto-generated method stub
	}

	public void selectAllBasesUntilGap(int x) {
		// TODO Auto-generated method stub
		
	}

	public char getCharAtPos(int n) {
		return (char) getBaseAsIntAtPos(n);
	}

	public void insertGapLeftOfSelectedBase() {
		// TODO Auto-generated method stub	
	}

	public void insertGapRightOfSelectedBase() {
		// TODO Auto-generated method stub	
	}

	public void deleteAllGaps() {
		// TODO Auto-generated method stub	
	}

	public int getID() {
		return id;
	}

	public int getPosOfSelectedIndex(int posInSeq) {
		return selectionModel.countPositionsUntilSelectedCount(posInSeq);
	}

	public int compareTo(Sequence other) {
		return getName().compareTo(other.getName());
	}


	public boolean isAllSelected() {
		return selectionModel.isAllSelected();
	}
	
	public int indexOf(char testChar) {
		for(int n = 0; n < getLength(); n++){
			int base = getBaseAsIntAtPos(n);
			if((char)base == testChar){
				return n;
			}
		}
		return -1;
	}
		
	
	public int countChar(char targetChar, int startpos, int endpos) {
		int count = 0;
		
		for(int n = startpos; n < getLength() && n < endpos; n++){
			int base = getBaseAsIntAtPos(n);
			if((char)base == targetChar){
				count++;
			}
		}

		return count;
	}


}
