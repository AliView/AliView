package aliview.alignment;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.sequences.Sequence;

public abstract class AliHistogram{
	int[][] hist;
	
	public AliHistogram(int length) {
		hist = new int[length][33];
	}
	
	public abstract void addSequence(Sequence seq);

	public int getValueCount(int x, int value){
		return hist[x][value];
	}
	
	public int getValueCount(int x, int[] values){
		int sum = 0;
		for(int value: values){
			sum += hist[x][value];
		}
		return sum;
	}
	
	public abstract double getSumNonGap(int x);
	
	public int getValueCount(int x, AminoAcid acid){
		return getValueCount(x, acid.intVal);
	}
	
	public double getValueCount(int x, AminoAcid[] acids){
		int sum = 0;
		for(AminoAcid acid: acids){
			sum += getValueCount(x, acid);
		}
		return sum;
	}
	
	public double getProportionCount(int x, AminoAcid acid){
		return (double)getValueCount(x,acid)/(double)getSumNonGap(x);
	}
	
	public double getProportionCount(int x, AminoAcid[] acids){
		return (double)getValueCount(x,acids)/(double)getSumNonGap(x);
	}
	
	public double getProportionCount(int x, int value){
		return (double)getValueCount(x,value)/(double)getSumNonGap(x);
	}
	
	public double getProportionCount(int x, int[] values){
		return (double)getValueCount(x,values)/(double)getSumNonGap(x);
	}
	
	public boolean isMajorityRuleConsensus(int x, int baseVal) {
		return (getProportionCount(x, baseVal) >= 0.5);
	}

}



