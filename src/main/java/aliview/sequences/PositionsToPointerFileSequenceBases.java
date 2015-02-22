package aliview.sequences;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;
import aliview.sequencelist.MemoryMappedSequencesFile;
import aliview.utils.ArrayUtilities;

public class PositionsToPointerFileSequenceBases extends FileSequenceBases{
	private static final Logger logger = Logger.getLogger(PositionsToPointerFileSequenceBases.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	PositionsToPointer positionsToPointer = new PositionsToPointer();
	
	public PositionsToPointerFileSequenceBases(MemoryMappedSequencesFile sequencesFile, long startPointer){
		super(sequencesFile, startPointer);
	}

	// TODO this is not creating a deep copy
	public PositionsToPointerFileSequenceBases getCopy(){
		PositionsToPointerFileSequenceBases copy  = new PositionsToPointerFileSequenceBases(sequencesFile, getStartPointer());
		copy.positionsToPointer = this.positionsToPointer.getCopy();
		return copy;
	}
	
	public void add(PositionToPointer posToPoint){
		positionsToPointer.add(posToPoint);
	}
	
	@Override
	public long getEndPointer() {
		return positionsToPointer.getMaxPointer(); 
	}

	@Override
	public byte get(int n) {
		long pos = positionsToPointer.getPointerFromPos(n);
		return (byte) sequencesFile.readInFile(pos);
	}
	
	@Override
	public int getLength(){
		int length = positionsToPointer.getMaxPosition() + 1; // +1 since that is length
		return length;
	}
	

}
