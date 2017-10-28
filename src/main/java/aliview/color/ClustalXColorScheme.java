package aliview.color;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import aliview.AminoAcid;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;

public class ClustalXColorScheme extends DefaultColorScheme{

	static final Color CLUSTAL_RED =  new Color((float) 0.9, (float) 0.2, (float) 0.1);
	static final Color CLUSTAL_BLUE = new Color((float) 0.5, (float) 0.7, (float) 0.9);
	static final Color CLUSTAL_GREEN = new Color((float) 0.1, (float) 0.8, (float) 0.1);
	static final Color CLUSTAL_ORANGE = new Color((float) 0.9, (float) 0.6, (float) 0.3);
	static final Color CLUSTAL_CYAN =  new Color((float) 0.1, (float) 0.7, (float) 0.7);
	static final Color CLUSTAL_PINK = new Color((float) 0.9, (float) 0.5, (float) 0.5);
	static final Color CLUSTAL_MAGENTA = new Color((float) 0.8, (float) 0.3, (float) 0.8);
	static final Color CLUSTAL_YELLOW = new Color((float) 0.8, (float) 0.8, (float) 0.0);

	//	static Color SELECTED_CLUSTAL_RED =  new Color((float) 0.9, (float) 0.2, (float) 0.1);
	//	static Color CLUSTAL_BLUE = new Color((float) 0.5, (float) 0.7, (float) 0.9);
	//	static Color CLUSTAL_GREEN = new Color((float) 0.1, (float) 0.8, (float) 0.1);
	//	static Color CLUSTAL_ORANGE = new Color((float) 0.9, (float) 0.6, (float) 0.3);
	//	static Color CLUSTAL_CYAN =  new Color((float) 0.1, (float) 0.7, (float) 0.7);
	//	static Color CLUSTAL_PINK = new Color((float) 0.9, (float) 0.5, (float) 0.5);
	//	static Color CLUSTAL_MAGENTA = new Color((float) 0.8, (float) 0.3, (float) 0.8);
	//	static Color CLUSTAL_YELLOW = new Color((float) 0.8, (float) 0.8, (float) 0.0);

	static final Color COLOR_FOREGROUND = Color.BLACK;
	static final Color COLOR_FOREGROUND_SELECTED = Color.WHITE;
	static final Color COLOR_BACKGROUND_SELECTED = Color.LIGHT_GRAY.darker();

	static final Color COLOR_OTHER = Color.WHITE;

	public ClustalXColorScheme() {
		super();
	}

	private static ColorThreshold percent = new ColorThreshold("WLVIMAFCHP",0.6);
	private static ColorThreshold number = new ColorThreshold("WLVIMAFCHP",0.8);
	private static ColorThreshold minus = new ColorThreshold("ED",0.5);
	private static ColorThreshold plus = new ColorThreshold("KR",0.6);
	private static ColorThreshold g = new ColorThreshold("G",0.5);
	private static ColorThreshold n = new ColorThreshold("N",0.5);
	private static ColorThreshold q = new ColorThreshold("QE",0.5);
	private static ColorThreshold p = new ColorThreshold("P",0.5);
	private static ColorThreshold t = new ColorThreshold("TS",0.5);
	private static ColorThreshold A = new ColorThreshold("A",0.85);
	private static ColorThreshold C = new ColorThreshold("C",0.85);
	private static ColorThreshold D = new ColorThreshold("D",0.85);
	private static ColorThreshold E = new ColorThreshold("E",0.85);
	private static ColorThreshold F = new ColorThreshold("F",0.85);
	private static ColorThreshold G = new ColorThreshold("G",0.85);
	private static ColorThreshold H = new ColorThreshold("H",0.85);
	private static ColorThreshold I = new ColorThreshold("I",0.85);
	private static ColorThreshold K = new ColorThreshold("K",0.85);
	private static ColorThreshold L = new ColorThreshold("L",0.85);
	private static ColorThreshold M = new ColorThreshold("M",0.85);
	private static ColorThreshold N = new ColorThreshold("N",0.85);
	private static ColorThreshold P = new ColorThreshold("P",0.85);
	private static ColorThreshold Q = new ColorThreshold("Q",0.85);
	private static ColorThreshold R = new ColorThreshold("R",0.85);
	private static ColorThreshold S = new ColorThreshold("S",0.85);
	private static ColorThreshold T = new ColorThreshold("T",0.85);
	private static ColorThreshold V = new ColorThreshold("V",0.85);
	private static ColorThreshold W = new ColorThreshold("W",0.85);
	private static ColorThreshold Y = new ColorThreshold("Y",0.85);
	private static ColorThreshold ALWAYS_G = new ColorThreshold("G",0.0);
	private static ColorThreshold ALWAYS_P = new ColorThreshold("P",0.0);

