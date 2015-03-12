package utils.nexus;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;



public class Excludes extends Ranges{
	private static final Logger logger = Logger.getLogger(Excludes.class);
	
	public Excludes() {
		super();
	}
	
	public Excludes(Excludes excludes) {
		super(excludes);
	}

	public boolean isExcluded(int pos){
		return contains(pos);
	}

	public Excludes getCopy(){
		return new Excludes(this);
	}

	public void addNexusRange(NexusRange range){
		Range newRange = new Range(range.getMinimumInt() - 1, range.getMaximumInt() - 1, 0); // one less because internally we work with 0 as first pos
		addRange(newRange);
	}
	

}
