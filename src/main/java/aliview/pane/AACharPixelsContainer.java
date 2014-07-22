package aliview.pane;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.color.ColorScheme;
import aliview.gui.AlignmentPane;
import aliview.sequencelist.SequenceListModel;


public class AACharPixelsContainer {
	private static final Logger logger = Logger.getLogger(AACharPixelsContainer.class);
	private CompoundCharPixelsContainer compoundContainer;
	private CharPixelsContainer container;
	
	// Below is for CompounColorScheme
	private ColorScheme colorScheme;
	
	
	public RGBArray getRGBArray(byte residue, int xPos, Alignment alignment){
		if(compoundContainer != null){
			return compoundContainer.getRGBArray(residue, xPos, alignment);
			
		}else{
			return container.getRGBArray(residue);
		}
	}
	
	public void setCompoundContainer(
			CompoundCharPixelsContainer compoundContainer) {
		this.compoundContainer = compoundContainer;
	}
	
	public void setContainer(CharPixelsContainer container) {
		this.container = container;
	}
	
}