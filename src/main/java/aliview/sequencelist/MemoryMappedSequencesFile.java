package aliview.sequencelist;

import it.unimi.dsi.io.ByteBufferInpStream;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import utils.DialogUtils;
import aliview.AliView;
import aliview.AliViewWindow;
import aliview.FileFormat;
import aliview.externalcommands.ExternalCommandExecutor;
import aliview.importer.AlignmentImportException;
import aliview.importer.ClustalFileIndexer;
import aliview.importer.FastaFileIndexer;
import aliview.importer.FileImportUtils;
import aliview.importer.FileIndexer;
import aliview.importer.NotUsed_IndexFileReader;
import aliview.importer.MSFFileIndexer;
import aliview.importer.NexusFileIndexer;
import aliview.importer.PhylipFileIndexer;
import aliview.messenges.Messenger;
import aliview.sequences.FastaFileSequence;
import aliview.sequences.FileSequence;
import aliview.sequences.Sequence;
import aliview.settings.Settings;
import aliview.subprocesses.SubProcessWindow;
import aliview.subprocesses.SubThreadProgressWindow;

public class MemoryMappedSequencesFile{
	private static final Logger logger = Logger.getLogger(MemoryMappedSequencesFile.class);
	private static final String LF = System.getProperty("line.separator");
	private FileFormat fileFormat;
	private File alignmentFile;
	private ByteBufferInpStream mappedBuff;
	private ReentrantLock mappedBuffLock = new ReentrantLock();
//	private FileSequence lastCachedSeq;
	private long fileSize = -1;
//	ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
//	private ArrayList<FilePage> pages;


	public MemoryMappedSequencesFile(File aliFile, FileFormat foundFormat) throws IOException {
		this.alignmentFile = aliFile;
		this.fileFormat = foundFormat;

		logger.info("new FileMMSequnceList");
	}
	
	public ReentrantLock getMappedBuffLock() {
		return mappedBuffLock;
	}
	
	void indexFileAndAddSequencesToAlignmentModel(FileSequenceAlignmentListModel destinationModel) throws IOException{
		
		// check if index file exists
		File indexFile = new File(alignmentFile.getAbsolutePath() + ".fai");
		// read from index file if exists
		if(fileFormat == FileFormat.FASTA && indexFile.exists()){
			List<Sequence> seqs = createSequencesFromExistingIndexFile(indexFile);

			// create memory mapped buffer
			if(mappedBuff == null){
				createMemoryMappedBuffer();	
			}

			// add sequences to cache
			addSequencesToDestination(seqs, destinationModel);
		}
		// Otherwise index file
		else{
			indexFileAndAddSequencesToListInSubthread(destinationModel, fileFormat);
		}
	}
		
	
	
