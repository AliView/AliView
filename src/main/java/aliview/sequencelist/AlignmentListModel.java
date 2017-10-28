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
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.log4j.Logger;

import utils.DialogUtils;
import utils.nexus.CharSet;
import utils.nexus.CodonPos;
import utils.nexus.CodonPositions;
import aliview.AliView;
import aliview.AliViewWindow;
import aliview.AminoAcid;
import aliview.GeneticCode;
import aliview.NucleotideUtilities;
import aliview.alignment.AAHistogram;
import aliview.alignment.NotUsed_AATranslator;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;
import aliview.alignment.AlignmentMeta;
import aliview.alignment.NucleotideHistogram;
import aliview.gui.TextEditDialog;
import aliview.importer.AlignmentImportException;
import aliview.importer.FileFormat;
import aliview.sequences.FileSequence;
import aliview.sequences.BasicSequence;
import aliview.sequences.InMemorySequence;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;
import aliview.undo.UndoSavedStateEditedSequences;

public class AlignmentListModel implements ListModel, Iterable<Sequence>{

	private static final String LF = System.getProperty("line.separator");
	private static final long serialVersionUID = -8081215660929212156L;
	private static final Logger logger = Logger.getLogger(AlignmentListModel.class);
	List<Sequence> delegateSequences;
	protected FileFormat fileFormat;
	protected int sequenceType = SequenceUtils.TYPE_UNKNOWN;
	private int selectionOffset;
	private AlignmentSelectionModel selectionModel = new AlignmentSelectionModel(this);
	protected EventListenerList listenerList = new EventListenerList();
	// AliHistogram and other cached variables has to be volatile so no problems araise
	// with the double lock synch strategy
	// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
	private volatile AliHistogram cachedHistogram;
	private volatile int cachedLongestSequenceName;
	private volatile int cachedLongestSequenceLength = -1;
	private boolean isTranslated;
	private Alignment alignment;


	public AlignmentListModel() {
		this.delegateSequences = new ArrayList<Sequence>();
	}

	public AlignmentListModel(List<Sequence> seqs) {
		for(Sequence seq: seqs){
			seq.setAlignmentModel(this);
		}
		this.delegateSequences = seqs;
		fireSequenceIntervalAdded(0, seqs.size() - 1);
	}

	public AlignmentListModel(List<Sequence> seqs, FileFormat foundFormat) {
		this.fileFormat = foundFormat;
		for(Sequence seq: seqs){
			seq.setAlignmentModel(this);
		}
		this.delegateSequences = seqs;
		fireSequenceIntervalAdded(0, seqs.size() - 1);
	}

	public AlignmentListModel(AlignmentListModel template){
		ArrayList<Sequence> seqClone = new ArrayList<Sequence>();
		for (Sequence seq: template.delegateSequences) {
			seqClone.add(seq.getCopy());
		}

		this.fileFormat = template.fileFormat;
		this.delegateSequences = seqClone;
		this.sequenceType = template.sequenceType;
		fireSequencesChangedAllNew();
	}


	// ***************************************
	// ListModel interface
	// ***************************************

	public int getSize() {
		return delegateSequences.size();
	}

