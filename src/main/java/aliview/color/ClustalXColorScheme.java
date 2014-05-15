package aliview.color;

import java.awt.Color;
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
	
	private static ColorT percent = new ColorT("WLVIMAFCHP",0.6);
	private static ColorT number = new ColorT("WLVIMAFCHP",0.8);
	private static ColorT minus = new ColorT("ED",0.5);
	private static ColorT plus = new ColorT("KR",0.6);
	private static ColorT g = new ColorT("G",0.5);
	private static ColorT n = new ColorT("N",0.5);
	private static ColorT q = new ColorT("QE",0.5);
	private static ColorT p = new ColorT("P",0.5);
	private static ColorT t = new ColorT("TS",0.5);
	private static ColorT A = new ColorT("A",0.85);
	private static ColorT C = new ColorT("C",0.85);
	private static ColorT D = new ColorT("D",0.85);
	private static ColorT E = new ColorT("E",0.85);
	private static ColorT F = new ColorT("F",0.85);
	private static ColorT G = new ColorT("G",0.85);
	private static ColorT H = new ColorT("H",0.85);
	private static ColorT I = new ColorT("I",0.85);
	private static ColorT K = new ColorT("K",0.85);
	private static ColorT L = new ColorT("L",0.85);
	private static ColorT M = new ColorT("M",0.85);
	private static ColorT N = new ColorT("N",0.85);
	private static ColorT P = new ColorT("P",0.85);
	private static ColorT Q = new ColorT("Q",0.85);
	private static ColorT R = new ColorT("R",0.85);
	private static ColorT S = new ColorT("S",0.85);
	private static ColorT T = new ColorT("T",0.85);
	private static ColorT V = new ColorT("V",0.85);
	private static ColorT W = new ColorT("W",0.85);
	private static ColorT Y = new ColorT("Y",0.85);
	private static ColorT ALWAYS_G = new ColorT("G",0.0);
	private static ColorT ALWAYS_P = new ColorT("P",0.0);

	
  AACompColorT[] ALL_COMPOUNDS = new AACompColorT[]{	
	new AACompColorT("F,I,L,M,F,V,W", CLUSTAL_BLUE, new ColorT[]{percent,number,A,C,F,H,I,L,M,V,W,Y,P,p}),
	new AACompColorT("K,R", CLUSTAL_RED, new ColorT[]{plus,K,R,Q}),
	new AACompColorT("C", CLUSTAL_BLUE, new ColorT[]{percent,number,A,C,F,H,I,L,M,V,W,S,P,p}),
	new AACompColorT("C", CLUSTAL_PINK, new ColorT[]{C}),
	new AACompColorT("C", CLUSTAL_PINK, new ColorT[]{C}),
	new AACompColorT("G", CLUSTAL_ORANGE, new ColorT[]{ALWAYS_G}),
	new AACompColorT("P", CLUSTAL_YELLOW, new ColorT[]{ALWAYS_P}),
	new AACompColorT("H,Y", CLUSTAL_CYAN, new ColorT[]{percent,number,A,C,F,H,I,L,M,V,W,Y,P,p}),
	new AACompColorT("E", CLUSTAL_MAGENTA, new ColorT[]{minus,D,E,q,Q}),
	new AACompColorT("D", CLUSTAL_MAGENTA, new ColorT[]{minus,D,E,n,N}),
	new AACompColorT("A", CLUSTAL_BLUE, new ColorT[]{percent,number,A,C,F,H,I,L,M,V,W,Y,P,p,T,S,G}),
	new AACompColorT("T", CLUSTAL_GREEN, new ColorT[]{percent,number,t,S,T}),
	new AACompColorT("S", CLUSTAL_GREEN, new ColorT[]{number,t,S,T}),
	new AACompColorT("N", CLUSTAL_GREEN, new ColorT[]{n,N,D}),
	new AACompColorT("Q", CLUSTAL_GREEN, new ColorT[]{q,Q,E,plus,K,R}),
	};
	
	
	public AACompColorT getCompIfResidueWithinThreshold(AminoAcid acid, int xPos, Alignment alignment){
		ArrayList<AACompColorT> allComp = getCompoundThresholdsFromAcid(acid);
		for(AACompColorT aComp: allComp){
			for(ColorT tHold: aComp.thresholds){
				if(tHold.threshold <= alignment.getHistogram().getProportionCount(xPos, tHold.acids)){
					return aComp;
				}
			}
		}
		return null;
	}

	private ArrayList<AACompColorT> getCompoundThresholdsFromAcid(AminoAcid acid){
		ArrayList<AACompColorT> all = new ArrayList<AACompColorT>();
		for(AACompColorT comp: ALL_COMPOUNDS){
			for(AminoAcid residue: comp.residues){
				if(residue == acid){
					all.add(comp);
				}
			}
		}
		return all;
	}

	public Color getAminoAcidBackgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		AACompColorT compColorT = getCompIfResidueWithinThreshold(acid, xPos, alignment);
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
		AACompColorT compColorT = getCompIfResidueWithinThreshold(acid, xPos, alignment);
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

}
