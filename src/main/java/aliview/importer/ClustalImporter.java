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
import aliview.MemoryUtils;
import aliview.sequences.ClustalSequence;
import aliview.sequences.PhylipSequence;
import aliview.sequences.Sequence;

public class ClustalImporter {
	private static final Logger logger = Logger.getLogger(ClustalImporter.class);

	private Reader reader;
	public static final int UNKNOWN = -1;
	public static int INTERLEAVED_OR_SINGLELINE_SEQUENTIAL = 0;
	public int formatType;

	private long fileSize;

	public static void main(String[] args) throws FileNotFoundException, AlignmentImportException {
		//File alignmentFile = new File("/home/anders/projekt/alignments/Woodsia_chloroplast_min4_20131109_v2.excluded.aln");
		//	File alignmentFile = new File("/home/anders/projekt/alignments/SMALL-FLAVI-v7-dating.nuc.aed.ALL.protfnuc.mafft.glob.cod.seav.aln");
		File alignmentFile = new File("/home/anders/projekt/alignments/testseq1.aln");
		ClustalImporter importer = new ClustalImporter(new FileReader(alignmentFile), INTERLEAVED_OR_SINGLELINE_SEQUENTIAL);
		importer.importSequences();
	}

	public ClustalImporter(Reader reader, int formatType, long fileSize) {
		this.reader = reader;
		this.formatType = formatType;
		this.fileSize = fileSize;
	}
	
	public ClustalImporter(Reader reader, long fileSize) {
		this(reader,INTERLEAVED_OR_SINGLELINE_SEQUENTIAL, fileSize);
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
			firstLine = firstLine.trim();

			// if not clustal file then it will throw error...
			boolean isRightFormat = isStringValidFirstLine(firstLine);
			if(! isRightFormat){
				throw new AlignmentImportException("Could not read first line as clustal format");
			}


			logger.info("inside clustal importer");

			if(formatType == INTERLEAVED_OR_SINGLELINE_SEQUENTIAL){

				List<String> seqNames = new ArrayList<String>();
				List<ByteBufferAutogrow> seqBuffers = new ArrayList<ByteBufferAutogrow>();

				// skip until start of seq
				helper.readUntilNextNonBlankLine();

				// get first rows of sequences(
				int seqCount = 0;
				int longestName = 0;
				int seqPartLen = 0;
				// in clustal there can be a non blank row without name that contains preservation
				while(helper.isNextLineStartingWithNonBlankChar()){
					String line = helper.getNextLine();
	//				logger.info("line" + line);
					int index = helper.indexOfFirstNonWhiteCharAfterWhiteChar(line);
					String name = line.substring(0, index).trim();
					seqNames.add(name);

					longestName = Math.max(longestName, name.length());
					
					// in clustal there cqan be another space followed by number - this should be removed
					int endIndex = line.indexOf(' ',index);
					if(endIndex == -1){
						endIndex = line.length();
					}
					// end clustal trim end of line
					String seqChars = line.substring(index,endIndex);

					// remove any blank - should not happen in clustal-format
					if(seqChars.indexOf(' ') > -1){
						seqChars = StringUtils.remove(seqChars, ' ');
					}
					
					seqPartLen = Math.max(seqPartLen, seqChars.length());

					int capacity = 1000; // we dont know in Clustal format
					ByteBufferAutogrow seqBuff = new ByteBufferAutogrow(capacity);	
					seqBuff.append(seqChars);
					seqBuffers.add(seqBuff);
					seqCount ++;

					helper.readNextLine();
				}

				// skip until start of seq
				helper.readUntilNextNonBlankLine();
				
				// Calculate seqBuff size
				int interleaveSize = (longestName + seqPartLen) * seqCount;
				long nInterleaves = fileSize / interleaveSize;			
				long guessedLength = nInterleaves * seqPartLen; 				
				int guessedCapacity = (int) (guessedLength * 1.05); // 1.05 to add some blank in between
				logger.info("guessedCapacity" + guessedCapacity);

				MemoryUtils.logMem();
				
				for(ByteBufferAutogrow seqBuff: seqBuffers){
					seqBuff.ensureCapacity(guessedCapacity);
				}
				
				MemoryUtils.logMem();
				
				logger.info("seqCount * guessedCapacity=" + (seqCount * guessedCapacity));
				
				// if sequences are interleaved then there are more data to read
				while(helper.isNextLineStartingWithNonBlankChar()){

					// loop through all sequences in order
					int lineCount = 0;

					while(lineCount < seqCount && helper.isNextLineStartingWithNonBlankChar()){
						// read lines of seq data
						String line = helper.getNextLine();

		//				logger.info("line" + line);

						// in clustal there is the name so lets find end of name
						int seqStart = helper.indexOfFirstNonWhiteCharAfterWhiteChar(line);

						// in clustal there cqan be another space followed by number - this should be removed
						int endIndex = line.indexOf(' ',seqStart);
						if(endIndex == -1){
							endIndex = line.length();
						}
						// end clustal trim end of line
						String moreChars = line.substring(seqStart,endIndex);

						// there should not be any blanks to remove in clustal format
						if(moreChars.indexOf(' ') > -1){
							moreChars = StringUtils.remove(moreChars, ' ');
						}
						ByteBufferAutogrow seqBuff = seqBuffers.get(lineCount);
						seqBuff.append(moreChars);

						lineCount ++;
						helper.readNextLine();
					}
//					logger.info("readUntilNextNonBlankLine");
					helper.readUntilNextNonBlankLine();
//					logger.info("donereadUntilNextNonBlankLine");
				//	MemoryUtils.logMem();
				}

				logger.info("before convert");
				
				for(int n = 0; n <seqCount; n++){	
					//sequences.add(new PhylipSequence(seqNames.get(n), ""));
					sequences.add(new ClustalSequence(seqNames.get(n), seqBuffers.get(n).getBytes()));
					// and clear memory
					seqNames.set(n,null);
					seqBuffers.set(n,null);
				}

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


	public static boolean isStringValidFirstLine(String firstLine) {
		if(StringUtils.containsIgnoreCase(firstLine, "CLUSTAL")){
			return true;
		}
		else{
			return false;
		}
	}
}
