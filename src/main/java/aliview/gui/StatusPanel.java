package aliview.gui;

import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box.Filler;
import javax.swing.event.ListDataEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.alignment.Alignment;
import aliview.alignment.AlignmentEvent;
import aliview.alignment.AlignmentListener;
import aliview.sequencelist.AlignmentDataEvent;
import aliview.sequencelist.AlignmentDataListener;
import aliview.sequencelist.AlignmentSelectionEvent;
import aliview.sequencelist.AlignmentSelectionListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

import java.awt.Component;

import javax.swing.Box;

public class StatusPanel extends JPanel implements AlignmentListener, AlignmentDataListener, AlignmentSelectionListener{
	private static final Logger logger = Logger.getLogger(StatusPanel.class);
	private Alignment alignment;
	private AlignmentPane aliPane;
	private Point pointerPos;
	private JLabel lblInfo;
	private JLabel lblTxtPos;
	private long selectionSize;
	private int posInSeq;
	private int posInUngapedSeq;
	private int selectedColumnCount;
	private int selectedSeqCount;
	private String firstSelectedSequenceName = "";
	JLabel lblTotalSelectedChars;
	JLabel lblCols;
	JLabel lblSelectedSeqCount;
	JLabel lblPosUngaped;
	JLabel lblPos;
	JLabel lblSelectedName;
	JLabel lblAlignmentInfo;
	/**
	 * @wbp.nonvisual location=929,-31
	 */
	private final JLabel label = new JLabel("New label");
	private JLabel lblNewLabel;
	private JSeparator separator;
	private Component horizontalGlue;
	private Component horizontalGlue_1;
	
