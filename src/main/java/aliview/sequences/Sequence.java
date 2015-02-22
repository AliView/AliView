package aliview.sequences;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import aliview.AminoAcid;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequencelist.Interval;


/*
 * 
 * TODO rename all Base into Residue (residues)
 * 
 */
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
	
	public void writeBasesBetween(int start, int end, Writer out) throws IOException;
	
	public String getBasesAsString(); // used when exporting
	
	public int getUngapedLength();
	
	public int getNonTranslatedLength();
	
	public AminoAcid getTranslatedAminoAcidAtNucleotidePos(int x);
	
	public byte[] getGapPaddedCodonInTranslatedPos(int pos);
	
	public AminoAcid getNoGapAminoAcidAtNucleotidePos(int target);
	
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
	
	public void clearBase(int n);
	
	public void insertGapLeftOfSelectedBase();
	
	public void rightPadSequenceWithGaps(int diffLen);

	public void leftPadSequenceWithGaps(int diffLen);

	public void insertGapRightOfSelectedBase();
	
	public void insertGapAt(int n);
	
	
	// Searchable
	public Interval find(Pattern pattern, int nextFindStartPos);
	
	public int find(byte find, int nextFindStartPos);

	
	// Selectable version
	
	public void setSelectionAt(int n);
	
	public void clearSelectionAt(int n);
	
	public void setSelection(int startPos, int endPos, boolean clearFirst);
	
	public boolean isBaseSelected(int position);
	
	public void clearAllSelection();

	public void selectAllBases();
	
	public boolean hasSelection();
	
	public long countSelectedPositions(int startIndex, int endIndex);
	
	public int[] getSelectedPositions();
	
	public byte[] getSelectedBasesAsByte();
	
	public String getSelectedBasesAsString();
	
	public int getFirstSelectedPosition();
	
	public int getLastSelectedPosition();
	
	// selectable movable
	//public void moveSelectedResiduesRightIfGapIsPresent();
	
	//public void moveSelectionRightIfGapIsPresent(int steps);
	
	public void moveSelectedResiduesRightIfGapOrEndIsPresent();

	public void moveSelectedResiduesLeftIfGapIsPresent();
	
	//public void moveSelectionLeftIfGapIsPresent(int steps);
	
	public boolean contains(char testChar);

	public boolean isGapRightOfSelection();

	public boolean isGapLeftOfSelection();
	
	public boolean isEndRightOfSelection();
	
	public boolean isGapOrEndRightOfSelection();

	public void deleteGapLeftOfSelection();
	
	public void deleteGapRightOfSelection();

	public void replaceSelectedBasesWithChar(char newChar);

	public void selectAllBasesUntilGap(int x);

	public void deleteAllGaps();

	public Sequence getCopy();
	
	public int getID();

	public int getPosOfSelectedIndex(int posInSeq);

	public boolean isAllSelected();

	public int countChar(char c);

	public int indexOf(char c);
	
	public int countChar(char targetChar, int startpos, int endpos);

	public void invertSelection();
	
	public void selectionExtendRight();
	
	public void selectionExtendLeft();

	public void setAlignmentModel(AlignmentListModel alignmentModel);
	
	public AlignmentListModel getAlignmentModel();

	

}
