package aliview.color;

import java.awt.Color;
import aliview.AminoAcid;
import aliview.NucleotideUtilities;

public class DarkColorSchemeText extends DefaultColorScheme {

    public DarkColorSchemeText() {
        super();
        this.colorSchemeName = "Dark Text";
        
        Color BG = new Color(30, 30, 30);
        Color BG_SELECTED = new Color(70, 70, 70);
        
        baseBackgroundColor = new Color[64];
        baseSelectionBackgroundColor = new Color[64];
        baseForegroundColor = new Color[64];
        baseSelectionForegroundColor = new Color[64];

        for (int i = 0; i < 64; i++) {
            baseBackgroundColor[i] = BG;
            baseSelectionBackgroundColor[i] = BG_SELECTED;
            baseForegroundColor[i] = new Color(200, 200, 200);
            baseSelectionForegroundColor[i] = Color.WHITE;
        }

        baseForegroundColor[NucleotideUtilities.A] = new Color(110, 240, 110); 
        baseForegroundColor[NucleotideUtilities.C] = new Color(110, 170, 255); 
        baseForegroundColor[NucleotideUtilities.G] = new Color(240, 200, 80);  
        baseForegroundColor[NucleotideUtilities.TU] = new Color(255, 110, 110); 
        
        baseSelectionForegroundColor[NucleotideUtilities.A] = baseForegroundColor[NucleotideUtilities.A].brighter();
        baseSelectionForegroundColor[NucleotideUtilities.C] = baseForegroundColor[NucleotideUtilities.C].brighter();
        baseSelectionForegroundColor[NucleotideUtilities.G] = baseForegroundColor[NucleotideUtilities.G].brighter();
        baseSelectionForegroundColor[NucleotideUtilities.TU] = baseForegroundColor[NucleotideUtilities.TU].brighter();

        baseForegroundColor[NucleotideUtilities.GAP] = new Color(120, 120, 120);
        baseSelectionForegroundColor[NucleotideUtilities.GAP] = new Color(170, 170, 170);

        baseConsensusBackgroundColor = new Color(50, 50, 50);

        // Amino Acids
        aminoAcidBackgroundColor = new Color[255];
        aminoAcidForegroundColor = new Color[255];
        aminoAcidSelectionBackgroundColor = new Color[255];
        aminoAcidSelectionForegroundColor = new Color[255];

        for (int i = 0; i < 255; i++) {
            aminoAcidBackgroundColor[i] = BG;
            aminoAcidSelectionBackgroundColor[i] = BG_SELECTED;
        }
        
        DefaultColorScheme def = new DefaultColorScheme();
        for (AminoAcid acid : AminoAcid.GROUP_ALL) {
            int i = acid.intVal;
            Color origBg = def.getAminoAcidBackgroundColor(acid);
            if (origBg != null && !origBg.equals(Color.white) && !origBg.equals(new Color(230, 230, 230))) {
                aminoAcidForegroundColor[i] = origBg.brighter();
                aminoAcidSelectionForegroundColor[i] = origBg.brighter().brighter();
            } else {
                aminoAcidForegroundColor[i] = new Color(200, 200, 200);
                aminoAcidSelectionForegroundColor[i] = Color.WHITE;
            }
        }
        aminoAcidForegroundColor[AminoAcid.GAP.intVal] = new Color(120, 120, 120);
        aminoAcidSelectionForegroundColor[AminoAcid.GAP.intVal] = new Color(170, 170, 170);
        aminoAcidConsensusBackgroundColor = new Color(50, 50, 50);
    }
}
