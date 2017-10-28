package aliview.primer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;

import aliview.NucleotideUtilities;
import aliview.color.ColorScheme;
import aliview.color.DefaultColorScheme;
import utils.OSNativeUtils;

public class PrimerDisplay extends JComponent{

	double charWidth = 7;
	double charHeight = 9;
	ColorScheme colorScheme = new DefaultColorScheme();
	private Font baseFont = new Font(OSNativeUtils.getMonospacedFontName(), Font.PLAIN, (int)charWidth);

	private String sequence;

	public PrimerDisplay(String sequence) {
		super();
		this.sequence = sequence;
		setFont(baseFont);

	}




	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = new Dimension(100,30);
		return prefSize;
	}




	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		for(int x = 0; x < sequence.length(); x++){

			char baseChar = sequence.charAt(x);

			int baseVal = NucleotideUtilities.baseValFromChar(baseChar);

			char[] nucleotidesInThisXpos = NucleotideUtilities.nucleotideCharsFromBaseVal(baseVal);

			// loop through all chars in this pos
			for(int y = 0; y < nucleotidesInThisXpos.length; y++){

				int nucleotideBaseVal = NucleotideUtilities.baseValFromChar(nucleotidesInThisXpos[y]);

				Color baseBackgroundColor = colorScheme.getBaseBackgroundColor(nucleotideBaseVal);
				Color baseForegroundColor = colorScheme.getBaseForegroundColor(nucleotideBaseVal);


				// draw background
				g.setColor(baseBackgroundColor);
				g.fillRect((int)(x * charWidth), (int)(y * charHeight), (int)charWidth, (int)charHeight);

				// Draw char letter
				g.setColor(baseForegroundColor);

				if(charHeight > 3){
					g.drawChars(nucleotidesInThisXpos, y, 1, (int)(x * charWidth ), (int)(y * charHeight + charHeight));
				}

			}

		}

	}
}