	public StatusPanel(AlignmentPane alignmentPane, Alignment alignment) {
		this.aliPane = alignmentPane;
		this.alignment = alignment;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		//this.setBackground(Color.red);
		
		lblInfo = new JLabel();
		
		lblInfo.setText("  Selected: ");
		this.add(lblInfo);
		lblInfo.setPreferredSize(lblInfo.getPreferredSize());
		lblInfo.setMinimumSize(lblInfo.getPreferredSize());
		
		lblSelectedName = new JLabel("Woodsia macrolaena and more...");
		lblSelectedName.setHorizontalAlignment(SwingConstants.LEADING);
		//lblSelectedName.setPreferredSize(lblSelectedName.getPreferredSize());
	    lblSelectedName.setPreferredSize(new Dimension(Short.MAX_VALUE,0));
	    lblSelectedName.setMinimumSize(new Dimension(0,0));
		add(lblSelectedName);
		lblSelectedName.setText("");

		//add(Box.createHorizontalGlue());
	//	add(new Box.Filler(new Dimension(0,0), new Dimension(Short.MAX_VALUE,0), new Dimension(Short.MAX_VALUE, 0)));
		
		add(createStatusPanelSeparator());
		
		lblTxtPos = new JLabel("Pos:");
	    add(lblTxtPos);
	    lblTxtPos.setPreferredSize(lblTxtPos.getPreferredSize());
		
	    lblPos = new JLabel("3009909");
		add(lblPos);
		lblPos.setHorizontalAlignment(SwingConstants.CENTER);
		lblPos.setPreferredSize(lblPos.getPreferredSize());
		lblPos.setMinimumSize(lblPos.getPreferredSize());
		lblPos.setText("");
		
		add(createStatusPanelSeparator());
		
		JLabel lblNewLabel_3 = new JLabel("Pos (ungaped):");
		add(lblNewLabel_3);
		lblNewLabel_3.setPreferredSize(lblNewLabel_3.getPreferredSize());
		
		lblPosUngaped = new JLabel("2776569");
		add(lblPosUngaped);
		lblPosUngaped.setHorizontalAlignment(SwingConstants.CENTER);
		lblPosUngaped.setPreferredSize(lblPosUngaped.getPreferredSize());
		lblPosUngaped.setMinimumSize(lblPosUngaped.getPreferredSize());
		//lblPosUngaped.setMaximumSize(lblPosUngaped.getPreferredSize());
		lblPosUngaped.setText("");
		
		add(createStatusPanelSeparator());
		
		JLabel lblNewLabel_5 = new JLabel("Selected seqs:");
		add(lblNewLabel_5);
		lblNewLabel_5.setPreferredSize(lblNewLabel_5.getPreferredSize());
		
		lblSelectedSeqCount = new JLabel("39879");
		add(lblSelectedSeqCount);
		lblSelectedSeqCount.setHorizontalAlignment(SwingConstants.CENTER);
		lblSelectedSeqCount.setPreferredSize(lblSelectedSeqCount.getPreferredSize());
		lblSelectedSeqCount.setMinimumSize(lblSelectedSeqCount.getPreferredSize());
		//lblSelectedSeqCount.setMaximumSize(lblSelectedSeqCount.getPreferredSize());
		lblSelectedSeqCount.setText("");
		
		add(createStatusPanelSeparator());
		
		JLabel lblNewLabel_1 = new JLabel("Cols:");
		lblNewLabel_1.setPreferredSize(lblNewLabel_1.getPreferredSize());
		lblNewLabel_1.setMinimumSize(lblNewLabel_1.getPreferredSize());
		lblNewLabel_1.setMaximumSize(lblNewLabel_1.getPreferredSize());
		add(lblNewLabel_1);
		
		lblCols = new JLabel("1087990");
		add(lblCols);
		lblCols.setHorizontalAlignment(SwingConstants.CENTER);
		lblCols.setPreferredSize(lblCols.getPreferredSize());
		lblCols.setMinimumSize(lblCols.getPreferredSize());
		//lblCols.setMaximumSize(lblCols.getPreferredSize());
		lblCols.setText("");
		
		add(createStatusPanelSeparator());

		JLabel lblNewLabel_8 = new JLabel("Total selected chars:");
		add(lblNewLabel_8);
		lblNewLabel_8.setPreferredSize(lblNewLabel_8.getPreferredSize());
		lblNewLabel_8.setMaximumSize(lblNewLabel_8.getPreferredSize());
		lblNewLabel_8.setMinimumSize(lblNewLabel_8.getPreferredSize());
		lblNewLabel_8.setOpaque(true);
		
		lblTotalSelectedChars = new JLabel("82099029");
		add(lblTotalSelectedChars);
		lblTotalSelectedChars.setHorizontalAlignment(SwingConstants.CENTER);
		lblTotalSelectedChars.setPreferredSize(lblTotalSelectedChars.getPreferredSize());
		//lblTotalSelectedChars.setMaximumSize(lblTotalSelectedChars.getPreferredSize());
		lblTotalSelectedChars.setMinimumSize(lblTotalSelectedChars.getPreferredSize());
		lblTotalSelectedChars.setText("");
		
		add(createStatusPanelSeparator());
		
		JLabel lblNewLabel_10 = new JLabel("Alignment:");
		add(lblNewLabel_10);
		lblNewLabel_10.setPreferredSize(lblNewLabel_10.getPreferredSize());
		//lblNewLabel_10.setMaximumSize(lblNewLabel_10.getPreferredSize());
		lblNewLabel_10.setMinimumSize(lblNewLabel_10.getPreferredSize());
		
		logger.info(getPreferredSize());
		
		lblAlignmentInfo = new JLabel("_133009 sequences 5509309pos.__");
		add(lblAlignmentInfo);
		lblAlignmentInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblAlignmentInfo.setPreferredSize(lblAlignmentInfo.getPreferredSize());
		lblAlignmentInfo.setMaximumSize(lblAlignmentInfo.getPreferredSize());
		lblAlignmentInfo.setMinimumSize(lblAlignmentInfo.getPreferredSize());
		lblAlignmentInfo.setText("");

		logger.info(getPreferredSize());
		
		this.updateAll();
	}


		private Component createStatusPanelSeparator() {
			JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
			sep.setPreferredSize(new Dimension(7,18));
			sep.setMaximumSize(sep.getPreferredSize());
			sep.setMinimumSize(sep.getPreferredSize());
			return sep;
	}


