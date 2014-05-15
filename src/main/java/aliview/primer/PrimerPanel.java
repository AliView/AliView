package aliview.primer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;


public class PrimerPanel extends JPanel {
	private static final Logger logger = Logger.getLogger(PrimerPanel.class);
	private Primer primer;
	private Font baseFont = new Font(Font.MONOSPACED, Font.PLAIN, 10);
	private LineBorder unselectedBorder = new LineBorder(Color.LIGHT_GRAY);
	private LineBorder selectedBorder = new LineBorder(Color.DARK_GRAY);
	private static final DecimalFormat DEC_FORMAT = new DecimalFormat("##.###");

	public PrimerPanel(Primer primer) {
		this.primer = primer;
		
		this.setFont(baseFont);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{50, 50, 44, 150, 44, 224, 33, 0, 150, 0};
		gridBagLayout.rowHeights = new int[]{50, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblScore = new JLabel("" + primer.getScore());
		lblScore.setFont(baseFont);
		GridBagConstraints gbc_lblScore = new GridBagConstraints();
		gbc_lblScore.fill = GridBagConstraints.VERTICAL;
		gbc_lblScore.insets = new Insets(0, 0, 0, 5);
		gbc_lblScore.gridx = 0;
		gbc_lblScore.gridy = 0;
		add(lblScore, gbc_lblScore);
		
		JLabel lblPosition = new JLabel("" + primer.getPosition());
		lblPosition.setFont(baseFont);
		GridBagConstraints gbc_lblPosition = new GridBagConstraints();
		gbc_lblPosition.fill = GridBagConstraints.VERTICAL;
		gbc_lblPosition.insets = new Insets(0, 0, 0, 5);
		gbc_lblPosition.gridx = 1;
		gbc_lblPosition.gridy = 0;
		add(lblPosition, gbc_lblPosition);
		
		JLabel lblLength = new JLabel("" + primer.getLength());
		lblLength.setFont(new Font("Monospaced", Font.PLAIN, 10));
		GridBagConstraints gbc_lblLength = new GridBagConstraints();
		gbc_lblLength.insets = new Insets(0, 0, 0, 5);
		gbc_lblLength.gridx = 2;
		gbc_lblLength.gridy = 0;
		add(lblLength, gbc_lblLength);
		
		PrimerDisplay primerDisplay = new PrimerDisplay(primer.getSequence());
		primerDisplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				logger.info("MouseEnter");
			}
		});
		GridBagConstraints gbc_primerDisplay = new GridBagConstraints();
		gbc_primerDisplay.insets = new Insets(0, 0, 0, 5);
		gbc_primerDisplay.fill = GridBagConstraints.BOTH;
		gbc_primerDisplay.gridx = 3;
		gbc_primerDisplay.gridy = 0;
		add(primerDisplay, gbc_primerDisplay);
		
		JLabel lblGCcontent = new JLabel("" + DEC_FORMAT.format(primer.getGCcontent()));
		lblGCcontent.setFont(new Font("Monospaced", Font.PLAIN, 10));
		GridBagConstraints gbc_lblGCcontent = new GridBagConstraints();
		gbc_lblGCcontent.insets = new Insets(0, 0, 0, 5);
		gbc_lblGCcontent.gridx = 4;
		gbc_lblGCcontent.gridy = 0;
		add(lblGCcontent, gbc_lblGCcontent);
		
		JLabel lblTm = new JLabel(primer.getBaseStackingTmAsString() + " (avg=" + primer.getBaseStackingAvgTmAsString() + ")" );
		lblTm.setFont(baseFont);
		GridBagConstraints gbc_lblTm = new GridBagConstraints();
		gbc_lblTm.fill = GridBagConstraints.VERTICAL;
		gbc_lblTm.insets = new Insets(0, 0, 0, 5);
		gbc_lblTm.gridx = 5;
		gbc_lblTm.gridy = 0;
		add(lblTm, gbc_lblTm);
		
		JLabel lbl3EndDimer = new JLabel("" + primer.get3EndDimerMaxLength());
		lbl3EndDimer.setFont(new Font("Monospaced", Font.PLAIN, 10));
		GridBagConstraints gbc_lbl3EndDimer = new GridBagConstraints();
		gbc_lbl3EndDimer.insets = new Insets(0, 0, 0, 5);
		gbc_lbl3EndDimer.gridx = 6;
		gbc_lbl3EndDimer.gridy = 0;
		add(lbl3EndDimer, gbc_lbl3EndDimer);
		
		JLabel lblDimer = new JLabel("" + primer.getDimerMaxLength());
		lblDimer.setFont(new Font("Monospaced", Font.PLAIN, 10));
		GridBagConstraints gbc_lblDimer = new GridBagConstraints();
		gbc_lblDimer.insets = new Insets(0, 0, 0, 5);
		gbc_lblDimer.gridx = 7;
		gbc_lblDimer.gridy = 0;
		add(lblDimer, gbc_lblDimer);
		
		JLabel lblSequence = new JLabel(primer.getSequence());
		lblSequence.setFont(baseFont);
		GridBagConstraints gbc_lblSequence = new GridBagConstraints();
		gbc_lblSequence.fill = GridBagConstraints.VERTICAL;
		gbc_lblSequence.gridx = 8;
		gbc_lblSequence.gridy = 0;
		add(lblSequence, gbc_lblSequence);
	}
	/*
	public Dimension getPreferredSize() {
		Dimension prefSize = new Dimension(300,30);
		return prefSize;
	}
	*/

	public void isSelected(boolean isSelected) {
		if(isSelected){
			this.setBorder(selectedBorder);
		}
		else{
			this.setBorder(unselectedBorder);
		}
	}

	public Primer getPrimer() {
		return primer;
	}
	
	

}
