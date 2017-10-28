package aliview.sequences;

import java.awt.Point;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;


public class PhylipSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(PhylipSequence.class);

	public PhylipSequence(String name, byte[] bases) {
		super(name, bases);
	}

	public PhylipSequence(String name, String basesAsString) {
		super(name, basesAsString);
	}

}
