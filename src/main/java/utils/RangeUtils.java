package utils;

import java.util.ArrayList;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

public class RangeUtils {
	private static final Logger logger = Logger.getLogger(RangeUtils.class);
	
public static ArrayList<IntRange> sortIntRangeList(ArrayList<IntRange> inlist){
		
		for(int counter=0; counter < inlist.size() -1; counter++) { //Loop once for each element in the array.
            for(int index=0; index < inlist.size() -1 -counter; index++) { //Once for each element, minus the counter.
                if(inlist.get(index).getMinimumInteger() > inlist.get(index + 1).getMinimumInteger()) { //Test if need a swap or not.
                    IntRange temp = inlist.get(index); //These three lines just swap the two elements:
                    inlist.set(index, inlist.get(index + 1));
                    inlist.set(index+1, temp);
                }
            }
        }

		
		return inlist;		
	}

public static final ArrayList<IntRange> boolArrayToListOfTrueIntRanges(boolean[] array) {
	
	ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
	
	if(array == null || array.length == 0){
		return allRanges;
	}
	
	
	boolean lastVal = false;
	int startRangePos = 0;
	
	for(int n = 0; n < array.length; n++){
	
		boolean thisVal = array[n];
		
		// this pos is start of a range
		if(thisVal== true && lastVal == false){
			startRangePos = n;
		}
		
		// last pos was end of a range
		if(thisVal == false && lastVal == true){
			allRanges.add(new IntRange(startRangePos, n - 1)); // minus one because we are at next pos already
		}

		// if it is the last one and true then it is the last range
		if(n == array.length - 1 && thisVal == true){
			allRanges.add(new IntRange(startRangePos, array.length - 1));
		}
		
		lastVal = thisVal;
	}
	
	logger.info("allRanges.size()" + allRanges.size());
	
	return allRanges;
	
}

public static final ArrayList<IntRange> boolArrayToListOfFalseIntRanges(boolean[] array) {
	
	ArrayList<IntRange> allRanges = new ArrayList<IntRange>();
	
	if(array == null || array.length == 0){
		return allRanges;
	}
	
	
	boolean lastVal = true;
	int startRange = 0;
	
	for(int n = 0; n < array.length; n++){
	
		boolean thisVal = array[n];
		
		if(thisVal != lastVal){
			// this was different and false so start new range
			if(thisVal == false){
				startRange = n;
			}
			// this was different and last was false so add range
			else{
				allRanges.add(new IntRange(startRange, n - 1));
			}
		}

		// if it is the last one and false
		if(n == array.length - 1 && thisVal == false){
			allRanges.add(new IntRange(startRange, n - 1));
		}
		
		lastVal = thisVal;
	}
	
	logger.info("allRanges.size()" + allRanges.size());
	
	return allRanges;
	
}

}
