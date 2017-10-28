package aliview.alignment;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.sequences.Sequence;

public class AAHistogram extends AliHistogram{

	public AAHistogram(int length) {
		super(length);
	}

	public void addSequence(Sequence seq){	
		for(int n = 0; n < seq.getLength(); n++){
			hist[n][AminoAcid.getAminoAcidFromByte(seq.getBaseAtPos(n)).intVal] ++;	
		}
	}

	public void addAminoAcid(int pos, AminoAcid acid){	
		hist[pos][acid.intVal] ++;
	}

	public double getSumNonGap(int x){
		int sum = 0;
		// add all
		for(int n = 0; n < hist[x].length; n++){
			sum += hist[x][n];
		}
		// and remove gaps
		sum -= hist[x][AminoAcid.GAP.intVal];
		return sum;
	}

}



