package aliview.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
	public static final int MAX_CHARSIZE_TO_DRAW = 8;
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

	// this is mainly for testing of alternative ways to paint
	StringBuilder byteBuffer = new StringBuilder();
	private boolean highlightCons;



	public AlignmentPane() {
		//this.setDoubleBuffered(false);
		//this.setBackground(Color.white);
		//this.infoLabel = infoLabel;
		alignmentRuler = new AlignmentRuler(this);
		createAdjustedDerivedBaseFont();
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

	public void decCharSize(){

		// stop when everything is in view (or char is 1 for smaller alignments)
		Dimension prefSize = getPreferredSize();
		if((prefSize.width >= this.getSize().width || prefSize.height >= this.getSize().height) || charWidth > 1){

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

			logFontMetrics();

			this.validateSize();
			this.repaint();
		}
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
		logFontMetrics();
		this.validateSize();
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
		// font and render context
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


	/*
	 *  Mainly for performance test-developing
	 */
	private long endTime;
	private int drawCounter = 0;
	private int DRAWCOUNT_LOF_INTERVAL = 1;

	public void repaintForceRuler(){
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
			logger.info("Time from last endTim " + (startTime - endTime) + " milliseconds");
		}

		//	logger.info("paintClipBounds" + g.getClipBounds());

		Graphics2D g2d = (Graphics2D) g;

		// This need to be off because I use exact font width in createAdjustedDerivedBaseFont
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		//		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
		//						RenderingHints.VALUE_RENDER_QUALITY);
		//	g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
		//			RenderingHints.VALUE_DITHER_DISABLE);		
		//		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		//				RenderingHints.VALUE_COLOR_RENDER_SPEED);	
		//				g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		//						RenderingHints.VALUE_COLOR_RENDER_QUALITY);



		// Font
		g2d.setFont(baseFont);

		// to get some space left and bottom around characters
		int charCenterXOffset = (int) (0.15 * charWidth);
		int charCenterYOffset = (int) (0.2 * charHeight);

		// What part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle clip = g2d.getClipBounds();
		Rectangle matrixClip = paneCoordToMatrixCoord(clip);

		int xMin = matrixClip.x - 1;
		int yMin = matrixClip.y - 1;
		int xMax = (int) matrixClip.getMaxX() + 1;
		int yMax = (int) matrixClip.getMaxY() + 1;

		// adjust for part of matrix that exists
		xMin = Math.max(0, xMin);
		yMin = Math.max(0, yMin);
		xMax = Math.min(alignment.getMaxX(), xMax);
		yMax = Math.min(alignment.getMaxY(), yMax);
		//		int drawWidth = xMax - xMin;
		//		int drawHeight = yMax - yMin;

		// Draw bg (gap-background - makes us save some drawing operations)
		if(alignment.isAAAlignment()){
			g2d.setColor(colorSchemeAminoAcid.getBaseBackgroundColor(NucleotideUtilities.GAP));
		}else{
			g2d.setColor(colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP));
		}
		g2d.fillRect(clip.x,clip.y,clip.width,clip.height);

		// left and right of viewport so scrolling always looks good
		int EXTRA_POSITIONS = 1;

		// If it is to be translated
		AATranslator aaTransSeq = null;
		if(showTranslation || showTranslationOnePos){
			aaTransSeq = new AATranslator(alignment.getAlignentMeta().getCodonPositions(), alignment.getGeneticCode());
			EXTRA_POSITIONS = 3; // one full codon
		}

		//		logger.info("matrixClip.getMaxX()" + matrixClip.getMaxX());
		//		logger.info("matrixClip.getMaxY()" + matrixClip.getMaxY());
		//		logger.info("clip.getMaxX()" + clip.getMaxX());
		//		logger.info("clip.getMaxY()" + clip.getMaxY());
		//		logger.info("xMax" + xMax);
		//		logger.info("yMax" + yMax);	
		//		logger.info("charWidth" + charWidth);


		// small chars have their own loop here
		if(charWidth < 1){

			if(showTranslationOnePos || showTranslation){
				for(int y = clip.y; y < clip.getMaxY(); y++){			
					int ySeq =(int)((double)y * (1/(double)charWidth));

					if(ySeq < alignment.getMaxY()){	
						aaTransSeq.setSequence(alignment.getSequences().get(ySeq));

						double maxX = clip.getMaxX();
						if(maxX > alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()){
							maxX = alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos();
						}

						// X Loop Start
						drawCharBuffer.clear();
						for(int x = clip.x; x < maxX ; x++){
							int xPos =(int)((double)x * (1/(double)charWidth));

							if(alignment.isPositionValid(xPos, ySeq)){
								byte base = alignment.getBaseAt(xPos,ySeq);
								AminoAcid acid = aaTransSeq.getAAinTranslatedPos(x);
								drawAminoAcids(g2d,acid.getCodeByteVal(),x,y, x, y, 1, 1, charCenterXOffset, charCenterYOffset);	
							}
						}
						// Now paint the buffered colors
						HashSet<Color> unique = new CharBufferCreator(drawCharBuffer).getUniqueBGColors();
						for(Color bgColor: unique){
							int endPos = 0;
							int MAX_LEN = 1000;
							while(endPos < drawCharBuffer.length()){
								int startPos = endPos;
								//int drawCount = drawCharBuffer.getNextSameBGColorCount(startPos, MAX_LEN);
								int drawCount = 1;
								if(drawCharBuffer.getBgColor(startPos) == bgColor){
									drawNucleotideBackground(g2d, startPos + (int)clip.getMinX(), y, drawCount, 1, drawCharBuffer.getBgColor(startPos));
								}
								endPos = startPos + drawCount;
							}
						}
						// No painting chars because they are to small
						
						
					}
				}

			}else{
				
				// Always start at closest even 10
				double startY = clip.y;
				startY = Math.floor(startY/100) * 100;
				for(int y = (int)startY; y < clip.getMaxY(); y++){					
					
					int ySeq =(int)((double)y * (1/(double)charWidth));

					if(ySeq < alignment.getMaxY()){	
						//	aaTransSeq.setSequence(alignment.getSequences().get(ySeq));

						// X Loop Start
						drawCharBuffer.clear();
						for(int x = clip.x; x < clip.getMaxX() ; x++){
							int xPos =(int)((double)x * (1/(double)charWidth));
							if(alignment.isPositionValid(xPos, ySeq)){
								byte base = alignment.getBaseAt(xPos,ySeq);
								
								// Draw as Nucleotides
								if(alignment.isNucleotideAlignment()){
									drawNucleotides(g2d,base,xPos,ySeq,x,y,1,1,0, 0);
								}
								// Draw as AA
								else{
									drawAminoAcids(g2d,base,xPos,ySeq,x,y,1,1, 0, 0);
								}
							}
						}
						
						
						
						
						// Now paint the buffered colors
						HashSet<Color> unique = new CharBufferCreator(drawCharBuffer).getUniqueBGColors();
						//Color bgColor = colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.T);
						for(Color bgColor: unique){
							int endPos = 0;
							int MAX_LEN = 1000;
							while(endPos < drawCharBuffer.length()){
								int startPos = endPos;
								//int drawCount = drawCharBuffer.getNextSameBGColorCount(startPos, MAX_LEN);
								int drawCount = 1;
								if(drawCharBuffer.getBgColor(startPos) == bgColor){
									drawNucleotideBackground(g2d, startPos + (int)clip.getMinX(), y, drawCount, 1, drawCharBuffer.getBgColor(startPos));
								}
								endPos = startPos + drawCount;
							}
						}
						// No painting chars because they are to small
					}
				}
			}
			
			// Draw excludes
			// calculaate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
			int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);
			//for(int x = xMin; x < xMax ; x++){
			for(int x = clip.x; x < clip.getMaxX() ; x++){
				int xPos =(int)((double)x * (1/(double)charWidth));
				if(alignment.isExcluded(xPos) == true){
					g2d.setColor(ColorScheme.GREY_TRANSPARENT);
					g2d.fillRect(x, this.getVisibleRect().y, 1, drawExcludesHeight);
					logger.info("drawExclude");
				}
			}		
		}	
		// Normal char width
		else{
			if(showTranslationOnePos){
				for(int y = yMin; y < yMax ; y = y + 1){
					aaTransSeq.setSequence(alignment.getSequences().get(y));

					// Draw one extra codon at start and end
					int minX = (int)(matrixClip.getMinX()-EXTRA_POSITIONS);
					if(minX < 0){
						minX=0;
					}
					int maxX = (int)(matrixClip.getMaxX()+EXTRA_POSITIONS);

					if(maxX > alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()){
						maxX = alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos();
					}

					// X Loop Start
					drawCharBuffer.clear();
					int yPaneCoord = (int)(y * charHeight);
					int widthInPaneCoord = (int)charWidth * 1;
					int heightInPaneCoord = (int)charHeight;
					for(int x = minX; x < maxX ; x++){
						int xPaneCoord = (int)(x * charWidth);
						AminoAcid acid = aaTransSeq.getAAinTranslatedPos(x);
						drawAminoAcids(g2d,acid.getCodeByteVal(),x,y, xPaneCoord, yPaneCoord, widthInPaneCoord, heightInPaneCoord, charCenterXOffset, charCenterYOffset);
					}
					// Now paint the buffered background colors
					HashSet<Color> unique = new CharBufferCreator(drawCharBuffer).getUniqueBGColors();
					int xPaneStartCoord = (int)(minX * charWidth);
					for(Color bgColor: unique){
						int endPos = 0;
						int MAX_LEN = 1000;
						while(endPos < drawCharBuffer.length()){
							int startPos = endPos;
							//int drawCount = drawCharBuffer.getNextSameBGColorCount(startPos, MAX_LEN);
							int drawCount = 1;
							if(drawCharBuffer.getBgColor(startPos) == bgColor){
								drawNucleotideBackground(g2d,xPaneStartCoord + (startPos * widthInPaneCoord), yPaneCoord, widthInPaneCoord * drawCount, heightInPaneCoord, drawCharBuffer.getBgColor(startPos));
							}
							endPos = startPos + drawCount;
						}
					}	
					// Now draw the buffered chars
					if(charHeight > MAX_CHARSIZE_TO_DRAW){
						charCenterXOffset = (int)(0.2*charWidth);		
						CharBufferCreator cbCreator = new CharBufferCreator(drawCharBuffer);
						ArrayList<DrawCharBuffer> buffs = cbCreator.createFGBuffers();
						//logger.info(buffs.size());
						for(DrawCharBuffer charBuff: buffs){
							int endPos = 0;
							int MAX_LEN = 100;
							while(endPos < charBuff.length()){
								int startPos = endPos;
								int drawCount = charBuff.getNextSameFGColorCount(startPos, MAX_LEN);
								drawCharsBuffered(g2d, charBuff.byteBuffer, startPos, drawCount, xMin + startPos, y, charBuff.getFgColor(startPos), charCenterXOffset, charCenterYOffset);			
								endPos = startPos + drawCount;
							}	
						}
					}
				}
			}else{
				for(int y = yMin; y < yMax ; y = y + 1){

					if(showTranslation){
						aaTransSeq.setSequence(alignment.getSequences().get(y));
					}

					// Draw one extra codon at start and end
					int minX = (int)(matrixClip.getMinX()-EXTRA_POSITIONS);
					if(minX < 0){
						minX=0;
					}
					int maxX = (int)(matrixClip.getMaxX()+EXTRA_POSITIONS);
					if(maxX > alignment.getLengthAt(y)){
						maxX = alignment.getLengthAt(y);
					}

					
					drawCharBuffer.clear();
					int widthInPaneCoord = (int)charWidth;
					int heightInPaneCoord = (int)charHeight;
					int yPaneCoord = (int)(y * charHeight);
					// X Loop Start
					for(int x = minX; x < maxX ; x++){
						int xPaneCoord = (int)(x * charWidth);
						
						// Draw as nucleotides
						if(alignment.isNucleotideAlignment()){
							byte base = alignment.getBaseAt(x,y);

							// Draw as translated
							if(showTranslation){
//								if(AliView.isDebugMode()){
//									drawTranslatedNucleotidesSkipGap(g2d, base,x,y,xPaneCoord,yPaneCoord, widthInPaneCoord, heightInPaneCoord, aaTransSeq, charCenterXOffset, charCenterYOffset);
//								}
//								else{
									drawTranslatedNucleotides(g2d,base,x,y,xPaneCoord,yPaneCoord, widthInPaneCoord, heightInPaneCoord, aaTransSeq, charCenterXOffset, charCenterYOffset);
//								}
								// TODO skall även rita ut chars

							}else{
								drawNucleotides(g2d, base,x,y,xPaneCoord,yPaneCoord, widthInPaneCoord, heightInPaneCoord, charCenterXOffset, charCenterYOffset);					
							}
	
						}
						// Draw as AminoAcids
						else{
							byte charAsByte = alignment.getBaseAt(x,y);
							drawAminoAcids(g2d,charAsByte,x,y, xPaneCoord, yPaneCoord, widthInPaneCoord, heightInPaneCoord, charCenterXOffset, charCenterYOffset);	
						}
					}		
					// Now paint the buffered background colors
					HashSet<Color> unique = new CharBufferCreator(drawCharBuffer).getUniqueBGColors();
					int xPaneStartCoord = (int)(minX * charWidth);
					for(Color bgColor: unique){
						int endPos = 0;
						int MAX_LEN = 1000;
						while(endPos < drawCharBuffer.length()){
							int startPos = endPos;
							//int drawCount = drawCharBuffer.getNextSameBGColorCount(startPos, MAX_LEN);
							int drawCount = 1;
							if(drawCharBuffer.getBgColor(startPos) == bgColor){
								drawNucleotideBackground(g2d,xPaneStartCoord + (startPos * widthInPaneCoord), yPaneCoord, widthInPaneCoord * drawCount, heightInPaneCoord, drawCharBuffer.getBgColor(startPos));
							}
							endPos = startPos + drawCount;
						}
					}	
					// Now draw buffer of chars
					// Draw all chars at end from buffer(this is a lot faster than drawing them one at a time once)		
					if(charHeight > MAX_CHARSIZE_TO_DRAW){
						charCenterXOffset = (int)(0.2*charWidth);
						CharBufferCreator cbCreator = new CharBufferCreator(drawCharBuffer);
						ArrayList<DrawCharBuffer> buffs = cbCreator.createFGBuffers();
						//logger.info(buffs.size());
						for(DrawCharBuffer charBuff: buffs){
							int endPos = 0;
							int MAX_LEN = 100;
							while(endPos < charBuff.length()){
								int startPos = endPos;
								int drawCount = charBuff.getNextSameFGColorCount(startPos, MAX_LEN);
								drawCharsBuffered(g2d, charBuff.byteBuffer, startPos, drawCount, xMin + startPos, y, charBuff.getFgColor(startPos), charCenterXOffset, charCenterYOffset);			
								endPos = startPos + drawCount;
							}	
						}
					}
				}// x loop end
			}// y loop end

			// Draw excludes
			// calculaate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
			int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);
			for(int x = xMin; x < xMax ; x++){
				if(alignment.isExcluded(x) == true){
					g2d.setColor(ColorScheme.GREY_TRANSPARENT);
					g2d.fillRect((int)(x * charWidth), this.getVisibleRect().y, (int)charWidth, drawExcludesHeight);				
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

	byte[] byteToDraw = new byte[1];
	//ByteBuffer bytesToDraw = new ByteBuffer(5000);
	DrawCharBuffer drawCharBuffer = new DrawCharBuffer(5000);

	private void drawAminoAcids(Graphics2D g2d, byte charAsByte, int x, int y,
			int xPaneCoord, int yPaneCoord, int widthInPaneCoord, int heightInPaneCoord, int charCenterXOffset, int charCenterYOffset){

		// Set default
		AminoAcid acid = AminoAcid.getAminoAcidFromByte(charAsByte);

		Color baseForegroundColor = colorSchemeAminoAcid.getAminoAcidForgroundColor(acid, x, alignment);
		Color baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidBackgroundColor(acid, x, alignment);

		// get char to draw
		byte byteToDraw = charAsByte;

		// adjustment if only diff to be shown
		if(highlightDiffTrace){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(y != differenceTraceSequencePosition && acid == AminoAcid.getAminoAcidFromByte(alignment.getBaseAt(x,differenceTraceSequencePosition))){
				byteToDraw = '.';
				baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidBackgroundColor(AminoAcid.GAP, x, alignment);
			}
		}

		// adjustment if non-cons to be highlighted
		if(highlightNonCons){
			if(acid == AminoAcid.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(alignment.getHistogram().isMajorityRuleConsensus(x,acid.intVal)){
				baseBackgroundColor = colorSchemeAminoAcid.getAminiAcidConsensusBackgroundColor();
				//baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidBackgroundColor(AminoAcid.GAP);
			}
			
//			// first set all gaps with one color
//			if(acid){
//				baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidBackgroundColor(AminoAcid.GAP);
//			}
//			else if(alignment.getHistogram().isMajorityRuleConsensus(x,acid.intVal)){
//				baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidBackgroundColor(AminoAcid.GAP);
//			}
		}
		// adjustment if cons to be highlighted
		if(highlightCons){
			if(acid == AminoAcid.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! alignment.getHistogram().isMajorityRuleConsensus(x,acid.intVal)){
				baseBackgroundColor = colorSchemeAminoAcid.getAminiAcidConsensusBackgroundColor();
				//baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidBackgroundColor(AminoAcid.GAP);
			}
		}

		// Draw
//		if(baseBackgroundColor == colorSchemeAminoAcid.getAminoAcidBackgroundColor(AminoAcid.GAP, x, alignment)){
//			// dont draw if gap or anything with gap-color (already bg-color is drawn on whole pane)
//			// this is to improve performance
//		}else{
//			drawNucleotideBackground(g2d, xPaneCoord, yPaneCoord, widthInPaneCoord, heightInPaneCoord, nSame, baseBackgroundColor);
//		}

		// Temp Selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() &&
					y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}

		// draw selection and temp selection
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){		
			baseBackgroundColor = colorSchemeAminoAcid.getAminoAcidSelectionBackgroundColor(acid, x, alignment);
			baseForegroundColor = colorSchemeAminoAcid.getAminoAcidSelectionForegroundColor(acid, x, alignment);

//			drawNucleotideBackground(g2d, xPaneCoord, yPaneCoord, widthInPaneCoord, heightInPaneCoord, 1, baseBackgroundColor);
		}
		
		// Put char letter in buffer
		if(charHeight > MAX_CHARSIZE_TO_DRAW && (! drawAminoAcidCode || !showTranslation)){
			drawCharBuffer.append(byteToDraw, baseForegroundColor, baseBackgroundColor);
		}else{
			drawCharBuffer.append((byte)' ', baseForegroundColor, baseBackgroundColor);
		}
		
	}

	private void drawTranslatedNucleotides(Graphics2D g2d, byte base, int x, int y, int xPaneCoord, int yPaneCoord, int width, int height, AATranslator aaTransSeq,
			int charCenterXOffset, int charCenterYOffset){

		int baseVal = NucleotideUtilities.baseValFromBase(base);

		byte[] byteToDraw = new byte[]{base};
		
		// Set default
		Color baseForegroundColor = colorSchemeNucleotide.getBaseForegroundColor(baseVal);

		if(aaTransSeq.isFullCodonStartingAt(x)){	
			byte[] codon = aaTransSeq.getTripletAt(x);
			AminoAcid acid = AminoAcid.getAminoAcidFromCodon(codon);
			Color backgroundColor = colorSchemeNucleotide.getAminoAcidBackgroundColor(acid);
			drawTranslatedTripletAminoAcidBackground(g2d,x,y,backgroundColor,codon);					
			if(drawAminoAcidCode){
				drawAminoAcidCode(g2d, x, y, acid, Color.WHITE, charCenterXOffset, charCenterYOffset);
			}	
		}

		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}

		// adjust colors if selected and temp selection
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			AminoAcid acid = aaTransSeq.getAminoAcidAtNucleotidePos(x);
			Color backgroundColor = colorSchemeNucleotide.getAminoAcidSelectionBackgroundColor(acid);
			drawNucleotideBackground(g2d, xPaneCoord, yPaneCoord, width, height, backgroundColor);
		}

		// Draw char letter
		if(! drawAminoAcidCode){
			drawNucleotideLetter(g2d, byteToDraw, x, y, baseForegroundColor, charCenterXOffset, charCenterYOffset);
		}

	}
	
	private void drawTranslatedNucleotidesSkipGap(Graphics2D g2d, byte base, int x, int y, int xPaneCoord, int yPaneCoord, int width, int height, AATranslator aaTransSeq,
			int charCenterXOffset, int charCenterYOffset){

		int baseVal = NucleotideUtilities.baseValFromBase(base);

		byte[] byteToDraw = new byte[]{base};
		
		// Set default
		Color baseForegroundColor = colorSchemeNucleotide.getBaseForegroundColor(baseVal);

		AminoAcid acid = aaTransSeq.getNoGapAminoAcidAtNucleotidePos(x);
		Color backgroundColor = colorSchemeNucleotide.getAminoAcidBackgroundColor(acid);
		// Draw background color
		drawNucleotideBackground(g2d, xPaneCoord, yPaneCoord, width, height, backgroundColor);	
		
		if(drawAminoAcidCode){
			//drawAminoAcidCode(g2d, x, y, acid, Color.WHITE, charCenterXOffset, charCenterYOffset);
			drawAminoAcidCodeSimple(g2d, x, y, acid, Color.WHITE, charCenterXOffset, charCenterYOffset);
		}

		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}

		// adjust colors if selected and temp selection
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			backgroundColor = colorSchemeNucleotide.getAminoAcidSelectionBackgroundColor(acid);
			drawNucleotideBackground(g2d, xPaneCoord, yPaneCoord, width, height, backgroundColor);
		}

		// Draw char letter
		if(! drawAminoAcidCode){
			drawNucleotideLetter(g2d, byteToDraw, x, y, baseForegroundColor, charCenterXOffset, charCenterYOffset);
		}

	}


	private void drawNucleotides(Graphics2D g2d, byte base, int x, int y, int xPaneCoord, int yPaneCoord, int width, int height,
			int charCenterXOffset, int charCenterYOffset){

		int baseVal = NucleotideUtilities.baseValFromBase(base);

		// Set default
		Color baseForegroundColor = colorSchemeNucleotide.getBaseForegroundColor(baseVal);
		Color baseBackgroundColor = colorSchemeNucleotide.getBaseBackgroundColor(baseVal);

		// get char to draw
		// TODO remove this hack (file-sequences are including \n and they are removed when apppending to stringbuilder)
		if(base == '\n'){
			base = ' ';
		}
		byte byteToDraw = base;

		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(tempSelectionRect != null){
			if(x <= tempSelectionRect.getMaxX() && x >= tempSelectionRect.getMinX() && y <= tempSelectionRect.getMaxY() && y >= tempSelectionRect.getMinY()){
				isPointWithinSelectionRect = true;
			}
		}

		// adjust colors if selected and temp selection
		if(alignment.isBaseSelected(x,y) || (tempSelectionRect != null && isPointWithinSelectionRect)){
			baseBackgroundColor = colorSchemeNucleotide.getBaseSelectionBackgroundColor(baseVal);
			baseForegroundColor = colorSchemeNucleotide.getBaseSelectionForegroundColor(baseVal);
		}

		// adjustment if only diff to be shown
		if(highlightDiffTrace){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(y != differenceTraceSequencePosition){
				if(NucleotideUtilities.baseValFromBase(base) == NucleotideUtilities.baseValFromBase(alignment.getBaseAt(x,differenceTraceSequencePosition))){
					byteToDraw = '.';
					//logger.info("bytetodraw");
					baseBackgroundColor = colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP);
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
				baseBackgroundColor = colorSchemeNucleotide.getBaseConsensusBackgroundColor();
				//baseBackgroundColor = colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.UNKNOWN);
			}
		}
		if(highlightCons){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! nucHistogram.isMajorityRuleConsensus(x,baseVal)){
				baseBackgroundColor = colorSchemeNucleotide.getBaseConsensusBackgroundColor();
				//baseBackgroundColor = colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.UNKNOWN);
			}
		}

		// Draw background
		if(baseBackgroundColor == colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP)){
			// dont draw if gap or anything with gap-color (already bg-color is drawn on whole pane)
			// this is to improve performance
		}else{
		//		drawNucleotideBackground(g2d, xPaneCoord, yPaneCoord, width, height, 1, baseBackgroundColor);
		}

		// Put char letter in buffer
		if(charHeight > MAX_CHARSIZE_TO_DRAW && (! drawAminoAcidCode || !showTranslation)){
			drawCharBuffer.append(byteToDraw, baseForegroundColor, baseBackgroundColor);
		}else{
			drawCharBuffer.append((byte)' ', baseForegroundColor, baseBackgroundColor);
		}
	}

	private void drawCharsBuffered(Graphics2D g2d, byte[] bytesToDraw, int startPos, int drawCount, int x, int y, Color baseForegroundColor, int charCenterXOffset, int charCenterYOffset) {	
		g2d.setColor(baseForegroundColor);
		if(charHeight > MAX_CHARSIZE_TO_DRAW){
			g2d.drawBytes(bytesToDraw, startPos, drawCount, (int)((x + 0) * charWidth + charCenterXOffset), (int)(y * charHeight + charHeight - charCenterYOffset));
		}
	}

	private void drawNucleotideLetter(Graphics2D g2d, byte[] bytesToDraw, int x, int y, Color baseForegroundColor, int charCenterXOffset, int charCenterYOffset) {
		g2d.setColor(baseForegroundColor);
		if(charHeight > MAX_CHARSIZE_TO_DRAW){
			g2d.drawBytes(bytesToDraw, 0, bytesToDraw.length, (int)((x + 0) * charWidth + charCenterXOffset), (int)(y * charHeight + charHeight - charCenterYOffset));
		}
	}

	private void drawNucleotideBackground(Graphics2D g2d, int xPaneCoord, int yPaneCoord, int width, int height, Color baseBackgroundColor){
		g2d.setColor(baseBackgroundColor);
		g2d.fillRect(xPaneCoord,yPaneCoord,width,height);
	}

	private void drawAminoAcidCode(Graphics2D g2d, int xAlignmentCoord, int yAlignmentCoord, AminoAcid acid, Color foregroundColor, int charCenterXOffset, int charCenterYOffset) {
		g2d.setColor(foregroundColor);
		if(charHeight > MAX_CHARSIZE_TO_DRAW){
			g2d.drawBytes(acid.getCodeByteArray(), 0, 1, (int)(xAlignmentCoord * charWidth + charCenterXOffset + charWidth), (int)(yAlignmentCoord * charHeight + charHeight - charCenterYOffset));
		}
	}
	
	private void drawAminoAcidCodeSimple(Graphics2D g2d, int xAlignmentCoord, int yAlignmentCoord, AminoAcid acid, Color foregroundColor, int charCenterXOffset, int charCenterYOffset) {
		g2d.setColor(foregroundColor);
		if(charHeight > MAX_CHARSIZE_TO_DRAW){
			g2d.drawBytes(acid.getCodeByteArray(), 0, 1, (int)(xAlignmentCoord * charWidth + charCenterXOffset), (int)(yAlignmentCoord * charHeight + charHeight - charCenterYOffset));
		}
	}

	private void drawTranslatedTripletAminoAcidBackground(Graphics2D g2d, int xAlignmentCoord, int yAlignmentCoord, Color aminoAcidBackgroundColor, byte[] codon) {
		// draw background
		g2d.setColor(aminoAcidBackgroundColor);
		g2d.fillRect((int)(xAlignmentCoord * charWidth), (int)(yAlignmentCoord * charHeight), (int)charWidth * codon.length, (int)charHeight);	
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
		if(showTranslationOnePos){
			return new Dimension((int) (charWidth * alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()), (int)(charHeight * alignment.getSize()));
		}else{
			return new Dimension((int) (charWidth * alignment.getMaximumSequenceLength()), (int)(charHeight * alignment.getSize()));
		}
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
			for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x ++){


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
			if(charHeight < 0.02){
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
			for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x ++){

				if(x % drawEveryNpos == 0){
					String number = Integer.toString(x);
					int stringSizeOffset = (int)((number.length() * rulerCharWidth) / 2);
					int xPos = (int)(pos * charWidth - stringSizeOffset - offsetDueToScrollPanePosition);
					g2d.drawString(number, xPos, 10);
					//	g2d.drawBytes(number.getBytes(), 0,number.getBytes().length,xPos, 10);
				}
				pos ++;
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

	public void setColorSchemeAminoAcid(ColorScheme aScheme) {
		this.colorSchemeAminoAcid = aScheme;
	}

	public void setColorSchemeNucleotide(ColorScheme aScheme) {
		this.colorSchemeNucleotide = aScheme;
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



	//	public void selectSequences(ArrayList<Sequence> selectedSequences) {
	//		preserveBaseSelection = false;
	//
	//	}

}

