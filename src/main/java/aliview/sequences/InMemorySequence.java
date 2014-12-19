package aliview.sequences;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.utils.ArrayUtilities;

// todo can save memory by changing data implementation into byte instead of char
public class InMemorySequence implements Sequence, Comparable<Sequence> {
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	private static final Logger logger = Logger.getLogger(InMemorySequence.class);
	private boolean simpleName = false;

	// TODO what is this selection offset?
	public int selectionOffset = 0;
	private byte[] bases;
	protected SequenceSelectionModel selectionModel;
	protected String name;
	private int id;

	public InMemorySequence(){
		selectionModel = new DefaultSequenceSelectionModel();
	}

	public InMemorySequence(String name, String basesAsString) {
		this(name, basesAsString.getBytes());	
	}

	public InMemorySequence(String name, byte[] bytes) {
		// replace all . with -
		if(bytes != null){
			ArrayUtilities.replaceAll(bytes, (byte) '.', (byte) '-');
		}

		this.bases = bytes;
		this.name = name;
		this.id = SequenceUtils.createID();
		this.selectionModel = new DefaultSequenceSelectionModel(); 
	}


	public InMemorySequence(InMemorySequence template) {
		this.name = template.name;
		this.id = template.id;
		this.bases = ArrayUtils.clone(template.bases);
		this.selectionModel = new DefaultSequenceSelectionModel();
	}

