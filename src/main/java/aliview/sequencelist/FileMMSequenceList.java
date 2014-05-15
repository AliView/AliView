package aliview.sequencelist;

import it.unimi.dsi.io.ByteBufferInpStream;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Logger;

import utils.DialogUtils;
import aliview.AliView;
import aliview.AliViewWindow;
import aliview.FileFormat;
import aliview.externalcommands.ExternalCommandExecutor;
import aliview.importer.AlignmentImportException;
import aliview.importer.FileImportUtils;
import aliview.sequences.FastaFileIndexer;
import aliview.sequences.FastaFileSequence;
import aliview.sequences.FileIndexer;
import aliview.sequences.FileSequence;
import aliview.sequences.IndexFileReader;
import aliview.sequences.PhylipFileIndexer;
import aliview.sequences.Sequence;
import aliview.settings.Settings;
import aliview.subprocesses.SubProcessWindow;
import aliview.subprocesses.SubThreadProgressWindow;

public class FileMMSequenceList implements List<Sequence>{
	private static final Logger logger = Logger.getLogger(FileMMSequenceList.class);
	private FileFormat fileFormat;
	private File aliFile;
	//private MappedByteBuffer mappedBuff;
	private ByteBufferInpStream mappedBuff;
	private List<Sequence> seqList = Collections.synchronizedList(new ArrayList<Sequence>()); //new SequenceListCache(0); // TODO maybe synchronize this list
	private FilePage currentPage = null;
	private FileSequence lastCachedSeq;
	private SequenceListModel sequenceListModel;
	private long fileSize = -1;
	private int totalSeqCount = -1;
	private int nSequencesPerPage = Settings.getLargeFileIndexing().getIntValue();
	ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
	private ArrayList<FilePage> pages;
	private long seekOffset;
	private long seekStartPos;
	private long seekToPos;
	
	
	public FileMMSequenceList(File aliFile, FileFormat foundFormat) throws IOException {
		this.aliFile = aliFile;
		this.fileFormat = foundFormat;
		
		// check if indexfile exists
		File indexFile = new File(aliFile.getAbsolutePath() + ".fai");
		if(foundFormat == FileFormat.FASTA && indexFile.exists()){
			ArrayList<FileSequence> seqs = IndexFileReader.createSequences(indexFile, this);
			
			// create memory mapped buffer
			if(mappedBuff == null){
				createMemoryMappedBuffer();	
			}

			if(pages == null){
				if(seqs.size() > 0){
					lastCachedSeq = seqs.get(seqs.size() - 1);
					pages = getFilePages();
					// set SeqList
					seqList = pages.get(0).seqList;
				}
			}

			// add sequences to cache
			addSequencesToCache(seqs);

			// fire contents changed
			fireContentsChanged(this);		
		}
		else{
		
			// first estimate number of sequences and number of sequences to read at once and how many pages that would be	
			// read and split large file info pages if needed
			if(fileFormat == FileFormat.PHYLIP){
				nSequencesPerPage = 1000000;
			}else{
				
			}
			findAndAddSequencesToCacheInSubthread(new FilePage(0,aliFile, Collections.synchronizedList(new ArrayList<Sequence>()),0,nSequencesPerPage,0,-1,nSequencesPerPage));
		}
	}
	
	public void loadMoreSequencesFromFile(FilePage page) {
		findAndAddSequencesToCacheInSubthread(page);
	}

