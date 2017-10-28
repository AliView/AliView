package aliview.primer;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import utils.OSNativeUtils;

public class PrimerResultTableRow {
	private static final Logger logger = Logger.getLogger(PrimerResultTableRow.class);
	private Primer primer;
	private Font baseFont = new Font(OSNativeUtils.getMonospacedFontName(), Font.PLAIN, 10);
	private static final DecimalFormat DEC_FORMAT = new DecimalFormat("##.###");

	public PrimerResultTableRow(Primer primer) {
		this.primer = primer;

	}

	public static final ArrayList<Object> getColumnHeaders(){
		ArrayList<Object> headers = new ArrayList<Object>();

		headers.add("Degen-fold");
		headers.add("Pos");
		headers.add("Len");
		headers.add("Sequence");
		headers.add("GC-cont");
		headers.add("TM(base-stacking)");
		headers.add("3-end-dimer-len");
		headers.add("dimer-len");
		headers.add("Sequence");

		return headers;
	}

	public static final ArrayList<Integer> getColumnSizes(){
		ArrayList<Integer> sizes = new ArrayList<Integer>();

		sizes.add(new Integer(30));
		sizes.add(new Integer(30));
		sizes.add(new Integer(30));
		sizes.add(new Integer(150));
		sizes.add(new Integer(30));
		sizes.add(new Integer(150));
		sizes.add(new Integer(30));
		sizes.add(new Integer(30));
		sizes.add(new Integer(100));

		return sizes;
	}


	public ArrayList<Object> getRow(){
		//this.setFont(baseFont);

		ArrayList<Object> row = new ArrayList<Object>();

		JLabel lblScore = new JLabel("" + primer.getDegenerateFold());
		lblScore.setFont(baseFont);
		//row.add(new String("" + primer.getDegenerateFold()));
		row.add(lblScore);

		JLabel lblPosition = new JLabel("" + primer.getPosition());
		lblPosition.setFont(baseFont);
		row.add(lblPosition);
		//row.add(new String("" + primer.getPosition()));

		JLabel lblLength = new JLabel("" + primer.getLength());
		lblLength.setFont(baseFont);
		row.add(lblLength);
		//row.add(new String("" + primer.getLength()));

		PrimerDisplay primerDisplay = new PrimerDisplay(primer.getSequence());
		primerDisplay.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				logger.info("MouseEnter");
			}
		});
		row.add(primerDisplay);


		JLabel lblGCcontent = new JLabel("" + DEC_FORMAT.format(primer.getGCcontent()));
		lblGCcontent.setFont(baseFont);
		row.add(lblGCcontent);

		JLabel lblTm = new JLabel(primer.getBaseStackingTmAsString() + " (avg=" + primer.getBaseStackingAvgTmAsString() + ")" );
		lblTm.setFont(baseFont);
		row.add(lblTm);

		JLabel lbl3EndDimer = new JLabel("" + primer.get3EndDimerMaxLength());
		lbl3EndDimer.setFont(baseFont);
		row.add(lbl3EndDimer);

		JLabel lblDimer = new JLabel("" + primer.getDimerMaxLength());
		lblDimer.setFont(baseFont);
		row.add(lblDimer);

		JLabel lblSequence = new JLabel(primer.getSequence());
		lblSequence.setFont(baseFont);
		row.add(lblSequence);

		return row;

	}



}
