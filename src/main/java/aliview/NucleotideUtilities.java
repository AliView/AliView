package aliview;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class NucleotideUtilities {
	private static final Logger logger = Logger.getLogger(NucleotideUtilities.class);
	public static final int GAP = 0;
	public static final int A = 1;
	public static final int C = 2;
	public static final int G = 4;
	public static final int TU = 8;
	public static final int U = 8;
	public static final int T = 8;
	
	public static final int UNKNOWN = 32;
	public static final int R = A + G;
	public static final int Y = C + TU;
	public static final int M = C + A;
	public static final int K = TU + G;
	public static final int W = TU + A;
	public static final int S = C + G;
	public static final int B = C + TU + G;
	public static final int D = A + TU + G;
	public static final int H = A + TU + C;
	public static final int V = A + C + G;
	public static final int N = A + C + T + G;
	
	
	public static final int baseValFromBase(byte base){
		return baseValFromChar((char)base);
	}
	
	public static final byte complement(char base){
		byte complement;
		
		switch (base) {
		
		case 'A': 
			complement = 'T';
			break;
		case 'C':
			complement = 'G';
			break;
		case 'G':
			complement = 'C';
			break;
		case 'T': 
		case 'U':
			complement = 'A';
			break;
		case 'R': 
			complement = 'Y';
			break;
		case 'Y': 
			complement = 'R';
			break;
		case 'M': 
			complement = 'K';
			break;
		case 'K': 
			complement = 'S';
			break;
		case 'W': 
			complement = 'W';
			break;
		case 'S': 
			complement = 'S';
			break;
		case 'B': 
			complement = 'V';
			break;
		case 'D': 
			complement = 'H';
			break;
		case 'H':
			complement = 'D';
			break;
		case 'V':
			complement = 'B';
			break;
		case 'N':
			complement = 'N';
			break;
		case 'a':
			complement = 't';
			break;
		case 'c':
			complement = 'g';
			break;
		case 'g':
			complement = 'c';
			break;
		case 't':
		case 'u':
			complement = 'a';
			break;
		case 'r':
			complement = 'y';
			break;
		case 'y':
			complement = 'r';
			break;
		case 'm':
			complement = 'k';
			break;
		case 'k':
			complement = 's';
			break;
		case 'w': 
			complement = 'w';
			break;
		case 's': 
			complement = 's';
			break;
		case 'b': 
			complement = 'v';
			break;
		case 'd': 
			complement = 'h';
			break;
		case 'h': 
			complement = 'd';
			break;
		case 'v': 
			complement = 'b';
			break;
		case 'n': case '?': case '.':
			complement = 'n';
			break;	
		case '-':
			complement = '-';
			break;
		default:
			complement = (byte)base;
			break;
		}
		
		return complement;
	}
	
	public static final boolean isGap(byte base){
		if(baseValFromBase(base) == GAP){
			return true;
		}else{
			return false;
		}
	}
	
	public static final boolean isNucleoticeOrIUPAC(byte base){
		//logger.info("baseValFromBase(base)" + baseValFromBase(base));
		int baseVal = baseValFromBase(base); 
		if(baseVal != GAP && baseVal != UNKNOWN){
			return true;
		}else{
			return false;
		}
	}
	
	
	public static final int baseValFromChar(char base){

		int baseVal;

		switch (base) {

		case 'A': case 'a':
			baseVal = A;
			break;
		case 'C': case 'c':
			baseVal = C;
			break;
		case 'G': case 'g':
			baseVal = G;
			break;
		case 'T': case 't':
		case 'U': case 'u':
			baseVal = TU;
			break;
		case 'R': case 'r':
			baseVal = A + G;
			break;
		case 'Y': case 'y':
			baseVal = C + TU;
			break;
		case 'M': case 'm':
			baseVal = C + A;
			break;
		case 'K': case 'k':
			baseVal = TU + G;
			break;
		case 'W': case 'w': 
			baseVal = TU + A;
			break;
		case 'S': case 's': 
			baseVal = C + G;
			break;
		case 'B': case 'b': 
			baseVal = C + TU + G;
			break;
		case 'D': case 'd': 
			baseVal = A + TU + G;
			break;
		case 'H': case 'h': 
			baseVal = A + TU + C;
			break;
		case 'V': case 'v': 
			baseVal = A + C + G;
			break;
		case 'N': case 'n': case '?': case '.':
			baseVal = A + TU + C + G;
			break;	
		case '-': case '_': case ' ': case '\n': case '\r':
			baseVal = GAP;
			break;
		default:
			baseVal = UNKNOWN;
			break;
		}

		return baseVal;

	}
	
	public static byte byteFromBaseVal(int consensusVal) {
		char charVal = charFromBaseVal(consensusVal);
		//logger.info(charVal);
		return (byte) charVal;
	}

	public static final char charFromBaseVal(int baseVal){

		char base;

		switch (baseVal) {

		case A:
			base = 'A';
			break;
		case C: 
			base = 'C';
			break;
		case G:
			base = 'G';
			break;
		case TU:
			base = 'T';
			break;
		case A + G:
			base = 'R';
			break;
		case C + TU:
			base = 'Y';
			break;
		case C + A:
			base = 'M';
			break;
		case TU + G:
			base = 'K';
			break;
		case TU + A: 
			base = 'W';
			break;
		case C + G: 
			base = 'S';
			break;
		case C + TU + G: 
			base = 'B';
			break;
		case A + TU + G: 
			base = 'D';
			break;
		case A + TU + C: 
			base = 'H';
			break;
		case A + C + G: 
			base = 'V';
			break;
		case A + TU + C + G:
			base = 'N';
			break;	
		case GAP:
			base = '-';
			break;
		default:
			base = '?';
			break;
		}

		return base;

	}

	public static final int degenFoldFromChar(char base){

		int degenFold;

		switch (base) {

		case 'A': case 'a': case 'C': case 'c': case 'G': case 'g': case 'T': case 't':  case 'U': case 'u':
			degenFold = 1;
			break;
		case 'R': case 'r': case 'Y': case 'y': case 'M': case 'm': case 'K': case 'k': case 'W': case 'w': case 'S': case 's': 
			degenFold = 2;
			break;
		case 'B': case 'b': case 'D': case 'd': case 'H': case 'h': case 'V': case 'v':
			degenFold = 3;
			break;
		case 'N': case 'n': case '?':
			degenFold = 4;
			break;
		case '-':
			degenFold = 1;
			break;
		default:
			degenFold = 1;
			break;
		}
		
		return degenFold;


	}

	public static char[] nucleotideCharsFromBaseVal(int baseVal) {
		
		String nuces = "";
		
		if( (A & baseVal) == A){
			nuces += "A";
		}
		if( (C & baseVal) == C){
			nuces += "C";
		}
		if( (4 & baseVal) == 4){
			nuces += "G";
		}
		if( (8 & baseVal) == 8){
			nuces += "T";
		}
		
		return nuces.toCharArray();
	}
	

	
	public static ArrayList<String> regenerateDegenerated(String input) {
		ArrayList<String> sequences = new ArrayList<String>();
		sequences.add(input);
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "R", "AG");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "Y", "CT");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "M", "CA");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "K", "TG");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "W", "TA");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "S", "CG");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "B", "CTG");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "D", "ATG");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "H", "ATC");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "V", "TGC");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		// loop through sequences - new replaced are added to end of list and will be iterated through also
		for(int n = 0; n < sequences.size(); n++){
			String nextSeq = sequences.get(n);
			ArrayList<String> replaced = deUpac(nextSeq, "N", "ATGC");
			// if sequence contained code remove it from collection an add new ones
			if(replaced != null && replaced.size() > 0){
				sequences.remove(n);
				sequences.addAll(replaced);
				// lower index to start on the one that takes the place when one is removed
				n = n -1;
			}
		}
		
		return sequences;
	}
	
	private static ArrayList<String> deUpac(String input, String upac, String replace){
		ArrayList<String> replaced = null;
		if(input.indexOf(upac) != -1){
			replaced = new ArrayList<String>();
			for(int n = 0; n < replace.length(); n++){
				String replacedString  = input.replace(upac.charAt(0), replace.charAt(n));
				replaced.add(replacedString);
			//	replaced.add(StringUtils.replaceOnce(input,upac, replace.substring(n, n+1)));
			}
		}
		return replaced;	
	}
	/*
	private static ArrayList<String> deUpac(String input, String upac, String replace){
		ArrayList<String> replaced = null;
		if(input.indexOf(upac) != -1){
			replaced = new ArrayList<String>();
			for(int n = 0; n < replace.length(); n++){
				replaced.add(StringUtils.replaceOnce(input,upac, replace.substring(n, n+1)));
			}
		}
		return replaced;	
	}
	*/

	public static final String reverse(String input) {
		StringBuffer sb = new StringBuffer(input);
		String reverse = sb.reverse().toString();
		return reverse;
	}

	public static final String complement(String input) {
		String NORMAL =     "AaCcGgTtUuRrYyKkMmSsWwBbDdHhVvNn-";
		String COMPLEMENT = "TtGgCcAaAaYyRrMmKkSsWwVvHhDdBbNn-";
		
		String output = "";
		
		for(int n = 0; n < input.length(); n++){
			char normChar = input.charAt(n);
			int nuclIndex = NORMAL.indexOf(normChar);
			if(nuclIndex > -1){
				output += COMPLEMENT.charAt(nuclIndex);
			}
			// character not in list retain original
			else{
				output += normChar;
			}
		}
		
		return output;
		
	}

	public static final String revComp(String sequence) {
		return reverse( complement(sequence) );
	}
	
	public static boolean containsA(char input){
		int baseVal = baseValFromChar(input);
		if( (A & baseVal)  == A){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public static boolean containsC(char input){
		int baseVal = baseValFromChar(input);
		if( (C & baseVal)  == C){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public static boolean containsT(char input){
		int baseVal = baseValFromChar(input);
		if( (T & baseVal)  == T){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public static boolean containsG(char input){
		int baseVal = baseValFromChar(input);
		if( (G & baseVal)  == G){
			return true;
		}
		else{
			return false;
		}
		
	}

	
	public static int getDimerBinding(char n1Char, char n2Char) {
		int n1Val = baseValFromChar(n1Char);
		int n2Val = baseValFromChar(n2Char);
		
		
		int bindVal = 0;
		int bindCount = 0;
		
		// check
		if(containsA(n1Char) && containsT(n2Char)){
			bindVal += 2;
			bindCount ++;
		}
		
		if(containsT(n1Char) && containsA(n2Char)){
			bindVal += 2;
			bindCount ++;
		}
		
		if(containsC(n1Char) && containsG(n2Char)){
			bindVal += 4;
			bindCount ++;
		}
		
		if(containsG(n1Char) && containsC(n2Char)){
			bindVal += 4;
			bindCount ++;
		}
		
		int degenerateFold = degenFoldFromChar(n1Char) * degenFoldFromChar(n2Char);
		
		// is degenerate - calculate new bindval
		if(degenerateFold > 1){	
			bindVal = bindCount*2 / degenerateFold;
		}
		
		return bindVal;
		
	}
	
	public static final String[] seqToDeUPACStringArray(String sequence) {
	
		// create a new char-matrix to hold result
		char[][] matrix = new char[4][sequence.length()];
		for(char[] row: matrix){
			Arrays.fill(row, ' ');
		}
		
		for(int x = 0; x < sequence.length(); x++){
			
			char baseChar = sequence.charAt(x);
			
			int baseVal = baseValFromChar(baseChar);
			
			char[] nucleotidesInThisXpos = nucleotideCharsFromBaseVal(baseVal);
			
			// loop through all chars in this pos
			for(int y = 0; y < nucleotidesInThisXpos.length; y++){				
				matrix[y][x]=nucleotidesInThisXpos[y];					
			}
		}
		
		String[] stringArray = new String[4];
		for(int n = 0; n < matrix.length; n++){
			stringArray[n] = new String(matrix[n]);
		}
		
		return stringArray;
		
	}

	public static void complement(byte[] byteSeq) {
		for(int n = 0; n < byteSeq.length; n++){
			byteSeq[n] = complement((char)byteSeq[n]);
		}
	}

	public static byte getConsensusFromBases(byte base1, byte base2) {

			int baseVal1 = baseValFromBase(base1);
			int baseVal2 = baseValFromBase(base2);

			// Create consensus by bitwise OR of the bases in the same column
			int consensusVal = baseVal1 | baseVal2;

			// skip GAP
			/*
			if((consensusVal & NucleotideUtilities.GAP) == NucleotideUtilities.GAP){
				consensusVal = consensusVal - NucleotideUtilities.GAP;
			}
			 */

			byte consensus = byteFromBaseVal(consensusVal);
			return consensus;
		}


	public static byte getMinBase(byte base1, byte base2) {

		int baseVal1 = baseValFromBase(base1);
		int baseVal2 = baseValFromBase(base2);
		int minBaseVal = Math.min(baseVal1,baseVal2);

		return byteFromBaseVal(minBaseVal);
	}

	
	public static boolean isAtLeastOneGap(byte base1, byte base2) {
		if(isGap(base1) || isGap(base2)){
			return true;
		}
		else{
			return false;
		}
	}

	static byte[] allResidues = new byte[256];
	public static int baseValFromBaseOtherVer(byte residue) {
		return allResidues[residue];
	}
	
}
