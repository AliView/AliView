package aliview;

import org.apache.commons.lang.StringUtils;

import utils.OSNativeUtils;


public class FileFormat {
	
	private String name;
	private String suffix;
	private String suffixWin;
	
	public static final FileFormat FILE_FASTA = new FileFormat("Fasta", "fasta", "fas");
	public static final FileFormat FASTA = new FileFormat("Fasta", "fasta", "fas");
	public static final FileFormat NEXUS = new FileFormat("Nexus", "nexus", "nex");
	public static final FileFormat NEXUS_CODONPOS_CHARSET = new FileFormat("NexusCodonposCharset", "codonpos.nexus", "codonpos.nex");
	public static final FileFormat NEXUS_SIMPLE = new FileFormat("NexusSimple", "nexus", "nex");
	public static final FileFormat PHYLIP = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat PHYLIP_RELAXED = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat PHYLIP_RELAXED_PADDED = new FileFormat("Phylip", "phy", "phy");
	public static final FileFormat MSF = new FileFormat("MSF", "msf", "msf");
	public static final FileFormat CLUSTAL = new FileFormat("Clustal", "aln", "aln");
	
	
	public static final FileFormat IMAGE_PNG = new FileFormat("png-image", "png", "png");
	
	// TODO should be different when not translated AminoAcid
	public static final FileFormat PHYLIP_TRANSLATED_AMINO_ACID = new FileFormat("PhylipAminoAcid", "translated.phy", "translated.phy");
	public static final FileFormat NEXUS_TRANSLATED_AMINO_ACID = new FileFormat("NexusTranslated", "translated.nexus", "translated.nex");
	public static final FileFormat FASTA_TRANSLATED_AMINO_ACID = new FileFormat("FastaTranslated", "translated.fasta", "translated.fas");
	

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
}
