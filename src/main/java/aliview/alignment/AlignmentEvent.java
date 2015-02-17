
package aliview.alignment;

public class AlignmentEvent extends Alignment{

	private Alignment source;

	public AlignmentEvent(Alignment alignment) {
		this.source = alignment;
	}
	
	public Alignment getSource(){
		return this.source;
	}

}
