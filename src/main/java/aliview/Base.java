package aliview;

import aliview.sequences.Sequence;

public class Base {
	private Sequence sequence;
	private int position;

	public Base(Sequence sequence, int position) {
		this.sequence = sequence;
		this.position = position;
	}

	public Sequence getSequence() {
		return sequence;
	}
	public int getPosition() {
		return position;
	}

	public int getUngapedPosition() {
		return sequence.getUngapedPos(this.position);
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return sequence.isBaseSelected(position);
	}

}
