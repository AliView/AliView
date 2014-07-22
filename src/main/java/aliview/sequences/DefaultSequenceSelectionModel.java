package aliview.sequences;

import java.util.BitSet;

import org.apache.log4j.Logger;

public class DefaultSequenceSelectionModel implements SequenceSelectionModel {
	private static final Logger logger = Logger.getLogger(DefaultSequenceSelectionModel.class);
	BitSet bitSelection;
	boolean allSelected;

	public boolean isBaseSelected(int n) {
		if(allSelected){
			return true;
		}
		if(bitSelection == null){
			return false;
		}else{
			return bitSelection.get(n);
		}
	}

	private BitSet createNewSelection() {
		allSelected = false;
		return new BitSet();
	}

	public void clearAll() {
		//logger.info("clearAll");
		bitSelection = null;
		allSelected = false;
	}

	public void selectAll() {
		clearAll();
		allSelected = true;
	}

	public int getFirstSelectedPosition() {
		if(allSelected){
			return 0;
		}
		if(bitSelection == null){
			return -1;
		}else{
			return bitSelection.nextSetBit(0);
		}
	}

	public int getLastSelectedPosition() {
		if(allSelected){
			return 0;
		}
		if(bitSelection == null){
			return -1;
		}else{
			// last bit in bitset is always set (autoshrink)
			return bitSelection.length() - 1;
		}
	}

	public boolean hasSelection() {
		if(allSelected){
			return true;
		}
		if(bitSelection == null){
			return false;
		}else{	
			return !bitSelection.isEmpty();
		}
	}

	public int[] getSelectedPositions(int startIndex, int endIndex) {
		if(allSelected){
			int[] pos = new int[endIndex - startIndex + 1];
			for(int n = 0; n<pos.length; n++){
				pos[n] = n;
			}
			return pos;
		}
		if(bitSelection == null){
			return new int[0];
		}
		int[] pos = new int[countSelectedPositions(startIndex, endIndex)];
		int index = 0;
		 for(int i = bitSelection.nextSetBit(startIndex); i >= 0 && i <= endIndex; i = bitSelection.nextSetBit(i+1)) {
			 pos[index] = i;
			 index++;
		 }
		
		return pos;	
	}
	
	public int countPositionsUntilSelectedCount(int selectedCount) {
		if(allSelected){
			return selectedCount-1;
		}
		if(bitSelection == null){
			logger.info("no bit selection");
			return -1;
		}else{
			int index = -1;
			int nCount = 0;
			 for (int i = bitSelection.nextSetBit(0); i >= 0 && i <= bitSelection.length(); i = bitSelection.nextSetBit(i+1)) {
//			    logger.info("i" + i);
				nCount ++;
			    if(nCount == selectedCount){
			    	index = i;
			    	break;
			    }
			 }
			return index;
		}	
	}


	public int countSelectedPositions(int startIndex, int endIndex) {
		if(allSelected){
			return endIndex - startIndex + 1;
		}
		if(bitSelection == null){
			return 0;
		}else{
			int nCount = 0;
			 for (int i = bitSelection.nextSetBit(startIndex); i >= 0 && i <= endIndex; i = bitSelection.nextSetBit(i+1)) {
			    nCount ++;
			 }
			return nCount;
		}	
	}
	
	public void setSelectionAt(int n, boolean selected){
		setSelection(n, n, selected);
	}

	public void setSelection(int startIndex, int endIndex, boolean isSelected){
		if(bitSelection == null){
			bitSelection = createNewSelection();
		}
		bitSelection.set(startIndex, endIndex + 1, isSelected); // bitselection end index is exclusive
		if(isSelected == false){
			allSelected = false;
		}
	}

	public void rightPad(int length) {
		// nothing need to be done	
	}
	
	public void leftPad(int length) {
		if(hasSelection() && allSelected == false){	
			for(int n = 0; n < length; n++){
				insertNewPosAt(0);
			}
		}
	}
	
	public void removePosition(int index) {
		if(hasSelection() && allSelected == false){	
			for(int n = index; n < bitSelection.length(); n++){
				bitSelection.set(n,bitSelection.get(n+1));
			}
		}
	}

	public void insertNewPosAt(int index) {
		if(hasSelection() && allSelected == false){	
			for(int n = bitSelection.length(); n >= index; n--){
				bitSelection.set(n + 1,bitSelection.get(n));
			}
			bitSelection.set(index,false);
		}
	}


	public boolean isAllSelected() {
		return allSelected;
	}

}
