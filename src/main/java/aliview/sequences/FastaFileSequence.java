package aliview.sequences;

import aliview.sequencelist.FileMMSequenceList;

public class FastaFileSequence extends FileSequence {
	
	public FastaFileSequence(FileMMSequenceList fileSeqList, int seqIndex,long startPointer){
		super(fileSeqList, seqIndex, startPointer);
	}
	
	public String getName() {
		return name.substring(1);
	}
}
