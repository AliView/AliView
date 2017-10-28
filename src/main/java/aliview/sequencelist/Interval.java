package aliview.sequencelist;

public class Interval {

	int startPos;
	int endPos;

	public Interval(int startPos, int endPos) {
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public int getStartPos() {
		return startPos;
	}

}