	public Sequence getElementAt(int index){
		return delegateSequences.get(index);
	}

	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}


	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

	// ***************************************
	// End List model interface
	// ***************************************


	public void addAlignmentDataListener(AlignmentDataListener l) {
		listenerList.add(AlignmentDataListener.class, l);
	}


	public void removeAlignmentDataListener(AlignmentDataListener l) {
		listenerList.remove(AlignmentDataListener.class, l);
	}



	// ***************************************
	// AbstractListModel 
	// ***************************************
	/*
	@Override
	public void addAlignmentListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		super.addListDataListener(l);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		super.addListDataListener(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		super.removeListDataListener(l);
	}

	@Override
	public ListDataListener[] getListDataListeners() {
		// TODO Auto-generated method stub
		return super.getListDataListeners();
	}

	@Override
	protected void fireContentsChanged(Object source, int index0, int index1) {
		// TODO Auto-generated method stub
		super.fireContentsChanged(source, index0, index1);
	}

	@Override
	protected void fireIntervalAdded(Object source, int index0, int index1) {
		// TODO Auto-generated method stub
		super.fireIntervalAdded(source, index0, index1);
	}

	@Override
	protected void fireIntervalRemoved(Object source, int index0, int index1) {
		// TODO Auto-generated method stub
		super.fireIntervalRemoved(source, index0, index1);
	}


	@Override
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		// TODO Auto-generated method stub
		return super.getListeners(listenerType);
	}
	 */

	// ***************************************
	// End AbstractList model
	// ***************************************



	// ***************************************
	// Iterableinterface
	// ***************************************

	public Iterator<Sequence> iterator() {
		return delegateSequences.listIterator();
	}

	// ***************************************
	// End Iterableinterface
	// ***************************************

	public void setSequences(List<Sequence> list){
		if(list != null){
			for(Sequence seq: list){
				seq.setAlignmentModel(this);
			}

			this.delegateSequences = list;
			fireSequencesChangedAllNew();
		}
	}

	public List<Sequence> getDelegateSequences() {
		return delegateSequences;
	}

	public AlignmentListModel getCopy(){
		return new AlignmentListModel(this);
	}

	public AlignmentListModel getCopyShallow(){
		AlignmentListModel copy = new AlignmentListModel();	

		copy.delegateSequences.addAll(this.delegateSequences);
		copy.fileFormat = fileFormat;
		copy.sequenceType = sequenceType;
		return copy;
	}

	public Sequence get(int index) {
		if(index >= delegateSequences.size()){
			return null;
		}
		return delegateSequences.get(index);
	}
	/*
	public void add(int index, Sequence seq) {
		logger.info("add at=" + index);
		delegateSequences.add(index, seq);
		fireSequenceIntervalAdded(index, index);
	}



	public Sequence set(int index, Sequence element){
		Sequence previous = sequences.set(index, element);
		fireSequencesChanged(index, index);
		return previous;
	}

	public void removeAt(int index) {
		sequences.remove(index);
		fireSequenceIntervalRemoved(index, index);	
	}

	public void insertAt(Sequence seq, int index) {
		sequences.add(index, seq);
		fireSequenceIntervalAdded(index, index);
	}

	 */

	// TODO these three set and add methods might give problems if there is no 
	// is adjusting method
	public Sequence set(int index, Sequence sequence){
		sequence.setAlignmentModel(this);
		Sequence previous = delegateSequences.set(index, sequence);
		// TODO Maybe add an adjusting parameter...
		fireSequencesChanged(index, index);
		return previous;
	}

	public void add(Sequence sequence) {
		sequence.setAlignmentModel(this);
		delegateSequences.add(sequence);
		fireSequenceIntervalAdded(this.size() -1, this.size() - 1);
	}

	public void add(int index, Sequence seq) {
		seq.setAlignmentModel(this);
		delegateSequences.add(index, seq);
		// TODO Maybe add an adjusting parameter...
		fireSequenceIntervalAdded(index, index);
	}

	public void addAll(AlignmentListModel otherSeqModel, boolean setSelected) {
		addAll(otherSeqModel.getDelegateSequencesCopy(), setSelected);
	}

	public void addAll(int index, AlignmentListModel otherSeqModel) {
		for(Sequence seq: otherSeqModel.getDelegateSequencesCopy()){
			seq.setAlignmentModel(this);
		}
		delegateSequences.addAll(index, otherSeqModel.getDelegateSequencesCopy());
		fireSequenceIntervalAdded(index, index + otherSeqModel.getDelegateSequencesCopy().size());
	}

	public void addAll(List<Sequence> moreSeqs, boolean setSelected) {
		for(Sequence seq: moreSeqs){
			seq.setAlignmentModel(this);
		}
		logger.info("added all moreSeqs.size()" +  moreSeqs.size());
		delegateSequences.addAll(moreSeqs);
		if(setSelected){
			selectionModel.setSequenceSelection(moreSeqs);
		}
		//fireSequencesChangedAll();
		fireSequenceIntervalAdded(this.size() - moreSeqs.size(), this.size() - 1);
	}


	public List<Sequence> getDelegateSequencesCopy(){
		return new ArrayList<Sequence>(delegateSequences);
	}


	protected List<Sequence> getSequences() {
		return delegateSequences;
	}

	public int getLongestSequenceLength(){

		if(cachedLongestSequenceLength <= 0){
			// this is double locked to avoid synchronized block after the cached initialization of variacle
			// cached variable has to be declared volatile above
			// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html and http://en.wikipedia.org/wiki/Double-checked_locking
			synchronized(this){
				if(cachedLongestSequenceLength <0){
					int maxLen = 0;
					for(int n = 0; n < delegateSequences.size(); n++){
						int len = delegateSequences.get(n).getLength();
						if(len > maxLen){
							maxLen = len;
						}
					}
					cachedLongestSequenceLength = maxLen;
				}
			}
		}
		return cachedLongestSequenceLength;
	}

	public int getShortestSequenceLength() {
		int minLen = getLongestSequenceLength();
		for(int n = 0; n < delegateSequences.size(); n++){
			int len = delegateSequences.get(n).getLength();
			if(len < minLen){
				minLen = len;
			}
		}

		return minLen;

	}

	public FileFormat getFileFormat() {
		return this.fileFormat;
	}

	public void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public int getSequenceType() {

		// Lazy initialization of sequence type field
		if(delegateSequences.size() > 0 && sequenceType == SequenceUtils.TYPE_UNKNOWN){
			// TODO could figure out if not a sequence
			int gapCount = 0;
			int nucleotideCount = 0;
			int otherCount = 0;

			// Loop through 1000 bases
			mainLoop:
				for(Sequence seq: delegateSequences){
					for(int pos = 0; pos < seq.getLength(); pos ++){
						byte base = seq.getBaseAtPos(pos); 
						if(NucleotideUtilities.isGap(base)){
							gapCount ++;
						}else if(NucleotideUtilities.isNucleoticeOrIUPAC(base)){
							nucleotideCount ++;
						}
						else{
							otherCount ++;
						}
						if((nucleotideCount + otherCount) >= 1000){
							break mainLoop;
						}
					}		
				}

			// First check low or nucleotide count to avoid div by zero 
			if(nucleotideCount < 4){
				this.sequenceType = SequenceUtils.TYPE_UNKNOWN;
			}
			else{
				double ratio = (double)otherCount/(double)nucleotideCount;

				if(ratio == 0){
					this.sequenceType = SequenceUtils.TYPE_NUCLEIC_ACID;
				}
				else if(ratio > 0.2){
					this.sequenceType = SequenceUtils.TYPE_AMINO_ACID;
				}
				else{
					this.sequenceType = SequenceUtils.TYPE_UNKNOWN;
				}
			}

			if(this.sequenceType == SequenceUtils.TYPE_UNKNOWN){

				// Dialog set Alignment type
				// No good idéa, better just start as SequenceUtils.TYPE_UNKNOWN

			}


		}	

		return sequenceType;	
	}


	public void reverseComplement(List<Sequence> seqs) {

		for(Sequence seq : seqs){
			seq.reverseComplement();
		}
		if(seqs.size() > 0){
			fireSequencesChanged(seqs);
		}

	}

	public void deleteSequence(Sequence seq) {
		delegateSequences.remove(seq);	
		fireSequencesChangedAll();
	}

	public void deleteSequences(List<Sequence> toDelete) {
		for(Sequence seq: toDelete){
			delegateSequences.remove(seq);
		}	
		fireSequencesChangedAll();
	}

	public List<Sequence> deleteFullySelectedSequences() {
		List<Sequence> toDelete = getFullySelectedSequences();
		deleteSequences(toDelete);
		return toDelete;		
	}

	public List<Sequence> getFullySelectedSequences(){
		List<Sequence> fullySelected = new ArrayList<Sequence>();
		for(Sequence seq: delegateSequences){
			if(seq.isAllSelected()){
				fullySelected.add(seq);
			}	
		}
		return fullySelected;
	}


	public ArrayList<Sequence> deleteEmptySequences(){
		ArrayList<Sequence> toDelete = new ArrayList<Sequence>();
		for(Sequence seq: delegateSequences){
			if(seq.isEmpty()){
				toDelete.add(seq);
			}		
		}
		deleteSequences(toDelete);

		return toDelete;
	}



	public void moveSelectedSequencesToBottom() {
		List<Sequence> selected = selectionModel.getSelectedSequences();
		moveSequencesToBottom(selected);
	}

	public void moveSelectedSequencesToTop() {
		List<Sequence> selected = selectionModel.getSelectedSequences();
		moveSequencesToTop(selected);
	}

	public void moveSelectedSequencesUp() {
		List<Sequence> selected = selectionModel.getSelectedSequences();
		moveSequencesUp(selected);
	}

	public void moveSelectedSequencesDown() {
		List<Sequence> selected = selectionModel.getSelectedSequences();
		moveSequencesDown(selected);
	}

	public void moveSelectedSequencesTo(int index) {
		List<Sequence> selected = selectionModel.getSelectedSequences();
		moveSequencesTo(index, selected);
	}

	public void moveSequencesToBottom(List<Sequence> seqs) {
		logger.info("removeAll");
		delegateSequences.removeAll(seqs);
		logger.info("addAll");
		delegateSequences.addAll(seqs);
		logger.info("seqChanged");
		if(seqs.size() > 0){
			fireSequencesChangedAll();
		}

	}


	public void moveSequencesToTop(List<Sequence> seqs) {
		delegateSequences.removeAll(seqs);
		delegateSequences.addAll(0, seqs);
		if(seqs.size() > 0){
			fireSequencesChangedAll();
		}

	}

	public void moveSequencesTo(int index, List<Sequence> seqs) {
		if(index >= delegateSequences.size()){
			index = delegateSequences.size() - 1;
		}
		if(index < 0){
			index = 0;
		}

		// get current pos
		int current = delegateSequences.indexOf(seqs.get(0));

		int diff = current - index;

		logger.info("diff" + diff);

		// loop sequences up or down
		if(diff > 0){

			for(int n = 0; n < diff; n++){
				moveSequencesUp(seqs);
			}
		}
		if(diff < 0){
			// Add seqs length because we are using the last index ad lead when counting
			for(int n = 0; n <= Math.abs(diff) - seqs.size(); n++){
				moveSequencesDown(seqs);
			}
		}

	}



	public void moveSequencesUp(List<Sequence> seqs){
		logger.info("move seq up");
		if(seqs == null || seqs.size() == 0){
			return;
		}
		for(Sequence seq: seqs){
			int index = delegateSequences.indexOf(seq);
			// break if we are at top
			if(index == 0){
				break;
			}
			Sequence previous = (Sequence) delegateSequences.set(index - 1, seq);
			delegateSequences.set(index,previous);

		}

		logger.info("seqs.size()" + seqs.size());

		if(seqs.size() > 0){
			fireSequencesChangedAll();
		}
	}

	public void moveSequencesDown(List<Sequence> seqs) {
		logger.info("move seq down");
		if(seqs == null || seqs.size() == 0){
			return;
		}
		// Has to be done reverse (otherwise index problem)
		for(int n = seqs.size() - 1; n >=0 ; n--){
			Sequence seq = seqs.get(n);
			int index = delegateSequences.indexOf(seq);
			// break if we are at bottom
			if(index >= delegateSequences.size() - 1){
				break;
			}
			Sequence previous = (Sequence) delegateSequences.set(index + 1, seq);
			delegateSequences.set(index, previous);


		}

		logger.info("seqs.size()" + seqs.size());

		if(seqs.size() > 0){
			fireSequencesChangedAll();
		}

	}



	public void writeSelectionAsFasta(Writer out) {

		List <Sequence> selectedSequences = selectionModel.getSelectedSequences();
		for(Sequence sequence : selectedSequences){
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

			//logger.info("WroteSeq=" + n);
		}

		logger.info("Write done");
	}

	public void writeSelectedSequencesAsFasta(Writer out) {
		writeSelectedSequencesAsFasta(out, false);
	}

	public void writeSelectedSequencesAsFasta(Writer out, boolean useIDAsName) {
		List <Sequence> selectedSequences = selectionModel.getSelectedSequences();
		for(Sequence sequence : selectedSequences){
			logger.info("has sel");
			writeSequenceAsFasta(sequence,out, useIDAsName);
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
		List <Sequence> unSelectedSequences = selectionModel.getUnSelectedSequences();
		for(Sequence sequence : unSelectedSequences){
			writeSequenceAsFasta(sequence, out, useIDAsName);
		}
		logger.info("Write done");
	}

	public byte getBaseAt(int x, int y) {
		return delegateSequences.get(y).getBaseAtPos(x);
	}

	public AminoAcid getTranslatedAminoAcidAtNucleotidePos(int x, int y) {
		return delegateSequences.get(y).getTranslatedAminoAcidAtNucleotidePos(x);
	}

	public int getLengthAt(int y) {
		return delegateSequences.get(y).getLength();
	}

	public int indexOf(Sequence seq) {
		return delegateSequences.indexOf(seq);
	}

	public FindObject findAndSelect(FindObject findObject) {
		if(isTranslated || getSequenceType() == SequenceUtils.TYPE_AMINO_ACID){
			return findAndSelectInAASequences(findObject);
		}
		else{
			return findAndSelectInNucleotideSequences(findObject);
		}
	}

	public FindObject findAndSelectALLInAASequences(FindObject findObj){
		String regex = findObj.getRegexSearchTerm();

		// Identical for AA and NUC search
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		findObj.setIsFound(false);
		for(int n = findObj.getNextFindSeqNumber(); n < this.getSize(); n++){
			Sequence seq = delegateSequences.get(n);
			Interval foundPos = seq.find(pattern, findObj.getNextFindStartPos());
			if(foundPos != null){
				selectionModel.selectBases(seq, foundPos);
				findObj.setNextFindSeqNumber(n);	
				// make sure it is not out of index
				findObj.setNextFindStartPos(Math.min(foundPos.getStartPos() + 1, getLongestSequenceLength() - 1));
				findObj.setFoundPos(foundPos.getStartPos(),n);
				findObj.setIsFound(true);
				// without this one it will find all positions
				//return findObj;
			}
			// not found in this seq - start again from 0
			findObj.setNextFindStartPos(0);

		}
		findObj.setNextFindSeqNumber(0);
		findObj.setNextFindStartPos(0);
		//findObj.setIsFound(false);
		return findObj;
	}

	public FindObject findAndSelectInAASequences(FindObject findObj){
		String regex = findObj.getRegexSearchTerm();

		// Identical for AA and NUC search
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		findObj.setIsFound(false);
		for(int n = findObj.getNextFindSeqNumber(); n < this.getSize(); n++){
			Sequence seq = delegateSequences.get(n);
			Interval foundPos = seq.find(pattern, findObj.getNextFindStartPos());
			if(foundPos != null){
				selectionModel.selectBases(seq, foundPos);
				findObj.setNextFindSeqNumber(n);	
				// make sure it is not out of index
				findObj.setNextFindStartPos(Math.min(foundPos.getStartPos() + 1, getLongestSequenceLength() - 1));
				findObj.setFoundPos(foundPos.getStartPos(),n);
				findObj.setIsFound(true);
				// without this one it will find all positions
				return findObj;
			}
			// not found in this seq - start again from 0
			findObj.setNextFindStartPos(0);

		}
		findObj.setNextFindSeqNumber(0);
		findObj.setNextFindStartPos(0);
		return findObj;
	}

	public FindObject findAndSelectInNucleotideSequences(FindObject findObj) {
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
		findObj.setIsFound(false);
		for(int n = findObj.getNextFindSeqNumber(); n < this.getSize(); n++){
			Sequence seq = delegateSequences.get(n);
			Interval foundPos = seq.find(pattern, findObj.getNextFindStartPos());
			if(foundPos != null){
				selectionModel.selectBases(seq, foundPos);
				findObj.setNextFindSeqNumber(n);	
				// make sure it is not out of index
				findObj.setNextFindStartPos(Math.min(foundPos.getStartPos() + 1, getLongestSequenceLength() - 1));
				findObj.setFoundPos(foundPos.getStartPos(),n);
				findObj.setIsFound(true);
				// without this one it will find all positions
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
		return findObj;
	}

	public FindObject findInNames(FindObject findObj) {
		String uCaseSearchTerm = findObj.getSearchTerm().toUpperCase();
		if(findObj.isFindAll()){
			// find all clear all previous
			findObj.clearIndices();
			for(int n = 0; n < delegateSequences.size(); n++){		
				Sequence seq = delegateSequences.get(n);
				if(seq.getName().toUpperCase().indexOf(uCaseSearchTerm) > -1){
					logger.info("Found" + n);
					findObj.addFoundNameIndex(n);
					findObj.setIsFound(true);
				}		
			}
			// Find single one
		}else{
			int startIndex = findObj.getNextNameFindIndex();
			if(startIndex >= delegateSequences.size()){
				startIndex = 0;
			}
			// only one to be found clear all previous
			findObj.clearIndices();
			for(int n = startIndex; n < delegateSequences.size(); n++){		
				Sequence seq = delegateSequences.get(n);
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
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		Rectangle selectionBounds = selectionModel.getSelectionBounds();
		for(Sequence seq: selectedSeqs){
			if(undoable){
				editedSequences.add(seq.getCopy());
			}
			seq.insertGapRightOfSelectedBase();
		}

		if(selectedSeqs.size() == delegateSequences.size()){
			int posToAdd = (int) (selectionBounds.getBounds().getMaxX() + 1);
			getAlignmentMeta().insertPosition(posToAdd);
		}

		if(selectedSeqs.size() > 0){
			fireSequencesChanged(selectedSeqs);
		}

		return editedSequences;	
	}

	public List<Sequence> insertGapLeftOfSelectedBase(boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		Rectangle selectionBounds = selectionModel.getSelectionBounds();
		for(Sequence seq: selectedSeqs){
			if(undoable){
				Sequence copy =  seq.getCopy();				
				editedSequences.add(copy);
			}
			seq.insertGapLeftOfSelectedBase();
		}

		if(selectedSeqs.size() > 0 && selectedSeqs.size() == delegateSequences.size()){
			int posToAdd = (int) (selectionBounds.getBounds().getMaxX() - 1);
			getAlignmentMeta().insertPosition(posToAdd);
		}

		if(selectedSeqs.size() > 0){
			fireSequencesChanged(selectedSeqs);
		}

		return editedSequences;
	}

	public List<Sequence> deleteGapMoveLeft(boolean undoable) {
		boolean gapPresentInAll = true;
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences();
		Rectangle selectionBounds = selectionModel.getSelectionBounds();
		for(Sequence seq: selectedSeqs){
			//logger.info("hassel" + seq);
			if(! seq.isGapLeftOfSelection()){
				gapPresentInAll = false;
				break;
			}
		}

		if(gapPresentInAll){
			selectedSeqs = selectionModel.getSelectedSequences(); 
			for(Sequence seq: selectedSeqs){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				seq.deleteGapLeftOfSelection();
			}
		}
		if(gapPresentInAll && selectedSeqs.size() == delegateSequences.size()){
			int posToDelete = (int) (selectionBounds.getBounds().getMaxX() - 1);
			getAlignmentMeta().deletePosition(posToDelete);
		}

		if(gapPresentInAll){
			fireSequencesChanged(selectedSeqs);
		}

		return editedSequences;	
	}

	public List<Sequence> deleteGapMoveRight(boolean undoable) {
		boolean gapPresentInAll = true;
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		Rectangle selectionBounds = selectionModel.getSelectionBounds();
		for(Sequence seq: selectedSeqs){
			//logger.info("hassel" + seq);
			if(! seq.isGapRightOfSelection()){
				gapPresentInAll = false;
				break;
			}
		}

		if(gapPresentInAll){
			selectedSeqs = selectionModel.getSelectedSequences(); 
			for(Sequence seq: selectedSeqs){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				seq.deleteGapRightOfSelection();
			}
		}
		if(gapPresentInAll && selectedSeqs.size() == delegateSequences.size()){
			int posToDelete = (int) (selectionBounds.getBounds().getMaxX() + 1);
			getAlignmentMeta().deletePosition(posToDelete);
		}
		if(gapPresentInAll){
			fireSequencesChanged(selectedSeqs);
		}

		return editedSequences;

	}	

	public boolean isGapPresentRightOfSelection() {
		boolean gapPresentInAll = true;
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		for(Sequence seq: selectedSeqs){
			if(! seq.isGapRightOfSelection()){
				gapPresentInAll = false;
				break;
			}
		}
		return gapPresentInAll;
	}

	public boolean isGapOrEndPresentRightOfSelection() {
		boolean gapOrEndPresentInAll = true;
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		for(Sequence seq: selectedSeqs){
			if(! seq.isGapOrEndRightOfSelection()){
				gapOrEndPresentInAll = false;
				break;
			}
		}
		return gapOrEndPresentInAll;
	}

	public List<Sequence> moveSelectedResiduesRightIfGapIsPresent(boolean undoable) {
		Rectangle oldSelectRectangle = selectionModel.getSelectionBounds();

		List<Sequence> editedSequences = new ArrayList<Sequence>();
		boolean wasEndRightOfSelection = false;
		if(isGapOrEndPresentRightOfSelection()){
			List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
			for(Sequence seq: selectedSeqs){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				if(seq.isEndRightOfSelection()){
					wasEndRightOfSelection = true;
				}
				seq.moveSelectedResiduesRightIfGapOrEndIsPresent();
			}
		}

		Rectangle newSelect = selectionModel.getSelectionBounds();
		if(oldSelectRectangle == null){
			// nothing can have changed
		}else{
			newSelect.add(oldSelectRectangle);
			fireSequencesChanged(newSelect);
			if(wasEndRightOfSelection){
				rightPadWithGapUntilEqualLength();
			}
		}

		return editedSequences;
	}

	public boolean isGapPresentLeftOfSelection() {
		boolean gapPresentInAll = true;
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		for(Sequence seq: selectedSeqs){
			if(! seq.isGapLeftOfSelection()){
				gapPresentInAll = false;
				break;
			}
		}
		return gapPresentInAll;
	}

	public List<Sequence> moveSelectedResiduesLeftIfGapIsPresent(boolean undoable){
		Rectangle oldSelectRectangle = selectionModel.getSelectionBounds();
		List<Sequence> editedSequences = new ArrayList<Sequence>();

		if(isGapPresentLeftOfSelection()){
			List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
			for(Sequence seq: selectedSeqs){
				if(undoable){
					editedSequences.add(seq.getCopy());
				}
				seq.moveSelectedResiduesLeftIfGapIsPresent();

			}
		}

		Rectangle newSelect = selectionModel.getSelectionBounds();
		if(oldSelectRectangle == null){
			// nothing can have changed
		}else{
			newSelect.add(oldSelectRectangle);
			fireSequencesChanged(newSelect);
		}


		return editedSequences;
	}

	// TODO break this into two (is gap present) and move

	public List<Sequence> moveSelectedResiduesIfGapIsPresent(int diff, boolean undoable){
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		// TODO this is moving and remembering startpos
		int unmovedDiff = diff - selectionOffset;
		if(unmovedDiff < 0){
			int absDiff = Math.abs(unmovedDiff);
			int n = 0;
			while(n < absDiff){
				// only keep first one
				if(editedSequences == null){
					editedSequences = this.moveSelectedResiduesLeftIfGapIsPresent(undoable);
				}else{
					this.moveSelectedResiduesLeftIfGapIsPresent(undoable);
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
					editedSequences = this.moveSelectedResiduesRightIfGapIsPresent(undoable);
				}else{
					this.moveSelectedResiduesRightIfGapIsPresent(undoable);
				}
				n++;
			}
		}				
		selectionOffset = diff;
		return editedSequences;
	}

	public void realignNucleotidesUseTheseAASequenceAsTemplate(AlignmentListModel templateSeqs) throws Exception{

		// make sure this alignment is translated
		setTranslation(true);

		for(Sequence templateSeq: templateSeqs.getDelegateSequencesCopy()){

			// do partial name since it if being cut by some programs....
			Sequence nucSeq =  this.getSequenceByName(templateSeq.getName());
			logger.info("nucSeq=" + nucSeq.getName() + nucSeq.getBasesAsString());
			logger.info("templateSeq=" + templateSeq.getName() + templateSeq.getBasesAsString());

			realignNucleotidesUseThisAASequenceAsTemplate(nucSeq, templateSeq);


		}
		// show result as nucleotides
		setTranslation(false);

		fireSequencesChangedAll();
	}

	private void realignNucleotidesUseThisAASequenceAsTemplate(Sequence nucSeq, Sequence template) throws Exception {

		if(nucSeq instanceof InMemorySequence){

			StringBuilder newSeq = new StringBuilder(nucSeq.getLength());
			int nextFindStartPos = 0;
			for(int n = 0; n < template.getLength(); n++){
				byte nextAAByte = template.getBaseAtPos(n);
				AminoAcid aaTemplate = AminoAcid.getAminoAcidFromByte(nextAAByte);


				//			logger.info("aaTemplate.getCodeCharVal()" + aaTemplate.getCodeCharVal());
				if(aaTemplate.getCodeCharVal() == AminoAcid.GAP.getCodeCharVal()){
					newSeq.append((char)SequenceUtils.GAP_SYMBOL);
					newSeq.append((char)SequenceUtils.GAP_SYMBOL);
					newSeq.append((char)SequenceUtils.GAP_SYMBOL);
				}else{

					//				logger.info("search for " + aaTemplate.getCodeCharVal() + " in seq " + nucSeq.getName() + " from pos " + nextFindStartPos);
					int posFound = nucSeq.find(aaTemplate.getCodeByteVal(), nextFindStartPos); 
					if(posFound == -1){
						logger.info("posnotfound");
						throw new Exception("Alignments not matching-exception, when trying to align sequences");
					}
					byte[] nextNucs = nucSeq.getGapPaddedCodonInTranslatedPos(posFound);
					newSeq.append(new String(nextNucs));
					nextFindStartPos = posFound + 1;		
				}
			}
			logger.info("newSeq.length()" + newSeq.length());

			((InMemorySequence) nucSeq).setBases(newSeq.toString().getBytes());
			fireSequencesChanged(nucSeq);
		}
	}
	/*
	private void realignNucleotidesUseThisAASequenceAsTemplate(Sequence nucSeq, Sequence template) throws Exception {

		StringBuilder newSeq = new StringBuilder(nucSeq.getLength());

		int nextPos = 0;
		for(int n = 0; n < template.getLength(); n++){

			byte nextAAByte = template.getBaseAtPos(n);
			AminoAcid aaTemplate = AminoAcid.getAminoAcidFromByte(nextAAByte);



			if(aaTemplate.getCodeCharVal() == AminoAcid.GAP.getCodeCharVal()){
				newSeq.append((char)SequenceUtils.GAP_SYMBOL);
				newSeq.append((char)SequenceUtils.GAP_SYMBOL);
				newSeq.append((char)SequenceUtils.GAP_SYMBOL);
			}else{

				byte nextByte = nucSeq.getBaseAtPos(n);
				AminoAcid translated = AminoAcid.getAminoAcidFromByte(nextByte);
				if(translated != aaTemplate){
					logger.info("posnotfound");
//					System.out.println("");
//					throw new Exception("Alignments not matching-exception, when trying to align sequences");
				}					
			}



		}
	}
	 */

	public Sequence getSequenceByName(String name) {
		if(name == null){
			return null;
		}
		Sequence foundSeq = null;
		for(Sequence seq: delegateSequences){
			if(name.equalsIgnoreCase(seq.getName())){
				foundSeq = seq;
				break;
			}
		}
		return foundSeq;	
	}

	public ArrayList<Sequence> getSequencesByName(String name) {
		ArrayList<Sequence> foundSeqs = new ArrayList<Sequence>();
		if(name == null){
			return foundSeqs;
		}
		for(Sequence seq: delegateSequences){
			if(name.equalsIgnoreCase(seq.getName())){
				foundSeqs.add(seq);
			}
		}
		return foundSeqs;	
	}



	public void deleteAllGaps() {
		for(Sequence seq: delegateSequences){
			seq.deleteAllGaps();
		}
		fireSequencesChangedAll();
	}

	public boolean rightPadWithGapUntilEqualLength(){
		int longLen = getLongestSequenceLength();
		ArrayList<Sequence> paddedSeqs = new ArrayList<Sequence>();
		for(Sequence sequence : delegateSequences){
			if(sequence.getLength() < longLen){
				sequence.rightPadSequenceWithGaps(longLen);
				paddedSeqs.add(sequence);
			}
		}
		if(paddedSeqs.size() > 0){
			fireSequencesChanged(paddedSeqs);
			return true;
		}
		else{
			return false;
		}
	}

	public boolean leftPadWithGapUntilEqualLength() {
		int longLen = getLongestSequenceLength();
		ArrayList<Sequence> paddedSeqs = new ArrayList<Sequence>();
		for(Sequence sequence : delegateSequences){
			if(sequence.getLength() < longLen){
				sequence.leftPadSequenceWithGaps(longLen);
				paddedSeqs.add(sequence);
			}

		}
		if(paddedSeqs.size() > 0){
			fireSequencesChanged(paddedSeqs);
			return true;
		}
		else{
			return false;
		}
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
		logger.info("cons=" + cons);
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
		if(wasTrimmed){
			fireSequencesChangedAll();
		}
		return wasTrimmed;
	}


	public void deleteBasesInAllSequencesFromMask(boolean[] deleteMask) {
		for(Sequence sequence : delegateSequences){
			sequence.deleteBasesFromMask(deleteMask);
		}
		fireSequencesChangedAll();
	}

	public String getConsensus() {
		if(isTranslated || getSequenceType() == SequenceUtils.TYPE_AMINO_ACID){
			return getAminoAcidConsensus();
		}
		else{
			return getNucleotideConsensus();
		}
	}

	private String getAminoAcidConsensus() {
		byte[] consVals = new byte[getLongestSequenceLength()];
		Arrays.fill(consVals, AminoAcid.GAP.getCodeByteVal());
		for(Sequence sequence : delegateSequences){
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
		for(Sequence sequence : delegateSequences){
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
		for(Sequence seq : delegateSequences){
			seq.reverseComplement();
		}

		fireSequencesChangedAll();
	}

	public void reverseComplementFullySelectedSequences() {
		List<Sequence> fullySelected = getFullySelectedSequences();
		for(Sequence seq : fullySelected){
			seq.reverseComplement();
		}
		if(fullySelected.size() > 0){
			fireSequencesChanged(fullySelected);
		}
	}


	public void complement() {
		for(Sequence seq : delegateSequences){
			seq.complement();	
		}

		fireSequencesChangedAll();
	}

	public int getLongestSequenceName() {
		long startTime = System.currentTimeMillis();
		if(cachedLongestSequenceName <= 0){
			// this is double locked to avoid synchronized block after the cached initialization of variacle
			// cached variable has to be declared volatile above
			// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html and http://en.wikipedia.org/wiki/Double-checked_locking
			synchronized(this){
				if(cachedLongestSequenceName <=0){
					int maxlen = 0;
					for(Sequence seq: delegateSequences){
						maxlen = Math.max(maxlen, seq.getName().length());
					}
					cachedLongestSequenceName = maxlen;
				}
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("getLongestSequenceName took " + (endTime - startTime) + " milliseconds");	
		return cachedLongestSequenceName;
	}

	public boolean isPositionValid(int x, int y) {
		return rangeCheck(x,y);
	}

	public boolean rangeCheck(int x, int y){
		boolean isValid = false;
		if(y > -1 && y < delegateSequences.size()){
			if(x >= 0 && x < delegateSequences.get(y).getLength()){
				isValid = true;
			}
		}
		return isValid;
	}

	private boolean rangeCheck(Point point) {
		return rangeCheck(point.x, point.y);
	}

	public Sequence getSequenceByID(int id){
		for(Sequence seq: delegateSequences){
			if(seq.getID() == id){
				return seq;
			}
		}
		return null;
	}


	public void sortSequencesByName() {
		//logger.info(sequences);
		Collections.sort(delegateSequences);

		fireSequencesOrderChangedAll();
	}

	public void sortSequencesByCharInSelectedColumn(AliHistogram histogram) {
		// get first selected column
		Point selPos = selectionModel.getFirstSelectedPos();
		Collections.sort(delegateSequences, new SequencePositionComparator(selPos.x, getHistogram()));

		fireSequencesOrderChangedAll();
	}

	public AliHistogram getHistogram(){
		if(cachedHistogram == null){
			// this is double locked to avoid synchronized block after the lazy initialization of Histogram object
			// Histogram has to be declared volatile above
			// see: http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html and http://en.wikipedia.org/wiki/Double-checked_locking
			synchronized(this){
				if(cachedHistogram == null){
					cachedHistogram = createHistogram();	
				}
			}
		}
		return cachedHistogram;
	}


	private AliHistogram createHistogram(){
		long startTime = System.currentTimeMillis();
		AliHistogram histogram = null;
		if(sequenceType == SequenceUtils.TYPE_AMINO_ACID || isTranslated){
			histogram = new AAHistogram(getLongestSequenceLength());
		}else{
			histogram = new NucleotideHistogram(getLongestSequenceLength());
		}

		for(Sequence seq: delegateSequences){
			if(sequenceType == SequenceUtils.TYPE_AMINO_ACID || isTranslated){
				histogram.addSequence(seq);
			}else{
				histogram.addSequence(seq);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("Create histogram took " + (endTime - startTime) + " milliseconds");
		return histogram;
	}

	public List<Sequence> replaceSelectedCharactersWithThis(AlignmentListModel newOnes, boolean undoable) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		for(Sequence seq: selectedSeqs){
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
			seq.setSelection(selPos[0],selPos[0] + realignedBases.length -1, false);
			if(undoable){
				editedSequences.add(seq.getCopy());
			}
		}
		if(selectedSeqs.size() > 0){
			Rectangle bounds = selectionModel.getSelectionBounds();
			fireSequencesChanged(bounds);
		}

		return editedSequences;	
	}



	public boolean mergeTwoSequences(InMemorySequence seq1, InMemorySequence seq2, boolean allowOverlap){		
		if(sequenceType == SequenceUtils.TYPE_NUCLEIC_ACID){
			return mergeTwoNucleotideSequences(seq1, seq2, allowOverlap);
		}
		else{
			return mergeTwoAminoAcidSequences(seq1, seq2, allowOverlap);
		}

	}

	public boolean mergeTwoAminoAcidSequences(InMemorySequence seq1, InMemorySequence seq2, boolean allowOverlap){
		boolean isMerged = false;
		int nExactOverlap = SequenceUtils.countExactAminoAcidOverlap(seq1, seq2);
		int nDifferentOverlap = SequenceUtils.countDifferentAminoAcidOverlap(seq1, seq2);

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
		if(isMerged){
			List<Sequence> mergedSeqs = new ArrayList<Sequence>(2);
			mergedSeqs.add(seq1);
			mergedSeqs.add(seq2);
			fireSequencesChanged(mergedSeqs);
		}

		return isMerged;
	}


	public boolean mergeTwoNucleotideSequences(InMemorySequence seq1, InMemorySequence seq2, boolean allowOverlap){
		boolean isMerged = false;
		int nExactOverlap = SequenceUtils.countExactNucleotideOverlap(seq1, seq2);
		int nDifferentOverlap = SequenceUtils.countDifferentNucleotideOverlap(seq1, seq2);

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

		if(isMerged){
			List<Sequence> mergedSeqs = new ArrayList<Sequence>(2);
			mergedSeqs.add(seq1);
			mergedSeqs.add(seq2);
			fireSequencesChanged(mergedSeqs);
		}


		return isMerged;
	}






	public ArrayList<Sequence> findDuplicates(){

		HashSet<Sequence> dupeSequences = new HashSet<Sequence>();
		for(int n = 0; n < delegateSequences.size(); n++){
			Sequence testSeq = delegateSequences.get(n);

			boolean isDupe = false;

			for(int m = n + 1; m < delegateSequences.size(); m++){

				Sequence otherSeq = delegateSequences.get(m);

				if(testSeq.getLength() == otherSeq.getLength()){
					if(SequenceUtils.isSeqResiduesIdentical(testSeq, otherSeq)){
						// add the dupes (since it is a set they will only be added once
						dupeSequences.add(testSeq);
						dupeSequences.add(otherSeq);
					}
				}
				else{
					logger.debug("wrong len");
				}
			}


			logger.info("dupeSequences.size()" + dupeSequences.size());

		}
		ArrayList<Sequence> dupeList = new ArrayList<Sequence>(dupeSequences);
		return dupeList;
	}

	/*
	public AliHistogram getTranslatedHistogram() {
		if(cachedTranslatedHistogram == null){
			cachedTranslatedHistogram = createTranslatedHistogram();
		}
		return cachedTranslatedHistogram;
	}



	private AliHistogram createTranslatedHistogram() {
		long startTime = System.currentTimeMillis();

		boolean wasTranslated = isTranslated;
		setTranslation(true);

		AAHistogram histogram = new AAHistogram(getLongestSequenceLength());

		for(Sequence seq: delegateSequences){
			for(int n = 0; n < seq.getLength(); n++){ 
				histogram.addAminoAcid(n,AminoAcid.getAminoAcidFromByte(seq.getBaseAtPos(n)));
			}
		}

		if(wasTranslated == false){
			setTranslation(false);
		}

		long endTime = System.currentTimeMillis();
		logger.info("Create translated histogram took " + (endTime - startTime) + " milliseconds");
		return histogram;
	}
	 */

	public int size() {
		return delegateSequences.size();
	}

	public void sortSequencesByThisModel(AlignmentListModel prevSeqOrder){
		ArrayList<Sequence> seqsInOrder = new ArrayList<Sequence>(prevSeqOrder.size());
		for(int n = 0; n < prevSeqOrder.getSize(); n++){
			Sequence prev = prevSeqOrder.get(n);
			//Sequence seq = getSequenceByPartialName(prev.getName());
			Sequence seq = getSequenceByName(prev.getName());
			seqsInOrder.add(seq);
		}
		if(seqsInOrder.size() == delegateSequences.size()){
			setSequences(seqsInOrder);
		}
		fireSequencesChangedAll();
	}

	public ArrayList<String> findDuplicateNames() {
		ArrayList<String> dupes = new ArrayList<String>();
		HashSet set = new HashSet<String>(delegateSequences.size());
		for(Sequence seq: delegateSequences){
			boolean isNotDuplicate = set.add(seq.getName());
			if(isNotDuplicate == false){
				dupes.add(seq.getName());
			}
		}
		return dupes;
	}

	//
	// DO something with selected sequences
	//

	public List<Sequence> replaceSelectedBasesWithGap(boolean undoable) {
		return replaceSelectedWithChar((char)SequenceUtils.GAP_SYMBOL, undoable);
	}

	public List<Sequence> replaceSelectedWithChar(char newChar, boolean undoable) {	
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences();
		boolean wasReplaced = false;
		for(Sequence seq: selectedSeqs){
			if(undoable){
				editedSequences.add(seq.getCopy());
			}
			seq.replaceSelectedBasesWithChar(newChar);
			wasReplaced = true;
		}
		if(wasReplaced){
			fireSequencesChanged(selectionModel.getSelectionBounds());
		}
		return editedSequences;
	}

	/**
	 * 
	 * TODO probably change this into two methods, getSelectedPositions and then deletePositions...
	 * @return
	 */
	public List<Sequence> deleteSelectedBases() {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		List<Sequence> selectedSeqs = selectionModel.getSelectedSequences(); 
		for(Sequence seq: selectedSeqs){
			editedSequences.add(seq);
			seq.deleteSelectedBases();
		}
		if(editedSequences.size() > 0){
			fireSequencesChangedAll();
		}
		return editedSequences;
	}


	//
	// ****************** SELECTION
	//

	public ArrayList<Sequence> findAndSelectDuplicates(){
		ArrayList<Sequence> dupes = findDuplicates();
		selectionModel.selectSequences(dupes);
		return dupes;
	}

	public void selectDuplicateNamesSequences() {
		ArrayList<String> dupeNames = findDuplicateNames();
		List<Sequence> dupeSeqs = new ArrayList<Sequence>();
		for(String dupeName: dupeNames){
			dupeSeqs.addAll(getSequencesByName(dupeName));
		}
		selectionModel.selectSequences(dupeSeqs);
	}

	public void selectEverythingWithinGaps(Point point) {
		if(!rangeCheck(point)){
			return;
		}
		// TODO this should probably be moved to out of listSelectionModel
		selectionModel.selectAllBasesUntilGapInThisSequence(delegateSequences.get(point.y), point.x);	
	}

	public void selectAll(CharSet aCharSet) {
		List<Integer> columns = new ArrayList<Integer>();
		for(int n = 0; n < getLongestSequenceLength(); n++){
			if(aCharSet.contains(n)){
				columns.add(new Integer(n));
			}else{
				// nothing to do
			}
		}
		if(columns.size() > 0){
			selectionModel.clearSequenceSelection();
			selectionModel.selectColumns(columns);
		}
	}

	public int getSelectedColumnCount() {
		return selectionModel.getSelectedColumnCount();
	}

	public int getSelectedSequencesCount() {
		return selectionModel.getSelectedSequencesCount();
	}

	public List<Sequence> getSelectedSequences() {
		return selectionModel.getSelectedSequences();
	}

	public String getSelectionNames() {
		return selectionModel.getSelectionNames();
	}

	public int getFirstSelectedWholeColumn() {
		return selectionModel.getFirstSelectedWholeColumn();	
	}

	public int getLastSelectedWholeColumn() {
		return selectionModel.getLastSelectedWholeColumn();
	}

	//
	// TODO domething about this one
	//
	public void setSelectionOffset(int i) {
		this.selectionOffset = i;
	}

	public void expandSelectionDown() {
		selectionModel.selectionExtendDown();
	}

	public String getSelectionAsNucleotides() {
		return selectionModel.getSelectionAsNucleotides();
	}

	public int getFirstSelectedSequenceIndex(){
		return selectionModel.getFirstSelectedSequenceIndex();
	}

	public int getLastSelectedSequenceIndex(){
		return selectionModel.getLastSelectedSequenceIndex();
	}

	public String getFirstSelectedName() {
		return selectionModel.getFirstSelectedName();
	}

	public List<Sequence> setFirstSelectedName(String newName) {
		return selectionModel.setFirstSelectedName(newName);
	}

	public Sequence getFirstSelected() {
		return selectionModel.getFirstSelected();
	}

	public boolean hasSelection() {
		return selectionModel.hasSelection();
	}

	public void selectAll() {
		selectionModel.selectAll();
	}

	public void selectionExtendRight() {
		selectionModel.selectionExtendRight();
	}

	public void selectionExtendLeft() {
		selectionModel.selectionExtendLeft();
	}

	public void invertSelection() {
		selectionModel.invertSelection();
	}

	public void copySelectionFromInto(int indexFrom, int indexTo) {
		selectionModel.copySelectionFromInto(indexFrom, indexTo);
	}

	public void selectColumn(int columnIndex) {
		selectionModel.selectColumn(columnIndex);
	}

	public void clearColumnSelection(int columnIndex) {
		selectionModel.clearColumnSelection(columnIndex);
	}

	public void copySelectionFromPosX1toX2(int x1, int x2) {
		selectionModel.copySelectionFromPosX1toX2(x1, x2);
	}

	public Point getFirstSelectedPos() {
		return selectionModel.getFirstSelectedPos();
	}

	public Point getLastSelectedPos() {
		return selectionModel.getLastSelectedPos();
	}

	public Point getFirstSelectedUngapedPos() {
		return selectionModel.getFirstSelectedUngapedPos();
	}

	public void setSelectionWithin(Rectangle bounds) {
		selectionModel.setSelectionWithin(bounds);
	}

	public void selectSequencesByName(String name) {
		ArrayList<Sequence> foundSeqs = getSequencesByName(name);
		selectionModel.selectSequences(foundSeqs);
	}

	public ArrayList<Integer> getIndicesOfSequencesWithSelection() {
		return selectionModel.getIndicesOfSequencesWithSelection();
	}

	public ArrayList<Integer> getIndicesOfSequencesWithAllSelected() {
		return selectionModel.getIndicesOfSequencesWithAllSelected();
	}

	public void selectSequencesWithIndex(List<Integer> listVals){
		selectionModel.selectSequencesWithIndex(listVals);
	}

	public void selectSequencesWithIndex(int[] selectedIndex){
		selectionModel.selectSequencesWithIndex(selectedIndex);
	}

	public void clearSequenceSelection() {
		selectionModel.clearSequenceSelection();
	}

	public void selectSequenceWithIndex(int y) {
		selectionModel.selectSequenceWithIndex(y);	
	}

	public boolean isBaseSelected(int x, int y) {
		//logger.info("isBaseSelected" + sequences.get(y));
		return selectionModel.isBaseSelected(x, y);
	}

	public void setSelectionAt(int xPos, int yPos, boolean clearFirst) {
		selectionModel.setSelectionAt(xPos, yPos, clearFirst);
	}

	public long getSelectionSize(){
		return selectionModel.getSelectionSize();
	}

	public boolean hasFullySelectedSequences() {
		return selectionModel.hasFullySelectedSequences();
	}


	public AlignmentSelectionModel getAlignmentSelectionModel() {
		return selectionModel;
	}

	public void setTempSelection(Rectangle selectRectMatrixCoords) {
		selectionModel.setTempSelection(selectRectMatrixCoords);
	}

	public Rectangle getTempSelection() {
		return selectionModel.getTempSelection();	
	}

	public void clearTempSelection() {
		selectionModel.clearTempSelection();
	}

	public void clearAllSelectionInSequenceWithIndex(int y) {
		selectionModel.clearAllSelectionInSequenceWithIndex(y);	
	}

	public void addAlignmentSelectionListener(AlignmentSelectionListener listener) {
		selectionModel.addAlignmentSelectionListener(listener);
	}


	//
	// ****************** END SELECTION
	//



	//
	// ****************** SEQUENCES CHANGED EVENTS
	//

	private void fireSequencesChanged(Sequence seq) {
		int index = delegateSequences.indexOf(seq);
		fireSequencesChanged(index, index);
	}

	private void fireSequencesChanged(List<Sequence> seqs) {
		int minIndex = delegateSequences.size();
		int maxIndex = 0;
		for(Sequence seq: seqs){
			int index = delegateSequences.indexOf(seq);
			minIndex = Math.min(index, minIndex);
			maxIndex = Math.max(index, maxIndex);
		}

		fireSequencesChanged(minIndex, maxIndex);

	}

	private void fireSequencesChanged(int minIndex, int maxIndex) {
		// TODO this might be a bit ugly...
		cachedLongestSequenceLength = -1;
		Rectangle rect = new Rectangle(0,minIndex, this.getLongestSequenceLength(), maxIndex + 1);
		fireSequencesChanged(rect);

	}

	private void fireSequencesChanged(Rectangle rect) {
		sequencesChanged(rect);

	}

	private void sequencesChanged(Rectangle rect) {
		logger.info("sequencesChanged");
		// clear cached values
		cachedLongestSequenceName = -1;
		cachedLongestSequenceLength = -1;
		cachedHistogram = null;


		Object[] listeners = listenerList.getListenerList();
		AlignmentDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {

			if (listeners[i] == AlignmentDataListener.class) {
				if (e == null) {
					e = new AlignmentDataEvent(this, ListDataEvent.CONTENTS_CHANGED, rect);
				}
				((AlignmentDataListener)listeners[i+1]).contentsChanged(e);
			}
			else if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new AlignmentDataEvent(this, ListDataEvent.CONTENTS_CHANGED, rect);
				}
				((ListDataListener)listeners[i+1]).contentsChanged(e);
			}
		}
	}

	private void fireSequencesChangedAll() {
		fireSequencesChanged(0, this.size() -1);
	}

	private void fireSequencesOrderChangedAll() {
		fireSequencesChanged(0, this.size() -1);
	}

	private void fireSequencesChangedAllNew() {
		fireSequencesChanged(0, this.size() -1);
	}

	private void fireSequenceIntervalRemoved(int index0, int index1){
		fireSequencesChanged(index0, index1);
	}

	private void fireSequenceIntervalAdded(int index0, int index1) {
		if(index0 < 0 || index1 < 0){
			return;
		}
		fireSequencesChanged(index0, index1);
	}

	public void setTranslation(boolean shouldTrans) {
		if(shouldTrans != isTranslated){
			if(shouldTrans){
				selectionModel.translateSelection(getAlignment().getAlignmentMeta());
			}else{
				selectionModel.reTranslateSelection(getAlignment().getAlignmentMeta());
			}
			isTranslated = shouldTrans;


			fireSequencesChangedAll();
		}
	}

	public AlignmentMeta getAlignmentMeta(){
		if(getAlignment() != null){
			return getAlignment().getAlignmentMeta();
		}
		return null;
	}

	private Alignment getAlignment() {
		return this.alignment;

	}

	public boolean isTranslated() {
		return isTranslated;
	}

	public Rectangle getSelectionBounds() {
		return selectionModel.getSelectionBounds();
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	public void setSequenceType(int sequenceType) {
		this.sequenceType = sequenceType;
	}
}
