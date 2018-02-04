package aliview.importer;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.MemoryUtils;
import aliview.sequences.FastFastQSequence;
import aliview.sequences.Sequence;

public class FastFastQImporter {
	private static final Logger logger = Logger.getLogger(FastFastQImporter.class);
	private Reader reader;
	private int longestSequenceLength;


	public FastFastQImporter(Reader reader) {
		this.reader = reader;
	}

	public List<Sequence> importSequences() throws AlignmentImportException {
		long startTime = System.currentTimeMillis();
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		double maxMem = MemoryUtils.getMaxMem();
		try {
			BufferedReader r = new BufferedReader(this.reader);
			String line;
			String name = null;
			int nLine = 0;

			while ((line = r.readLine()) != null) {

				line = line.trim();
				
				

				// Starting with line 0 every 4:th line should be name
				if(nLine % 4 == 0){

					// if not fastq file then break
					if(line.length() > 0 && line.charAt(0) != '@'){
						// no fastg
						throw new AlignmentImportException("Error fastq format: very fourth line in fastq file should start with @ character");
					}
					name = line;				
				}

				// Starting with line 1 every 4:th line should be sequence
				if(nLine % 4 == 1){	
					if(line.length() > maxMem/8){
						throw new AlignmentImportException("Sequence to long for memory");
					}
					sequences.add(new FastFastQSequence(name, line));
					this.longestSequenceLength = Math.max(this.longestSequenceLength, line.length());
				}

				nLine ++;
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO Auto-generated catch block
			throw new AlignmentImportException("could not import as fastq file because: " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

		return sequences;
	}

	public int getLongestSequenceLength() {
		return longestSequenceLength;
	}

}