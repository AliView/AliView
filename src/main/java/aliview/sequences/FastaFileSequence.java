package aliview.sequences;

import aliview.sequencelist.FileMMSequenceList;

public class FastaFileSequence extends FileSequence {
	private int residuesPerLine;
	private int charsPerLine;
	
	public FastaFileSequence(FileMMSequenceList fileSeqList, int seqIndex,long startPointer){
		super(fileSeqList, seqIndex, startPointer);
	}
	
	public String getName() {
		return name.substring(1);
	}

	public int getResiduesPerLine() {
		return residuesPerLine;
	}

	public void setResiduesPerLine(int residuesPerLine) {
		this.residuesPerLine = residuesPerLine;
	}

	public int getCharsPerLine() {
		return charsPerLine;
	}

	public void setCharsPerLine(int charsPerLine) {
		this.charsPerLine = charsPerLine;
	}

	
	
	
}
