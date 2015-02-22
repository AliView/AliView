package aliview.sequences;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.utils.ArrayUtilities;


public class InMemorySequence extends BasicSequence{
	private static final Logger logger = Logger.getLogger(FastFastaSequence.class);

	public InMemorySequence(String name, String basesAsString) {
		this(name, basesAsString.getBytes());
	}

	public InMemorySequence(String name, byte[] bytes) {
		super();
		// replace all . with -
		if(bytes != null){
			ArrayUtilities.replaceAll(bytes, (byte) '.', (byte) '-');
		}

		this.bases = new DefaultBases(bytes);
		this.name = name;
		
	}
	
	public void setBases(byte[] bytes) {
		logger.info("setnewbases");
		this.bases = new DefaultBases(bytes);
		translatedBases = null;
		if(selectionModel == null){
			createNewSelectionModel();
		}
	}

	/*
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
	*/
	
}
