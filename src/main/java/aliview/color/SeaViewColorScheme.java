package aliview.color;

import java.awt.Color;

import aliview.NucleotideUtilities;

public class SeaViewColorScheme extends DefaultColorScheme{

	String COLORSCHEMENAME = "SeaView";

	public String getName() {
		return COLORSCHEMENAME;
	}

	public SeaViewColorScheme() {
		super();

		baseForegroundColor = new Color[64];
		baseForegroundColor[NucleotideUtilities.A] = Color.black;
		baseForegroundColor[NucleotideUtilities.C] = Color.black;
		baseForegroundColor[NucleotideUtilities.G] = Color.black; 
		baseForegroundColor[NucleotideUtilities.TU] = Color.black;
		baseForegroundColor[NucleotideUtilities.R] = Color.black;
		baseForegroundColor[NucleotideUtilities.Y] = Color.black;
		baseForegroundColor[NucleotideUtilities.M] = Color.black;
		baseForegroundColor[NucleotideUtilities.K] = Color.black;
		baseForegroundColor[NucleotideUtilities.W] = Color.black;
		baseForegroundColor[NucleotideUtilities.S] = Color.black; 
		baseForegroundColor[NucleotideUtilities.B] = Color.black; 
		baseForegroundColor[NucleotideUtilities.D] = Color.black;
		baseForegroundColor[NucleotideUtilities.H] = Color.black;
		baseForegroundColor[NucleotideUtilities.V] = Color.black;
		baseForegroundColor[NucleotideUtilities.N] = Color.black; 
		baseForegroundColor[NucleotideUtilities.GAP] = Color.black; 
		baseForegroundColor[NucleotideUtilities.UNKNOWN] = Color.cyan; 

		baseBackgroundColor = new Color[64];
		baseBackgroundColor[NucleotideUtilities.A] = new Color(255,0,0); 
		baseBackgroundColor[NucleotideUtilities.C] = new Color(0,255,0);
		baseBackgroundColor[NucleotideUtilities.G] = new Color(255,255,0); 
		baseBackgroundColor[NucleotideUtilities.TU] = new Color(0,0,255); 
		Color IUPAC_BG =  new Color(225,225,225).darker();
		baseBackgroundColor[NucleotideUtilities.R] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.Y] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.M] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.K] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.W] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.S] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.B] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.D] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.H] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.V] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.N] = IUPAC_BG;
		baseBackgroundColor[NucleotideUtilities.GAP] = IUPAC_BG.brighter();
		baseBackgroundColor[NucleotideUtilities.UNKNOWN] = IUPAC_BG.brighter();

		baseSelectionForegroundColor = new Color[64];

		Color baseSelectionForegroundColorDefault = Color.black.brighter().brighter();

		baseSelectionForegroundColor[NucleotideUtilities.A] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.C] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.G] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.TU] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.R] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.Y] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.M] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.K] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.W] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.S] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.B] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.D] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.H] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.V] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.N] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.GAP] = baseSelectionForegroundColorDefault;
		baseSelectionForegroundColor[NucleotideUtilities.UNKNOWN] = baseSelectionForegroundColorDefault;

		baseSelectionBackgroundColor = new Color[64];
		baseSelectionBackgroundColor[NucleotideUtilities.A] = new Color(255 - 120,0,0); 
		baseSelectionBackgroundColor[NucleotideUtilities.C] = new Color(0,255 - 120,0);
		baseSelectionBackgroundColor[NucleotideUtilities.G] = new Color(255 - 120,255 - 120,0);  
		baseSelectionBackgroundColor[NucleotideUtilities.TU] = new Color(0,0,255 - 120); 

		Color IUPAC_BG_SELECTED =  new Color(225,225,225).darker().darker();

		baseSelectionBackgroundColor[NucleotideUtilities.R] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.Y] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.M] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.K] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.W] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.S] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.B] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.D] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.H] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.V] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.N] = IUPAC_BG_SELECTED;
		baseSelectionBackgroundColor[NucleotideUtilities.GAP] = IUPAC_BG_SELECTED.brighter();
		baseSelectionBackgroundColor[NucleotideUtilities.UNKNOWN] = IUPAC_BG_SELECTED.brighter();

	}

}
