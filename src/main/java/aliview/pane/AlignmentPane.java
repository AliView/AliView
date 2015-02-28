package aliview.pane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import utils.OSNativeUtils;
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
import aliview.messenges.Messenger;
import aliview.sequences.AminoAcidAndPosition;
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
	//private static final Color ALPHACOLOR = new Color(255, 255,255, 128 );
	double charWidth = 10;
	double charHeight = 12;
	private Font baseFont = new Font(Font.MONOSPACED, Font.PLAIN, (int)charWidth);
	private Font highDPIFont = new Font(Font.MONOSPACED, Font.PLAIN, (int)charWidth);
	private int highDPIScaleFactor = 1;

	private Alignment alignment;

	private ColorScheme colorSchemeAminoAcid = Settings.getColorSchemeAminoAcid();
	private ColorScheme colorSchemeNucleotide = Settings.getColorSchemeNucleotide();
	//	private Rectangle tempSelectionRect;

	// TODO This should instead be tracing a sequence instead of a position?
	int differenceTraceSequencePosition = 0;
	private boolean showTranslation = false;
	private boolean showTranslationAndNuc = false;
	//	private boolean showTranslationOnePos = false;
	private AlignmentRuler alignmentRuler;
	private boolean drawAminoAcidCode; 
	private boolean drawCodonPosRuler;
	private Rectangle lastClip = new Rectangle();
	private boolean rulerIsDirty;
	boolean highlightDiffTrace = false;
	boolean highlightNonCons;
	boolean highlightCons;
	private boolean ignoreGapInTranslation;
	private byte byteToDraw;
	private long endTime; // performance measure
	private int drawCounter = 0; // performance measure
	private int DRAWCOUNT_LOF_INTERVAL = 1; // performance measure
	private int fontCase = Settings.getFontCase().getIntValue();

	CharPixelsContainer charPixDefaultNuc;
	CharPixelsContainer charPixSelectedNuc;
	CharPixelsContainer charPixConsensusNuc;
	AACharPixelsContainer charPixDefaultAA;
	AACharPixelsContainer charPixSelectedAA;
	AACharPixelsContainer charPixConsensusAA;
	TranslationCharPixelsContainer charPixTranslationDefault;
	TranslationCharPixelsContainer charPixTranslationSelected;
	TranslationCharPixelsContainer charPixTranslationLetter;
	TranslationCharPixelsContainer charPixTranslationSelectedLetter;
	TranslationCharPixelsContainer charPixTranslationAndNucDefault;
	TranslationCharPixelsContainer charPixTranslationAndNucSelected;
	TranslationCharPixelsContainer charPixTranslationAndNucDefaultNoAALetter;
	TranslationCharPixelsContainer charPixTranslationAndNucSelectedNoAALetter;
	TranslationCharPixelsContainer charPixTranslationAndNucDominantNuc;
	TranslationCharPixelsContainer charPixTranslationAndNucDominantNucNoAALetter;
	TranslationCharPixelsContainer charPixTranslationAndNucDominantNucSelected;
	TranslationCharPixelsContainer charPixTranslationAndNucDominantNucNoAALetterSelected;



	public AlignmentPane() {
		highDPIScaleFactor = (int)OSNativeUtils.getHighDPIScaleFactor();
		createAdjustedDerivedBaseFont();
		createAdjustedDerivedHighDPIFont();
		createCharPixelsContainers();
		//	highDPIScaleFactor = 1;
		logger.info("highDPIScaleFactor" + highDPIScaleFactor);
		this.setOpaque(true);
		//this.setDoubleBuffered(false);
		//this.setBackground(Color.white);
		//this.infoLabel = infoLabel;
		alignmentRuler = new AlignmentRuler(this);


	}

	public long getEndTime(){
		return endTime;
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
		//		Dimension prefSize = getPreferredSize();	

		// go on decreasing while everything is not in view or while font size >=1
		boolean didDecrease = false;
		if(this.getSize().width > this.getVisibleRect().width || this.getSize().height > this.getVisibleRect().height || charWidth >=1){

			double preferredWidth = charWidth;
			double preferredHeight = charHeight;

			if(charWidth > 1){
				// a little bit faster above char 18
				if(charWidth >= 18){
					preferredWidth = (int) (charWidth - 0.12*charWidth); // +1
				}else{
					preferredWidth = charWidth - 1;
				}

				preferredHeight = (int)(preferredWidth*CHAR_HEIGHT_RATIO);// 1.2 * charWidth;
			}
			else{
				preferredWidth = 0.85 * charWidth;
				preferredHeight = preferredWidth;
			}

			if(preferredWidth >= MIN_CHAR_SIZE){
				charWidth = preferredWidth;
				charHeight = preferredHeight;
			}
			//baseFont = new Font(baseFont.getName(), baseFont.getStyle(), (int)charWidth);

			createAdjustedDerivedBaseFont();
			createAdjustedDerivedHighDPIFont();
			createCharPixelsContainers();
			//	logFontMetrics();
			this.validateSize();	
			didDecrease = true;
		}

		return didDecrease;

	}

	public void incCharSize(){
		if(charWidth >= 1){
			// a little bit faster above char 16
			if(charWidth >= 16){
				charWidth = (int) (charWidth + 0.12*charWidth); // +1
			}
			else{
				charWidth = (int) charWidth + 1; // +1
			}
			charHeight = (int)(charWidth*CHAR_HEIGHT_RATIO);
		}else{
			charWidth = charWidth * 1.2; // +1

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
		createAdjustedDerivedHighDPIFont();
		createCharPixelsContainers();
		//		logFontMetrics();
		this.validateSize();
	}


	private void createCharPixelsContainers(){

		long startTime = System.currentTimeMillis();

		Font charFont = highDPIFont;
		// no less than 1
		int charPixWidth = Math.max(1, (int)(getCharWidth()));
		charPixWidth = charPixWidth * highDPIScaleFactor;
		// no less than 1
		int charPixHeight = Math.max(1, (int)(getCharHeight()));
		charPixHeight = charPixHeight * highDPIScaleFactor;

		int charMaxSizeToDraw = (int)MAX_CHARSIZE_TO_DRAW * highDPIScaleFactor;

		logger.info("charFont" + charFont.getSize());
		logger.info("charPixWidth" + charPixWidth);


		// Nucleotides

		charPixDefaultNuc = CharPixelsContainer.createDefaultNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixSelectedNuc = CharPixelsContainer.createSelectedNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixConsensusNuc = CharPixelsContainer.createConsensusNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		// Translated

		charPixTranslationDefault = TranslationCharPixelsContainer.createDefaultTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationSelected = TranslationCharPixelsContainer.createSelectedTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationLetter = TranslationCharPixelsContainer.createLetterTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationSelectedLetter = TranslationCharPixelsContainer.createSelectedLetterTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());



		// Translated and nuc at same time

		charPixTranslationAndNucDefault = TranslationCharPixelsContainer.createDefaultTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDefaultNoAALetter = TranslationCharPixelsContainer.createDefaultTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucSelected = TranslationCharPixelsContainer.createSelectedTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucSelectedNoAALetter = TranslationCharPixelsContainer.createSelectedTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNuc = TranslationCharPixelsContainer.createDominantNucTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNucNoAALetter = TranslationCharPixelsContainer.createDominantNucTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNucSelected = TranslationCharPixelsContainer.createSelectedDominantNucTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNucNoAALetterSelected = TranslationCharPixelsContainer.createSelectedDominantNucTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());


		// AminoAcid

		charPixDefaultAA =  new AACharPixelsContainer();
		if(colorSchemeAminoAcid.getALLCompundColors() != null){
			CompoundCharPixelsContainer compContainer = CompoundCharPixelsContainer.createDefaultCompoundColorContainer(charFont, charMaxSizeToDraw,
					charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());
			charPixDefaultAA.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = CharPixelsContainer.createDefaultAAContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());
			charPixDefaultAA.setContainer(container);
		}

		charPixSelectedAA =  new AACharPixelsContainer();
		if(colorSchemeAminoAcid.getALLCompundColors() != null){

			CompoundCharPixelsContainer compContainer = CompoundCharPixelsContainer.createSelectedCompoundColorContainer(charFont, charMaxSizeToDraw,
					charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());
			charPixSelectedAA.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = CharPixelsContainer.createSelectedAAContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());
			charPixSelectedAA.setContainer(container);
		}

		charPixConsensusAA =  new AACharPixelsContainer();
		if(colorSchemeAminoAcid.getALLCompundColors() != null){
			CompoundCharPixelsContainer compContainer = CompoundCharPixelsContainer.createDefaultCompoundColorContainer(charFont, charMaxSizeToDraw,
					charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());
			charPixConsensusAA.setCompoundContainer(compContainer);

		}else{
			CharPixelsContainer container = CharPixelsContainer.createConsensusAAContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());
			charPixConsensusAA.setContainer(container);
		}

		endTime = System.currentTimeMillis();
		logger.info("Creating charPixContainers took " + (endTime - startTime) + " milliseconds");

	}

	private int getFontCase() {
		return fontCase;
	}

	@Override
	public Font getFont() {
		return baseFont;
	}

	private void createAdjustedDerivedBaseFont() {
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();

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

	private void createAdjustedDerivedHighDPIFont() {
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();

		// create a font without Tracking to see the diff in font actual size and specified font size
		attributes.put(TextAttribute.TRACKING, 0);
		attributes.put(TextAttribute.SIZE, (int)charWidth*highDPIScaleFactor);
		Font calcFont = baseFont.deriveFont(attributes);
		FontMetrics metrics = getFontMetrics(calcFont);
		int fontActualWidth = metrics.stringWidth("X");

		double sizeDiff = charWidth*highDPIScaleFactor - fontActualWidth;
		// Calculate tracking for font size
		double tracking = (double)sizeDiff/charWidth*highDPIScaleFactor;
		logger.info("tracking" + tracking);

		// Create a font with correct tracking so characters are exactly spaced as pixels on pane
		attributes.put(TextAttribute.TRACKING, tracking); // 8
		attributes.put(TextAttribute.SIZE, (int)charWidth*highDPIScaleFactor);
		Font spacedFont = baseFont.deriveFont(attributes);

		highDPIFont = spacedFont;

	}

	private void logFontMetrics(Font font){
		FontMetrics metrics = this.getGraphics().getFontMetrics(font);

		logger.info("font.getSize()" + font.getSize());
		logger.info("font.getSize2D()" + font.getSize2D());

		// get the height of a line of text in this
		// font and render context	logger.info("baseFont.getSize()" + baseFont.getSize());
		logger.info("font.getSize2D()" + font.getSize2D());

		int hgt = metrics.getHeight();
		logger.info("metrics.getHeight()" + metrics.getHeight());
		logger.info("metrics.getMaxAdvance()" + metrics.getMaxAdvance());	// get the advance of my text in this font
		logger.info("metrics.getLeading()" + metrics.getLeading());

		int adv = metrics.stringWidth("A");


		logger.info("metrics.stringWidth(\"A\")" + metrics.stringWidth("AAAAAAAAAA"));
		logger.info("metrics.stringWidth(\"T\")" + metrics.stringWidth("T"));
		logger.info("metrics.stringWidth(\"c\")" + metrics.stringWidth("c"));
		logger.info("font.getAttributes().get(WIDTH_REGULAR)" + font.getAttributes().get(TextAttribute.WIDTH_REGULAR));

	}

	// should throw no valid base error
	public Point getBasePosition(Base base){
		if(base == null){
			return null;
		}
		int x = (int) (base.getPosition() * charWidth);
		int y = (int) (alignment.getSequenceIndex(base.getSequence()) * charHeight);

		Point pos = new Point(x,y);

		return pos;
	}

	public Base selectBaseAt(Point pos) throws InvalidAlignmentPositionException{

		Base base = null;

		base = getBaseAt(pos);
		if(base != null){

			base.getPosition();
			base.getSequence();
			alignment.getSequenceIndex(base.getSequence());
			alignment.setSelectionAt(base.getPosition(), alignment.getSequenceIndex(base.getSequence()),true);
		}

		return base;
	}

	public int getUngapedPositionInSequenceAt(Point pos) throws InvalidAlignmentPositionException{
		int ungapedPos = 0;

		Base base = getBaseAt(pos);
		if(base != null){
			ungapedPos = base.getUngapedPosition();
		}
		else{

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
		getAlignment().selectColumn(columnIndex);
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


	public void repaintAndForceRuler(){
		rulerIsDirty = true;
		repaint();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		paintAlignment(g);
	}


	public void paintAlignment(Graphics g){
		drawCounter ++;
		long startTime = System.currentTimeMillis();	
		if(drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			logger.info("Inside paintAlignment: Time from last endTim " + (startTime - endTime) + " milliseconds");
			System.out.println("Inside paintAlignment: Time from last endTim " + (startTime - endTime) + " milliseconds");
		}

		Graphics2D g2d = (Graphics2D) g;

		// What part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle clip = g2d.getClipBounds();

		
		Rectangle matrixClip = paneCoordToMatrixCoord(clip);

		//		 logger.info(matrixClip);

		int xMin = matrixClip.x - 1;
		int yMin = matrixClip.y - 1;
		int xMax = (int) matrixClip.getMaxX() + 1;
		int yMax = (int) matrixClip.getMaxY() + 1;
		//
		//				logger.info("yMin" + yMin);
		//				logger.info("yMin" + yMax);
		//				logger.info("xMin" + xMin);
		//				logger.info("xMax" + xMax);
		//
		// add one extra position when drawing translated
		// otherwise there could be some white borders when scrolling
		if(showTranslation){
			xMin --;
			xMax ++;
		}

		// adjust for part of matrix that exists
		xMin = Math.min(alignment.getMaxX(), xMin);
		xMin = Math.max(0, xMin);

		yMin = Math.min(alignment.getMaxY(), yMin);
		yMin = Math.max(0, yMin);

		xMax = Math.min(alignment.getMaxX(), xMax);
		yMax = Math.min(alignment.getMaxY(), yMax);

		//				logger.info("yMin" + yMin);
		//				logger.info("yMax" + yMax);
		//				logger.info("xMin" + xMin);
		//				logger.info("xMax" + xMax);

		// Extra because pixelCopyDraw
		int height = (yMax - yMin) * (int)charHeight;
		int width = (xMax - xMin) * (int)charWidth;
		//
		//				logger.info("width" + width);
		//				logger.info("height" + height);
		//
		// Small chars
		if(charWidth < 1){
			height = clip.height;
			width = clip.width;
		}
		//
		//				logger.info("yMax" + yMax);
		//				logger.info("yMin" + yMin);
		//				logger.info("width" + width);
		//				logger.info("clipHeight" + clip.height);
		//				logger.info("width" + width);
		//				logger.info("height" + height);


		// TODO adjust for retina

		int[] pixArray = new int[width* highDPIScaleFactor * height * highDPIScaleFactor];
		//	logger.info(pixArray.length);
		RGBArray clipRGB = new RGBArray(pixArray, width*highDPIScaleFactor, height*highDPIScaleFactor);




		// HERE FILL RGB-ARRAY DRAW...
		//		fillRGBArrayAndPaint(xMin, xMax, yMin, yMax, clipRGB, clip, g2d);
		fillRGBArrayAndPaintMultithreaded(xMin, xMax, yMin, yMax, clipRGB, clip, g2d);
		
		

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

	private void fillRGBArrayAndPaintMultithreaded(int xMin, int xMax, int yMin, int yMax, RGBArray clipRGB, Rectangle clip, Graphics2D g2d){
		// these vals are not going to change so get it only once
		boolean isNucleotideAlignment = alignment.isNucleotideAlignment();
		double seqPerPixX = 1/(double)charWidth;
		double seqPerPixY = 1/(double)charWidth;

		logger.info("Runtime.getRuntime().availableProcessors()" + Runtime.getRuntime().availableProcessors());
		int nThreads = 1;
		
		// Only one thread if filesequences - more threads make reading file slower
		if(alignment.isFileSequences()){
			nThreads = 1;
		}else{
			if(Runtime.getRuntime().availableProcessors() > 2){
				nThreads = 2;
			}
			if(Runtime.getRuntime().availableProcessors() > 4){
				nThreads = 3;
			}
		}
		
		


		// small chars have their own loop here
		if(charWidth < 1){
			ExecutorService executor = Executors.newFixedThreadPool(nThreads);

			// No longer: Always start at closest even 10
			//			double startY = clip.y;
			//			startY = Math.floor(startY/100) * 100;

			int clipYPos = 0;
			for(int y = clip.y; y < clip.getMaxY(); y ++){

				int ySeq = (int)((double)(y) * seqPerPixY);

				if(ySeq <= yMax && ySeq >= 0){

					double xMinimum = clip.x;
					double xMaximum = clip.getMaxX();
					double xSeqMin =  (int)((double)xMinimum * seqPerPixX);
					double xSeqMax =  (int)((double)xMaximum * seqPerPixX);
					int seqYPos = ySeq;
					Sequence seq = alignment.getSequences().get(seqYPos);
					double step = seqPerPixX;
					int xPosStart = (int) xSeqMin;
					int xPosEnd = (int) xSeqMax;

					if(isNucleotideAlignment){
						if(isShowTranslationOnePos()){							
							SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);

						}else if(showTranslation && !isShowTranslationOnePos() && ignoreGapInTranslation){
							SequencePainter seqPainter = new SequencePainterAminoAcidTranslatedIgnoreGap(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);

						}else if(showTranslation){
							if(showTranslationAndNuc){
								SequencePainter seqPainter = new SequencePainterNucleotideTranslatedShowNucAndAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
								executor.execute(seqPainter);

							}else{
								SequencePainter seqPainter = new SequencePainterAminoAcidTranslated(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
								executor.execute(seqPainter);
							}

						}else{
							SequencePainter seqPainter = new SequencePainterNucleotide(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);
						}
					}
					// Draw as AminoAcids
					else{
						SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);
					}


				}
				else{
					logger.info("outside");
				}

				clipYPos ++;
			}


			executor.shutdown();
			try {
				executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}	


		/////////////////////////
		//
		// Normal char width
		//
		/////////////////////////
		else{


			ExecutorService executor = Executors.newFixedThreadPool(nThreads);

			int clipYPos = 0;			
			for(int y = yMin; y < yMax; y = y + 1){

				int seqYPos = y;
				Sequence seq = alignment.getSequences().get(seqYPos);
				int step = 1;
				int xPosStart = xMin;
				int xPosEnd = xMax;


				if(isNucleotideAlignment){
					if(isShowTranslationOnePos()){							
						SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);

					}else if(showTranslation && !isShowTranslationOnePos() && ignoreGapInTranslation){
						SequencePainter seqPainter = new SequencePainterAminoAcidTranslatedIgnoreGap(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);

					}else if(showTranslation){
						if(showTranslationAndNuc){
							SequencePainter seqPainter = new SequencePainterNucleotideTranslatedShowNucAndAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);

						}else{
							SequencePainter seqPainter = new SequencePainterAminoAcidTranslated(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);
						}

					}else{
						SequencePainter seqPainter = new SequencePainterNucleotide(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);
					}
				}
				// Draw as AminoAcids
				else{
					SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, step, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
					executor.execute(seqPainter);
				}

				clipYPos ++;
			}

			executor.shutdown();
			try {
				executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		//          // Excludes by manipulating pixelColor		
		//			for(int n = 0; n <clipRGB.getBackend().length; n++){	
		//				clipRGB.
		//				clipRGB.getBackend()[n] = ColorUtils.darkerRGB(clipRGB.getBackend()[n]);
		//			}



		// Now draw the pixels onto the image
		Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));
		// First fill background
		g2d.setColor(this.getBackground());
		g2d.fill(clip);



		int clipRGBXPos = clip.x;
		int clipRGBYPos = clip.y;
		// Adjust because we start always on exact char upp to one pos before
		if(charWidth > 1){
			clipRGBXPos = (int)(xMin * charWidth);
			clipRGBYPos = (int)(yMin * charHeight);
		}

		if (img != null){	
			// Mac retina screen
			if(highDPIScaleFactor > 1){
				int dx1 = clipRGBXPos;
				int dx2 = dx1 + clipRGB.getScanWidth() / highDPIScaleFactor;
				int dy1 = clipRGBYPos;
				int dy2 = dy1 + clipRGB.getHeight() / highDPIScaleFactor;

				int sx1 = 0;
				int sx2 = sx1 + clipRGB.getScanWidth();
				int sy1 = 0;
				int sy2 = sy1 + clipRGB.getHeight();
				g2d.drawImage(img,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2, null);
			}else{
				g2d.drawImage(img, clipRGBXPos, clipRGBYPos, null);
			}
		}


		// Draw excludes	
		if(! isShowTranslationOnePos()){

			// calculate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
			int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);

			// Two versions depending on if it is small chars or not
			if(charWidth < 1){
				for(int x = clip.x; x < clip.getMaxX() ; x++){
					int xPos =(int)((double)x * (1/(double)charWidth));
					if(alignment.isExcluded(xPos) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect(x, this.getVisibleRect().y, 1, drawExcludesHeight);
						//				logger.info("drawExclude");
					}
				}
			}else{
				for(int x = xMin; x < xMax ; x++){
					if(alignment.isExcluded(x) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect((int)(x * charWidth), this.getVisibleRect().y, (int)charWidth, drawExcludesHeight);				
					}
				}
			}
		}
	}


	private void copyTranslatedNucleotidesPixelsSkipGap(RGBArray clipArray, byte residue, AminoAcid acid, int x, int y, int clipX, int clipY, int acidStartPos){

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// // set defaults
		// AminoAcid acid = aaTransSeq.getNoGapAminoAcidAtNucleotidePos(x);
		// int acidStartPos = aaTransSeq.getCachedClosestStartPos();

		TranslationCharPixelsContainer pixContainerToUse = charPixTranslationDefault;
		TranslationCharPixelsContainer pixLetterContainerToUse = charPixTranslationLetter;

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(x <= alignment.getTempSelection().getMaxX() && x >= alignment.getTempSelection().getMinX() && y <= alignment.getTempSelection().getMaxY() && y >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
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



	private void copyTranslatedNucleotidesPixelsShowTranslationAndNuc(RGBArray clipArray, byte residue, AminoAcid acid, int x, int y, int clipX, int clipY, Sequence seq){

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(x <= alignment.getTempSelection().getMaxX() && x >= alignment.getTempSelection().getMinX() && y <= alignment.getTempSelection().getMaxY() && y >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		boolean isSelected = false;
		if(alignment.isBaseSelected(x,y) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			isSelected = true;
		}

		boolean isSecondPos = false;
		if(seq.isCodonSecondPos(x)){
			isSecondPos = true;
		}

		TranslationCharPixelsContainer pixContainerToUse = charPixTranslationAndNucDefault;
		if(! drawAminoAcidCode){
			if(isSecondPos){
				if(isSelected){
					pixContainerToUse = charPixTranslationAndNucSelected;
				}else{
					pixContainerToUse = charPixTranslationAndNucDefault;
				}
			}else{
				if(isSelected){
					pixContainerToUse = charPixTranslationAndNucSelectedNoAALetter;
				}else{
					pixContainerToUse = charPixTranslationAndNucDefaultNoAALetter;
				}	
			}
		}else{
			if(isSecondPos){
				if(isSelected){
					pixContainerToUse = charPixTranslationAndNucDominantNucSelected;
				}else{
					pixContainerToUse = charPixTranslationAndNucDominantNuc;
				}
			}else{
				if(isSelected){
					pixContainerToUse = charPixTranslationAndNucDominantNucNoAALetterSelected;
				}else{
					pixContainerToUse = charPixTranslationAndNucDominantNucNoAALetter;
				}	
			}
		}

		RGBArray newPiece = pixContainerToUse.getRGBArray(acid, residue);


		try {
			ImageUtils.insertRGBArrayAt(clipX, clipY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("clipX" + clipX);
			logger.info("clipY" + clipY);
		}
	}

	private void copyTranslatedNucleotidesPixels(RGBArray clipArray, byte residue, AminoAcid acid, int x, int y, int clipX, int clipY, Sequence seq){

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		//AminoAcid acid =  aaTransSeq.getAminoAcidAtNucleotidePos(x);
		TranslationCharPixelsContainer pixContainerToUse = charPixTranslationDefault;
		TranslationCharPixelsContainer pixLetterContainerToUse = charPixTranslationLetter;
		TranslationCharPixelsContainer pixLetterContainerToUseNoAALetter = charPixTranslationDefault;


		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(x <= alignment.getTempSelection().getMaxX() && x >= alignment.getTempSelection().getMinX() && y <= alignment.getTempSelection().getMaxY() && y >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){		
			pixContainerToUse = charPixTranslationSelected;
			pixLetterContainerToUse = charPixTranslationSelectedLetter;			
		}

		RGBArray newPiece;

		if(! drawAminoAcidCode){	
			newPiece = pixContainerToUse.getRGBArray(acid, residue);
		}else{
			if(seq.isCodonSecondPos(x)){
				newPiece = pixLetterContainerToUse.getRGBArray(acid, residue);
			}else{
				residue = ' ';
				newPiece = pixLetterContainerToUseNoAALetter.getRGBArray(acid, residue);
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

		// A small hack
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
		if(alignment.getTempSelection() != null){
			if(x <= alignment.getTempSelection().getMaxX() && x >= alignment.getTempSelection().getMinX() && y <= alignment.getTempSelection().getMaxY() && y >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
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

	private void copyNucleotidePixelsTimeTest(RGBArray clipArray, byte residue, int x, int y, int clipX, int clipY){
		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		CharPixelsContainer pixContainerToUse = charPixDefaultNuc;
		byteToDraw = residue;
		//		int baseVal = NucleotideUtilities.baseValFromBase(residue);

		int baseVal = NucleotideUtilities.baseValFromBaseOtherVer(residue);

		//int baseVal = 1;


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
		if(alignment.getTempSelection() != null){
			if(x <= alignment.getTempSelection().getMaxX() && x >= alignment.getTempSelection().getMinX() && y <= alignment.getTempSelection().getMaxY() && y >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		//				if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
		//					pixContainerToUse = charPixSelectedNuc;
		//				}

		//				RGBArray newPiece = pixContainerToUse.getRGBArray(byteToDraw);

		try {
			//					ImageUtils.insertRGBArrayAt(clipX, clipY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("x" + x);
			logger.info("y" + y);
			logger.info("clipX" + clipX);
			logger.info("clipY" + clipY);
		}

	}



	private void copyNucleotidePixels(RGBArray clipArray, byte residue, int x, int y, int clipX, int clipY){

		// A small hack
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
		if(alignment.getTempSelection() != null){
			if(x <= alignment.getTempSelection().getMaxX() && x >= alignment.getTempSelection().getMinX() && y <= alignment.getTempSelection().getMaxY() && y >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(alignment.isBaseSelected(x,y) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
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

		alignment.setSelectionWithin(bounds);

		return nSelection;
	}

	/*
	private Rectangle getTempSelection() {
		return alignment.getTempSelection();
	}
	 */

	/*
	public void clearTempSelection() {
		this.tempSelectionRect = null;
	}

	 */

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

		// also set min to 0
		matrixMinX = Math.max(0, matrixMinX);
		matrixMaxX = Math.max(0, matrixMaxX);
		matrixMinY = Math.max(0, matrixMinY);
		matrixMaxY = Math.max(0, matrixMaxY);

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
		Dimension current = getSize();
		Dimension prefSize = getCalculatedPreferredSize();
		//	Rectangle prefRect = this.getVisibleRect();

		if(current.width != prefSize.width || current.height != prefSize.height){
			this.setPreferredSize(prefSize);
			//this.updateStatisticsLabel();
			this.rulerIsDirty = true;
			this.revalidate();
		}
		//		this.scrollRectToVisible(prefRect);
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

		//		logger.info("charWidth" + charWidth);
		//		logger.info("charHeight" + charHeight);

		//		if(showTranslationOnePos){
		//			newDim = new Dimension((int) (charWidth * alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()), (int)(charHeight * alignment.getSize()));
		//		}else{
		newDim = new Dimension((int) (charWidth * alignment.getMaximumSequenceLength()), (int)(charHeight * alignment.getSize()));
		//		}		
		//		logger.info("newDim" + newDim);

		if(newDim.width == Integer.MAX_VALUE || newDim.height == Integer.MAX_VALUE){
			Messenger.showMaxJPanelSizeMessageOnceThisSession();
			//			logger.info("Hit max jpanel length");
		}
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

	public boolean isShowTranslationOnePos() {
		return alignment.isTranslatedOnePos();
	}

	public JComponent getRulerComponent(){
		return this.alignmentRuler;
	}

	public void setDrawAminoAcidCode(boolean drawCode){
		this.drawAminoAcidCode = drawCode;
	}

	public boolean isDrawAminoAcidCode(){
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
		Rectangle rect = new Rectangle(ulPanePos, this.getVisibleRect().getSize());
		rect.grow(-10, -10);
		logger.info("ulPanePos" + ulPanePos);
		logger.info("Scroll to rect" + rect);
		this.scrollRectToVisible(rect);
		logger.info("after this.getVisibleRect()" + this.getVisibleRect());
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


			//		g2d.setFont(baseFont);

			// What part of alignment matrix is in view (what part of matrix is in graphical view)
			Rectangle paneClip = alignmentPane.getVisibleRect();
			Rectangle matrixClip = paneCoordToMatrixCoord(paneClip);

			// todo calculate from font metrics
			double charCenterXOffset = 0.9997;


			// NUMBERS
			int rulerCharWidth = 11;
			//int rulerCharHeight = 11;
			Font rulerFont = new Font(alignmentPane.getFont().getName(), alignmentPane.getFont().getStyle(), (int)rulerCharWidth);
			g2d.setFont(rulerFont);



			//
			// Draw ruler background
			//
			Rectangle rulerRect = new Rectangle(this.getVisibleRect());
			g2d.setColor(colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP));
			g2d.fill(rulerRect);

			int offsetDueToScrollPanePosition = 0;





			// Normal char-with smaller 
			if(charWidth >= 1){

				offsetDueToScrollPanePosition = paneClip.x % (int)charWidth;
				offsetDueToScrollPanePosition = offsetDueToScrollPanePosition -1;

				// Tickmarks
				int posTick = 0;
				int count = 0;

				int maxY = alignment.getMaxY();
				int maxX = alignment.getMaxX();
				//				if(showTranslationOnePos){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getTranslatedAminAcidLength();
				//				}

				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					// Only draw part of matrix that exists 
					if(maxY > 0 && x >= 0 && x < maxX){

						// draw codon-pos background on ruler depending on codonpos
						if(drawCodonPosRuler && ! isShowTranslationOnePos()){
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
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.fillRect((int)(posTick * charCenterXOffset * charWidth - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - boxHeight), (int)charWidth, boxHeight);
						}



						// draw tickmarks
						g2d.setColor(Color.DARK_GRAY);
						// make every 5 tickmarks a bit bigger
						if(x % 5 == 4 && charWidth > 0.6){ // it has to be 4 and not 0 due to the fact that 1:st base har position 0 in matrix
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 5);
						}
						// dont draw smallest tick if to small
						else if(charWidth > 4){
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 3);
						}


						// and numbers


						posTick ++;
					}
					count ++;
				}

				// NUMBERS

				// Only draw every xx pos
				int drawEveryNpos = 10;

				if(charWidth < 4){
					drawEveryNpos = 50;
				}else if(charWidth < 5){
					drawEveryNpos = 20;
				}

				// position numbers
				int lastTextEndPos = 0;
				int pos = 0;
				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					if(x % drawEveryNpos == 0){
						String posText = Integer.toString(x);
						int stringSizeOffset = g2d.getFontMetrics().stringWidth(posText) / 2;
						//int stringSizeOffset = (int)((posText.length()*0.8 * rulerCharWidth) / 2) + 5;
						//int stringSizeOffset = ( posText.length()*(rulerFont.getSize()) ) / 2;
						//	int stringSizeOffset = (int)((posText.length()*0.8 * rulerCharWidth) / 2) + 5;
						int textPosX = (int)((pos -1) * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition) - stringSizeOffset;
						// dont draw on top of last (if number is very long)
						if(lastTextEndPos < textPosX){
							g2d.drawString(posText, textPosX, 10);
							lastTextEndPos = textPosX + stringSizeOffset + 40; // add 40 extra space between numbers
						}
					}
					pos ++;
				}	
			}
			// Less than one pix char size 
			else{



				double seqOffsetVisiblePanePos = matrixClip.getMinX() -1; //(double)paneClip.x / charWidth;


				// pos per pixel
				//	double posPerPix = 1/charWidth;

				double posPerPix = matrixClip.getWidth() / paneClip.getWidth();

				int xStep = 10;

				if(posPerPix < 2.5){
					xStep = 10;
				}
				else{	
					// This loop is the same as all the commented (else if) below
					// first set something if something in loop goes wrong...
					xStep = 100000000;
					for(int posPixRange = 5; posPixRange < Integer.MAX_VALUE; posPixRange = (int)(posPixRange * 2)){		
						if(posPerPix < posPixRange){
							xStep = posPixRange * 5;

							break;
						}
					}
				}


				/*
				else if(posPerPix < 5){
					xStep = 25;
				}
				else if(posPerPix < 10){
					xStep = 50;
				}
				else if(posPerPix < 20){
					xStep = 100;
				}
				else if(posPerPix < 40){
					xStep = 200;
				}
				else if(posPerPix < 80){
					xStep = 400;
				}
				else if(posPerPix < 160){
					xStep = 800;
				}
				else if(posPerPix < 320){
					xStep = 1600;
				}
				else if(posPerPix < 640){
					xStep = 3200;
				}
				else if(posPerPix < 1000){
					xStep = 5000;
				}
				else if(posPerPix < 2000){
					xStep = 10000;
				}
				else{
					xStep = 80000;
				}

				 */


				double startPosSeq = roundToClosestUpper((int)seqOffsetVisiblePanePos,xStep);
				int startPosPane = (int) (charWidth * startPosSeq);

				//				logger.info("ruler startPosSeq" + startPosSeq);
				//				logger.info("ruler startPosPane" + startPosPane);		
				//				logger.info("posPerPix" + posPerPix);


				int maxY = alignment.getMaxY();
				int maxX = alignment.getMaxX();
				//				if(showTranslationOnePos){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getTranslatedAminAcidLength();
				//				}

				int maxVisibleSeq = (int)matrixClip.getMaxX();
				logger.info("maxVisibleSeq" + maxVisibleSeq + 200);

				int lastTextEndPos = 0;

				// Tickmarks
				int countTicks = 0;

				// Same color for everything
				g2d.setColor(Color.DARK_GRAY);

				// X Loop Start
				for(int xSeq = (int)startPosSeq; xSeq < maxVisibleSeq; xSeq = xSeq + xStep){

					// get closest pane pos
					int xPane = (int)  ( (double) xSeq / posPerPix ); 
					//					
					//					logger.info("maxX" + maxX);
					//					logger.info("xPane" + xPane);

					// Only draw part of matrix that exists 
					if(maxY > 0 && xSeq >= 0 && xSeq < maxX){

						// no no codon-pos-ruler


						// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with pane.x
						// since it is hidden in scrollpane
						int tickPosX = (xPane - paneClip.x);

						// larger and text every 10-interval
						int tickSize;	
						int largerInterval = xStep * 10;

						if(xSeq % largerInterval == 0){																				
							String posText = Integer.toString(xSeq);

							int stringSizeOffset = g2d.getFontMetrics().stringWidth(posText) / 2;

							//				int stringSizeOffset = ( posText.length()*(rulerFont.getSize() -1) ) / 2;
							//int stringSizeOffset = (int)((posText.length() * (rulerCharWidth)) / 2) ;
							int textPosX = (int)(tickPosX - stringSizeOffset);
							// dont draw text outside
							if(textPosX >=0){
								// dont draw on top of last (if number is very long)
								if(lastTextEndPos < textPosX){
									g2d.drawString(posText, textPosX, 10);
									lastTextEndPos = textPosX + stringSizeOffset + 40; // add 40 extra space between numbers
								}
							}
							// larger tick size
							tickSize = 3;	
						}else{
							// smaller tick size
							tickSize = 1;
						}		

						// draw tick
						g2d.drawLine(tickPosX, (int) (rulerRect.getMaxY() - 2),tickPosX, (int)rulerRect.getMaxY() - 2 - tickSize);

						countTicks ++;
					}
				}

			} // end draw small char

			long endTime = System.currentTimeMillis();
			logger.info("Ruler PaintComponent took " + (endTime - startTime) + " milliseconds");


		}


		private int roundToClosestUpper(int inval, int roundTo) {
			// int rounded = ((num + 99) / 100 ) * 100;
			int rounded = ((inval + roundTo -1) / roundTo ) * roundTo;
			return rounded;
		}

	} // end Ruler class

	private class AlignmentCharsetRuler extends JPanel{

		private AlignmentPane alignmentPane;

		public AlignmentCharsetRuler(AlignmentPane alignmentPane) {
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


			//		g2d.setFont(baseFont);

			// What part of alignment matrix is in view (what part of matrix is in graphical view)
			Rectangle paneClip = alignmentPane.getVisibleRect();
			Rectangle matrixClip = paneCoordToMatrixCoord(paneClip);

			// todo calculate from font metrics
			double charCenterXOffset = 0.9997;


			// NUMBERS
			int rulerCharWidth = 11;
			//int rulerCharHeight = 11;
			Font rulerFont = new Font(alignmentPane.getFont().getName(), alignmentPane.getFont().getStyle(), (int)rulerCharWidth);
			g2d.setFont(rulerFont);

			//
			// Draw ruler background
			//
			Rectangle rulerRect = new Rectangle(this.getVisibleRect());
			g2d.setColor(colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP));
			g2d.fill(rulerRect);

			int offsetDueToScrollPanePosition = 0;





			// Normal char-with smaller 
			if(charWidth >= 1){

				offsetDueToScrollPanePosition = paneClip.x % (int)charWidth;
				offsetDueToScrollPanePosition = offsetDueToScrollPanePosition -1;

				// Tickmarks
				int posTick = 0;
				int count = 0;

				int maxY = alignment.getMaxY();
				int maxX = alignment.getMaxX();
				//				if(showTranslationOnePos){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getTranslatedAminAcidLength();
				//				}

				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					// Only draw part of matrix that exists 
					if(maxY > 0 && x >= 0 && x < maxX){

						// draw codon-pos background on ruler depending on codonpos
						if(drawCodonPosRuler && ! isShowTranslationOnePos()){
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
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.fillRect((int)(posTick * charCenterXOffset * charWidth - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - boxHeight), (int)charWidth, boxHeight);
						}



						// draw tickmarks
						g2d.setColor(Color.DARK_GRAY);
						// make every 5 tickmarks a bit bigger
						if(x % 5 == 4 && charWidth > 0.6){ // it has to be 4 and not 0 due to the fact that 1:st base har position 0 in matrix
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 5);
						}
						// dont draw smallest tick if to small
						else if(charWidth > 4){
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 3);
						}


						// and numbers


						posTick ++;
					}
					count ++;
				}

				// NUMBERS

				// Only draw every xx pos
				int drawEveryNpos = 10;

				if(charWidth < 4){
					drawEveryNpos = 50;
				}else if(charWidth < 5){
					drawEveryNpos = 20;
				}

				// position numbers
				int lastTextEndPos = 0;
				int pos = 0;
				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					if(x % drawEveryNpos == 0){
						String posText = Integer.toString(x);
						int stringSizeOffset = g2d.getFontMetrics().stringWidth(posText) / 2;
						//int stringSizeOffset = (int)((posText.length()*0.8 * rulerCharWidth) / 2) + 5;
						//int stringSizeOffset = ( posText.length()*(rulerFont.getSize()) ) / 2;
						//	int stringSizeOffset = (int)((posText.length()*0.8 * rulerCharWidth) / 2) + 5;
						int textPosX = (int)((pos -1) * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition) - stringSizeOffset;
						// dont draw on top of last (if number is very long)
						if(lastTextEndPos < textPosX){
							g2d.drawString(posText, textPosX, 10);
							lastTextEndPos = textPosX + stringSizeOffset + 40; // add 40 extra space between numbers
						}
					}
					pos ++;
				}	
			}
			// Less than one pix char size 
			else{



				double seqOffsetVisiblePanePos = matrixClip.getMinX() -1; //(double)paneClip.x / charWidth;


				// pos per pixel
				//	double posPerPix = 1/charWidth;

				double posPerPix = matrixClip.getWidth() / paneClip.getWidth();

				int xStep = 10;

				if(posPerPix < 2.5){
					xStep = 10;
				}
				else{	
					// This loop is the same as all the commented (else if) below
					// first set something if something in loop goes wrong...
					xStep = 100000000;
					for(int posPixRange = 5; posPixRange < Integer.MAX_VALUE; posPixRange = (int)(posPixRange * 2)){		
						if(posPerPix < posPixRange){
							xStep = posPixRange * 5;

							break;
						}
					}
				}


				/*
				else if(posPerPix < 5){
					xStep = 25;
				}
				else if(posPerPix < 10){
					xStep = 50;
				}
				else if(posPerPix < 20){
					xStep = 100;
				}
				else if(posPerPix < 40){
					xStep = 200;
				}
				else if(posPerPix < 80){
					xStep = 400;
				}
				else if(posPerPix < 160){
					xStep = 800;
				}
				else if(posPerPix < 320){
					xStep = 1600;
				}
				else if(posPerPix < 640){
					xStep = 3200;
				}
				else if(posPerPix < 1000){
					xStep = 5000;
				}
				else if(posPerPix < 2000){
					xStep = 10000;
				}
				else{
					xStep = 80000;
				}

				 */


				double startPosSeq = roundToClosestUpper((int)seqOffsetVisiblePanePos,xStep);

				int startPosPane = (int) (charWidth * startPosSeq);

				//				logger.info("ruler startPosSeq" + startPosSeq);
				//				logger.info("ruler startPosPane" + startPosPane);		
				//				logger.info("posPerPix" + posPerPix);


				int maxY = alignment.getMaxY();
				int maxX = alignment.getMaxX();
				//				if(showTranslationOnePos){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getTranslatedAminAcidLength();
				//				}

				int maxVisibleSeq = (int)matrixClip.getMaxX();
				logger.info("maxVisibleSeq" + maxVisibleSeq + 200);

				int lastTextEndPos = 0;

				// Tickmarks
				int countTicks = 0;

				// Same color for everything
				g2d.setColor(Color.DARK_GRAY);

				// X Loop Start
				for(int xSeq = (int)startPosSeq; xSeq < maxVisibleSeq; xSeq = xSeq + xStep){

					// get closest pane pos
					int xPane = (int)  ( (double) xSeq / posPerPix ); 
					//					
					//					logger.info("maxX" + maxX);
					//					logger.info("xPane" + xPane);

					// Only draw part of matrix that exists 
					if(maxY > 0 && xSeq >= 0 && xSeq < maxX){

						// no no codon-pos-ruler


						// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with pane.x
						// since it is hidden in scrollpane
						int tickPosX = (xPane - paneClip.x);

						// larger and text every 10-interval
						int tickSize;	
						int largerInterval = xStep * 10;

						if(xSeq % largerInterval == 0){																				
							String posText = Integer.toString(xSeq);

							int stringSizeOffset = g2d.getFontMetrics().stringWidth(posText) / 2;

							//				int stringSizeOffset = ( posText.length()*(rulerFont.getSize() -1) ) / 2;
							//int stringSizeOffset = (int)((posText.length() * (rulerCharWidth)) / 2) ;
							int textPosX = (int)(tickPosX - stringSizeOffset);
							// dont draw text outside
							if(textPosX >=0){
								// dont draw on top of last (if number is very long)
								if(lastTextEndPos < textPosX){
									g2d.drawString(posText, textPosX, 10);
									lastTextEndPos = textPosX + stringSizeOffset + 40; // add 40 extra space between numbers
								}
							}
							// larger tick size
							tickSize = 3;	
						}else{
							// smaller tick size
							tickSize = 1;
						}		

						// draw tick
						g2d.drawLine(tickPosX, (int) (rulerRect.getMaxY() - 2),tickPosX, (int)rulerRect.getMaxY() - 2 - tickSize);

						countTicks ++;
					}
				}

			} // end draw small char

			long endTime = System.currentTimeMillis();
			logger.info("Ruler PaintComponent took " + (endTime - startTime) + " milliseconds");

		}

		private int roundToClosestUpper(int inval, int roundTo) {
			// int rounded = ((num + 99) / 100 ) * 100;
			int rounded = ((inval + roundTo -1) / roundTo ) * roundTo;
			return rounded;
		}


	} // end CodonPosRuler class

	public void setFontCase(int fontCase){
		this.fontCase = fontCase;
		createCharPixelsContainers();
	}

	public void scrollRectToSelection() {
		Rectangle selectRect = alignment.getSelectionAsMinRect();
		if(selectRect != null){
			Rectangle grown1xtra = new Rectangle(selectRect.x - 1, selectRect.y - 1, selectRect.width + 3, selectRect.height + 3);
			Rectangle paneCoord = matrixCoordToPaneCoord(grown1xtra);
			if(! getVisibleRect().contains(selectRect)){
				logger.info("not visible");
				scrollRectToVisible(paneCoord);
			}
		}
	}

	public void scrollRectToSelectionCenter() {
		Rectangle selectRect = alignment.getSelectionAsMinRect();
		if(selectRect != null){
			Rectangle paneCoord = matrixCoordToPaneCoord(selectRect);
			if(! getVisibleRect().contains(selectRect)){
				logger.info("not visible");
				Rectangle newVisible = new Rectangle(paneCoord);
				//logger.info("new visible" + newVisible);
				newVisible.grow(getVisibleRect().width/2,getVisibleRect().height/2);
				//logger.info("newVisible" + newVisible);
				scrollRectToVisible(newVisible);
			}
		}
	}

	public boolean getShowTranslationAndNuc() {
		return showTranslationAndNuc;
	}

	public void setShowTranslationAndNuc(boolean b) {
		showTranslationAndNuc = b;

	}

	public int getDifferenceTraceSequencePosition() {
		return differenceTraceSequencePosition;
	}

	public boolean isHighlightDiffTrace() {
		return highlightDiffTrace;
	}
	
	private void fillRGBArrayAndPaint(int xMin, int xMax, int yMin, int yMax, RGBArray clipRGB, Rectangle clip, Graphics2D g2d){
		// these vals are not going to change so get it only once
		boolean isNucleotideAlignment = alignment.isNucleotideAlignment();
		double seqPerPixX = 1/(double)charWidth;
		double seqPerPixY = 1/(double)charWidth;
		//int ySeqMax = alignment.getMaxY();

		// test time to get all bases
		boolean testloop = false;
		if(testloop && charWidth >= 1){
			long sTimeMS = System.currentTimeMillis();
			long sTimeNS = System.nanoTime();

			int testWidth = (xMax - xMin) * (int)charWidth;
			int testHeight = (yMax - yMin) * (int)charHeight;
			int[] testpixArray = new int[testWidth * testHeight];
			RGBArray testclipRGB = new RGBArray(testpixArray, testWidth, testHeight);

			int count = 0;
			int clipY = 0;
			for(int y = yMin; y <= yMax; y++){

				// X Loop Start
				int clipX = 0;
				for(int x = xMin; x < xMax ; x++){
					byte residue = alignment.getBaseAt(x,y);
					// Draw as nucleotides
					if(isNucleotideAlignment){
						// Draw as translated
						if(showTranslation){
							if(ignoreGapInTranslation){
								//copyTranslatedNucleotidesPixelsSkipGap(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight), aaTransSeq);
							}
							else{
								//copyTranslatedNucleotidesPixels(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight), aaTransSeq);
							}
						}else{
							//logger.info("testPixArr" + testpixArray.length);
							copyNucleotidePixelsTimeTest(testclipRGB,residue,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor));					
						}
					}
					// Draw as AminoAcids
					else{
						//copyAminoAcidPixels(clipRGB,residue,x,y,(int)(clipX*charWidth), (int)(clipY*charHeight));	
					}
					clipX ++;
					count ++;
				}		
				clipY ++;
			}
			long eTimeMS = System.currentTimeMillis();
			long eTimeNS = System.nanoTime();
			logger.info("Testloop took " + (eTimeMS - sTimeMS) + " milliseconds, count " + count);
			logger.info("Testloop took " + (eTimeNS - sTimeNS) + " nanoseconds, count " + count);
		}


		// small chars have their own loop here
		if(charWidth < 1){

			if(showTranslation && ! isShowTranslationOnePos()){

				// this only need to be calculated once
				double maxX = clip.getMaxX();
				//				if(maxX > alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos();
				//				}

				int clipY = 0;
				for(int y = clip.y; y < clip.getMaxY(); y ++){		

					int ySeq = (int)((double)(y) * seqPerPixY);

					if(ySeq <= yMax && ySeq >= 0){	

						// X Loop Start
						int seqMaxX = alignment.getLengthAt(ySeq);
						int clipX = 0;
						for(int x = clip.x; x < clip.getMaxX(); x++){
							int xSeqPos =(int)((double)x * seqPerPixX);

							if(xSeqPos < seqMaxX && xSeqPos >= 0){

								byte residue = alignment.getBaseAt(xSeqPos,ySeq);

								if(ignoreGapInTranslation){
									AminoAcidAndPosition aaAndPos = alignment.getSequences().get(y).getNoGapAminoAcidAtNucleotidePos(xSeqPos);
									copyTranslatedNucleotidesPixelsSkipGap(clipRGB,residue,aaAndPos.acid,xSeqPos,ySeq,clipX*highDPIScaleFactor, clipY*highDPIScaleFactor, aaAndPos.position);
								}
								else{
									AminoAcid aminoAcid = alignment.getTranslatedAminoAcidAtNucleotidePos(xSeqPos,ySeq);
									copyTranslatedNucleotidesPixels(clipRGB,residue, aminoAcid,xSeqPos,ySeq,clipX*highDPIScaleFactor, clipY*highDPIScaleFactor, alignment.getSequences().get(y));
								}
							}

							clipX ++;
						}

					}
					clipY ++;
				}


			}else{

				// No longer: Always start at closest even 10
				//			double startY = clip.y;
				//			startY = Math.floor(startY/100) * 100;
				int clipY = 0;
				for(int y = clip.y; y < clip.getMaxY(); y ++){

					int ySeq = (int)((double)(y) * seqPerPixY);

					if(ySeq <= yMax && ySeq >= 0){

						int seqMaxX = alignment.getLengthAt(ySeq);

						// X Loop Start
						int clipX = 0;
						for(int x = clip.x; x < clip.getMaxX(); x++){

							int xSeqPos =(int)((double)x * seqPerPixX);

							if(xSeqPos < seqMaxX && xSeqPos >= 0){

								boolean valid = alignment.isPositionValid(xSeqPos, ySeq);
								if(! valid){
									logger.info("invalidx="+xSeqPos + "y=" + ySeq);
								}
								byte residue = alignment.getBaseAt(xSeqPos,ySeq);

								// Draw as Nucleotides
								if(isNucleotideAlignment){
									if(isShowTranslationOnePos()){
										copyAminoAcidPixels(clipRGB,residue,xSeqPos,ySeq,clipX*highDPIScaleFactor,clipY*highDPIScaleFactor);
									}
									else{
										copyNucleotidePixels(clipRGB,residue,xSeqPos,ySeq,clipX*highDPIScaleFactor,clipY*highDPIScaleFactor);
									}			
								}
								// Draw as AA
								else{
									copyAminoAcidPixels(clipRGB,residue,xSeqPos,ySeq,clipX*highDPIScaleFactor,clipY*highDPIScaleFactor);
								}
							}

							clipX ++;

						}		

					}
					clipY ++;
				}
			}


			// Now draw the pixels onto the image
			Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));
			//
			//									logger.info(clipRGB.getBackend().length);
			//									logger.info("img.getWidth" + img.getWidth(null));
			//									logger.info("img.getHeight" + img.getHeight(null));
			//			
			//									
			//									logger.info("xMin" + xMin);
			//									logger.info("clip.x" + clip.x);
			//									logger.info("clip.y" + clip.y);
			//									logger.info("clip.width" + clip.width);
			//									logger.info("clip.height" + clip.height);
			//

			// First fill background
			//long fillStartTime = System.currentTimeMillis();
			g2d.setColor(this.getBackground());
			g2d.fill(clip);
			//long fillTime = System.currentTimeMillis() - fillStartTime;
			//logger.info("fillTime" + fillTime);

			if (img != null){
				//											logger.info(img.getHeight(null));
				//											logger.info(img.getWidth(null));
				//											logger.info("clip.y=" + clip.y);
				//											logger.info("x=" + (int)(xMin * charWidth));
				//											logger.info("y=" + (int)(yMin * charHeight));

				// Mac retina screen
				if(highDPIScaleFactor > 1){

					//translate and scale with AffineTransform
					//	                AffineTransform affineTransform = new AffineTransform();
					//	                affineTransform.translate((int)(xMin * charWidth),(int)(yMin * charHeight));
					//	                affineTransform.scale(0.5, 0.5);
					//	                Graphics2D g2 = (Graphics2D) g;
					//	                g2.drawImage(img, affineTransform, null);			


					int dx1 = clip.x;
					int dx2 = dx1 + clipRGB.getScanWidth() / highDPIScaleFactor;
					int dy1 = clip.y;
					int dy2 = dy1 + clipRGB.getHeight() / highDPIScaleFactor;

					int sx1 = 0;
					int sx2 = sx1 + clipRGB.getScanWidth();
					int sy1 = 0;
					int sy2 = sy1 + clipRGB.getHeight();

					//  g.drawImage(img, clip.x, clip.y, null);

					g2d.drawImage(img, dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2, null);
					//g.drawImage(img, (int)(xMin * charWidth), (int)(yMin * charHeight), null);

					// normal
				}else{
					g2d.drawImage(img, clip.x, clip.y, null);
					//g.drawImage(img, (int)(xMin * charWidth), (int)(yMin * charHeight),null);
				}		
			}

			// Draw excludes - only if not isShowTranslationOnePos
			if(! isShowTranslationOnePos()){
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
			//			if(showTranslationOnePos){			
			//
			//				int maxX = xMax;
			//				if(maxX > alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()){
			//					maxX = alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos();
			//				}
			//
			//				int clipY = 0;
			//				for(int y = yMin; y < yMax; y = y + 1){
			//
			//					aaTransSeq.setSequence(alignment.getSequences().get(y));
			//
			//					// X Loop Start
			//					int clipX = 0;
			//					for(int x = xMin; x < maxX ; x++){
			//
			//						AminoAcid acid;
			//						if(ignoreGapInTranslation){
			//							acid = aaTransSeq.getAAinNoGapTranslatedPos(x);
			//						}else{
			//							acid = aaTransSeq.getAAinTranslatedPos(x);
			//						}
			//						copyAminoAcidPixels(clipRGB,(byte)acid.getCodeCharVal(),x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor));
			//
			//						clipX ++;
			//					}		
			//					clipY ++;
			//				}// y loop end		
			//			}else{
			// Most normal one

			int clipY = 0;			
			for(int y = yMin; y < yMax; y = y + 1){

				// X Loop Start
				int clipX = 0;
				for(int x = xMin; x < xMax ; x++){

					byte residue = alignment.getBaseAt(x,y);
					// Draw as nucleotides
					if(isNucleotideAlignment){
						// Draw as translated
						if(isShowTranslationOnePos()){

							copyAminoAcidPixels(clipRGB,residue,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor));			

						}else if(showTranslation && !isShowTranslationOnePos()){

							if(ignoreGapInTranslation){
								AminoAcidAndPosition aaAndPos = alignment.getSequences().get(y).getNoGapAminoAcidAtNucleotidePos(x);
								copyTranslatedNucleotidesPixelsSkipGap(clipRGB,residue,aaAndPos.acid,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor), aaAndPos.position);
							}
							else{
								AminoAcid aminoAcid = alignment.getTranslatedAminoAcidAtNucleotidePos(x,y);
								if(showTranslationAndNuc){
									copyTranslatedNucleotidesPixelsShowTranslationAndNuc(clipRGB,residue,aminoAcid,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor), alignment.getSequences().get(y));
								}else{
									copyTranslatedNucleotidesPixels(clipRGB,residue,aminoAcid,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor), alignment.getSequences().get(y));
								}	
							}

						}else{
							copyNucleotidePixels(clipRGB,residue,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor));					
						}
					}
					// Draw as AminoAcids
					else{
						copyAminoAcidPixels(clipRGB,residue,x,y,(int)(clipX*charWidth*highDPIScaleFactor), (int)(clipY*charHeight*highDPIScaleFactor));	
					}
					clipX ++;
				}		
				clipY ++;
			}// x loop end
			//	}// y loop end

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


			// First fill background
			//long fillStartTime = System.currentTimeMillis();
			g2d.setColor(this.getBackground());
			g2d.fill(clip);
			//			Rectangle larger = new Rectangle(clip);
			//			larger.grow(300, 300);
			//long fillTime = System.currentTimeMillis() - fillStartTime;
			//logger.info("fillTime" + fillTime);

			// Now draw the pixels
			Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));

			//						logger.info(clipRGB.getBackend().length);
			//						logger.info("img.getWidth" + img.getWidth(null));
			//						logger.info("img.getHeight" + img.getHeight(null));
			//			
			//			
			//						logger.info("xMin" + xMin);
			//						
			//						logger.info("clip.width" + clip.width);
			//						logger.info("clip.height" + clip.height);
			//



			if (img != null){
				//								logger.info(img.getHeight(null));
				//								logger.info("x=" + (int)(xMin * charWidth));
				//								logger.info("y=" + (int)(yMin * charHeight));
				//								logger.info("clip.x" + clip.x);
				//								logger.info("clip.y" + clip.y);
				//g.drawImage(img, clip.x, clip.y, clip.width, clip.height, null);
				if(highDPIScaleFactor > 1){

					//translate and scale with AffineTransform
					//	                AffineTransform affineTransform = new AffineTransform();
					//	                affineTransform.translate((int)(xMin * charWidth),(int)(yMin * charHeight));
					//	                affineTransform.scale(0.5, 0.5);
					//	                Graphics2D g2 = (Graphics2D) g;
					//	                g2.drawImage(img, affineTransform, null);			


					int dx1 = (int)(xMin * charWidth);
					int dx2 = dx1 + clipRGB.getScanWidth() / highDPIScaleFactor;
					int dy1 = (int)(yMin * charHeight);
					int dy2 = dy1 + clipRGB.getHeight() / highDPIScaleFactor;

					int sx1 = 0;
					int sx2 = sx1 + clipRGB.getScanWidth();
					int sy1 = 0;
					int sy2 = sy1 + clipRGB.getHeight();

					logger.info("dx1" + dx1);
					logger.info("dx2" + dx2);

					logger.info("sx1" + sx1);
					logger.info("sx2" + sx2);

					g2d.drawImage(img, dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2, null);
					//g.drawImage(img, (int)(xMin * charWidth), (int)(yMin * charHeight), null);

				}else{
					g2d.drawImage(img, (int)(xMin * charWidth), (int)(yMin * charHeight),null);
				}
				//
				//			    try {
				//			    	BufferedImage buffImg = ImageUtils.toBufferedImage(img);
				//					ImageIO.write(buffImg, "png", new File("/home/anders/tmp/aliviewpane.png"));
				//				} catch (IOException e) {
				//					// TODO Auto-generated catch block
				//					e.printStackTrace();
				//				}
				//			    
			}

			//			Rectangle testRect = new Rectangle(350, 200, 400, 400);		
			//			Color randomColor = new Color((int)(Math.random()*255), (int)(Math.random()*255),(int)(Math.random()*255));		
			//			g2d.setColor(randomColor);
			//			g2d.fill(testRect);




			// Draw excludes
			if(! isShowTranslationOnePos()){
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
	}
	

}

