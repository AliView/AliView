package aliview.importer;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

import utils.OSNativeUtils;


public class FileFormat {

	private static final Logger logger = Logger.getLogger(FileFormat.class);

	private String name;
	private String suffix;
	private String suffixWin;

	public static final FileFormat UNKNOWN = new FileFormat("Unknown", "", "");
	public static final FileFormat FILE_FASTA = new FileFormat("Fasta", "fasta", "fas");
	public static final FileFormat FASTA = new FileFormat("Fasta", "fasta", "fas");
	public static final FileFormat NEXUS = new FileFormat("Nexus", "nexus", "nex");
	public static final FileFormat NEXUS_CODONPOS_CHARSET = new FileFormat("NexusCodonposCharset", "codonpos.nexus", "codonpos.nex");
	public static final FileFormat NEXUS_SIMPLE = new FileFormat("NexusSimple", "nexus", "nex");
	public static final FileFormat PHYLIP = new FileFormat("Phylip", "phy", "phy");	
	public static final FileFormat PHYLIP_RELAXED = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat PHYLIP_RELAXED_PADDED_AKA_LONG_NAME_SEQUENTIAL = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat PHYLIP_RELAXED_PADDED_INTERLEAVED_AKA_LONG_NAME_INTERLEAVED = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat PHYLIP_STRICT_SEQUENTIAL_AKA_SHORT_NAME_SEQUENTIAL = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat PHYLIP_SHORT_NAME_INTERLEAVED = new FileFormat("Phylip", "phy", "phy");


	public static final FileFormat MSF = new FileFormat("MSF", "msf", "msf");
	public static final FileFormat CLUSTAL = new FileFormat("Clustal", "aln", "aln");

	public static final FileFormat IMAGE_PNG = new FileFormat("png-image", "png", "png");

	// TODO should be different when not translated AminoAcid
	public static final FileFormat PHYLIP_TRANSLATED_AMINO_ACID = new FileFormat("PhylipAminoAcid", "translated.phy", "translated.phy");
	public static final FileFormat NEXUS_TRANSLATED_AMINO_ACID = new FileFormat("NexusTranslated", "translated.nexus", "translated.nex");
	public static final FileFormat FASTA_TRANSLATED_AMINO_ACID = new FileFormat("FastaTranslated", "translated.fasta", "translated.fas");


	public static void main(String[] args) {
		//FileFormat ffFileTest = new FileFormat();
		//ffFileTest.isFileOfAlignmentFormat(new File("/vol2/big_data/SSURef_108_filtered_bacteria_pos_5389-24317.fasta"));
	}

	public FileFormat(String name, String suffix, String suffixWin) {
		this.name = name;
		this.suffix = suffix;
		this.suffixWin = suffixWin;
	}

	public static final String stripFileSuffixFromName(String name){
		String strippedName = StringUtils.substringBeforeLast(name, ".");
		return strippedName;
	}

	public String getSuffix(){
		if(OSNativeUtils.isWindows()){
			return suffixWin;
		}else{
			return suffix;
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static boolean isThisFasta(String seq){
		boolean isFasta = false;
		if(seq != null && seq.startsWith(">")){
			isFasta = true;
		}
		return isFasta;
	}


	public static FileFormat isFileOfAlignmentFormat(File seqFile){
		if(seqFile == null || !seqFile.exists()){
			return null;
		}

		long startTime = System.currentTimeMillis();
		FileFormat foundFormat = null;

		try {
			StringBuilder sequence = new StringBuilder();

			//File seqFile = new File("/home/anders/projekt/ormbunkar/analys/karin_alignment/ssu_pr2-99.fasta.diffenc2");

			//RandomAccessFile raf = new RandomAccessFile(seqFile, "r");
			OptimizedRandomAccessFile raf = new OptimizedRandomAccessFile(seqFile, "r");

			//BufferedReader r = new BufferedReader(this.reader);
			String line = "";
			String name = null;
			int nLine = 0;
			long nSeqCount = 0;

			byte[] buffer = new byte[200];

			if ((raf.read(buffer)) > 0) {

				String filestart = new String(buffer);

				// remove controlchar
				filestart = StringUtils.trim(filestart);

				// only first char
				String[] splitted = filestart.split("\n");
				String firstLine = splitted[0];

				logger.info("firstLine" + firstLine);

				if(firstLine.startsWith(">")){
					foundFormat = FileFormat.FASTA;
				}else if(StringUtils.containsIgnoreCase(firstLine, "NEXUS")){
					foundFormat = FileFormat.NEXUS;
				}else if(ClustalImporter.isStringValidFirstLine(firstLine)){
					foundFormat = FileFormat.CLUSTAL;
				}else if(MSFImporter.isStringValidFirstLine(firstLine)){
					foundFormat = FileFormat.MSF;
				}else if(PhylipImporter.isStringValidFirstLine(firstLine)){
					foundFormat = FileFormat.PHYLIP;
				}

			}

			long endTime = System.currentTimeMillis();
			logger.info("check fileformat took " + (endTime - startTime) + " milliseconds, found:" + foundFormat);

		}catch(Exception exc){
			exc.printStackTrace();
			// not file format skip
		}

		return foundFormat;
	}

	public static boolean isThisSequenceFile(String fileName) {
		boolean isSequenceFile = false;
		if(fileName != null){
			File testFile = new File(fileName);
			FileFormat format = isFileOfAlignmentFormat(testFile);
			if(format != null){
				isSequenceFile = true;
			}
		}
		return isSequenceFile;
	}
}
