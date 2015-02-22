package aliview.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import it.unimi.dsi.io.ByteBufferInpStream;
import aliview.sequencelist.MemoryMappedSequencesFile;
import aliview.sequences.FastaFileSequence;
import aliview.sequences.FileSequence;
import aliview.sequences.PhylipFileSequence;
import aliview.sequences.PositionToPointer;
import aliview.sequences.Sequence;
import aliview.subprocesses.SubThreadProgressWindow;

public class PhylipFileIndexer implements FileIndexer{
	private static final String LF = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(PhylipFileIndexer.class);
	long estimateTotalSeqInFile = 0;
	long fileSize = -1;
	private MappedBuffReaderHelper readerHelper;

	public ArrayList<Sequence> findSequencesInFile(MemoryMappedSequencesFile sequencesFile, long filePointerStart, int seqOffset, int nSeqsToRetrieve,
			SubThreadProgressWindow progressWin) throws AlignmentImportException {

		ByteBufferInpStream mappedBuff = sequencesFile.getMappedBuff();
		
		ArrayList<Sequence> allSeqs = new ArrayList<Sequence>();
		try{
			this.fileSize = mappedBuff.length();
			int longestSequenceLength = 0;
			mappedBuff.position(filePointerStart);
			readerHelper = new MappedBuffReaderHelper(mappedBuff);

			String firstLine = readerHelper.readLine();
			int newlineLen = 1;
			if(firstLine.endsWith("\r")){
				newlineLen = 2;
			}else{
				newlineLen = 1;
			}
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


			long firstNameStartPointer = mappedBuff.position();


			// determine file type
			int importerType = PhylipImporter.UNKNOWN;	

			// Test PhylipImporter.LONG_NAME_SEQUENTIAL
			if(importerType == PhylipImporter.UNKNOWN){
				try {
					mappedBuff.position(firstNameStartPointer);
					long seqStartPointer = readerHelper.posOfFirstNonWhiteCharAfterWhiteChar();
					long seqEndPointerIfSequential = readerHelper.posAtNSequenceCharacters(seqStartPointer, longestSequenceLength);
					if(readerHelper.isNextLF()){
						// probably long name sequential
						importerType = PhylipImporter.LONG_NAME_SEQUENTIAL;
						logger.info("probably long name sequential");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Test PhylipImporter.SHORT_NAME_SEQUENTIAL
			if(importerType == PhylipImporter.UNKNOWN){
				try {
					long seqEndPointerIfTenPosSequential = readerHelper.posAtNSequenceCharacters(firstNameStartPointer + 10, longestSequenceLength);
					if(readerHelper.isNextLF()){
						// probably long name sequential
						importerType = PhylipImporter.SHORT_NAME_SEQUENTIAL;
						logger.info("probably short name sequential");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// finally try PhylipImporter.LONG_NAME_INTERLEAVED
			if(importerType == PhylipImporter.UNKNOWN){		
				// if only one continous gap --> long interleaved
				try {
					mappedBuff.position(firstNameStartPointer);
					if(readerHelper.hasLineOnlyOneContinousSpace()){
						importerType = PhylipImporter.LONG_NAME_INTERLEAVED;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// finally try PhylipImporter.LONG_NAME_INTERLEAVED
			if(importerType == PhylipImporter.UNKNOWN){
				importerType = PhylipImporter.SHORT_NAME_INTERLEAVED;
				logger.info("probably short name interleaved");
			}


			
			// load depending on file type
			if(importerType == PhylipImporter.LONG_NAME_INTERLEAVED){

				long nameStartPointer = firstNameStartPointer;
				readerHelper.setPosition(nameStartPointer);
				int lineCount = 0;
				
				for(int n = 0; n <seqCount; n++){
					int seqPos = 0;
					// position sequence start (also name endpos)
					readerHelper.setPosition(nameStartPointer);
					long seqStartPointer = readerHelper.posOfFirstNonWhiteCharAfterWhiteChar();
					// end of first line
					long firstNewlinePointer = readerHelper.posOfNextNewline();

					PhylipFileSequence seq = new PhylipFileSequence(sequencesFile, nameStartPointer);

					String name = readerHelper.readString(nameStartPointer, seqStartPointer - 1);
					name = name.trim();
					seq.setName(name);
					int seqSeqmentLen = (int) (firstNewlinePointer - newlineLen - seqStartPointer + 1);	
					seq.add(new PositionToPointer(seqPos,seqPos + seqSeqmentLen -1, seqStartPointer, firstNewlinePointer - newlineLen));

					nameStartPointer = firstNewlinePointer + newlineLen;

					allSeqs.add(seq);

					lineCount ++;
					if(lineCount % 1000 == 0){
						progressWin.setMessage("Indexing interleaved Phylip file" + LF + "line:" + lineCount);
						if(progressWin.wasSubThreadInterruptedByUser()){
							break;
						}
					}
				}

				FileSequence lastPhylSeq = (FileSequence) allSeqs.get(allSeqs.size()-1);
				mappedBuff.position(lastPhylSeq.getEndPointer() + 1);

				// and now append the inteleaved sequences
				while(true){
					long interleavedStartPointer = readerHelper.posOfNextNonWhiteChar();
					//logger.info("interleavedStartPointer" + interleavedStartPointer);				
					long interleavedEndLinePointer = readerHelper.posOfNextNewline() - newlineLen;
					//logger.info("interleavedEndLinePointer" + interleavedEndLinePointer);					
					long interleavedNextStartPointer = readerHelper.posOfNextNonWhiteChar();
					//logger.info("interleavedNextStartPointer" + interleavedNextStartPointer);				
					long lengthBetweenTwoInterleaveStartPointer = interleavedNextStartPointer - interleavedStartPointer -1;
					int nextSeqPartStartPos = allSeqs.get(0).getLength(); // length is one more already than pointer
					//logger.info("nextSeqPartStartPos" + nextSeqPartStartPos);
					int seqSeqmentLen = (int) (interleavedEndLinePointer - interleavedStartPointer +1);
					//logger.info("seqSeqmentLen" + seqSeqmentLen);
					for(int n = 0; n < seqCount; n++){
						PhylipFileSequence appendSeq = (PhylipFileSequence) allSeqs.get(n);
						appendSeq.add(new PositionToPointer(nextSeqPartStartPos,nextSeqPartStartPos + seqSeqmentLen -1, interleavedStartPointer, interleavedEndLinePointer));

						interleavedStartPointer = interleavedStartPointer + lengthBetweenTwoInterleaveStartPointer +1;
						interleavedEndLinePointer = interleavedStartPointer + (seqSeqmentLen -1);
					}
					
					// break when full
					//					logger.info("allSeqs.get(0).getLength()" + allSeqs.get(0).getLength());
					//					logger.info("longestSequenceLength" + longestSequenceLength);
					if(allSeqs.get(0).getLength() >= longestSequenceLength){
						logger.info("done indexing");
						break;
					}else{
						FileSequence lastSeq = (FileSequence) allSeqs.get(allSeqs.size()-1);
						mappedBuff.position(lastSeq.getEndPointer() + 1);
					}
					
					lineCount ++;
					if(lineCount % 1000 == 0){
						progressWin.setMessage("Indexing interleaved Phylip file" + LF + "line:" + lineCount);
						if(progressWin.wasSubThreadInterruptedByUser()){
							break;
						}
					}

				}
			}

			// load depending on file type
			if(importerType == PhylipImporter.SHORT_NAME_INTERLEAVED){

				long nameStartPointer = firstNameStartPointer;
				readerHelper.setPosition(nameStartPointer);
				int lineCount = 0;

				for(int n = 0; n <seqCount; n++){
					int seqPos = 0;
					// position sequence start (also name endpos)
					readerHelper.setPosition(nameStartPointer);
					// This row is the only difference between SHORT AND LONG NAME INTERLEAVED
					long seqStartPointer = nameStartPointer + 10;
					// end of first line
					long firstNewlinePointer = readerHelper.posOfNextNewline();
					// and number of non-char positions 
					//						spaces = readerHelper.countSpaceBetween(seqStartPos, firstNewlinePos);
					//						if(spaces > 0){
					//							logger.info("spaces" + spaces);
					//						}	

					PhylipFileSequence seq = new PhylipFileSequence(sequencesFile, nameStartPointer);


					String name = readerHelper.readString(nameStartPointer, seqStartPointer - 1);
					name = name.trim();
					seq.setName(name);
					int seqSeqmentLen = (int) (firstNewlinePointer - newlineLen - seqStartPointer + 1);	
					seq.add(new PositionToPointer(seqPos,seqPos + seqSeqmentLen -1, seqStartPointer, firstNewlinePointer - newlineLen));

					nameStartPointer = firstNewlinePointer + newlineLen;

					allSeqs.add(seq);

					lineCount ++;
					if(lineCount % 1000 == 0){
						progressWin.setMessage("Indexing interleaved Phylip file" + LF + "line:" + lineCount);
						if(progressWin.wasSubThreadInterruptedByUser()){
							break;
						}
					}
				}
				
				FileSequence lastSeq = (FileSequence) allSeqs.get(allSeqs.size()-1);
				mappedBuff.position(lastSeq.getEndPointer() + 1);

				// and now append the inteleaved sequences
				while(true){
					long interleavedStartPointer = readerHelper.posOfNextNonWhiteChar();
					//logger.info("interleavedStartPointer" + interleavedStartPointer);				
					long interleavedEndLinePointer = readerHelper.posOfNextNewline() - newlineLen;
					//logger.info("interleavedEndLinePointer" + interleavedEndLinePointer);					
					long interleavedNextStartPointer = readerHelper.posOfNextNonWhiteChar();
					//logger.info("interleavedNextStartPointer" + interleavedNextStartPointer);				
					long lengthBetweenTwoInterleaveStartPointer = interleavedNextStartPointer - interleavedStartPointer -1;
					int nextSeqPartStartPos = allSeqs.get(0).getLength(); // length is one more already than pointer
					//logger.info("nextSeqPartStartPos" + nextSeqPartStartPos);
					int seqSeqmentLen = (int) (interleavedEndLinePointer - interleavedStartPointer +1);
					//logger.info("seqSeqmentLen" + seqSeqmentLen);
					for(int n = 0; n < seqCount; n++){
						PhylipFileSequence appendSeq = (PhylipFileSequence) allSeqs.get(n);
						appendSeq.add(new PositionToPointer(nextSeqPartStartPos,nextSeqPartStartPos + seqSeqmentLen -1, interleavedStartPointer, interleavedEndLinePointer));

						interleavedStartPointer = interleavedStartPointer + lengthBetweenTwoInterleaveStartPointer +1;
						interleavedEndLinePointer = interleavedStartPointer + (seqSeqmentLen -1);
					}

					// break when full
					//					logger.info("allSeqs.get(0).getLength()" + allSeqs.get(0).getLength());
					//					logger.info("longestSequenceLength" + longestSequenceLength);
					if(allSeqs.get(0).getLength() >= longestSequenceLength){
						logger.info("done indexing");
						break;
					}else{
						
						FileSequence theLastSeq = (FileSequence) allSeqs.get(allSeqs.size()-1);
						mappedBuff.position(theLastSeq.getEndPointer() + 1);
						
					}
					
					
					lineCount ++;
					if(lineCount % 1000 == 0){
						progressWin.setMessage("Indexing interleaved Phylip file" + LF + "line:" + lineCount);
						if(progressWin.wasSubThreadInterruptedByUser()){
							break;
						}
					}

				}
			}

			// load depending on file type
			if(importerType == PhylipImporter.LONG_NAME_SEQUENTIAL){

				logger.info("PhylipImporter.LONG_NAME_SEQUENTIAL");

				// get all names and initial sequences
				long nameStartPointer = firstNameStartPointer;
				readerHelper.setPosition(nameStartPointer);

				// position sequence start (also name endpos)
				long seqStartPointer = readerHelper.posOfFirstNonWhiteCharAfterWhiteChar();

				// endpointer
				long sequentialEndPointer = readerHelper.posAtNSequenceCharacters(seqStartPointer, longestSequenceLength);

				// sequence-length-in-pointers
				int seqSeqmentLen = (int) (sequentialEndPointer - seqStartPointer + 1);	// length is +1


				for(int n = 0; n <seqCount; n++){
					int seqPos = 0;

					mappedBuff.position(nameStartPointer);
					seqStartPointer = readerHelper.posOfFirstNonWhiteCharAfterWhiteChar();

					// just calculate - don't read
					sequentialEndPointer = seqStartPointer + seqSeqmentLen -1;

					PhylipFileSequence seq = new PhylipFileSequence(sequencesFile, nameStartPointer);

					String name = readerHelper.readString(nameStartPointer, seqStartPointer - 1);
					name = name.trim();
					seq.setName(name);

					seq.add(new PositionToPointer(seqPos,seqPos + seqSeqmentLen -1, seqStartPointer, sequentialEndPointer));

					allSeqs.add(seq);

					// move forward
					nameStartPointer = sequentialEndPointer + newlineLen + 1;
					
					if(n % 1000 == 0){
						progressWin.setMessage("Indexing Phylip sequential file" + LF + "seq:" + n + "/" + seqCount);
						if(progressWin.wasSubThreadInterruptedByUser()){
							break;
						}
					}
				}
			}

			// load depending on file type
			if(importerType == PhylipImporter.SHORT_NAME_SEQUENTIAL){

				logger.info("PhylipImporter.SHORT_NAME_SEQUENTIAL");

				// get all names and initial sequences
				long nameStartPointer = firstNameStartPointer;
				readerHelper.setPosition(nameStartPointer);

				// position sequence start (also name endpos)
				long seqStartPointer = nameStartPointer + 10;

				// endpointer
				long sequentialEndPointer = readerHelper.posAtNSequenceCharacters(seqStartPointer, longestSequenceLength);

				// sequence-length-in-pointers
				int seqSeqmentLen = (int) (sequentialEndPointer - seqStartPointer + 1);	// length is +1


				for(int n = 0; n <seqCount; n++){
					int seqPos = 0;

					mappedBuff.position(nameStartPointer);
					seqStartPointer = nameStartPointer + 10;

					// just calculate - don't read
					sequentialEndPointer = seqStartPointer + seqSeqmentLen -1;

					PhylipFileSequence seq = new PhylipFileSequence(sequencesFile, nameStartPointer);

					String name = readerHelper.readString(nameStartPointer, seqStartPointer - 1);
					name = name.trim();
					seq.setName(name);

					seq.add(new PositionToPointer(seqPos,seqPos + seqSeqmentLen -1, seqStartPointer, sequentialEndPointer));

					allSeqs.add(seq);

					// move forward
					nameStartPointer = sequentialEndPointer + newlineLen + 1;
					
					if(n % 1000 == 0){
						progressWin.setMessage("Indexing Phylip sequential file" + LF + "seq:" + n + "/" + seqCount);
						if(progressWin.wasSubThreadInterruptedByUser()){
							break;
						}
					}
				}
			}



		}catch(Exception exc){
			logger.info("could not read as phylip");
			exc.printStackTrace();
			throw new AlignmentImportException("Could not read phylip format");
		}

		return allSeqs;
	}
}
