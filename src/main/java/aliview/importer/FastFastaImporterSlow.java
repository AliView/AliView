package aliview.importer;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import aliview.sequences.FastFastaSequence;
import aliview.sequences.Sequence;

public class FastFastaImporterSlow {
	private static final Logger logger = Logger.getLogger(FastFastaImporterSlow.class);
	private Reader reader;
	private int longestSequenceLength;

	public FastFastaImporterSlow(Reader reader) {
		this.reader = reader;
	}

	public List<Sequence> importSequences() throws AlignmentImportException {

		long startTime = System.currentTimeMillis();
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		try {
			StringBuilder sequence = new StringBuilder();
			BufferedReader r = new BufferedReader(this.reader);
			String line;
			String name = null;
			int nLine = 0;
			while ((line = r.readLine()) != null) {
				
				line = line.trim();
				
				if(nLine == 0){
					// if not fasta file then break
					if(line.length() > 0 && line.charAt(0) != '>'){
						// no fasta
						throw new AlignmentImportException("Fasta file should start with > character");
					}
				}
				
				if(line.length() > 0){

					if(line.charAt(0) == '>'){

						// if there is one sequence in buffer already create that one before starting a new one
						if(name != null && name.length() > 0){
							//char[] bases = new char[sequence.length()];
							//sequence.getChars(0, sequence.length() -1, bases, 0);
							
							//char[] bases = sequence.toString().toCharArray();
							
							// remove blank in string todo this could maybe be done quicker
							// in some fasta files there are blanks (ncbi format)
							String seqAsString = sequence.toString();
							seqAsString = seqAsString.replaceAll(" ","");
							sequences.add(new FastFastaSequence(name, seqAsString));
							this.longestSequenceLength = Math.max(this.longestSequenceLength, seqAsString.length());
							sequence = new StringBuilder();
							name = null;
						}	
						name = line;
					}
					else{
						sequence.append(line);
					}

				}
				nLine ++;
			}
			
			// add last sequence
			if(name != null && name.length() > 0){
				
				String seqAsString = sequence.toString();
				seqAsString = seqAsString.replaceAll(" ","");
				sequences.add(new FastFastaSequence(name, seqAsString));
				this.longestSequenceLength = Math.max(this.longestSequenceLength, seqAsString.length());
				name = null;
			}	
			
			
		} catch (Exception e) {
			logger.error(e);
			// TODO Auto-generated catch block
			throw new AlignmentImportException("could not import as fasta file because: " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

		return sequences;
	}

	public int getLongestSequenceLength() {
		return longestSequenceLength;
	}
}