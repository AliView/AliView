package utils.nexus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

public class Ranges implements Iterable<Range> {
	private static final Logger logger = Logger.getLogger(Ranges.class);
	protected ArrayList<Range> backend;

	public Ranges() {
		this.backend = new ArrayList<Range>();
	}

	public Ranges(Ranges template) {
		this();
		for(Range templateRange: template){
			backend.add(templateRange.getCopy());
		}	
	}
	
	public Ranges getCopy() {
		return new Ranges(this);
	}

	public void addRange(int start, int stop) {
		int min = Math.min(start, stop);
		int max = Math.max(start, stop);
		addRange(new Range(min, max, 0));
	}
	
	public void clearRange(int start, int stop) {
		int min = Math.min(start, stop);
		int max = Math.max(start, stop);
		clearRange(new Range(min, max, 0));
	}
	
	public void addRange(Range additionalRange) {
		
		logger.info("add=" + additionalRange);
		
		// first remove completely containing ones
		ArrayList<Range> toDelete = new ArrayList<Range>();
		for(Range range: backend){
			if(additionalRange.containsRange(range)){
				toDelete.add(range);
			}
		}
		
		backend.removeAll(toDelete);
		
		// if it within merge if same frame, otherwise cut out
		ArrayList<Range> additionalParts = new ArrayList<Range>();
		for(Range range: backend){
			if(additionalRange.within(range)){
				// if same reading frame then skip additional
				if(additionalRange.startVal == range.getPosVal(additionalRange.start)){
					return;
				}
				else{
					Range restPart = range.cutOut(additionalRange);
					additionalParts.add(restPart);
				}
			}
		}
		
		backend.addAll(additionalParts);
		
		
		// now adjust the existing ones making new one fit in between
		// or merge together with overlapping existing if same reading frame
		ArrayList<Range> toRemove = new ArrayList<Range>();
		for(Range range: backend){
			if(additionalRange.intersects(range)){
				// if same reading frame then merge and remove the overlapping
				if(additionalRange.startVal == range.getPosVal(additionalRange.start)){
					logger.info("merge" + additionalRange);
					additionalRange.merge(range);
					toRemove.add(range);
				}
				// if not same reading frame then crop
				else{
					range.crop(additionalRange);
				}
			}
		}
		// remove overlapping and same frame
		backend.removeAll(toRemove);
		// and add the new one
		backend.add(additionalRange);
		
		// and remove 0-length ones
		removeZeroLengthOnes();
		
		// and sort it
		Collections.sort(backend);
		
		this.debug();
			
	}

	public void clearRange(Range rangeToRemove) {
		
		logger.info("remove=" + rangeToRemove);
		
		// first remove completely containing ones
		ArrayList<Range> toDelete = new ArrayList<Range>();
		for(Range range: backend){
			if(rangeToRemove.containsRange(range)){
				toDelete.add(range);
			}
		}
		
		backend.removeAll(toDelete);
		
		// if it within cut out
		ArrayList<Range> additionalParts = new ArrayList<Range>();
		for(Range range: backend){
			if(rangeToRemove.within(range)){
				Range restPart = range.cutOut(rangeToRemove);
				additionalParts.add(restPart);
			}
		}
		
		backend.addAll(additionalParts);
		
		// now adjust the existing ones making new one fit in between
		// or merge together with overlapping existing if same reading frame
		ArrayList<Range> toRemove = new ArrayList<Range>();
		for(Range range: backend){
			if(rangeToRemove.intersects(range)){
				range.crop(rangeToRemove);
			}
		}
		
		// and remove 0-length ones
		removeZeroLengthOnes();
		
		// and sort it
		Collections.sort(backend);
		
		this.debug();	
	}
	
	private void removeZeroLengthOnes() {
		ArrayList<Range> zeroLengthOnes = new ArrayList<Range>();
		for(Range range: backend){
			if(range.getLength() < 0){
				zeroLengthOnes.add(range);
			}
		}
		backend.removeAll(zeroLengthOnes);
	}
	
	
	public void debug() {
		int count = 0;
		for(Range range: backend){
			logger.info("range.toString() count =" + count + " " + range.toString());
			count ++;
		}
	}

	public Range getRange(int pos){
		
		for(int n = this.backend.size() - 1; n >= 0; n--){
			Range range = backend.get(n);
			if(range.contains(pos)){
				return range;
			}
		}
		return null;
	}
	
	public boolean contains(int pos) {
		for(Range range: backend){
			if(range.contains(pos)){
				return true;
			}
		}
		return false;
	}
	
	public boolean intersects(Ranges testRanges) {
		for(Range aRange: backend){
			for(Range testRange: testRanges){
				if(testRange.intersects(aRange)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean intersects(int minX, int maxX) {
		Ranges templateRanges = new Ranges();
		templateRanges.addRange(minX, maxX);
		return intersects(templateRanges);
	}

	public int size() {
		return backend.size();
	}

	public Iterator<Range> iterator() {
		  return backend.iterator();
	}

	public void reverse(int length) {
		if(length <=0){
			return;
		}
		ArrayList<Range> newBackend = new ArrayList<Range>();
		for(Range range: backend){
			int newStart = (length - 1) - range.start;
			int newEnd = (length - 1) - range.end;
			int newStartVal = range.getPosVal(range.end);
			CodonRange reverseRange = new CodonRange(newStart, newEnd, newStartVal);
			newBackend.add(reverseRange);
			// and sort it
			Collections.sort(newBackend);
		}
		
		// and set it
		logger.info("before debug 1");
		debug();
		backend = newBackend;
		logger.info("before debug 2");
		debug();
	}

	public int countPositions(){
		int length = 0;
		for(Range range: backend){
			length += range.getLength();
		}
		return length;
	}

	public int getMaximumEndPos() {
		int max = -1;
		for(Range range: backend){
			max = Math.max(max, range.end);
		}
		return max;
	}
	
	public int getMinimumStartPos() {
		if(backend == null || backend.size() == 0){
			return -1;
		}
		int min = Integer.MAX_VALUE;
		for(Range range: backend){
			min = Math.min(min, range.start);
		}
		return min;
	}

	public void deletePosition(int pos) {
		for(int n = backend.size() - 1; n >= 0; n--){
			Range range = backend.get(n);
			if(range.end >= pos){
				range.end = range.end - 1;
			}
			if(range.start > pos){ // if start is same then leave it
				range.start = range.start - 1;
			}
		}
		
		// and remove 0-length ones
		removeZeroLengthOnes();
	}
	
	public void insertPosition(int pos) {
		for(int n = backend.size() - 1; n >= 0; n--){
			Range range = backend.get(n);
			if(range.end >= pos){
				range.end = range.end + 1;
			}
			if(range.start > pos){ // if start is same then leave it
				range.start = range.start + 1;
			}
		}
	}
	
	public void set(int pos, boolean boolVal){
		if(boolVal == true){
			addRange(new Range(pos, pos, 0));
		}else{
			clearRange(new Range(pos, pos, 0));
		}
	}
	
	public boolean containsAnyPosition() {
		if(size() > 0){
			return true;
		}
		return false;
	}
	
	public ArrayList<NexusRange> getAsContinousNexusRanges(){
		ArrayList<NexusRange> nexusRanges = new ArrayList<NexusRange>();
		for(Range range: backend){
			logger.info("range=" + range);
			NexusRange continousRange=new NexusRange(range.start + 1, range.end + 1, range.step, range.startVal); // +1 because Nexus uses 1 as first index
			nexusRanges.add(continousRange);
			logger.info(continousRange.debug());
		}
		return nexusRanges;
	}
	
}
