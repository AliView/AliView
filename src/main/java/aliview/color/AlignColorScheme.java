package aliview.color;

import java.awt.Color;
import java.awt.Font;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;

public class AlignColorScheme implements ColorScheme {

	Color[] baseBackgroundColor;
	Color[] baseForegroundColor;
	Color[] baseSelectionBackgroundColor;
	Color[] baseSelectionForegroundColor;
	Color[] aminoAcidBackgroundColor;
	Color[] aminoAcidForegroundColor;
	Color[] aminoAcidSelectionBackgroundColor;
	Color[] aminoAcidSelectionForegroundColor;
	Color baseConsensusBackgroundColor;
	Color aminoAcidConsensusBackgroundColor;
	
	
	protected String colorSchemeName = "Default";
	
	public String getName() {
		return colorSchemeName;
	}
	
	public AlignColorScheme() {
		super();
		this.colorSchemeName = "Default";
		

		baseForegroundColor = new Color[64];
		baseForegroundColor[NucleotideUtilities.A] = new Color(1,128,1); //Color.green
		baseForegroundColor[NucleotideUtilities.C] = new Color(1,1,255); //Color.blue
		baseForegroundColor[NucleotideUtilities.G] = Color.black; 
		baseForegroundColor[NucleotideUtilities.TU] = new Color(255,1,1); // Color.red
		baseForegroundColor[NucleotideUtilities.R] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.Y] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.M] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.K] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.W] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.S] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.B] = Color.magenta; 
		baseForegroundColor[NucleotideUtilities.D] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.H] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.V] = Color.magenta;
		baseForegroundColor[NucleotideUtilities.N] =  Color.magenta;//new Color(180,200,250);
		baseForegroundColor[NucleotideUtilities.GAP] = Color.LIGHT_GRAY.darker(); 
		baseForegroundColor[NucleotideUtilities.UNKNOWN] = Color.cyan;
		
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
		
		baseConsensusBackgroundColor =  new Color(240,240,240);
			
		baseSelectionForegroundColor = new Color[64];
		baseSelectionForegroundColor[NucleotideUtilities.A]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.A] );
		baseSelectionForegroundColor[NucleotideUtilities.C]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.C] );
		baseSelectionForegroundColor[NucleotideUtilities.G]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.G] );
		baseSelectionForegroundColor[NucleotideUtilities.TU]      =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.TU]);
		baseSelectionForegroundColor[NucleotideUtilities.R]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.R] );
		baseSelectionForegroundColor[NucleotideUtilities.Y]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.Y] );
		baseSelectionForegroundColor[NucleotideUtilities.M]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.M] );
		baseSelectionForegroundColor[NucleotideUtilities.K]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.K] );
		baseSelectionForegroundColor[NucleotideUtilities.W]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.W] );
		baseSelectionForegroundColor[NucleotideUtilities.S]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.S] );
		baseSelectionForegroundColor[NucleotideUtilities.B]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.B] );
		baseSelectionForegroundColor[NucleotideUtilities.D]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.D] );
		baseSelectionForegroundColor[NucleotideUtilities.H]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.H] );
		baseSelectionForegroundColor[NucleotideUtilities.V]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.V] );
		baseSelectionForegroundColor[NucleotideUtilities.N]       =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.N] );
		baseSelectionForegroundColor[NucleotideUtilities.GAP]     =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.GAP]);
		baseSelectionForegroundColor[NucleotideUtilities.UNKNOWN] =createSelectionForegroundColor(baseForegroundColor[NucleotideUtilities.UNKNOWN]);
		

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
		
		
		
		/*
		 * 
		 * This is mesquite color-sceme
		 * 
		 */
		/*
		aminoAcidBackgroundColor = new Color[255];
		aminoAcidBackgroundColor[AminoAcid.A.intVal] = new Color(0x82e9ff);
		aminoAcidBackgroundColor[AminoAcid.C.intVal] = new Color(0xffabff);
		aminoAcidBackgroundColor[AminoAcid.D.intVal] = new Color(0xaea876);
		aminoAcidBackgroundColor[AminoAcid.E.intVal] = new Color(0x948f71);
		aminoAcidBackgroundColor[AminoAcid.F.intVal] = new Color(0x94b6ff);
		aminoAcidBackgroundColor[AminoAcid.G.intVal] = new Color(0xe306b0);
		aminoAcidBackgroundColor[AminoAcid.H.intVal] = new Color(0xb914ff);
		aminoAcidBackgroundColor[AminoAcid.I.intVal] = new Color(0x4f9bff);
		aminoAcidBackgroundColor[AminoAcid.K.intVal] = new Color(0xa70de3);
		aminoAcidBackgroundColor[AminoAcid.L.intVal] = new Color(0x0012b8);
		aminoAcidBackgroundColor[AminoAcid.M.intVal] = new Color(0x5eb5b5);
		aminoAcidBackgroundColor[AminoAcid.N.intVal] = new Color(0xff000c);
		aminoAcidBackgroundColor[AminoAcid.P.intVal] = new Color(0x14b86e);
		aminoAcidBackgroundColor[AminoAcid.Q.intVal] = new Color(0x810d0f);
		aminoAcidBackgroundColor[AminoAcid.R.intVal] = new Color(0xa172b7);
		aminoAcidBackgroundColor[AminoAcid.S.intVal] = new Color(0xffdf0a);
		aminoAcidBackgroundColor[AminoAcid.T.intVal] = new Color(0x781b1e);
		aminoAcidBackgroundColor[AminoAcid.V.intVal] = new Color(0x2ab528);
		aminoAcidBackgroundColor[AminoAcid.W.intVal] = new Color(0x9bc186);
		aminoAcidBackgroundColor[AminoAcid.Y.intVal] = new Color(0xea8f85);
		aminoAcidBackgroundColor[AminoAcid.STOP.intVal] = Color.black;
		aminoAcidBackgroundColor[AminoAcid.GAP.intVal] = Color.white;
		aminoAcidBackgroundColor[AminoAcid.X.intVal] = Color.white;
		*/
		
		/*
		 * 
		 * This is seaview color-sceme (almost - but I put shades on same color)
		 * 
		 */
		aminoAcidBackgroundColor = new Color[255];
		aminoAcidBackgroundColor[AminoAcid.A.intVal] = new Color(0x276eb7);
		aminoAcidBackgroundColor[AminoAcid.C.intVal] = new Color(0xe68080);
		aminoAcidBackgroundColor[AminoAcid.D.intVal] = new Color(0xcc4dcc);
		aminoAcidBackgroundColor[AminoAcid.E.intVal] = new Color(0x984097);
		aminoAcidBackgroundColor[AminoAcid.F.intVal] = new Color(0x1980e6);
		aminoAcidBackgroundColor[AminoAcid.G.intVal] = new Color(0xe6994d);
		aminoAcidBackgroundColor[AminoAcid.H.intVal] = new Color(0x19b3b3);
		aminoAcidBackgroundColor[AminoAcid.I.intVal] = new Color(0x4ea0f3);
		aminoAcidBackgroundColor[AminoAcid.K.intVal] = new Color(0xe63319);
		aminoAcidBackgroundColor[AminoAcid.L.intVal] = new Color(0x78a6d5);
		aminoAcidBackgroundColor[AminoAcid.M.intVal] = new Color(0x0f549b);
		aminoAcidBackgroundColor[AminoAcid.N.intVal] = new Color(0x19cc19);
		aminoAcidBackgroundColor[AminoAcid.P.intVal] = new Color(0xcccc00);
		aminoAcidBackgroundColor[AminoAcid.Q.intVal] = new Color(0x5ced5c);
		aminoAcidBackgroundColor[AminoAcid.R.intVal] = new Color(0xf6442c);
		aminoAcidBackgroundColor[AminoAcid.S.intVal] = new Color(0x029602);
		aminoAcidBackgroundColor[AminoAcid.T.intVal] = new Color(0x45c945);
		aminoAcidBackgroundColor[AminoAcid.V.intVal] = new Color(0x047df9);
		aminoAcidBackgroundColor[AminoAcid.W.intVal] = new Color(0x0355a9);
		aminoAcidBackgroundColor[AminoAcid.Y.intVal] = new Color(0x14c6c8);
		aminoAcidBackgroundColor[AminoAcid.STOP.intVal] = Color.darkGray;
		aminoAcidBackgroundColor[AminoAcid.GAP.intVal] = new Color(230,230,230);
		//aminoAcidBackgroundColor[AminoAcid.GAP.intVal] = Color.white;
		aminoAcidBackgroundColor[AminoAcid.X.intVal] = Color.white;
		
		aminoAcidConsensusBackgroundColor = new Color(240,240,240);
		
		aminoAcidForegroundColor = new Color[255];
		aminoAcidForegroundColor[AminoAcid.A.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.C.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.D.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.E.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.F.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.G.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.H.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.I.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.K.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.L.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.M.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.N.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.P.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.Q.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.R.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.S.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.T.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.V.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.W.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.Y.intVal] = Color.BLACK;
		aminoAcidForegroundColor[AminoAcid.STOP.intVal] = Color.CYAN;
		aminoAcidForegroundColor[AminoAcid.GAP.intVal] = Color.DARK_GRAY;
		aminoAcidForegroundColor[AminoAcid.X.intVal] = Color.CYAN;
		
		aminoAcidSelectionBackgroundColor = new Color[255];
		aminoAcidSelectionBackgroundColor[AminoAcid.A.intVal] = new Color(0x276eb7).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.C.intVal] = new Color(0xe68080).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.D.intVal] = new Color(0xcc4dcc).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.E.intVal] = new Color(0x984097).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.F.intVal] = new Color(0x1980e6).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.G.intVal] = new Color(0xe6994d).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.H.intVal] = new Color(0x19b3b3).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.I.intVal] = new Color(0x4ea0f3).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.K.intVal] = new Color(0xe63319).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.L.intVal] = new Color(0x78a6d5).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.M.intVal] = new Color(0x0f549b).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.N.intVal] = new Color(0x19cc19).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.P.intVal] = new Color(0xcccc00).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.Q.intVal] = new Color(0x5ced5c).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.R.intVal] = new Color(0xf6442c).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.S.intVal] = new Color(0x029602).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.T.intVal] = new Color(0x45c945).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.V.intVal] = new Color(0x047df9).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.W.intVal] = new Color(0x0355a9).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.Y.intVal] = new Color(0x14c6c8).brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.STOP.intVal] = Color.darkGray.brighter();
		aminoAcidSelectionBackgroundColor[AminoAcid.GAP.intVal] = Color.lightGray;
		aminoAcidSelectionBackgroundColor[AminoAcid.X.intVal] = Color.lightGray;
		
		aminoAcidSelectionForegroundColor = new Color[255];
		aminoAcidSelectionForegroundColor[AminoAcid.A.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.C.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.D.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.E.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.F.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.G.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.H.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.I.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.K.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.L.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.M.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.N.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.P.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.Q.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.R.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.S.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.T.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.V.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.W.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.Y.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.STOP.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.GAP.intVal] = Color.GRAY;
		aminoAcidSelectionForegroundColor[AminoAcid.X.intVal] = Color.GRAY;
	}

	public static Color createSelectionColor(Color color) {
		//return color.darker();
		
		int r = Math.max(color.getRed() - 85, 0);
		int g = Math.max(color.getGreen() - 85, 0);
		int b = Math.max(color.getBlue() - 85, 0);
		return new Color(r,g,b);
		
	}
	
	public static Color createSelectionForegroundColor(Color color) {
	//	return color.darker();
		
		int r = Math.min(color.getRed() + 150, 255);
		int g = Math.min(color.getGreen() + 150, 255);
		int b = Math.min(color.getBlue() + 150, 255);
		return new Color(r,g,b); //color.brighter().brighter().brighter().brighter().brighter();//Color.white;//color.brighter(); //Color.white;//
	}

	public Color getBaseBackgroundColor(int baseValue) {
		return baseBackgroundColor[baseValue];
	}

	public Color getBaseForegroundColor(int baseValue) {
		return baseForegroundColor[baseValue];
	}

	public Color getBaseSelectionForegroundColor(int baseValue) {
		return baseSelectionForegroundColor[baseValue];
	}
	
	public Color getBaseSelectionBackgroundColor(int baseValue) {
		return baseSelectionBackgroundColor[baseValue];
	}

	public Color getAminoAcidBackgroundColor(AminoAcid acid) {
		return aminoAcidBackgroundColor[acid.intVal];
	}

	public Color getAminoAcidForgroundColor(AminoAcid acid){
		return aminoAcidForegroundColor[acid.intVal];
	}

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid) {
		return aminoAcidSelectionBackgroundColor[acid.intVal];
	}

	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid) {
		return aminoAcidSelectionForegroundColor[acid.intVal];
	}

	public Color getAminoAcidBackgroundColor(AminoAcid acid, int xPos, Alignment aliment) {
		return getAminoAcidBackgroundColor(acid);
	}

	public Color getAminoAcidForgroundColor(AminoAcid acid, int xPos, Alignment aliment) {
		return getAminoAcidForgroundColor(acid);
	}

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid, int xPos, Alignment aliment) {
		return getAminoAcidSelectionBackgroundColor(acid);
	}

	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid, int xPos, Alignment aliment) {
		return getAminoAcidSelectionForegroundColor(acid);
	}

	public Color getBaseConsensusBackgroundColor() {
		return baseConsensusBackgroundColor;
	}

	public Color getAminoAcidConsensusBackgroundColor() {
		return aminoAcidConsensusBackgroundColor;
	}


	public Color[] getALLCompundColors() {
		// TODO Auto-generated method stub
		return null;
	}



}
