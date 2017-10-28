package aliview.phenotype2genotype;

import java.io.File;
import java.io.IOException;

public class Phenotype2Genootype {

	File inputFile;
	File outFile;


	public void createGenotypeFasta(File inputFile, File outFile) throws IOException{
		this.inputFile = inputFile;
		this.outFile = outFile;

		Image2AscII.createAscFile(inputFile, outFile);
	}


}
