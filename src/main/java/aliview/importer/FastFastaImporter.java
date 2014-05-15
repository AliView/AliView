package aliview.importer;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.sequences.FastFastaSequence;
import aliview.sequences.Sequence;

public class FastFastaImporter {
	private static final Logger logger = Logger.getLogger(FastFastaImporter.class);
	private Reader reader;
	private int longestSequenceLength;

	public FastFastaImporter(Reader reader) {
		this.reader = reader;
	}

	public List<Sequence> importSequences() throws AlignmentImportException {
		long startTime = System.currentTimeMillis();
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		int nextSeqEstSize = 5000;
		try {
			StringBuilder sequence = new StringBuilder(nextSeqEstSize);
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
							
							String seqAsString = sequence.toString();
							nextSeqEstSize = sequence.length();
							// if there are whitespace replace them
							if(sequence.indexOf(" ") > -1){
							  // sequence = FileImportUtils.replace(sequence, " ", "", -1);
							   sequence = FileImportUtils.removeAll(sequence, " ");
							}
							sequences.add(new FastFastaSequence(name, sequence.toString()));
							this.longestSequenceLength = Math.max(this.longestSequenceLength, sequence.length());
							sequence = new StringBuilder(nextSeqEstSize + 10);
							name = null;
						}	
						// skip
						name = line.substring(1);
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
				// if there are whitespace replace them
				if(seqAsString.indexOf(' ') > -1){
					seqAsString = seqAsString.replaceAll(" ","");
				}
				sequences.add(new FastFastaSequence(name, seqAsString));
				this.longestSequenceLength = Math.max(this.longestSequenceLength, seqAsString.length());
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