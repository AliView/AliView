package aliview.sequences;





public class ConvertedJEBLSequence extends InMemoryBasicSequence {


	public ConvertedJEBLSequence(jebl.evolution.sequences.Sequence jeblSeq) {
		super(jeblSeq.getTaxon().toString(),jeblSeq.getString().getBytes());
		
	}

}
