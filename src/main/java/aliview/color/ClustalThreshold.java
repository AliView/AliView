package aliview.color;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import aliview.AminoAcid;

public class ClustalThreshold{

	public AminoAcid[] acids;
	public double threshold;
	public String identifyer;

	public ClustalThreshold(String identifyer, String acidString, double threshold){
		this.acids = new AminoAcid[acidString.length()];
		for(int n = 0; n < acidString.length(); n++){
			acids[n] = AminoAcid.getAminoAcidFromChar(acidString.charAt(n));
		}
		this.threshold = threshold;
		this.identifyer = identifyer;
	}

	public static ClustalThreshold parseColorThreshold(String text) throws NumberFormatException{

		String identifyer = StringUtils.substringBefore(text, "=").trim();

		double percent = Double.parseDouble(StringUtils.substringBetween(text, "=", "%").trim());

		String residues = StringUtils.substringAfter(text,"%");	
		residues = StringUtils.remove(residues, " ");
		residues = StringUtils.remove(residues, ":");

		return  new ClustalThreshold(identifyer, residues, percent);		
	}

	@Override
	public String toString() {
		return "identifyer: " + identifyer + " acids: " + " threshold: " + threshold; 
	}
}