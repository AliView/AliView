package aliview.sequences;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.AliView;
import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequencelist.Interval;
import aliview.utils.ArrayUtilities;

// todo can save memory by changing data implementation into byte instead of char
public class BasicSequence implements Sequence, Comparable<Sequence> {
	private static final Logger logger = Logger.getLogger(BasicSequence.class);
	private boolean simpleName = false;

	// TODO what is this selection offset?
	public int selectionOffset = 0;
	protected Bases bases;
	// TranslatedBases has to be volatile so no problems araise with the double lock in the lazy creation below
	// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
	protected TranslatedBases translatedBases;
	protected SequenceSelectionModel selectionModel;
	private AlignmentListModel alignmentModel;
	protected String name;
	protected int id;
	

	public BasicSequence(){
		this.id = SequenceUtils.createID();
		selectionModel = new DefaultSequenceSelectionModel();
	}
	
	public BasicSequence(Bases bases) {
		this();
		this.bases = bases;
	}

	public BasicSequence(BasicSequence template) {
		this.name = template.name;
		this.id = template.id;
		this.bases = template.getBases().getCopy();
		this.alignmentModel = template.alignmentModel;
		this.selectionModel = createNewSelectionModel();
	}
	
	public Sequence getCopy() {
		return new BasicSequence(this);
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

	public boolean isTranslated() {
		if(getAlignmentModel() != null){
			return getAlignmentModel().isTranslated();
		}
		return false;
	}
	
	public int getLength() {
		return getBases().getLength();
	}
	
	public int getNonTranslatedLength() {
		return getNonTranslatedBases().getLength();
	}
	
	public byte[] getGapPaddedCodonInTranslatedPos(int pos) {
		return getTranslatedBases().getGapPaddedCodonInTranslatedPos(pos);
	}
	
	public boolean isCodonSecondPos(int pos) {
		return getTranslatedBases().isCodonSecondPos(pos);
	}

	protected Bases getBases(){
		if(isTranslated()){
			return getTranslatedBases();
		}
		return this.bases;
	}
	
	protected Bases getNonTranslatedBases(){
		return this.bases;
	}

	private TranslatedBases getTranslatedBases(){
		
		if(translatedBases == null){
			// this is double locked to avoid synchronized block after the lazy initialization of TranslatedBases object
			// TranslatedBases has to be declared volatile above
			// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html and http://en.wikipedia.org/wiki/Double-checked_locking
			synchronized(this){
				if(translatedBases == null){
					translatedBases = new TranslatedBases(this.bases, this);
				}
			}
		}
		return translatedBases;
	}

	public SequenceSelectionModel createNewSelectionModel(){
		return new DefaultSequenceSelectionModel();
	}
	
	public int countStopCodon(){
		return getTranslatedBases().countStopCodon();
	}

	public AminoAcid getTranslatedAminoAcidAtNucleotidePos(int x) {
		return getTranslatedBases().getAminoAcidAtNucleotidePos(x);
	}
	
	public AminoAcidAndPosition getNoGapAminoAcidAtNucleotidePos(int target){
		return getTranslatedBases().getNoGapAminoAcidAtNucleotidePos(target);
	}

	public byte getBaseAtPos(int n){
		return getBases().get(n);
	}

	public char getCharAtPos(int n) {
		return (char) getBaseAtPos(n);
	}

	// TODO 
	public boolean isBaseSelected(int n){
		return selectionModel.isSelected(n);	
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
			for(int n = 0;n < getBases().getLength();n++){
				if(selectionModel.isSelected(n) == true){					
					selection.append( getBases().charAt(n) );
				}
			}
		}
		return selection.toString();
	}


	public byte[] getSelectedBasesAsByte(){
		byte[] bases = null;
		bases = getSelectedBasesAsString().toString().getBytes();
		return bases;
	}

	public String getBasesAsString(){
		String baseString = "";
		baseString = bases.toString();
		return baseString;
	}

	public void writeBases(OutputStream out) throws IOException{
		int length = getBases().getLength();
		for(int n = 0; n < length; n++){
			out.write(getBases().get(n));
		}	
	}

