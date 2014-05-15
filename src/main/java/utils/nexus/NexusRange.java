package utils.nexus;

import org.apache.commons.lang.math.IntRange;

public class NexusRange{
	
	IntRange range;
	int steps;
	
	public NexusRange(IntRange range, int steps) {
		this.range = range;
		this.steps = 1; // default every one
	}
	
	public NexusRange(int min, int max) {
		range = new IntRange(min,max);
		this.steps = 1; // default every one
	}
	
	public NexusRange(int min, int max, int steps) {
		range = new IntRange(min,max);
		this.steps = steps;
	}
	
	@Override
	public String toString() {
		return range.toString() + "\\" + steps;
	}

	public int getMinimumInteger() {
		return range.getMinimumInteger();
	}
	
	public int getMaximumInteger() {
		return range.getMaximumInteger();
	}

	public int getSteps() {
		return steps;
	}
}
