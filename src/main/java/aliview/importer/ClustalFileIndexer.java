package aliview.importer;

import it.unimi.dsi.io.ByteBufferInpStream;

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

import aliview.sequencelist.FileSequenceAlignmentListModel;
import aliview.sequencelist.MemoryMappedSequencesFile;
import aliview.sequences.ClustalFileSequence;
import aliview.sequences.FileSequence;
import aliview.sequences.PositionToPointer;
import aliview.sequences.Sequence;
import aliview.subprocesses.SubThreadProgressWindow;

public class ClustalFileIndexer {
	private static final Logger logger = Logger.getLogger(ClustalFileIndexer.class);
	private static final String LF = System.getProperty("line.separator");
	public static int INTERLEAVED_OR_SINGLELINE_SEQUENTIAL = 0;


	public static void main(String[] args) throws AlignmentImportException, IOException {
		//File alignmentFile = new File("/home/anders/projekt/alignments/Woodsia_chloroplast_min4_20131109_v2.excluded.aln");
		//	File alignmentFile = new File("/home/anders/projekt/alignments/SMALL-FLAVI-v7-dating.nuc.aed.ALL.protfnuc.mafft.glob.cod.seav.aln");

		File alignmentFile = new File("/home/anders/projekt/alignments/testseq1.aln");
		FileSequenceAlignmentListModel model = new FileSequenceAlignmentListModel(alignmentFile, FileFormat.CLUSTAL);
	}


	public ArrayList<Sequence> findSequencesInFile(MemoryMappedSequencesFile sequencesFile, long filePointerStart, int seqOffset, int nSeqsToRetrieve,
			SubThreadProgressWindow progressWin) throws AlignmentImportException {
		long startTime = System.currentTimeMillis();
		
		ByteBufferInpStream mappedBuff = sequencesFile.getMappedBuff();
		
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		try{
			long fileSize = mappedBuff.length();
			int longestSequenceLength = 0;
			mappedBuff.position(filePointerStart);
			MappedBuffReaderHelper readerHelper = new MappedBuffReaderHelper(mappedBuff);

			// Get newline char
			String firstLine = readerHelper.readLine();
			int newlineLen = 1;
			if(firstLine.endsWith("\r\n")){
				newlineLen = 2;
			}else{
				newlineLen = 1;
			}


			// if not clustal file then it will throw error...
			boolean isRightFormat = isStringValidFirstLine(firstLine);
			if(! isRightFormat){
				throw new AlignmentImportException("Could not read first line as clustal format");
			}


			logger.info("inside clustal importer");

			int formatType = INTERLEAVED_OR_SINGLELINE_SEQUENTIAL;
			if(formatType == INTERLEAVED_OR_SINGLELINE_SEQUENTIAL){

				try{
					List<String> seqNames = new ArrayList<String>();
					List<StringBuilder> seqBuffers = new ArrayList<StringBuilder>();

					// skip until start of seq
					readerHelper.skipUntilNextNonWhiteCharInFirstPosAfterNewLine();
					long nameStartPointer = readerHelper.position();
					readerHelper.setPosition(nameStartPointer);

					int lineCount = 0; // only for display
					int seqCount = 0;
					int seqPos = 0;
					// in clustal there can be a non blank row without name that contains preservation
					while(true){

						// position sequence start (also name endpos)
						readerHelper.setPosition(nameStartPointer);
						long seqStartPointer = readerHelper.posOfFirstNonWhiteCharAfterWhiteChar();
						// in clustal there are optional numbers after whitespace before line end
						long seqEndPointer = readerHelper.posOfNextWhitespaceOrLF() - 1;

						ClustalFileSequence seq = new ClustalFileSequence(sequencesFile, nameStartPointer);

						String name = readerHelper.readString(nameStartPointer, seqStartPointer - 1);
						name = name.trim();
						seq.setName(name);
						int seqSeqmentLen = (int) (seqEndPointer - seqStartPointer + 1);	
						seq.add(new PositionToPointer(seqPos,seqPos + seqSeqmentLen -1, seqStartPointer, seqEndPointer));

						sequences.add(seq);
						seqCount ++;

						lineCount ++;
						if(lineCount % 100000 == 0){
							progressWin.setMessage("Indexing interleaved Phylip file" + LF + "line:" + lineCount);
							if(progressWin.wasSubThreadInterruptedByUser()){
								break;
							}
						}

						// if the next name is after more than one linebreak - should be EOF or a round of interleaved sequence parts
						int linebreaks = readerHelper.skipUntilNextNonWhiteCharInFirstPosAfterNewLine();
						nameStartPointer = readerHelper.position();
						if(linebreaks > 1){
							break;
						}			
					}

					// and now append the inteleaved sequences
					while(true){


						for(int n = 0; n < seqCount; n++){
							// position sequence start (also name endpos)
							readerHelper.setPosition(nameStartPointer);

							// clustal has name on every interleaved line
							long interleavedStartPointer = readerHelper.posOfFirstNonWhiteCharAfterWhiteChar();
							//logger.info("interleavedStartPointer" + interleavedStartPointer);		
							// in clustal there are optional numbers after whitespace before line end
							long interleavedEndPointer = readerHelper.posOfNextWhitespaceOrLF() - 1;

							mappedBuff.position(interleavedEndPointer);

							int seqSeqmentLen = (int) (interleavedEndPointer - interleavedStartPointer +1);
							//logger.info("seqSeqmentLen" + seqSeqmentLen);

							ClustalFileSequence appendSeq = (ClustalFileSequence) sequences.get(n);

							int appendSeqPosition = appendSeq.getLength();
							appendSeq.add(new PositionToPointer(appendSeqPosition, interleavedStartPointer, interleavedEndPointer));

							// check that there is a next name on next line (without a empty line between)
							// otherwise break
							int linebreaks = readerHelper.skipUntilNextNonWhiteCharInFirstPosAfterNewLine();
							nameStartPointer = readerHelper.position();
		
							lineCount ++;
							if(lineCount % 100000 == 0){
								progressWin.setMessage("Indexing interleaved Phylip file" + LF + "line:" + lineCount);
								if(progressWin.wasSubThreadInterruptedByUser()){
									break;
								}
							}
							
							// if the next name is after more than one linebreak - should be next round of interleaved sequence parts
							if(linebreaks > 1){
	//							logger.info("break");
								break;
							}
						}
					}
				}catch(EOFException eof){
					logger.info("hit EOF hopefully file is read OK");
					// only log output
//					for(Sequence seq: sequences){
//						logger.info(seq.getName() + " " + seq.getBasesAsString());
//					}
				}




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
