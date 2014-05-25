package aliview;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;



public class AminoAcid {
	private static final Logger logger = Logger.getLogger(AminoAcid.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	public static final AminoAcid A = new AminoAcid("Alanine", "Ala", "A", 0);
    public static final AminoAcid C = new AminoAcid("Cysteine", "Cys", "C", 1);
    public static final AminoAcid D = new AminoAcid("Aspartic acid", "Asp", "D", 2);
    public static final AminoAcid E = new AminoAcid("Glutamic acid", "Glu", "E", 3);
    public static final AminoAcid F = new AminoAcid("Phenylalanine", "Phe", "F", 4);
    public static final AminoAcid G = new AminoAcid("Glycine", "Gly", "G", 5);
    public static final AminoAcid H = new AminoAcid("Histidine", "His", "H", 6);
    public static final AminoAcid I = new AminoAcid("Isoleucine", "Ile", "I", 7);
    public static final AminoAcid K = new AminoAcid("Lysine", "Lys", "K", 8);
    public static final AminoAcid L = new AminoAcid("Leucine", "Leu", "L", 9);
    public static final AminoAcid M = new AminoAcid("Methionine", "Met", "M", 10);
    public static final AminoAcid N = new AminoAcid("Asparagine", "Asn", "N", 11);
    public static final AminoAcid P = new AminoAcid("Proline", "Pro", "P", 12);    
    public static final AminoAcid Q = new AminoAcid("Glutamine", "Gln", "Q", 13);
    public static final AminoAcid R = new AminoAcid("Arginine", "Arg", "R", 14);
    public static final AminoAcid S = new AminoAcid("Serine", "Ser", "S", 15);
    public static final AminoAcid T = new AminoAcid("Threonine", "Thr", "T", 16);
    public static final AminoAcid V = new AminoAcid("Valine", "Val", "V", 17);
    public static final AminoAcid W = new AminoAcid("Tryptophan", "Trp", "W", 18);
    public static final AminoAcid Y = new AminoAcid("Tyrosine", "Tyr", "Y", 19);
    public static final AminoAcid X = new AminoAcid("Unknown amino acid", "Xaa", "X", 25);
    public static final AminoAcid STOP = new AminoAcid("Stop codon", " * ","X", 27);
    public static final AminoAcid GAP = new AminoAcid("Gap", " - ","-", 29);
    
    public static final AminoAcid[] GROUP_ACFHILMVWY = new AminoAcid[]{A,C,F,H,I,L,M,V,W,Y};
    public static final AminoAcid[] GROUP_WLVIMAFCHP = new AminoAcid[]{W,L,V,I,M,A,F,C,H,P};
    public static final AminoAcid[] GROUP_KR = new AminoAcid[]{K,R};
    public static final AminoAcid[] GROUP_QE = new AminoAcid[]{Q,E};
    public static final AminoAcid[] GROUP_ED = new AminoAcid[]{E,D};
    public static final AminoAcid[] GROUP_TS = new AminoAcid[]{T,S};
    
    
	private String name;
	private String threeLeterName;
	private String code;
	private char codeCharVal;
	public int intVal;
	private byte codeByteVal;
	private byte[] codeByteArray;
	
	/*private static final HashMap<String, AminoAcid> aminoAcids = new HashMap<String, AminoAcid>(30);
	
	static{
		aminoAcids.put("A", new AminoAcid("Alanine", "Ala", "A", 0));
		aminoAcids.put("C", new AminoAcid("Cysteine", "Cys", "C", 1));
		aminoAcids.put("D", new AminoAcid("Aspartic acid", "Asp", "D", 2));
		aminoAcids.put("E", new AminoAcid("Glutamic acid", "Glu", "E", 3));
		aminoAcids.put("F", new AminoAcid("Phenylalanine", "Phe", "F", 4));
	    aminoAcids.put("G", new AminoAcid("Glycine", "Gly", "G", 5));
	    aminoAcids.put("H", new AminoAcid("Histidine", "His", "H", 6));
	    aminoAcids.put("I", new AminoAcid("Isoleucine", "Ile", "I", 7));
	    aminoAcids.put("K", new AminoAcid("Lysine", "Lys", "K", 8));
	    aminoAcids.put("L", new AminoAcid("Leucine", "Leu", "L", 9));
	    aminoAcids.put("M", new AminoAcid("Methionine", "Met", "M", 10));
	    aminoAcids.put("N", new AminoAcid("Asparagine", "Asn", "N", 11));
	    aminoAcids.put("P", new AminoAcid("Proline", "Pro", "P", 12));    
	    aminoAcids.put("Q", new AminoAcid("Glutamine", "Gln", "Q", 13));
	    aminoAcids.put("R", new AminoAcid("Arginine", "Arg", "R", 14));
	    aminoAcids.put("S", new AminoAcid("Serine", "Ser", "S", 15));
	    aminoAcids.put("T", new AminoAcid("Threonine", "Thr", "T", 16));
	    aminoAcids.put("V", new AminoAcid("Valine", "Val", "V", 17));
	    aminoAcids.put("W", new AminoAcid("Tryptophan", "Trp", "W", 18));
	    aminoAcids.put("Y", new AminoAcid("Tyrosine", "Tyr", "Y", 19));
	    aminoAcids.put("X", new AminoAcid("Unknown amino acid", "Xaa", "X", 25));
	    aminoAcids.put("*", new AminoAcid("Stop codon", " * ","*", 27));
	    aminoAcids.put("-", new AminoAcid("Gap", " - ","-", 29));
    
	}
	*/
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		StringBuilder b = new StringBuilder(1000000);
		for(int n = 0; n < 1000000; n++){
			b.append(AminoAcid.A.getCodeCharVal());
			b.append(AminoAcid.E.getCodeCharVal());
			b.append(AminoAcid.C.getCodeCharVal());
		}
		b.toString();
		
		long endTime = System.nanoTime();
		long timeSpent = endTime - startTime;
		double timeSpentMS = (double)timeSpent / 1000000000.0;
		logger.info("getCode took " + (timeSpentMS) + " millisec");
	}

