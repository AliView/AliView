package aliview.sequencelist;

import java.awt.Point;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import aliview.AliView;
import aliview.sequences.Sequence;

public class SequenceListSelectionModel extends DefaultListSelectionModel {

	private static final Logger logger = Logger.getLogger(SequenceListSelectionModel.class);

	private AlignmentSelectionModel aliSelectionModel;

	public SequenceListSelectionModel(AlignmentSelectionModel aliSelectionModel) {
		this.aliSelectionModel = aliSelectionModel;
		super.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	/*
	@Override
	public void fireValueChanged(int firstIndex, int lastIndex) {
		// TODO Auto-generated method stub
		super.fireValueChanged(firstIndex, lastIndex);
		//super.fireValueChanged(0, aliSelectionModel.getSequenceListModel().size());
	}

	@Override
	public void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
		// TODO Auto-generated method stub
		logger.info("fireValueChanged");
		super.fireValueChanged(firstIndex, lastIndex, isAdjusting);
		//super.fireValueChanged(0, aliSelectionModel.getSequenceListModel().size(), isAdjusting);
	}
	 */

	//
	//
	// ListSelectionModel
	//
	//
	public boolean isSelectedIndex(int index){
		// Rangecheck?? - No
		//return delegateLSM.isSelectedIndex(index);
		//	logger.info("isSelectedIndex=" + index + " " + aliSelectionModel.isSequenceAtLeastPartlySelected(index));
		return aliSelectionModel.isSequenceAtLeastPartlySelected(index);
	}

	//
	// This is called from JList BasicUI Handler at mouse or key events on list
	//
	public void setSelectionInterval(int index0, int index1) {
		logger.info("setSelectionInterval index0=" + index0 + " index1=" + index1);
		super.setSelectionInterval(index0, index1);
		aliSelectionModel.setSequenceSelection(index0, index1);
		//fireValueChanged(index0, index1);
		//aliSelectionModel.fireSelectionChanged(index0, index1);
	}

	public void clearSelection() {
		logger.info("clear selection");
		//super.clearSelection();

	}

	//
	// This is called from JList BasicUI Handler at mouse or key events on list
	//
	public void addSelectionInterval(int index0, int index1) {

		super.addSelectionInterval(index0, index1);
		logger.info("addSelectionInterval ix0=" + index0 + " ix1=" + index1);
		logger.info("addSelectionInterval getValueIsAdjusting=" + getValueIsAdjusting());

		// only add if not already selected, this is to prevent partly selected sequence to
		// be fully selected when a drag event is initiated
		int minIndex = Math.min(index0, index1);
		int maxIndex = Math.max(index0, index1);

		boolean shouldChange = false;
		for(int n = minIndex; n <= maxIndex; n++){
			if(!aliSelectionModel.isSequenceAtLeastPartlySelected(n)){
				shouldChange = true;
			}
		}
		if(shouldChange){
			aliSelectionModel.addSequenceSelection(index0, index1); 
		}
	}

	//
	// This is called from JList BasicUI Handler at mouse or key events on list
	//
	public void removeSelectionInterval(int index0, int index1) {
		logger.info("removeSelectionInterval");
		super.removeSelectionInterval(index0, index1);
		aliSelectionModel.removeSequenceSelection(index0, index1);
		//aliSelectionModel.fireSelectionChanged();

	}

	//
	// This is called from JList BasicUI Handler at mouse or key events on list
	//
	public int getMinSelectionIndex() {
		logger.info("getMinSelectionIndex=" +  aliSelectionModel.getFirstSelectedSequenceIndex());
		int minPos = aliSelectionModel.getFirstSelectedSequenceIndex();

		return minPos;
		//return super.getMinSelectionIndex();
	}

	//
	// This is called from JList BasicUI Handler at mouse or key events on list
	//
	public int getMaxSelectionIndex() {
		logger.info("getMaxSelectionIndex" + aliSelectionModel.getLastSelectedSequenceIndex());
		int maxPos = aliSelectionModel.getLastSelectedSequenceIndex();
		return maxPos;
	}


	public int getAnchorSelectionIndex() {
		logger.info("getAnchorSelectionIndex" + super.getAnchorSelectionIndex());
		int anchorIndex = super.getAnchorSelectionIndex();
		if(anchorIndex == -1){
			anchorIndex = aliSelectionModel.getFirstSelectedSequenceIndex();
		}
		logger.info("anchorIndex" + anchorIndex);
		return anchorIndex;
	}

	public void setAnchorSelectionIndex(int anchorIndex) {
		logger.info("setAnchorSelectionIndex" + anchorIndex);
		super.setAnchorSelectionIndex(anchorIndex);
	}

	public int getLeadSelectionIndex() {
		logger.info("getLeadSelectionIndex" + super.getLeadSelectionIndex());
		return super.getLeadSelectionIndex();
	}

	public void setLeadSelectionIndex(int leadIndex) {
		logger.info("setLeadSelectionIndex");
		super.setLeadSelectionIndex(leadIndex);
	}

	public boolean isSelectionEmpty(){
		return super.isSelectionEmpty();
		//logger.warn("isSelectionEmpty method is being used - this might have performace issues");
		//return false; //!aliSelectionModel.hasSelection();
	}

	public void insertIndexInterval(int index, int length, boolean before) {
		// this is unwanted since selection is drawn from sequence selection and not internal array
		logger.info("insertIndexInterval index=" + index + " length" + length);
		//super.insertIndexInterval(index, length, before);	
	}

	public void removeIndexInterval(int index0, int index1) {
		// this is unwanted since selection is drawn from sequence selection and not internal array
		logger.info("removeIndexInterval");
		//super.removeIndexInterval(index0, index1);
	}

	public void setValueIsAdjusting(boolean isAdjusting) {
		logger.info("setValueIsAdjusting");
		super.setValueIsAdjusting(isAdjusting);
	}

	public boolean getValueIsAdjusting() {
		logger.info("getValueIsAdjusting");
		return super.getValueIsAdjusting();
	}

	public void setSelectionMode(int selectionMode) {
		super.setSelectionMode(selectionMode);
	}
	public int getSelectionMode() {
		return super.getSelectionMode();
	}

	public void addListSelectionListener(ListSelectionListener l) {
		super.addListSelectionListener(l);
	}

	public void removeListSelectionListener(ListSelectionListener l) {
		super.removeListSelectionListener(l);
	}

}
