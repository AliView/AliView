package aliview.sequences;

public interface SequenceSelectionModel extends Cloneable {

	public abstract boolean isBaseSelected(int n);

	public abstract void clearAll();

	public abstract void selectAll();

	public abstract int getFirstSelectedPosition();

	public abstract int getLastSelectedPosition();

	public abstract boolean hasSelection();

	public abstract void setSelectionAt(int i, boolean isSelected);

	public abstract int[] getSelectedPositions(int startIndex, int endIndex);

	public abstract int countSelectedPositions(int startIndex, int endIndex);

	public abstract void setSelection(int startIndex, int endIndex,
			boolean isSelected);
	
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

}