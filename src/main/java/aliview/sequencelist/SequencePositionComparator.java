package aliview.sequencelist;

import java.util.Comparator;

import aliview.sequences.Sequence;

public class SequencePositionComparator implements Comparator<Sequence> {
	
	private int sortPosition;

	public SequencePositionComparator(int position) {
		this.sortPosition = position;
	}
	
	 public int compare(Sequence seq1, Sequence seq2) {
		 byte seq1Byte = seq1.getBaseAtPos(sortPosition);
		 byte seq2Byte = seq2.getBaseAtPos(sortPosition);
	     return seq1Byte - seq2Byte;
	 }
}