	private void findAndAddSequencesToCacheInSubthread(final FilePage page){
		// switchPage
		if(page != null){
			currentPage = page;
			seqList = page.seqList;
		}
		
		if(seqList.size() > 0){
			fireContentsChanged(this);
			
		}else{
			
			final SubThreadProgressWindow progressWin = new SubThreadProgressWindow();
			progressWin.setAlwaysOnTop(true);
			progressWin.setTitle("Indexing");
			progressWin.setMessage("Indexing file: " + 0 + "/" + page.nMaxSeqsToRetrieve);	
			progressWin.show();
			progressWin.centerLocationToThisComponentOrScreen(AliView.getActiveWindow());
			
			try{	
				final Thread thread = new Thread(new Runnable(){
					
					public void run(){
						try {
							// The standard JAVA-MappedFileBuffer, but it is limited to 2GB files
							// mappedBuff = new FileInputStream(aliFile).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, aliFile.length()); 
							// This is extended version - any size files
							if(mappedBuff == null){
								progressWin.setTitle("Mapping file");
								progressWin.setMessage("Mapping file - usually takes about 0-15sek" );
								progressWin.setVisible(true);
								createMemoryMappedBuffer();	
							}
												
							List<FileSequence> seqs = findSequencesInFile(page.startPointer,page.startIndex,page.nMaxSeqsToRetrieve, progressWin);
							
							if(pages == null){
								if(seqs.size() > 0){
									lastCachedSeq = seqs.get(seqs.size() - 1);
									pages = getFilePages();
									// set SeqList
									seqList = pages.get(0).seqList;
								}
							}
							
							// save index-file
							
							// add seqs to cache
							addSequencesToCache(seqs);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						// loading is done the new thread should activate GUI again before it is finished
						SwingUtilities.invokeLater(new Runnable() {
							public void run(){
								boolean wasThreadInterruptedByUser = progressWin.wasSubThreadInterruptedByUser();
								progressWin.dispose();
								fireContentsChanged(this);
								// unlock window
								AliViewWindow.getAliViewWindowGlassPane().setVisible(false);
							}

						});
					}
				});
				// Lock GUI while second thread is working
				progressWin.setActiveThread(thread);
				thread.start();
				AliViewWindow.getAliViewWindowGlassPane().setVisible(true);
			} catch (Exception e) {
				// unlock window
				AliViewWindow.getAliViewWindowGlassPane().setVisible(false);
				progressWin.dispose();
				fireContentsChanged(this);
				e.printStackTrace();
			}
		}
	}
	
	// TODO close buffer maybe? When alignment is changed?
	protected void createMemoryMappedBuffer() throws FileNotFoundException, IOException {
		mappedBuff = ByteBufferInpStream.map(new FileInputStream(aliFile).getChannel(),FileChannel.MapMode.READ_ONLY );
	}
	
	private synchronized List<FileSequence> findSequencesInFile(long filePointerStart, int seqOffset, final int nSeqsToRetrieve, final SubThreadProgressWindow progressWin){
		long startTime = System.currentTimeMillis();
		
		int nSeqCount = 0;
		
		ArrayList<FileSequence> allSeqs = new ArrayList<FileSequence>();
		if(this.fileFormat == FileFormat.PHYLIP){
			
			try {
				PhylipFileIndexer fileIndexer = new PhylipFileIndexer();
				allSeqs = fileIndexer.findSequencesInFile(mappedBuff, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin, this);
			} catch (AlignmentImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			FastaFileIndexer fileIndexer = new FastaFileIndexer();
			allSeqs = fileIndexer.findSequencesInFile(mappedBuff, filePointerStart, seqOffset, nSeqsToRetrieve, progressWin, this);
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");	
		return allSeqs;
	}
	
	
	private synchronized void addSequencesToCache(List<FileSequence> seqs){

		for(int n = 0; n < seqs.size(); n++){
			//seqCache.put(new Integer(seqs.get(n).getSeqIndex()) ,seqs.get(n) );
			seqList.add(seqs.get(n));
		}

		if(seqs.size() > 0){
			this.lastCachedSeq = seqs.get(seqs.size() - 1);
		}

	}
	
	public ArrayList<FilePage> getFilePages() {
	
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

	public synchronized byte readByteInFile(long pos) {
		return (byte) readInFile(pos);
	}
	
	public synchronized int readInFile(long pos) {
		if(pos < 0){
			return 0;
		}
		mappedBuff.position(pos);
		return mappedBuff.read();
	}
	
	public ByteBufferInpStream getMappedBuff() {
		return mappedBuff;
	}
	
	public synchronized int readBytesInFile(long pos, int i, byte[] bytesToDraw) {
		mappedBuff.position(pos);
		return mappedBuff.read(bytesToDraw,0,i);
	}

	public synchronized long getFileSize(){		
		if(fileSize == -1){
			fileSize = mappedBuff.length();
		}
		return fileSize;
	}

	public int size() {

		// return 400000;
		return seqList.size();

		//	return seqList.size() + extraUnloadedSeqs;


		/*		
		if(totalSeqCount == -1){
			return seqList.size() + extraUnloadedSeqs ;
		}
		else{
			return totalSeqCount;
		}
		 */		

		//return 10000;

		/*
		int index = lastCachedSeq.getSeqIndex();
		long pointer = lastCachedSeq.getSeqStartPointer();

		long seqFileSize = pointer / (index + 1);

		long estimate = getFileSize() / seqFileSize;

		return (int) estimate;
		 */

	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object[] toArray() {
		return seqList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return seqList.toArray(a);
	}

	public boolean add(Sequence e) {
		return seqList.add(e);
	}

	public boolean remove(Object o) {
		return seqList.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return seqList.containsAll(c);
	}

	public boolean addAll(Collection<? extends Sequence> c) {
		return seqList.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Sequence> c) {
		return seqList.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return seqList.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return seqList.retainAll(c);
	}

	public void clear() {
		seqList.clear();
	}

	// Arraylistversion
	public Sequence get(int index) {
		Sequence seq = seqList.get(index);	
		return seq;
	}

	public Sequence set(int index, Sequence element) {
		return seqList.set(index, element);
	}

	public void add(int index, Sequence element) {
		seqList.add(index, element);
	}

	public Sequence remove(int index) {
		return seqList.remove(index);
	}

	public int indexOf(Object o) {
		return seqList.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return seqList.lastIndexOf(o);
	}

	public List<Sequence> subList(int fromIndex, int toIndex) {
		return seqList.subList(fromIndex, toIndex);
	}

	public Iterator<Sequence> iterator() {
		return seqList.iterator();
	}

	public ListIterator<Sequence> listIterator() {
		return seqList.listIterator();
	}

	public ListIterator<Sequence> listIterator(int index) {
		return seqList.listIterator(index);
	}
	
	protected void fireContentsChanged(Object source){
		ListDataEvent e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, 0, seqList.size() - 1);
		for(ListDataListener listener: listeners){
			listener.contentsChanged(e);
		}
	}

	public void addListDataListener(ListDataListener listener){
		if(listener == null || listeners.contains(listener)){
			return;
		}
		listeners.add(listener);
	}
	
	public List<Sequence> getCachedSequences() {
		return seqList;
	}

	public FilePage getActivePage() {
		return currentPage;
	}
	
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
