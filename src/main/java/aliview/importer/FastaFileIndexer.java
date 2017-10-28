package aliview.importer;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.unimi.dsi.io.ByteBufferInpStream;
import aliview.sequencelist.MemoryMappedSequencesFile;
import aliview.sequences.FastaFileSequence;
import aliview.sequences.FileSequence;
import aliview.sequences.Sequence;
import aliview.subprocesses.SubThreadProgressWindow;

public class FastaFileIndexer implements FileIndexer{
	private static final Logger logger = Logger.getLogger(FastaFileIndexer.class);
	long estimateTotalSeqInFile = 0;
	long fileSize = -1;

	public ArrayList<Sequence> findSequencesInFile(MemoryMappedSequencesFile sequencesFile, long filePointerStart, int seqOffset, int nSeqsToRetrieve,
			SubThreadProgressWindow progressWin) {

		ByteBufferInpStream mappedBuff = sequencesFile.getMappedBuff();

		this.fileSize = mappedBuff.length();
		int nSeqCount = 0;
		ArrayList<Sequence> allSeqs = new ArrayList<Sequence>();
		for(int n = 0; n < nSeqsToRetrieve; n++){

			FileSequence seq = findSequenceInFile(sequencesFile, filePointerStart, seqOffset);
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

				int lastSeqIndex = seqOffset;	
				long lastSeqEndPointer = seq.getEndPointer();		
				long oneSeqFileSizeSize = (lastSeqEndPointer +1) / (lastSeqIndex + 1);
				estimateTotalSeqInFile = fileSize / oneSeqFileSizeSize;
				final int current = lastSeqIndex;
				progressWin.setMessage("Indexing file " + current + " out of ~" + estimateTotalSeqInFile);

			}

			if(progressWin.wasSubThreadInterruptedByUser()){
				Thread.currentThread().interrupt();
				break;
			}


			// TODO check if window is closed - then kill thread

			// if other thread is waiting for mapped buffer (e.g. main Thread, pause indexing for 200ms)
			// this is done by releasing lock and sleeping a short while
			if(sequencesFile.getMappedBuffLock().hasQueuedThreads()){
				sequencesFile.getMappedBuffLock().unlock();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sequencesFile.getMappedBuffLock().lock();
			}

		}

		return allSeqs;
	}

	public FileSequence findSequenceInFile(MemoryMappedSequencesFile sequencesFile, long filePointerStart, int seqOffset){
		StringBuilder name = new StringBuilder();
		FileSequence sequence = null;
		boolean bytesUntilNextLFAreName = false;
		byte nextByte;
		ByteBufferInpStream mappedBuff = sequencesFile.getMappedBuff();
		mappedBuff.position(filePointerStart);
		int lineLength = 0;

		while ((nextByte = (byte)mappedBuff.read()) != -1) {

			boolean findNextLF = false;

			// Find name start
			if(nextByte == '>' && bytesUntilNextLFAreName == false){	

				// save and return last seq
				if(sequence != null){
					sequence.setEndPointer(mappedBuff.position() -2); // remove > and LF
					return sequence;
				}

				// start new one
				name = new StringBuilder(250);
				sequence = new FastaFileSequence(sequencesFile, mappedBuff.position()); // skip >		
				bytesUntilNextLFAreName = true;

			}

			// line feed - end of name
			if((nextByte == '\n')){
				if(bytesUntilNextLFAreName){
					// take care of name
					sequence.setName(name.toString());
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

			lineLength ++;

			// build name
			if(bytesUntilNextLFAreName){
				name.append((char) nextByte);
			}

		}

		// EOF
		if(nextByte == -1){
			if(sequence != null){
				logger.info("EOF=" + mappedBuff.position());
				//logger.info("sequence.getStartPointer()" + sequence.getStartPointer());
				sequence.setEndPointer(mappedBuff.position() - 1); // remove EOF
			}
		}

		return sequence;
	}

}