	public AminoAcid(String name, String threeLetterName, String code, int intVal) {
		this.name = name;
		this.threeLeterName = threeLetterName;
		this.code = code;
		this.intVal = intVal;
		this.codeByteArray = code.getBytes();
		this.codeByteVal = (byte) code.charAt(0);
		this.codeCharVal = code.charAt(0);
	}
	
	public static AminoAcid getAminoAcidFromCodon(byte[] codon) {
		return getAminoAcidFromCodon(codon, GeneticCode.DEFAULT);
	}

	public static AminoAcid getAminoAcidFromCodon(byte[] codon, GeneticCode geneticCode) {
		
		if(codon == null || codon.length <3){
			return AminoAcid.GAP;
		}
			
		int baseVal0 = NucleotideUtilities.baseValFromChar((char)codon[0]);
		int baseVal1 = NucleotideUtilities.baseValFromChar((char)codon[1]);
		int baseVal2 = NucleotideUtilities.baseValFromChar((char)codon[2]);
		
		int position1Val;
		if(baseVal0 == NucleotideUtilities.T){
			position1Val = 0;
		}else if(baseVal0 == NucleotideUtilities.C){
			position1Val = 1;
		}else if(baseVal0 == NucleotideUtilities.A){
			position1Val = 2;
		}else if(baseVal0 == NucleotideUtilities.G){
			position1Val = 3;
		}
		else{
			position1Val = -1;
		}
		
		int position2Val;
		if(baseVal1 == NucleotideUtilities.T){
			position2Val = 0;
		}else if(baseVal1 == NucleotideUtilities.C){
			position2Val = 1;
		}else if(baseVal1 == NucleotideUtilities.A){
			position2Val = 2;
		}else if(baseVal1 == NucleotideUtilities.G){
			position2Val = 3;
		}
		else{
			position2Val = -1;
		}
		
		int position3Val;
		if(baseVal2 == NucleotideUtilities.T){
			position3Val = 0;
		}else if(baseVal2 == NucleotideUtilities.C){
			position3Val = 1;
		}else if(baseVal2 == NucleotideUtilities.A){
			position3Val = 2;
		}else if(baseVal2 == NucleotideUtilities.G){
			position3Val = 3;
		}
		else{
			position3Val = -1;
		}
		
		AminoAcid acid = X;
		
		if(position1Val == -1 || position2Val == -1 || position3Val == -1){
			acid = AminoAcid.X;
		}
		else{
			int acidVal = position1Val * 16 + position2Val * 4 + position3Val;
			acid = geneticCode.acidTranslation[acidVal];
		}
		
		if(baseVal0 == NucleotideUtilities.GAP && baseVal1 == NucleotideUtilities.GAP && baseVal2 == NucleotideUtilities.GAP){
			acid = AminoAcid.GAP;
		}
		/*
		if(baseVal0 == NucleotideUtilities.UNKNOWN || baseVal1 == NucleotideUtilities.UNKNOWN || baseVal2 == NucleotideUtilities.UNKNOWN){
			acid = AminoAcid.X;
		}
		*/
		
		return acid;	
	}