	Color[] ALL_COMPOUND_COLORS = new Color[]{
			CLUSTAL_RED, CLUSTAL_BLUE, CLUSTAL_GREEN, CLUSTAL_ORANGE, CLUSTAL_CYAN, CLUSTAL_PINK, CLUSTAL_PINK, CLUSTAL_MAGENTA, CLUSTAL_YELLOW, COLOR_OTHER};

	AACompColorThreshold[] ALL_COMPOUNDS = new AACompColorThreshold[]{	
			new AACompColorThreshold("F,I,L,M,F,V,W", CLUSTAL_BLUE, new ColorThreshold[]{percent,number,A,C,F,H,I,L,M,V,W,Y,P,p}),
			new AACompColorThreshold("K,R", CLUSTAL_RED, new ColorThreshold[]{plus,K,R,Q}),
			new AACompColorThreshold("C", CLUSTAL_BLUE, new ColorThreshold[]{percent,number,A,C,F,H,I,L,M,V,W,S,P,p}),
			new AACompColorThreshold("C", CLUSTAL_PINK, new ColorThreshold[]{C}),
			new AACompColorThreshold("C", CLUSTAL_PINK, new ColorThreshold[]{C}),
			new AACompColorThreshold("G", CLUSTAL_ORANGE, new ColorThreshold[]{ALWAYS_G}),
			new AACompColorThreshold("P", CLUSTAL_YELLOW, new ColorThreshold[]{ALWAYS_P}),
			new AACompColorThreshold("H,Y", CLUSTAL_CYAN, new ColorThreshold[]{percent,number,A,C,F,H,I,L,M,V,W,Y,P,p}),
			new AACompColorThreshold("E", CLUSTAL_MAGENTA, new ColorThreshold[]{minus,D,E,q,Q}),
			new AACompColorThreshold("D", CLUSTAL_MAGENTA, new ColorThreshold[]{minus,D,E,n,N}),
			new AACompColorThreshold("A", CLUSTAL_BLUE, new ColorThreshold[]{percent,number,A,C,F,H,I,L,M,V,W,Y,P,p,T,S,G}),
			new AACompColorThreshold("T", CLUSTAL_GREEN, new ColorThreshold[]{percent,number,t,S,T}),
			new AACompColorThreshold("S", CLUSTAL_GREEN, new ColorThreshold[]{number,t,S,T}),
			new AACompColorThreshold("N", CLUSTAL_GREEN, new ColorThreshold[]{n,N,D}),
			new AACompColorThreshold("Q", CLUSTAL_GREEN, new ColorThreshold[]{q,Q,E,plus,K,R}),
	};


	private AACompColorThreshold getCompIfResidueWithinThreshold(AminoAcid acid, int xPos, Alignment alignment){
		ArrayList<AACompColorThreshold> allComp = getCompoundThresholdsFromAcid(acid);
		for(AACompColorThreshold aComp: allComp){
			for(ColorThreshold tHold: aComp.thresholds){
				if(tHold.threshold <= alignment.getHistogram().getProportionCount(xPos, tHold.acids)){
					return aComp;
				}
			}
		}
		return null;
	}

	private ArrayList<AACompColorThreshold> getCompoundThresholdsFromAcid(AminoAcid acid){
		ArrayList<AACompColorThreshold> all = new ArrayList<AACompColorThreshold>();
		for(AACompColorThreshold comp: ALL_COMPOUNDS){
			for(AminoAcid residue: comp.residues){
				if(residue == acid){
					all.add(comp);
				}
			}
		}
		return all;
	}

	public Color getAminoAcidBackgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		AACompColorThreshold compColorT = getCompIfResidueWithinThreshold(acid, xPos, alignment);
		if(compColorT != null){
			return compColorT.color;
		}
		else{
			return COLOR_OTHER;
		}
	}

	public Color getAminoAcidForgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		return COLOR_FOREGROUND;
	}

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		AACompColorThreshold compColorT = getCompIfResidueWithinThreshold(acid, xPos, alignment);
		if(compColorT != null){
			return compColorT.color;
		}
		else{
			return COLOR_BACKGROUND_SELECTED;
		}
	}

	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		return COLOR_FOREGROUND_SELECTED;
	}



	public String getName() {
		return "ClustalX";
	}

	public Color[] getALLCompundColors() {
		return ALL_COMPOUND_COLORS;
	}

}
