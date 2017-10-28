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

import aliview.MemoryUtils;
import aliview.sequences.MSFSequence;
import aliview.sequences.PhylipSequence;
import aliview.sequences.Sequence;

public class MSFImporter {
	private static final Logger logger = Logger.getLogger(MSFImporter.class);

	private Reader reader;
	public static final int UNKNOWN = -1;
	public static int INTERLEAVED_OR_SINGLELINE_SEQUENTIAL = 0;
	public int formatType;

	public static void main(String[] args) throws FileNotFoundException, AlignmentImportException {
		File alignmentFile = new File("/home/anders/projekt/alignments/MSF_format.example.msf");
		MSFImporter importer = new MSFImporter(new FileReader(alignmentFile), INTERLEAVED_OR_SINGLELINE_SEQUENTIAL);
		importer.importSequences();
	}


	public MSFImporter(Reader reader, int formatType) {
		this.reader = reader;
		this.formatType = formatType;
	}

	public MSFImporter(FileReader fileReader) {
		this(fileReader,INTERLEAVED_OR_SINGLELINE_SEQUENTIAL);
	}


	public List<Sequence> importSequences() throws AlignmentImportException {

		long startTime = System.currentTimeMillis();
		List<Sequence> sequences = new ArrayList<Sequence>();
		try {
			String sequenceString = "";
			BufferedReader r = new BufferedReader(this.reader);
			ReaderHelper helper = new ReaderHelper(r);


			helper.readNextLine();
			String firstLine = helper.getNextLine();
			boolean isRightFormat = isStringValidFirstLine(firstLine);
			if(! isRightFormat){
				throw new AlignmentImportException("Could not read file as MSF format");
			}

			boolean containsMSF = helper.readUntilNextLineContains("MSF:");
			String metaLine = helper.getNextLine();		
			String strLength = StringUtils.substringBetween(metaLine,"MSF:","Type:");
			strLength = strLength.trim();
			int guessedLength = 0;
			try {
				guessedLength = Integer.parseInt(strLength);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			logger.info("guessedLength" + guessedLength);

			boolean containsName = helper.readUntilNextLineContains("Name:");
			String firstNameLine = helper.getNextLine();		
			String firstName = StringUtils.substringBetween(firstNameLine,": "," ");
			firstName = firstName.trim();

			logger.info("inside msf importer, firstName = " + firstName);

			if(formatType == INTERLEAVED_OR_SINGLELINE_SEQUENTIAL){

				List<String> seqNames = new ArrayList<String>();
				List<ByteBufferAutogrow> seqBuffers = new ArrayList<ByteBufferAutogrow>();

				// skip until start of sequences
				helper.readUntilNextLineContains("//");
				helper.readUntilNextLineContains(firstName);


				// get first rows of sequences(
				int seqCount = 0;
				int longestName = 0;
				int longestSeq = 0;
				// in clustal there can be a non blank row without name that contains preservation
				while(helper.isNextLineContainingNonWhitespaceChars()){
					String line = helper.getNextLine();
					//		logger.info("line" + line);
					// remove blanks in beginning of name
					line = line.trim();
					int index = ReaderHelper.indexOfFirstNonWhiteCharAfterWhiteChar(line);
					String name = line.substring(0, index).trim();
					seqNames.add(name);

					String seqChars = line.substring(index);

					// remove any blank and replace MSF . and ~ characters
					seqChars = ReaderHelper.removeSpaceAndTab(seqChars);
					seqChars = replaceMSFGapCharacters(seqChars);

					int capacity = guessedLength; // we dont know (i guess it could be read in header)
					ByteBufferAutogrow seqBuff = new ByteBufferAutogrow(capacity);	
					seqBuff.append(seqChars);
					seqBuffers.add(seqBuff);
					seqCount ++;

					helper.readNextLine();
				}




				// if sequences are interleaved then there are more data to read
				while(helper.readUntilNextLineContains(firstName)){

					// loop through all sequences in order
					int lineCount = 0;

					while(lineCount < seqCount){
						// read lines of seq data
						String line = helper.getNextLine();
						// remove blanks in beginning of name
						line = line.trim();
						int index = ReaderHelper.indexOfFirstNonWhiteCharAfterWhiteChar(line);

						String moreChars = line.substring(index);

						// remove any blank and replace MSF . and ~ characters
						moreChars = ReaderHelper.removeSpaceAndTab(moreChars);
						moreChars = replaceMSFGapCharacters(moreChars);

						ByteBufferAutogrow seqBuff = seqBuffers.get(lineCount);
						seqBuff.append(moreChars);

						lineCount ++;
						helper.readNextLine();
					}

					//	MemoryUtils.logMem();

				}

				for(int n = 0; n <seqCount; n++){	
					//sequences.add(new PhylipSequence(seqNames.get(n), ""));
					sequences.add(new MSFSequence(seqNames.get(n), seqBuffers.get(n).getBytes()));
					// and clear memory
					seqNames.set(n,null);
					seqBuffers.set(n,null);
				}

				// Only logging
				//				for(Sequence seq: sequences){
				//					logger.info(seq.getName() + " " + seq.getBasesAsString());
				//				}
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

		return sequences;
	}

	private String replaceMSFGapCharacters(String seqChars){
		if(seqChars.indexOf('.') > -1){
			seqChars = seqChars.replace('.', '-');
		}
		if(seqChars.indexOf('~') > -1){
			seqChars = seqChars.replace('~', '-');
		}
		return seqChars;
	}


	public static boolean isStringValidFirstLine(String firstLine) {
		if(StringUtils.contains(firstLine, "!!") || StringUtils.containsIgnoreCase(firstLine, "PileUp")){
			return true;
		}else{
			return false;
		}
	}

	/*
	 * 
	 * This method is copied and modified from iubio.readseq
	 * 
	 */

	public static int GCGchecksum(Sequence seq){
		int check = 0;

		for (int n = 0; n < seq.getLength(); n++){
			byte byteVal = seq.getBaseAtPos(n);
			int val = Character.toLowerCase(byteVal);
			if (val >= 'a' && val <= 'z'){
				val -= 32;
			}

			int positionMultiplier = n % 57 + 1;
			check += val * positionMultiplier;

		}
		check %= 10000;
		return check;
	}

}

