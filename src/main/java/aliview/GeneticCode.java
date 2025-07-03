package aliview;

import java.util.ArrayList;
import java.util.HashMap;


public final class GeneticCode {

	private final static int CODE_STATES = 64;
	public final AminoAcid[] acidTranslation = new AminoAcid[CODE_STATES];
	public int transTable;
	public String name;
	public String codeString;
	private String startCodon;

	private final static int IUPAC_CODE_STATES = 4192;
	public final AminoAcid[] iupacAcidTranslation = new AminoAcid[IUPAC_CODE_STATES];

	// Genetic code information: http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi
	//


	public static final GeneticCode GC1 = new GeneticCode(1,"Standard code","FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG", "AUG");
	public static final GeneticCode  GC2 = new GeneticCode(2,"Vertebrate Mitochondrial", "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSS**VVVVAAAADDEEGGGG","AUG");
	public static final GeneticCode  GC3 = new GeneticCode(3,"Yeast Mitochondrial","FFLLSSSSYY**CCWWTTTTPPPPHHQQRRRRIIMMTTTTNNKKSSRRVVVVAAAADDEEGGGG","AUG");
	public static final GeneticCode  GC4 = new GeneticCode(4,"Mold, Protozoan, and Coelenterate Mitochondrial and Mycoplasma/Spiroplasma", "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG","AUG");
	public static final GeneticCode  GC5 = new GeneticCode(5,"Invertebrate Mitochondrial","FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSSSVVVVAAAADDEEGGGG","AUG"); 
	public static final GeneticCode  GC6 = new GeneticCode(6,"The Ciliate, Dasycladacean and Hexamita Nuclear Code", "FFLLSSSSYYQQCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");	  
	public static final GeneticCode  GC9 = new GeneticCode(9,"The Echinoderm and Flatworm Mitochondrial", "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC10 = new GeneticCode(10,"The Euplotid Nuclear Code", "FFLLSSSSYY**CCCWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC11 = new GeneticCode(11,"The Bacterial, Archaeal and Plant Plastid Code", "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC12 = new GeneticCode(12,"The Alternative Yeast Nuclear Code", "FFLLSSSSYY**CC*WLLLSPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC13 = new GeneticCode(13,"The Ascidian Mitochondrial Code","FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNKKSSGGVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC14 = new GeneticCode(14,"The Alternative Flatworm Mitochondrial Code","FFLLSSSSYYY*CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNNKSSSSVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC15 = new GeneticCode(15,"Blepharisma Nuclear Code","FFLLSSSSYY*QCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC16 = new GeneticCode(16,"Chlorophycean Mitochondrial Code","FFLLSSSSYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC21 = new GeneticCode(21,"Trematode Mitochondrial Code", "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIMMTTTTNNNKSSSSVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC22 = new GeneticCode(22,"Scenedesmus obliquus mitochondrial Code","FFLLSS*SYY*LCC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");	  
	public static final GeneticCode  GC23 = new GeneticCode(23,"Thraustochytrium Mitochondrial Code", "FF*LSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC24 = new GeneticCode(24,"Pterobranchia mitochondrial code", "FFLLSSSSYY**CCWWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSSKVVVVAAAADDEEGGGG");
	public static final GeneticCode  GC25 = new GeneticCode(25,"Candidate Division SR1 and Gracilibacteria Code", "FFLLSSSSYY**CCGWLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG");


	public static final GeneticCode DEFAULT = GC1;
	public static final GeneticCode[] allCodesArray = new GeneticCode[]{GC1,GC2,GC3,GC4,GC5,GC6,GC9,GC10,GC11,GC12,GC13,GC14,GC15,GC16,GC21,GC22,GC23,GC24,GC25};		

	public GeneticCode(int transTable, String name, String codeString){
		this(transTable,name,codeString,"AUG");
	}

	public GeneticCode(int transTable, String name, String codeString, String startCodon){
		this.transTable = transTable;
		this.name = name;
		this.codeString = codeString;
		this.startCodon = startCodon;
		for(int n = 0; n < CODE_STATES; n++){
			acidTranslation[n] = AminoAcid.getAminoAcidFromChar(codeString.charAt(n));
		}
		// initialize iupacAcidTranslation to UNKNOWN (except for 0, which is GAP)
		iupacAcidTranslation[0] = AminoAcid.GAP;
		for(int n = 1; n < IUPAC_CODE_STATES; n++){
			iupacAcidTranslation[n] = AminoAcid.X;
		}
		// build a map of which codons code for each amino acid
		HashMap<AminoAcid, ArrayList<Integer>> acidToCodon = new HashMap<AminoAcid, ArrayList<Integer>>();
		for(int n = 0; n < CODE_STATES; n++){
			AminoAcid acid = acidTranslation[n];
			if(!acidToCodon.containsKey(acid)){
				acidToCodon.put(acid, new ArrayList<Integer>());
			}
			acidToCodon.get(acid).add(n);
		}

		// codons use 2-bit encoding, (T = 0, C = 1, A = 2, G = 3)
		// everything else uses 4-bit encoding (T = 8, C = 2, A = 1, G = 4))
		int[] indexToBase = {8,2,1,4};

        // now fill in the other non-UNKNOWN codons

		// codons which code for the current amino acid, and which have a base which matches the current ambiguity at position2
		ArrayList<Integer> position2Codons = new ArrayList<Integer>();
		// codons which code for the current amino acid, and which have a base which matches the current ambiguity at position1
		// (and position2)
		ArrayList<Integer> position1Codons = new ArrayList<Integer>();

		for (AminoAcid aa : acidToCodon.keySet()){
			ArrayList<Integer> codons = acidToCodon.get(aa);
			// start with position 2, it is the most conserved, so it will tend to fail early
			for (int position2IUPAC = 1; position2IUPAC < 16; position2IUPAC++){
				position2Codons.clear();
				int p2found = 0; // accumulate all bases at position2 which match position2IUPAC
				for (Integer codon : codons) {
					// get the base at position 2
					int base2 = indexToBase[(codon & 0b001100) >> 2];
					// if the base matches the current ambiguity at position 2, add the codon to the list
					if ((base2 & position2IUPAC) == base2) {
						position2Codons.add(codon);
						p2found |= base2;
					}
				}
				// if we found all of the bases required to match position2IUPAC, then we can continue
				if (p2found != position2IUPAC) {
					continue;
				}
				for (int position1IUPAC = 1; position1IUPAC < 16; position1IUPAC++){
					position1Codons.clear();
					int p1found = 0; // accumulate all bases at position1 which match position1IUPAC
					for (Integer codon : position2Codons) {
						int base1 = indexToBase[(codon & 0b110000) >> 4];
						if ((base1 & position1IUPAC) == base1) {
							position1Codons.add(codon);
							p1found |= base1;
						}
					}
					if (p1found != position1IUPAC) {
						continue;
					}
					for (int position3IUPAC = 1; position3IUPAC < 16; position3IUPAC++){
						int p3found = 0;
						for (Integer codon : position1Codons) {
							int base3 = indexToBase[(codon & 0b000011)];
							if ((base3 & position3IUPAC) == base3) {
								p3found |= base3;
								if (p3found == position3IUPAC) {
									// we have found all of the bases required to match position3IUPAC
									// so we can add the codon to the iupacAcidTranslation and stop looking
									iupacAcidTranslation[position3IUPAC + (position2IUPAC << 4) + (position1IUPAC << 8)] = aa;
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "" + transTable + ". " + name;
	}

}
