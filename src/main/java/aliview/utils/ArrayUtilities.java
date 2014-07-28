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

	public static byte[] replaceAll(byte[] byteArray, char find, byte replace) {
		if(byteArray == null){
			return null;
		}
		
		for(int n = 0; n < byteArray.length; n++){
			if(byteArray[n] == find){
				byteArray[n] = replace;
			}
		}
		return byteArray;
	}
	
	public static byte[] replaceAll(byte[] byteArray, byte find, byte replace) {
		if(byteArray == null){
			return null;
		}
		
		for(int n = 0; n < byteArray.length; n++){
			if(byteArray[n] == find){
				byteArray[n] = replace;
			}
		}
		return byteArray;
	}
	
	

}
