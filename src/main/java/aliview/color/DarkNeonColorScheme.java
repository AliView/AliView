package aliview.color;

import java.awt.Color;
import aliview.NucleotideUtilities;

public class DarkNeonColorScheme extends DarkColorScheme {
    public DarkNeonColorScheme() {
        super();
        this.colorSchemeName = "Dark Neon";

        // --- Background Block Colors ---
        baseBackgroundColor[NucleotideUtilities.A] = new Color(32, 167, 102);
        baseBackgroundColor[NucleotideUtilities.C] = new Color(0, 128, 255);
        baseBackgroundColor[NucleotideUtilities.G] = new Color(107, 99, 99);
        baseBackgroundColor[NucleotideUtilities.TU] = new Color(223, 32, 86);

        // --- Foreground Letter Colors ---
        baseForegroundColor[NucleotideUtilities.A] = new Color(0, 60, 0);
        baseForegroundColor[NucleotideUtilities.C] = new Color(0, 0, 110);
        baseForegroundColor[NucleotideUtilities.G] = new Color(45, 40, 40);
        baseForegroundColor[NucleotideUtilities.TU] = new Color(90, 10, 30);
        
        // IUPAC Foreground
        Color iupacFG = new Color(110, 0, 110);
        baseForegroundColor[NucleotideUtilities.R] = iupacFG;
        baseForegroundColor[NucleotideUtilities.Y] = iupacFG;
        baseForegroundColor[NucleotideUtilities.M] = iupacFG;
        baseForegroundColor[NucleotideUtilities.K] = iupacFG;
        baseForegroundColor[NucleotideUtilities.W] = iupacFG;
        baseForegroundColor[NucleotideUtilities.S] = iupacFG;
        baseForegroundColor[NucleotideUtilities.N] = iupacFG;
        baseForegroundColor[NucleotideUtilities.GAP] = new Color(80, 80, 80);

        // --- Custom Selection Logic for Neon ---
        for(int i = 0; i < 64; i++) {
            if (baseBackgroundColor[i] != null) {
                // Selection blocks are significantly brighter versions of the neon blocks
                baseSelectionBackgroundColor[i] = baseBackgroundColor[i].brighter();
                // Letters turn white when selected for high contrast
                baseSelectionForegroundColor[i] = Color.WHITE;
            }
        }
    }
}
