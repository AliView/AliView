package aliview.sequences;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class InMemoryBasicSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(FastFastaSequence.class);

	public InMemoryBasicSequence(String name, byte[] bases) {
		super(name, bases);
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getSimpleName(){
		String formatted = StringUtils.substringAfter(this.name, "-");
		formatted = StringUtils.substringAfter(formatted, "-");
		formatted = StringUtils.substringAfter(formatted, "-");
		if(formatted.length() == 0){
			formatted = this.name;
		}
		return formatted;
	}
	
	public String toString(){
		return getSimpleName();
	}

	public int compareTo(Sequence anotherSeq) {
		return this.getSimpleName().compareTo(anotherSeq.getSimpleName());
	}
}
