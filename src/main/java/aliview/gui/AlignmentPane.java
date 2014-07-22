package aliview.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.image.MemoryImageSource;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import aliview.AATranslator;
import aliview.AliView;
import aliview.AminoAcid;
import aliview.Base;
import aliview.NucleotideUtilities;
import aliview.alignment.AAHistogram;
import aliview.alignment.AliHistogram;
import aliview.alignment.Alignment;
import aliview.alignment.NucleotideHistogram;
import aliview.color.ColorScheme;
import aliview.color.ColorUtils;
import aliview.pane.AACharPixelsContainer;
import aliview.pane.CharPixelsContainer;
import aliview.pane.CompoundCharPixelsContainer;
import aliview.pane.ImageUtils;
import aliview.pane.RGBArray;
import aliview.pane.TranslationCharPixelsContainer;
import aliview.sequences.Sequence;
import aliview.settings.Settings;
import aliview.utils.ArrayUtilities;


// HAS to be JPanel - JComponent is not enough for only partial cliprect when in jscrollpane when painting
// When JComponent only then I had to paint it all (maybe because of layoutmanager?)
public class AlignmentPane extends JPanel{
	private static final long serialVersionUID = 601195400946835871L;
	private static final Logger logger = Logger.getLogger(AlignmentPane.class);
	private static final double MIN_CHAR_SIZE = 0;
	private static final int MAX_CHAR_SIZE = 100;
	private static final double CHAR_HEIGHT_RATIO = 1.4;
	public static final int MAX_CHARSIZE_TO_DRAW = 6;
	public static final int INTERACTION_MODE_VIEW = 0;
	public static final int INTERACTION_MODE_EDIT = 1;
	private static final Color ALPHACOLOR = new Color(255, 255,255, 128 );
	//	public static final int INTERACTION_MODE_SELECT = 2;
	double charWidth = 10;
	double charHeight = 12;
	private Font baseFont = new Font(Font.MONOSPACED, Font.PLAIN, (int)charWidth);
	private Color ALIGNMENT_PANE_BG_COLOR = Color.WHITE;
	private Alignment alignment;

	private ColorScheme colorSchemeAminoAcid = Settings.getColorSchemeAminoAcid();
	private ColorScheme colorSchemeNucleotide = Settings.getColorSchemeNucleotide();
	private boolean highlightDiffTrace = false;
	private Rectangle tempSelectionRect;
	//	private InfoLabel infoLabel;
	private int interactionMode;
	// TODO This should instead be tracing a sequence instead of a position?
	private int differenceTraceSequencePosition = 0;
	private boolean showTranslation = false;
	private boolean showTranslationOnePos = false;
	private AlignmentRuler alignmentRuler;
	private boolean drawAminoAcidCode; 
	private boolean drawCodonPosRuler;
	private Rectangle lastClip = new Rectangle();
	private boolean rulerIsDirty;
	private boolean highlightNonCons;
	private boolean highlightCons;
	DrawCharBuffer drawCharBuffer = new DrawCharBuffer(5000);
	private boolean ignoreGapInTranslation;
	private byte byteToDraw;
	private CharPixelsContainer charPixDefaultNuc;
	private CharPixelsContainer charPixSelectedNuc;
	private CharPixelsContainer charPixConsensusNuc;
	private AACharPixelsContainer charPixDefaultAA;
	private AACharPixelsContainer charPixSelectedAA;
	private AACharPixelsContainer charPixConsensusAA;
	private TranslationCharPixelsContainer charPixTranslationDefault;
	private TranslationCharPixelsContainer charPixTranslationSelected;
	private TranslationCharPixelsContainer charPixTranslationLetter;
	private TranslationCharPixelsContainer charPixTranslationSelectedLetter;
	private boolean forceRepaintAll;

	public AlignmentPane() {
		//this.setDoubleBuffered(false);
		//this.setBackground(Color.white);
		//this.infoLabel = infoLabel;
		alignmentRuler = new AlignmentRuler(this);
		createAdjustedDerivedBaseFont();
		createCharPixelsContainers();
	}

	public boolean isOnlyDrawDiff() {
		return highlightDiffTrace;
	}

	public void setHighlightDiffTrace(boolean highlightDiff) {
		this.highlightDiffTrace = highlightDiff;
	}

	public void setHighlightNonCons(boolean b) {
		this.highlightNonCons = b;
	}

	public boolean isHighlightNonCons() {
		return highlightNonCons;
	}

	public void setHighlightCons(boolean b) {
		this.highlightCons = b;
	}

	public boolean isHighlightCons() {
		return highlightCons;
	}

	public void setDrawCodonPosRuler(boolean drawCodonPosRuler) {
		this.drawCodonPosRuler = drawCodonPosRuler;
	}

	public boolean getDrawCodonPosRuler() {
		return this.drawCodonPosRuler;
	}

	public boolean decCharSize(){

		// stop when everything is in view (or char is 1 for smaller alignments)
		Dimension prefSize = getPreferredSize();	

		// stop when everything is in view
		boolean didDecrease = false;
		if(this.getSize().width > this.getVisibleRect().width || this.getSize().height > this.getVisibleRect().height){

			double preferredWidth = charWidth;
			double preferredHeight = charHeight;

			if(charWidth > 1){
				preferredWidth = charWidth - 1;
				preferredHeight = (int)(preferredWidth*CHAR_HEIGHT_RATIO);// 1.2 * charWidth;
			}
			else{
				preferredWidth = 0.80 * charWidth;
				preferredHeight = preferredWidth;
			}
			if(preferredWidth >= MIN_CHAR_SIZE){
				charWidth = preferredWidth;
				charHeight = preferredHeight;
			}
			//baseFont = new Font(baseFont.getName(), baseFont.getStyle(), (int)charWidth);

			createAdjustedDerivedBaseFont();
			createCharPixelsContainers();
			//	logFontMetrics();
			this.validateSize();	
			didDecrease = true;
		}

		return didDecrease;

	}

