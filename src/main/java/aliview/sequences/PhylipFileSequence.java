package aliview.sequences;

import org.apache.log4j.Logger;

import aliview.importer.ClustalFileIndexer;
import aliview.sequencelist.FileMMSequenceList;

public class PhylipFileSequence extends PositionsToPointerFileSequence {

	public PhylipFileSequence(FileMMSequenceList fileSeqList, int seqIndex, long startPointer) {
		super(fileSeqList, seqIndex, startPointer);
	}	
}
