package aliview.sequences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;
import aliview.importer.AlignmentImportException;
import aliview.importer.FileImportUtils;
import aliview.sequencelist.FileMMSequenceList;

public class IndexFileReader {
	private static final Logger logger = Logger.getLogger(IndexFileReader.class);

	public static ArrayList<FileSequence> createSequences(File indexFile, FileMMSequenceList seqList) {
		
			long startTime = System.currentTimeMillis();
			ArrayList<FileSequence> sequences = new ArrayList<FileSequence>();
			
			try {
				BufferedReader r = new BufferedReader(new FileReader(indexFile));
				String line;
				int nLine = 0;
				int seqIndex = 0;
		//		String[] splitted = new String[5];
				while ((line = r.readLine()) != null) {
					
					line = line.trim();
				
					if(line.length() > 0){
						
						String[] splitted = StringUtils.split(line, '\t');
					//	String[] splitted = line.split("\t");//StringUtils.split(line, '\t');
						
//						int startPos = 0;
//						int endPos = line.indexOf('\t',startPos);
//						
//						splitted[0] = line.substring(startPos, endPos);
//						startPos = endPos + 1;
//						endPos = line.indexOf('\t',startPos);
//						splitted[1] = line.substring(startPos, endPos);
//						startPos = endPos + 1;
//						endPos = line.indexOf('\t',startPos);
//						splitted[2] = line.substring(startPos, endPos);
//						startPos = endPos + 1;
//						endPos = line.indexOf('\t',startPos);
//						splitted[3] = line.substring(startPos, endPos);
//						startPos = endPos + 1;
//						endPos = line.indexOf('\t',startPos);
//						splitted[4] = line.substring(startPos, endPos);
						
						String  seqName = splitted[0];
						int seqWithoutWhitespaceLength = Integer.parseInt(splitted[1]);
						long seqAfterNameStartPointer = Long.parseLong(splitted[2]);
						int lineCharLength = Integer.parseInt(splitted[3]);
						int lineAbsoluteLength = Integer.parseInt(splitted[4]);
						
						int nSeqFullLines = (int)Math.floor(seqWithoutWhitespaceLength/lineCharLength);
						int lineDiff = lineAbsoluteLength - lineCharLength;
						
						
						double partialLine = ((double)seqWithoutWhitespaceLength/(double)lineCharLength) - (double)nSeqFullLines;
						
						int extraChars = (int)Math.floor(partialLine * lineDiff);
						
						
						long endPointer = seqAfterNameStartPointer + seqWithoutWhitespaceLength + nSeqFullLines * lineDiff + extraChars;
						
						FileSequence seq = new FileSequence(seqList, seqIndex, seqName, seqWithoutWhitespaceLength, seqAfterNameStartPointer, endPointer, lineCharLength, lineAbsoluteLength);
						sequences.add(seq);
						seqIndex ++;
					}
					nLine ++;
				}
				
			} catch (Exception e) {
				logger.error(e);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("reading index took " + (endTime - startTime) + " milliseconds");

			return sequences;
		}
		

}
