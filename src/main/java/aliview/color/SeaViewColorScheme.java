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
		baseBackgroundColor[NucleotideUtilities.GAP] = new Color(225,225,225);
		baseBackgroundColor[NucleotideUtilities.UNKNOWN] = Color.lightGray;
			
		baseSelectionForegroundColor = new Color[64];
		
		baseSelectionForegroundColor[NucleotideUtilities.A] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.C] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.G] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.TU] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.R] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.Y] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.M] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.K] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.W] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.S] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.B] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.D] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.H] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.V] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.N] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.GAP] = Color.white;
		baseSelectionForegroundColor[NucleotideUtilities.UNKNOWN] = Color.white;
		
		baseSelectionBackgroundColor = new Color[64];
		baseSelectionBackgroundColor[NucleotideUtilities.A] = new Color(255 - 120,0,0); 
		baseSelectionBackgroundColor[NucleotideUtilities.C] = new Color(0,255 - 120,0);
		baseSelectionBackgroundColor[NucleotideUtilities.G] = new Color(255 - 120,255 - 120,0);  
		baseSelectionBackgroundColor[NucleotideUtilities.TU] = new Color(0,0,255 - 120); 
		baseSelectionBackgroundColor[NucleotideUtilities.R] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.Y] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.M] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.K] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.W] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.S] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.B] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.D] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.H] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.V] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.N] = Color.cyan.darker();
		baseSelectionBackgroundColor[NucleotideUtilities.GAP] = Color.darkGray;
		baseSelectionBackgroundColor[NucleotideUtilities.UNKNOWN] = Color.darkGray;

	}

}
