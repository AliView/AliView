package aliview.sequencelist;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.AliView;
import aliview.FileFormat;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.NucleotideHistogram;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;
import aliview.settings.Settings;

public class FileSequenceListModel extends SequenceListModel implements ListDataListener{
	private static final Logger logger = Logger.getLogger(FileSequenceListModel.class);
	private static final String LF = System.getProperty("line.separator");
	private List<FileSequenceLoadListener> fileSeqLoadListeners = new ArrayList<FileSequenceLoadListener>();
	
	public FileSequenceListModel(File aliFile, FileFormat foundFormat) throws IOException {
		super(new FileMMSequenceList(aliFile, foundFormat));
		this.getSequences().addListDataListener(this);
	}

	@Override
	public FileMMSequenceList getSequences() {
		return (FileMMSequenceList) super.getSequences();
	}

	
	@Override
	// only for the ones in the cache
	public int getLongestSequenceLength() {		
		int maxLen = 0;
		for(Sequence seq: this.getSequences()){
			int len = seq.getLength();
			if(len > maxLen){
				maxLen = len;
			}
		}
		return maxLen;
	}

	public void contentsChanged(ListDataEvent e) {
		fireContentsChanged(this, 0, this.getSequences().size()-1);
	}


	public List<FilePage> getFilePages() {
		return this.getSequences().getFilePages();
	}

	public void loadMoreSequencesFromFile(FilePage page) {
		this.getSequences().loadMoreSequencesFromFile(page);
	}

	@Override
	public boolean isBaseSelected(int x, int y) {
		return super.isBaseSelected(x, y);
	}

	@Override
	public void setSelectionAt(int xPos, int yPos, boolean b) {
		super.setSelectionAt(xPos, yPos, b);
	}

	public void writeSelectionAsFasta(Writer out) {
		//if(getSelectionSize()*3 > MemoryUtils.getMaxMem()){
		if(getSelectionSize() > 200000000){
			//AliView.showUserMessage("Selection is to big to Copy with current Java Memory setting");
			AliView.showUserMessage("Selection is to big to Copy");
			logger.info("getSelectionSize" + getSelectionSize());
		}
		else{	
			int n = 0;
			for(Sequence sequence : this.sequences){
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

	public byte getBaseAt(int x, int y){
		return getSequences().get(y).getBaseAtPos(x);
	}

	public int getLengthAt(int y) {
		return getSequences().get(y).getLength();
	}

	public boolean isEditable(){
		return false;
	}
	
	public FilePage getActivePage(){
		return getSequences().getActivePage();
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
		for(Sequence seq: sequences){
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
	
	private void fireSequenceContentChanged(){
		for(FileSequenceLoadListener listener: fileSeqLoadListeners){
			listener.fileSequenceContentsChanged();
		}
	}

	public void addSequenceLoadLisetner(FileSequenceLoadListener listener) {
		if(listener == null || fileSeqLoadListeners.contains(listener)){
			return;
		}
		fileSeqLoadListeners.add(listener);
	}
	
	public void intervalAdded(ListDataEvent e) {
		// TODO Auto-generated method stub
	}


	public void intervalRemoved(ListDataEvent e) {
		// TODO Auto-generated method stub
	}


}
