package aliview.sequencelist;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EventListener;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jebl.evolution.sequences.Sequences;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.AliView;
import aliview.alignment.AlignmentMeta;
import aliview.sequences.Sequence;
import aliview.sequencelist.Interval;
import aliview.utils.Utils;

/**
 * 
 * This model is delegating to the sequences themself to keep track of selection
 * 
 * 
 */

public class AlignmentSelectionModel{
	
	private static final Logger logger = Logger.getLogger(AlignmentSelectionModel.class);
	private static final String LF = System.getProperty("line.separator");
	private AlignmentListModel sequences;
	protected EventListenerList listenerList = new EventListenerList();
	private Rectangle tempSelectionRect;
	private Rectangle tempSelectionMaxRect;
	private SequenceListSelectionModel sequenceListSelectionModel;

	public AlignmentSelectionModel(AlignmentListModel sequenceListModel) {
		this.sequences = sequenceListModel;
		this.sequenceListSelectionModel = new SequenceListSelectionModel(this);
	}
	
	
	public SequenceListSelectionModel getSequenceListSelectionModel() {
		return sequenceListSelectionModel;
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
	
	public ArrayList<Integer> getIndicesOfSequencesWithAllSelected() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int n = 0; n < sequences.size(); n++){
			if(sequences.get(n).isAllSelected()){
				indices.add(new Integer(n));
			}
		}
		return indices;
	}
	
	public boolean isBaseSelected(int x, int y) {
		//logger.info("isBaseSel" + sequences.get(y));
		boolean isSel = sequences.get(y).isBaseSelected(x);
//		if(isSel){ logger.info("isSel" + sequences.get(y));};
		return sequences.get(y).isBaseSelected(x);
	}
	
	
	
	public void selectSequenceWithIndex(int index){
		setSequenceSelection(index, index);
	}
	
	public void selectSequencesWithIndex(List<Integer> listVals){
		Integer[] array = listVals.toArray(new Integer[listVals.size()]);	
		int[] intVals = ArrayUtils.toPrimitive(array, 0);
		selectSequencesWithIndex(intVals);
	}
	
	public void selectSequencesWithIndex(int[] selectedIndex){
		List<Sequence> seqs = new ArrayList<Sequence>(selectedIndex.length);
		for(int index: selectedIndex){
			seqs.add(sequences.get(index));
		}	
		changeSelection(seqs, true);
	}
	
	public void setSelectionAt(int xPos, int yPos) {
		setSelectionAt(xPos, yPos, false);
	}
	
	public void setSelectionAt(int xPos, int yPos, boolean clearFirst) {
		//delegateLSM.setAnchorSelectionIndex(yPos);
		//delegateLSM.setLeadSelectionIndex(yPos);
		changeSelection(xPos, yPos, xPos, yPos, clearFirst);
	}
	
	
	private void changeSelection(int x1, int y1, int x2, int y2, boolean clearFirst){
		
		Rectangle newRect = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1-y2));
		
		logger.info("newRect" + newRect);

		// Get current selection so we can fire a correct bounding box
		Rectangle oldSelectRect = getSelectionBounds();
		
		// value is adjusting true
		
		for(int n = 0; n < sequences.size(); n++){
			
			if(n>= newRect.getMinY() && n <= newRect.getMaxY()){
				sequences.get(n).setSelection(newRect.x, newRect.x + newRect.width, clearFirst);
			}else{
				sequences.get(n).clearAllSelection();
			}
		}
		
		// affected area is old selection plus new
		if(clearFirst && oldSelectRect != null){
			logger.info("newRect" + newRect);
			logger.info("oldRect" + oldSelectRect);
			if(Utils.hasSameBounds(newRect, oldSelectRect)){	
				// do nothing
			}else{
				Rectangle both = Utils.addRects(oldSelectRect, newRect);
				fireSelectionChanged(both, false);
			}
		}
		else{
			fireSelectionChanged(newRect, false);
		}
	}

	public void setSequenceSelection(int index0, int index1) {
		changeSelection(index0, index1, true);
	}
	
	public void addSequenceSelection(int index0, int index1) {
		changeSelection(index0, index1, false);
	}
	
	public void removeSequenceSelection(int index0, int index1) {
		// set value is adjusting true
		
		int minIndex = Math.min(index0, index1);
		int maxIndex = Math.max(index0, index1);
				
				for(int n = 0; n < sequences.size(); n++){
					
					if(n>= minIndex && n<= maxIndex){
						sequences.get(n).clearAllSelection();
					}else{
						// nothing
					}
				}
				
				// fire all because on insert and delete indexes might change
				fireSelectionChanged(0, sequences.size());

	}
	
	public void setSequenceSelection(List<Sequence> moreSeqs) {
		changeSelection(moreSeqs, true);
		
	}
	
	private void changeSelection(List<Sequence> toSelect, boolean clearFirst){
		Rectangle oldSelect = getSelectionBounds();
		logger.info("toSelect.size()" + toSelect.size());
		for(Sequence seq: sequences){
			
			if(toSelect.contains(seq)){
				seq.selectAllBases();
			}
			else if(clearFirst){
				seq.clearAllSelection();
			}
			
		}
		Rectangle newSelect = getSelectionBounds();
		logger.info("newSelect" + newSelect);		
		Rectangle addedSelection = Utils.addRects(oldSelect, newSelect);
		
		if(addedSelection != null){
			fireSelectionChanged(addedSelection, false);
		}
	}
		
	
	private void changeSelection(int index0, int index1, boolean clearFirst){
		
		// set value is adjusting true
		int minIndex = Math.min(index0, index1);
        int maxIndex = Math.max(index0, index1);
		
		for(int n = 0; n < sequences.size(); n++){
			
			if(n>= minIndex && n<= maxIndex){
				sequences.get(n).selectAllBases();
			}else{
				if(clearFirst){
					sequences.get(n).clearAllSelection();
				}
			}
		}
		
		// Update all
		fireSelectionChanged(0, sequences.size());
		
		// set value is adjusting false
//		if(clearFirst || updateAll){
//			fireSelectionChanged(0, sequences.size());
//		}else{
//			fireSelectionChanged(minIndex, maxIndex);
//		}
	}
		
	
	public long getSelectionSize(){
		long size = 0;
		for(Sequence sequence : this.sequences){
			size += sequence.countSelectedPositions(0, sequence.getLength());
		}
		return size;
	}
	
	public Rectangle getSelectionBounds(){
		if(! hasSelection()){
			return null;
		}
		// TODO this could be changed if non rectangular selections
		Rectangle bounds = new Rectangle(getFirstSelectedPos());
		bounds.add(getLastSelectedPos());
		return bounds;
	}
	
	public List<Sequence> getSelectedSequences() {
		ArrayList<Sequence> selection = new ArrayList<Sequence>();
		for(Sequence sequence : sequences){
			if(sequence.hasSelection()){
				selection.add(sequence);
			}
		}
		return selection;
	}
	
	public List<Sequence> getUnSelectedSequences() {
		ArrayList<Sequence> selection = new ArrayList<Sequence>();
		for(Sequence sequence : sequences){
			if(! sequence.hasSelection()){
				selection.add(sequence);
			}
		}
		return selection;
	}
	
	
	
	
	
	
	public void selectBases(Sequence seq, Interval foundPos) {
		seq.setSelection(foundPos.startPos, foundPos.endPos, false);
		fireSelectionChanged(seq, true);
	}
	
	
	
	
	

	public void selectAllBasesUntilGapInThisSequence(Sequence sequence, int x){
		sequence.selectAllBasesUntilGap(x);
		fireSelectionChanged(sequence, false);
	}
	
	

	

	public void selectSequences(List<Sequence> seqs) {
		changeSelection(seqs, true);
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
	
	public String getFirstSelectedName() {
		String name = null;
		if(getFirstSelected() != null){
			name = getFirstSelected().getName();
		}
		return name;
	}
	
	public List<Sequence> setFirstSelectedName(String newName) {
		List<Sequence> editedSequences = new ArrayList<Sequence>();
		if(newName == null){
			return editedSequences;
		}
		if(getFirstSelected() != null){
			editedSequences.add(getFirstSelected().getCopy());
			getFirstSelected().setName(newName);
		}
		return editedSequences;
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
		long startTime = System.currentTimeMillis();
		for(Sequence sequence : sequences){
			if(sequence.hasSelection()){
				return true;
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("hasSelection false " + (endTime - startTime) + " milliseconds");
		return false;
	}
	
	public void selectAll() {
		for(Sequence seq: sequences){
			seq.selectAllBases();
		}
		fireSelectionChangedAll();
	}
	
	public void selectionExtendRight(){
		if(!hasSelection()){
			return;
		}
		Rectangle oldSelect = getSelectionBounds();
		for(Sequence seq: sequences){
			seq.selectionExtendRight();
		}
		Rectangle newSelect = getSelectionBounds();
		newSelect.add(oldSelect);
		fireSelectionChanged(newSelect, false);
	}
	
	public void selectionExtendLeft() {
		if(!hasSelection()){
			return;
		}
		Rectangle oldSelect = getSelectionBounds();
		for(Sequence seq: sequences){
			seq.selectionExtendLeft();
		}
		Rectangle newSelect = getSelectionBounds();
		newSelect.add(oldSelect);
		fireSelectionChanged(newSelect, false);
	}
	
	public void selectionExtendDown() {
		if(!hasSelection()){
			return;
		}
		Rectangle oldSelect = getSelectionBounds();
		
		// start one above bottom
		for(int n = sequences.size()-2; n >= 1; n--){
			Sequence seq = sequences.get(n);
			if(seq.hasSelection()){
				int[] selected = seq.getSelectedPositions();
				for(int index: selected){
					sequences.get(n+1).setSelection(index,index, true);
				}
			}
		}
		Rectangle newSelect = getSelectionBounds();
		newSelect.add(oldSelect);
		fireSelectionChanged(newSelect, false);
	}
	
	
	public void invertSelection() {
		for(Sequence seq: sequences){
			seq.invertSelection();
		}
		fireSelectionChangedAll();
	}
	
	public void copySelectionFromInto(int indexFrom, int indexTo) {
		Sequence seqFrom = sequences.get(indexFrom);
		Sequence seqTo = sequences.get(indexTo);
		
		for(int x = 0; x < seqFrom.getLength() || x < seqTo.getLength(); x++){
			if(seqFrom.isBaseSelected(x)){
				seqTo.setSelectionAt(x);
			}
			else{
				seqTo.clearSelectionAt(x);
			}
		}
		
		fireSelectionChanged(indexFrom, indexTo, false);
		
	}

	public void selectColumn(int columnIndex) {
		for(Sequence seq: sequences){
			seq.setSelectionAt(columnIndex);
		}
		fireSelectionChanged(new Rectangle(columnIndex,0,columnIndex,sequences.size()), false);
	}
	
	public void selectColumns(List<Integer> columns){
		int maxIndex = 0;
		int minIndex = sequences.getLongestSequenceLength();
		for(Integer col: columns){
			maxIndex = Math.max(maxIndex, col.intValue());
			minIndex = Math.min(minIndex, col.intValue());
			for(Sequence seq: sequences){
				seq.setSelectionAt(col.intValue());
			}
		}
		fireSelectionChanged(new Rectangle(minIndex,0,maxIndex,sequences.size()), false);
	}
	
	public void clearColumnSelection(int columnIndex) {
		for(Sequence seq: sequences){
			seq.clearSelectionAt(columnIndex);
		}
		fireSelectionChanged(new Rectangle(columnIndex,0,0,sequences.size()), false);	
	}

	public void copySelectionFromPosX1toX2(int x1, int x2) {
		for(Sequence seq: sequences){
			if(seq.isBaseSelected(x1)){
				seq.setSelectionAt(x2);
			}
			else{
				seq.clearSelectionAt(x2);
			}
		}
		fireSelectionChanged(new Rectangle(x1,0,x2,sequences.size()), false);
	}

	public Point getFirstSelectedPos() {
		int n = getFirstSelectedSequenceIndex();
		if(n != -1){
			return new Point(sequences.get(n).getFirstSelectedPosition(), n);
		}else{
			return null;
		}
	}
	
	public int getFirstSelectedSequenceIndex() {
		for(int n = 0; n < sequences.size(); n++){
			if(sequences.get(n).hasSelection()){
				return n;
			}
		}
		return -1;
	}
	
	public int getLastSelectedSequenceIndex() {
		for(int n = sequences.size() - 1; n >= 0; n--){
			if(sequences.get(n).hasSelection()){
				return n;
			}
		}
		return -1;
	}
	
	public Point getLastSelectedPos() {
		int n = getLastSelectedSequenceIndex();
		if(n != -1){
			return new Point(sequences.get(n).getLastSelectedPosition(), n);
		}else{
			return null;
		}
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
	
	
	public void setSelectionWithin(Rectangle bounds) {
		changeSelection(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, true);
	}

	public boolean hasFullySelectedSequences() {
		for(Sequence seq: sequences){
			if(seq.isAllSelected()){
				return true;
			}
		}
		return false;
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
	
	public int getFirstSelectedWholeColumn() {
		Point pos = getFirstSelectedPos();
		if(pos == null){
			return -1;
		}
		else{
			for(Sequence seq: sequences){
				// seq has to be long enough
				if(seq.getLength() > pos.x && !seq.hasSelection()){
					return -1;
				}
			}
			return pos.x;
		}	
	}
	
	public int getLastSelectedWholeColumn() {
		Point pos = getLastSelectedPos();
		if(pos == null){
			return -1;
		}
		else{
			for(Sequence seq: sequences){
				// seq has to be long enough
				if(seq.getLength() > pos.x && !seq.hasSelection()){
					return -1;
				}
			}
			return pos.x;
		}	
	}
	
	
	
	public void clearAllSelectionInSequenceWithIndex(int index) {
		sequences.get(index).clearAllSelection();
		fireSelectionChanged(index, index);
	}
	
	public void clearSequenceSelection() {

			Rectangle oldSelectRectangle = getSelectionBounds();
			for(Sequence seq: sequences){
				seq.clearAllSelection();
			}
			if(oldSelectRectangle != null){
				fireSelectionChanged(oldSelectRectangle, false);
			}
	}
	
	
	
	/** {@inheritDoc} */
    public void addAlignmentSelectionListener(AlignmentSelectionListener l) {
        listenerList.add(AlignmentSelectionListener.class, l);
    }

    /** {@inheritDoc} */
    public void removeSequenceListSelectionListener(AlignmentSelectionListener l) {
        listenerList.remove(AlignmentSelectionListener.class, l);
    }

    /**
     * Returns an array of all the list selection listeners
     * registered on this <code>DefaultListSelectionModel</code>.
     *
     * @return all of this model's <code>ListSelectionListener</code>s
     *         or an empty
     *         array if no list selection listeners are currently registered
     *
     * @see #addListSelectionListener
     * @see #removeListSelectionListener
     *
     * @since 1.4
     */
    public AlignmentSelectionListener[] getAlignmentSelectionListeners() {
        return listenerList.getListeners(AlignmentSelectionListener.class);
    }


    /**
     * Notifies <code>ListSelectionListeners</code> that the value
     * of the selection, in the closed interval <code>firstIndex</code>,
     * <code>lastIndex</code>, has changed.
     */
    protected void fireSelectionChanged(int firstIndex, int lastIndex) {
    	fireSelectionChanged(firstIndex, lastIndex, getValueIsAdjusting());
    }
    
    protected void fireSelectionChangedAll() {
    	fireSelectionChanged(0, sequences.size(), getValueIsAdjusting());
    }

    private boolean getValueIsAdjusting() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
     * @param firstIndex the first index in the interval
     * @param lastIndex the last index in the interval
     * @param isAdjusting true if this is the final change in a series of
     *          adjustments
     * @see EventListenerList
     */
    protected void fireSelectionChanged(int index0, int index1, boolean isAdjusting)
    {
        Rectangle rect = new Rectangle(0, Math.min(index0, index1), sequences.getLongestSequenceLength(), Math.abs(index0 - index1));
        fireSelectionChanged(rect, isAdjusting);
    }

    
    private void fireSelectionChanged(Sequence sequence, boolean isAdjusting) {
    	int index = sequences.indexOf(sequence);
    	fireSelectionChanged(index, index, isAdjusting);
		
	}
    
/*
    private void fireSelectionChanged(int x1, int y1, int x2, int y2, boolean isAdjusting) {
  */  	
    	
    private void fireSelectionChanged(Rectangle rect, boolean isAdjusting) {
    	logger.info("fire Selection changed + rect" + rect);
    	 Object[] listeners = listenerList.getListenerList();
    	 AlignmentSelectionEvent e = null;
    	 
         for (int i = listeners.length - 2; i >= 0; i -= 2) {
             if (listeners[i] == AlignmentSelectionListener.class) {
                 if (e == null) {
                     e = new AlignmentSelectionEvent(this, rect, isAdjusting);
                 }
                 ((AlignmentSelectionListener)listeners[i+1]).selectionChanged(e);
             }
         }
         
         // Also let ListView know
        // delegateLSM.fireValueChanged(rect.y, rect.y + rect.height, isAdjusting);
         
         
         logger.info("Time from last endTim " + (System.currentTimeMillis() - AliView.getActiveWindow().getLastPaneEndTime()) + " milliseconds");
		
	}

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s
     * are registered using the <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a <code>DefaultListSelectionModel</code>
     * instance <code>m</code>
     * for its list selection listeners
     * with the following code:
     *
     * <pre>ListSelectionListener[] lsls = (ListSelectionListener[])(m.getListeners(ListSelectionListener.class));</pre>
     *
     * If no such listeners exist,
     * this method returns an empty array.
     *
     * @param listenerType  the type of listeners requested;
     *          this parameter should specify an interface
     *          that descends from <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s
     *          on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     *          specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getListSelectionListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

	public void setTempSelection(Rectangle newSelectRect){
		if(tempSelectionRect != null && tempSelectionRect.equals(newSelectRect)){
			// old selection is the same - do nothing
		}
		
		if(tempSelectionRect == null){
			tempSelectionRect = newSelectRect;
			tempSelectionMaxRect = tempSelectionRect;
		}
		
		else{
			tempSelectionRect = newSelectRect;
			tempSelectionMaxRect.add(tempSelectionRect);
			fireSelectionChanged(tempSelectionMaxRect, false);
		}
		
	}

	public Rectangle getTempSelection() {
		return tempSelectionRect;
	}
	
	public void clearTempSelection() {
		this.tempSelectionRect = null;
	}
	
	public boolean isSequenceAtLeastPartlyAffectedByTempSelection(int index){
		if(tempSelectionRect == null){
			return false;
		}
		if(index >= tempSelectionRect.getMinY() && index <= tempSelectionRect.getMaxY()){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean isSequenceAtLeastPartlySelected(int index) {
		if(! sequences.rangeCheck(0, index)){
			return false;
		}
		if(isSequenceAtLeastPartlyAffectedByTempSelection(index)){
			return true;
		}
		return sequences.get(index).hasSelection();
	}

	public void translateSelection(AlignmentMeta aliMeta) {
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(! seq.isAllSelected()){
					int[] selection = seq.getSelectedPositions();
					int[] translated = aliMeta.translatePositions(selection);
					seq.clearAllSelection();
					for(int pos: translated){
						seq.setSelectionAt(pos);
					}
				}
			}
		}
		fireSelectionChangedAll();
	}
	
	public void reTranslateSelection(AlignmentMeta aliMeta) {
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				if(! seq.isAllSelected()){
					int[] selection = seq.getSelectedPositions();
					int[] translated = aliMeta.reTranslatePositions(selection);
					seq.clearAllSelection();
					for(int pos: translated){
						seq.setSelectionAt(pos);
					}
				}
			}
		}
		fireSelectionChangedAll();
		
	}
}
