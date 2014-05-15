package aliview.sequences;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Pattern;


public interface Sequence extends Comparable<Sequence>{
	
	public int getLength();
	
	public String getName();
	
	public void setName(String string);
	
	public String getSimpleName();

	// probably in the normal interface
	
	public byte getBaseAtPos(int n);
	
	public char getCharAtPos(int n);

	public byte[] getBasesBetween(int startIndexInclusive, int endIndexInclusive);
	
	//public void writeBasesAt(Writer out, int x, int i);
	
	//public int getBasesAt(int x, int i, byte[] bytesToDraw);
	
	public byte[] getAllBasesAsByteArray();
	
	public void writeBases(OutputStream out) throws IOException;
	
	public void writeBases(Writer out) throws IOException;
	
	public String getBasesAsString(); // used when exporting
	
	public int getUngapedLength();
	
	// Undecided which version
	public boolean isEmpty();
	
	public int[] getSequenceAsBaseVals();
	
	public int getUngapedPos(int position); // for view at
	
	public String getBasesAtThesePosAsString(ArrayList<Integer> allPositions);
	
	// Editable version (must also always implement selectable and seleclable moveable)
	
	public void reverseComplement() ;
	
	public void complement();
	
	public void replaceBases(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases);
	
	public void replaceSelectedBasesWithGap();
	
	public void deleteSelectedBases();
	
	public void deleteBasesFromMask(boolean[] deleteMask);
	
	public void setBases(byte[] bases);
	
	public void clearBase(int n);
	
	public void insertGapLeftOfSelectedBase();
	
	public void rightPadSequenceWithGaps(int diffLen);

	public void leftPadSequenceWithGaps(int diffLen);

	public void insertGapRightOfSelectedBase();
	
	
	// Searchable
	public int findAndSelect(Pattern pattern, int nextFindStartPos);
	
	// Selectable version
	
	public void setSelectionAt(int n, boolean selected); // used in Base
	
	public void selectBases(int startPos, int endPos);
	
	public boolean isBaseSelected(int position);
	
	public void clearAllSelection();

	public void selectAllBases();
	
	public boolean hasSelection();
	
	public long countSelectedPositions(int startIndex, int endIndex);
	
	public int[] getSelectedPositions();
	
	public byte[] getSelectedBasesAsByte();
	
	public String getSelectedBasesAsString();
	
	public int getFirstSelectedPosition();
	
	// selectable movable
	public void moveSelectionRightIfGapIsPresent();
	
	public void moveSelectionRightIfGapIsPresent(int steps);

	public void moveSelectionLeftIfGapIsPresent();
	
	public void moveSelectionLeftIfGapIsPresent(int steps);

	public boolean contains(char testChar);

	public boolean isGapRightOfSelection();

	public boolean isGapLeftOfSelection();

	public void deleteGapLeftOfSelection();

	public void replaceSelectedBasesWithChar(char newChar);

	public void selectAllBasesUntilGap(int x);

	public void deleteAllGaps();

	public Sequence getCopy();
	
	public int getID();

	public int getPosOfSelectedIndex(int posInSeq);

	public boolean isAllSelected();

}
