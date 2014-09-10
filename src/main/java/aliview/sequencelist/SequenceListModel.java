package aliview.sequencelist;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.log4j.Logger;

import utils.DialogUtils;
import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;

import aliview.AATranslator;
import aliview.AminoAcid;
import aliview.FileFormat;
import aliview.GeneticCode;
import aliview.NucleotideUtilities;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.NucleotideHistogram;
import aliview.sequences.InMemorySequence;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;

public class SequenceListModel extends DefaultListModel implements Iterable<Sequence>{
	
	/**
	 * 
	 */
	private static final String LF = System.getProperty("line.separator");
	private static final long serialVersionUID = -8081215660929212156L;
	private static final Logger logger = Logger.getLogger(SequenceListModel.class);
	List<Sequence> sequences;
	protected FileFormat fileFormat;
	protected int sequenceType = SequenceUtils.TYPE_UNKNOWN;
	private int selectionOffset;
	private int cachedLongestSequenceName;
	private int cachedLongestSequenceLength;
	
	public SequenceListModel() {
		this.sequences = new ArrayList<Sequence>();
	}
	
	public SequenceListModel(List<Sequence> seq) {
		this.sequences = seq;
		sequencesChanged();
	}
	
	private void sequencesChanged() {
		// clear cached values
		cachedLongestSequenceName = -1;
		cachedLongestSequenceLength = -1;
		
		
	}

	public void setSequences(List<Sequence> list){
		if(list != null){
			this.sequences = list;
		}
		sequencesChanged();
	}

	public SequenceListModel(SequenceListModel template){
		ArrayList<Sequence> seqClone = new ArrayList<Sequence>();
		for (Sequence seq: template.sequences) {
	        seqClone.add(seq.getCopy());
		}
		
		this.fileFormat = template.fileFormat;
		this.sequences = seqClone;
		this.sequenceType = template.sequenceType;
		sequencesChanged();
	}
	
	public SequenceListModel getCopy(){
		return new SequenceListModel(this);
	}
	
	public SequenceListModel getCopyShallow(){
		SequenceListModel copy = new SequenceListModel();	
		for(Sequence seq: this.sequences) {
	        copy.add(seq);
		}
		copy.fileFormat = fileFormat;
		copy.sequenceType = sequenceType;
		return copy;
	}
	
	public Sequence getElementAt(int index){
		return sequences.get(index);
	}

	public int getSize() {
		return sequences.size();
	}

	public boolean removeElement(Sequence obj) {
		int index = sequences.indexOf(obj);
		boolean rv = sequences.remove(obj);
		if (index >= 0) {
		    fireIntervalRemoved(this, index, index);
		}
		return rv;
	}

	public void add(int index, Sequence seq) {
		sequences.add(index, seq);
		fireIntervalRemoved(this, index, index);
		sequencesChanged();
	}

	public Sequence get(int index) {
		return sequences.get(index);
	}
	
	public Sequence set(int index, Sequence element){
		sequencesChanged();
		return sequences.set(index, element);
		
	}

	public void removeAt(int index) {
		sequences.remove(index);
		fireIntervalRemoved(this, index, index);	
		sequencesChanged();
	}

	public void insertAt(Sequence seq, int index) {
		sequences.add(index, seq);
		fireIntervalRemoved(this, index, index);
		
	}

	public Iterator<Sequence> iterator() {
		return sequences.listIterator();
	}
	
	public void addAll(List<Sequence> moreSeqs) {
		sequences.addAll(moreSeqs);	
	}
	
	public void addAll(SequenceListModel otherSeqModel) {
		sequences.addAll(otherSeqModel.getSequences());
	}

	public void add(Sequence sequence) {
		sequences.add(sequence);
	}
		
	protected List<Sequence> getSequences() {
		return sequences;
	}
	
	public void sequenceListSizeChanged(int index0, int index1) {
		fireContentsChanged(this, index0, index1);
	}
	
