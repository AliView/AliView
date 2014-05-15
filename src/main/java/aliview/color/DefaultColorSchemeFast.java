package aliview.color;

import java.awt.Color;

import aliview.NucleotideUtilities;


public class DefaultColorSchemeFast extends AlignColorScheme {

	public DefaultColorSchemeFast() {
		super();
		this.colorSchemeName =  "Default-variant - simpler, slightly faster";

		baseForegroundColor = new Color[64];
		Color NUC_FG = Color.DARK_GRAY.darker();
		Color IUPAC_FG = NUC_FG;
		baseForegroundColor[NucleotideUtilities.A] = NUC_FG;
		baseForegroundColor[NucleotideUtilities.C] = NUC_FG;
		baseForegroundColor[NucleotideUtilities.G] = NUC_FG;
		baseForegroundColor[NucleotideUtilities.TU] = NUC_FG;
		baseForegroundColor[NucleotideUtilities.R] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.Y] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.M] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.K] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.W] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.S] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.B] = IUPAC_FG; 
		baseForegroundColor[NucleotideUtilities.D] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.H] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.V] = IUPAC_FG;
		baseForegroundColor[NucleotideUtilities.N] = IUPAC_FG; 
		baseForegroundColor[NucleotideUtilities.GAP] = IUPAC_FG; 
		baseForegroundColor[NucleotideUtilities.UNKNOWN] = IUPAC_FG;

		baseBackgroundColor = new Color[64];
		baseBackgroundColor[NucleotideUtilities.A] = new Color(90,220,90); 
		baseBackgroundColor[NucleotideUtilities.C] = new Color(100,100,250);
		baseBackgroundColor[NucleotideUtilities.G] = new Color(90,90,90); 
		baseBackgroundColor[NucleotideUtilities.TU] = new Color(245,130,130); 
		baseBackgroundColor[NucleotideUtilities.R] = Color.white;
		baseBackgroundColor[NucleotideUtilities.Y] = Color.white;
		baseBackgroundColor[NucleotideUtilities.M] = Color.white;
		baseBackgroundColor[NucleotideUtilities.K] = Color.white;
		baseBackgroundColor[NucleotideUtilities.W] = Color.white;
		baseBackgroundColor[NucleotideUtilities.S] = Color.white;
		baseBackgroundColor[NucleotideUtilities.B] = Color.white; 
		baseBackgroundColor[NucleotideUtilities.D] = Color.white;
		baseBackgroundColor[NucleotideUtilities.H] = Color.white;
		baseBackgroundColor[NucleotideUtilities.V] = Color.white;
		baseBackgroundColor[NucleotideUtilities.N] = Color.white; 
		baseBackgroundColor[NucleotideUtilities.GAP] = new Color(250,250,250);
		baseBackgroundColor[NucleotideUtilities.UNKNOWN] = Color.white;

		Color NUC_SELECTED_FG = Color.WHITE;
		Color IUPAC_SELECTED_FG = NUC_FG;
		baseSelectionForegroundColor = new Color[64];
		baseSelectionForegroundColor[NucleotideUtilities.A] = NUC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.C] = NUC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.G] = NUC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.TU] = NUC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.R] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.Y] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.M] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.K] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.W] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.S] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.B] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.D] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.H] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.V] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.N] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.GAP] = IUPAC_SELECTED_FG;
		baseSelectionForegroundColor[NucleotideUtilities.UNKNOWN] = IUPAC_SELECTED_FG;


		baseSelectionBackgroundColor = new Color[64];	
		baseSelectionBackgroundColor[NucleotideUtilities.A]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.A]);
		baseSelectionBackgroundColor[NucleotideUtilities.C]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.C]);
		baseSelectionBackgroundColor[NucleotideUtilities.G]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.G]);
		baseSelectionBackgroundColor[NucleotideUtilities.TU]      =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.TU]);
		baseSelectionBackgroundColor[NucleotideUtilities.R]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.R]);
		baseSelectionBackgroundColor[NucleotideUtilities.Y]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.Y]);
		baseSelectionBackgroundColor[NucleotideUtilities.M]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.M]);
		baseSelectionBackgroundColor[NucleotideUtilities.K]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.K]);
		baseSelectionBackgroundColor[NucleotideUtilities.W]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.W]);
		baseSelectionBackgroundColor[NucleotideUtilities.S]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.S]);
		baseSelectionBackgroundColor[NucleotideUtilities.B]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.B]);
		baseSelectionBackgroundColor[NucleotideUtilities.D]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.D]);
		baseSelectionBackgroundColor[NucleotideUtilities.H]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.H]);
		baseSelectionBackgroundColor[NucleotideUtilities.V]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.V]);
		baseSelectionBackgroundColor[NucleotideUtilities.N]       =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.N]);
		baseSelectionBackgroundColor[NucleotideUtilities.GAP]     =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.GAP]);
		baseSelectionBackgroundColor[NucleotideUtilities.UNKNOWN] =  createSelectionColor(baseBackgroundColor[NucleotideUtilities.UNKNOWN]);

	}


}
