package aliview.color;

import java.util.ArrayList;
import java.util.List;

public class ColorSchemeFactory {
	
	private static ColorScheme DEFAULT_COLOR_SCHEME = new AlignColorScheme();
	private static ColorScheme DEFAULT_COLOR_SCHEME_FAST = new DefaultColorSchemeFast();
	private static ColorScheme SEAVIEW_COLOR_SCHEME = new SeaViewColorScheme();
	private static ColorScheme CLUSTAL_X_COLOR_SCHEME = new ClustalXColorScheme();
	private static List<ColorScheme> nucleotideColorSchemes = new ArrayList<ColorScheme>();
	private static List<ColorScheme> aaColorSchemes = new ArrayList<ColorScheme>();
	private static List<ColorScheme> allColorSchemes = new ArrayList<ColorScheme>();
	static{
		nucleotideColorSchemes.add(DEFAULT_COLOR_SCHEME);
		nucleotideColorSchemes.add(DEFAULT_COLOR_SCHEME_FAST);
		nucleotideColorSchemes.add(SEAVIEW_COLOR_SCHEME);	
		aaColorSchemes.add(DEFAULT_COLOR_SCHEME);
		aaColorSchemes.add(SEAVIEW_COLOR_SCHEME);
		aaColorSchemes.add(CLUSTAL_X_COLOR_SCHEME);
		allColorSchemes.add(DEFAULT_COLOR_SCHEME);
		allColorSchemes.add(DEFAULT_COLOR_SCHEME_FAST);
		allColorSchemes.add(SEAVIEW_COLOR_SCHEME);
		allColorSchemes.add(CLUSTAL_X_COLOR_SCHEME);
	}

	public static ColorScheme getColorScheme(String name){	
		ColorScheme selectedScheme = DEFAULT_COLOR_SCHEME;
		for(ColorScheme aScheme: allColorSchemes){
			if(aScheme.getName().equals(name)){			
				selectedScheme = aScheme;				
			}
		}		
		return selectedScheme;
	}
	
	public static List<ColorScheme> getNucleotideColorSchemes(){
		return nucleotideColorSchemes;
	}
	
	public static List<ColorScheme> getAAColorSchemes(){
		return aaColorSchemes;
	}
	
	public static ColorScheme getDefaultColorScheme(){
		return DEFAULT_COLOR_SCHEME;
	}
	
}
