package utils;

import org.apache.commons.lang.StringUtils;

public class MyStringUtils {

	public static int countMatches( final String s, final char c ) {
		  final char[] chars = s.toCharArray();
		  int count = 0;
		  for(int i=0; i<chars.length; i++) {
		    if (chars[i] == c) {
		      count++;
		    }
		  }
		  return count;
	}

	public static String replaceProblematicSequenceNameSymbols(String name) {
		if(name != null){
			// replacing is done in two ways - with string regexp and StringUtilities (just for fun...)
			name = name.replaceAll("\\-", "_");
			name = StringUtils.replaceChars(name, ' ', '_');
			name = StringUtils.replace(name, "\"", "_");
			name = StringUtils.replace(name, "?", "");
			name = StringUtils.replace(name, ".", "");
			name = StringUtils.replace(name, "/", "_");
			name = StringUtils.replace(name, "|", "_");
		}
		
		return name;
	}
	
	
}
