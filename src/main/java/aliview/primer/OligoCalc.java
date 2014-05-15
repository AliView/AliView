package aliview.primer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class OligoCalc {
	
	/**
	 * 
	 * Calculation were converted into java from 
	 * http://www.biophp.org/minitools/melting_temperature/
	 * 
	 * Original licence:
	 * author    Joseba Bikandi
	 * license   GNU GPL v2
	 * 
	 */
	private static final Logger logger = Logger.getLogger(OligoCalc.class);
	
	public static void main(String[] args){
		double conc_primer = 200;// nM 
		double conc_salt = 50; //mM
		double conc_mg = 0; //mM
		String testSeq = "cagcaatggatggatgatct";
		
		logger.info(OligoCalc.getBaseStackingTM(testSeq, conc_primer, conc_salt, conc_mg));
	}
	
	private static double getEnthalpy(String seq){

		double entalphy = 0;
		// enthalpy values
		if("AA".equalsIgnoreCase(seq)){ entalphy = -7.9; }   
		if("AC".equalsIgnoreCase(seq)){ entalphy = -8.4; }
		if("AG".equalsIgnoreCase(seq)){ entalphy = -7.8; }
		if("AT".equalsIgnoreCase(seq)){ entalphy = -7.2; }
		if("CA".equalsIgnoreCase(seq)){ entalphy = -8.5; }
		if("CC".equalsIgnoreCase(seq)){ entalphy = -8.0; }
		if("CG".equalsIgnoreCase(seq)){ entalphy = -10.6; }
		if("CT".equalsIgnoreCase(seq)){ entalphy = -7.8; }
		if("GA".equalsIgnoreCase(seq)){ entalphy = -8.2; }
		if("GC".equalsIgnoreCase(seq)){ entalphy = -10.6; }
		if("GG".equalsIgnoreCase(seq)){ entalphy = -8.0; }
		if("GT".equalsIgnoreCase(seq)){ entalphy = -8.4; }
		if("TA".equalsIgnoreCase(seq)){ entalphy = -7.2; }
		if("TC".equalsIgnoreCase(seq)){ entalphy = -8.2; }
		if("TG".equalsIgnoreCase(seq)){ entalphy = -8.5; }
		if("TT".equalsIgnoreCase(seq)){ entalphy = -7.9; }

		return entalphy;

	}

	private static double getEntropy(String seq){

		double entropy = 0;
		// entropy values
		if("AA".equalsIgnoreCase(seq)){ entropy = -22.2; }
		if("AC".equalsIgnoreCase(seq)){ entropy = -22.4; }
		if("AG".equalsIgnoreCase(seq)){ entropy = -21.0; }
		if("AT".equalsIgnoreCase(seq)){ entropy = -20.4; }
		if("CA".equalsIgnoreCase(seq)){ entropy = -22.7; }
		if("CC".equalsIgnoreCase(seq)){ entropy = -19.9; }
		if("CG".equalsIgnoreCase(seq)){ entropy = -27.2; }
		if("CT".equalsIgnoreCase(seq)){ entropy = -21.0; }
		if("GA".equalsIgnoreCase(seq)){ entropy = -22.2; }
		if("GC".equalsIgnoreCase(seq)){ entropy = -27.2; }
		if("GG".equalsIgnoreCase(seq)){ entropy = -19.9; }
		if("GT".equalsIgnoreCase(seq)){ entropy = -22.4; }
		if("TA".equalsIgnoreCase(seq)){ entropy = -21.3; }
		if("TC".equalsIgnoreCase(seq)){ entropy = -22.2; }
		if("TG".equalsIgnoreCase(seq)){ entropy = -22.7; }
		if("TT".equalsIgnoreCase(seq)){ entropy = -22.2; }


		return entropy;
	}

	
	
		
	public static double getBaseStackingTM(String sequence, double conc_primer, double conc_salt, double conc_mg){

		sequence = sequence.toUpperCase();
		
        // to do check only valid bases
		// to do check len > 0


        // effect on entropy by salt correction; von Ahsen et al 1999
        // Increase of stability due to presence of Mg;
        double salt_effect = conc_salt/1000 + conc_mg/1000 * 140;
        
        double h = 0;
        double s = 0;
        
        // effect on entropy
        s =0.368 * (sequence.length() -1) * Math.log(salt_effect);

        // terminal corrections. Santalucia 1998
        char firstnucleotide=sequence.charAt(0);
        if (firstnucleotide=='G' || firstnucleotide=='C'){
        	h+=0.1;
        	s+=-2.8;
        }
        if (firstnucleotide=='A' || firstnucleotide=='T'){
        	h+=2.3;
        	s+=4.1;
        }
        char lastnucleotide=sequence.charAt(sequence.length() - 1);
        if (lastnucleotide=='G' || lastnucleotide=='C'){
        	h+=0.1;
        	s+=-2.8;
        }
        if (lastnucleotide=='A' || lastnucleotide=='T'){
        	h+=2.3;
        	s+=4.1;
        }
        
        // compute new H and s based on sequence. Santalucia 1998
        for(int i=0; i < sequence.length()-1; i++){
                String subSeq=sequence.substring(i,i+2);
                h += getEnthalpy(subSeq);
                s += getEntropy(subSeq);
        }
        
        double tm=((1000*h)/(s+(1.987*Math.log(conc_primer/2000000000))))-273.15;
        //print "Tm:                 <font color=880000><b>".round($tm,1)." &deg;C</b></font>";
        //print  "\n<font color=008800>  Enthalpy: ".round($h,2)."\n  Entropy:  ".round($s,2)."</font>";
        return tm;
}
	
	public static double getEurofinsTM(String sequence){
		
		sequence = sequence.toUpperCase();
		double L = sequence.length();
		double ng = StringUtils.countMatches(sequence, "G");
		double nc = StringUtils.countMatches(sequence, "C");
		double na = StringUtils.countMatches(sequence, "A");
		double nt = StringUtils.countMatches(sequence, "T");
		
		double tm;
		if(sequence.length() > 15){
			tm = 69.3 + 41 * (ng + nc)/L - 650/L;
		}
		else{
			tm = 2*(na + nt) + 4*(ng + nc);
		}
		
		return tm;
	}
	
	

/*
function Mol_wt($primer){
$upper_mwt=molwt($primer,"DNA","upperlimit");
$lower_mwt=molwt($primer,"DNA","lowerlimit");
if ($upper_mwt==$lower_mwt){
        print "Molecular weight:        $upper_mwt";
        }else{
        print "Upper Molecular weight:  $upper_mwt\nLower Molecular weight:  $lower_mwt";
        }
        }
function CountCG($c){
        $cg=substr_count($c,"G")+substr_count($c,"C");
        return $cg;
        }

function CountATCG($c){
        $cg=substr_count($c,"A")+substr_count($c,"T")+substr_count($c,"G")+substr_count($c,"C");
        return $cg;
        }


function Tm_min($primer){
        $primer_len=strlen($primer);
        $primer2=preg_replace("/A|T|Y|R|W|K|M|D|V|H|B|N/","A",$primer);
        $n_AT=substr_count($primer2,"A");
        $primer2=preg_replace("/C|G|S/","G",$primer);
        $n_CG=substr_count($primer2,"G");

                if ($primer_len > 0) {
                        if ($primer_len < 14) {
                                return round(2 * ($n_AT) + 4 * ($n_CG));
                        }else{
                                return round(64.9 + 41*(($n_CG-16.4)/$primer_len),1);
                        }
                }
}

function Tm_max($primer){
        $primer_len=strlen($primer);
        $primer=primer_max($primer);
        $n_AT=substr_count($primer,"A");
        $n_CG=substr_count($primer,"G");
                if ($primer_len > 0) {
                        if ($primer_len < 14) {
                                return round(2 * ($n_AT) + 4 * ($n_CG));
                        }else{
                                return round(64.9 + 41*(($n_CG-16.4)/$primer_len),1);
                        }
                }
}

function primer_min($primer){
        $primer=preg_replace("/A|T|Y|R|W|K|M|D|V|H|B|N/","A",$primer);
        $primer=preg_replace("/C|G|S/","G",$primer);
        return $primer;
        }

function primer_max($primer){
        $primer=preg_replace("/A|T|W/","A",$primer);
        $primer=preg_replace("/C|G|Y|R|S|K|M|D|V|H|B|N/","G",$primer);
        return $primer;
        }
function molwt($sequence,$moltype,$limit)
    {

        // the following are single strand molecular weights / base
        $rna_A_wt = 329.245;
        $rna_C_wt = 305.215;
        $rna_G_wt = 345.245;
        $rna_U_wt = 306.195;

        $dna_A_wt = 313.245;
        $dna_C_wt = 289.215;
        $dna_G_wt = 329.245;
        $dna_T_wt = 304.225;

        $water = 18.015;

        $dna_wts = array('A' => array($dna_A_wt, $dna_A_wt),  // Adenine
                         'C' => array($dna_C_wt, $dna_C_wt),  // Cytosine
                         'G' => array($dna_G_wt, $dna_G_wt),  // Guanine
                         'T' => array($dna_T_wt, $dna_T_wt),  // Thymine
                         'M' => array($dna_C_wt, $dna_A_wt),  // A or C
                         'R' => array($dna_A_wt, $dna_G_wt),  // A or G
                         'W' => array($dna_T_wt, $dna_A_wt),  // A or T
                         'S' => array($dna_C_wt, $dna_G_wt),  // C or G
                         'Y' => array($dna_C_wt, $dna_T_wt),  // C or T
                         'K' => array($dna_T_wt, $dna_G_wt),  // G or T
                         'V' => array($dna_C_wt, $dna_G_wt),  // A or C or G
                         'H' => array($dna_C_wt, $dna_A_wt),  // A or C or T
                         'D' => array($dna_T_wt, $dna_G_wt),  // A or G or T
                         'B' => array($dna_C_wt, $dna_G_wt),  // C or G or T
                         'X' => array($dna_C_wt, $dna_G_wt),  // G, A, T or C
                         'N' => array($dna_C_wt, $dna_G_wt)   // G, A, T or C
           );

        $rna_wts = array('A' => array($rna_A_wt, $rna_A_wt),  // Adenine
                         'C' => array($rna_C_wt, $rna_C_wt),  // Cytosine
                         'G' => array($rna_G_wt, $rna_G_wt),  // Guanine
                         'U' => array($rna_U_wt, $rna_U_wt),  // Uracil
                         'M' => array($rna_C_wt, $rna_A_wt),  // A or C
                         'R' => array($rna_A_wt, $rna_G_wt),  // A or G
                         'W' => array($rna_U_wt, $rna_A_wt),  // A or U
                         'S' => array($rna_C_wt, $rna_G_wt),  // C or G
                         'Y' => array($rna_C_wt, $rna_U_wt),  // C or U
                         'K' => array($rna_U_wt, $rna_G_wt),  // G or U
                         'V' => array($rna_C_wt, $rna_G_wt),  // A or C or G
                         'H' => array($rna_C_wt, $rna_A_wt),  // A or C or U
                         'D' => array($rna_U_wt, $rna_G_wt),  // A or G or U
                         'B' => array($rna_C_wt, $rna_G_wt),  // C or G or U
                         'X' => array($rna_C_wt, $rna_G_wt),  // G, A, U or C
                         'N' => array($rna_C_wt, $rna_G_wt)   // G, A, U or C
             );

        $all_na_wts = array('DNA' => $dna_wts, 'RNA' => $rna_wts);
        //print_r($all_na_wts);
        $na_wts = $all_na_wts[$moltype];

        $mwt = 0;
        $NA_len = strlen($sequence);

        if($limit=="lowerlimit"){$wlimit=1;}
        if($limit=="upperlimit"){$wlimit=0;}

        for ($i = 0; $i < $NA_len; $i++) {
            $NA_base = substr($sequence, $i, 1);
            $mwt += $na_wts[$NA_base][$wlimit];
        }
        $mwt += $water;

        return $mwt;
    }
*/
	
	/*
	
	private terminalcorrections(seq)//helix initiation corrections from Santalucia 1998 & Allawi & Santalucia 1997
	{
	var deltah=0;
	var deltas=0;
	if ((seq.charAt(0)=="G")||(seq.charAt(0)=="C"))
		{
		deltah+=0.1;
		deltas+=-2.8;
		}
	if((seq.charAt(0)=="A")||(seq.charAt(0)=="T"))
		{
		deltah+=2.3;
		deltas+=4.1;
		}
		
	if ((seq.charAt(seq.length-1)=="G")||(seq.charAt(seq.length-1)=="C"))
		{
		deltah+=0.1;
		deltas+=-2.8;
		}
	if((seq.charAt(seq.length-1)=="A")||(seq.charAt(seq.length-1)=="T"))
		{
		deltah+=2.3;
		deltas+=4.1;
		}
	dh+=deltah;
	ds+=deltas;
	}

function saltcorrections(seq,salt)//changes to ds dependant on the salt concentration & sequence length
	{
	salt+=(mg/1E3 * 140);//convert to moles and then adjust for greater stabilizing effects of Mg compared to Na or K. See von Ahsen et al 1999	
	var deltas=0;
	deltas+=0.368 * (seq.length-1)* Math.log(salt);//This comes from von Ahsen et al 1999
	ds+=deltas;
	}
	
function stack(seq,salt, primer)// base stacking calculations. 
{
ds=0;
dh=0;//deltaH. Enthalpy
	var R=1.987; //universal gas constant in Cal/degrees C*Mol
	saltcorrections(seq,salt);
	terminalcorrections(seq);
	K=(primer/2) * 1E-9; //converts from nanomolar to Molar. Note this ignores the contribution of the target since this is << than primer concentration.
	for (i = 0; i < seq.length; i++)//adds up dh and ds for each 2 base combination. dh is in kcal/mol. ds is in cal/Kelvin/mol
		{	
		if (seq.charAt(i)=="G")
			{
			if (seq.charAt(i+1)=="G")
				{
				dh+=-8;
				ds+=-19.9;
				}
			if (seq.charAt(i+1)=="A")
				{
				dh+=-8.2;
				ds+=-22.2;
				}
			if (seq.charAt(i+1)=="T")
				{
				dh+=-8.4;
				ds+=-22.4;
				}
			if (seq.charAt(i+1)=="C")
			//These values where fixed on 4/23/2008. They were dh = -10.6 & ds = -27.2
			//The new values have been double check against Santalucia 1998 
				{
				dh+=-9.8;
				ds+=-24.4;
				}
			}
		if (seq.charAt(i)=="A")
			{
			if (seq.charAt(i+1)=="G")
				{
				dh+=-7.8;
				ds+=-21;
				}
			if (seq.charAt(i+1)=="A")
				{
				dh+=-7.9;
				ds+=-22.2;
				}
			if (seq.charAt(i+1)=="T")
				{
				dh+=-7.2;
				ds+=-20.4;
				}
			if (seq.charAt(i+1)=="C")
				{
				dh+=-8.4;
				ds+=-22.4;
				}
			}
		if (seq.charAt(i)=="T")
			{
			if (seq.charAt(i+1)=="G")
				{
				dh+=-8.5;
				ds+=-22.7;
				}
			if (seq.charAt(i+1)=="A")
				{
				dh+=-7.2;
				ds+=-21.3;
				}
			if (seq.charAt(i+1)=="T")
				{
				dh+=-7.9;
				ds+=-22.2;
				}
			if (seq.charAt(i+1)=="C")
				{
				dh+=-8.2;
				ds+=-22.2;
				}
			}
		if (seq.charAt(i)=="C")
			{
			if (seq.charAt(i+1)=="G")
				{
				dh+=-10.6;
				ds+=-27.2;
				}
			if (seq.charAt(i+1)=="A")
				{
				dh+=-8.5;
				ds+=-22.7;
				}
			if (seq.charAt(i+1)=="T")
				{
				dh+=-7.8;
				ds+=-21;
				}
			if (seq.charAt(i+1)=="C")
				{
				dh+=-8;
				ds+=-19.9;
				}
			}
		}
	tm= ((1000* dh)/(ds+(R * Math.log(K))))-273.15;	//The actual answer!
	return tm;	
	}
	
	*/

}
