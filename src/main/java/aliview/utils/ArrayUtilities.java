package aliview.utils;

public class ArrayUtilities {
	
	public static final int count(boolean[] array, boolean target){
		if(array == null){
			return 0;
		}
		int count = 0;
		for(boolean val: array){
			if(val == target){
				count ++;
			}
		}
		return count;
	}

}
