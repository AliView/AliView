package utils.nexus;

import java.util.ArrayList;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import utils.RangeUtils;

public class CharSet {
	private static final Logger logger = Logger.getLogger(CharSet.class);
	private String name;
	private NexusBoolArray positions;


	public CharSet(String name, int length) {
		this.name = name;
		this.positions = new NexusBoolArray(length);
	}
	
	private CharSet(String name, NexusBoolArray positions) {
		this.name = name;
		this.positions = positions;
	}

	public void addRange(NexusRange range){
		positions.setTrueFromNexusRange(range);
	}

	public void debug() {
			logger.info("name=" + name);
			positions.debug();
	}

	public String getName() {
		return name;
	}
	
	public boolean isPositionIncluded(int index){
		return positions.valueAt(index);
	}
	
	public void removePosition(int index) {
		positions.removePosition(index);
	}


	public void addRanges(ArrayList<NexusRange> allRanges) {
		for(NexusRange range: allRanges){
			addRange(range);
		}
	}
	
	public ArrayList<NexusRange> getCharSetAsNexusRanges() {
		ArrayList<IntRange> intRanges = RangeUtils.boolArrayToListOfTrueIntRanges(positions.getBooleanArray());
		ArrayList<NexusRange> nexusRanges = new ArrayList<NexusRange>();
		for(IntRange range: intRanges){
			nexusRanges.add(new NexusRange(range, 1));
		}
		return nexusRanges;
	}

	public boolean isTrueValContinous() {
		return positions.isTrueValContinous();
	}

	public CharSet getCopy() {
		return new CharSet(this.name, this.positions.getCopy());
	}
}
