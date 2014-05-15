package aliview.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jebl.evolution.io.FastaImporter;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.sequences.SequenceType;

import org.apache.log4j.Logger;

import aliview.FileFormat;
import aliview.MemoryUtils;
import aliview.sequencelist.FileSequenceListModel;
import aliview.sequencelist.MemorySequenceListModel;
import aliview.sequencelist.SequenceListModel;
import aliview.sequences.ConvertedJEBLSequence;
import aliview.sequences.Sequence;

public class SequencesFactory {
	private static final String LF = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(SequencesFactory.class);
	//private SequencesArrayList sequences;
	//private int longestSequenceLength = 0;
	//private FileFormat fileFormat; 

	public SequencesFactory() {
	}

	// TODO move to Sequences
	public List<Sequence> cloneSequences(List<Sequence> seqs){
		ArrayList<Sequence> clone = new ArrayList<Sequence>();
		for(Sequence seq: seqs){
			clone.add(seq);
		}
		return clone;
	}

	public List<Sequence> createEmptyMemorySequencesArrayList(){
		return new ArrayList<Sequence>();
	}

	//
	//	TODO maybe change the "longestSequenceLength" to something more dynamic....
	//  maybe create a "sequences" container that also keep track of longest seq
	//
	//
	public SequenceListModel createSequences(File alignmentFile) throws AlignmentImportException{

		// Check if file is to large - then create OnFile sequences instead of InMemory
		String importErrorMessage = "";
		SequenceListModel model = null;
				
		// check if file size is to big for memory sequences
		boolean memorySequences = true;
		if(alignmentFile != null){
			if(alignmentFile.exists()){
				long fileSize = alignmentFile.length();
				long maxMem = MemoryUtils.getMaxMem();
				
				// memory need to be 2 times file size
				if(maxMem/fileSize < 2){
					memorySequences = false;
				}
					
//				// TODO remove - this is pretty much only for testing
//				if(fileSize > 1000 * 1000 * 1000){
//					memorySequences = false;
//				}
				
			}
		}	

		//
		//	In memory sequences
		//
		if(memorySequences){
			// import sequences into memory
			logger.info("memorySequence");
			
			// check file-format
			FileFormat foundFormat = FileImportUtils.isFileOfAlignmentFormat(alignmentFile);
			
			if(foundFormat == FileFormat.FASTA){
				
				try {
					FastFastaImporter fastaImporter = new FastFastaImporter(new FileReader(alignmentFile));
					List<Sequence> sequences = fastaImporter.importSequences();
					model = new MemorySequenceListModel();
					model.setSequences(sequences);
					model.setFileFormat(FileFormat.FASTA);
				} catch (FileNotFoundException e) {
					importErrorMessage += "Tried import as Fasta but: " + e.getMessage() + LF;
					logger.error(importErrorMessage);
					logger.error(e);
				}	
			}
			
			
			if(foundFormat == FileFormat.PHYLIP){
				
				try {
						// First try phylip sequencial long names
						PhylipImporter phylipImporter = new PhylipImporter(new FileReader(alignmentFile), PhylipImporter.LONG_NAME_SEQUENTIAL);
						// this method will throw error if problem importing as this format and then we can try with other versions of phylip
						List<Sequence> sequences = phylipImporter.importSequences();
						model = new MemorySequenceListModel();
						model.setSequences(sequences);
						model.setFileFormat(FileFormat.PHYLIP);
	
				} catch (Exception e) {
						// TODO Auto-generated catch block
						importErrorMessage += "Tried import as Phylip but: " + e.getMessage() + LF;
						logger.error(importErrorMessage);
						logger.error(e);
				}
				
				if(model == null){			
					try {
						logger.info("try LONG_NAME_INTERLEAVED");
						// Then try phylip sequencial short names
						PhylipImporter phylipImporter = new PhylipImporter(new FileReader(alignmentFile), PhylipImporter.LONG_NAME_INTERLEAVED);
						// this method will throw error if problem importing as this format and then we can try with other versions of phylip
						List<Sequence> sequences = phylipImporter.importSequences();
						model = new MemorySequenceListModel();
						model.setSequences(sequences);
						model.setFileFormat(FileFormat.PHYLIP);	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						importErrorMessage += "Tried import as Phylip but: " + e.getMessage() + LF;
						logger.error(importErrorMessage);
						logger.error(e);
					}					
				}	
				
					
				if(model == null){			
					try {
						logger.info("try short name sequential");
						// Then try phylip sequencial short names
						PhylipImporter phylipImporter = new PhylipImporter(new FileReader(alignmentFile), PhylipImporter.SHORT_NAME_SEQUENTIAL);
						// this method will throw error if problem importing as this format and then we can try with other versions of phylip
						List<Sequence> sequences = phylipImporter.importSequences();
						model = new MemorySequenceListModel();
						model.setSequences(sequences);
						model.setFileFormat(FileFormat.PHYLIP);	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						importErrorMessage += "Tried import as Phylip but: " + e.getMessage() + LF;
						logger.error(importErrorMessage);
						logger.error(e);
					}					
				}	
				
				
				if(model == null){		
					try {
						logger.info("try short name interleaved");
						// Then try phylip sequencial short names
						PhylipImporter phylipImporter = new PhylipImporter(new FileReader(alignmentFile), PhylipImporter.SHORT_NAME_INTERLEAVED);
						// this method will throw error if problem importing as this format and then we can try with other versions of phylip
						List<Sequence> sequences = phylipImporter.importSequences();
						model = new MemorySequenceListModel();
						model.setSequences(sequences);
						model.setFileFormat(FileFormat.PHYLIP);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						importErrorMessage += "Tried import as Phylip but: " + e.getMessage() + LF;
						logger.error(importErrorMessage);
						logger.error(e);
					}		
				}
			}
	
			if(foundFormat == FileFormat.NEXUS){
			
				// Import sequences with jebl-library
				try{
					NexusImporter importer = new NexusImporter(new FileReader(alignmentFile));
					List<jebl.evolution.sequences.Sequence> jeblSequences = importer.importSequences();
					if(jeblSequences != null && jeblSequences.size() > 0){
						model = new MemorySequenceListModel();
						model.setSequences(convertJEBLSequences(jeblSequences));
						model.setFileFormat(FileFormat.NEXUS);
					}
				}catch (ImportException impExc) {
					logger.error(impExc);
					importErrorMessage += "Tried import as Nexus but: " + impExc.userMessage() + LF;
				}catch (Exception e) {
						logger.error(e);
						importErrorMessage += "Tried import as Nexus but: " + e.getMessage() + LF;
				}
			}
			
			if(foundFormat == null || model == null || model.getSize() == 0){
				// still nothing 
				throw new AlignmentImportException("Could not find sequences in file: " + alignmentFile + LF + importErrorMessage);
			}
			
		//
		// FILE SEQUENCES
		//
		}else if(!memorySequences){
		
				FileFormat foundFormat = FileImportUtils.isFileOfAlignmentFormat(alignmentFile);
				
				if(foundFormat == FileFormat.FASTA || foundFormat == FileFormat.PHYLIP){
					
					try{
						model = new FileSequenceListModel(alignmentFile, foundFormat);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// no supported large file format
				else{
					
				}
		}
		
		// could still be null
		return model;
	}
	
	public SequenceListModel createFastaSequences(StringReader stringReader) throws AlignmentImportException {
		SequenceListModel model = new MemorySequenceListModel();
			
			try {
				// First try fast fasta
				FastFastaImporter fastaImporter = new FastFastaImporter(stringReader);
				model.setSequences(fastaImporter.importSequences());
				model.setFileFormat(FileFormat.FASTA);
			} catch (Exception e) {
				logger.error(e);	
			}
			
			return model;
	}

	private List<Sequence> convertJEBLSequences(List<jebl.evolution.sequences.Sequence> jeblSequences) {
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		for(jebl.evolution.sequences.Sequence jeblSequence: jeblSequences){
			// Craete sequences by wrapping jebl-sequences in AliView-sequences
			Sequence seq = new ConvertedJEBLSequence(jeblSequence);
			sequences.add(seq);
		}
		return sequences;
	}

	
	
	
}
