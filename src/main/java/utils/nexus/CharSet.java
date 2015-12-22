package utils.nexus;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class CharSet extends Ranges implements Comparable<CharSet>{
	private static final Logger logger = Logger.getLogger(CharSet.class);
	private String name;


	public CharSet(String name) {
		super();
		this.name = name;
	}

	public CharSet(CharSet template) {
		this(template.name);
	}
	
	public CharSet getCopy() {
		return new CharSet(this);
	}
	
	public void addNexusRanges(ArrayList<NexusRange> allRanges) {
		for(NexusRange nexRange: allRanges){
			addNexusRange(nexRange);
		}
	}
	
	public void addNexusRange(NexusRange range){
		Range newRange = new Range(range.getMinimumInt() - 1, range.getMaximumInt() - 1, 0, range.steps); // one less because internally we work with 0 as first pos
		logger.info("newRange=" + newRange);
		addRange(newRange);
		debug();
	}

	public String getName() {
		return name;
	}
	
	public void debug() {
		super.debug();
		logger.info("name=" + name);
	}

	public int compareTo(CharSet other) {
		return this.getMinimumStartPos() - other.getMinimumStartPos();
	}
}
