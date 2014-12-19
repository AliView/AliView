package aliview.genotype2phenotype;

import java.io.File;
import java.io.IOException;

public class Genotype2Phenotype {
	
	File inputFile;
	File outFile;
	
	
	public void createGenotypeFasta(File inputFile, File outFile) throws IOException{
		this.inputFile = inputFile;
		this.outFile = outFile;
		
		Image2AscII.createAscFile(inputFile, outFile);
	}
	
	
}
