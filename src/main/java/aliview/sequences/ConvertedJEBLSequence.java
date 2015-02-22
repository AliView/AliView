package aliview.sequences;





public class ConvertedJEBLSequence extends InMemorySequence {


	public ConvertedJEBLSequence(jebl.evolution.sequences.Sequence jeblSeq) {
		super(jeblSeq.getTaxon().toString(),jeblSeq.getString().getBytes());
		
	}

}
