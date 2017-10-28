package aliview.sequences;

import aliview.sequencelist.MemoryMappedSequencesFile;

public class FastaFileSequence extends FileSequence {
	private int residuesPerLine;
	private int charsPerLine;

	public FastaFileSequence(MemoryMappedSequencesFile sequencesFile, long startPointer){
		super(sequencesFile, startPointer);
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
