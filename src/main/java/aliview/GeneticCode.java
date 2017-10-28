package aliview;


public final class GeneticCode {

	private final static int CODE_STATES = 64;
	public final AminoAcid[] acidTranslation = new AminoAcid[CODE_STATES];
	public int transTable;
	public String name;
	public String codeString;
	private String startCodon;


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
	}

	@Override
	public String toString() {
		return "" + transTable + ". " + name;
	}

}
