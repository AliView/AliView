package utils.nexus;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import utils.RangeUtils;


public class Excludes{
	private static final Logger logger = Logger.getLogger(Excludes.class);

	private NexusBoolArray positions;
	
	public Excludes(int length) {
		positions = new NexusBoolArray(length);
	}
	
	private Excludes(NexusBoolArray positions) {
		this.positions = positions;
	}
	
	public boolean isExcluded(int position){
		if(position >= 0 && position < positions.getLength()){
			return positions.valueAt(position);
		}
		else{
			return false;
		}
	}

	public Excludes getCopy(){
		return new Excludes(positions.getCopy());
	}

	public void reverse() {
		positions.reverse();
		
	}

	public boolean isAnythingExcluded() {
		if(positions.containsValue(true)){
			return true;
		}else{
			return false;
		}
	}

	public void removeExcludedPositionsFromList(ArrayList<Integer> allPos0) {
		Iterator<Integer> iter = allPos0.iterator();
		while(iter.hasNext()){
			Integer pos = iter.next();
			if(isExcluded(pos.intValue())){
				iter.remove();
			}
		}	
	}
	
	public int countExcludedSites() {		
		int count = positions.countValue(true);
		return count;
	}
	
	public int getLength() {
		return positions.getLength();
	}

	public void addRange(NexusRange range) {
		positions.setTrueFromNexusRange(range);
	}

	public boolean[] getPositionsBooleanArray() {
		return positions.getBooleanArray();
	}
	
	public void setPositionsBooleanArray(boolean[] array) {
		positions = new NexusBoolArray(array);
	}

	public ArrayList<NexusRange> getExcludedAsNexusRanges() {
		ArrayList<IntRange> intRanges = RangeUtils.boolArrayToListOfTrueIntRanges(positions.getBooleanArray());
		ArrayList<NexusRange> nexusRanges = new ArrayList<NexusRange>();
		for(IntRange range: intRanges){
			nexusRanges.add(new NexusRange(range, 1));
		}
		return nexusRanges;
	}
	
	public void append(Excludes secondExcludes) {
		this.positions.append(secondExcludes.positions);
	}

	public void removePosition(int n) {
		this.positions.removePosition(n);
	}
}
