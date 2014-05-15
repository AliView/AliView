package aliview.test;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

import aliview.importer.AlignmentImportException;
import aliview.sequences.FastFastaSequence;
import aliview.sequences.Sequence;

public class FastFastaFileTest {
	private static final Logger logger = Logger.getLogger(FastFastaFileTest.class);

	private int longestSequenceLength;

	public static void main(String[] args) {

			FastFastaFileTest ffFileTest = new FastFastaFileTest();
				try {
					ffFileTest.importSequences();
				} catch (AlignmentImportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


	}
	
	public FastFastaFileTest() {
		
	}

	public static void importSequences() throws AlignmentImportException {

		long startTime = System.currentTimeMillis();
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		try {
			StringBuilder sequence = new StringBuilder();
			
			//File seqFile = new File("/home/anders/projekt/ormbunkar/analys/karin_alignment/ssu_pr2-99.fasta.diffenc2");
			File seqFile = new File("/vol2/big_data/SSURef_108_filtered_bacteria_pos_5389-24317.fasta");
			
			//RandomAccessFile raf = new RandomAccessFile(seqFile, "r");
			OptimizedRandomAccessFile raf = new OptimizedRandomAccessFile(seqFile, "r");

			
			//BufferedReader r = new BufferedReader(this.reader);
			String line = "";
			String name = null;
			int nLine = 0;
			long nSeqCount = 0;
			
			byte[] buffer = new byte[10000];
			
			while ((raf.read(buffer)) > 0) {
			//while ((line = raf.readLine()) != null) {
				
				line = line.trim();
				
				long filePoint = raf.getFilePointer();
				boolean findNextLF = false;
				for(int n = 0; n<buffer.length; n++){
					if(buffer[n] == '>'){	
						long startPos = filePoint - n;
						nSeqCount ++;
						findNextLF = true;
					}
					
					/*
					if((buffer[n] == '\n' || buffer[n] == '\r') && findNextLF){
						long endPos = filePoint - n;
						nSeqCount --;
						findNextLF = false;
					}
					*/
					
					n++;
					
				}
				
/*				
				if(nLine == 0){
					// if not fasta file then break
					
					if(line.length() > 0 && line.charAt(0) != '>'){
						// no fasta
						throw new AlignmentImportException("Fasta file should start with > character");
					}
					
				}
				
				if(line.length() > 0){

					if(line.charAt(0) == '>'){

						
					}
					else{
						
					}
				}
*/			
			
				nLine ++;
				
//				System.out.println("nLine" + nLine + "pointer" + raf.getFilePointer());
//				if(nLine > 100){
//				System.err.println("SystemExit");	
//				System.exit(1);
//				}
//				System.out.println(line);
				
				
//				if(nLine > 5){
//				System.err.println("SystemExit");	
//				System.exit(1);
//				}
				
				if(nSeqCount % 100 == 0){
					System.err.println("found seq" + nSeqCount);					
				}
				
				if(nLine % 10000 == 0){
					System.out.println("nLine" + nLine + "pointer" + raf.getFilePointer() + "nSecCount" + nSeqCount);
					
				}
			}
			
			
			
			
			
			// add last sequence
			if(name != null && name.length() > 0){
				
				String seqAsString = sequence.toString();
				seqAsString = seqAsString.replaceAll(" ","");
				sequences.add(new FastFastaSequence(name, seqAsString));
				name = null;
			}	
			
			
		} catch (Exception e) {
			logger.error(e);
			// TODO Auto-generated catch block
			throw new AlignmentImportException("could not import as fasta file because: " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

	}

	public int getLongestSequenceLength() {
		return longestSequenceLength;
	}
}
