package aliview.sequencelist;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import aliview.sequences.FileSequence;


public class FindObject {
	private static final Logger logger = Logger.getLogger(FindObject.class);
	String searchTerm;
	int nextFindSeqNumber;
	int nextFindStartPos;
	private int position;
	private int seqIndex;
	private boolean isFound;
	private List<Integer> foundIndices = new ArrayList<Integer>();
	private boolean findAll;
	private Point foundPos;
	private boolean findInNames;

	public FindObject(String searchTerm) {
		this(searchTerm, false);
	}

	public FindObject(String searchTerm, boolean findAll) {
		this.searchTerm = searchTerm;
		this.findAll = findAll;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public int getNextFindSeqNumber() {
		return nextFindSeqNumber;
	}

	public void setNextFindSeqNumber(int nextFindSeqNumber) {
		this.nextFindSeqNumber = nextFindSeqNumber;
	}

	public void setFoundPos(int position, int sequenceIndex) {
		this.foundPos = new Point(position, sequenceIndex);
	}

	public Point getFoundPos() {
		return foundPos;
	}

	public int getNextFindStartPos() {
		return nextFindStartPos;
	}

	public void setNextFindStartPos(int pos) {
		this.nextFindStartPos = pos;
	}

	public void setIsFound(boolean isFound) {
		this.isFound = isFound;

	}

	public boolean isFound() {
		return isFound;
	}

	public String getRegexSearchTerm() {
		String regex = "";
		for(int n = 0; n < searchTerm.length(); n++){
			regex += searchTerm.charAt(n);
			// add one or many gap between each character to be found
			regex +="\\-*"; 
		}
		return regex;
	}

	public List<Integer> getFoundIndices() {
		return this.foundIndices;
	}

	public void setFindAll(boolean b) {
		this.findAll = b;

	}

	public boolean isFindAll() {
		return this.findAll;
	}

	public void addFoundNameIndex(int n) {
		foundIndices.add(n);
	}

	public void setFoundNameIndex(int n) {
		foundIndices.clear();
		foundIndices.add(n);
	}

	public int getNextNameFindIndex() {
		logger.info(foundIndices.size());
		if(foundIndices.isEmpty()){
			return 0;
		}else{
			// last found
			int lastFound = foundIndices.get(foundIndices.size() - 1).intValue();
			return  lastFound + 1;
		}

	}

	public void clearIndices() {
		foundIndices.clear();

	}

	public boolean findNextInNames(){
		return findInNames;
	}

	public boolean findNextInSequences() {
		return !findInNames;
	}

	public void setFindNextInNames(boolean b) {
		this.findInNames = b;
	}

	public void setFindNextInSequences(boolean b) {
		this.findInNames = !b;
	}


}
