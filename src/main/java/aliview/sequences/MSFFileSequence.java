package aliview.sequences;

import org.apache.log4j.Logger;

import aliview.importer.ClustalFileIndexer;
import aliview.sequencelist.FileMMSequenceList;

public class MSFFileSequence extends PositionsToPointerFileSequence {

	public MSFFileSequence(FileMMSequenceList fileSeqList, int seqIndex, long startPointer) {
		super(fileSeqList, seqIndex, startPointer);
	}	
}
