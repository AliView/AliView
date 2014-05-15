package aliview.sequences;

import java.awt.Point;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;



public class PhylipSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(PhylipSequence.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	
	public PhylipSequence(String name, byte[] bases) {
		super(name, bases);
	}
	
	public PhylipSequence(String name, String basesAsString) {
		super(name, basesAsString);
	}

	public String getName(){
		return this.name;
	}
	
	public String getSimpleName(){
		return getName();
	}

	public int compareTo(Sequence anotherSeq) {
		return this.getSimpleName().compareTo(anotherSeq.getSimpleName());
	}

}
