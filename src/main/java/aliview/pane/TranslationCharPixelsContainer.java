package aliview.pane;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.color.ColorScheme;
import aliview.gui.AlignmentPane;
import aliview.sequencelist.AlignmentListModel;


public class TranslationCharPixelsContainer {
	private static final Logger logger = Logger.getLogger(TranslationCharPixelsContainer.class);
	private CharPixelsContainer[] allAAContainers;

	public RGBArray getRGBArray(AminoAcid aa, byte residue){
		return allAAContainers[aa.intVal].getRGBArray(residue);
	}


	public static TranslationCharPixelsContainer createDefaultTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color bgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	public static TranslationCharPixelsContainer createSelectedTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				// It looks better without the selected color for foreground
				Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color bgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	public static TranslationCharPixelsContainer createLetterTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				Color fgColor = Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color bgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels(containerAcid.getCodeCharVal(), width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	public static TranslationCharPixelsContainer createSelectedLetterTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				Color fgColor = Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color bgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels(containerAcid.getCodeCharVal(), width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	////////////////////////
	
	
	public static TranslationCharPixelsContainer createDefaultTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid); //Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				//Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color aaBgColor =colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);
			//	Color nucBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal);//.brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	
	
	public static TranslationCharPixelsContainer createSelectedTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid); //Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				//Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color aaBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
			//	Color nucBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
			//	Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal).brighter();
				Color nucBgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal); //.brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	public static TranslationCharPixelsContainer createDefaultNoAALetterTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				//Color aaFgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid); //Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color aaFgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				//Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color aaBgColor =colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);
			//	Color nucBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal);//.brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	
	
	public static TranslationCharPixelsContainer createSelectedNoAALetterTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				//Color aaFgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid); //Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color aaFgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				//Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color aaBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
			//	Color nucBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
			//	Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal).brighter();
				Color nucBgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal); //.brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	public static TranslationCharPixelsContainer createLetterTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);//colorScheme.getBaseForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
			///	Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal).brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n, containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}
	
	public static TranslationCharPixelsContainer createSelectedLetterTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		TranslationCharPixelsContainer transContainer = new TranslationCharPixelsContainer();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color aaBgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);//colorScheme.getBaseForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
			///	Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal).brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n, containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
				
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
			
	}

}