		public void updateAll() {
			updateAlignmentText();
			updateSelectionText();	
		}

		public void updateSelectionText(){
			if(alignment != null && selectionSize > 0){	
				
				String firstSelSeqName = StringUtils.substring(firstSelectedSequenceName, 0, 40);			
				if(firstSelSeqName != null){
					//if(firstSelSeqName.length() == 40 || selectedSeqCount > 1){
						firstSelSeqName +="...";
					//}
				}
			
				
				String selSize = "" + selectionSize;
				String ungapPos = "" + posInUngapedSeq;
				String posInSeqTxt = "" + (posInSeq + 1); // +1 because internally we are working with 0 as first pos and first sequence
				if(selectionSize == 0){
					ungapPos = "";
					posInSeqTxt = "";
				}
					
				lblSelectedName.setText(firstSelSeqName);
				lblPos.setText(posInSeqTxt);
				lblPosUngaped.setText(ungapPos);
				lblSelectedSeqCount.setText("" + selectedSeqCount);
				lblCols.setText("" + selectedColumnCount);
				lblTotalSelectedChars.setText("" + selectionSize);
				
			}else{
				lblSelectedName.setText("");
				lblPos.setText("");
				lblPosUngaped.setText("");
				lblSelectedSeqCount.setText("");
				lblCols.setText("");
				lblTotalSelectedChars.setText("");
			}
		}
		
		
		public void updateAlignmentText(){
			if(alignment == null){
				lblAlignmentInfo.setText("no alignmnet loaded");
			}else{
				String seqCount = StringUtils.leftPad("" + alignment.getMaxY(), 6);
				String width = StringUtils.leftPad("" + alignment.getMaxX(), 6);
				
				lblAlignmentInfo.setText("" + seqCount + " sequences " + width + " pos.  ");
			}
		}

		public void setAlignment(Alignment alignment) {
			this.alignment = alignment;
			
		}
		
		public void setPointerPos(Point pointerPos) {
			this.pointerPos = pointerPos;
			try {
				posInSeq = aliPane.getPositionInSequenceAt(pointerPos);
				posInUngapedSeq = aliPane.getUngapedPositionInSequenceAt(pointerPos);		
			} catch (InvalidAlignmentPositionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateSelectionText();
		}

		public void selectionChanged(AlignmentSelectionEvent e) {
			
			logger.info("selectionChanged");
			
			this.selectionSize = alignment.getSelectionSize();
			this.selectedColumnCount = alignment.getSelectedColumnCount();
			this.selectedSeqCount = alignment.getSelectedSequencesCount();
			this.firstSelectedSequenceName = alignment.getFirstSelectedSequenceName();
			this.posInSeq = alignment.getFirstSelectedPositionX();
			this.posInUngapedSeq = alignment.getFirstSelectedUngapedPositionX();
			if(firstSelectedSequenceName == null){
				firstSelectedSequenceName = "";
			}
			
			this.updateSelectionText();
		}


		//
		// AlignmentListener
		//
		public void newSequences(AlignmentEvent alignmentEvent) {
			this.updateAll();
		}


		public void alignmentMetaChanged(AlignmentEvent alignmentEvent) {
			this.updateAll();
		}
		
		//
		// AlignmentDataListener
		//
		public void intervalAdded(ListDataEvent e) {
			if(e instanceof AlignmentDataEvent){
				contentsChanged((AlignmentDataEvent)e);
			}
		}

		public void intervalRemoved(ListDataEvent e) {
	        if(e instanceof AlignmentDataEvent){
				contentsChanged((AlignmentDataEvent)e);
			}
		}  	
		
		public void contentsChanged(ListDataEvent e) {
			if(e instanceof AlignmentDataEvent){
				contentsChanged((AlignmentDataEvent)e);
			}
		}
		
		public void contentsChanged(AlignmentDataEvent e) {
			this.updateAll();
		}
}
