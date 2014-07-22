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

import aliview.FileFormat;


public class FileImportUtils {
	private static final Logger logger = Logger.getLogger(FileImportUtils.class);
	private static final String LF = System.getProperty("line.separator");

	private int longestSequenceLength;

	public static void main(String[] args) {
		FileImportUtils ffFileTest = new FileImportUtils();
		ffFileTest.isFileOfAlignmentFormat(new File("/vol2/big_data/SSURef_108_filtered_bacteria_pos_5389-24317.fasta"));
	}

	public FileImportUtils() {
	}

	public static boolean isThisFasta(String seq){
		boolean isFasta = false;
		if(seq != null && seq.startsWith(">")){
			isFasta = true;
		}
		return isFasta;
	}
	
	
	public static FileFormat isFileOfAlignmentFormat(File seqFile){
		if(seqFile == null || !seqFile.exists()){
			return null;
		}
		
		long startTime = System.currentTimeMillis();
		FileFormat foundFormat = null;

		try {
			StringBuilder sequence = new StringBuilder();

			//File seqFile = new File("/home/anders/projekt/ormbunkar/analys/karin_alignment/ssu_pr2-99.fasta.diffenc2");

			//RandomAccessFile raf = new RandomAccessFile(seqFile, "r");
			OptimizedRandomAccessFile raf = new OptimizedRandomAccessFile(seqFile, "r");

			//BufferedReader r = new BufferedReader(this.reader);
			String line = "";
			String name = null;
			int nLine = 0;
			long nSeqCount = 0;

			byte[] buffer = new byte[200];

			if ((raf.read(buffer)) > 0) {

				String filestart = new String(buffer);

				// remove controlchar
				filestart = StringUtils.trim(filestart);

				// only first char
				String[] splitted = filestart.split("\n");
				String firstLine = splitted[0];
				
				logger.info("firstLine" + firstLine);

				if(firstLine.startsWith(">")){
					foundFormat = FileFormat.FASTA;
				}else if(StringUtils.containsIgnoreCase(firstLine, "NEXUS")){
					foundFormat = FileFormat.NEXUS;
				}else if(ClustalImporter.isStringValidFirstLine(firstLine)){
					foundFormat = FileFormat.CLUSTAL;
				}else if(MSFImporter.isStringValidFirstLine(firstLine)){
					foundFormat = FileFormat.MSF;
				}else if(PhylipImporter.isStringValidFirstLine(firstLine)){
					foundFormat = FileFormat.PHYLIP;
				}
				
			}

			long endTime = System.currentTimeMillis();
			logger.info("check fileformat took " + (endTime - startTime) + " milliseconds, found:" + foundFormat);

		}catch(Exception exc){
			exc.printStackTrace();
			// not file format skip
		}

		return foundFormat;
	}

	public static boolean isThisSequenceFile(String fileName) {
		boolean isSequenceFile = false;
		if(fileName != null){
			File testFile = new File(fileName);
			FileFormat format = isFileOfAlignmentFormat(testFile);
			if(format != null){
				isSequenceFile = true;
			}
		}
		return isSequenceFile;
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
