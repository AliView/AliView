package aliview.color;

import java.awt.Color;
import aliview.AminoAcid;
import aliview.NucleotideUtilities;

public class DarkColorScheme extends DefaultColorScheme {

    public DarkColorScheme() {
        super();
        this.colorSchemeName = "Dark Blocks";
        
        Color GAP_BG = new Color(40, 40, 40);
        Color FG_DEFAULT = new Color(220, 220, 220);
        
        baseBackgroundColor = new Color[64];
        baseBackgroundColor[NucleotideUtilities.A] = new Color(0, 80, 0); 
        baseBackgroundColor[NucleotideUtilities.C] = new Color(0, 0, 120);
        baseBackgroundColor[NucleotideUtilities.G] = new Color(80, 80, 0); 
        baseBackgroundColor[NucleotideUtilities.TU] = new Color(120, 0, 0); 
        Color IUPAC_BG = new Color(60, 60, 60);
        baseBackgroundColor[NucleotideUtilities.R] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.Y] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.M] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.K] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.W] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.S] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.B] = IUPAC_BG; 
        baseBackgroundColor[NucleotideUtilities.D] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.H] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.V] = IUPAC_BG;
        baseBackgroundColor[NucleotideUtilities.N] = IUPAC_BG; 
        baseBackgroundColor[NucleotideUtilities.GAP] = GAP_BG;
        baseBackgroundColor[NucleotideUtilities.UNKNOWN] = IUPAC_BG;
        
        baseForegroundColor = new Color[64];
        for (int i = 0; i < 64; i++) baseForegroundColor[i] = FG_DEFAULT;
        baseForegroundColor[NucleotideUtilities.GAP] = new Color(120, 120, 120);

        baseSelectionBackgroundColor = new Color[64];
        for (int i = 0; i < 64; i++) {
            Color c = baseBackgroundColor[i];
            if (c == null) c = GAP_BG;
            baseSelectionBackgroundColor[i] = new Color(Math.min(255, c.getRed() + 50), Math.min(255, c.getGreen() + 50), Math.min(255, c.getBlue() + 50));
        }
        
        baseSelectionForegroundColor = new Color[64];
        for (int i = 0; i < 64; i++) baseSelectionForegroundColor[i] = Color.WHITE;

        baseConsensusBackgroundColor = new Color(60, 60, 60);

        // Amino Acids
        aminoAcidBackgroundColor = new Color[255];
        aminoAcidForegroundColor = new Color[255];
        aminoAcidSelectionBackgroundColor = new Color[255];
        aminoAcidSelectionForegroundColor = new Color[255];

        for (int i = 0; i < 255; i++) {
            aminoAcidBackgroundColor[i] = GAP_BG;
            aminoAcidSelectionBackgroundColor[i] = new Color(80, 80, 80);
            aminoAcidForegroundColor[i] = FG_DEFAULT;
            aminoAcidSelectionForegroundColor[i] = Color.WHITE;
        }

        DefaultColorScheme def = new DefaultColorScheme();
        for (AminoAcid acid : AminoAcid.GROUP_ALL) {
            int i = acid.intVal;
            Color origBg = def.getAminoAcidBackgroundColor(acid);
            if (origBg == null) continue;
            if (origBg.equals(Color.white) || origBg.equals(new Color(230,230,230))) {
                aminoAcidBackgroundColor[i] = GAP_BG;
            } else {
                // Darken the original color for dark mode
                aminoAcidBackgroundColor[i] = new Color(origBg.getRed()/2, origBg.getGreen()/2, origBg.getBlue()/2);
            }
            
            Color c = aminoAcidBackgroundColor[i];
            aminoAcidSelectionBackgroundColor[i] = new Color(
                Math.min(255, c.getRed() + 50),
                Math.min(255, c.getGreen() + 50),
                Math.min(255, c.getBlue() + 50)
            );
        }
        aminoAcidForegroundColor[AminoAcid.GAP.intVal] = new Color(120, 120, 120);
        
        aminoAcidConsensusBackgroundColor = new Color(60, 60, 60);
    }
}
