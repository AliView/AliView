package aliview.importer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.unimi.dsi.io.ByteBufferInpStream;
import aliview.sequences.FileSequence;
import aliview.sequences.NexusFileSequence;
import aliview.sequences.NexusSequence;
import aliview.sequences.PhylipFileSequence;
import aliview.sequences.PositionToPointer;
import aliview.sequences.Sequence;
import aliview.subprocesses.SubThreadProgressWindow;

public class FastNexusImporterSlow{
	private static final Logger logger = Logger.getLogger(FastNexusImporterSlow.class);
//	long estimateTotalSeqInFile = 0;
//	long fileSize = -1;
	File alignmentFile;

	public FastNexusImporterSlow(File alignmentFile) throws FileNotFoundException, IOException {
		this.alignmentFile = alignmentFile;
	}
	
	public List<Sequence> importSequences() throws AlignmentImportException {

		ArrayList<Sequence> allSeqs = new ArrayList<Sequence>();
		try{
			ByteBufferInpStream mappedBuff = ByteBufferInpStream.map(new FileInputStream(alignmentFile).getChannel(),FileChannel.MapMode.READ_ONLY );
	//		this.fileSize = mappedBuff.length();
			int longestSequenceLength = 0;
			//mappedBuff.position(0);

			logger.info("Nexus file ix");

			MappedBuffReaderHelper readerHelper = new MappedBuffReaderHelper(mappedBuff);
			ArrayList<String> allTokens = readerHelper.readAllNexusTokensUntil("MATRIX");

			for(int n = 0; n < allTokens.size(); n ++){
				String token = allTokens.get(n);
				logger.info(token);
			}

			// determine file type
			int NEXUS_TYPE_SEQUENTIAL = 0;
			int NEXUS_TYPE_INTERLEAVED = 1;

			int importerType = NEXUS_TYPE_SEQUENTIAL;	

			if(tokenExists(allTokens,"interleave")){
				importerType = NEXUS_TYPE_INTERLEAVED;
				String token = tokenAfter(allTokens, "interleave");
				if(token != null){
					if(StringUtils.equalsIgnoreCase(token, "NO")){
						importerType = NEXUS_TYPE_SEQUENTIAL;
					}
				}
			}

			int nChar = 0;
			String value = tokenAfter(allTokens, "nchar");
			if(value != null){
				nChar = Integer.parseInt(value);
			}

			int nTax = 0;
			value = tokenAfter(allTokens, "ntax");
			if(value != null){
				nTax = Integer.parseInt(value);
			}

			// load depending on file type
			if(importerType == NEXUS_TYPE_SEQUENTIAL){

				for(int n = 0; n < nTax; n ++){

					String name = readerHelper.readNextNexusSeqName();

	//				logger.info("name=" + name);
					
					
					long seqStartPointer = readerHelper.posOfNextNonWhiteNexusChar();
					long seqEndPointer = readerHelper.getPosOfNonWhiteNexusCharacter(nChar - 1); // minus one because first is already read when loooking for startpoint

					byte[] seqAsBytes = readerHelper.getBytesBetween(seqStartPointer, seqEndPointer);
					
					NexusSequence seq = new NexusSequence(name, seqAsBytes);
					allSeqs.add(seq);
					
					readerHelper.setPosition(seqEndPointer + 1);
							
				}
			}

			// load depending on file type
			if(importerType == NEXUS_TYPE_INTERLEAVED){
				
				logger.info("NEXUS_TYPE_INTERLEAVED");
			/*	
				// first lines
				for(int n = 0; n < nTax; n ++){

					String name = readerHelper.readNextNexusSeqName();
					//logger.info(name);
					long nameStartPointer = mappedBuff.position() - name.length();

					long seqStartPointer = readerHelper.posOfNextNonWhiteNexusChar();
					long seqEndPointer = readerHelper.posOfNextNewline() - 1; // minus one because we dont want newline

					NexusFileSequence seq = new NexusFileSequence( fileMMSequenceList, seqOffset + n, nameStartPointer);
					seq.addName(name);

					seq.add(new PositionToPointer(0, (int)(seqEndPointer - seqStartPointer), seqStartPointer, seqEndPointer));
					allSeqs.add(seq);
	
				}
				
				// loop until all characters are found
				while(allSeqs.get(0).getLength() < nChar){
					
					for(int n = 0; n < nTax; n ++){
						
						String name = readerHelper.readNextNexusSeqName();
						long nameStartPointer = mappedBuff.position() - name.length();

						long segmentStartPointer = readerHelper.posOfNextNonWhiteNexusChar();
						long segmentEndPointer = readerHelper.posOfNextNewline(); // minus one because we dont want newline
						
						NexusFileSequence appendSeq = (NexusFileSequence) allSeqs.get(n);
						int segmentLength = (int)(segmentEndPointer - segmentStartPointer);
						int segmentStartPos = appendSeq.getLength(); // startpos (because pos start at 0 so length is next startPos)
						int segmentEndPos = segmentStartPos + segmentLength - 1; // -1 because seqment length and seqm start pos otherwise is one to much
							
						appendSeq.add(new PositionToPointer(segmentStartPos, segmentEndPos, segmentStartPointer, segmentEndPointer));									
					}
				}	
				
					*/
			}


		}catch(Exception exc){
			logger.info("could not read as nexus");
			exc.printStackTrace();
			throw new AlignmentImportException("Could not read nexus format");
		}

		return allSeqs;
	}

	private int tokenIndex(ArrayList<String> allTokens, String target) {
		for(int n = 0; n < allTokens.size(); n++){
			String token = allTokens.get(n);
			if(StringUtils.equalsIgnoreCase(token, target)){
				return n;
			}
		}
		return -1;
	}

	private boolean tokenExists(ArrayList<String> allTokens, String target) {
		if(tokenIndex(allTokens, target) > -1){
			return true;
		}else{
			return false;
		}
	}

	private String tokenAfter(ArrayList<String> allTokens, String target) {
		int index = tokenIndex(allTokens, target);
		if(index > -1 && allTokens.size() > index){
			return allTokens.get(index + 1);
		}
		else{
			return null;
		}
	}
}
