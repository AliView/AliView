package aliview.sequences;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



public class FastFastaSequence extends InMemorySequence{
	private static final Logger logger = Logger.getLogger(FastFastaSequence.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	
	public FastFastaSequence(String name, byte[] bases){
		super(name, bases);
	}
	
	public FastFastaSequence(String name, String basesAsString){
		super(name, basesAsString);
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

	public int compareTo(Sequence anotherSeq) {
		return this.getSimpleName().compareTo(anotherSeq.getSimpleName());
	}
}
