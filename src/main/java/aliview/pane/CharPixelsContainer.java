package aliview.pane;

import java.awt.Color;
import java.awt.Font;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.color.ColorScheme;
import aliview.gui.AlignmentPane;
import aliview.sequencelist.SequenceListModel;


public class CharPixelsContainer {
	CharPixels[] backend = new CharPixels[256];
	private static final Logger logger = Logger.getLogger(CharPixelsContainer.class);
	
	// Below is for CompounColorScheme
	private ColorScheme colorScheme;
	
	public CharPixelsContainer() {
	//	logger.info("init CharPixContainer");
	}
	
	public RGBArray getRGBArray(byte target){
		return backend[target].getRGBArray();
	}
		
	public static CharPixelsContainer createDefaultNucleotideImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme){
		
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseBackgroundColor(baseVal);
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize);
		}
		return container;
	}
	
	public static CharPixelsContainer createSelectedNucleotideImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme){
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal);
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize);
		}
		return container;
	}

	public static CharPixelsContainer createConsensusNucleotideImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme){
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseConsensusBackgroundColor();
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize);
		}
		return container;
	}
	
	public static CharPixelsContainer createDefaultAAImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme) {
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
			Color fgColor = colorScheme.getAminoAcidForgroundColor(aa);
			Color bgColor = colorScheme.getAminoAcidBackgroundColor(aa);
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize);
		}
		return container;
	}
	
	public static CharPixelsContainer createSelectedAAImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme) {
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
			Color fgColor = colorScheme.getAminoAcidSelectionForegroundColor(aa);
			Color bgColor = colorScheme.getAminoAcidSelectionBackgroundColor(aa);
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize);
		}
		return container;
	}

	public static CharPixelsContainer createConsensusAAImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme) {
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
			Color fgColor = colorScheme.getAminoAcidForgroundColor(aa);
			Color bgColor = colorScheme.getBaseConsensusBackgroundColor();
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize);
		}
		return container;
	}
}