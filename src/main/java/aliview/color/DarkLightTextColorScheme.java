package aliview.color;

import java.awt.Color;
import aliview.NucleotideUtilities;

public class DarkLightTextColorScheme extends DarkColorScheme {
    public DarkLightTextColorScheme() {
        super();
        this.colorSchemeName = "Dark with light text";

        // --- Background Block Colors ---
        baseBackgroundColor[NucleotideUtilities.A] = new Color(0, 128, 0);
        baseBackgroundColor[NucleotideUtilities.C] = new Color(0, 0, 255);
        baseBackgroundColor[NucleotideUtilities.G] = new Color(107, 99, 99);
        baseBackgroundColor[NucleotideUtilities.TU] = new Color(199, 29, 77);

        // --- Foreground Letter Colors (Light Text) ---
        for(int i = 0; i < 64; i++) {
            baseForegroundColor[i] = new Color(220, 220, 220); // Off-white
        }
        baseForegroundColor[NucleotideUtilities.GAP] = new Color(100, 100, 100);

        // --- Custom Selection Logic for Light Text ---
        for(int i = 0; i < 64; i++) {
            if (baseBackgroundColor[i] != null) {
                // Selection background becomes a bit more vivid
                baseSelectionBackgroundColor[i] = baseBackgroundColor[i].brighter();
                // Letters become pure white to "glow"
                baseSelectionForegroundColor[i] = Color.WHITE;
            }
        }
    }
}
