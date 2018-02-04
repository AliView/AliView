package aliview.sequences;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FastFastQSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(FastFastQSequence.class);

	public FastFastQSequence(String name, byte[] bases){
		super(name, bases);
	}

	public FastFastQSequence(String name, String basesAsString){
		super(name, basesAsString);
	}
}
