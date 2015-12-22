package aliview.color;

import java.awt.Color;
import java.util.ArrayList;

import aliview.AminoAcid;
import aliview.gui.pane.CharPixelsContainer;

public class AACompColorT {
	
	public AminoAcid[] residues;
	public ColorT[] thresholds;
	public int intVal;
	public Color color;
	

	public AACompColorT(String residueString, Color color, ColorT[] threasholds) {
		String[] splitted = residueString.split(",");
		residues = new AminoAcid[splitted.length];
		for(int n = 0; n<splitted.length; n++){
			residues[n] = AminoAcid.getAminoAcidFromChar(splitted[n].charAt(0));
		}
		
		this.color = color;
		this.thresholds = threasholds;
	}
}
