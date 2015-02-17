package aliview.alignment;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import utils.DialogUtils;
import utils.nexus.CharSet;
import utils.nexus.Excludes;
import utils.nexus.NexusRange;
import utils.nexus.NexusUtilities;
import aliview.AliViewExtraNexusUtilities;
import aliview.AminoAcid;
import aliview.Base;
import aliview.FileFormat;
import aliview.GeneticCode;
import aliview.NucleotideUtilities;
import aliview.importer.AlignmentImportException;
import aliview.importer.MSFFileIndexer;
import aliview.importer.MSFImporter;
import aliview.importer.SequencesFactory;
import aliview.messenges.Messenger;
import aliview.messenges.TextEditDialog;
import aliview.primer.Dimer;
import aliview.primer.Primer;
import aliview.sequencelist.AlignmentDataListener;
import aliview.sequencelist.AlignmentSelectionEvent;
import aliview.sequencelist.AlignmentSelectionListener;
import aliview.sequencelist.FileSequenceAlignmentListModel;
import aliview.sequencelist.FindObject;
import aliview.sequencelist.MemorySequenceAlignmentListModel;
import aliview.sequencelist.AlignmentListModel;
import aliview.sequencelist.FileSequenceLoadListener;
import aliview.sequences.FastaFileSequence;
import aliview.sequences.InMemoryBasicSequence;
import aliview.sequences.PhylipSequence;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceUtils;
import aliview.settings.Settings;
import aliview.utils.ArrayUtilities;

public class Alignment implements FileSequenceLoadListener {
	private static final Logger logger = Logger.getLogger(Alignment.class);
	private static final String LF = System.getProperty("line.separator");
	private static final SequencesFactory seqFactory = new SequencesFactory();
	AlignmentListModel sequences;
	protected int nextFindSequenceNumber;
	protected int nextFindStartPos;
	private File alignmentFile;
	private int PRIMER_MAX_DEGENERATE_SCORE = 1000;
	private boolean isEditedAfterLastSave = false;
	private FileFormat fileFormat;
	private AlignmentMeta alignmentMeta;
	private int readingFrame = 1;
	private boolean editMode;
	private ArrayList<AlignmentListener> alignmentListeners = new ArrayList<AlignmentListener>();
	private boolean isSelectable;

	public Alignment(){
		this(null, null, new MemorySequenceAlignmentListModel(), new AlignmentMeta());
	}

	public Alignment(AlignmentListModel sequences) {
		this(null, null, sequences, new AlignmentMeta());
	}
	
	public Alignment(File file, FileFormat fileFormat, AlignmentListModel sequences, AlignmentMeta aliMeta) {
		setAlignmentFile(file);
		this.fileFormat = fileFormat;
		this.sequences = sequences;
		this.sequences.setAlignment(this);
		//this.sequences.addAlignmentSelectionListener(this);
		this.alignmentMeta = aliMeta;
		setEditedAfterLastSave(false);
		if(this.sequences instanceof FileSequenceAlignmentListModel){
			((FileSequenceAlignmentListModel) sequences).addSequenceLoadLisetner(this);
		}
		fireAllSequencesAreNew();
	}
	
	
	
	public void setNewSequences(AlignmentListModel seqs){
		this.sequences = seqs;			
		// TODO this should also be read from file
		this.alignmentMeta = new AlignmentMeta();	
		setEditedAfterLastSave(false);
		fireAllSequencesAreNew();
	}
	
	
	public double getApproximateMemorySizeMB(){
		double seqSize = sequences.getSize() * sequences.getLongestSequenceLength();
		double MB = 1000 * 1000;
		return seqSize /MB;
	}
	
	public void addAlignmentListener(AlignmentListener l){
		alignmentListeners.add(l);
	}
	
	public void addAlignmentDataListener(AlignmentDataListener l){
		sequences.addAlignmentDataListener(l);
	}
	
	public void addAlignmentSelectionListener(AlignmentSelectionListener l){
		sequences.addAlignmentSelectionListener(l);
	}
	
	private void fireAlignmentMetaOnlyChanged(){
		for(AlignmentListener listener: alignmentListeners){
		//	listener.alignmentMetaChanged(new AlignmentEvent(this));
		}
	}
	
	// instead listen to sequence change and then set cached histogram
	private void fireAllSequencesAreNew(){
		for(AlignmentListener listener: alignmentListeners){
			listener.newSequences(new AlignmentEvent(this));
		}
	}
	
	public int getMaxY() {
		return sequences.getSize();
	}

	public byte getBaseAt(int x, int y) {
		return sequences.getBaseAt(x,y);
	}

	public AminoAcid getTranslatedAminoAcidAtNucleotidePos(int x, int y) {
		return sequences.getTranslatedAminoAcidAtNucleotidePos(x,y);
	}
	
	public int getLengthAt(int y) {
		return sequences.getLengthAt(y);
	}

	public int getMaxX() {
		return sequences.getLongestSequenceLength();
	}
	
	// TODO this is the only place where sequences are checked for modification when they are "outside"
	public AlignmentListModel getSequences() {
		return sequences;
	}

	public int getMaximumSequenceLength() {
		return getMaxX();
	}

	public int getSize(){
		return sequences.getSize();
	}

	public void clearSelection() {
		sequences.clearSequenceSelection();
		//fireSelectionChanged();
	}

	public int getSequenceIndex(Sequence seq) {
		return sequences.indexOf(seq);
	}
	
	public File getAlignmentFile() {
		return this.alignmentFile;
	}

	public String getFileName() {
		if(alignmentFile == null){
			return null;
		}
		return alignmentFile.getName();

	}

	public void setAlignmentFile(File alignmentFile) {
		if(alignmentFile != null && alignmentFile.exists()){
			this.alignmentFile = alignmentFile.getAbsoluteFile();
		}
	}
	
	public void storeAlignmetAsFasta(Writer out) throws IOException{
		int longest = sequences.getLongestSequenceLength();
		for(Sequence seq: sequences){
			String name = seq.getName();
		
			out.write('>');
			out.write(seq.getName());
			out.write(LF);
			seq.writeBases(out);
					
			out.write(LF);

		}		
		out.flush();
		out.close();
	}
	
	private void storeAlignmetAsClustal(Writer out) throws IOException {
		
		// First write meta
		out.write("CLUSTAL W (1.99) multiple sequence alignment" + LF);
		out.write(LF);
		out.write(LF);
		
		int longSeq = sequences.getLongestSequenceLength();
		int namePadSize = sequences.getLongestSequenceName() + 1;
		
	    //  Clustal uses up to 60 residues per line
		for(int pos = 0; pos < longSeq; pos += 60){
			
			int endPos = pos + 60;
			endPos = Math.min(endPos, longSeq);
			
			for(int n = 0; n < sequences.getSize(); n++){		
				// Write name space and up to 60 residues
				Sequence seq = sequences.get(n);
				String paddedName = StringUtils.rightPad(seq.getName(), namePadSize);
				byte[] bases = seq.getBasesBetween(pos,  endPos - 1);

				out.write(paddedName);
				out.write(new String(bases));
				out.write(LF);
			}
			
			// add two blank lines
			out.write(LF);
			out.write(LF);
		}
		out.flush();
		out.close();
	}
	
