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
	private String firstSelectedSequenceName;
	
	public StatusPanel(AlignmentPane alignmentPane, Alignment alignment) {
		this.aliPane = alignmentPane;
		this.alignment = alignment;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{350, 300, 0};
		gridBagLayout.rowHeights = new int[]{1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		lblInfo = new JLabel();
		lblInfo.setText("infotext");
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblInfo.insets = new Insets(0, 0, 0, 5);
		gbc_lblInfo.gridx = 0;
		gbc_lblInfo.gridy = 0;
		this.add(lblInfo, gbc_lblInfo);
		lblSelectionInfo = new JLabel();
		lblSelectionInfo.setText("selectiontext");
		GridBagConstraints gbc_lblSelectionInfo = new GridBagConstraints();
		gbc_lblSelectionInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSelectionInfo.anchor = GridBagConstraints.NORTH;
		gbc_lblSelectionInfo.gridx = 1;
		gbc_lblSelectionInfo.gridy = 0;
		this.add(lblSelectionInfo, gbc_lblSelectionInfo);
		this.updateAll();
	}


		public void updateAll() {
			updateAlignmentText();
			updatePositionText();
			updateSelectionText();	
		}

		public void updateSelectionText(){
			if(alignment != null){
				String ungapPosPadded = StringUtils.leftPad("" + posInUngapedSeq, 4);
				String posInSeqPadded = StringUtils.leftPad("" + posInSeq, 4);
				String selSizePadded = StringUtils.leftPad("" + selectionSize, 6);
				String selectedColumnCounPadded = StringUtils.leftPad("" + selectedColumnCount, 4);
				String selectedSeqCountPadded = StringUtils.leftPad("" + selectedSeqCount, 4);
				String firstSelSeqName = StringUtils.substring(firstSelectedSequenceName, 0, 100);
				if(selectedSeqCount > 1){
					firstSelSeqName += ("....");
				}
				
				lblSelectionInfo.setText(" Selected: " + firstSelSeqName + "  pos: " + posInSeqPadded + ", pos (ungaped): " + ungapPosPadded + " Selected sequences:" + selectedSeqCountPadded + " columns: " +  selectedColumnCounPadded + " total selected characters: " + selSizePadded);
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
				String seqCount = StringUtils.leftPad("" + alignment.getMaxY(), 6);
				String width = StringUtils.leftPad("" + alignment.getMaxX(), 6);
				lblInfo.setText("" + seqCount + " sequences, width: " + width);	
			}
		}

		public void setAlignment(Alignment alignment) {
			this.alignment = alignment;
			
		}
		

		public void setPointerPos(Point pointerPos) {
			this.pointerPos = pointerPos;
			try {
				// +1 because internally we are working with 0 as first pos and first sequence
				posInSeq = aliPane.getPositionInSequenceAt(pointerPos) + 1;
				posInUngapedSeq = aliPane.getUngapedPositionInSequenceAt(pointerPos) + 1;
				
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
