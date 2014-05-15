package aliview.importer;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import aliview.FileFormat;
import aliview.sequences.PhylipSequence;
import aliview.sequences.Sequence;

public class PhylipImporter {
	private static final Logger logger = Logger.getLogger(PhylipImporter.class);
	
	private Reader reader;
	private int longestSequenceLength;
	public static final int UNKNOWN = -1;
	public static int LONG_NAME_INTERLEAVED = 0;
	public static int SHORT_NAME_INTERLEAVED = 1;
	public static int SHORT_NAME_SEQUENTIAL = 2;
	public static int LONG_NAME_SEQUENTIAL = 3;
	public static int LONG_NAME_SEQUENTIAL_ONELINE = 4;
	public int importerType;
	
	public static void main(String[] args) throws FileNotFoundException, AlignmentImportException {
		File alignmentFile = new File("/home/anders/projekt/alignments/smalphylipSeqShortName.phy");
		PhylipImporter phylipImporter = new PhylipImporter(new FileReader(alignmentFile), PhylipImporter.SHORT_NAME_INTERLEAVED);
		phylipImporter.importSequences();
	}
	
	
	public PhylipImporter(Reader reader, int phylipType) {
		this.reader = reader;
		this.importerType = phylipType;
	}
	
	public List<Sequence> importSequences() throws AlignmentImportException {

		long startTime = System.currentTimeMillis();
		List<Sequence> sequences = new ArrayList<Sequence>();
		try {
			String sequenceString = "";
			BufferedReader r = new BufferedReader(this.reader);
			
			String firstLine = r.readLine();
			firstLine = firstLine.trim();

			// if not phylip file then it will throw error...
			int seqCount = 0;
			String[] lineSplitted = firstLine.split("\\s+"); // one or many whitespace
			logger.info("splitSize" + lineSplitted.length);
			if(lineSplitted != null && lineSplitted.length == 2 && NumberUtils.isNumber(lineSplitted[0]) && NumberUtils.isNumber(lineSplitted[1]) ){
				seqCount = Integer.parseInt(lineSplitted[0]);
				longestSequenceLength = Integer.parseInt(lineSplitted[1]);
			}
			else{
				throw new AlignmentImportException("Could not read first line as phylip format");
			}

			ReaderHelper helper = new ReaderHelper(r);
			
			logger.info("inside phy importer");
			
			try{
				
				if(importerType == LONG_NAME_INTERLEAVED){
					
					List<String> seqNames = new ArrayList<String>();
					//List<StringBuilder> seqBuffers = new ArrayList<StringBuilder>();
					List<ByteBuffer> seqBuffers = new ArrayList<ByteBuffer>();
					
					// try long name sequential
					for(int n = 0; n <seqCount; n++){
						String line = helper.readLine();
						int index = indexOfFirstNonWhiteCharAfterWhiteChar(line);
						String name = line.substring(0, index).trim();	
						seqNames.add(name);
						ByteBuffer seqBuff = ByteBuffer.allocate(longestSequenceLength);
						String seqChars = line.substring(index);
						if(seqChars.indexOf(' ') > -1){
							seqChars = StringUtils.remove(seqChars, ' ');
						}
						seqBuff.put(seqChars.getBytes());
						seqBuffers.add(seqBuff);					
					}
							
					while(true){
							
						// loop through all sequences in order
						int lineCount = 0;
						while(lineCount < seqCount){		
							// read lines of seq data
							String line = helper.readLine();
							int index = indexOfFirstNonWhiteChar(line);
							
							// Skip empty lines
							if(index > -1){
								String moreChars = line.substring(index);
								if(moreChars.indexOf(' ') > -1){
									moreChars = StringUtils.remove(moreChars, ' ');
								}
								ByteBuffer seqBuff = seqBuffers.get(lineCount);
								seqBuff.put(moreChars.getBytes());
								lineCount ++;
							}
						}
						ByteBuffer seqBuff = seqBuffers.get(seqBuffers.size() - 1);
						// check to see if last sequence is filled then break
						if(seqBuff.position() == longestSequenceLength){
							logger.info("right length");
							
							// create sequences
							for(int n = 0; n <seqCount; n++){	
								//sequences.add(new PhylipSequence(seqNames.get(n), ""));
								sequences.add(new PhylipSequence(seqNames.get(n), seqBuffers.get(n).array()));
								seqNames.set(n,null);
								seqBuffers.set(n,null);
							}
							
							break;			
						}
						
						if(seqBuff.position() > longestSequenceLength){
							logger.info("wrong length");	
							throw new AlignmentImportException("Did not match Phylip.LONG_NAME_INTERLEAVED");		
						}
					}
					
				}
				
				if(importerType == LONG_NAME_SEQUENTIAL){
					// try long name sequential
					for(int n = 0; n <seqCount; n++){
						String name = helper.getStringUntilNextSpaceOrTab();
						// read lines of seq data
						StringBuilder seqBuffer = new StringBuilder(longestSequenceLength);
						while(seqBuffer.length() < longestSequenceLength){
							String line = helper.readLine();
							if(line.indexOf(' ')>-1){
								line = StringUtils.remove(line, ' ');
							}
							seqBuffer.append(line);
						}
						if(seqBuffer.length() != longestSequenceLength){
							throw new AlignmentImportException("Did not match Phylip.LONG_NAME_SEQUENTIAL");
						}
						sequences.add(new PhylipSequence(name, seqBuffer.toString()));
					}
				}
				
				if(importerType == SHORT_NAME_SEQUENTIAL){
					// try long name sequential
					for(int n = 0; n <seqCount; n++){
						String name = helper.getStringFromNextPositions(10);
						byte[] sequence = helper.getNonWhiteBytes(longestSequenceLength);
						sequences.add(new PhylipSequence(name, sequence));
						// if not last seq go on to nest name
						if(n != seqCount -1){
							helper.skipPastNextline();
						}
					}
				}
				
				
				if(importerType == SHORT_NAME_INTERLEAVED){
					// try long name sequential
					
					// first read names lines
					for(int n = 0; n <seqCount; n++){
					
						// short name sequential
						String name = helper.getStringFromNextPositions(10);
						
						// read rest of line as seq data
						String line = helper.readLine();
						if(line.indexOf(' ')>-1){
							line = StringUtils.remove(line, " ");
						}	
						sequences.add(new PhylipSequence(name, line));
					}
					
					// now read rest of sequences
					
					while(true){
						// loop through all sequences in order
						for(int n = 0; n <seqCount; n++){			
							// read lines of seq data
							String line = helper.readLine();
							if(line.indexOf(' ')>-1){
								line = StringUtils.remove(line, " ");
							}
							PhylipSequence seq = (PhylipSequence) sequences.get(n);
							seq.append(line);
						}
						PhylipSequence seq = (PhylipSequence) sequences.get(sequences.size() - 1);
						// check to see if last sequence is filled then break
						if(seq.getLength() == longestSequenceLength){
							break;
						}
					}
				}
				
				
			}catch(EOFException eofExc){
				// if import is ok there should not have been an EOF
				throw new AlignmentImportException("Premature End of file when importing");
			}
			
			
			/*
			if(importerType == SHORT_NAME_INTERLEAVED){
				// try short name sequential
				for(int n = 0; n <seqCount; n++){
					String name = helper.getStringFromNextPositions(10);
					byte[] sequence = helper.getNonWhiteBytesUntilNewLine(longestSequenceLength);
					sequences.add(new PhylipSequence(name, sequence));
					// if not last seq go on to nest name
					if(n != seqCount -1){
						helper.skipPastNextline();
					}
				}
			}
			*/
			
			
			
			
			
			// try long name interleaved
			
			// try short name sequential
			

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

		return sequences;
	}

	public int getLongestSequenceLength() {
		return longestSequenceLength;
	}
	
	private int indexOfFirstNonWhiteCharAfterWhiteChar(String text) {
		boolean whiteFound = false;
		int index = -1;
		for(int n = 0; n< text.length(); n++){
			if(isWhiteSpace(text.charAt(n))){
				whiteFound = true;
			}
			if(whiteFound && !isWhiteSpace(text.charAt(n))){
				index = n;
				break;
			}
		}
		return index;
	}
	
	private int indexOfFirstNonWhiteChar(String text) {
		int index = -1;
		for(int n = 0; n< text.length(); n++){
			if(! isWhiteSpace(text.charAt(n))){
				index = n;
				break;
			}
		}
		return index;
	}
	
	private boolean isWhiteSpace(char c) {
		if(c==' ' || c == '\t' || c=='\r' || c=='\n'){
			return true;
		}
		else{
			return false;
		}
	}
}
