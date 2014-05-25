package aliview.color;

import java.util.ArrayList;

import aliview.AminoAcid;

public class ColorT{
	
	public AminoAcid[] acids;
	public double threshold;
	
	public ColorT(String acidString, double threshold){
		this.acids = new AminoAcid[acidString.length()];
		for(int n = 0; n < acidString.length(); n++){
			acids[n] = AminoAcid.getAminoAcidFromChar(acidString.charAt(n));
		}
		this.threshold = threshold;
	}
}