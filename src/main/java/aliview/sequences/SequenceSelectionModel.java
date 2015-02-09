package aliview.sequences;

public interface SequenceSelectionModel extends Cloneable {

	public abstract boolean isSelected(int n);

	public abstract void clearAll();

	public abstract void selectAll();

	public abstract int getFirstSelectedPosition();

	public abstract int getLastSelectedPosition(int i);

	public abstract boolean hasSelection();

	public abstract void setSelectionAt(int i);
	
	public abstract void clearSelectionAt(int i);

	public abstract int[] getSelectedPositions(int startIndex, int endIndex);

	public abstract int countSelectedPositions(int startIndex, int endIndex);

	public abstract void setSelection(int startIndex, int endIndex, boolean clearFirst);
	
	public abstract void clearSelection(int startIndex, int endIndex, boolean clearFirst);
	
	/*
	 * 
	 * TODO these two methods could be one and instead tabe care of selection movement
	 * 
	 */
	public abstract void insertNewPosAt(int n);

	public abstract void removePosition(int index);

	public abstract void rightPad(int length);

	public abstract void leftPad(int length);

	public abstract int countPositionsUntilSelectedCount(int posInSeq);

	public abstract boolean isAllSelected();

	public abstract void invertSelection(int length);

}