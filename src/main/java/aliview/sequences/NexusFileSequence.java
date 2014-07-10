package aliview.sequences;

import aliview.sequencelist.FileMMSequenceList;

public class NexusFileSequence extends PositionsToPointerFileSequence {

	public NexusFileSequence(FileMMSequenceList fileSeqList, int seqIndex, long startPointer) {
		super(fileSeqList, seqIndex, startPointer);
	}	
}
