package aliview.cli;

import java.io.File;
import java.io.IOException;

import aliview.alignment.Alignment;
import aliview.importer.AlignmentFactory;
import aliview.importer.FileFormat;

public class Cli {
	
	public static void main(String[] args){
		
		File alignmentFile = new File("/home/anders/projekt/alignments/Woodsia_chloroplast_min1_20131029.nexus");

		Alignment aliment = AlignmentFactory.createNewAlignment(alignmentFile);
		
		aliment.reverseComplementAlignment();
		
		File outFile = new File("/home/anders/tmp/aliview-out-rc-cli.nexus");
		try {
			aliment.saveAlignmentAsFile(outFile, FileFormat.);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * 
		
		clustal
		fasta
		nexus
		phylip
		

aliview filenames ..... convert

aliview --convert

aliview <cmd default=--open> <file names>

No recognized command specified will default to open

aliview open <infiles>

aliview help

aliview convert --outformat=fasta <infile> <outfile>

aliview reversecomp <infile> <outfile>
		 */
		
	}

}
