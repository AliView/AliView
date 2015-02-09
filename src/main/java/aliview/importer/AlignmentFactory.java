package aliview.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import utils.nexus.CharSet;
import utils.nexus.CodonPositions;
import utils.nexus.Excludes;
import utils.nexus.NexusAlignmentImportException;
import utils.nexus.NexusUtilities;
import aliview.AliView;
import aliview.MemoryUtils;
import aliview.alignment.Alignment;
import aliview.alignment.AlignmentMeta;
import aliview.messenges.Messenger;
import aliview.sequencelist.FileSequenceAlignmentListModel;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequences.NexusSequence;
import aliview.sequences.SequenceUtils;
import aliview.settings.Settings;

public class AlignmentFactory {
	private static final String LF = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(AlignmentFactory.class);
	private static final SequencesFactory seqFactory = new SequencesFactory();
	// TODO change so it also reads from buffer
	
	/*
	 * Create alignment in factory since we dont know until we have read file if it is a nucleotide
	 * or a protein alignment
	 * 
	 */
	
	public static Alignment createNewAlignment(File alignmentFile){
		
			logger.info("inside createNewAlignment");
			
			long startTime;
			startTime = System.currentTimeMillis();
			Alignment alignment = null;
			try {
				
				AlignmentListModel sequences = seqFactory.createSequences(alignmentFile);			
				
				Excludes excludes = new Excludes(sequences.getLongestSequenceLength());
				logger.info("sequences.getLongestSequenceLength()" + sequences.getLongestSequenceLength());
				CodonPositions codonPositions = new CodonPositions(sequences.getLongestSequenceLength());
				ArrayList<CharSet> charsets = new ArrayList<CharSet>();

					try {
						// Try to read Excludes etc. from alignmentfile	
						if(NexusUtilities.isNexusFile(alignmentFile) && sequences instanceof FileSequenceAlignmentListModel == false && sequences.get(0) instanceof NexusSequence == false){
							NexusUtilities.updateExcludesFromFile(alignmentFile,excludes);
							NexusUtilities.updateCodonPositionsFromNexusFile(alignmentFile, codonPositions);
							charsets = NexusUtilities.createCharsetsFromNexusFile(alignmentFile, sequences.getLongestSequenceLength());
						}
						// Try to read Excludes etc. from metaFile
						else{
							// Try to read Excludes from metaFile
							File metaFile = new File(alignmentFile.getAbsolutePath()+ ".meta");
							if(metaFile.exists()){
								NexusUtilities.updateExcludesFromFile(metaFile,excludes);
								NexusUtilities.updateCodonPositionsFromNexusFile(metaFile, codonPositions);
								charsets = NexusUtilities.createCharsetsFromNexusFile(metaFile, sequences.getLongestSequenceLength());
							}
						}
					} catch (NexusAlignmentImportException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e);
						Messenger.showOKOnlyMessage(Messenger.ALIGNMENT_META_READ_ERROR,
								LF + e.getLocalizedMessage());	
					}
				
				
				MemoryUtils.logMem();
				AlignmentMeta aliMeta = new AlignmentMeta(excludes, codonPositions, charsets);
				if(sequences.getSequenceType() == SequenceUtils.TYPE_AMINO_ACID){
					alignment = new Alignment(alignmentFile, sequences.getFileFormat(), sequences, aliMeta);
				}
				else{
					alignment = new Alignment(alignmentFile, sequences.getFileFormat(), sequences, aliMeta);
				}
				MemoryUtils.logMem();
				
				
			// There was a problem 
			} catch (AlignmentImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e);
				Messenger.showOKOnlyMessage(Messenger.ALIGNMENT_IMPORT_ERROR,
						LF + e.getLocalizedMessage());

			}
			
			// Check if unique names - otherwise warn
			if(alignment != null){
				boolean hideMessage = Settings.getHideDuplicateSeqNamesMessage().getBooleanValue();
				if(! hideMessage){
					ArrayList duplicateSeqNames = alignment.findDuplicateNames();
					if(duplicateSeqNames != null && duplicateSeqNames.size() > 0){
						alignment.selectDuplicateNamesSequences();
						Messenger.showDuplicateSeqNamesMessage(duplicateSeqNames);
					}
				}
			}
			
		
			long endTime = System.currentTimeMillis();
			System.out.println("Importing sequences took " + (endTime - startTime) + " milliseconds");
			
			return alignment;
		}

	// ToDO maybe throw something if not working
	public static Alignment createNewAlignment(String alignmentText){
		Alignment alignment = null;
		try {
			logger.info("ali" + alignmentText);
			File tempFile = File.createTempFile("tmp-aliview-clipboard-alignment", "");
			FileUtils.writeStringToFile(tempFile, alignmentText);
			alignment = createNewAlignment(tempFile);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alignment;
	}
	
	public static Alignment createNewEmptyAlignment(){
		return new Alignment();
	}

}
		