	public void incCharSize(){
		if(charWidth >= 1){
			charWidth = charWidth +1; // +1
			charHeight = (int)(charWidth*CHAR_HEIGHT_RATIO);
		}else{
			charWidth = charWidth * 1.25; // +1

			if(charWidth > 1  && charWidth <2){
				charWidth = 1;
			}
			charHeight = charWidth; // +1	
		}
		if(charWidth > MAX_CHAR_SIZE){
			charWidth = MAX_CHAR_SIZE;
			charHeight = (int)(charWidth*CHAR_HEIGHT_RATIO);
		}
		//baseFont = new Font(baseFont.getName(), baseFont.getStyle(), (int)charWidth);

		createAdjustedDerivedBaseFont();
		createCharPixelsContainers();
		//		logFontMetrics();
		this.validateSize();
	}


	private void createCharPixelsContainers(){

		long startTime = System.currentTimeMillis();	

		charPixDefaultNuc = CharPixelsContainer.createDefaultNucleotideImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW, (int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);
		charPixSelectedNuc = CharPixelsContainer.createSelectedNucleotideImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW, (int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);
		charPixConsensusNuc = CharPixelsContainer.createConsensusNucleotideImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW, (int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);

		charPixTranslationDefault = TranslationCharPixelsContainer.createDefaultTranslationPixelsImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
				(int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);

		charPixTranslationSelected = TranslationCharPixelsContainer.createSelectedTranslationPixelsImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
				(int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);

		charPixTranslationLetter = TranslationCharPixelsContainer.createLetterTranslationPixelsImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
				(int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);

		charPixTranslationSelectedLetter = TranslationCharPixelsContainer.createSelectedLetterTranslationPixelsImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
				(int)getCharWidth(), (int)getCharHeight(), colorSchemeNucleotide);