	public List<Sequence> createSequencesFromExistingIndexFile(File indexFile) {
		
		long startTime = System.currentTimeMillis();
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		
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
					
//					int startPos = 0;
//					int endPos = line.indexOf('\t',startPos);
//					
//					splitted[0] = line.substring(startPos, endPos);
//					startPos = endPos + 1;
//					endPos = line.indexOf('\t',startPos);
//					splitted[1] = line.substring(startPos, endPos);
//					startPos = endPos + 1;
//					endPos = line.indexOf('\t',startPos);
//					splitted[2] = line.substring(startPos, endPos);
//					startPos = endPos + 1;
//					endPos = line.indexOf('\t',startPos);
//					splitted[3] = line.substring(startPos, endPos);
//					startPos = endPos + 1;
//					endPos = line.indexOf('\t',startPos);
//					splitted[4] = line.substring(startPos, endPos);
					
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
					
					FileSequence seq = new FileSequence(this, seqIndex, seqName, seqWithoutWhitespaceLength, seqAfterNameStartPointer, endPointer, lineCharLength, lineAbsoluteLength);
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
	
	
	private void indexFileAndAddSequencesToListInSubthread(final AlignmentListModel destinationModel, final FileFormat fileFormat){
		
		final SubThreadProgressWindow progressWin = new SubThreadProgressWindow();
		progressWin.setAlwaysOnTop(true);
		progressWin.setTitle("Background indexing");
		progressWin.setInitialMessage("Indexing file: " + 0 + "/" + "number of sequences");
		progressWin.show();
		progressWin.centerLocationToThisComponentOrScreen(AliView.getActiveWindow());
		//progressWin.setTopRightRelativeThisComponent(AliView.getActiveWindow());
		progressWin.setBottomRightRelativeThisComponent(AliView.getActiveWindow());
		

			try{	
				final Thread thread = new Thread(new Runnable(){

					public void run(){
						try {
							logger.info("Indexing Thread started");
							int nMaxSeqsToRetrieveBeforeDestinationUpdate = 500;
							// These formats are possibly sequential and it is good to retrieve all seqs at once
							if(fileFormat == FileFormat.PHYLIP || fileFormat == FileFormat.NEXUS || fileFormat == FileFormat.CLUSTAL){
								nMaxSeqsToRetrieveBeforeDestinationUpdate = Integer.MAX_VALUE;
							}
							boolean hasMoreSequencesToIndex = true;
							FileSequence lastCachedSeq = null;
							int indexOffset = 0;
							while(hasMoreSequencesToIndex){
									
								// The standard JAVA-MappedFileBuffer, but it is limited to 2GB files
								// mappedBuff = new FileInputStream(aliFile).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, aliFile.length()); 
								// This is extended version - any size files
								if(mappedBuff == null){
									progressWin.setTitle("Mapping file");
								    progressWin.setMessage("Mapping file - usually takes about 0-15 sec." );
									progressWin.setVisible(true);

									createMemoryMappedBuffer();
									
									progressWin.setTitle("Background indexing");
									progressWin.setMessage("Indexing file: " + 0 + "/" + "number of sequences");
								}
								
								
								long startPointer = 0;
								if(lastCachedSeq != null){
									startPointer = lastCachedSeq.getEndPointer();
								}
								
								List<Sequence> moreSeqs = findSequencesInFile(startPointer,indexOffset,nMaxSeqsToRetrieveBeforeDestinationUpdate, progressWin);		
								logger.info("Thread here moreSeqs.size()" + moreSeqs.size());
								
								if(moreSeqs.size() > 0){
									addSequencesToDestination(moreSeqs, destinationModel);
									lastCachedSeq = (FileSequence) moreSeqs.get(moreSeqs.size() - 1);
									indexOffset += moreSeqs.size();
								}else{
									hasMoreSequencesToIndex = false;
								}
								
								if(Thread.interrupted()){
									break;
								}
								
//								// sleeep a while so file can be read by other thread
//								try {
//									logger.info("Thread sleep");
//									Thread.sleep(100);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
								
							}
							
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Messenger.showOKOnlyMessage(Messenger.FILE_OPEN_NOT_EXISTS,
									LF + e.getLocalizedMessage());	

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Messenger.showOKOnlyMessage(Messenger.FILE_ERROR,
									LF + e.getLocalizedMessage());
						}

						// loading is done the new thread should activate GUI again before it is finished
						SwingUtilities.invokeLater(new Runnable() {
							public void run(){
								boolean wasThreadInterruptedByUser = progressWin.wasSubThreadInterruptedByUser();
								progressWin.dispose();
								//fireContentsChanged(this);
								// unlock window
								// AliViewWindow.getAliViewWindowGlassPane().setVisible(false);
							}

						});
					}
				});
				// Lock GUI while second thread is working
				progressWin.setActiveThread(thread);
				thread.start();
				//				AliViewWindow.getAliViewWindowGlassPane().setVisible(true);
			} catch (Exception e) {
				// unlock window
				//				AliViewWindow.getAliViewWindowGlassPane().setVisible(false);
				progressWin.dispose();
	//			fireContentsChanged(this);
				e.printStackTrace();
			}
	}

	// TODO close buffer maybe? When alignment is changed?
	protected void createMemoryMappedBuffer() throws IOException{

		try {
			mappedBuff = ByteBufferInpStream.map(new FileInputStream(alignmentFile).getChannel(),FileChannel.MapMode.READ_ONLY );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			Messenger.showOKOnlyMessage(Messenger.OPEN_LARGE_FILE_ERROR, LF + e.getLocalizedMessage());

			e.getLocalizedMessage();
			throw e;
		}
	}

	private List<Sequence> findSequencesInFile(long filePointerStart, int seqOffset, final int nSeqsToRetrieve, final SubThreadProgressWindow progressWin){
		long startTime = System.currentTimeMillis();

		int nSeqCount = 0;

		ArrayList<Sequence> allSeqs = new ArrayList<Sequence>();
		if(this.fileFormat == FileFormat.PHYLIP){		
			try {
				PhylipFileIndexer fileIndexer = new PhylipFileIndexer();
				allSeqs = fileIndexer.findSequencesInFile(this, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin);
			} catch (AlignmentImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(this.fileFormat == FileFormat.NEXUS){
			try {
				NexusFileIndexer fileIndexer = new NexusFileIndexer();
				allSeqs = fileIndexer.findSequencesInFile(this, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin);
			} catch (AlignmentImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(this.fileFormat == FileFormat.CLUSTAL){
			try {
				ClustalFileIndexer fileIndexer = new ClustalFileIndexer();
				allSeqs = fileIndexer.findSequencesInFile(this, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin);
			} catch (AlignmentImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(this.fileFormat == FileFormat.MSF){
			try {
				MSFFileIndexer fileIndexer = new MSFFileIndexer();
				allSeqs = fileIndexer.findSequencesInFile(this, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin);
			} catch (AlignmentImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			FastaFileIndexer fileIndexer = new FastaFileIndexer();
			mappedBuffLock.lock();
				allSeqs = fileIndexer.findSequencesInFile(this, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin);
			mappedBuffLock.unlock();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");	
		return allSeqs;
	}


	private void addSequencesToDestination(final List<Sequence> moreSeqs, final AlignmentListModel destinationModel){

		logger.info("addSequencesToDestination");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				destinationModel.addAll(moreSeqs, false);
			}
		});
		
	}

	/*
	public List<FilePage> getFilePages() {

		if(pages == null && lastCachedSeq != null){

			pages = new ArrayList<FilePage>();		
			int nMaxPageSizeInSequences = nSequencesPerPage;
			int lastCachedIndex = lastCachedSeq.getIndex();
			long lastCachedEndPointer = lastCachedSeq.getEndPointer();	
			long oneSeqFileSizeSize = (lastCachedEndPointer +1) / (lastCachedIndex + 1);
			long fileSize = getFileSize();
			long estimateTotalSeqInFile = getFileSize() / oneSeqFileSizeSize;

			long pageFileSize = (lastCachedIndex + 1) * oneSeqFileSizeSize;
			long maxPageFileSize = nSequencesPerPage * oneSeqFileSizeSize;

			pageFileSize = Math.max(pageFileSize, maxPageFileSize);

			int nPages = (int)Math.round(fileSize/pageFileSize + 0.5); // add 0.5 to always round up

			logger.info("nPages" + nPages);

			long startPointer = 0;
			int startIndex = 0;
			long endPointer = startPointer + pageFileSize;
			int endIndex = startIndex + nSequencesPerPage;

			for(int n = 0; n < nPages; n++){			
				pages.add(new FilePage(n, aliFile, new ArrayList<Sequence>(), startIndex, endIndex, startPointer, endPointer, nMaxPageSizeInSequences));	
				startIndex = endIndex;
				endIndex = Math.min((int)estimateTotalSeqInFile, startIndex + nSequencesPerPage);
				startPointer = endPointer + 1;
				endPointer = Math.min(fileSize, startPointer + pageFileSize -1);
			}			
		}		
		return pages;	
	}
	*/

	public byte readByteInFile(long pos) {
		return (byte) readInFile(pos);
	}

	public int readInFile(long pos) {
		if(pos < 0){
			return 0;
		}
		
		mappedBuffLock.lock();
			mappedBuff.position(pos);
			int val = mappedBuff.read();
		mappedBuffLock.unlock();
		return val;

		
	}

	public ByteBufferInpStream getMappedBuff() {
		return mappedBuff;
	}

	public int readBytesInFile(long pos, int i, byte[] bytesToDraw) {
//		synchronized (mappedBuff) {
//			mappedBuff.position(pos);
//			return mappedBuff.read(bytesToDraw,0,i);
//		}
		
		mappedBuffLock.lock();
			mappedBuff.position(pos);
			int val = mappedBuff.read(bytesToDraw,0,i);
		mappedBuffLock.unlock();
	return val;
		
		
	}

	public long getFileSize(){	
//		synchronized (mappedBuff) {
//			if(fileSize == -1){
//				fileSize = mappedBuff.length();
//			}
//			return fileSize;
//		}
		
		mappedBuffLock.lock();
		if(fileSize == -1){
			fileSize = mappedBuff.length();
		}
		mappedBuffLock.unlock();
		return fileSize;
		
	}

	


	
//	public int size() {
//
//		// return 400000;
//		return seqList.size();
//
//		//	return seqList.size() + extraUnloadedSeqs;
//
//
//		/*		
//		if(totalSeqCount == -1){
//			return seqList.size() + extraUnloadedSeqs ;
//		}
//		else{
//			return totalSeqCount;
//		}
//		 */		
//
//		//return 10000;
//
//		/*
//		int index = lastCachedSeq.getSeqIndex();
//		long pointer = lastCachedSeq.getSeqStartPointer();
//
//		long seqFileSize = pointer / (index + 1);
//
//		long estimate = getFileSize() / seqFileSize;
//
//		return (int) estimate;
//		 */
//
//	}


}


/*
private synchronized List<FileSequence> findSequencesInFile(long filePointerStartPos, int seqOffset, final int nSeqsToRetrieve,
		                                                               final SubThreadProgressWindow progressWin){
	long startTime = System.currentTimeMillis();
	int nSeqCount = 0;
	ArrayList<FileSequence> allSeqs = new ArrayList<FileSequence>();
	long filePointerNextStartPos = filePointerStartPos;
	for(int n = 0; n < nSeqsToRetrieve; n++){
		FileSequence seq = findSequenceInFile(filePointerNextStartPos, seqOffset, progressWin);
		allSeqs.add(seq);
		seqOffset ++;
		filePointerNextStartPos = seq.getEndPointer();
		nSeqCount ++;

		if(nSeqCount % 1000 == 0){		
			final int current = nSeqCount;
			progressWin.setMessage("Indexing file " + current + "/" + nSeqsToRetrieve);
		}
		if(progressWin.wasSubThreadInterruptedByUser()){
			break;
		}			
	}	

	long endTime = System.currentTimeMillis();
	System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");
	return allSeqs;
}

 */
/*
private synchronized FileSequence findSequenceInFile(long filePointerStart, int seqOffset, final SubThreadProgressWindow progressWin){
//	long startTime = System.currentTimeMillis();
	int nSeqCount = 0;

	StringBuilder name = new StringBuilder();
	FileSequence sequence = null;

	boolean bytesUntilNextLFAreName = false;
	byte nextByte;

	readerHelper.setPosition(filePointerStart);

	long nameStartPos = 0;
	long endNamePos;
	long seqStartPos;
	long seqEndPos;

		nameStartPos = readerHelper.findNext((byte)'>');
		sequence = new FastaFileSequence(this, seqOffset, nameStartPos);
	//	endNamePos = readerHelper.appendBytesUntilNextLF(name);
		sequence.addName("Hej");
	//	seqStartPos = readerHelper.findNextNonControlChar();
//		sequence.setSequenceAfterNameStartPointer(seqStartPos);
		seqEndPos = readerHelper.findNextOrEOF((byte)'>');
		sequence.setEndPointer(seqEndPos);		


//	long endTime = System.currentTimeMillis();
//	System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

	return sequence;
}

 */


/*


private synchronized List<FileSequence> findSequenceInFile(long filePointerStart, int seqOffset, final int nSeqsToRetrieve, final SubThreadProgressWindow progressWin){

	long startTime = System.currentTimeMillis();

	ArrayList<FileSequence> allSeqs = new ArrayList<FileSequence>();

	int nSeqCount = 0;

	StringBuilder name = new StringBuilder();

	FileSequence sequence = null;

	boolean bytesUntilNextLFAreName = false;

	//long filePos = filePointerStart;
	byte nextByte;

	mappedBuff.position(filePointerStart);
	logger.info("mapBuffpositionbefore" + mappedBuff.position());

	if(mappedBuff.position() != filePointerStart){
		logger.info("filePointerStart" + filePointerStart);
	}

	while ((nextByte = (byte)mappedBuff.read()) != -1) {

		boolean findNextLF = false;

		// Find name start
		if(nextByte == '>'){	

			// save last seq and start a new one
			if(sequence != null){
				sequence.setEndPointer(mappedBuff.position() -2); // remove > and LF
				// sequence.setNextSeqStartPos(mappedBuff.position() - 1); // include >
				allSeqs.add(sequence);

				// calculate seek offset
				if(seekOffset == 0){
					seekOffset = sequence.getLength();
				}


//				if(sequence.getLength() > 19000){
//					logger.info("error");
//					logger.info("filePointerStart" + filePointerStart);
//					logger.info("mappedBuff.position()" + mappedBuff.position());
//					logger.info("index" + sequence.getIndex());
//					logger.info("seqstartpoint" + sequence.getStartPointer());
//					logger.info("seqendpoint" + sequence.getEndPointer());
//
//				}
//				

			}
			//					
			name = new StringBuilder('>');
			sequence = new FileSequence(this, seqOffset + nSeqCount, mappedBuff.position() -1); // include >

			bytesUntilNextLFAreName = true;
			nSeqCount ++;

			if(nSeqCount % 1000 == 0){		
				final int current = nSeqCount;
				progressWin.setMessage("Indexing file " + current + "/" + nSeqsToRetrieve);
			}

		}

		if((nextByte == '\n' || nextByte == '\r')){
			if(bytesUntilNextLFAreName){
				sequence.addName(name.toString());
				sequence.setSequenceAfterNameStartPointer(mappedBuff.position() + 1); // exlude LF
				bytesUntilNextLFAreName = false;
				// jump over sequence to next name if possible 
				if(seekOffset > 0){			
					seekStartPos = mappedBuff.position();
					seekToPos = seekStartPos + seekOffset + 1;
					mappedBuff.position(seekToPos);
					// if next pos not is newline then sequences are not aligned and we
					// go back and loop through all positions
					byte checkByte =(byte) mappedBuff.read();
					if(checkByte != '\n' && checkByte != '\r'){
						// rewind
						mappedBuff.position(seekStartPos);
						seekOffset = 0;
					}		
				}
			}
		}

		if(bytesUntilNextLFAreName){
			name.append((char) nextByte);
		}

		if(nSeqCount > nSeqsToRetrieve){
			System.out.println("Found " + nSeqCount + " seq, break");	
			break;
		}

		if(progressWin.wasSubThreadInterruptedByUser()){
			break;
		}

	}

	// EOF
	if(nextByte == -1){
		if(sequence != null){
			System.out.println("EOF");
			sequence.setEndPointer(mappedBuff.position() - 1); // remove EOF
		}
	}

	logger.info("mapBuffpositionafter" + mappedBuff.position());

	// Skip adding the last seq for now
	//long endPos = raf.length();
	//sequence.addNextSeqStartPos(startPos);


	long endTime = System.currentTimeMillis();
	System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");


	return allSeqs;
}

 */