	private void storeAlignmetAsMSF(Writer out) throws IOException {
		
		int longSeq = sequences.getLongestSequenceLength();
		int namePadSize = sequences.getLongestSequenceName() + 1;
			
		// first write alignment meta
		String type = "P";
		String type2 = "!!AA";
		if(this.isNucleotideAlignment()){
			type = "N";
			type2 = "!!NA";
		}
		
		out.write("" + type2 + "_MULTIPLE_ALIGNMENT" + LF);
		out.write(LF);
		out.write(LF);
		
		
	//	 checksumTotal += check;
	//	 checksumTotal %= 10000;
		
		int checkTotal = 0;
		for(int n = 0; n < sequences.getSize(); n++){
			Sequence seq = sequences.get(n);
			String paddedName = StringUtils.rightPad(seq.getName(), namePadSize);
			int check = MSFImporter.GCGchecksum(seq);
			checkTotal += check;
			checkTotal %= 10000;
		}

		
		String totalMeta = "   MSF: " + longSeq + "  Type: " + type + "  Check: "  + checkTotal + "  ..";
		out.write(totalMeta);
		out.write(LF);
		
		out.write(LF);
		
		// then write names and checksums
		for(int n = 0; n < sequences.getSize(); n++){
			Sequence seq = sequences.get(n);
			String paddedName = StringUtils.rightPad(seq.getName(), namePadSize);
			int check = MSFImporter.GCGchecksum(seq);
			String seqMeta = " Name: " + paddedName + "  Len: " + seq.getLength() + "  Check: "  + check + "  Weight: 1.00";
			out.write(seqMeta);
			out.write(LF);
			
		}
		
		// start sequences
		out.write(LF);
		out.write(LF);
		out.write("//");
		out.write(LF);
		out.write(LF);
		// then write 
		
		
	    // MSF uses up to 50 residues per line
		for(int pos = 0; pos < longSeq; pos += 50){
			
			int endPos = pos + 50;
			endPos = Math.min(endPos, longSeq);
			
			for(int n = 0; n < sequences.getSize(); n++){		
				// Write name space and up to 50 residues
				Sequence seq = sequences.get(n);
				String paddedName = StringUtils.rightPad(seq.getName(), namePadSize);
				
				out.write(paddedName);
				
				byte[] bases = seq.getBasesBetween(pos,  endPos - 1);
				// replace gaps to MSF notation
				bases = ArrayUtilities.replaceAll(bases, '-', (byte) '.');			
				
				// write bases with space every 10 pos
				for(int delPos = 0; delPos < bases.length; delPos += 10){
					int maxLen = 10;
					maxLen = Math.min(maxLen, bases.length - delPos);
					out.write(new String(bases, delPos, maxLen));
					
					// add space, but not at last
					if(delPos+10 < bases.length){
						out.write(' ');
					}
				}
					
				out.write(LF);
			}
			
			// add two blank lines
			out.write(LF);
			out.write(LF);
		}
		out.flush();
		out.close();
	}


	private void storeAlignmetAsPhyFile(Writer out, FileFormat fileFormat) throws IOException{
		// First line number of seq + seqLen
		out.write("" + sequences.getSize() + " " + this.getMaximumSequenceLength() + LF);

		
		// Interleaved
		if(fileFormat == FileFormat.PHYLIP_RELAXED_PADDED_INTERLEAVED){
			int longSeq = sequences.getLongestSequenceLength();
			int namePadSize = sequences.getLongestSequenceName() + 1;

			// 60 residues per line
			int residuesPerLine = 80;
			// names on first round only
			for(int n = 0; n < sequences.getSize(); n++){		
				// Write name space and up to xx residues
				Sequence seq = sequences.get(n);
				String seqName = seq.getName();
				seqName = escapeSeqName(seqName);
				
				String paddedName = StringUtils.rightPad(seqName, namePadSize);
				
				out.write(paddedName);
				
				byte[] bases = seq.getBasesBetween(0,  residuesPerLine - 1);
				
				out.write(new String(bases));
					
				out.write(LF);
			}
			
			out.write(LF);
			out.write(LF);
			

			
			for(int pos = residuesPerLine; pos < longSeq; pos += residuesPerLine){
				int endPos = pos + residuesPerLine;
				endPos = Math.min(endPos, longSeq);
				
				for(int n = 0; n < sequences.getSize(); n++){		
					// Write name space and up to xx residues
					Sequence seq = sequences.get(n);
//					String seqName = seq.getName();
//					seqName = escapeSeqName(seqName);
//					
//					String paddedName = StringUtils.rightPad(seqName, namePadSize);
//					
//					out.write(paddedName);
					
					byte[] bases = seq.getBasesBetween(pos,  endPos - 1);
					
					out.write(new String(bases));
						
					out.write(LF);
				}
				
				// add two blank lines before next "interleave"
				out.write(LF);
				out.write(LF);
			}
		}
		// Sequential versions
		else{
			for(Sequence seq: sequences){
				String seqName = seq.getName();
				seqName = escapeSeqName(seqName);
						
				if(fileFormat == FileFormat.PHYLIP_RELAXED_PADDED){
					int longSeqName = sequences.getLongestSequenceName();
					seqName = StringUtils.rightPad(seqName , longSeqName + 1); // + one space
				}
				
				if(fileFormat == FileFormat.PHYLIP_RELAXED){
					int longSeqName = sequences.getLongestSequenceName();
					seqName += ' '; // + one space
				}
				
				if(fileFormat == FileFormat.PHYLIP_STRICT_SEQUENTIAL){
					// Make sure exact 10 positions
					if(seqName.length() > 10){
						seqName = StringUtils.substring(seqName, 0, 10);					
					}
					seqName = StringUtils.rightPad(seqName , 10); // 10 name no space
				}
				
				out.write(seqName); // no space after name that is added on name already (but not on strict)
				seq.writeBases(out);
				out.write(LF);
			}
		}

		out.flush();
		out.close();	

	}

	private String escapeSeqName(String name) {
		if(name != null && name.indexOf(' ') > -1){
			name = StringUtils.replace(name, " ", "_");
		}
		return name;
	}
/*
	private void storeAlignmetAsPhyTranslatedAminoAcidFile(Writer out) throws IOException{
		// First line number of seq + seqLen
		out.write("" + sequences.getSize() + " " + alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(sequences.getLongestSequenceLength()) + LF);

		// int longSeqName = sequences.getLongestSequenceName();
		
		AATranslator aaTransSeq = new AATranslator(getAlignmentMeta().getCodonPositions(),getGeneticCode());
		for(Sequence seq: sequences){
			aaTransSeq.setSequence(seq);
		
			out.write(escapeSeqName(seq.getName()) + " ");
			aaTransSeq.writeTranslation(out);
			out.write(LF);
		}

		out.flush();
		out.close();	

	}
	
	private void storeAlignmetAsFastaTranslatedAminoAcidFile(Writer out) throws IOException{
		// make sure it is translated
		boolean wasTranslated = isTranslatedOnePos();
		setTranslationOnePos(true);
		
		for(Sequence seq: sequences){

			String name = seq.getName();
			out.write('>');
			out.write(seq.getName());
			out.write(LF);
			seq.writeBases(out);
			out.write(LF);
		}		
		out.flush();
		out.close();
		
		setTranslationOnePos(wasTranslated);
	}
	*/

