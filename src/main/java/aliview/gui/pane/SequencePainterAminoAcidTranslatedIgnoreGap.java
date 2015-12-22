package aliview.gui.pane;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.alignment.Alignment;
import aliview.sequences.AminoAcidAndPosition;
import aliview.sequences.Sequence;

public class SequencePainterAminoAcidTranslatedIgnoreGap extends SequencePainter {

	public SequencePainterAminoAcidTranslatedIgnoreGap(Sequence seq,
			int seqYPos, int clipPosY, int xMinSeqPos, int xMaxSeqPos,
			double seqPerPix, double charWidth, double charHeight,
			double highDPIScaleFactor, RGBArray clipRGB, AlignmentPane aliPane,
			Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, seqPerPix, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, aliPane, alignment);
		// TODO Auto-generated constructor stub
	}



	private static final Logger logger = Logger.getLogger(SequencePainterAminoAcidTranslatedIgnoreGap.class);

	

	@Override
	protected void copyPixels(Sequence seq, RGBArray clipRGB, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, AlignmentPane aliPane, Alignment alignment) {

		AminoAcidAndPosition aaAndPos = seq.getNoGapAminoAcidAtNucleotidePos(seqXPos);
		int acidStartPos = aaAndPos.position;
		AminoAcid acid = aaAndPos.acid;	
		byte residue = seq.getBaseAtPos(seqXPos);
		

		TranslationCharPixelsContainer pixContainerToUse = aliPane.charPixTranslationDefault;
		TranslationCharPixelsContainer pixLetterContainerToUse = aliPane.charPixTranslationLetter;

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(seqXPos,seqYPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			pixContainerToUse = aliPane.charPixTranslationSelected;
			pixLetterContainerToUse = aliPane.charPixTranslationSelectedLetter;
		}

		RGBArray newPiece;

		if(! aliPane.isDrawAminoAcidCode()){	
			newPiece = pixContainerToUse.getRGBArray(acid, residue);
		}else{
			if(seqXPos == acidStartPos + 1){ // this line is changed
				newPiece = pixLetterContainerToUse.getRGBArray(acid, residue);
			}else{
				residue = ' ';		
				newPiece = pixContainerToUse.getRGBArray(acid, residue);
			}
		}

		try {
			ImageUtils.insertRGBArrayAt(pixelPosX, pixelPosY, newPiece, clipRGB);
		} catch (Exception e) {
			logger.info("clipX" + pixelPosX);
			logger.info("clipY" + pixelPosY);
		}
	}

}
