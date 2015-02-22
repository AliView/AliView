package aliview.sequences;

import java.awt.Point;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;


public class ClustalSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(ClustalSequence.class);
	
	public ClustalSequence(String name, byte[] bases) {
		super(name, bases);
	}
	
	public ClustalSequence(String name, String basesAsString) {
		super(name, basesAsString);
	}

}
