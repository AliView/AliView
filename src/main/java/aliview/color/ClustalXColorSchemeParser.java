package aliview.color;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;
import jebl.evolution.sequences.Nucleotides;

public class ClustalXColorSchemeParser extends DefaultColorScheme{
	private static final Logger logger = Logger.getLogger(ClustalXColorSchemeParser.class);
	private static final String LF = System.getProperty("line.separator");
	
	static final Color COLOR_FOREGROUND = Color.BLACK;
	static final Color COLOR_FOREGROUND_SELECTED = Color.WHITE;
	static final Color COLOR_BACKGROUND_SELECTED = Color.LIGHT_GRAY.darker();
	static final Color COLOR_OTHER = Color.WHITE;
	
	static ArrayList<ColorAndThreshold>[] residueConsensusColors;
	
	public static void main(String[] args) throws IOException {
		Logger.getRootLogger().setLevel(Level.DEBUG);
		File textfile = new File("/home/anders/projekt/maven/AliView/src/main/resources/colorschemes/clustal.txt");
		ClustalXColorSchemeParser colorParser = new ClustalXColorSchemeParser();
		colorParser.parseFile(textfile);
	}
	
	public ClustalXColorSchemeParser() throws IOException {
		super();
	}
	
	
	
	private static void parseFile(File textfile) throws IOException{
		
		String definition = FileUtils.readFileToString(textfile);
		// make sure there is an end-tag
		definition += LF + "@end";
		
		String rgbIndexBlock = StringUtils.substringBetween(definition, "@rgbindex", "@");
		HashMap<String, Color> allRgbColors = parseRgbColors(rgbIndexBlock);
		logger.debug(allRgbColors);
		
		String consensusBlock =  StringUtils.substringBetween(definition, "@consensus", "@");
		HashMap<String, ClustalThreshold> allConsensusThresholds = parseConsensusThresholds(consensusBlock);
		logger.debug(allConsensusThresholds);
			
		String colorBlock = StringUtils.substringBetween(definition, "@color", "@");
		residueConsensusColors = parseColorBlock(colorBlock, allRgbColors, allConsensusThresholds);
		logger.info(residueConsensusColors);
		for(ArrayList<ColorAndThreshold> cAndTList : residueConsensusColors){
			logger.debug(cAndTList);
		}

	}
	
	private static ArrayList<ColorAndThreshold>[] parseColorBlock(String colorBlock, HashMap<String, Color> allRgbColors, HashMap<String, ClustalThreshold> allConsensusThresholds) {
		ArrayList<ColorAndThreshold>[] residueConsensusColors = (ArrayList<ColorAndThreshold> []) new ArrayList[AminoAcid.HIGEST_AA_INT_VAL + 1];
		String[]lines = colorBlock.split("\n");
		for(String line : lines){
		   line = line.trim();
		   if(line.length() > 0){
			   
			   String residue = StringUtils.substringBefore(line, "=").trim();
			   AminoAcid acid = AminoAcid.getAminoAcidFromChar(residue.charAt(0));
			   
			   logger.debug(acid);
			   
			   ArrayList<ColorAndThreshold> colorAndThresholdList = residueConsensusColors[acid.intVal];
			   if(colorAndThresholdList == null){
				   colorAndThresholdList = new ArrayList<ColorAndThreshold>();
				   residueConsensusColors[acid.intVal] = colorAndThresholdList;
			   }
			 
			   if(line.contains(" if ")){
				   String colorName = StringUtils.substringBetween(line, "=", " if ").trim();
				   Color color = allRgbColors.get(colorName);
				   
				   String consensusIdentifyers = StringUtils.substringAfter(line," if ").trim();
				   consensusIdentifyers = StringUtils.remove(consensusIdentifyers, " ");
				   String[] splittedConsIdentifiers = StringUtils.split(consensusIdentifyers,":");
				   // Create list entry for every consensus threshold for this residue
				   for(String consensusIdentifier: splittedConsIdentifiers){
					   ClustalThreshold thold = allConsensusThresholds.get(consensusIdentifier);
					   ColorAndThreshold colAndThreshold = new ColorAndThreshold(color, thold);
					   colorAndThresholdList.add(colAndThreshold);  
				   }			   
			   }
			   // no if - then create and add a 0.0 threshold (ALWAYS) for thhis color
			   else{
				   String colorName = StringUtils.substringAfter(line, "=").trim();	   
				   Color color = allRgbColors.get(colorName);
				   ClustalThreshold thold = new ClustalThreshold("", acid.getCodeStringVal(), 0.0);		   
				   ColorAndThreshold colAndThreshold = new ColorAndThreshold(color, thold);
				   colorAndThresholdList.add(colAndThreshold);   
			   }

		   }
		}
		
		return residueConsensusColors;
		
	}

	private static HashMap<String, ClustalThreshold> parseConsensusThresholds(String consensusBlock) {
		HashMap<String, ClustalThreshold> allThresholds = new HashMap<String, ClustalThreshold>();
		String[]lines = consensusBlock.split("\n");
		for(String line : lines){
		   line = line.trim();
		   if(line.length() > 0){
			   ClustalThreshold colorThreshold = ClustalThreshold.parseColorThreshold(line);
			   allThresholds.put(colorThreshold.identifyer, colorThreshold);
		   }
		}
		return allThresholds;
	}

	private static HashMap<String, Color> parseRgbColors(String rgbIndexBlock) {
		HashMap<String, Color> allColors = new HashMap<String, Color>();
		String[]lines = rgbIndexBlock.split("\n");
		for(String line : lines){
		   line = line.trim();
		   if(line.length() > 0){
			   NamedColor namedColor = ColorUtils.parseColor(line);
			   allColors.put(namedColor.name, namedColor.color);
		   }
		}
		return allColors;
	}
	
	
	private Color getColorIfResidueWithinThreshold(AminoAcid acid, int xPos, Alignment alignment){
		ArrayList<ColorAndThreshold> listForThisAcid = getCompoundThresholdsFromAcid(acid);
		for(ColorAndThreshold colAndThold: listForThisAcid){
			ClustalThreshold tHold = colAndThold.threshold;
			if(tHold.threshold <= alignment.getHistogram().getProportionCount(xPos, tHold.acids)){
				return colAndThold.color;
			}
		}
		return null;
	}

	private ArrayList<ColorAndThreshold> getCompoundThresholdsFromAcid(AminoAcid acid){
		ArrayList<ColorAndThreshold> listForThisAcid = residueConsensusColors[acid.intVal];
		return listForThisAcid;
	}

	public Color getAminoAcidBackgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		Color bgColor = getColorIfResidueWithinThreshold(acid, xPos, alignment);
		if(bgColor == null){
			bgColor = COLOR_OTHER;
		}
		return bgColor;
	}

	public Color getAminoAcidForgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		return COLOR_FOREGROUND;
	}

	public Color getAminoAcidSelectionBackgroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		Color bgColor = getColorIfResidueWithinThreshold(acid, xPos, alignment);
		if(bgColor == null){
			bgColor = COLOR_BACKGROUND_SELECTED;
		}
		return bgColor;
	}

	public Color getAminoAcidSelectionForegroundColor(AminoAcid acid, int xPos, Alignment alignment) {
		return COLOR_FOREGROUND_SELECTED;
	}
	
	
	
	public String getName() {
		return "ClustalX";
	}
	

	public boolean isCompoundAminoAcidColorScheme() {
		return true;
	}
	
}
