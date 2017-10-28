package aliview.gui.pane;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.alignment.NucleotideHistogram;
import aliview.sequences.Sequence;

public class SequencePainterNucleotideTranslatedShowNucAndAcid extends SequencePainter {

	private static final Logger logger = Logger.getLogger(SequencePainterNucleotideTranslatedShowNucAndAcid.class);

	public SequencePainterNucleotideTranslatedShowNucAndAcid(Sequence seq,
			int seqYPos, int clipPosY, int xMinSeqPos, int xMaxSeqPos,
			double seqPerPix, double charWidth, double charHeight,
			double highDPIScaleFactor, RGBArray clipRGB, AlignmentPane aliPane,
			Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, seqPerPix, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, aliPane, alignment);
		// TODO Auto-generated constructor stub
	}





	@Override
	protected void copyPixels(Sequence seq, RGBArray clipArray, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, AlignmentPane aliPane, Alignment alignment){

		byte residue = seq.getBaseAtPos(seqXPos);
		AminoAcid acid = seq.getTranslatedAminoAcidAtNucleotidePos(seqXPos);
		// A small hack
		if(residue == 0){
			residue = ' ';
		}


		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		boolean isSelected = false;
		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			isSelected = true;
		}

		boolean isSecondPos = false;
		if(seq.isCodonSecondPos(seqXPos)){
			isSecondPos = true;
		}

		TranslationCharPixelsContainer pixContainerToUse = aliPane.charPixTranslationAndNucDefault;
		if(! aliPane.isDrawAminoAcidCode()){
			if(isSecondPos){
				if(isSelected){
					pixContainerToUse = aliPane.charPixTranslationAndNucSelected;
				}else{
					pixContainerToUse = aliPane.charPixTranslationAndNucDefault;
				}
			}else{
				if(isSelected){
					pixContainerToUse = aliPane.charPixTranslationAndNucSelectedNoAALetter;
				}else{
					pixContainerToUse = aliPane.charPixTranslationAndNucDefaultNoAALetter;
				}	
			}
		}else{
			if(isSecondPos){
				if(isSelected){
					pixContainerToUse = aliPane.charPixTranslationAndNucDominantNucSelected;
				}else{
					pixContainerToUse = aliPane.charPixTranslationAndNucDominantNuc;
				}
			}else{
				if(isSelected){
					pixContainerToUse = aliPane.charPixTranslationAndNucDominantNucNoAALetterSelected;
				}else{
					pixContainerToUse = aliPane.charPixTranslationAndNucDominantNucNoAALetter;
				}	
			}
		}

		RGBArray newPiece = pixContainerToUse.getRGBArray(acid, residue);


		try {
			ImageUtils.insertRGBArrayAt(pixelPosX, pixelPosY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("clipX" + pixelPosX);
			logger.info("clipY" + pixelPosY);
		}
	}

}
