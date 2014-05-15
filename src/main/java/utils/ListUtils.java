package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtils {
	
	
	public static <T> List getToStringDupes(Collection<T> list){
		final List<T> dupes = new ArrayList<T>();
		Set<String> set = new HashSet<String>();
		
		for (T t : list) {
			if( set.add(t.toString()) == false){
				dupes.add(t);
		    }
		}
		
		return dupes;
	}
	
	public static <T>boolean containsToStringEquals(Collection<T> list, Object elem){
		
		boolean doesContain = false;
		for (T t : list) {
			if(t.toString().equals(elem.toString())){
				doesContain = true;
			}
			
		}
		
		return doesContain;
	}
	

}
