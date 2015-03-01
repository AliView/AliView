package aliview.pane;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.alignment.Alignment;
import aliview.sequences.Sequence;

public class SequencePainterAminoAcid extends SequencePainter {

	public SequencePainterAminoAcid(Sequence seq, int seqYPos, int clipPosY,
			int xMinSeqPos, int xMaxSeqPos, double seqPerPix, double charWidth,
			double charHeight, double highDPIScaleFactor, RGBArray clipRGB,
			AlignmentPane aliPane, Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, seqPerPix, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, aliPane, alignment);
		// TODO Auto-generated constructor stub
	}



	private static final Logger logger = Logger.getLogger(SequencePainterAminoAcid.class);
	

	
	@Override
	protected void copyPixels(Sequence seq, RGBArray clipRGB, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, AlignmentPane aliPane, Alignment alignment) {
		
		byte residue = seq.getBaseAtPos(seqXPos);
		
		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		AACharPixelsContainer pixContainerToUse = aliPane.charPixDefaultAA;
		byte byteToDraw = residue;
		AminoAcid acid = AminoAcid.getAminoAcidFromByte(residue);

		// adjustment if only diff to be shown
		if(aliPane.isHighlightDiffTrace()){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(seqYPos != aliPane.getDifferenceTraceSequencePosition() && acid == AminoAcid.getAminoAcidFromByte(alignment.getBaseAt(seqXPos,aliPane.getDifferenceTraceSequencePosition()))){
				byteToDraw = '.';
				pixContainerToUse = aliPane.charPixDefaultAA;
			}
		}

		// adjustment if non-cons to be highlighted
		if(aliPane.isHighlightNonCons()){
			if(acid == AminoAcid.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(alignment.getHistogram().isMajorityRuleConsensus(seqXPos,acid.intVal)){
				pixContainerToUse = aliPane.charPixConsensusAA;
			}
		}
		if(aliPane.isHighlightCons()){
			if(acid == AminoAcid.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! alignment.getHistogram().isMajorityRuleConsensus(seqXPos,acid.intVal)){
				pixContainerToUse = aliPane.charPixConsensusAA;
			}
		}

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			pixContainerToUse = aliPane.charPixSelectedAA;
		}

		RGBArray newPiece = pixContainerToUse.getRGBArray(byteToDraw, seqXPos, alignment);

		try {
			ImageUtils.insertRGBArrayAt(pixelPosX, pixelPosY, newPiece, clipRGB);
		} catch (Exception e) {
			logger.info("clipX" + pixelPosX);
			logger.info("clipY" + pixelPosY);
			logger.info("clipRGB.getHeight()" + clipRGB.getHeight());
			logger.info("clipRGB.getWidth()" + clipRGB.getScanWidth());
		}
	}

}
