package aliview.sequenceold;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

import aliview.FileFormat;
import aliview.sequences.FileSequence;
import aliview.sequences.Sequence;

public class OldFileSequenceList implements List<Sequence>{
	
	private FileFormat fileFormat;
	private File aliFile;
	private int estimatedSize;
	private SequenceCache seqCache = new SequenceCache(1000);
	
	public OldFileSequenceList(File aliFile) {
		this.aliFile = aliFile;	
		try {
			createFileSequences(0,1000,2000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createFileSequences(int firstIndex, int lastIndex, int seqLength) throws IOException {
		
		OptimizedRandomAccessFile raf = new OptimizedRandomAccessFile(aliFile, "r");
		List<FileSequence> seqs = findSequencesInFile(raf);	
		estimatedSize = seqs.size();	
		for(int n = 0; n < seqs.size(); n++){
			seqCache.put( new Integer(n) ,seqs.get(n) );
		}
	}
	
	private ArrayList<FileSequence> findSequencesInFile(OptimizedRandomAccessFile raf) throws IOException{
	
		long startTime = System.currentTimeMillis();
		
		ArrayList<FileSequence> allSeqs = new ArrayList<FileSequence>();
		String line = "";
		long nSeqCount = 0;
	
		byte[] buffer = new byte[100];
	
		long startPos = 0;
		
//		StringBuilder name = new StringBuilder();
		boolean bytesUntilNextLFAreName = false;
		FileSequence sequence = null;
		while ((raf.read(buffer)) > 0) {
				
				long filePoint = raf.getFilePointer();
				boolean findNextLF = false;
				
				int n = 0;
				
				while(n<buffer.length){
					
					//System.out.println("buffer[n]" + buffer[n]);
					
					// Find name start
					if(buffer[n] == '>'){	
						
						startPos = filePoint - n;
						
						// save last seq and start a new one
						if(sequence != null){
//							sequence.addName(name.toString());
//							name = new StringBuilder('>');
							bytesUntilNextLFAreName = true;
//							sequence.setNextSeqStartPos(startPos);
//							allSeqs.add(sequence);
						}
//						sequence = new FileSequence(raf, startPos);
						
						//raf.seek(filePoint + 48000 *10);
						//n = 100000;
						
						
						
					}
					
					
					if(buffer[n] == '\n'){
						raf.seek(filePoint + 48000);
						
							nSeqCount ++;
							if(nSeqCount % 1000 == 1){
								//System.out.println("n" + n);
								System.out.println("nSeqCount" + nSeqCount);
								System.out.println("raf.getFilePointer()" + raf.getFilePointer());
							}
							
						
						n = 100000;
					}
					
					if(bytesUntilNextLFAreName){
//						name.append((char) buffer[n]);
					}
		
					n++;
				
				}
				
			//	System.out.println("nSeqCount" + nSeqCount);
			//	System.out.println("raf.getFilePointer()" + raf.getFilePointer());


		}
		
		// Skip adding the last seq for now
		//long endPos = raf.length();
		//sequence.addNextSeqStartPos(startPos);
	
		
		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");
		
		return allSeqs;
	}

	public byte getByteInFile(int pos) {
//		byte[] val = new byte[1];
//		mappedBuff.read(val,pos,1);
		return 66;
	}

	public int size() {
		return estimatedSize;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public Iterator<Sequence> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean add(Sequence e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addAll(Collection<? extends Sequence> c) {
		return false;
	}

	public boolean addAll(int index, Collection<? extends Sequence> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
	}

	public Sequence get(int index) {
		return seqCache.get(new Integer(index));
	}

	public Sequence set(int index, Sequence element) {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(int index, Sequence element) {
		// TODO Auto-generated method stub
	}

	public Sequence remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ListIterator<Sequence> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public ListIterator<Sequence> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Sequence> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 private class SequenceCache extends LinkedHashMap<Integer,Sequence> {
		    private int cacheSize;

		    public SequenceCache (int size) {
		      cacheSize = size;
		    }
		    /*
		    protected boolean removeEldestEntry(Entry<Integer, Record> eldest) {
		      if (size() > ldcSize) {
		        // Release some memory
		        eldest.getValue().columnData = null;
		        return true;
		      }
		      return false;
		    }
		    */
		  }
	
}
