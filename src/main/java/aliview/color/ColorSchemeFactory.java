package aliview.color;

import java.util.ArrayList;
import java.util.List;

public class ColorSchemeFactory {

	private static ColorScheme DEFAULT_COLOR_SCHEME = new DefaultColorScheme();
	private static ColorScheme DEFAULT_COLOR_SCHEME_FAST = new DefaultColorSchemeFast();
	private static ColorScheme SEAVIEW_COLOR_SCHEME = new SeaViewColorScheme();
	private static ColorScheme CLUSTAL_X_COLOR_SCHEME = new ClustalXColorScheme();
	private static ColorScheme DARK_COLOR_SCHEME_BLOCKS = new DarkColorScheme();
	private static ColorScheme DARK_COLOR_SCHEME_TEXT = new DarkColorSchemeText();
	private static ColorScheme DARK_NEON = new DarkNeonColorScheme();
	private static ColorScheme DARK_LIGHT_TEXT = new DarkLightTextColorScheme();
	private static List<ColorScheme> nucleotideColorSchemes = new ArrayList<ColorScheme>();
	private static List<ColorScheme> aaColorSchemes = new ArrayList<ColorScheme>();
	private static List<ColorScheme> allColorSchemes = new ArrayList<ColorScheme>();
	static{
        nucleotideColorSchemes.clear(); // Clear default list
		nucleotideColorSchemes.add(DEFAULT_COLOR_SCHEME);
		nucleotideColorSchemes.add(DEFAULT_COLOR_SCHEME_FAST);
		nucleotideColorSchemes.add(SEAVIEW_COLOR_SCHEME);	
		nucleotideColorSchemes.add(DARK_NEON);
        nucleotideColorSchemes.add(DARK_LIGHT_TEXT);
		nucleotideColorSchemes.add(DARK_COLOR_SCHEME_TEXT);
		aaColorSchemes.add(DEFAULT_COLOR_SCHEME);
		aaColorSchemes.add(SEAVIEW_COLOR_SCHEME);
		aaColorSchemes.add(CLUSTAL_X_COLOR_SCHEME);
		aaColorSchemes.add(DARK_COLOR_SCHEME_BLOCKS);
		aaColorSchemes.add(DARK_COLOR_SCHEME_TEXT);
		allColorSchemes.add(DEFAULT_COLOR_SCHEME);
		allColorSchemes.add(DEFAULT_COLOR_SCHEME_FAST);
		allColorSchemes.add(SEAVIEW_COLOR_SCHEME);
		allColorSchemes.add(CLUSTAL_X_COLOR_SCHEME);
		allColorSchemes.add(DARK_NEON);
        allColorSchemes.add(DARK_LIGHT_TEXT);
		allColorSchemes.add(DARK_COLOR_SCHEME_TEXT);
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
