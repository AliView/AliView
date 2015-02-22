package aliview.sequences;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



public class FastFastaSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(FastFastaSequence.class);
	
	public FastFastaSequence(String name, byte[] bases){
		super(name, bases);
	}
	
	public FastFastaSequence(String name, String basesAsString){
		super(name, basesAsString);
	}

}
