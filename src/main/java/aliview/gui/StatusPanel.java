package aliview.gui;

import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.alignment.Alignment;
import aliview.alignment.AlignmentEvent;
import aliview.alignment.AlignmentListener;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class StatusPanel extends JPanel implements AlignmentListener{
	private static final Logger logger = Logger.getLogger(StatusPanel.class);
	private Alignment alignment;
	private AlignmentPane aliPane;
	private Point pointerPos;
	private JLabel lblInfo;
	private JLabel lblSelectionInfo;
	private long selectionSize;
	private int posInSeq;
	private int posInUngapedSeq;
	private int selectedColumnCount;
	private int selectedSeqCount;
	private String firstSelectedSequenceName = "";
	
	public StatusPanel(AlignmentPane alignmentPane, Alignment alignment) {
		this.aliPane = alignmentPane;
		this.alignment = alignment;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 200, 10, 100, 0};
		gridBagLayout.rowHeights = new int[]{1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		lblInfo = new JLabel();
		
		lblInfo.setText("infotext");
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblInfo.insets = new Insets(0, 0, 0, 5);
		gbc_lblInfo.gridx = 1;
		gbc_lblInfo.gridy = 0;
		
		
		lblSelectionInfo = new JLabel();
//		lblSelectionInfo.setBackground(Color.red);
//		lblSelectionInfo.setOpaque(true);
		lblSelectionInfo.setText("selectiontext");
		GridBagConstraints gbc_lblSelectionInfo = new GridBagConstraints();
		gbc_lblSelectionInfo.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblSelectionInfo.gridx = 3;
		gbc_lblSelectionInfo.gridy = 0;
		
		this.add(lblInfo, gbc_lblSelectionInfo);
		this.add(lblSelectionInfo, gbc_lblInfo);
		this.updateAll();
	}


		public void updateAll() {
			updateAlignmentText();
			updatePositionText();
			updateSelectionText();	
		}

		public void updateSelectionText(){
			if(alignment != null && selectionSize > 0){
				// +1 because internally we are working with 0 as first pos and first sequence
				String ungapPosPadded = StringUtils.leftPad("" + (posInUngapedSeq + 1), 4);
				String posInSeqPadded = StringUtils.leftPad("" + (posInSeq + 1), 4);
				String selSizePadded = StringUtils.leftPad("" + selectionSize, 6);
				String selectedColumnCounPadded = StringUtils.leftPad("" + selectedColumnCount, 4);
				String selectedSeqCountPadded = StringUtils.leftPad("" + selectedSeqCount, 4);
				String firstSelSeqName = StringUtils.substring(firstSelectedSequenceName, 0, 40);
				if(firstSelSeqName != null){
					if(firstSelSeqName.length() == 40 || selectedSeqCount > 1){
						firstSelSeqName +="...";
					}
				}
				if(selectionSize == 0){
					ungapPosPadded = StringUtils.leftPad("" + "", 4);
					posInSeqPadded = StringUtils.leftPad("" + "", 4);
				}
				
				lblSelectionInfo.setText("Selected: " + firstSelSeqName + " | pos: " + posInSeqPadded + " | pos (ungaped): " + ungapPosPadded + " | Selected seqs:" + selectedSeqCountPadded + " | cols: " +  selectedColumnCounPadded + " | total selected chars: " + selSizePadded);
			}else{
				lblSelectionInfo.setText("");
			}
		}
		
		public void updatePositionText(){
			
		}
		
		
		public void updateAlignmentText(){
			if(alignment == null){
				lblInfo.setText("no alignmnet loaded");
			}else{
				String seqCount = StringUtils.leftPad("Alignment: " + alignment.getMaxY(), 6);
				String width = StringUtils.leftPad("" + alignment.getMaxX(), 6);
				lblInfo.setText("" + seqCount + " sequences " + width + " pos.  ");	
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



		public void selectionChanged(Alignment source) {
			
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


		public void sequencesChanged(AlignmentEvent alignmentEvent) {
			this.updateAll();
			
		}


		public void newSequences(AlignmentEvent alignmentEvent) {
			this.updateAll();
		}


		public void sequenceOrderChanged(AlignmentEvent alignmentEvent) {
			// TODO Auto-generated method stub		
		}


		public void alignmentMetaChanged(AlignmentEvent alignmentEvent) {
			this.updateAll();
			
		}


		public void sequencesRemoved(AlignmentEvent alignmentEvent) {
			this.updateAll();	
		}
		
	
	
}
