package aliview.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

import utils.FileUtilities;


public class FileImportUtils {
	private static final Logger logger = Logger.getLogger(FileImportUtils.class);
	private static final String LF = System.getProperty("line.separator");

	private int longestSequenceLength;

	

	public FileImportUtils() {
	}

	
	
public static final int INDEX_NOT_FOUND = -1;
	
    /*
	* Modified version of Apache Commons that take a String builder as input and returns StringBuilder
	 */
	public static StringBuilder replace(final StringBuilder text, final String searchString, final String replacement, int max) {
	    int start = 0;
	    int end = text.indexOf(searchString, start);
	    if (end == INDEX_NOT_FOUND) {
	        return text;
	    }
	    final int replLength = searchString.length();
	    int increase = replacement.length() - replLength;
	    increase = increase < 0 ? 0 : increase;
	    increase *= max < 0 ? 16 : max > 64 ? 64 : max;
	    final StringBuilder buf = new StringBuilder(text.length() + increase);
	    while (end != INDEX_NOT_FOUND) {
	        buf.append(text.substring(start, end)).append(replacement);
	        start = end + replLength;
	        if (--max == 0) {
	            break;
	        }
	        end = text.indexOf(searchString, start);
	    }
	    buf.append(text.substring(start));
	    return buf;
	}
	
	 /*
		* Modified version of Apache Commons that take a String builder as input and returns StringBuilder
		 */
		public static StringBuilder removeAll(final StringBuilder text, final String searchString) {
		    int start = 0;
		    int end = text.indexOf(searchString, start);
		    if (end == INDEX_NOT_FOUND) {
		        return text;
		    }
		    final int replLength = searchString.length();
		    final StringBuilder buf = new StringBuilder(text.length());
		    while (end != INDEX_NOT_FOUND) {
		        buf.append(text.substring(start, end));
		        start = end + replLength;
		        end = text.indexOf(searchString, start);
		    }
		    buf.append(text.substring(start));
		    return buf;
		}

		public static StringBuilder replaceChar(StringBuilder sb, char orig, char replace){
			if(sb == null){
				return null;
			}
			for(int index = 0; index < sb.length(); index++) {
				if (sb.charAt(index) == orig) {
					sb.setCharAt(index, replace);
				}
			}
			return sb;
		}

		public static String removeAll(String line, char c) {
			return StringUtils.remove(line, c);
		}

}
