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

import aliview.sequences.PhylipSequence;
import aliview.sequences.Sequence;

public class PhylipImporter {
	private static final Logger logger = Logger.getLogger(PhylipImporter.class);
	
	private Reader reader;
	private int longestSequenceLength;
	public FileFormat formatType;
	
	public static void main(String[] args) throws FileNotFoundException, AlignmentImportException {
		File alignmentFile = new File("/home/anders/projekt/alignments/smalphylipSeqShortName.phy");
		PhylipImporter importer = new PhylipImporter(new FileReader(alignmentFile), FileFormat.PHYLIP_SHORT_NAME_INTERLEAVED);
		importer.importSequences();
	}
	
	
	public PhylipImporter(Reader reader, FileFormat formatType) {
		this.reader = reader;
		this.formatType = formatType;
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
				
				if(formatType == FileFormat.PHYLIP_RELAXED_PADDED_INTERLEAVED_AKA_LONG_NAME_INTERLEAVED){
					
					List<String> seqNames = new ArrayList<String>();
					// since we already know sequence size then we can use ByteBuffer
					List<ByteBuffer> seqBuffers = new ArrayList<ByteBuffer>();
					//List<StringBuilder> seqBuffers = new ArrayList<StringBuilder>();
					
					// try long name sequential
					for(int n = 0; n <seqCount; n++){
						// read lines of seq data
						helper.readNextLine();
						String line = helper.getNextLine();
						int index = ReaderHelper.indexOfFirstNonWhiteCharAfterWhiteChar(line);
						String name = line.substring(0, index).trim();	
						seqNames.add(name);
						
						logger.info("name" + name);
						logger.info("index" + index);
						
						int capacity = longestSequenceLength;
						ByteBuffer seqBuff = ByteBuffer.allocate(capacity);
						String seqChars = line.substring(index);
						seqChars = ReaderHelper.removeSpaceAndTab(seqChars);
						logger.info("seqChars" + seqChars);
						seqBuff.put(seqChars.getBytes());
						seqBuffers.add(seqBuff);					
					}
							
					while(true){
							
						// loop through all sequences in order
						int lineCount = 0;
						while(lineCount < seqCount){		
							// read lines of seq data
							helper.readNextLine();
							String line = helper.getNextLine();
							int index = ReaderHelper.indexOfFirstNonWhiteChar(line);
							
							// Skip empty lines
							if(index == -1){
								logger.info("skip empty");
							}else{
								String moreChars = line.substring(index);
								
								moreChars = ReaderHelper.removeSpaceAndTab(moreChars);
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
						}else{
							logger.info("seqBuff.position()" + seqBuff.position());
						}
						
						if(seqBuff.position() > longestSequenceLength){
							logger.info("wrong length");	
							throw new AlignmentImportException("Did not match Phylip.LONG_NAME_INTERLEAVED");		
						}
					}
					
				}
				
				if(formatType == FileFormat.PHYLIP_RELAXED_PADDED_AKA_LONG_NAME_SEQUENTIAL){
					// try long name sequential
					for(int n = 0; n <seqCount; n++){
						String name = helper.getStringUntilNextSpaceOrTab();
						// read lines of seq data
						StringBuilder seqBuffer = new StringBuilder(longestSequenceLength);
						while(seqBuffer.length() < longestSequenceLength){
							// read lines of seq data
							helper.readNextLine();
							String line = helper.getNextLine();
							line = ReaderHelper.removeSpaceAndTab(line);		
							seqBuffer.append(line);
						}
						if(seqBuffer.length() != longestSequenceLength){
							throw new AlignmentImportException("Did not match FileFormat.PHYLIP_RELAXED_PADDED_AKA_LONG_NAME_SEQUENTIAL");
						}
						sequences.add(new PhylipSequence(name, seqBuffer.toString()));
					}
				}
				
				if(formatType == FileFormat.PHYLIP_STRICT_SEQUENTIAL_AKA_SHORT_NAME_SEQUENTIAL){
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
				
				
				if(formatType == FileFormat.PHYLIP_SHORT_NAME_INTERLEAVED){
					// try long name sequential
					
					// first read names lines
					for(int n = 0; n <seqCount; n++){
					
						// short name sequential
						String name = helper.getStringFromNextPositions(10);
						
						// read rest of line as seq data
						helper.readNextLine();
						String line = helper.getNextLine();
						line = ReaderHelper.removeSpaceAndTab(line);			
						sequences.add(new PhylipSequence(name, line));
					}
					
					// now read rest of sequences
					
					while(true){
						// loop through all sequences in order
						for(int n = 0; n <seqCount; n++){			
							// read lines of seq data
							helper.readNextLine();
							String line = helper.getNextLine();
							line = ReaderHelper.removeSpaceAndTab(line);	
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

	public static boolean isStringValidFirstLine(String firstLine) {
		boolean isValid = false;
		if(firstLine.contains(" ")){
			String[] lineSplitted = firstLine.split("\\s+"); // one or many whitespace
			if( NumberUtils.isNumber(lineSplitted[0]) && NumberUtils.isNumber(lineSplitted[1]) ){
				isValid = true;
			}
		}
		return isValid;
	}


	public FileFormat getFileFormat() {
		// TODO Auto-generated method stub
		return null;
	}
}
