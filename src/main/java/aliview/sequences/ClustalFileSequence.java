package aliview.sequences;

import org.apache.log4j.Logger;

import aliview.importer.ClustalFileIndexer;
import aliview.sequencelist.FileMMSequenceList;

public class ClustalFileSequence extends PositionsToPointerFileSequence {

	public ClustalFileSequence(FileMMSequenceList fileSeqList, int seqIndex, long startPointer) {
		super(fileSeqList, seqIndex, startPointer);
	}	
}