	public int compare(Sequence o1, Sequence o2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	// From previous SequenceList Interface
	public int getLongestSequenceLength(){	
		if(cachedLongestSequenceLength <=0){
			int maxLen = 0;
			for(int n = 0; n < sequences.size(); n++){
				int len = sequences.get(n).getLength();
				if(len > maxLen){
					maxLen = len;
				}
			}
			cachedLongestSequenceLength = maxLen;
		}
		return cachedLongestSequenceLength;
	}
	
	public FileFormat getFileFormat() {
		return this.fileFormat;
	}

	public void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

		public int getSequenceType() {

			if(sequences.size() > 0 && sequenceType == SequenceUtils.TYPE_UNKNOWN){
				// TODO could figure out if not a sequence
				int gapCount = 0;
				int nucleotideCount = 0;
				
				
				// Loop through 5000 bases or sequence length
				Sequence testSeq = sequences.get(0);
				int maxLen = Math.min(5000, testSeq.getLength());
				for(int n = 0; n < maxLen; n++){
					byte base = testSeq.getBaseAtPos(n); 
					if(NucleotideUtilities.isGap(base)){
						gapCount ++;
					}else if(NucleotideUtilities.isNucleoticeOrIUPAC(base)){
						nucleotideCount ++;
					}
					else{
//						logger.info("other base=" + base);
					}
				}
				
				// allow 1 wrong base
				if(maxLen == 0 || (nucleotideCount + gapCount + 1 >= maxLen)){
					this.sequenceType = SequenceUtils.TYPE_NUCLEIC_ACID;
				}
				else{
					this.sequenceType = SequenceUtils.TYPE_AMINO_ACID;
				}
			}	
			sequencesChanged();
			return sequenceType;	
	}


	public void reverseComplement(Sequence[] selection) {
		if(selection != null){
			for(Sequence seq : selection){
				seq.reverseComplement();
			}
			sequencesChanged();
		}
	}

	public void deleteSequence(Sequence seq) {
		sequences.remove(seq);
		sequencesChanged();
	}
	
	public ArrayList<Sequence> deleteEmptySequences(){
		ArrayList<Sequence> toDelete = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.isEmpty()){
				toDelete.add(seq);
			}		
		}
		for(Sequence seq: toDelete){
			deleteSequence(seq);
		}
		return toDelete;
	}


	public void moveSequencesToBottom(Sequence[] selection) {
		List<Sequence> selAsList = Arrays.asList(selection);
		logger.info("removeAll");
		sequences.removeAll(selAsList);
		logger.info("addAll");
		sequences.addAll(selAsList);
		logger.info("seqChanged");
		sequencesChanged();
		/*
		for(Sequence seq: selection){
			removeElement(seq);
			add(seq);
		}
		*/
		
	}

	public void moveSequencesToTop(Sequence[] selection) {
		List<Sequence> selAsList = Arrays.asList(selection);
		sequences.removeAll(selAsList);
		sequences.addAll(0, selAsList);
		sequencesChanged();
		/*
		//int insertPos = 0;
		sequences.addAll(0, c)
		for(Sequence seq: selection){
			int index = sequences.indexOf(seq);
			removeElement(seq);
			insertAt(seq, insertPos);
			insertPos ++;
			
		}
		*/
	}
	
	public void moveSequencesTo(int index, Sequence[] selection) {
		if(index >= sequences.size()){
			index = sequences.size() - 1;
		}
		if(index < 0){
			index = 0;
		}
		
		// get current pos
		int current = sequences.indexOf(selection[0]);
		
		int diff = current - index;
		
		logger.info("diff" + diff);
		
		// loop sequences up or down
		if(diff > 0){
			for(int n = 0; n < diff; n++){
				moveSequencesUp(selection);
			}
		}
		if(diff < 0){
			// Add selection length because we are using the last index ad lead when counting
			for(int n = 0; n <= Math.abs(diff) - selection.length; n++){
				moveSequencesDown(selection);
			}
		}
		sequencesChanged();
	}

	public void moveSequencesUp(Sequence[] selection){
		for(Sequence seq: selection){
			int index = sequences.indexOf(seq);
			// break if we are at top
			if(index == 0){
				break;
			}
			Sequence previous = (Sequence) set(index - 1, seq);
			set(index,previous);
		}
		sequencesChanged();
	}

	public void moveSequencesDown(Sequence[] selection) {
		if(selection == null || selection.length == 0){
			return;
		}
		// Has to be done reverse (otherwise index problem)
		for(int n = selection.length - 1; n >=0 ; n--){
			Sequence seq = selection[n];
			int index = sequences.indexOf(seq);
			// break if we are at bottom
			if(index >= sequences.size() - 1){
				break;
			}
			Sequence previous = (Sequence) set(index + 1, seq);
			set(index, previous);
			
		}
		sequencesChanged();
		
	}

	public int indexOf(Sequence seq) {
		return sequences.indexOf(seq);
	}

	public ArrayList<Integer> getIndicesOfSequencesWithSelection() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int n = 0; n < sequences.size(); n++){
			if(sequences.get(n).hasSelection()){
				indices.add(new Integer(n));
			}
		}
		
		return indices;
	}


	public void selectSequencesWithIndex(int[] selectedIndex){
		for(int index: selectedIndex){
			sequences.get(index).selectAllBases();
		}
		
	}

	public void clearSelection() {
		for(Sequence seq: sequences){
			seq.clearAllSelection();
		}
	}

	public boolean isBaseSelected(int x, int y) {
		return sequences.get(y).isBaseSelected(x);
	}

	public void setSelectionAt(int xPos, int yPos, boolean b) {
		sequences.get(yPos).setSelectionAt(xPos,b);
	}

	public void writeSelectionAsFasta(Writer out) {
			int n = 0;
			for(Sequence sequence : this.sequences){
				if(sequence.hasSelection()){
//					logger.info("has sel");
					String tempSeq = sequence.getSelectedBasesAsString();
					try {
						//TODO maybe format fasta better
						out.append(">");
						out.append(sequence.getName());
						out.append(LF);
						out.append(sequence.getSelectedBasesAsString());

						out.append(LF);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					logger.info("WroteSeq=" + n);
				}
			}

		logger.info("Write done");
	}
	
	public void writeSelectedSequencesAsFasta(Writer out) {
		writeSelectedSequencesAsFasta(out, false);
	}
	
	public void writeSelectedSequencesAsFasta(Writer out, boolean useIDAsName) {
		for(Sequence sequence : this.sequences){
			if(sequence.hasSelection()){
				logger.info("has sel");
				writeSequenceAsFasta(sequence,out, useIDAsName);
			}
		}
		logger.info("Write done");
	}
	
	private void writeSequenceAsFasta(Sequence sequence, Writer out, boolean useIDAsName){
		try {
			//TODO maybe format fasta better
			out.append(">");
			if(useIDAsName){
				out.append(Integer.toString(sequence.getID()));
			}else{
				out.append(sequence.getName());
			}
			out.append(LF);
			
			sequence.writeBases(out);
			
			out.append(LF);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeUnSelectedSequencesAsFasta(Writer out) {
		writeUnSelectedSequencesAsFasta(out, false);
	}
	
	public void writeUnSelectedSequencesAsFasta(Writer out, boolean useIDAsName) {
		for(Sequence sequence : this.sequences){
			if(! sequence.hasSelection()){
				logger.info("has not sel");
				writeSequenceAsFasta(sequence, out, useIDAsName);
			}
		}
		logger.info("Write done");
	}

	public byte getBaseAt(int x, int y) {
		return sequences.get(y).getBaseAtPos(x);
	}

	public int getLengthAt(int y) {
		return sequences.get(y).getLength();
	}
	
	public long getSelectionSize(){
		long size = 0;
		for(Sequence sequence : this.sequences){
			size += sequence.countSelectedPositions(0, sequence.getLength());
		}
		return size;
	}

	
	public FindObject findAndSelect(FindObject findObject) {
		if(getSequenceType() == SequenceUtils.TYPE_AMINO_ACID){
			return findInAASequences(findObject);
		}
		else{
			return findInNucleotideSequences(findObject);
		}
	}
	
	public FindObject findInAASequences(FindObject findObj){
		String regex = findObj.getRegexSearchTerm();
		
		// Identical for AA and NUC search
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		for(int n = findObj.getNextFindSeqNumber(); n < this.getSize(); n++){
			Sequence seq = sequences.get(n);
			int position = seq.findAndSelect(pattern, findObj.getNextFindStartPos());
			if(position > -1){
				findObj.setNextFindSeqNumber(n);	
				// make sure it is not out of index
				findObj.setNextFindStartPos(Math.min(position + 1, getLongestSequenceLength() - 1));
				findObj.setFoundPos(position,n);
			}
			// not found in this seq - start again from 0
			findObj.setNextFindStartPos(0);

		}
		findObj.setNextFindSeqNumber(0);
		findObj.setNextFindStartPos(0);
		findObj.setIsFound(false);
		return findObj;
	}

	public FindObject findInNucleotideSequences(FindObject findObj) {
		String regex = findObj.getRegexSearchTerm();

		// lower-case before replace
		regex = regex.toLowerCase();

		// upac-codes
		regex = regex.replaceAll("w", "\\[tua\\]");
		regex = regex.replaceAll("n", "\\[agctu\\]");
		regex = regex.replaceAll("r", "\\[ag\\]");
		regex = regex.replaceAll("y", "\\[ctu\\]");
		regex = regex.replaceAll("m", "\\[ca\\]");
		regex = regex.replaceAll("k", "\\[tug\\]");

		regex = regex.replaceAll("s", "\\[cg\\]");
		regex = regex.replaceAll("b", "\\[ctug\\]");
		regex = regex.replaceAll("d", "\\[atug\\]");
		regex = regex.replaceAll("h", "\\[atuc\\]");
		regex = regex.replaceAll("v", "\\[acg\\]");
		regex = regex.replaceAll("n", "\\[agctu\\]");

		// Identical for AA and NUC search
				Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
				
				logger.info("startpos = " + findObj.getNextFindStartPos());
				
				for(int n = findObj.getNextFindSeqNumber(); n < this.getSize(); n++){
					Sequence seq = sequences.get(n);
					int position = seq.findAndSelect(pattern, findObj.getNextFindStartPos());
					if(position > -1){
						findObj.setNextFindSeqNumber(n);	
						// make sure it is not out of index
						findObj.setNextFindStartPos(Math.min(position + 1, getLongestSequenceLength() - 1));
						findObj.setFoundPos(position,n);
						findObj.setIsFound(true);
						return findObj;
					}
					// not found in this seq - start again from 0
					findObj.setNextFindSeqNumber(n + 1);
					findObj.setNextFindStartPos(0);
				}
				logger.info("beforereset = " + findObj.getNextFindStartPos());
				// nothing found reset everything
				findObj.setNextFindSeqNumber(0);
				findObj.setNextFindStartPos(0);
				findObj.setIsFound(false);
				return findObj;
	}

	public FindObject findInNames(FindObject findObj) {
		String uCaseSearchTerm = findObj.getSearchTerm().toUpperCase();
		if(findObj.isFindAll()){
			// find all clear all previous
			findObj.clearIndices();
			for(int n = 0; n < sequences.size(); n++){		
				Sequence seq = sequences.get(n);
				if(seq.getName().toUpperCase().indexOf(uCaseSearchTerm) > -1){
					logger.info("Found" + n);
					findObj.addFoundNameIndex(n);
					findObj.setIsFound(true);
				}		
			}
			// Find single one
		}else{
			int startIndex = findObj.getNextNameFindIndex();
			if(startIndex >= sequences.size()){
				startIndex = 0;
			}
			// only one to be found clear all previous
			findObj.clearIndices();
			for(int n = startIndex; n < sequences.size(); n++){		
				Sequence seq = sequences.get(n);
				if(seq.getName().toUpperCase().indexOf(uCaseSearchTerm) > -1){
					logger.info("Found" + n);
					findObj.setFoundNameIndex(n);
					findObj.setIsFound(true);
					return findObj;
				}		
			}	
		}
		
		return findObj;
		
	}
	
	public boolean isEditable(){
		return true;
	}
	
	public List<Sequence> insertGapRightOfSelectedBase(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				seq.insertGapRightOfSelectedBase();
			}
		}
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;	
	}
	
	public List<Sequence> insertGapLeftOfSelectedBase(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				seq.insertGapLeftOfSelectedBase();
			}
		}
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;
	}
	
	public List<Sequence> deleteGapMoveLeft(boolean undoable) {
		boolean gapPresentInAll = true;
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				//logger.info("hassel" + seq);
				if(! seq.isGapLeftOfSelection()){
					gapPresentInAll = false;
					break;
				}
			}
		}

		if(gapPresentInAll){
			for(Sequence seq: sequences){
				if(seq.hasSelection()){
					if(undoable){
						editedSequences.add(seq.getCopy());
					}
					seq.deleteGapLeftOfSelection();
				}
			}
		}
		
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;	
	}
	
	public List<Sequence> deleteGapMoveRight(boolean undoable) {
		boolean gapPresentInAll = true;
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				//logger.info("hassel" + seq);
				if(! seq.isGapRightOfSelection()){
					gapPresentInAll = false;
					break;
				}
			}
		}

		if(gapPresentInAll){
			for(Sequence seq: sequences){
				if(seq.hasSelection()){
					if(undoable){
						editedSequences.add(seq.getCopy());
					}
					seq.deleteGapRightOfSelection();
				}
			}
		}
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;
		
	}	

	public boolean isGapPresentRightOfSelection() {
		boolean gapPresentInAll = true;
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(! seq.isGapRightOfSelection()){
					gapPresentInAll = false;
					break;
				}
			}
		}
		return gapPresentInAll;
	}
	
	public List<Sequence> moveSelectionRightIfGapIsPresent(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		if(isGapPresentRightOfSelection()){
			for(Sequence seq: sequences){
				if(seq.hasSelection()){
					if(undoable){
						editedSequences.add(seq.getCopy());
					}
					seq.moveSelectionRightIfGapIsPresent();
				}
			}
		}
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;
	}
	
	public boolean isGapPresentLeftOfSelection() {
		boolean gapPresentInAll = true;
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(! seq.isGapLeftOfSelection()){
					gapPresentInAll = false;
					break;
				}
			}
		}
		return gapPresentInAll;
	}
	
	public List<Sequence> moveSelectionLeftIfGapIsPresent(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		if(isGapPresentLeftOfSelection()){
			for(Sequence seq: sequences){
				if(seq.hasSelection()){
					if(undoable){
						editedSequences.add(seq.getCopy());
					}
					seq.moveSelectionLeftIfGapIsPresent();
				}
			}
		}	
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;
	}

	public List<Sequence> moveSelectionIfGapIsPresent(int diff, boolean undoable){
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		// TODO this is moving and remembering startpos
		int unmovedDiff = diff - selectionOffset;
		if(unmovedDiff < 0){
			int absDiff = Math.abs(unmovedDiff);
			int n = 0;
			while(n < absDiff){
				// only keep first one
				if(editedSequences == null){
					editedSequences = this.moveSelectionLeftIfGapIsPresent(undoable);
				}else{
					this.moveSelectionLeftIfGapIsPresent(undoable);
				}
				n++;
			}
		}
		if(unmovedDiff > 0){
			int absDiff = Math.abs(unmovedDiff);
			int n = 0;
			while(n < absDiff){
				// only keep first one
				if(editedSequences == null){
					editedSequences = this.moveSelectionRightIfGapIsPresent(undoable);
				}else{
					this.moveSelectionRightIfGapIsPresent(undoable);
				}
				n++;
			}
		}				
		selectionOffset = diff;
		if(editedSequences.size()>0){
			sequencesChanged();
		}
		return editedSequences;
	}
	
	

	public void setSelectionOffset(int i) {
		this.selectionOffset = i;
		
	}

	public void expandSelectionDown() {
		logger.info("expDown");
		// start one above bottom
		for(int n = sequences.size()-2; n >= 1; n--){
			Sequence seq = sequences.get(n);
			if(seq.hasSelection()){
				logger.info("hasSel");
				int[] selected = seq.getSelectedPositions();
				for(int index: selected){
					sequences.get(n+1).setSelectionAt(index, true);
				}
			}
		}
	}

	public boolean replaceSelectedWithChar(char newChar) {
		boolean wasReplaced = false;
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				seq.replaceSelectedBasesWithChar(newChar);
				wasReplaced = true;
			}
		}
		return wasReplaced;
	}

	public void selectEverythingWithinGaps(Point point) {
		if(!rangeCheck(point)){
			return;
		}
		Sequence seq = sequences.get(point.y);
		seq.selectAllBasesUntilGap(point.x);
	}

	public void realignNucleotidesUseThisAASequenceAsTemplate(Sequence nucSeq, Sequence template, CodonPositions codonPos, GeneticCode genCode) throws IOException {
		StringBuilder newSeq = new StringBuilder(nucSeq.getLength());
		
		AATranslator oldTranslator = new AATranslator(codonPos, genCode);
		oldTranslator.setSequence(nucSeq);
		
		logger.info("oldtrans" + oldTranslator.getTranslatedAsString());
		
		int nextFindPos = 0;
		for(int n = 0; n < template.getLength(); n++){
			byte nextAAByte = template.getBaseAtPos(n);
			AminoAcid aaTemplate = AminoAcid.getAminoAcidFromByte(nextAAByte);
			
						
			logger.info("aaTemplate.getCodeCharVal()" + aaTemplate.getCodeCharVal());
			if(aaTemplate.getCodeCharVal() == AminoAcid.GAP.getCodeCharVal()){
				newSeq.append((char)SequenceUtils.GAP_SYMBOL);
				newSeq.append((char)SequenceUtils.GAP_SYMBOL);
				newSeq.append((char)SequenceUtils.GAP_SYMBOL);
			}else{
				
//				// get all stopCodons
//				AminoAcid oldAcid = oldTranslator.getAAinTranslatedPos(nextFindPos);
//				logger.info("oldAcid=" + oldAcid.getCodeCharVal());
//				while(oldAcid == AminoAcid.STOP){
//					
//					logger.info("isStop");
//					
//					byte[] nextNucs = oldTranslator.getGapPaddedCodonInTranslatedPos(nextFindPos);
//					newSeq.append(new String(nextNucs));
//					nextFindPos += 1;
//					oldAcid = oldTranslator.getAAinTranslatedPos(nextFindPos);
//				}
				
				
//				logger.info("search for " + aaTemplate.getCodeCharVal() + " in seq " + nucSeq.getName() + " from pos " + nextFindPos);
				int posFound = oldTranslator.findFistPos(nextFindPos,aaTemplate);
				if(posFound == -1){
					logger.info("posnotfound");
					break;
				}
				byte[] nextNucs = oldTranslator.getGapPaddedCodonInTranslatedPos(posFound);
				newSeq.append(new String(nextNucs));
				nextFindPos = posFound + 1;		
			}
		}
		nucSeq.setBases(newSeq.toString().getBytes());
	}

	public void realignNucleotidesUseTheseAASequenceAsTemplate(SequenceListModel templateSeqs, CodonPositions codonPos, GeneticCode genCode){
		for(Sequence templateSeq: templateSeqs.getSequences()){	
			// do partial name since it if being cut by some programs....
			Sequence nucSeq =  this.getSequenceByName(templateSeq.getName());
			logger.info("nucSeq=" + nucSeq.getName() + nucSeq.getBasesAsString());
			logger.info("templateSeq=" + templateSeq.getName() + templateSeq.getBasesAsString());
			
			try {
				realignNucleotidesUseThisAASequenceAsTemplate(nucSeq, templateSeq, codonPos, genCode);
			} catch (IOException e) {
				// Nothing needs to be done just go to next seq
				e.printStackTrace();
			}				
		}
	}