		charPixDefaultAA =  new AACharPixelsContainer();
		if(colorSchemeAminoAcid.getALLCompundColors() != null){
			CompoundCharPixelsContainer compContainer = CompoundCharPixelsContainer.createDefaultCompoundColorImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
					(int)getCharWidth(), (int)getCharHeight(), colorSchemeAminoAcid);
			charPixDefaultAA.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = CharPixelsContainer.createDefaultAAImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW, (int)getCharWidth(), (int)getCharHeight(), colorSchemeAminoAcid);
			charPixDefaultAA.setContainer(container);
		}

		charPixSelectedAA =  new AACharPixelsContainer();
		if(colorSchemeAminoAcid.getALLCompundColors() != null){
			logger.info("baseFont.getSize()" + baseFont.getSize());
			logger.info("baseFont.getSize2D()" + baseFont.getSize2D());

			CompoundCharPixelsContainer compContainer = CompoundCharPixelsContainer.createSelectedCompoundColorImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
					(int)getCharWidth(), (int)getCharHeight(), colorSchemeAminoAcid);
			charPixSelectedAA.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = CharPixelsContainer.createSelectedAAImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW, (int)getCharWidth(), (int)getCharHeight(), colorSchemeAminoAcid);
			charPixSelectedAA.setContainer(container);
		}

		charPixConsensusAA =  new AACharPixelsContainer();
		if(colorSchemeAminoAcid.getALLCompundColors() != null){
			CompoundCharPixelsContainer compContainer = CompoundCharPixelsContainer.createDefaultCompoundColorImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW,
					(int)getCharWidth(), (int)getCharHeight(), colorSchemeAminoAcid);
			charPixConsensusAA.setCompoundContainer(compContainer);

		}else{
			CharPixelsContainer container = CharPixelsContainer.createConsensusAAImpl(getFont(), (int)MAX_CHARSIZE_TO_DRAW, (int)getCharWidth(), (int)getCharHeight(), colorSchemeAminoAcid);
			charPixConsensusAA.setContainer(container);
		}

		endTime = System.currentTimeMillis();
		logger.info("Creating charPixContainers took " + (endTime - startTime) + " milliseconds");

	}

	@Override
	public Font getFont() {
		return baseFont;
	}

	private void createAdjustedDerivedBaseFont() {
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		/*
		if(charWidth == 17){
			attributes.put(TextAttribute.TRACKING, 0.4117647);
		}else if(charWidth == 16){
			attributes.put(TextAttribute.TRACKING, 0.375);
		}else if(charWidth == 15){
			attributes.put(TextAttribute.TRACKING, 0.400);
		}else if(charWidth == 14){
			attributes.put(TextAttribute.TRACKING, 0.4 + 0.0285);
		}else if(charWidth == 13){
			attributes.put(TextAttribute.TRACKING, 0.384 + 0.0007);
		}else if(charWidth == 12){
			//attributes.put(TextAttribute.TRACKING, 0.400001 + 0.2);// + 0.008);
			attributes.put(TextAttribute.TRACKING, 0.416 + 0.0008);// + 0.008);
		}else if(charWidth == 11){
			attributes.put(TextAttribute.TRACKING, 0.363);
		}else if(charWidth == 10){
			attributes.put(TextAttribute.TRACKING, 0.400);
		}else if(charWidth == 9){
			attributes.put(TextAttribute.TRACKING, 0.445);
		}else if(charWidth == 8){
			attributes.put(TextAttribute.TRACKING, 0.375);
		}else if(charWidth == 7){
			attributes.put(TextAttribute.TRACKING, 0.4285);
		}else if(charWidth == 6){
			attributes.put(TextAttribute.TRACKING, 0.4);
		}
		 */
		//; // 11
		//attributes.put(TextAttribute.TRACKING, 0.443); // 10
		//attributes.put(TextAttribute.TRACKING, 0.375); // 9
		//attributes.put(TextAttribute.TRACKING, 0.4278); // 8

		// create a font without Tracking to see the diff in font actual size and specified font size
		attributes.put(TextAttribute.TRACKING, 0);
		attributes.put(TextAttribute.SIZE, (int)charWidth);
		Font calcFont = baseFont.deriveFont(attributes);
		FontMetrics metrics = getFontMetrics(calcFont);
		int fontActualWidth = metrics.stringWidth("X");

		double sizeDiff = charWidth - fontActualWidth;
		// Calculate tracking for font size
		double tracking = (double)sizeDiff/charWidth;
		logger.info("tracking" + tracking);

		// Create a font with correct tracking so characters are exactly spaced as pixels on pane
		attributes.put(TextAttribute.TRACKING, tracking); // 8
		attributes.put(TextAttribute.SIZE, (int)charWidth);
		Font spacedFont = baseFont.deriveFont(attributes);

		baseFont = spacedFont;

	}

	private void logFontMetrics(){
		FontMetrics metrics = this.getGraphics().getFontMetrics(baseFont);

		logger.info("baseFont.getSize()" + baseFont.getSize());
		logger.info("baseFont.getSize2D()" + baseFont.getSize2D());

		// get the height of a line of text in this
		// font and render context	logger.info("baseFont.getSize()" + baseFont.getSize());
		logger.info("baseFont.getSize2D()" + baseFont.getSize2D());

		int hgt = metrics.getHeight();
		logger.info("metrics.getHeight()" + metrics.getHeight());
		logger.info("metrics.getMaxAdvance()" + metrics.getMaxAdvance());	// get the advance of my text in this font
		logger.info("metrics.getLeading()" + metrics.getLeading());
		//	logger.info("metrics.getLeading()" + metrics.getWidths()


		//logger.info("metrics.getMaxAdvance()" + metrics.get
		// and render context
		int adv = metrics.stringWidth("A");


		logger.info("metrics.stringWidth(\"A\")" + metrics.stringWidth("AAAAAAAAAA"));
		logger.info("metrics.stringWidth(\"T\")" + metrics.stringWidth("T"));
		logger.info("metrics.stringWidth(\"c\")" + metrics.stringWidth("c"));
		logger.info("baseFont.getAttributes().get(WIDTH_REGULAR)" + baseFont.getAttributes().get(TextAttribute.WIDTH_REGULAR));




		//	logger.info("metrics.stringWidth(\"c\")" + metrics.get

		// calculate the size of a box to hold the
		// text with some padding.

	}

	// should throw no valid base error
	public Point getBasePosition(Base base){
		if(base == null){
			return null;
		}
		int x = (int) (base.getPosition() * charWidth);
		int y = (int) (alignment.getSequencePosition(base.getSequence()) * charHeight);

		Point pos = new Point(x,y);

		return pos;
	}

	public Base selectBaseAt(Point pos) throws InvalidAlignmentPositionException{

		Base base = null;

		base = getBaseAt(pos);
		if(base != null){
			alignment.setSelectionAt(base.getPosition(), alignment.getSequencePosition(base.getSequence()),true);
		}

		return base;
	}

	public int getUngapedPositionInSequenceAt(Point pos) throws InvalidAlignmentPositionException{
		int ungapedPos = 0;

		Base base = getBaseAt(pos);
		if(base != null){
			ungapedPos = base.getUngapedPosition();
		}
		return ungapedPos;
	}

	public int getPositionInSequenceAt(Point pos) throws InvalidAlignmentPositionException{

		int xPos = 0;
		Base base = getBaseAt(pos);
		if(base != null){
			xPos = base.getPosition();
		}

		return xPos;
	}



	public void selectColumnAt(Point pos) {
		int columnIndex = getColumnAt(pos);
		getAlignment().setColumnSelection(columnIndex, true);
	}


	public Base getBaseAt(Point pos) throws InvalidAlignmentPositionException{

		Point matrixPoint = paneCoordToMatrixCoord(pos);

		Base base = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			Sequence seq = (Sequence) alignment.getSequences().get(matrixPoint.y);
			base = new Base(seq, matrixPoint.x);
		}
		else{
			base = null;
		}
		return base;
	}




	public Base getClosestBaseAt(Point pos){

		Point matrixPoint = paneCoordToMatrixCoord(pos);

		Base base = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			Sequence seq = (Sequence) alignment.getSequences().get(matrixPoint.y);
			base = new Base(seq, matrixPoint.x);
		}
		else{
			// get last sequence
			Sequence seq = (Sequence) alignment.getSequences().get(alignment.getSequences().getSize()-1);
			base = new Base(seq, matrixPoint.x);
		}

		return base;
	}


	public int getColumnAt(Point pos){

		Point matrixPoint = paneCoordToMatrixCoord(pos);

		return matrixPoint.x;

	}


	public void setAlignment(Alignment alignment){
		this.alignment = alignment;
		//		this.infoLabel.setAlignment(alignment);
		this.validateSize();
	}






	public void repaintForceRuler(){
		rulerIsDirty = true;
		repaint();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		paintAlignment(g);
	}


	/*
	 *  Mainly for performance test-developing
	 */
	private long endTime;
	private int drawCounter = 0;
	private int DRAWCOUNT_LOF_INTERVAL = 1;
	public void paintAlignment(Graphics g){
		drawCounter ++;
		long startTime = System.currentTimeMillis();	
		if(drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			logger.info("Time from last endTim " + (startTime - endTime) + " milliseconds");
		}

		//	logger.info("paintClipBounds" + g.getClipBounds());



		Graphics2D g2d = (Graphics2D) g;

		// This need to be off because I use exact font width in createAdjustedDerivedBaseFont
		//		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
		//				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		//		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		//				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		//		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
		//				RenderingHints.VALUE_RENDER_SPEED);
		//		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
		//						RenderingHints.VALUE_RENDER_QUALITY);
		//	g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
		//			RenderingHints.VALUE_DITHER_DISABLE);		
		//		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		//				RenderingHints.VALUE_COLOR_RENDER_SPEED);	
		//				g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		//						RenderingHints.VALUE_COLOR_RENDER_QUALITY);



		// Font
		//		g2d.setFont(baseFont);

		// What part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle clip = g2d.getClipBounds();

		if(forceRepaintAll){
			Rectangle allVisible = this.getVisibleRect();
			clip = allVisible;
			setForceRepaintAll(false);
		}

		Rectangle matrixClip = paneCoordToMatrixCoord(clip);

		int xMin = matrixClip.x - 1;
		int yMin = matrixClip.y - 1;
		int xMax = (int) matrixClip.getMaxX() + 1;
		int yMax = (int) matrixClip.getMaxY() + 1;

		// add one extra position when drawing translated
		// otherwise there could be some white borders when scrolling
		if(showTranslation || showTranslationOnePos){
			xMin --;
			xMax ++;
		}

		// adjust for part of matrix that exists
		xMin = Math.max(0, xMin);
		yMin = Math.max(0, yMin);
		xMax = Math.min(alignment.getMaxX(), xMax);
		yMax = Math.min(alignment.getMaxY(), yMax);



		// Extra because pixelCopyDraw
		int height = (yMax - yMin) * (int)charHeight;
		int width = (xMax - xMin) * (int)charWidth;

		// Small chars
		if(charWidth < 1){
			height = clip.height;
			width = clip.width;
		}

		logger.info("yMax" + yMax);
		logger.info("yMin" + yMin);
		logger.info("width" + width);
		logger.info("clipHeight" + clip.height);
		logger.info("height" + height);

		int[] pixArray = new int[width * height];
		RGBArray clipRGB = new RGBArray(pixArray, width, height);


		// If it is to be translated
		AATranslator aaTransSeq = null;
		if(showTranslation || showTranslationOnePos){
			aaTransSeq = new AATranslator(alignment.getAlignentMeta().getCodonPositions(), alignment.getGeneticCode());
		}

		// small chars have their own loop here
		if(charWidth < 1){

			if(showTranslationOnePos || showTranslation){
				int clipY = 0;
				for(int y = clip.y; y < clip.y + clip.height; y ++){		
					int ySeq =(int)((double)y * (1/(double)charWidth));

					if(ySeq < alignment.getMaxY()){	

						aaTransSeq.setSequence(alignment.getSequences().get(ySeq));

						if(showTranslationOnePos){
							double maxX = clip.getMaxX();
							if(maxX > alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()){
								maxX = alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos();
							}						

							// X Loop Start
							int clipX = 0;						
							for(int x = clip.x; x < maxX ; x++){
								int xPos =(int)((double)x * (1/(double)charWidth));

								if(xPos < aaTransSeq.getMaximumTranslationLength()){

									AminoAcid acid;
									if(ignoreGapInTranslation){
										acid = aaTransSeq.getAAinNoGapTranslatedPos(xPos);
									}else{
										acid = aaTransSeq.getAAinTranslatedPos(xPos);
									}
									copyAminoAcidPixels(clipRGB,(byte)acid.getCodeCharVal(),xPos,ySeq,clipX, clipY);

								}
								clipX ++;
							}
							// standard translation 3-pos
						}else{
							// X Loop Start
							int clipX = 0;
							for(int x = clip.x; x < clip.getMaxX() ; x++){
								int xPos =(int)((double)x * (1/(double)charWidth));
								if(alignment.isPositionValid(xPos, ySeq)){

									byte residue = alignment.getBaseAt(xPos,ySeq);

									if(ignoreGapInTranslation){
										copyTranslatedNucleotidesPixelsSkipGap(clipRGB,residue,xPos,ySeq,clipX, clipY, aaTransSeq);
									}
									else{
										copyTranslatedNucleotidesPixels(clipRGB,residue,xPos,ySeq,clipX, clipY, aaTransSeq);
									}
								}
								clipX ++;
							}
						}
					}
					clipY ++;
				}

			}else{

				// No longer: Always start at closest even 10
				//			double startY = clip.y;
				//			startY = Math.floor(startY/100) * 100;
				int clipY = 0;
				for(int y = clip.y; y < clip.y + clip.height; y ++){

					int ySeq = (int)((double)(y) * (1/(double)charWidth));

					if(ySeq < alignment.getMaxY()){	

						// X Loop Start
						int clipX = 0;
						for(int x = clip.x; x < clip.getMaxX(); x++){

							int xPos =(int)((double)x * (1/(double)charWidth));

							if(alignment.isPositionValid(xPos, ySeq)){
								byte residue = alignment.getBaseAt(xPos,ySeq);

								// Draw as Nucleotides
								if(alignment.isNucleotideAlignment()){
									//drawNucleotides(g2d,base,xPos,ySeq,x,y,1,1,0, 0);
									copyNucleotidePixels(clipRGB,residue,xPos,ySeq,clipX,clipY);			
								}
								// Draw as AA
								else{
									copyAminoAcidPixels(clipRGB,residue,xPos,ySeq,clipX,clipY);
								}

							}
							clipX ++;
						}		
						clipY ++;
					}
				}
			}

			// Now draw the pixels
			Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));

			//						logger.info(clipRGB.getBackend().length);
			//						logger.info("img.getWidth" + img.getWidth(null));
			//						logger.info("img.getHeight" + img.getHeight(null));
			//
			//						
			//						logger.info("xMin" + xMin);
			//						logger.info("clip.x" + clip.x);
			//						logger.info("clip.y" + clip.y);
			//						logger.info("clip.width" + clip.width);
			//						logger.info("clip.height" + clip.height);


			if (img != null){
				//							logger.info(img.getHeight(null));
				//							logger.info("clip.y=" + clip.y);
				//							logger.info("x=" + (int)(xMin * charWidth));
				//							logger.info("y=" + (int)(yMin * charHeight));
				g.drawImage(img, clip.x, clip.y, null);
			}

			// Draw excludes - only if not 
			if(!showTranslationOnePos){
				// calculaate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
				int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);
				//for(int x = xMin; x < xMax ; x++){
				for(int x = clip.x; x < clip.getMaxX() ; x++){
					int xPos =(int)((double)x * (1/(double)charWidth));
					if(alignment.isExcluded(xPos) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect(x, this.getVisibleRect().y, 1, drawExcludesHeight);
						//				logger.info("drawExclude");
					}
				}	
			}
		}	


		/////////////////////////
		//
		// Normal char width
		//
		/////////////////////////
		else{
			if(showTranslationOnePos){			

				int maxX = xMax;
				if(maxX > alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()){
					maxX = alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos();
				}

				int clipY = 0;
				for(int y = yMin; y < yMax; y = y + 1){

					aaTransSeq.setSequence(alignment.getSequences().get(y));

					// X Loop Start
					int clipX = 0;
					for(int x = xMin; x < maxX ; x++){

						AminoAcid acid;
						if(ignoreGapInTranslation){
							acid = aaTransSeq.getAAinNoGapTranslatedPos(x);
						}else{
							acid = aaTransSeq.getAAinTranslatedPos(x);
						}
						copyAminoAcidPixels(clipRGB,(byte)acid.getCodeCharVal(),x,y,(int)(clipX*charWidth), (int)(clipY*charHeight));

						clipX ++;
					}		
					clipY ++;
				}// y loop end		
			}else{
				// Most normal one
				int clipY = 0;			
				for(int y = yMin; y < yMax; y = y + 1){

					if(showTranslation){
						aaTransSeq.setSequence(alignment.getSequences().get(y));
					}

					// X Loop Start
					int clipX = 0;
					for(int x = xMin; x < xMax ; x++){

						byte residue = alignment.getBaseAt(x,y);
						// Draw as nucleotides
						if(alignment.isNucleotideAlignment()){
							// Draw as translated
							if(showTranslation){
								if(ignoreGapInTranslation){
									copyTranslatedNucleotidesPixelsSkipGap(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight), aaTransSeq);
								}
								else{
									copyTranslatedNucleotidesPixels(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight), aaTransSeq);
								}
							}else{
								copyNucleotidePixels(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight));					
							}
						}
						// Draw as AminoAcids
						else{
							copyAminoAcidPixels(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight));	
						}
						clipX ++;
					}		
					clipY ++;
				}// x loop end
			}// y loop end


			//			logger.info(clip);
			//						
			//						for(int n = 0; n <clipRGB.getBackend().length; n++){
			//							clipRGB.getBackend()[n] = ColorUtils.addTranspGrey(clipRGB.getBackend()[n], 0.45);
			//							clipRGB.getBackend()[n] = clipRGB.getBackend()[n] | 0x333333;
			//							getGolorVal(getR)
			//						}


			//						// Draw excludes (by manipulating color val)
			//						if(! showTranslationOnePos){
			//							// calculaate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
			//							int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);
			//							for(int x = xMin; x < xMax ; x++){
			//								if(alignment.isExcluded(x) == true){
			//									g2d.setColor(ColorScheme.GREY_TRANSPARENT);
			//									g2d.fillRect((int)(x * charWidth), this.getVisibleRect().y, (int)charWidth, drawExcludesHeight);				
			//								}
			//							}
			//						}

			// Now draw the pixels
			Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));

			//			logger.info(clipRGB.getBackend().length);
			//			logger.info("img.getWidth" + img.getWidth(null));
			//			logger.info("img.getHeight" + img.getHeight(null));
			//
			//
			//			logger.info("xMin" + xMin);
			//			logger.info("clip.x" + clip.x);
			//			logger.info("clip.y" + clip.y);
			//			logger.info("clip.width" + clip.width);
			//			logger.info("clip.height" + clip.height);






			if (img != null){
				//				logger.info(img.getHeight(null));
				//				logger.info("x=" + (int)(xMin * charWidth));
				//				logger.info("y=" + (int)(yMin * charHeight));
				g.drawImage(img, (int)(xMin * charWidth), (int)(yMin * charHeight), null);
			}


			// Draw excludes
			if(! showTranslationOnePos){
				// calculaate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
				int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);
				for(int x = xMin; x < xMax ; x++){
					if(alignment.isExcluded(x) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect((int)(x * charWidth), this.getVisibleRect().y, (int)charWidth, drawExcludesHeight);				
					}
				}
			}


		}

		if(drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			endTime = System.currentTimeMillis();
			logger.info("Alignment pane PaintComponent took " + (endTime - startTime) + " milliseconds");
		}

		// repaint ruler also if needed
		if(clip.x != lastClip.x || clip.width != lastClip.width || rulerIsDirty){
			alignmentRuler.repaint();
			rulerIsDirty = false;
		}
		lastClip = clip;

	}


	public void setForceRepaintAll(boolean b) {
		forceRepaintAll = b;

	}

	private void copyTranslatedNucleotidesPixelsSkipGap(RGBArray clipArray, byte residue, int x, int y, int clipX, int clipY, AATranslator aaTransSeq){

		// Ett litet hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		AminoAcid acid =  aaTransSeq.getNoGapAminoAcidAtNucleotidePos(x);
		int acidStartPos = aaTransSeq.getCachedClosestStartPos();

		TranslationCharPixelsContainer pixContainerToUse = charPixTranslationDefault;
		TranslationCharPixelsContainer pixLetterContainerToUse = charPixTranslationLetter;

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			pixContainerToUse = charPixTranslationSelected;
			pixLetterContainerToUse = charPixTranslationSelectedLetter;
		}

		RGBArray newPiece;

		if(! drawAminoAcidCode){	
			newPiece = pixContainerToUse.getRGBArray(acid, residue);
		}else{
			if(x == acidStartPos + 1){ // this line is changed
				newPiece = pixLetterContainerToUse.getRGBArray(acid, residue);
			}else{
				residue = ' ';
				newPiece = pixContainerToUse.getRGBArray(acid, residue);
			}
		}

		try {
			ImageUtils.insertRGBArrayAt(clipX, clipY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("x" + x);
			logger.info("y" + y);
			logger.info("clipX" + clipX);
			logger.info("clipY" + clipY);
		}
	}


	private void copyTranslatedNucleotidesPixels(RGBArray clipArray, byte residue, int x, int y, int clipX, int clipY, AATranslator aaTransSeq){

		// Ett litet hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		AminoAcid acid =  aaTransSeq.getAminoAcidAtNucleotidePos(x);
		TranslationCharPixelsContainer pixContainerToUse = charPixTranslationDefault;
		TranslationCharPixelsContainer pixLetterContainerToUse = charPixTranslationLetter;

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			pixContainerToUse = charPixTranslationSelected;
			pixLetterContainerToUse = charPixTranslationSelectedLetter;
		}

		RGBArray newPiece;

		if(! drawAminoAcidCode){	
			newPiece = pixContainerToUse.getRGBArray(acid, residue);
		}else{
			if(aaTransSeq.isCodonSecondPos(x)){
				newPiece = pixLetterContainerToUse.getRGBArray(acid, residue);
			}else{
				residue = ' ';
				newPiece = pixContainerToUse.getRGBArray(acid, residue);
			}
		}

		try {
			ImageUtils.insertRGBArrayAt(clipX, clipY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("clipX" + clipX);
			logger.info("clipY" + clipY);
		}
	}



	private void copyAminoAcidPixels(RGBArray clipArray, byte residue, int x, int y, int clipX, int clipY){

		// Ett litet hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		AACharPixelsContainer pixContainerToUse = charPixDefaultAA;
		byteToDraw = residue;
		AminoAcid acid = AminoAcid.getAminoAcidFromByte(residue);

		// adjustment if only diff to be shown
		if(highlightDiffTrace){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(y != differenceTraceSequencePosition && acid == AminoAcid.getAminoAcidFromByte(alignment.getBaseAt(x,differenceTraceSequencePosition))){
				byteToDraw = '.';
				pixContainerToUse = charPixDefaultAA;
			}
		}

		// adjustment if non-cons to be highlighted
		if(highlightNonCons){
			if(acid == AminoAcid.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(alignment.getHistogram().isMajorityRuleConsensus(x,acid.intVal)){
				pixContainerToUse = charPixConsensusAA;
			}
		}
		if(highlightCons){
			if(acid == AminoAcid.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! alignment.getHistogram().isMajorityRuleConsensus(x,acid.intVal)){
				pixContainerToUse = charPixConsensusAA;
			}
		}

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			pixContainerToUse = charPixSelectedAA;
		}

		RGBArray newPiece = pixContainerToUse.getRGBArray(byteToDraw, x, alignment);

		try {
			ImageUtils.insertRGBArrayAt(clipX, clipY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("clipX" + clipX);
			logger.info("clipY" + clipY);
		}
	}




	private void copyNucleotidePixels(RGBArray clipArray, byte residue, int x, int y, int clipX, int clipY){

		// Ett litet hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		CharPixelsContainer pixContainerToUse = charPixDefaultNuc;
		byteToDraw = residue;
		int baseVal = NucleotideUtilities.baseValFromBase(residue);


		// adjustment if only diff to be shown
		if(highlightDiffTrace){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(y != differenceTraceSequencePosition){
				if(baseVal == NucleotideUtilities.baseValFromBase(alignment.getBaseAt(x,differenceTraceSequencePosition))){
					byteToDraw = '.';
					pixContainerToUse = charPixDefaultNuc;
				}
			}
		}

		// adjustment if non-cons to be highlighted
		if(highlightNonCons){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(nucHistogram.isMajorityRuleConsensus(x,baseVal)){
				pixContainerToUse = charPixConsensusNuc;
			}
		}
		if(highlightCons){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! nucHistogram.isMajorityRuleConsensus(x,baseVal)){
				pixContainerToUse = charPixConsensusNuc;
			}
		}

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			pixContainerToUse = charPixSelectedNuc;
		}

		RGBArray newPiece = pixContainerToUse.getRGBArray(byteToDraw);

		try {
			ImageUtils.insertRGBArrayAt(clipX, clipY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("x" + x);
			logger.info("y" + y);
			logger.info("clipX" + clipX);
			logger.info("clipY" + clipY);
			//break;
		}

	}


	public Alignment getAlignment() {
		return alignment;
	}

	public void validateSequenceOrder(){

		// verify that tracing sequence not is out of index
		if(differenceTraceSequencePosition >= alignment.getSize()){
			differenceTraceSequencePosition = 0;
		}
	}

	public int selectWithin(Rectangle rect){
		// First clear
		int selectionSize = addSelectionWithin(rect);
		return selectionSize;
	}

	public int selectColumnsWithin(Rectangle rect) {
		// First clear
		int selectionSize = addColumnSelectionWithin(rect);
		return selectionSize;
	}


	public int addColumnSelectionWithin(Rectangle rect){
		int nSelection = 0;

		// grow so all sequences are included
		Rectangle columns = new Rectangle(rect.x, 0, rect.width, this.getHeight());
		return addSelectionWithin(columns);
	}

	public int addSelectionWithin(Rectangle rect){
		int nSelection = 0;
		// calculate what part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle bounds = paneCoordToMatrixCoord(rect);

		alignment.setSelectionWithin(bounds,true);

		return nSelection;
	}

	public void setTempSelection(Rectangle selectRect) {
		// change rect to matrixCoordSys
		this.tempSelectionRect = paneCoordToMatrixCoord(selectRect);
	}

	public void clearTempSelection() {
		this.tempSelectionRect = null;
	}

	public Rectangle paneCoordToMatrixCoord(Rectangle rect){

		// TODO maybe problem when calculating a 0-width rect - then it will give eg. xmin=34 xmax=35

		//		logger.info("rect.getMinX()" + rect.getMinX());
		//		logger.info("rect.getMaxX()" + rect.getMaxX());
		//		logger.info("rect.getMinX()/charWidth" + rect.getMinX()/charWidth);
		//		
		int matrixMinX = (int) Math.floor(rect.getMinX()/charWidth); // always round down
		int matrixMaxX = (int) Math.floor(rect.getMaxX()/charWidth); // always round up
		int matrixMinY = (int) Math.floor(rect.getMinY()/charHeight); // always round down
		int matrixMaxY = (int) Math.floor(rect.getMaxY()/charHeight); // always round down
		//		logger.info("matrixMinX" + matrixMinX);
		//		logger.info("matrixMaxX" + matrixMaxX);
		////	logger.info(getMatrixTopOffset());
		//		

		Rectangle converted = new Rectangle(matrixMinX, matrixMinY, matrixMaxX - matrixMinX, matrixMaxY - matrixMinY); 
		return converted;
	}

	// todo this should be listening to changes in alignmnet instead
	public void updateStatisticsLabel(){
		//		logger.info("unimplemented should be done by changelistener");
	}




	public void validateSize() {
		// Set component preferred size
		Dimension prefSize = getCalculatedPreferredSize();
		this.setPreferredSize(prefSize);
		this.updateStatisticsLabel();
		this.rulerIsDirty = true;
		this.revalidate();
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
	}

	@Override
	public Dimension getPreferredSize() {
		return getCalculatedPreferredSize();
	}

	private Dimension getCalculatedPreferredSize(){
		Dimension newDim;
		if(showTranslationOnePos){
			newDim = new Dimension((int) (charWidth * alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()), (int)(charHeight * alignment.getSize()));
		}else{
			newDim = new Dimension((int) (charWidth * alignment.getMaximumSequenceLength()), (int)(charHeight * alignment.getSize()));
		}

		//		logger.info("newDim" + newDim);
		return newDim;

	}

	public Point paneCoordToMatrixCoord(Point pos){

		int matrixX = (int) Math.floor(pos.getX() / charWidth);
		int matrixY = (int) Math.floor(pos.getY() /  charHeight);
		Point converted = new Point(matrixX, matrixY);	
		return converted;
	}

	public Point matrixCoordToPaneCoord(Point pos){
		int paneX = (int) (pos.getX() * charWidth);
		int paneY = (int) (pos.getY() * charHeight);
		Point converted = new Point(paneX, paneY);
		return converted;
	}


	public Rectangle matrixCoordToPaneCoord(Rectangle rect){
		Point min = new Point((int)rect.getMinX(), (int)rect.getMinY());
		//		logger.info("min" + min);
		Point max = new Point((int)rect.getMaxX(), (int)rect.getMaxY());
		//		logger.info("max" + max);
		Rectangle converted = new Rectangle(matrixCoordToPaneCoord(min));
		converted.add(matrixCoordToPaneCoord(max));
		//		logger.info("converted" + converted);
		return converted;
	}

	public boolean isPointWithinMatrix(Point pos) {
		Point matrixPoint = paneCoordToMatrixCoord(pos);
		return alignment.isPositionValid(matrixPoint.x, matrixPoint.y);
	}

	public void setInteractionMode(int interactionMode) {
		this.interactionMode = interactionMode;
	}

	public int getInteractionMode() {
		return this.interactionMode;
	}

	public double getCharHeight() {
		return this.charHeight;
	}

	public double getCharWidth() {
		return this.charWidth;
	}


	public void setDifferenceTraceSequence(Point pos) throws InvalidAlignmentPositionException {
		Point matrixPoint = paneCoordToMatrixCoord(pos);
		Sequence seq = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			// todo this should be changed because problem when removed or moved
			this.differenceTraceSequencePosition = matrixPoint.y;
		}
		else{
			throw new InvalidAlignmentPositionException("Position is out of range" + pos);
		}
	}

	public void setDifferenceTraceSequence(int nIndex){
		this.differenceTraceSequencePosition = nIndex;
	}

	public Sequence getSequenceAt(Point pos) throws InvalidAlignmentPositionException {
		Point matrixPoint = paneCoordToMatrixCoord(pos);
		Sequence seq = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			seq = (Sequence) alignment.getSequences().get(matrixPoint.y);
		}
		else{
			throw new InvalidAlignmentPositionException("Position is out of range" + pos);
		}
		return seq;
	}

	public boolean isWithinExistingSelection(Point point) {
		boolean isSelected = false;

		try {
			Base base = getBaseAt(point);
			if(base != null){
				isSelected = base.isSelected();
			}
		} catch (InvalidAlignmentPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isSelected;


	}

	public void setShowTranslation(boolean showTranslation){
		this.showTranslation = showTranslation;
	}

	public boolean isShowTranslation() {
		return showTranslation;
	}

	public void toggleTranslationOnePos() {
		this.showTranslationOnePos = ! this.showTranslationOnePos;
		this.validateSize();
	}

	public boolean isShowTranslationOnePos() {
		return showTranslationOnePos;
	}


	public JComponent getRulerComponent(){
		return this.alignmentRuler;
	}

	private class AlignmentRuler extends JPanel{

		private AlignmentPane alignmentPane;

		public AlignmentRuler(AlignmentPane alignmentPane) {
			this.alignmentPane = alignmentPane;
		}


		public void paintComponent(Graphics g){
			super.paintComponent(g);
			paintRuler(g);
		}

		public void paintRuler(Graphics g){

			long startTime = System.currentTimeMillis();

			//super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
					RenderingHints.VALUE_ANTIALIAS_OFF); 
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);
			//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			//					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
					RenderingHints.VALUE_DITHER_DISABLE);		

			//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			//					RenderingHints.VALUE_RENDER_QUALITY);
			//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			//					RenderingHints.VALUE_ANTIALIAS_ON);
			//			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			//								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			//			//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			//					RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			//g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
			//					RenderingHints.VALUE_COLOR_RENDER_SPEED);
			//g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
			//					RenderingHints.VALUE_DITHER_DISABLE);


			g2d.setFont(baseFont);

			// What part of alignment matrix is in view (what part of matrix is in graphical view)
			Rectangle alignmentPaneClip = alignmentPane.getVisibleRect();
			Rectangle matrixClip = paneCoordToMatrixCoord(alignmentPaneClip);

			// todo calculate from font metrics
			double charCenterXOffset = 0.9997;

			//
			// Draw ruler background
			//
			Rectangle rulerRect = new Rectangle(this.getVisibleRect());
			g2d.setColor(colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP));
			g2d.fill(rulerRect);

			int offsetDueToScrollPanePosition = 0;
			if(charWidth >=1){
				offsetDueToScrollPanePosition = alignmentPaneClip.x % (int)charWidth;
				offsetDueToScrollPanePosition = offsetDueToScrollPanePosition -1;
			}

			// Tickmarks
			int posTick = 0;
			int count = 0;
			int maxX = alignment.getMaxX(); // alignment maxX is 
			int maxY = alignment.getMaxY();

			int step = 1;
			int matrixClipSize = (int) (matrixClip.getMaxX() - matrixClip.x);

			if(matrixClipSize < 10000000){

				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){


					// TODO Ruler tickmark and numbers is off....

					// draw background depending on codonpos

					// Only draw part of matrix that exists 
					if(maxY > 0 && x >= 0 && x < maxX){

						if(drawCodonPosRuler && ! showTranslationOnePos){
							int codonPos = alignment.getCodonPosAt(x);
							//logger.info(codonPos);
							Color codonPosColor = Color.GREEN;
							if(codonPos == 0){
								codonPosColor = Color.LIGHT_GRAY;
							}else if(codonPos == 1){
								codonPosColor = Color.GREEN;
							}else if(codonPos == 2){
								codonPosColor = Color.orange;
							}else if(codonPos == 3){
								codonPosColor = Color.red;
							}

							g2d.setColor(codonPosColor);

							int boxHeight = 5;
							g2d.fillRect((int)(posTick * charCenterXOffset * charWidth - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - boxHeight), (int)charWidth, boxHeight);
							//g2d.fillRect((int)(x * charWidth), (int) (rulerRect.getMaxY() - 4), (int)charWidth, boxHeight);
						}

						g2d.setColor(Color.DARK_GRAY);
						// make every 5 a bit bigger
						if(x % 5 == 4 && charHeight > 0.6){ // it has to be 4 and not 0 due to the fact that 1:st base har position 0 in matrix
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 5);
						}
						// dont draw smallest tick if to small
						else if(charHeight > 4){
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 3);
						}
						posTick ++;
					}
					count ++;

				}

				// NUMBERS
				int rulerCharWidth = 11;
				int rulerCharHeight = 11;
				Font rulerFont = new Font(baseFont.getName(), baseFont.getStyle(), (int)rulerCharWidth);
				g2d.setFont(rulerFont);

				// Only draw every 20-10000 pos
				int drawEveryNpos = 10;

				if(charHeight < 0.001){
					drawEveryNpos = 1000000;
				}else if(charHeight < 0.02){
					drawEveryNpos = 10000;
				}else if(charHeight < 0.04){
					drawEveryNpos = 5000;
				}else if(charHeight < 0.1){
					drawEveryNpos = 1000;
				}else if(charHeight < 0.4){
					drawEveryNpos = 500;
				}else if(charHeight < 1){
					drawEveryNpos = 100;
				}else if(charHeight < 4){
					drawEveryNpos = 50;
				}else if(charHeight < 5){
					drawEveryNpos = 20;
				}

				// position numbers
				int pos = 0;
				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					if(x % drawEveryNpos == 0){
						String number = Integer.toString(x);
						int stringSizeOffset = (int)((number.length() * rulerCharWidth) / 2);
						int xPos = (int)(pos * charWidth - stringSizeOffset - offsetDueToScrollPanePosition);
						g2d.drawString(number, xPos, 10);
						//	g2d.drawBytes(number.getBytes(), 0,number.getBytes().length,xPos, 10);
					}
					pos ++;
				}	
			}


			long endTime = System.currentTimeMillis();
			logger.info("Ruler PaintComponent took " + (endTime - startTime) + " milliseconds");
		}
	}



	public void setDrawAminoAcidCode(boolean drawCode){
		this.drawAminoAcidCode = drawCode;
	}

	public boolean getDrawAminoAcidCode(){
		return this.drawAminoAcidCode;
	}

	public void setColorSchemeAminoAcid(ColorScheme aScheme){
		this.colorSchemeAminoAcid = aScheme;
		createCharPixelsContainers();

	}

	public void setColorSchemeNucleotide(ColorScheme aScheme) {
		this.colorSchemeNucleotide = aScheme;
		createCharPixelsContainers();
	}

	public Point getVisibleUpperLeftMatrixPos() {
		Rectangle rect = this.getVisibleRect();
		Point ulPanePos = rect.getLocation();
		Point ulMatrixPos = paneCoordToMatrixCoord(ulPanePos);
		return ulMatrixPos;
	}

	public void scrollToVisibleUpperLeftMatrixPos(Point ulPos) {
		Point ulPanePos = matrixCoordToPaneCoord(ulPos);
		this.setLocation(ulPanePos);
	}

	public void scrollMatrixX(int offset) {
		int offsetPane = (int)(offset * charWidth);
		this.setLocation( getLocation().x + offsetPane, getLocation().y );
	}

	public boolean getIgnoreGapInTranslation(){	
		return ignoreGapInTranslation;
	}

	public void setIgnoreGapInTranslation(boolean ignoreGapInTranslation) {
		this.ignoreGapInTranslation = ignoreGapInTranslation;
	}



	//	public void selectSequences(ArrayList<Sequence> selectedSequences) {
	//		preserveBaseSelection = false;
	//
	//	}

}