	private void storeMetaData(BufferedWriter outMeta) throws IOException {
		outMeta.write("" + NexusUtilities.getExcludesAsNexusBlock(alignmentMeta.getExcludes()));
		outMeta.write(LF);
		outMeta.write("" + NexusUtilities.getCodonPosAsNexusBlock(alignmentMeta.getCodonPositions(), 0, this.getMaximumSequenceLength()));
		outMeta.write(LF);
		outMeta.write("" + NexusUtilities.getCharsetsBlockAsNexus(alignmentMeta.getCharsets()));
		outMeta.write(LF);

		outMeta.flush();
		outMeta.close();
	}

	private void storeTranslatedMetaData(BufferedWriter outMeta, AlignmentMeta translatedMeta) throws IOException {
		outMeta.write("" + NexusUtilities.getExcludesAsNexusBlock(translatedMeta.getExcludes()));
		outMeta.write(LF);
		//outMeta.write("" + NexusUtilities.getCodonPosAsNexusBlock(alignmentMeta.getCodonPositions()));
		outMeta.write("" + NexusUtilities.getCharsetsBlockAsNexus(translatedMeta.getCharsets()));
		outMeta.write(LF);
		outMeta.flush();
		outMeta.close();
	}

	public void saveSelectionAsFastaFile(File selectedFile){
		
		try {
			BufferedWriter buffOut = new BufferedWriter(new FileWriter(selectedFile));
			sequences.writeSelectionAsFasta(buffOut);
			buffOut.flush();
			buffOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveAlignmentAsFile(File outFile) throws IOException{	
		// save as current format
		saveAlignmentAsFile(outFile,this.fileFormat);	
	}

	public void saveAlignmentAsFile(File outFile, FileFormat fileFormat) throws IOException{
		
		// make sure they are equal length (if editable)
		if(sequences.isEditable()){
			sequences.rightPadWithGapUntilEqualLength();
			sequences.rightTrimSequencesRemoveGapsUntilEqualLength();
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

		int nexusDatatype = AliViewExtraNexusUtilities.DATATYPE_DNA;
		if(sequences.getSequenceType() == SequenceUtils.TYPE_AMINO_ACID){
			nexusDatatype = AliViewExtraNexusUtilities.DATATYPE_PROTEIN;
		}
		
		if(fileFormat == FileFormat.FASTA){
			storeAlignmetAsFasta(out);
			// save meta if exset it is set
			if(this.alignmentMeta.isMetaOutputNeeded()){
				BufferedWriter outMeta = new BufferedWriter(new FileWriter(new File(outFile.getAbsoluteFile() + ".meta")));
				storeMetaData(outMeta);
			}
		}else if(fileFormat == FileFormat.PHYLIP || fileFormat == FileFormat.PHYLIP_RELAXED ||
				 fileFormat == FileFormat.PHYLIP_RELAXED_PADDED || fileFormat == FileFormat.PHYLIP_STRICT_SEQUENTIAL ||
				 fileFormat == FileFormat.PHYLIP_RELAXED_PADDED_INTERLEAVED){
			storeAlignmetAsPhyFile(out, fileFormat);
			// save meta if exset it is set
			if(this.alignmentMeta.isMetaOutputNeeded()){
				BufferedWriter outMeta = new BufferedWriter(new FileWriter(new File(outFile.getAbsoluteFile() + ".meta")));
				storeMetaData(outMeta);
			}
			
		
		}else if(fileFormat == FileFormat.PHYLIP_TRANSLATED_AMINO_ACID){
			boolean wasTranslated = isTranslatedOnePos();
			setTranslationOnePos(true);
			storeAlignmetAsPhyFile(out, FileFormat.PHYLIP_RELAXED_PADDED);
			// save meta if exset it is set
			if(this.alignmentMeta.isMetaOutputNeeded()){
				//AlignmentMeta translatedMeta = getTranslatedMeta();
				BufferedWriter outMeta = new BufferedWriter(new FileWriter(new File(outFile.getAbsoluteFile() + ".meta")));
			//	storeTranslatedMetaData(outMeta, translatedMeta);
			}
			setTranslationOnePos(wasTranslated);
		}else if(fileFormat == FileFormat.FASTA_TRANSLATED_AMINO_ACID){
			boolean wasTranslated = isTranslatedOnePos();
			setTranslationOnePos(true);
			storeAlignmetAsFasta(out);
			// save meta if exset it is set
			if(this.alignmentMeta.isMetaOutputNeeded()){
				//AlignmentMeta translatedMeta = getTranslatedMeta();
				BufferedWriter outMeta = new BufferedWriter(new FileWriter(new File(outFile.getAbsoluteFile() + ".meta")));
				//storeTranslatedMetaData(outMeta, translatedMeta);
			}
			setTranslationOnePos(wasTranslated);
		}else if(fileFormat == FileFormat.CLUSTAL){
			storeAlignmetAsClustal(out);
		}else if(fileFormat == FileFormat.MSF){
			storeAlignmetAsMSF(out);
		}else if(fileFormat == FileFormat.NEXUS){
			AliViewExtraNexusUtilities.exportAlignmentAsNexus(new BufferedWriter(new FileWriter(outFile)), this, false,nexusDatatype);
		}else if(fileFormat == FileFormat.NEXUS_TRANSLATED_AMINO_ACID){
			// make sure it is translated
			boolean wasTranslated = isTranslatedOnePos();
			setTranslationOnePos(true);
			AliViewExtraNexusUtilities.exportAlignmentAsNexus(new BufferedWriter(new FileWriter(outFile)), this, false, AliViewExtraNexusUtilities.DATATYPE_PROTEIN);
			setTranslationOnePos(false);
		}else if(fileFormat == FileFormat.NEXUS_SIMPLE){
			AliViewExtraNexusUtilities.exportAlignmentAsNexus(new BufferedWriter(new FileWriter(outFile)), this, true, nexusDatatype);
		}else if(fileFormat == FileFormat.NEXUS_CODONPOS_CHARSET){
			AliViewExtraNexusUtilities.exportAlignmentAsNexusCodonpos(new BufferedWriter(new FileWriter(outFile)), this, AliViewExtraNexusUtilities.DATATYPE_DNA);
		}		
	}

	/*
	 * utility method
	 */
	/*
	private String translateSequence(Sequence seq){
		AATranslator aaTransSeq = new AATranslator(getAlignmentMeta().getCodonPositions(),getGeneticCode());
		aaTransSeq.setSequence(seq);
		return aaTransSeq.getTranslatedAsString();
	}

	private Alignment getTranslatedAlignment(){
		AlignmentMeta transMeta = getTranslatedMeta();
		AlignmentListModel transSeq = createTranslatedSequences();
		Alignment translatedAlignment = new Alignment(null, null, transSeq, transMeta);
		return translatedAlignment;
	}
	*/
	
	//
	//  TODO this is not working for fileSequences
	//
	/*
	private AlignmentListModel createTranslatedSequences(){
		AlignmentListModel transSeqs = new MemorySequenceAlignmentListModel();
		for(Sequence seq: sequences){			
			transSeqs.add(new PhylipSequence(seq.getName(), translateSequence(seq)));
		}
		return transSeqs;
	}
	*/


	// TODO this might get wrong if translating sequence and sequence order in alignment not is same, or sequence 
	// has x offset in alignment
	private AlignmentMeta getTranslatedMeta(){	
		ArrayList<CharSet> charsetsTrans = new ArrayList<CharSet>();
		Excludes excludesTrans = new Excludes(alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(sequences.getLongestSequenceLength()));
		AlignmentMeta metaTrans = new AlignmentMeta(excludesTrans,null,charsetsTrans,alignmentMeta.getGeneticCode());
		
		for(CharSet charset: alignmentMeta.getCharsets()){
			ArrayList<NexusRange> nexusRanges = charset.getCharSetAsNexusRanges();
			if(nexusRanges != null){
				logger.info("rangesSize" + nexusRanges.size());
				CharSet translatedSet = new CharSet(charset.getName(),alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(sequences.getLongestSequenceLength()));
				for(NexusRange range: nexusRanges){	
					NexusRange translatedRange = new NexusRange(alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(range.getMinimumInteger()) + 1, // +1 för Nexus ranges börjar på 1
							                                         alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(range.getMaximumInteger()) + 1); // +1 för Nexus ranges börjar på 1
					logger.info(translatedRange);
					translatedSet.addRange(translatedRange);
				}
				charsetsTrans.add(translatedSet);
			}
		}
		for(NexusRange range: alignmentMeta.getExcludes().getExcludedAsNexusRanges()){
			NexusRange translatedRange = new NexusRange(alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(range.getMinimumInteger()),
					                                         alignmentMeta.getCodonPositions().getAminoAcidPosFromNucleotidePos(range.getMaximumInteger()));
			excludesTrans.addRange(translatedRange);
		}
		return metaTrans;
	}
		
	
	/*
	 * Alternate method
	 */
	public boolean mergeTwoSequences(List<Sequence> seqs, boolean allowOverlap) {
		boolean isMerged = false;
		if(seqs.size() == 2){
			isMerged = mergeTwoSequences(seqs.get(0),seqs.get(1), allowOverlap);
		}
		return isMerged;
	}
	

	public boolean mergeTwoSequences(Sequence seq1, Sequence seq2, boolean allowOverlap){
		boolean wasMerged = sequences.mergeTwoSequences(seq1, seq2, allowOverlap);
		
		return wasMerged;
	}


	public void deleteAllExsetBases(){	
		// first create a deletemask
		boolean[] deleteMask = new boolean[this.alignmentMeta.getExcludes().getLength()];
		for(int n = 0; n < deleteMask.length; n++){
			if(this.alignmentMeta.isExcluded(n)){
				deleteMask[n] = true;
			}
		}
		sequences.deleteBasesInAllSequencesFromMask(deleteMask);
		//and finally remove in AlignmentMeta(excludes, codonpos & charset)
		alignmentMeta.removeFromMask(deleteMask);
//		sequencesChanged();
	}



	public ArrayList<Primer> findPrimerInSelection(){
		ArrayList<Primer> allPrimers = new ArrayList<Primer>();

		// no selection returm
		if(! sequences.hasSelection()){
			return allPrimers;
		}
		
		String selection = getSelectionAsNucleotides();
		
//		logger.info(selection);
		
		String[] selectionSeq = selection.split("\\n");

		// Start with a prototype as consensus
		int consensusVal[] = new int[selectionSeq[0].length()];

		for(String seq: selectionSeq){
			for(int n = 0; n < consensusVal.length; n++){
				int baseVal = NucleotideUtilities.baseValFromChar(seq.charAt(n));
				// Create consensus by bitwise OR of the bases in the same column 
				consensusVal[n] = consensusVal[n] | baseVal;
			}
		}

		// Create consensus from baseValues
		StringBuilder consensusBuilder = new StringBuilder();
		for(int n = 0; n < consensusVal.length; n++){
			consensusBuilder.append(NucleotideUtilities.charFromBaseVal(consensusVal[n]));
		}
		String consensus = consensusBuilder.toString();

		logger.info(consensus.length());
		
		// remove gaps in consensus
		consensus = consensus.replaceAll("\\-", "");

		// create primer put them in a list and sort list (on score)
		// create all primer min-max bases long 
		int primerMinLen = Settings.getMinPrimerLength().getIntValue();
		int primerMaxLen = Settings.getMaxPrimerLength().getIntValue();
		int primerMinTM = Settings.getPrimerMinTM().getIntValue();
		int primerMaxTM = Settings.getPrimerMaxTM().getIntValue();
		Dimer.setDimerLengthThreashold(Settings.getDimerReportThreashold().getIntValue());
		
		long nCount = 0;
		int selectionStartPos = getFirstSelectedPositionX();
		for(int winSize = primerMinLen; winSize <= primerMaxLen; winSize ++){

			int offset = 0;		
			int startPos = offset;
			int endPos = offset + winSize;

			logger.info(consensus.length());
			logger.info( endPos);

			while(endPos <= consensus.length()){
				String primerSeq = consensus.substring(startPos, endPos);
				Primer aPrimer = new Primer(primerSeq, selectionStartPos + startPos);
				// only add primers below score
				long degenFold = aPrimer.getDegenerateFold();
				double baseStackTM = aPrimer.getBaseStackingAvgTm();
				if(degenFold <= PRIMER_MAX_DEGENERATE_SCORE){
					if(baseStackTM >= primerMinTM && baseStackTM <= primerMaxTM){
						allPrimers.add(aPrimer);
					}
				}
				offset ++;
				startPos = offset;
				endPos = offset + winSize;
				nCount ++;
			}
			logger.info("winSize=" + winSize);
		}
		logger.info("Primers tested:" + nCount);

		Collections.sort(allPrimers);


		return allPrimers;
		// https://ecom.mwgdna.com/services/webgist/mops.tcl?ot=OLIGO_ALC_UNMOD&oligoSequence=RTTGCTYRAKACTCGGTRA&ShowModiTables=OFF&oligo_name=&mod3=&mod5=&modx=&modz=&ot=OLIGO_ALC_UNMOD&oligo_name=&oligoSequence=RTT+GCT+YYY+RAK+ACT+CGG+TRA&action=properties&next_url=

	}
	
	/*
	 * 
	 * 	Selection section
	 * 
	 * 
	 */

	private String getSelectionAsNucleotides(){
		return sequences.getSelectionAsNucleotides();
	}

	public void copySelectionToClipboardAsFasta(){
		StringWriter writer = new StringWriter();
		sequences.writeSelectionAsFasta(writer);
		// Set to clipboard
		StringSelection ss = new StringSelection(writer.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
	
	public void copySelectionToClipboardAsNucleotides(){
		// Set to clipboard
		StringSelection ss = new StringSelection(getSelectionAsNucleotides());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
	
	public void copySelectionNames() {
		// Set to clipboard
		logger.info("copy sel names");
		String names = sequences.getSelectionNames();
		StringSelection ss = new StringSelection(names);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		Toolkit.getDefaultToolkit().getSystemSelection().setContents(ss, null);
	}
	


	public boolean isBaseSelected(int x, int y) {
		return sequences.isBaseSelected(x, y);
	}

	public void setSelectionAt(int xPos, int yPos) {
		setSelectionAt(xPos, yPos, false);

	}

	public void setSelectionAt(int xPos, int yPos, boolean clearFirst) {
		sequences.setSelectionAt(xPos, yPos, clearFirst);
		//fireSelectionChanged();
	}
	
	public void setSelectionWithin(Rectangle bounds) {
		sequences.setSelectionWithin(bounds);
		//fireSelectionChanged();
	}

	public void reverseComplementAlignment(){
		sequences.reverseComplement();
		alignmentMeta.reverse();
	}
	
	public void reverseComplementFullySelectedSequences(){
		sequences.reverseComplementFullySelectedSequences();
	}

	/*
	 * 
	 * Find
	 * 
	 */
	public FindObject findInNames(FindObject findObj) {
		findObj = sequences.findInNames(findObj);
		return findObj;
	}
	
	public void clearFindLastPos() {
		nextFindSequenceNumber = 0;
		nextFindStartPos = 0;
	}
	
	public FindObject findAndSelectInSequences(FindObject findObj){
		findObj = sequences.findAndSelect(findObj);
		return findObj;
	}
	
	public Sequence getSequenceByName(String name){
		return sequences.getSequenceByName(name);
	}
	
	public Sequence getSequenceByID(int id){
		return sequences.getSequenceByID(id);
	}

	
	public void incReadingFrame() {
		int newReadFrame = this.getReadingFrame();
		newReadFrame ++;
		if(newReadFrame > 3){
			newReadFrame = 1;
		}
		this.setReadingFrame(newReadFrame);
	}

	public int getReadingFrame() {
		return this.readingFrame;
	}

	public void decReadingFrame() {
		int newReadFrame = this.getReadingFrame();
		newReadFrame --;
		if(newReadFrame < 1){
			newReadFrame = 3;
		}
		this.setReadingFrame(newReadFrame);
	}


	public List<Sequence> clearSelectedBases(boolean undoable){
		List<Sequence> affected =  sequences.replaceSelectedBasesWithGap(undoable);
		return affected;
	}

	public void deleteFullySelectedSequences(){
		sequences.deleteFullySelectedSequences();
	}
	
	public void deleteSelectedBases() {
		
		int first = sequences.getFirstSelectedWholeColumn();
		// If whole column is selected do a bit different since we also want to delete
		// in meta
		if(first != -1){
			int last = sequences.getLastSelectedWholeColumn();
			
			logger.info("first" + first);
			logger.info("last" + last);
			
			// create a bit-mask with pos to delete
			boolean[] deleteMask = new boolean[getMaxX()];
			for(int n = first; n <= last; n++){
				deleteMask[n] = true;
			}
			sequences.deleteBasesInAllSequencesFromMask(deleteMask);
			//and finally remove in AlignmentMeta(excludes, codonpos & charset)
			//logger.info(alignmentMeta.getCodonPositions().getLength());
			alignmentMeta.removeFromMask(deleteMask);
			//logger.info(alignmentMeta.getCodonPositions().getLength());

		}
		// only delete in selected sequences
		else{	
			List<Sequence> affected =  sequences.deleteSelectedBases();
		}
	}

	public String getConsensus(){
		return sequences.getConsensus();
	}

	public void removeVerticalGaps(){
		String cons = getConsensus();
		
		logger.info("done cons" + cons);
		
		logger.info('-' == cons.charAt(0));
		
		// if there is a gap
		if(cons.indexOf(SequenceUtils.GAP_SYMBOL) >= 0){		
			
			logger.info("there is a gap in cons");
			
			// create a bit-mask with pos to delete
			boolean[] deleteMask = new boolean[cons.length()];
			for(int n = 0; n < deleteMask.length; n++){
				if(cons.charAt(n) == SequenceUtils.GAP_SYMBOL){
					deleteMask[n] = true;
				}
			}	
			sequences.deleteBasesInAllSequencesFromMask(deleteMask);

			//and finally remove in AlignmentMeta(excludes, codonpos & charset)
			alignmentMeta.removeFromMask(deleteMask);
		}

	}

	public void addFasta(String clipboardSelection) {
		try {
			AlignmentListModel sequencesFromClipboard = seqFactory.createFastaSequences(new StringReader(clipboardSelection));
			sequences.addAll(sequencesFromClipboard);
		} catch (AlignmentImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addNewSequence() {
			int minLen = sequences.getShortestSequenceLength();
			int maxLen = sequences.getLongestSequenceLength();
			int len;
			if(maxLen == minLen){
				len = maxLen;
			}else{
				len = 1;
			}
			String seqName = "New_sequence";
			
			byte[] bases = new byte[len];
			Arrays.fill(bases,(byte)'-');
			
			// TODO this is not working on file sequences 
			Sequence newSeq = new InMemoryBasicSequence(seqName, bases);
			
			Point lastSel = sequences.getLastSelectedPos();
			
//			logger.info("lastSel.y" + lastSel.y);
//			logger.info("sequences.getSize()" + sequences.getSize());
			
			if(lastSel != null && lastSel.y + 1 <  sequences.getSize()){
				sequences.add(lastSel.y +1, newSeq);
			}else{
				sequences.add(newSeq);
			}
	}
	
	
	public void addSequences(File additionalFile) {
		addSequences(additionalFile, 0);
	}
	
	public void addSequences(File additionalFile, int index) {
		try {
			AlignmentListModel additionalSequences = seqFactory.createSequences(additionalFile);
			sequences.addAll(index, additionalSequences);

		} catch (AlignmentImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/*
	 * 
	 * 
	 * Start Aligner section
	 * 
	 * 
	 * 
	 */
	public void clearSelectionOffset() {
		sequences.setSelectionOffset(0);
	}
	
	public List<Sequence> moveSelectionRight(boolean undoable){
		logger.info("move");
		List<Sequence> previousState = sequences.moveSelectedResiduesRightIfGapIsPresent(undoable);
		return previousState;
	}

	public List<Sequence> moveSelectionLeft(boolean undoable) {
		List<Sequence> previousState = sequences.moveSelectedResiduesLeftIfGapIsPresent(undoable);
		return previousState;
	}

	public List<Sequence> moveSelection(int diff, boolean undoable) {
		List<Sequence> previousState = sequences.moveSelectedResiduesIfGapIsPresent(diff, undoable);
		return previousState;
	}
	public boolean isMoveSelectionLeftPossible() {
		return sequences.isGapPresentLeftOfSelection();
	}
	public boolean isMoveSelectionRightPossible() {
		return sequences.isGapPresentRightOfSelection();
	}
	
	public List<Sequence> deleteGapMoveLeft(boolean undoable) {
		List<Sequence> previousState = sequences.deleteGapMoveLeft(undoable);
		if(previousState.size()> 0){
			rightPadSequencesWithGapUntilEqualLength();
		}
		return previousState;
	}
	
	public List<Sequence> deleteGapMoveRight(boolean undoable) {
		List<Sequence> previousState = sequences.deleteGapMoveRight(undoable);
		if(previousState.size()> 0){
			rightPadSequencesWithGapUntilEqualLength();
		}
		return previousState;
	}

	public List<Sequence> insertGapLeftOfSelectionMoveRight(boolean undoable) {
		List<Sequence> previousState = sequences.insertGapLeftOfSelectedBase(undoable);
		if(previousState.size()> 0){
			rightPadSequencesWithGapUntilEqualLength();
		}
		return previousState;
	}
	
	public List<Sequence> insertGapRightOfSelectionMoveLeft(boolean undoable) {
		List<Sequence> previousState = sequences.insertGapRightOfSelectedBase(undoable);
		if(previousState.size()> 0){
			leftPadSequencesWithGapUntilEqualLength();
		}
		return previousState;
	}

	
	public boolean rightPadSequencesWithGapUntilEqualLength(){
		boolean wasPadded = sequences.rightPadWithGapUntilEqualLength();
//		if(wasPadded){
//			alignmentMeta.verifyLength(sequences.getLongestSequenceLength());
//		}
		return wasPadded;
		
	}
	
	public boolean leftPadSequencesWithGapUntilEqualLength() {
		boolean wasPadded = sequences.leftPadWithGapUntilEqualLength();
//		if(wasPadded){
//			alignmentMeta.verifyLength(sequences.getLongestSequenceLength());
//		}
		return wasPadded;
	}

	/*
	 * 
	 * End Aligner section
	 * 
	 */

	public long getSelectedBasesCount(){
		return sequences.getSelectionSize();
	}

	public Rectangle getSelectionAsMinRect(){
		if(! hasSelection()){
			return null;
		}
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		int seqIndex = 0;
		for(int n = 0; n < sequences.getSize(); n++){
			int[] selection = sequences.get(n).getSelectedPositions();
			if(selection.length > 0){
				minY = Math.min(minY, n);
				maxY = Math.max(maxY, n);
				minX = Math.min(minX, selection[0]);
				maxX = Math.max(minX, selection[selection.length - 1]);
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);

	}


	public void addOrRemoveSelectionToExcludes() {
		// check if anythin is excluded already - then remove
		// otherwise add
		boolean containsExcludedAlready = false;
		for(Sequence sequence : sequences){
			
			int[] selection = sequence.getSelectedPositions();
			if(sequences.isTranslated()){
				selection = getAlignmentMeta().reTranslatePositions(selection);
			}	
			
			if(selection != null){
				for(int n = 0; n < selection.length; n++){
					if(this.alignmentMeta.isExcluded(selection[n])){
						containsExcludedAlready = true;
						break;
					}
				}
			}
		}
		
		logger.info("containsExcludedAlready" + containsExcludedAlready);
		if(containsExcludedAlready){
			removeSelectionFromExcludes();
		}else{
			addSelectionToExcludes();
		}
	}
	
	public void addSelectionToExcludes() {
		for(Sequence sequence : sequences){
			
			int[] selection = sequence.getSelectedPositions();
			if(sequences.isTranslated()){
				selection = getAlignmentMeta().reTranslatePositions(selection);
			}
			
			if(selection != null){
				this.alignmentMeta.excludePositions(selection);
			}
		}
		//fireAlignmentMetaOnlyChanged();
		
	}

	public void removeSelectionFromExcludes() {
		for(Sequence sequence : sequences){
			
			int[] selection = sequence.getSelectedPositions();
			if(sequences.isTranslated()){
				selection = getAlignmentMeta().reTranslatePositions(selection);
			}
			
			if(selection != null){
				//this.alignmentMeta.doNotexcludePositions(selection);
				
				for(int n = 0; n < selection.length; n++){
					this.alignmentMeta.getExcludes().getPositionsBooleanArray()[selection[n]] = false;
				}
			}
		}
		//fireAlignmentMetaOnlyChanged();
	}

	public void setSelectionAsCoding(int startOffset) {
		Rectangle bounds = sequences.getSelectionBounds();
		
		if(sequences.isTranslated()){
			bounds = getAlignmentMeta().reTranslatePositions(bounds);
		}
		
		// TODO check that selection is continous
		
		if(bounds != null){
			this.alignmentMeta.getCodonPositions().addRange(bounds.x, bounds.x + bounds.width, startOffset);
		}

		//fireAlignmentMetaOnlyChanged();
	}

	public void setSelectionAsNonCoding() {
		setSelectionAsCoding(0);
	}

	public void complementAlignment() {	
		sequences.complement();
	}

	public boolean isEditedAfterLastSave() {
		return isEditedAfterLastSave;
	}

	public void setEditedAfterLastSave(boolean isEditedAfterLastSave) {
		this.isEditedAfterLastSave = isEditedAfterLastSave;
	}

/*
	public boolean isAllCharactersValid() {
		return getFirstMuscleInvalidCharacter().length() == 0;
	}
*/

	// TODO should be different depending on alignment class nuc or protein
	public String getFirstAlignmentProgInvalidCharacter() {
		String testChars = "?ÅÄÖ*";
		String invalidCharsInAlignment = "";
		for(int n = 0; n < testChars.length(); n++){
			char testChar = testChars.charAt(n);
			for(Sequence seq: getSequences()){
				if(seq.contains(testChar)){
					invalidCharsInAlignment += testChar;
					break; // breaking out of this is loop no reason to check other seq
				}
			}
		}
		return invalidCharsInAlignment;
	}


	public void setAlignmentFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public FileFormat getFileFormat() {
		return fileFormat;
	}

	public int getLongestSequenceName() {
		return sequences.getLongestSequenceName();
	}

	public int getCodonPosAt(int x) {
		return alignmentMeta.getCodonPosAt(x);
	}

	public void setReadingFrame(int readingFrame){
		if(readingFrame > 0 && readingFrame <=3){
			this.readingFrame = readingFrame;
			alignmentMeta.setReadingFrame(readingFrame);
		//	fireAlignmentMetaOnlyChanged();
		}
	}

	public boolean isFullCodonStartingAt(int x) {
		return alignmentMeta.isFullCodonStartingAt(x);
	}

	public boolean isExcluded(int x) {
		return alignmentMeta.isExcluded(x);
	}

	public AlignmentMeta getAlignentMetaCopy(){
		return alignmentMeta.getCopy();
	}

	public void setAlignentMeta(AlignmentMeta aliMeta) {
		this.alignmentMeta = aliMeta;
		//fireAlignmentMetaOnlyChanged();
	}

	public AlignmentMeta getAlignmentMeta() {
		return alignmentMeta;
	}

	public ArrayList<Integer> getAllCodonPositions(int i, boolean removeExcluded, int startPos, int endPosInclusive) {		
		return alignmentMeta.getAllCodonPositions(i, removeExcluded, startPos, endPosInclusive);
	}

	/*
	 * 
	 */
	public void selectDuplicates(){
		sequences.findAndSelectDuplicates();
	}
	
	public ArrayList<String> findDuplicateNames(){
		if(sequences != null){
			return sequences.findDuplicateNames();
		}else{
			return null;
		}
	}
	
	public void selectDuplicateNamesSequences(){
		sequences.selectDuplicateNamesSequences();
	}
	

	/*
	 * 
	 * This one is just dumpting to stdout
	 * 
	 */
	public void getStats() {
		
			// TODO might not work if no charset
			for(CharSet charSet: alignmentMeta.getCharsets()){
				
				// Start with a prototype as consensus
				int consensusVal[] = new int[sequences.getLongestSequenceLength()];
				int missingCount = 0;
				int nonemptySeqCount = 0;
				
				for(Sequence seq: sequences){
					boolean sequenceIsAllEmpty = true;
					int sequenceMissingCount = 0;
					for(int n = 0; n <consensusVal.length; n++){
						
						if(charSet.isPositionIncluded(n)){
							
							int baseVal = NucleotideUtilities.baseValFromBase(seq.getBaseAtPos(n));
							// Create consensus by bitwise OR of the bases in the same column
							// Skip if n or unknown
							if(baseVal == NucleotideUtilities.N || baseVal == NucleotideUtilities.UNKNOWN || baseVal == NucleotideUtilities.GAP){
								sequenceMissingCount ++;
							}else{
								consensusVal[n] = consensusVal[n] | baseVal;
								sequenceIsAllEmpty = false;
							}
							
						}
					}
					if(! sequenceIsAllEmpty){
						nonemptySeqCount ++;
						missingCount += sequenceMissingCount;
					}
				}
				
				// Create consensus from baseValues
				StringBuilder consensusBuilder = new StringBuilder();
				int invaribleCount = 0;
				int varibleCount = 0;
				for(int n = 0; n < consensusVal.length; n++){
					if(charSet.isPositionIncluded(n)){
						consensusBuilder.append(NucleotideUtilities.charFromBaseVal(consensusVal[n]));
						if(consensusVal[n] == NucleotideUtilities.A || consensusVal[n] == NucleotideUtilities.C || consensusVal[n] == NucleotideUtilities.G || consensusVal[n] == NucleotideUtilities.T){
							invaribleCount ++;
						}
						else{
							varibleCount ++;
						}
					}
				}
				String consensus = consensusBuilder.toString();	
				
				logger.info(charSet.getName());
				logger.info("consensus" + consensus);
				logger.info("invaribleCount" + invaribleCount);
				logger.info("variableCount" + varibleCount);
				logger.info("totalCount" + (invaribleCount + varibleCount));
				logger.info("missingCount" + missingCount);
				logger.info("nonemptySeqCount" + nonemptySeqCount);
				double totalBases = (invaribleCount + varibleCount) * nonemptySeqCount;
				double missingPercent = missingCount/totalBases;
				logger.info("missingPercent" + missingPercent);
			}
	}


	public ArrayList<Sequence> deleteEmptySequences() {
		ArrayList<Sequence> deleted = sequences.deleteEmptySequences();
		return deleted;
	}

	public void deleteSequence(Sequence sequence) {
		sequences.deleteSequence(sequence);
	}

	public int getFirstSelectedSequenceIndex() {
		int selectedIndex = -1;
		int n = 0;
		for(Sequence seq: sequences){
			if(seq.hasSelection()){
				selectedIndex = n;
				break;
			}
			n++;
		}
		return selectedIndex;
	}

	public void selectSequencesWithIndex(List<Integer> selectedIndex) {
		sequences.selectSequencesWithIndex(selectedIndex);
	}
	
	public void selectSequencesWithIndex(int[] selectedIndex) {
		sequences.selectSequencesWithIndex(selectedIndex);
	}

	public GeneticCode getGeneticCode() {
		return alignmentMeta.getGeneticCode();
	}
	
	public void setGeneticCode(GeneticCode genCode) {
		alignmentMeta.setGeneticCode(genCode);
		//fireAlignmentMetaOnlyChanged();
	}

	public boolean isUndoable() {
		boolean isUndoable = true;
		if(this.getSequences() != null){
			if(this.getSequences() instanceof FileSequenceAlignmentListModel){
				isUndoable = false;
			}
		}
		return isUndoable;
	}

	public boolean isNucleotideAlignment() {
		return (sequences.getSequenceType() == SequenceUtils.TYPE_NUCLEIC_ACID);
	}
	
	public boolean isAAAlignment() {
		return (sequences.getSequenceType() == SequenceUtils.TYPE_AMINO_ACID);
	}

	public boolean replaceSelectedWithChar(char newChar) {
		List<Sequence> replacedSeqs = sequences.replaceSelectedWithChar(newChar, isUndoable());
		if(replacedSeqs.size() > 0){
			return true;
		}else{
			return false;
		}
	}

	public void selectEverythingWithinGaps(Point point) {
		sequences.selectEverythingWithinGaps(point);
	}

	public void realignNucleotidesUseThisAAAlignmentAsTemplate(Alignment realignment) throws Exception {
		sequences.realignNucleotidesUseTheseAASequenceAsTemplate(realignment.getSequences());
	}

	public void deleteAllGaps() {
		sequences.deleteAllGaps();
	}

	public void padAndTrimSequences(){
		boolean wasPadded = sequences.rightPadWithGapUntilEqualLength();
		boolean wasTrimed = sequences.rightTrimSequencesRemoveGapsUntilEqualLength();
	}
	
	public void trimSequences(){
		boolean wasTrimed = sequences.rightTrimSequencesRemoveGapsUntilEqualLength();
	}

	public void selectAll(){
		sequences.selectAll();
	}
	
	public void selectionExtendRight() {
		sequences.selectionExtendRight();
	}
	
	public void selectionExtendLeft() {
		sequences.selectionExtendLeft();
	}
	
	public void invertSelection() {
		long startTime = System.currentTimeMillis();
		sequences.invertSelection();
		long endTime = System.currentTimeMillis();
		logger.info("Invert took" + (endTime - startTime) + " milliseconds");
	}

	public boolean isPositionValid(int x, int y) {
		return sequences.isPositionValid(x,y);
	}

	public boolean getSelectionAt(int x, int y) {
		return sequences.isBaseSelected(x, y);
	}

	public void setAllHorizontalSelectionAt(int y, boolean selection) {
		if(selection == true){
			sequences.selectSequenceWithIndex(y);
		}else{
			sequences.clearAllSelectionInSequenceWithIndex(y);
		}
	}

	public void copySelectionFromSequenceTo(int indexFrom, int indexTo) {
		sequences.copySelectionFromInto(indexFrom,indexTo);
		//fireSelectionChanged();
		
	}

	public void selectColumn(int columnIndex) {
		sequences.selectColumn(columnIndex);
	}
	
	public void clearColumnSelection(int columnIndex) {
		sequences.clearColumnSelection(columnIndex);
		//fireSelectionChanged();
	}

	public void copySelectionFromPosX1toX2(int x1, int x2) {
		sequences.copySelectionFromPosX1toX2(x1, x2);
		//fireSelectionChanged();
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	
	public boolean isEditMode() {
		return editMode;
	}

	public Point getFirstSelectedPosition() {
		return sequences.getFirstSelectedPos();
	}
	
	public int getFirstSelectedPositionX() {
		Point pos = sequences.getFirstSelectedPos();
		if(pos == null){
			return 0;
		}else{
			return pos.x;
		}
	}
	
	public int getFirstSelectedUngapedPositionX() {
		Point pos = sequences.getFirstSelectedUngapedPos();
		if(pos == null){
			logger.info("pos == null");
			return -1;
		}else{
			logger.info("pos == " + pos.x);
			return pos.x;
		}
	}

	public void sortSequencesByName() {
		sequences.sortSequencesByName();
	}
	
	public void sortSequencesByCharInSelectedColumn() {
		sequences.sortSequencesByCharInSelectedColumn(getHistogram());
	}

	public AliHistogram getHistogram(){
		return sequences.getHistogram();
	}

	/*
	private AliHistogram getTranslatedHistogram() {	
		return sequences.getTranslatedHistogram(new AATranslator(getAlignentMeta().getCodonPositions(),getGeneticCode()));	
	}
	*/

	public boolean hasSelection() {
		return sequences.hasSelection();
	}

	public List<Sequence> replaceSelectedCharactersWithThis(Alignment realignment) {
		boolean undoable = true;
		List<Sequence> affected = sequences.replaceSelectedCharactersWithThis(realignment.getSequences(), undoable);
		return affected;
	}

	public void saveSelectedSequencesAsFastaFile(File outFile, boolean useIDAsName) throws IOException {
		BufferedWriter buffWriter = new BufferedWriter(new FileWriter(outFile));
		sequences.writeSelectedSequencesAsFasta(buffWriter, useIDAsName);
		buffWriter.flush();
		buffWriter.close();
	}
	
	public void saveUnSelectedSequencesAsFastaFile(File outFile, boolean useIDAsName) throws IOException {
		BufferedWriter buffWriter = new BufferedWriter(new FileWriter(outFile));
		sequences.writeUnSelectedSequencesAsFasta(buffWriter, useIDAsName);
		buffWriter.flush();
		buffWriter.close();
	}

	public boolean isEditable() {
		boolean isEditable = false;
		if(sequences != null){
			isEditable = sequences.isEditable();
		}
		if(sequences.isTranslated()){
			isEditable = false;
		}
		return isEditable;
	}
	
	public boolean isSelectable(){
		return isSelectable;
	}

	public long getSelectionSize() {
		return sequences.getSelectionSize();
	}

	public void fileSequenceContentsChanged() {
		logger.info("fileSequenceContChanged");
//		if(alignmentMeta.getCodonPositions().size()getLength() == 0){
//			//logger.info("sequences.getLongestSequenceLength()" + sequences.getLongestSequenceLength());
//			alignmentMeta = new AlignmentMeta(this.geneticCode);
//		}
	}

	public void setTranslationOnePos(boolean showTranslationOnePos){
		if(isNucleotideAlignment()){
			sequences.setTranslation(showTranslationOnePos);
		}
	}
	
	public boolean isTranslatedOnePos(){
		return sequences.isTranslated();
	}
	
	public boolean hasFullySelectedSequences() {
		return sequences.hasFullySelectedSequences();
	}

	public void sortSequencesByThisModel(AlignmentListModel prevSeqOrder) {
		sequences.sortSequencesByThisModel(prevSeqOrder);
	}

	public int getSelectedColumnCount() {
		return sequences.getSelectedColumnCount();
	}

	public int getSelectedSequencesCount() {
		return sequences.getSelectedSequencesCount();
	}
	
	public List<Sequence> getSelectedSequences() {
		return sequences.getSelectedSequences();
	}

	public String getFirstSelectedSequenceName() {
		return sequences.getFirstSelectedName();
	}

	public List<Sequence> setFirstSelectedSequenceName(String newName) {
		List<Sequence> previous = sequences.setFirstSelectedName(newName);
		return previous;
	}

	public void selectAll(CharSet aCharSet) {
		sequences.selectAll(aCharSet);
	}

	public int countStopCodons() {
		int totalCount = 0;
		if(isNucleotideAlignment()){
	//		logger.info("is nucleotide");
			AATranslator aaTransSeq = new AATranslator(getAlignmentMeta().getCodonPositions(),getGeneticCode());
			for(Sequence seq: sequences){
				aaTransSeq.setSequence(seq);
	//			logger.info("seq len" + seq.getLength());
	//			logger.info(aaTransSeq.countStopCodon());
				totalCount += aaTransSeq.countStopCodon();	
			}
		}else{
			for(Sequence seq: sequences){
				totalCount += seq.countChar('*');	
			}
		}
		
		return totalCount;
	}

	public void saveFastaIndex() {
		logger.info(this.fileFormat);
		
		if(this.fileFormat == FileFormat.FASTA && sequences instanceof FileSequenceAlignmentListModel){
			FileSequenceAlignmentListModel model = (FileSequenceAlignmentListModel) getSequences();
			
			//String indexName = FileFormat.stripFileSuffixFromName(this.getAlignmentFile().getAbsolutePath());
			String indexName = this.getAlignmentFile().getAbsolutePath();
			indexName += ".fai";
			
			File indexFile = new File(indexName);
			
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(indexFile));
			
				Sequence firstSeq = sequences.get(0);
				
				
				int charsPerLine = 0;
				int newlinePos = firstSeq.indexOf('\n');
				if(newlinePos == -1){
					charsPerLine = firstSeq.getLength() + 2; // +2 because of .......
				}else{
					charsPerLine = newlinePos + 1 + 1; // +1 because of 0f index,  +1 because of LF;
				}
				
				int spacePerLine =  firstSeq.countChar(' ', 0, newlinePos);
				
				logger.info("newlinePos" + newlinePos);
				logger.info("spacePerLine" + spacePerLine);
				
				
				
				spacePerLine += 1; // +1 because of LF 
				int residuesPerLine = charsPerLine - spacePerLine;
				
//				int returnCount = firstSeq.countChar('\r');
//				int tabCount = firstSeq.countChar('\t');
//				int spaceCount = firstSeq.countChar(' ');
				
			//	int totalLen = firstSeq.getLength();
				//int lenWithoutWhite = totalLen - newlineCount - returnCount - tabCount - spaceCount;
				
			//	logger.info("lenWithoutWhite" + lenWithoutWhite);
				
				
				for(Sequence seq: sequences){
								
					FastaFileSequence fileSeq = (FastaFileSequence) seq;
					
					//String name = StringUtils.substring(fileSeq.getName(), 0, 50);
					String name = fileSeq.getName();

					out.write(name);
					
					out.write('\t');
					
					int length = fileSeq.getLength();
					logger.info("length" + length);
					int fullRows = length/charsPerLine;
					logger.info("fullRows" + fullRows);
					int diff = charsPerLine - residuesPerLine;
					logger.info("diff" + diff);
					double decimalPart = (double)length/(double)charsPerLine - (double)fullRows;
					int remindDiff = (int) (decimalPart * (double)diff);
					logger.info("remindDiff" + remindDiff);
					diff = diff * fullRows;
					diff = diff + remindDiff;
					
					logger.info("diff" + diff);
					
					int lengthWithoutWhite = length - diff + 1; // don't know exactly why +1
					
					out.write("" + lengthWithoutWhite);
					
					out.write('\t');
					
					out.write("" + (fileSeq.getSequenceAfterNameStartPointer() -1));
					
					out.write('\t');
					
					out.write("" + residuesPerLine);
							
					out.write('\t');
					
					out.write("" + charsPerLine);
					
					out.write(LF);
								
				}	
				
				out.flush();
				out.close();
			
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			Messenger.showOKOnlyMessage(Messenger.NO_FASTA_INDEX_COULD_BE_SAVED);
		}
	}

	public void setTempSelection(Rectangle selectRectMatrixCoords) {
		getSequences().setTempSelection(selectRectMatrixCoords);
		
	}

	public Rectangle getTempSelection() {
		return getSequences().getTempSelection();
	}

	public void clearTempSelection() {
		getSequences().clearTempSelection();
		
	}

	public void moveSelectedSequencesToBottom() {
		getSequences().moveSelectedSequencesToBottom();
		
	}

	public void moveSelectedSequencesToTop() {
		getSequences().moveSelectedSequencesToTop();
		
	}
	
	public void moveSelectedSequencesUp() {
		getSequences().moveSelectedSequencesUp();
		
	}
	
	public void moveSelectedSequencesDown() {
		getSequences().moveSelectedSequencesDown();
		
	}

	public void moveSelectedSequencesTo(int index) {
		getSequences().moveSelectedSequencesTo(index);
	}

	public void selectIndices(List<Integer> allFoundIndices) {
		getSequences().getAlignmentSelectionModel().selectSequencesWithIndex(allFoundIndices);
	}

	
	
}
