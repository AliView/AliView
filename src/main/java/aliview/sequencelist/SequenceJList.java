package aliview.sequencelist;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import utils.OSNativeUtils;
import aliview.UndoControler;
import aliview.gui.AlignmentPane_Orig;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceSelectionModel;
import aliview.settings.Settings;
import aliview.undo.UndoSavedStateSequenceOrder;


public class SequenceJList extends javax.swing.JList{
	private static final Logger logger = Logger.getLogger(SequenceJList.class);
	// todo These two constants should be synchronized in one class (AlignmentPane & this)
	private static final int MIN_CHAR_SIZE = 2;
	private static final int MAX_CHAR_SIZE = 100;
	private double charHeight;
	private ListCellRenderer storedCellRenderer;
	
	
	public SequenceJList(SequenceListModel model, double charHeight) {
		super(model);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setTransferHandler(new SequenceTransferHandler());
		this.setDropMode(DropMode.INSERT);
		this.setCharSize(charHeight);
		
		// int width = model.getLongestSequenceName();
		// int fixedWidth = 300;
		
		this.setCellRenderer(new FasterTextCellRenderer()); 	
		
		this.setDragEnabled(true);
		
		this.setBorder(new EmptyBorder(0,0,0,0));

		// Remove default ctrl-C action (because it only copys names in list and not sequence)
		InputMap map = this.getInputMap();

		// remove from input map is not working so I am replacing one with nothing
		map.put(OSNativeUtils.getPasteKeyAccelerator(),"null");
		map.put(OSNativeUtils.getCopyKeyAccelerator(),"null");
		map.put(OSNativeUtils.getCopySelectionAsFastaKeyAccelerator(),"null");
		map.put(OSNativeUtils.getMoveSelectionUpKeyAccelerator(),"null");
		map.put(OSNativeUtils.getMoveSelectionDownKeyAccelerator(),"null");
		map.put(OSNativeUtils.getIncreaseFontSizeKeyAccelerator(),"null");
		map.put(OSNativeUtils.getDecreaseFontSizeKeyAccelerator(),"null");
		
	}
	
