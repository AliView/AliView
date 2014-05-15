package aliview.alignment;

public interface AlignmentListener {
	
	public void selectionChanged(Alignment source);

	public void sequencesChanged(AlignmentEvent alignmentEvent);

	public void newSequences(AlignmentEvent alignmentEvent);

	public void sequenceOrderChanged(AlignmentEvent alignmentEvent);

	public void alignmentMetaChanged(AlignmentEvent alignmentEvent);

	public void sequencesRemoved(AlignmentEvent alignmentEvent);
	
}