	public void writeBases(Writer out) throws IOException{
		int length = getBases().getLength();
		for(int n = 0; n < length; n++){
			out.write(getBases().get(n));
		}
	}
	
	public void writeBasesBetween(int start, int end, Writer out) throws IOException {
		for(int n = start; n <= end; n++){
			out.write( getBases().charAt(n));
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

	public Interval find(Pattern pattern, int startPos){
		// Allocate a Matcher object from the compiled regex pattern,
		// and provide the input to the Matcher
		String basesAsString = getBases().toString();
		Matcher matcher = pattern.matcher(basesAsString);

		Interval foundInterval = null;
		boolean wasFound = matcher.find(startPos);
		if(wasFound){
			int foundStart = matcher.start();
			int foundEnd = matcher.end() - 1;
			foundInterval = new Interval(foundStart, foundEnd);
		}
		else{
			//logger.info("not found");
		}
		return foundInterval;
	}
	
	public int find(byte find, int startPos){	
		for(int n = startPos; n < getBases().getLength(); n++){
			if(find == getBases().get(n)){
				return n;
			}
		}
		return -1;
	}
	
	public int getFirstSelectedPosition() {
		return selectionModel.getFirstSelectedPosition();
	}
	
	public int getLastSelectedPosition() {
		return selectionModel.getLastSelectedPosition(this.getLength());
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
		if(hasSelection()){
			// loop through all bases and see if it is selected - this is just as
			// fast as trying to separate the selected ones first and then only doing them
			for(int n = 0;n < getBases().getLength();n++){
				if(isBaseSelected(n) == true){
					getBases().set(n, newBase);
				}
			}
		}
	}

	public int[] getSequenceAsBaseVals() {
		int[] baseVals = new int[getBases().getLength()];
		for(int n = 0;n < getBases().getLength() ;n++){
			baseVals[n] = NucleotideUtilities.baseValFromChar((char) getBases().get(n));
		}	
		return baseVals;	
	}


	public void insertGapLeftOfSelectedBase(){	
		// get first selected position
		int position = getFirstSelectedPosition();
		if(rangeCheck(position)){
			insertGapAt(position);
		}		
	}

	public void insertGapRightOfSelectedBase(){
		// get first selected position
		int position = getLastSelectedPosition();
		if(rangeCheck(position+1)){
			insertGapAt(position+1);
		}	
	}

	public boolean isGapRightOfSelection(){
		return isGapRightOfSelection(1);
	}
	
	public boolean isEndRightOfSelection(){
		int rightSelected = getLastSelectedPosition();
		if(rightSelected + 1 == getLength()){
			return true;
		}
		return false;
	}
	
	public boolean isGapOrEndRightOfSelection(){
		if(isEndRightOfSelection()){
			return true;
		}
		else{
			return isGapRightOfSelection();
		}
	}
	
	public boolean isGapLeftOfSelection(){
		return isGapLeftOfSelection(1);
	}

	public boolean isGapRightOfSelection(int offset){
		boolean isGap = false;
		int rightSelected = getLastSelectedPosition();
		if(rangeCheck(rightSelected) && rangeCheck(rightSelected+offset)){
			if(NucleotideUtilities.isGap(getBaseAtPos(rightSelected + offset))){
				isGap = true;
			}
		}
		return isGap;
	}

	public boolean isGapLeftOfSelection(int offset){
		boolean isGap = false;
		int leftSelected = getFirstSelectedPosition();
		if(rangeCheck(leftSelected) && rangeCheck(leftSelected-offset)){
			if(NucleotideUtilities.isGap(getBaseAtPos(leftSelected - offset))){
				isGap = true;
			}
		}
		return isGap;
	}


	public void deleteGapLeftOfSelection(){
		// get first selected position
		int leftPosition = getFirstSelectedPosition();
		if(rangeCheck(leftPosition-1)){
			// only if gap is left of selection
			if(NucleotideUtilities.isGap(getBaseAtPos(leftPosition-1))){
				deleteBase(leftPosition-1);
			}				
		}
	}

	public void deleteGapRightOfSelection() {
		// get first selected position
		int rightPosition = getLastSelectedPosition();
		if(rangeCheck(rightPosition+1)){
			// only if gap is left of selection
			if(NucleotideUtilities.isGap(getBaseAtPos(rightPosition+1))){
				deleteBase(rightPosition+1);
			}				
		}

	}

	public void moveSelectionRightIfGapOrEndIsPresent(int steps) {

		for(int m = 0; m < steps; m++){
			// get first selected position
			int leftPosition = getFirstSelectedPosition();
			int rightPosition = getLastSelectedPosition();
			//if(rangeCheck(leftPosition) && rangeCheck(rightPosition+1)){

				// only if gap is right of selection
				if(isGapOrEndRightOfSelection()){

					// move bases one step at the time from right to left
					for(int n = rightPosition; n >= leftPosition; n--){
						// move residue
						getBases().moveBaseRight(n);
						//getBases().set(n + 1, getBases().get(n));
						// move selection
						// move selection
						if(isBaseSelected(n)){
							setSelectionAt(n+1);
						}else{
							clearSelectionAt(n+1);
						}
					}
					// and finally put the gap at the left side
					getBases().set(leftPosition,'-');
					clearSelectionAt(leftPosition);
				}
			//}
		}
	}

	public void moveSelectionLeftIfGapIsPresent(int steps) {

		for(int m = 0; m < steps; m++){
			// get first selected position
			int leftPosition = getFirstSelectedPosition();
			int rightPosition = getLastSelectedPosition();

			if(rangeCheck(leftPosition-1) && rangeCheck(rightPosition)){

				// only if gap is left of selection
				if(isGapLeftOfSelection()){

					for(int n = leftPosition; n <= rightPosition; n++){
						
						// move residue
						getBases().moveBaseLeft(n);
						//getBases().set(n - 1, getBases().get(n));
						
						// move selection
						if(isBaseSelected(n)){
							setSelectionAt(n-1);
						}else{
							clearSelectionAt(n-1);
						}
						
						
						
					}
					// and finally put the gap at the right side
					getBases().set(rightPosition, '-');
					clearSelectionAt(rightPosition);
				}				
			}
		}
	}
	

	public void moveSelectedResiduesRightIfGapOrEndIsPresent(){
		moveSelectionRightIfGapOrEndIsPresent(1);
	}

	public void moveSelectedResiduesLeftIfGapIsPresent(){
		moveSelectionLeftIfGapIsPresent(1);
	}


	public void insertGapAt(int n){
		getBases().insertAt(n, SequenceUtils.GAP_SYMBOL);
		// do the same with selmodel
		selectionModel.insertNewPosAt(n);
	}

	public int[] getSelectedPositions() {
		return selectionModel.getSelectedPositions(0, this.getLength() - 1);
	}

	public void replaceBases(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases) {
		getBases().replace(startReplaceIndex, stopReplaceIndex, insertBases);
	}

	public void setSelectionAt(int i){
		selectionModel.setSelectionAt(i);
	}
	
	public void clearSelectionAt(int i){
		selectionModel.clearSelectionAt(i);
	}

	public void setSelection(int startIndex, int endIndex, boolean clearFirst){
		startIndex = Math.max(0, startIndex);
		endIndex = Math.min(this.getLength() - 1, endIndex);
		selectionModel.setSelection(startIndex, endIndex, clearFirst);
	}

	private boolean rangeCheck(int pos) {
		if(bases != null &&pos >= 0 && pos < bases.getLength()){
			return true;
		}
		return false;
	}

	public void deleteSelectedBases(){	

		int[] toDelete = selectionModel.getSelectedPositions(0, this.getLength() - 1);	
		getBases().delete(toDelete);	
		createNewSelectionModel();
	}

	public void deleteBase(int index){	
		getBases().delete(index);
		selectionModel.removePosition(index);
	}

	public void reverseComplement() {
		reverse();
		complement();
	}

	public void complement() {
		getBases().complement();
	}

	public void reverse(){
		getBases().reverse();
	}


	public void rightPadSequenceWithGaps(int finalLength) {
		
//		logger.info("finalLength" + finalLength);
//		logger.info("getBases().getLength()" + getBases().getLength());
		
		int addCount = finalLength - getBases().getLength();
		if(addCount > 0){
			byte[] additional = new byte[addCount];
			Arrays.fill(additional, SequenceUtils.GAP_SYMBOL);
			getBases().append(additional);
		}
		
		
//		while(getBases().getLength() < finalLength){
//			logger.info("append");
//			getBases().append(new byte[]{SequenceUtils.GAP_SYMBOL});
//		}
//		logger.info("after getBases().getLength()" + getBases().getLength());
		
	}

	public void leftPadSequenceWithGaps(int amount) {

		if(amount > 0){

			byte[] padding = new byte[amount];
			
			Arrays.fill(padding, SequenceUtils.GAP_SYMBOL);
			
			getBases().insertAt(0,padding);

			selectionModel.leftPad(amount);

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

		int[] toDelete = new int[nTruePos];
		
		int deleteCount = 0;
		for(int n = 0; n < getBases().getLength() && n < mask.length ; n++){
			if(mask[n] == true){
				toDelete[deleteCount] = n;
				deleteCount ++;
			}
		}
		
		getBases().delete(toDelete);
		
		// and do same for sel-model
		for(int n = mask.length-1; n >= 0; n--){
			if(mask[n] == true){
				selectionModel.removePosition(n);
			}
		}
	}

	public void append(String moreInterleavedsequence) {
		getBases().append(moreInterleavedsequence.getBytes());
	}


	public boolean hasSelection() {
		return selectionModel.hasSelection();
	}

	public void clearBase(int pos) {
		getBases().set(pos, SequenceUtils.GAP_SYMBOL);
	}

	public byte[] getAllBasesAsByteArray(){
		return getBases().toByteArray();
	}

	public byte[] getBasesBetween(int startIndexInclusive, int endIndexInclusive){
		return getBases().toByteArray(startIndexInclusive, endIndexInclusive);
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		boolean isEmpty = true;
		for(int n = 0; n < getBases().getLength(); n++){
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
				setSelectionAt(n);
			}
		}	
		// and then left 
		for(int n = x; n >=0; n--){
			if(NucleotideUtilities.isGap(getBaseAtPos(n))){
				break;
			}
			else{
				setSelectionAt(n);
			}
		}
	}
	
	public void selectionExtendRight() {
		if(hasSelection()){
			int lastSelectedPos = getLastSelectedPosition();
			int seqEndPos = getLength() - 1;
			setSelection(lastSelectedPos, seqEndPos, true);
		}
	}
	
	public void selectionExtendLeft() {
		if(hasSelection()){
			int firstSelectedPos = getLastSelectedPosition();
			setSelection(0, firstSelectedPos, true);
		}
	}
	
	public void invertSelection(){
		selectionModel.invertSelection(getLength());
	}
	

	public void deleteAllGaps(){
		
		// no matter if translated or not - always remove all gaps from backend sequence
		getNonTranslatedBases().deleteAll(SequenceUtils.GAP_SYMBOL);
		//getUnBases().deleteAll(SequenceUtils.GAP_SYMBOL);
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
		for(int n = 0; n < getBases().getLength(); n++){
			if(getCharAtPos(n) == targetChar){
				count ++;
			}
		}
		return count;
	}

	public boolean contains(char testChar) {
		boolean contains = false;
		for(int n = 0; n < getBases().getLength(); n++){
			if(getCharAtPos(n) == testChar){
				contains = true;
				break;
			}
		}
		return contains;
	}

	public int indexOf(char testChar) {
		for(int n = 0; n < getBases().getLength(); n++){
			if(getCharAtPos(n) == testChar){
				return n;
			}
		}
		return -1;
	}

	public int countChar(char targetChar, int startpos, int endpos) {
		int count = 0;

		for(int n = startpos; n < endpos && n < getBases().getLength(); n++){
			if(targetChar == getBases().charAt(n)){
				count ++;
			}
		}
		return count;
	}
	
	public void setAlignmentModel(AlignmentListModel model){
		this.alignmentModel = model;
	}
	
	public AlignmentListModel getAlignmentModel() {
		return alignmentModel;
	}

	

}
