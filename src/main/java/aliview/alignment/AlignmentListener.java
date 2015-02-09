package aliview.alignment;

public interface AlignmentListener {
	
	public void newSequences(AlignmentEvent alignmentEvent);

	public void alignmentMetaChanged(AlignmentEvent alignmentEvent);

	
}