	public Sequence getCopy() {
		return new InMemorySequence(this);
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getSimpleName(){
		return name;
	}


	public int getLength() {
		return getBases().length;
	}

	private byte[] getBases(){
		return this.bases;
	}


	public void createNewSelectionModel(){
		this.selectionModel = new DefaultSequenceSelectionModel();
	}


	public byte getBaseAtPos(int n){
		if(getBases().length > n){
			return getBases()[n];
		}
		return 0;
	}

	public char getCharAtPos(int n) {
		return (char) getBaseAtPos(n);
	}

	// TODO 
	public boolean isBaseSelected(int n){
		return selectionModel.isBaseSelected(n);	
	}

	public void setBaseSelection(int n, boolean selected){
		selectionModel.setSelectionAt(n, selected);
	}

	public void clearAllSelection(){
		selectionModel.clearAll();
	}

	public void selectAllBases(){
		selectionModel.selectAll();
	}

	public long countSelectedPositions(int startIndex, int endIndex) {
		return selectionModel.countSelectedPositions(startIndex, endIndex);
	}

	public String getSelectedBasesAsString(){
		StringBuilder selection = new StringBuilder();
		if(selectionModel.hasSelection()){
			//logger.info("hasSel");
			for(int n = 0;n < getBases().length;n++){
				if(selectionModel.isBaseSelected(n) == true){					
					selection.append( (char) getBases()[n] );
				}
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

	public void writeBases(OutputStream out) throws IOException{
		out.write(getBases());
	}

	public void writeBases(Writer out) throws IOException{
		for(byte next: getBases()){
			out.write( (char) next);
		}
	}

	public void toggleSimpleName(){
		this.simpleName = !simpleName;
	}

	public String toString(){
		if(simpleName == true){
			return getSimpleName();
		}else{
			return getName();
		}
	}


	public int findAndSelect(Pattern pattern, int startPos){
		// Allocate a Matcher object from the compiled regex pattern,
		// and provide the input to the Matcher
		String basesAsString = new String(getBases());
		Matcher matcher = pattern.matcher(basesAsString);

		int findPos = -1;	
		boolean wasFound = matcher.find(startPos);
		if(wasFound){
			int foundStart = matcher.start();
			int foundEnd = matcher.end();
			selectionModel.setSelection(foundStart,foundEnd -1,true);
			findPos = foundStart;
		}
		else{
			//logger.info("not found");
		}
		return findPos;
	}

	public int getFirstSelectedPosition() {
		return selectionModel.getFirstSelectedPosition();
	}
	
	public int getLastSelectedPosition() {
		return selectionModel.getLastSelectedPosition();
	}
	

	/*
	 * 
	 * TODO could skip
	 * 
	 */
	public void replaceSelectedBasesWithGap(){
		replaceSelectedBasesWithChar((char)SequenceUtils.GAP_SYMBOL);
	}

	public void replaceSelectedBasesWithChar(char newChar) {
		byte newBase = (byte) newChar;
		if(selectionModel.hasSelection()){
			// loop through all bases and see if it is selected - this is just as
			// fast as trying to separate the selected ones first and then only doing them
			for(int n = 0;n < getBases().length;n++){
				if(selectionModel.isBaseSelected(n) == true){
					getBases()[n] = newBase;
				}
			}
		}
	}

	public int[] getSequenceAsBaseVals() {
		int[] baseVals = new int[getBases().length];
		for(int n = 0;n < getBases().length ;n++){
			baseVals[n] = NucleotideUtilities.baseValFromChar((char) getBases()[n]);
		}	
		return baseVals;	
	}


	public void insertGapLeftOfSelectedBase(){	
		// get first selected position
		int position = selectionModel.getFirstSelectedPosition();
		if(rangeCheck(position)){
			insertGapAt(position);
		}		
	}

	public void insertGapRightOfSelectedBase(){
		// get first selected position
		int position = selectionModel.getLastSelectedPosition();
		if(rangeCheck(position+1)){
			insertGapAt(position+1);
		}	
	}

	public boolean isGapRightOfSelection(){
		return isGapRightOfSelection(1);
	}
	public boolean isGapLeftOfSelection(){
		return isGapLeftOfSelection(1);
	}

	public boolean isGapRightOfSelection(int offset){
		boolean isGap = false;
		int rightSelected = selectionModel.getLastSelectedPosition();
		if(rangeCheck(rightSelected) && rangeCheck(rightSelected+offset)){
			if(NucleotideUtilities.isGap(getBaseAtPos(rightSelected + offset))){
				isGap = true;
			}
		}
		return isGap;
	}

	public boolean isGapLeftOfSelection(int offset){
		boolean isGap = false;
		int leftSelected = selectionModel.getFirstSelectedPosition();
		if(rangeCheck(leftSelected) && rangeCheck(leftSelected-offset)){
			if(NucleotideUtilities.isGap(getBaseAtPos(leftSelected - offset))){
				isGap = true;
			}
		}
		return isGap;
	}


	public void deleteGapLeftOfSelection(){
		// get first selected position
		int leftPosition = selectionModel.getFirstSelectedPosition();
		if(rangeCheck(leftPosition-1)){
			// only if gap is left of selection
			if(NucleotideUtilities.isGap(getBaseAtPos(leftPosition-1))){
				deleteBase(leftPosition-1);
			}				
		}
	}

	public void deleteGapRightOfSelection() {
		// get first selected position
		int rightPosition = selectionModel.getLastSelectedPosition();
		if(rangeCheck(rightPosition+1)){
			// only if gap is left of selection
			if(NucleotideUtilities.isGap(getBaseAtPos(rightPosition+1))){
				deleteBase(rightPosition+1);
			}				
		}

	}


	public void moveSelectionRightIfGapIsPresent(int steps) {

		for(int m = 0; m < steps; m++){
			// get first selected position
			int leftPosition = selectionModel.getFirstSelectedPosition();
			int rightPosition = selectionModel.getLastSelectedPosition();
			if(rangeCheck(leftPosition) && rangeCheck(rightPosition+1)){

				// only if gap is right of selection
				if(NucleotideUtilities.isGap(getBaseAtPos(rightPosition + 1))){

					// move bases one step at the time from right to left
					for(int n = rightPosition; n >= leftPosition; n--){
						getBases()[n + 1] = getBases()[n];						
						selectionModel.setSelectionAt(n+1, selectionModel.isBaseSelected(n));	
					}
					// and finally put the gap at the left side
					getBases()[leftPosition] = '-';
					selectionModel.setSelectionAt(leftPosition,false);
				}
			}
		}
	}

	public void moveSelectionLeftIfGapIsPresent(int steps) {

		for(int m = 0; m < steps; m++){
			// get first selected position
			int leftPosition = selectionModel.getFirstSelectedPosition();
			int rightPosition = selectionModel.getLastSelectedPosition();

			if(rangeCheck(leftPosition-1) && rangeCheck(rightPosition)){

				// only if gap is left of selection
				if(NucleotideUtilities.isGap(getBaseAtPos(leftPosition - 1))){

					for(int n = leftPosition; n <= rightPosition; n++){
						getBases()[n - 1] = getBases()[n];
						selectionModel.setSelectionAt(n-1, selectionModel.isBaseSelected(n));	
					}
					// and finally put the gap at the right side
					getBases()[rightPosition] = '-';
					selectionModel.setSelectionAt(rightPosition,false);
				}				
			}
		}
	}

	public void moveSelectionRightIfGapIsPresent(){
		moveSelectionRightIfGapIsPresent(1);
	}

	public void moveSelectionLeftIfGapIsPresent(){
		moveSelectionLeftIfGapIsPresent(1);
	}

	/*
	public void insertGapAtLastSelectedBaseMoveLeft(){

		// get first selected position
		int position = getRightSelectedBasePosition();
		if(position != -1){
			insertGapAtMoveLeft(position);
		}	
	}
	 */

	private void insertGapAt(int n) {
		byte[] newBases = ArrayUtils.add(getBases(), n, SequenceUtils.GAP_SYMBOL);
		// do the same with selmodel
		selectionModel.insertNewPosAt(n);
		setBases(newBases);
	}

	//	private void insertGapAtMoveLeft(int n) {
	//		insertGapAtMoveLeft(n)
	//		
	//		ArrayUtils.add(getBases(), n, SequenceUtils.GAP_SYMBOL);
	//		
	//		// make same with selection
	//		selectionModel.insertNewPosAtAndMoveLeft(n);
	//		
	//		setBases(newBases);
	//	}
	/*
	private void removeBaseAt(int n) {
		// 
		byte[] newBases = new byte[getBases().length + 1];
		System.arraycopy(getBases(), 0, newBases, 0, n);
		newBases[n] = '-';
		System.arraycopy(getBases(), n, newBases, n + 1, newBases.length - n - 1);

		// make same with selection
		boolean[] newSelection = new boolean[getBasesSelection().length + 1];
		System.arraycopy(getBasesSelection(), 0, newSelection, 0, n);
		newSelection[n] = false;
		System.arraycopy(getBasesSelection(), n, newSelection, n + 1, newSelection.length - n - 1);

		setBases(newBases, newSelection);
	}

	private void setBasesSelection(boolean[] newSelection) {
		baseSelection = newSelection;	
	}
	 */
	public int[] getSelectedPositions() {
		return selectionModel.getSelectedPositions(0, this.getLength() - 1);
	}

	/*
	private void replaceBases(int fromIndex, int toIndex, String newBases) {
		try {
			replaceBases(fromIndex, toIndex, newBases.getBytes(TEXT_FILE_BYTE_ENCODING));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 */

	public void replaceBases(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases) {

		int newLength = this.getBases().length - (stopReplaceIndex + 1 - startReplaceIndex) + insertBases.length;

		// TODO could check if length is less - then just clear and insert
		byte[] newBases = new byte[newLength];

		// copy first untouched part of sequence
		System.arraycopy(getBases(), 0, newBases, 0, startReplaceIndex);

		// copy insertbases
		System.arraycopy(insertBases, 0, newBases, startReplaceIndex, insertBases.length);

		// copy last untouched part of sequence - if there is one
		if(stopReplaceIndex < getBases().length - 1){
			System.arraycopy(getBases(), stopReplaceIndex + 1, newBases, startReplaceIndex + insertBases.length, getBases().length - (stopReplaceIndex + 1));
		}

		setBases(newBases);

	}

	public void setSelectionAt(int i, boolean selected){
		selectionModel.setSelectionAt(i, selected);
	}

	public void selectBases(int startIndex, int endIndex){
		if(!rangeCheck(startIndex) || !rangeCheck(endIndex)){
			return;
		}
		selectionModel.setSelection(startIndex, endIndex, true);
	}

	private boolean rangeCheck(int pos) {
		if(bases != null &&pos >= 0 && pos < bases.length){
			return true;
		}
		return false;
	}


	public void deleteSelectedBases(){	
		// create new array size removed selected bases
		byte[] newBases = new byte[getBases().length - selectionModel.countSelectedPositions(0, this.getLength() - 1)];

		int newIndex = 0;
		for(int n = 0;n < bases.length ;n++){
			// copy only unselected bases to new array
			if(selectionModel.isBaseSelected(n) == false){
				newBases[newIndex] = getBases()[n];
				newIndex ++;
			}
		}
		setBases(newBases);
		createNewSelectionModel();
	}

	public void deleteBase(int index){	
		// create new array size removed selected bases
		byte[] newBases = ArrayUtils.remove(getBases(), index);
		selectionModel.removePosition(index);
		setBases(newBases);
	}



	public void reverseComplement() {
		reverse();
		complement();
	}

	public void complement() {
		NucleotideUtilities.complement(getBases());	
	}

	public void reverse(){
		ArrayUtils.reverse(getBases());	
	}


	public void rightPadSequenceWithGaps(int amount) {

		if(amount > 0){

			byte[] newBases = new byte[getBases().length + amount];
			System.arraycopy(getBases(), 0, newBases, 0, getBases().length);

			// fill last pos with gaps
			for(int n = 1; n <= amount; n++){
				newBases[newBases.length - n] = SequenceUtils.GAP_SYMBOL;
			}

			setBases(newBases);
		}	
	}

	public void leftPadSequenceWithGaps(int amount) {

		if(amount > 0){

			byte[] newBases = new byte[getBases().length + amount];
			System.arraycopy(getBases(), 0, newBases, amount, getBases().length);

			// fill first pos with gaps
			for(int n = 0; n < amount; n++){
				newBases[n] = SequenceUtils.GAP_SYMBOL;
			}

			selectionModel.leftPad(amount);

			setBases(newBases);
		}	

	}

	public String getCitatedName() {
		String name = getName();
		name = StringUtils.remove(name, '\'');
		logger.info(name);
		name = StringUtils.remove(name, '\"');
		name = StringUtils.remove(name, '>');
		name = "'" + name + "'";
		return name;
	}

	public String getBasesAtThesePosAsString(ArrayList<Integer> allWantedPos) {
		StringBuilder allPos = new StringBuilder();
		for(Integer aPos: allWantedPos){
			allPos.append((char)getBaseAtPos(aPos.intValue()));
		}
		return allPos.toString();
	}

	public void deleteBasesFromMask(boolean[] mask){
		int nTruePos = ArrayUtilities.count(mask, true);
		
		int maxLen = Math.max(mask.length, getBases().length);
		byte[] newBases = new byte[maxLen - nTruePos];
		int destPos = 0;				
		for(int n = 0; n < getBases().length && n < mask.length ; n++){
			if(mask[n] == true){
				// dont copy - this pos is to be deleted

			}else{
				newBases[destPos] = getBases()[n];
				destPos ++;
			}
		}
		// and do same for sel-model
		for(int n = mask.length-1; n >= 0; n--){
			if(mask[n] == true){
				selectionModel.removePosition(n);
			}
		}

		setBases(newBases);
	}

	public void append(String moreInterleavedsequence) {
		byte[] newArray = ArrayUtils.addAll(getBases(), moreInterleavedsequence.getBytes());
		setBases(newArray);
	}


	public boolean hasSelection() {
		return selectionModel.hasSelection();
	}

	public void clearBase(int pos) {
		getBases()[pos] = SequenceUtils.GAP_SYMBOL;
	}

	public byte[] getAllBasesAsByteArray(){
		return getBasesBetween(0,getBases().length);
	}

	public byte[] getBasesBetween(int startIndexInclusive, int endIndexInclusive) {
		byte[] newBases = ArrayUtils.subarray(getBases(), startIndexInclusive, endIndexInclusive + 1);
		return newBases;	
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		boolean isEmpty = true;
		for(int n = 0; n < getBases().length; n++){
			if(getBaseAtPos(n) == '-' || getBaseAtPos(n) =='?'){

			}else{
				isEmpty = false;
				break;
			}
		}

		return isEmpty;
	}

	public int getUngapedLength() {
		return getUngapedPos(this.getLength());
	}

	public int compareTo(Sequence other) {
		return getName().compareTo(other.getName());
	}

	public void setBases(byte[] bases) {
		this.bases = bases;
		if(selectionModel == null){
			createNewSelectionModel();
		}
	}

	public int getUngapedPos(int position){

		// TODO this is a problem with large sequences
		if(position > 1000000){
			return -1;
		}
		

		int posCount = 0;
		int gapCount = 0;
		for(int n = 0; n <= position; n++){
			if(NucleotideUtilities.isGap(getBaseAtPos(n))){
				gapCount ++;
			}else{
				posCount ++;
			}
		}	
		return posCount;
	}

	public String getUngapedSequence() {
		StringBuilder ungapedSeq = new StringBuilder(getLength());
		for(int n = 0; n < getLength(); n++){
			byte base = getBaseAtPos(n);
			if(NucleotideUtilities.isGap(base)){
				// skip this one
			}else{
				ungapedSeq.append((char)base);
			}
		}	
		return ungapedSeq.toString();
	}

	public void selectAllBasesUntilGap(int x) {
		// loop right until gap
		for(int n = x; n < getLength(); n++){
			if(NucleotideUtilities.isGap(getBaseAtPos(n))){
				break;
			}
			else{
				selectionModel.setSelectionAt(n, true);
			}
		}	
		// and then left 
		for(int n = x; n >=0; n--){
			if(NucleotideUtilities.isGap(getBaseAtPos(n))){
				break;
			}
			else{
				selectionModel.setSelectionAt(n, true);
			}
		}
	}
	
	public void selectionExtendRight() {
		if(selectionModel.hasSelection()){
			int lastSelectedPos = selectionModel.getLastSelectedPosition();
			int seqEndPos = getLength() - 1;
			selectionModel.setSelection(lastSelectedPos, seqEndPos, true);
		}
	}
	
	public void selectionExtendLeft() {
		if(selectionModel.hasSelection()){
			int firstSelectedPos = selectionModel.getLastSelectedPosition();
			selectionModel.setSelection(0, firstSelectedPos, true);
		}
	}
	
	public void invertSelection(){
		selectionModel.invertSelection(getLength());
	}
	

	public void deleteAllGaps(){
		setBases(getUngapedSequence().getBytes());
		createNewSelectionModel();
	}

	public int getID() {
		return id;
	}

	public int getPosOfSelectedIndex(int posInSeq) {
		return selectionModel.countPositionsUntilSelectedCount(posInSeq);
	}


	public boolean isAllSelected() {
		return selectionModel.isAllSelected();
	}

	public int countChar(char targetChar) {
		int count = 0;
		for(byte base: getBases()){
			if((char)base == targetChar){
				count ++;
			}
		}
		return count;
	}

	public boolean contains(char testChar) {
		boolean contains = false;
		for(byte base: getBases()){
			if((char)base == testChar){
				contains = true;
				break;
			}
		}
		return contains;
	}

	public int indexOf(char testChar) {
		int index = -1;
		for(byte base: getBases()){
			index ++;
			if((char)base == testChar){
				break;
			}
		}
		return index;
	}

	public int countChar(char targetChar, int startpos, int endpos) {
		int count = 0;

		for(int n = startpos; n < endpos && n < getBases().length; n++){
			if(targetChar == (char)getBases()[n]){
				count ++;
			}
		}
		return count;
	}

}