	@Override
	public SequenceListModel getModel() {
		// TODO Auto-generated method stub
		return (SequenceListModel) super.getModel();
	}

	
	public void paintComponent(Graphics g){
		long startTime = System.currentTimeMillis();
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g);
		long endTime = System.currentTimeMillis();
		logger.info("Draw JList took " + (endTime - startTime) + " milliseconds");	
	}
	
	@Override
	public void validate() {
		long startTime = System.currentTimeMillis();
		super.validate();
		long endTime = System.currentTimeMillis();
		logger.info("Validate JList took " + (endTime - startTime) + " milliseconds");	
	}
	
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		//logger.info("super.getPreferredScrollableViewportSize();" + super.getPreferredScrollableViewportSize());
		return super.getPreferredScrollableViewportSize();
	}

	public void setCharSize(double charHeight) {
	    this.charHeight = charHeight;
	    
	    // And now check font size
		float listFontSize = (int)(charHeight -1);
		if(listFontSize > 13 && !Settings.getUseCustomFontSize().getBooleanValue()){	
			listFontSize = 13;
		}	
		updateCharSize(listFontSize);
	}
	
	@Override
	public void revalidate(){
		super.revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		//logger.info(super.getPreferredSize());
		return super.getPreferredSize();
	}
	
	private void updateCharSize(float listFontSize) {

		
		// Fixed cell height is needed or otherwise all items are loaded
		this.setFixedCellHeight((int)charHeight);
		this.setFixedCellWidth(this.getModel().getLongestSequenceName()*(int)(charHeight));

		this.setFont(this.getFont().deriveFont(listFontSize));
		
		//logger.info(getFont().getName());
	    //logger.info("fontSize" + this.getFont().getSize());
		
		// Remove List cell renderer att small sizes (saves a lot of drawing speed)
				if(charHeight < 3 && this.getCellRenderer() != null){
					this.storedCellRenderer = this.getCellRenderer();
					this.setCellRenderer(null);
				}else if(charHeight >= 3 && this.getCellRenderer() == null){
					this.setCellRenderer(this.storedCellRenderer);
				}

	}

	public void deleteSelectedSequences() {
		Sequence[] selection = this.getSelectedValues();
		if(! isSelectionValid(selection)){
			return;
		}
		for(Sequence seq: selection){
			 this.getModel().deleteSequence(seq);
		}	
	}
	
	private boolean isSelectionValid(Object[] array) {
		if(array != null && array.length > 0){
			return true;
		}
		else{
			return false;
		}
	}

	public void reverseComplementSelectedSequences() {
		Sequence[] selection = this.getSelectedValues();
		this.getModel().reverseComplement(selection);
	}
	
	/*
	 * 
	 * TODO this should be changed into "Sequences-class" that includes JListModel
	 * 
	 */
	public void moveSelectionDown() {
		Sequence[] selection = this.getSelectedValues();
		this.getModel().moveSequencesDown(selection);
		this.setSelected(selection);
	}
	
	public void moveSelectionUp() {
		Sequence[] selection = this.getSelectedValues();
		this.getModel().moveSequencesUp(selection);
		this.setSelected(selection);
	}
	
	public void moveSelectedSequencesTo(int toIndex){
		Sequence[] selection = this.getSelectedValues();
		if(! isSelectionValid(selection)){
			return;
		}
		// Since move is between list in same list the input index has to be lowered because the
		// target sequence is one of the lower index sequences
		if(this.getSelectedIndex() < toIndex){
			if(toIndex > 0){
				toIndex --;
			}
		}
		this.getModel().moveSequencesTo(toIndex, selection);
		this.setSelected(selection);
	}


	public void moveSelectionToTop() {
		Sequence[] selection = this.getSelectedValues();
		this.getModel().moveSequencesToTop(selection);
		this.setSelected(selection);
	}
	
	private void setSelected(Sequence[] selection) {
		int[] indicies = new int[selection.length];
		int n = 0;
		for(Sequence seq: selection){
			indicies[n] = this.getModel().indexOf(seq);
			n++;
		}
		this.setValueIsAdjusting(true);
		this.setSelectedIndices(indicies);	
		this.setValueIsAdjusting(false);
	}
	
	public void setSelectedIndices(List<Integer> indices) {
		int[] indexArray = new int[indices.size()];
		for(int n = 0; n < indices.size(); n++){
			indexArray[n] = indices.get(n).intValue();
		}
		this.setValueIsAdjusting(true);
		this.setSelectedIndices(indexArray);
		this.setValueIsAdjusting(false);
	}

	public void moveSelectionToBottom() {
		Sequence[] selection = this.getSelectedValues();
		this.getModel().moveSequencesToBottom(selection);
		this.setSelected(selection);
	}
	
	public boolean hasSelection() {
		return ! getSelectionModel().isSelectionEmpty();
	}
	
	public void invertSelection(){
		List<Integer> newSelection = new ArrayList<Integer>();
		
		for(int n = 0; n < this.getModel().getSize(); n++){
			if(! this.isSelectedIndex(n)){
				newSelection.add(new Integer(n));
			}
		}
		setSelectedIndices(newSelection);
	}
	

	
	@Override
	public Sequence[] getSelectedValues() {
		Object[] vals = super.getSelectedValues();
		return convertArray(vals);
	}
	
	private Sequence[] convertArray(Object[] vals){
		Sequence[] seqs = null;
		if(vals != null){
			seqs = new Sequence[vals.length];
			for(int n = 0; n <vals.length; n++){
				if(vals[n] instanceof Sequence){
					seqs[n] = (Sequence) vals[n];
				}
			}
		}
		return seqs;
	}

	public void validateSelection() {
		ArrayList<Integer> indices = this.getModel().getIndicesOfSequencesWithAllSelected();
		setSelectedIndices(indices);
	}

}