	public byte getCodeByteVal() {
		return codeByteVal;
	}
	
	public static final AminoAcid getAminoAcidFromByte(byte base){
		return getAminoAcidFromChar((char)base);
	}
	
	public static final AminoAcid getAminoAcidFromChar(char base){

			AminoAcid acid;

			switch (base) {

			case 'A': case 'a':
				acid = AminoAcid.A;
				break;
			case 'C': case 'c':
				acid = AminoAcid.C;
				break;
			case 'D': case 'd':
				acid = AminoAcid.D;
				break;
			case 'E': case 'e':
				acid = AminoAcid.E;
				break;
			case 'F': case 'f':
				acid = AminoAcid.F;
				break;
			case 'G': case 'g':
				acid = AminoAcid.G;
				break;
			case 'H': case 'h':
				acid = AminoAcid.H;
				break;
			case 'I': case 'i':
				acid = AminoAcid.I;
				break;
			case 'K': case 'k':
				acid = AminoAcid.K;
				break;
			case 'L': case 'l':
				acid = AminoAcid.L;
				break;
			case 'M': case 'm':
				acid = AminoAcid.M;
				break;
			case 'N': case 'n':
				acid = AminoAcid.N;
				break;
			case 'P': case 'p':
				acid = AminoAcid.P;
				break;
			case 'Q': case 'q':
				acid = AminoAcid.Q;
				break;
			case 'R': case 'r':
				acid = AminoAcid.R;
				break;
			case 'S': case 's':
				acid = AminoAcid.S;
				break;
			case 'T': case 't':
				acid = AminoAcid.T;
				break;
			case 'V': case 'v':
				acid = AminoAcid.V;
				break;
			case 'W': case 'w':
				acid = AminoAcid.W;
				break;
			case 'X': case 'x': case '?':
				acid = AminoAcid.X;
				break;
			case 'Y': case 'y':
				acid = AminoAcid.Y;
				break;
			case '*':
				acid = AminoAcid.STOP;
				break;	
			case '-': case '_':
				acid = AminoAcid.GAP;
				break;	
			default:
				acid = AminoAcid.X;
				break;
			}

			return acid;

		}

	public char getCodeCharVal() {
		return codeCharVal;
	}
	
	public String getCodeStringVal() {
		return code;
	}

	public byte[] getCodeByteArray() {
		return codeByteArray;
	}

	public static boolean isGap(byte byteVal) {
		return AminoAcid.GAP == getAminoAcidFromByte(byteVal);
	}

	public static byte getConsensusFromByteVal(byte byteVal1, byte byteVal2) {
		
		if(AminoAcid.getAminoAcidFromByte(byteVal1) == AminoAcid.getAminoAcidFromByte(byteVal2)){
			return byteVal1;
		}
		else{
			// if one is gap - return the one that is not
			if(AminoAcid.isGap(byteVal1) || AminoAcid.isGap(byteVal2)){
				if(isGap(byteVal1)){
					return byteVal2;
				}else{
					return byteVal1;
				}
			}
			else{
				return AminoAcid.X.getCodeByteVal();
			}
		}
	}
}
