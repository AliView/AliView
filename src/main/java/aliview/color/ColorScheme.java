package aliview.color;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import aliview.AminoAcid;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;

public interface ColorScheme {
	
	public static Color GREY_TRANSPARENT = new Color(0,0,0,140);
	public static Color TRANSPARENT = new Color(0,0,0,0);

	public Color getBaseForegroundColor(int baseValue);
	
	public Color getBaseBackgroundColor(int baseValue);
	
	public Color getBaseSelectionForegroundColor(int baseValue);

	public Color getBaseSelectionBackgroundColor(int baseValue);
	
	public Color getBaseConsensusBackgroundColor();
	
	public String getName();
	
	
	// Below this should be moved to AAColorSceme (which in turn is an Compound Color scheme
		
	public Color getAminoAcidBackgroundColor(AminoAcid acid);

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid);

	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid);
	
	public Color getAminoAcidBackgroundColor(AminoAcid acid, int xPos, Alignment alignment);

	public Color getAminoAcidForgroundColor(AminoAcid acid);
	
	public Color getAminoAcidForgroundColor(AminoAcid acid, int xPos, Alignment alignment);

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid, int xPos, Alignment alignment);
	
	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid, int xPos, Alignment alignment);
	
	public Color getAminoAcidConsensusBackgroundColor();
	
	public boolean isCompoundAminoAcidColorScheme();

	public Color[] getAminoAcidBackgroundColors();
	

}
