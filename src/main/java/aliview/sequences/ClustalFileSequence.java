package aliview.sequences;

import org.apache.log4j.Logger;

import aliview.importer.ClustalFileIndexer;
import aliview.sequencelist.MemoryMappedSequencesFile;

public class ClustalFileSequence extends PositionsToPointerFileSequence {

	public ClustalFileSequence(MemoryMappedSequencesFile sequencesFile, long startPointer) {
		super(sequencesFile, startPointer);
	}	
}
