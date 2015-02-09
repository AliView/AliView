package aliview.sequences;

public class EmptySelectionModel implements SequenceSelectionModel {

	public boolean isSelected(int n) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearAll() {
		// TODO Auto-generated method stub
	}

	public void selectAll() {
		// TODO Auto-generated method stub
	}
	
	public void invertSelection(int length){
	}

	public int getFirstSelectedPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLastSelectedPosition(int seqLength) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSelectionAt(int i) {
		// TODO Auto-generated method stub
		
	}

	public void clearSelectionAt(int i) {
		// TODO Auto-generated method stub
		
	}

	public int[] getSelectedPositions(int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countSelectedPositions(int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSelection(int startIndex, int endIndex, boolean clearFirst) {
		// TODO Auto-generated method stub

	}
	
	public void clearSelection(int startIndex, int endIndex, boolean clearFirst) {
		// TODO Auto-generated method stub

	}

	public void insertNewPosAtAndMoveRight(int n) {
		// TODO Auto-generated method stub

	}

	public void insertNewPosAtAndMoveLeft(int n) {
		// TODO Auto-generated method stub

	}

	public void removePosition(int index) {
		// TODO Auto-generated method stub
		
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void rightPad(int length) {
		// TODO Auto-generated method stub
		
	}

	public void leftPad(int length) {
		// TODO Auto-generated method stub
		
	}

	public void insertNewPosAt(int n) {
		// TODO Auto-generated method stub
		
	}

	public int countPositionsUntilSelectedCount(int posInSeq) {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean isAllSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
