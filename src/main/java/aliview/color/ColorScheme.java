package aliview.color;

import java.awt.Color;

import aliview.AminoAcid;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;

public interface ColorScheme {
	
	Color GREY_TRANSPARENT = new Color(0,0,0,140);

	public Color getBaseForegroundColor(int baseValue);
	
	public Color getBaseBackgroundColor(int baseValue);
	
	public Color getBaseSelectionForegroundColor(int baseValue);

	public Color getBaseSelectionBackgroundColor(int baseValue);
	
	public Color getBaseConsensusBackgroundColor();
	
	
	public Color getAminoAcidBackgroundColor(AminoAcid acid);

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid);

	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid);
	
	public Color getAminoAcidBackgroundColor(AminoAcid acid, int xPos, Alignment alignment);

	public Color getAminoAcidForgroundColor(AminoAcid acid, int xPos, Alignment alignment);

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid, int xPos, Alignment alignment);
	
	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid, int xPos, Alignment alignment);
	
	public Color getAminiAcidConsensusBackgroundColor();
	
	public String getName();
}
