package utils.nexus;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class CharSets implements Iterable<CharSet>{
	
	ArrayList<CharSet> backend = new ArrayList<CharSet>();
	
	public CharSets() {
	}
	
	public CharSets(CharSets template){
		this();
		for(CharSet aCharset: template){
			backend.add(aCharset.getCopy());
		}
	}
	
	public Iterator<CharSet> iterator() {
		  return backend.iterator();
	}

	public int size() {
		return backend.size();
	}

	public CharSets getCopy() {
		return new CharSets(this);
	}

	public void add(CharSet charSet) {
		backend.add(charSet);
		Collections.sort(backend);
	}

	public int getMaxOverlapCount() {
		int maxOverlapCount = 0;
		for(CharSet aSet: backend){
			int overlapCount = 0;
			for(CharSet innerSet: backend){
				// dont count intersecting itself
				if(innerSet != aSet){
					if(innerSet.intersects(aSet)){
						overlapCount ++;
					}
				}
			}
			maxOverlapCount = Math.max(maxOverlapCount, overlapCount);
		}
		return maxOverlapCount;
	}
	
	public void deletePosition(int n) {
		for(CharSet charset: backend){
			charset.deletePosition(n);
		}	
		Collections.sort(backend);
	}
	
	public void insertPosition(int n) {
		for(CharSet charset: backend){
			charset.insertPosition(n);
		}
		Collections.sort(backend);
	}

	public ArrayList<CharSet> getIntersected(Rectangle selection) {
		ArrayList<CharSet> intersects = new ArrayList<CharSet>();
		if(selection == null){
			return intersects;
		}
		for(CharSet aSet: backend){
			if(aSet.intersects((int)selection.getMinX(), (int)selection.getMaxX())){
				intersects.add(aSet);
			}
		}
		return intersects;
	}

	public void debug() {
		for(CharSet aSet: backend){
			aSet.debug();
		}
		
	}
}
