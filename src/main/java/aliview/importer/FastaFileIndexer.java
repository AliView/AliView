package aliview.importer;

import java.util.ArrayList;

import it.unimi.dsi.io.ByteBufferInpStream;
import aliview.FileFormat;
import aliview.sequencelist.FileMMSequenceList;
import aliview.sequencelist.MappedBuffReaderHelper;
import aliview.sequences.FastaFileSequence;
import aliview.sequences.FileSequence;
import aliview.subprocesses.SubThreadProgressWindow;

public class FastaFileIndexer implements FileIndexer{
	long estimateTotalSeqInFile = 0;
	long fileSize = -1;

	public ArrayList<FileSequence> findSequencesInFile(ByteBufferInpStream mappedBuff, long filePointerStart, int seqOffset, int nSeqsToRetrieve,
			SubThreadProgressWindow progressWin, FileMMSequenceList fileMMSequenceList) {
		
		this.fileSize = mappedBuff.length();
		int nSeqCount = 0;
		ArrayList<FileSequence> allSeqs = new ArrayList<FileSequence>();
		for(int n = 0; n < nSeqsToRetrieve; n++){

			FileSequence seq = findSequenceInFile(mappedBuff, filePointerStart, seqOffset, fileMMSequenceList);
			if(seq == null){
				break;
			}		

			long seqLength = seq.getLength();

			allSeqs.add(seq);
			seqOffset ++;
			filePointerStart = seq.getEndPointer() + 1;
			nSeqCount ++;

			int MESSAGE_FREQUENCE = 1;
			if(estimateTotalSeqInFile > 500){
				MESSAGE_FREQUENCE = 100;
			}
			if(estimateTotalSeqInFile > 5000){
				MESSAGE_FREQUENCE = 1000;
			}
			if(nSeqCount % MESSAGE_FREQUENCE == 0 && nSeqCount > 1){

				int lastSeqIndex = seq.getIndex();	
				long lastSeqEndPointer = seq.getEndPointer();		
				long oneSeqFileSizeSize = (lastSeqEndPointer +1) / (lastSeqIndex + 1);
				estimateTotalSeqInFile = fileSize / oneSeqFileSizeSize;
				final int current = nSeqCount;
				progressWin.setMessage("Indexing file " + current + "/" + nSeqsToRetrieve + "\n" +
				                        "Total seq. in file ca: " + estimateTotalSeqInFile + "\n" +
				                        "\n" + 
				                        "- you index the rest under Menu " + "\n" + 
				                        "\"Load more sequences from file\""	+ "\n" + 
				                        "\n" + 
						                "- you can change number of sequences to " + "\n" + 
				                        " be indexed at once in program Preferences.");
				                        		

			}

			if(progressWin.wasSubThreadInterruptedByUser()){
				break;
			}
		}

		return allSeqs;
	}

	public synchronized FileSequence findSequenceInFile(ByteBufferInpStream mappedBuff, long filePointerStart, int seqOffset, FileMMSequenceList seqList){
		StringBuilder name = new StringBuilder();
		FileSequence sequence = null;
		boolean bytesUntilNextLFAreName = false;
		byte nextByte;
		mappedBuff.position(filePointerStart);

		while ((nextByte = (byte)mappedBuff.read()) != -1) {

			boolean findNextLF = false;

			// Find name start
			if(nextByte == '>'){	

				// save and return last seq
				if(sequence != null){
					sequence.setEndPointer(mappedBuff.position() -2); // remove > and LF
					return sequence;
				}

				// start new one
				name = new StringBuilder(250);
				sequence = new FastaFileSequence(seqList, seqOffset, mappedBuff.position()); // skip >		
				bytesUntilNextLFAreName = true;

			}

			// line feed - end of name
			if((nextByte == '\n')){
				if(bytesUntilNextLFAreName){
					// take care of name
					sequence.addName(name.toString());
					sequence.setSequenceAfterNameStartPointer(mappedBuff.position() + 1); // exlude LF
					bytesUntilNextLFAreName = false;
					// jump over sequence to next name if possible 

					//					if(seekOffset > 0){			
					//						seekStartPos = mappedBuff.position();
					//						seekToPos = seekStartPos + seekOffset + 1;
					//						mappedBuff.position(seekToPos);
					//						// if next pos not is newline then sequences are not aligned and we
					//						// go back and loop through all positions
					//						byte checkByte =(byte) mappedBuff.read();
					//						if(checkByte != '\n' && checkByte != '\r'){
					//							// rewind
					//							mappedBuff.position(seekStartPos);
					//							seekOffset = 0;
					//						}		
					//					}

				}
			}

			// build name
			if(bytesUntilNextLFAreName){
				name.append((char) nextByte);
			}			
		}

		// EOF
		if(nextByte == -1){
			if(sequence != null){
				System.out.println("EOF");
				sequence.setEndPointer(mappedBuff.position() - 1); // remove EOF
			}
		}

		return sequence;
	}

}