/*
	private Sequence getSequenceByPartialName(String name) {
		if(name == null){
			return null;
		}
		Sequence foundSeq = null;
		for(Sequence seq: sequences){
			if(seq.getName().toUpperCase().startsWith(name.toUpperCase())){
				foundSeq = seq;
				break;
			}
		}
		// try other way round
		if(foundSeq == null){
			for(Sequence seq: sequences){
				if(name.toUpperCase().startsWith(seq.getName().toUpperCase())){
					foundSeq = seq;
					break;
				}
			}
		}
		
		return foundSeq;	
	}
*/
	public Sequence getSequenceByName(String name) {
		if(name == null){
			return null;
		}
		Sequence foundSeq = null;
		for(Sequence seq: sequences){
			if(name.equalsIgnoreCase(seq.getName())){
				foundSeq = seq;
				break;
			}
		}
		return foundSeq;	
	}

	public void deleteAllGaps() {
		for(Sequence seq: sequences){
			seq.deleteAllGaps();
		}
	}

	public boolean rightPadWithGapUntilEqualLength(){
		boolean wasPadded = false;	
		int longLen = getLongestSequenceLength();
		for(Sequence sequence : sequences){
			int diffLen = longLen - sequence.getLength();
			if(diffLen != 0){
				sequence.rightPadSequenceWithGaps(diffLen);
				wasPadded = true;
			}
		}
		return wasPadded;	
	}

	public boolean leftPadWithGapUntilEqualLength() {
		boolean wasPadded = false;	
		for(Sequence sequence : sequences){
			int diffLen = getLongestSequenceLength() - sequence.getLength();
			if(diffLen != 0){
				sequence.leftPadSequenceWithGaps(diffLen);
				wasPadded = true;
			}
			
		}
		return wasPadded;
	}

	/*
	public void rightTrimSequencesRemoveGapsUntilEqualLength(){
		
		for(int n = sequences.size() - 1 && thisPosHasBase == false; n >=0; n--){
			boolean thisPosHasBase = false;
			for(Sequence sequence : sequences){
				if(n < sequence.getLength() && !NucleotideUtilities.isGap(sequence.getBaseAtPos(n)){
					thisPosHasBase = true;
					break;
				}
			}
			if(thisPosHasBase == false){
				for(Sequence sequence : sequences){
					if(n < sequence.getLength() && !NucleotideUtilities.isGap(sequence.getBaseAtPos(n)){
						thisPosHasBase = true;
						break;
					}
				}
			}
			else{
				break;
			}
		}
				
	}
	*/
	public boolean rightTrimSequencesRemoveGapsUntilEqualLength(){
		boolean wasTrimmed = false;
		String cons = getConsensus();			
		// check if there are any
		if(cons.indexOf(SequenceUtils.GAP_SYMBOL) > 0){		
			// create a bit-mask with pos to delete
			boolean[] deleteMask = new boolean[cons.length()];
			
			for(int n = deleteMask.length - 1; n>=0 ;n--){
				if(cons.charAt(n) == SequenceUtils.GAP_SYMBOL){
					deleteMask[n] = true;
				}else{
					break; // break out of for loop
				}
			}			
			if(ArrayUtils.contains(deleteMask, true)){
				deleteBasesInAllSequencesFromMask(deleteMask);
				wasTrimmed = true;
			}	
		}
		return wasTrimmed;
	}
	
	
	public void deleteBasesInAllSequencesFromMask(boolean[] deleteMask) {
		for(Sequence sequence : sequences){
			sequence.deleteBasesFromMask(deleteMask);
		}
	}
	
	public String getConsensus() {
		
		if(getSequenceType() == SequenceUtils.TYPE_AMINO_ACID){
			return getAminoAcidConsensus();
		}
		else{
			return getNucleotideConsensus();
		}
	}
		
	private String getAminoAcidConsensus() {
		byte[] consVals = new byte[getLongestSequenceLength()];
		Arrays.fill(consVals, AminoAcid.GAP.getCodeByteVal());
		for(Sequence sequence : sequences){
			for(int n = 0; n < sequence.getLength(); n++){
				consVals[n] = AminoAcid.getConsensusFromByteVal(sequence.getBaseAtPos(n), (byte)consVals[n]);
			}
		}
		
		String consAsString = new String(consVals);
		
		logger.info(consAsString);
		
		return consAsString;
	}

	private String getNucleotideConsensus(){
		int[] consVals = new int[getLongestSequenceLength()];
		for(Sequence sequence : sequences){
			int[] baseVals = sequence.getSequenceAsBaseVals();
				// bitwise add everything
				for(int n = 0; n < baseVals.length; n++){
					consVals[n] = consVals[n] | baseVals[n];
				}
		}
		char[] cons = new char[consVals.length];
		for(int n = 0; n < cons.length; n++){
			cons[n] = NucleotideUtilities.charFromBaseVal(consVals[n]);
		}
		return new String(cons);
	}

	public void reverseComplement() {
		for(Sequence seq : sequences){
			seq.reverseComplement();
		}
	}

	public String getSelectionAsNucleotides() {
		StringBuilder selection = new StringBuilder();
		for(Sequence sequence : sequences){
			if(sequence.getSelectedBasesAsString() != null && sequence.getSelectedBasesAsString().length() > 0){
				selection.append(sequence.getSelectedBasesAsString());
				selection.append(LF);
			}
		}
		return selection.toString();
	}

	public int getFirstSelectedYPos(){
		int selectionStartPos = -1;
		for(Sequence sequence : sequences){
			if(sequence.hasSelection()){
				selectionStartPos = sequence.getFirstSelectedPosition();
				break;
			}
		}
		return selectionStartPos;
	}
	
	public String getFirstSelectedName() {
		String name = null;
		if(getFirstSelected() != null){
			name = getFirstSelected().getName();
		}
		return name;
	}
	
	public void setFirstSelectedName(String newName) {
		if(newName == null){
			return;
		}
		if(getFirstSelected() != null){
			getFirstSelected().setName(newName);
		}
	}
	
	public Sequence getFirstSelected() {
		for(int n = 0; n < sequences.size(); n++){
			if(sequences.get(n).hasSelection()){
				return sequences.get(n);
			}
		}
		return null;
	}


	public boolean hasSelection() {
		for(Sequence sequence : sequences){
			if(sequence.hasSelection()){
				return true;
			}
		}
		return false;
	}

	public void complement() {
		for(Sequence seq : sequences){
			seq.complement();	
		}
	}

	public int getLongestSequenceName() {
		long startTime = System.currentTimeMillis();
		if(cachedLongestSequenceName <=0){
			int maxlen = 0;
			for(Sequence seq: sequences){
				maxlen = Math.max(maxlen, seq.getName().length());
			}
			cachedLongestSequenceName = maxlen;
		}
		long endTime = System.currentTimeMillis();
		logger.info("getLongestSequenceName took " + (endTime - startTime) + " milliseconds");	
		return cachedLongestSequenceName;
	}

	public void selectAll() {
		for(Sequence seq: sequences){
			seq.selectAllBases();
		}
	}

	public boolean isPositionValid(int x, int y) {
		return rangeCheck(x,y);
	}

	private boolean rangeCheck(int x, int y){
		boolean isValid = false;
		if(y > -1 && y < sequences.size()){
			if(x >= 0 && x < sequences.get(y).getLength()){
				isValid = true;
			}
		}
		return isValid;
	}
	
	private boolean rangeCheck(Point point) {
		return rangeCheck(point.x, point.y);
	}
	

	public void copySelectionFromInto(int indexFrom, int indexTo) {
		Sequence seqFrom = sequences.get(indexFrom);
		Sequence seqTo = sequences.get(indexTo);
		
		for(int x = 0; x < seqFrom.getLength() || x < seqTo.getLength(); x++){
			seqTo.setSelectionAt(x,seqFrom.isBaseSelected(x));
		}
	}

	public void selectColumn(int columnIndex, boolean selected) {
		for(Sequence seq: sequences){
			seq.setSelectionAt(columnIndex, selected);
		}
		
	}

	public void copySelectionFromPosX1toX2(int x1, int x2) {
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				seq.setSelectionAt(x2, seq.isBaseSelected(x1));
			}
		}
	}
	public Sequence getSequenceFromID(int id){
		for(Sequence seq: sequences){
			if(seq.getID() == id){
				return seq;
			}
		}
		return null;
	}

	public Point getFirstSelectedPos() {
		for(int n = 0; n < sequences.size(); n++){
			if(sequences.get(n).hasSelection()){
				return new Point(sequences.get(n).getFirstSelectedPosition(), n);
			}
		}
		return null;
	}
	
	public Point getFirstSelectedUngapedPos() {
		for(int n = 0; n < sequences.size(); n++){
			if(sequences.get(n).hasSelection()){
				Sequence firstSelected = sequences.get(n);
				int position = firstSelected.getFirstSelectedPosition();
				int ungaped = firstSelected.getUngapedPos(position);
				return new Point(ungaped, n);
			}		
		}
		return null;		
	}
	

	public void sortSequencesByName() {
		logger.info(sequences);
		Collections.sort(sequences);	
	}
	
	public void sortSequencesByCharInSelectedColumn() {
		// get first selected column
		Point selPos = getFirstSelectedPos();
		Collections.sort(sequences, new SequencePositionComparator(selPos.x));
	}

	public AliHistogram getHistogram() {
		long startTime = System.currentTimeMillis();
		AliHistogram histogram = null;
		if(sequenceType == SequenceUtils.TYPE_AMINO_ACID){
			histogram = new AAHistogram(getLongestSequenceLength());
		}else{
			histogram = new NucleotideHistogram(getLongestSequenceLength());
		}
		
		for(Sequence seq: sequences){
			if(sequenceType == SequenceUtils.TYPE_AMINO_ACID){
				histogram.addSequence(seq);
			}else{
				histogram.addSequence(seq);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("Create histogram took " + (endTime - startTime) + " milliseconds");
		return histogram;
	}

	public List<Sequence> replaceSelectedCharactersWithThis(SequenceListModel newOnes, boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				// No partial name that might swich sequences
				//Sequence realignedSeq = newOnes.getSequenceByPartialName(seq.getName());
				Sequence realignedSeq = newOnes.getSequenceByName(seq.getName());
				byte[] realignedBases;
				// muscle removes empty seq if that is case create an empty bases
				if(realignedSeq != null){
					realignedBases = realignedSeq.getAllBasesAsByteArray();
				}else{
					realignedBases = SequenceUtils.createGapByteArray(newOnes.getLongestSequenceLength());
				}

				int selPos[] = seq.getSelectedPositions();
				
				// if selection is larger than result pad up with gap
				if(selPos.length > realignedBases.length){
					byte[] paddedRealigned = Arrays.copyOf(realignedBases, selPos.length);
					for(int n = realignedBases.length; n < paddedRealigned.length; n++){
						paddedRealigned[n] = SequenceUtils.GAP_SYMBOL;
					}
					realignedBases = paddedRealigned;
				}		
				seq.replaceBases(selPos[0],selPos[selPos.length -1],realignedBases);
				seq.selectBases(selPos[0],selPos[0] + realignedBases.length -1);
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
			}
		}
		return editedSequences;	
	}
	
	
	
	public boolean mergeTwoSequences(Sequence seq1, Sequence seq2, boolean allowOverlap){
		
		if(sequenceType == SequenceUtils.TYPE_NUCLEIC_ACID){
			return mergeTwoNucleotideSequences(seq1, seq2, allowOverlap);
		}
		else{
			return mergeTwoAminoAcidSequences(seq1, seq2, allowOverlap);
		}
		
	}
	
	public boolean mergeTwoAminoAcidSequences(Sequence seq1, Sequence seq2, boolean allowOverlap){
		boolean isMerged = false;
		int nExactOverlap = countExactAminoAcidOverlap(seq1, seq2);
		int nDifferentOverlap = countDifferentAminoAcidOverlap(seq1, seq2);
		
		boolean isOverlap = false;
		boolean isOverlapExactlySame = false;
		if(nExactOverlap > 0 || nDifferentOverlap > 0){
			isOverlap = true;
			if(nExactOverlap > 0 && nDifferentOverlap ==0){
				isOverlapExactlySame = true;
			}
		}
		
		// Warn
		if(isOverlap){			
			String overlapMessage = "";
			if(isOverlapExactlySame){
				overlapMessage = "Overlapping parts are identical (" + nExactOverlap +"bases)";
			}else{
				overlapMessage = "Overlapping parts are different (" + nDifferentOverlap + "/" + (nDifferentOverlap + nExactOverlap) + ")";
				overlapMessage += LF + "Differences will be replaced by " + AminoAcid.X.getCodeCharVal();

			}
			String message="Sequences are overlapping - " + overlapMessage + LF +
					"Do you want to continue?";

			// TODO I dont know how to deal with dialogs in a nice pattern way if it is within the alignment class? Maybe it should be splited
			// and moved into aliview-class (for example to do a temporary merge and then ask and then call alignment again to do join
			int retVal = JOptionPane.showConfirmDialog(DialogUtils.getDialogParent(), message, "Continue?", JOptionPane.OK_CANCEL_OPTION);
			if(retVal != JOptionPane.OK_OPTION){
				return false;
			}
		}
		else{
			String message= "Sequences are NOT overlapping" + LF +
					"Do you want to continue?";
			int retVal = JOptionPane.showConfirmDialog(DialogUtils.getDialogParent(), message, "Continue?", JOptionPane.OK_CANCEL_OPTION);
			if(retVal != JOptionPane.OK_OPTION){
				return false;
			}
		}

		//
		// OK go ahead merge
		//
		byte[] merged = new byte[seq1.getLength()];	
		for(int n = 0; n < seq1.getLength(); n++){
			merged[n] = AminoAcid.getConsensusFromByteVal(seq1.getBaseAtPos(n),seq2.getBaseAtPos(n));
		}
		if(isOverlap && allowOverlap == false){
			// skip
		}
		else{
			// set new merged data - keep selection
			seq1.setBases(merged);
			seq1.setName(seq1.getName() + "_merged_" + seq2.getName());
			seq2.setBases(merged.clone());
			seq2.setName(seq2.getName() + "_merged_" + seq1.getName());
			isMerged = true;
		}
		return isMerged;
}
	

	public boolean mergeTwoNucleotideSequences(Sequence seq1, Sequence seq2, boolean allowOverlap){
			boolean isMerged = false;
			int nExactOverlap = countExactNucleotideOverlap(seq1, seq2);
			int nDifferentOverlap = countDifferentNucleotideOverlap(seq1, seq2);
			
			boolean isOverlap = false;
			boolean isOverlapExactlySame = false;
			if(nExactOverlap > 0 || nDifferentOverlap > 0){
				isOverlap = true;
				if(nExactOverlap > 0 && nDifferentOverlap ==0){
					isOverlapExactlySame = true;
				}
			}
			
			// Warn
			if(isOverlap){			
				String overlapMessage = "";
				if(isOverlapExactlySame){
					overlapMessage = "Overlapping parts are identical (" + nExactOverlap +"bases)";
				}else{
					overlapMessage = "Overlapping parts are different (" + nDifferentOverlap + "/" + (nDifferentOverlap + nExactOverlap) + ")";

				}
				String message="Sequences are overlapping - " + overlapMessage + LF +
						"Do you want to continue?";

				// TODO I dont know how to deal with dialogs in a nice pattern way if it is within the alignment class? Maybe it should be splited
				// and moved into aliview-class (for example to do a temporary merge and then ask and then call alignment again to do join
				int retVal = JOptionPane.showConfirmDialog(DialogUtils.getDialogParent(), message, "Continue?", JOptionPane.OK_CANCEL_OPTION);
				if(retVal != JOptionPane.OK_OPTION){
					return false;
				}
			}
			else{
				String message= "Sequences are NOT overlapping" + LF +
						"Do you want to continue?";
				int retVal = JOptionPane.showConfirmDialog(DialogUtils.getDialogParent(), message, "Continue?", JOptionPane.OK_CANCEL_OPTION);
				if(retVal != JOptionPane.OK_OPTION){
					return false;
				}
			}

			//
			// OK go ahead merge
			//
			byte[] merged = new byte[seq1.getLength()];	
			for(int n = 0; n < seq1.getLength(); n++){
				merged[n] = NucleotideUtilities.getConsensusFromBases(seq1.getBaseAtPos(n),seq2.getBaseAtPos(n));
			}
			if(isOverlap && allowOverlap == false){
				// skip
			}
			else{
				// set new merged data - keep selection
				seq1.setBases(merged);
				seq1.setName(seq1.getName() + "_merged_" + seq2.getName());
				seq2.setBases(merged.clone());
				seq2.setName(seq2.getName() + "_merged_" + seq1.getName());
				isMerged = true;
			}
			return isMerged;
	}
	
	public int countExactNucleotideOverlap(Sequence seq1, Sequence seq2) {
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
	
	public int countDifferentNucleotideOverlap(Sequence seq1, Sequence seq2) {
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
	
	public int countDifferentAminoAcidOverlap(Sequence seq1, Sequence seq2) {
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
	
	public int countExactAminoAcidOverlap(Sequence seq1, Sequence seq2) {
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

	public List<Sequence> replaceSelectedBasesWithGap(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				seq.replaceSelectedBasesWithGap();
			}
		}
		return editedSequences;
	}

	public List<Sequence> deleteSelectedBases(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		for(Sequence sequence : sequences){
			if(sequence.hasSelection()){
				if(undoable){
					editedSequences.add(sequence.getCopy());
				}
				sequence.deleteSelectedBases();
			}
		}	
		return editedSequences;
	}

	
	public String findDuplicates() {
		StringBuilder dupeMessage = new StringBuilder();
		for(int n = 0; n < sequences.size(); n++){
			Sequence seq1 = getSequences().get(n);
			String duplicates = "";
			for(int m = n + 1; m < sequences.size(); m++){
				Sequence seq2 = getSequences().get(m);
				int exactOverlap = countExactNucleotideOverlap(seq1, seq2);
				int differentOverlap = countDifferentNucleotideOverlap(seq1, seq2);
				
				if(exactOverlap > 100 && differentOverlap == 0){					
					duplicates += seq2.getName() + " (" + exactOverlap + ") " + seq2.getUngapedLength()  + LF;				
				}
			}
			if(duplicates.length() > 0){
				dupeMessage.append(seq1.getName() + " " + seq1.getUngapedLength()  + "=" + LF); 
				dupeMessage.append(duplicates + "-------------------------------------------------------------------" + LF);
			}
		}
		return dupeMessage.toString();
	}

	public void setSelectionWithin(Rectangle bounds, boolean isSelected) {
		// loop through the part of alignment matrix that is within selection
		for(int y = (int) bounds.getMinY(); y <= bounds.getMaxY(); y++){
			for(int x = (int)bounds.getMinX(); x <= bounds.getMaxX(); x++){
				// and make sure not outside matrix
				if(rangeCheck(x, y)){
					setSelectionAt(x,y,isSelected);
				}
			}
		}		
	}

	public AliHistogram getTranslatedHistogram(AATranslator translator) {
		long startTime = System.currentTimeMillis();
		int transLength = translator.getMaximumTranslationLength();
//		logger.info(transLength);
		AAHistogram histogram = new AAHistogram(transLength);
		
		for(Sequence seq: sequences){
			translator.setSequence(seq);
//			logger.info("seqtranslen" + translator.getTranslatedAminAcidSequenceLength());
			for(int n = 0; n < transLength; n++){ // translator.getTranslatedAminAcidSequenceLength()
				histogram.addAminoAcid(n,translator.getAAinTranslatedPos(n));
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("Create translated histogram took " + (endTime - startTime) + " milliseconds");
		return histogram;
	}

	public boolean hasFullySelectedSequences() {
		for(Sequence seq: sequences){
			if(seq.isAllSelected()){
				return true;
			}
		}
		return false;
	}

	public void sortSequencesByThisModel(SequenceListModel prevSeqOrder){
		ArrayList<Sequence> seqsInOrder = new ArrayList<Sequence>(prevSeqOrder.size());
		for(int n = 0; n < prevSeqOrder.getSize(); n++){
			Sequence prev = prevSeqOrder.get(n);
			//Sequence seq = getSequenceByPartialName(prev.getName());
			Sequence seq = getSequenceByName(prev.getName());
			seqsInOrder.add(seq);
		}
		if(seqsInOrder.size() == sequences.size()){
			setSequences(seqsInOrder);
		}
	}

	public int getSelectedColumnCount() {
		BitSet colSelect = new BitSet();
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				int[] allSelected = seq.getSelectedPositions();
				for(int nextInt: allSelected){
					colSelect.set(nextInt);
				}
			}
		}
		return colSelect.cardinality();
	}

	public int getSelectedSequencesCount() {
		int count = 0;
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				count ++;
			}
		}
		return count;
	}
	
	public String getSelectionNames() {
		String names = "";
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				names += seq.getName() + LF;
			}
		}
		// remove last LF
		if(names.length() > 0){
			names = StringUtils.removeEnd(names, LF);
		}
		
		return names;
	}

	

	
}
