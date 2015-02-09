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


public class CompoundCharPixelsContainer {
	private static final Logger logger = Logger.getLogger(CompoundCharPixelsContainer.class);
	private HashMap<Color, CharPixelsContainer> colorContainerMap;
	
	// Below is for CompounColorScheme
	private ColorScheme colorScheme;
	
	public CompoundCharPixelsContainer() {
		logger.info("init CharPixContainer");
	}
	
	public RGBArray getRGBArray(byte residue, int xPos, Alignment alignment){
		AminoAcid aa = AminoAcid.getAminoAcidFromByte(residue);
		Color compoundColor = colorScheme.getAminoAcidBackgroundColor(aa, xPos, alignment);
		CharPixelsContainer pixContainer = colorContainerMap.get(compoundColor);

		return pixContainer.getRGBArray(residue);
	}
	
	
	public static CompoundCharPixelsContainer createDefaultCompoundColorImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		
		if(colorScheme.getALLCompundColors() == null || colorScheme.getALLCompundColors().length == 0){
			return null;
		}
		
		CompoundCharPixelsContainer compundContainer = new CompoundCharPixelsContainer();
		compundContainer.colorScheme = colorScheme;
		
		compundContainer.colorContainerMap = new HashMap<Color, CharPixelsContainer>(colorScheme.getALLCompundColors().length);
		
		
		for(Color bgColor : colorScheme.getALLCompundColors()){
				
			CharPixelsContainer container = new CharPixelsContainer();	
			for(int n = 0; n < container.backend.length; n++){	
				AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
				Color fgColor = colorScheme.getAminoAcidForgroundColor(aa);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}

			compundContainer.colorContainerMap.put(bgColor, container);

		}
		
		return compundContainer;
		
	}

	public static CompoundCharPixelsContainer createSelectedCompoundColorImpl(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		
		if(colorScheme.getALLCompundColors() == null || colorScheme.getALLCompundColors().length == 0){
			return null;
		}
		
		CompoundCharPixelsContainer compundContainer = new CompoundCharPixelsContainer();
		compundContainer.colorScheme = colorScheme;
		
		compundContainer.colorContainerMap = new HashMap<Color, CharPixelsContainer>(colorScheme.getALLCompundColors().length);
		
		for(Color bgColor : colorScheme.getALLCompundColors()){
			
			CharPixelsContainer container = new CharPixelsContainer();	
			for(int n = 0; n < container.backend.length; n++){	
				AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
				Color fgColor = colorScheme.getAminoAcidSelectionForegroundColor(aa);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor.darker(), font, minFontSize, fontCase);
			}		

			compundContainer.colorContainerMap.put(bgColor, container);

		}
		
		return compundContainer;
	}
	
}