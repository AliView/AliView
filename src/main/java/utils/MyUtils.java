package utils;

import org.apache.commons.lang.StringUtils;
/*
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
 */

public class MyUtils {

	/*
	public static BufferedReader getBufferedReaderForBZ2File(String fileIn) throws FileNotFoundException, CompressorException {
	    FileInputStream fin = new FileInputStream(fileIn);
	    BufferedInputStream bis = new BufferedInputStream(fin);
	    CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
	    BufferedReader br2 = new BufferedReader(new InputStreamReader(input));

	    return br2;
	}
	 */

	public static int[] explodeInBetweenString(String inBetween) {

		String[] parts = StringUtils.split(inBetween,"-");

		int start = Integer.parseInt(parts[0]);
		int end = Integer.parseInt(parts[1]);
		int[] allValues = new int[end-start + 1]; 

		int count = 0;
		for(int n = start; n <= end; n++){
			System.err.println("addingVal" + n);
			allValues[count] = n;
			count ++;
		}

		return allValues;

	}
	/*
	public static String subStringBeforeDelimiter(String input, String delimiter, int nDelimiterOccurrence){



	}

	public static int indexOfNOccurrence(String input, String substring, int nOccurrence){

		int occurrCount = 0;
		int fromIndex = 0;
		while(nOccurrence < occurrCount){

			int index = input.indexOf(substring, fromIndex);

			fromIndex = index + 1;

		}

	}
	 */


	public static String wildcardToRegex(String wildcard){
		StringBuffer s = new StringBuffer(wildcard.length());
		s.append('^');
		for (int i = 0, is = wildcard.length(); i < is; i++) {
			char c = wildcard.charAt(i);
			switch(c) {
			case '*':
				s.append(".*");
				break;
			case '?':
				s.append(".");
				break;
				// escape special regexp-characters
			case '(': case ')': case '[': case ']': case '$':
			case '^': case '.': case '{': case '}': case '|':
			case '\\':
				s.append("\\");
				s.append(c);
				break;
			default:
				s.append(c);
				break;
			}
		}
		s.append('$');
		return(s.toString());
	}

	public static String getFileSuffix(String in){
		return StringUtils.substringAfter(in, ".");
	}


}
