package aliview.sequencelist;

import java.util.Comparator;

import aliview.NucleotideUtilities;
import aliview.alignment.AliHistogram;
import aliview.sequences.Sequence;

public class SequencePositionComparator implements Comparator<Sequence> {
	
	private int sortPosition;
	private AliHistogram histogram;

	public SequencePositionComparator(int position, AliHistogram histogram) {
		this.sortPosition = position;
		this.histogram = histogram;
	}
	
	 public int compare(Sequence seq1, Sequence seq2) {
		 byte seq1Byte = seq1.getBaseAtPos(sortPosition);
		 byte seq2Byte = seq2.getBaseAtPos(sortPosition);
		 
		 int byte1Count = histogram.getValueCount(sortPosition, NucleotideUtilities.baseValFromBase(seq1Byte));
		 int byte2Count = histogram.getValueCount(sortPosition, NucleotideUtilities.baseValFromBase(seq2Byte));
		 
		 if(byte1Count < byte2Count){
			 return -1;
		 }
		 else if(byte1Count > byte2Count){
			 return 1;
		 }
		 // byte1Count == byte2Count
		 else{
			 return seq1Byte - seq2Byte;
		 } 
	    
	 }
}
