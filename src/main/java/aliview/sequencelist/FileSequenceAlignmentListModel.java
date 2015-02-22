package aliview.sequencelist;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.AliView;
import aliview.FileFormat;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.NucleotideHistogram;
import aliview.messenges.Messenger;
import aliview.sequences.FileSequence;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;
import aliview.settings.Settings;

public class FileSequenceAlignmentListModel extends AlignmentListModel{
	private static final Logger logger = Logger.getLogger(FileSequenceAlignmentListModel.class);
	private static final String LF = System.getProperty("line.separator");
	private List<FileSequenceLoadListener> fileSeqLoadListeners = new ArrayList<FileSequenceLoadListener>();
	
	public FileSequenceAlignmentListModel(File alignmentFile, FileFormat foundFormat) throws IOException {
		super(new CopyOnWriteArrayList<Sequence>(), foundFormat);
		MemoryMappedSequencesFile sequencesFile = new MemoryMappedSequencesFile(alignmentFile, foundFormat);
		sequencesFile.indexFileAndAddSequencesToAlignmentModel(this);	
	}

	public void writeSelectionAsFasta(Writer out) {
		//if(getSelectionSize()*3 > MemoryUtils.getMaxMem()){
		if(getSelectionSize() > 200000000){
			//AliView.showUserMessage("Selection is to big to Copy with current Java Memory setting");
			Messenger.showOKOnlyMessage(Messenger.TO_BIG_SELECTION_FOR_COPY);	
			logger.info("getSelectionSize" + getSelectionSize());
		}
		else{	
			int n = 0;
			for(Sequence sequence : this.delegateSequences){
				if(sequence.hasSelection()){
					String tempSeq = sequence.getSelectedBasesAsString();
					try {
						//TODO maybe format fasta better
						out.append(">");
						out.append(sequence.getName());
						out.append(LF);
						out.append(sequence.getSelectedBasesAsString());

						out.append(LF);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					logger.info("WroteSeq=" + n);
					n++;

				}
			}
		}
		logger.info("Write done");
	}

	

	public boolean isEditable(){
		return false;
	}
	
	
	
	@Override
	public AliHistogram getHistogram() {
		long startTime = System.currentTimeMillis();
		AliHistogram histogram = null;
		if(sequenceType == SequenceUtils.TYPE_AMINO_ACID){
			histogram = new AAHistogram(getLongestSequenceLength());
		}else{
			histogram = new NucleotideHistogram(getLongestSequenceLength());
		}
		
		// only do MAX_HISTOGRAM_SEQUENCES
		int counter = 0;
		for(Sequence seq: delegateSequences){
			if(counter > Settings.getMaxFileHistogramSequences().getIntValue()){
				break;
			}
			if(sequenceType == SequenceUtils.TYPE_AMINO_ACID){
				histogram.addSequence(seq);
			}else{
				histogram.addSequence(seq);
			}
			counter++;
		}
		long endTime = System.currentTimeMillis();
		logger.info("Create histogram took " + (endTime - startTime) + " milliseconds");
		return histogram;
	}
	
	/*
	public void addAll(List<Sequence> moreSeqs, boolean setSelected) {
		for(Sequence seq: moreSeqs){
			seq.setAlignmentModel(this);
		}
		logger.info("added all");
		delegateSequences.addAll(moreSeqs);
		if(setSelected){
			//getSeleselectionModel.setSequenceSelection(moreSeqs);
		}
	}
	*/
	
	
	// Skip padding if filesequences
	public boolean rightPadWithGapUntilEqualLength(){
		return false;
	}
	
	// Skip padding if filesequences
	public boolean leftPadWithGapUntilEqualLength() {
		return false;
	}
	
	// Skip trimming if filesequences
	public boolean rightTrimSequencesRemoveGapsUntilEqualLength(){
		return false;
	}
	
	// Skip delete if filesequences
	public void deleteBasesInAllSequencesFromMask(boolean[] deleteMask) {
		return;
	}

}
