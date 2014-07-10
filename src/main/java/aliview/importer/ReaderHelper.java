package aliview.importer;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/*
 * 
 * This method is inspired by Import Helper
 * 
 * 
 */
public class ReaderHelper {
	private static final Logger logger = Logger.getLogger(ReaderHelper.class);
	private BufferedReader reader;
	private int lastReadInt;
	private String nextLine;

	public ReaderHelper(BufferedReader reader){
		this.reader = reader;
	}
	
	public String getStringUntilNextSpaceOrTab() throws IOException{
		StringBuilder chars = new StringBuilder();
		boolean haveHadNonWhiteChars = false;
		while(true){
			char nextChar = readChar();
			if(isWhiteSpace(nextChar) && haveHadNonWhiteChars){
				break;
			}
			else{
				chars.append(nextChar);
				haveHadNonWhiteChars = true;
			}	
		}
		return chars.toString();
	}
	
	public String getStringFromNextPositions(int count) throws IOException {
		StringBuilder chars = new StringBuilder();
		for(int n = 0; n < count; n++){
			char nextChar = readChar();
			if(isPhylipNameChar(nextChar)){
				chars.append(nextChar);
			}else{
				// skip this one
			}
		}
		return chars.toString();
	}
	
	public String getNonWhiteCharsUntilNextLineOrEOF() throws IOException {
		StringBuilder chars = new StringBuilder();
		try {
			while(true){
				char nextChar = readChar();
				
				if(isNewline(nextChar)){
					break;
				}else if(isWhiteSpaceNotNewline(nextChar)){
					// skip
				}else{
					chars.append(nextChar);
				}
			}
		} catch (EOFException e) {
			// do nothing and return what is in buffer
		}
		return chars.toString();
	}
	
	public byte[] getNonWhiteBytes(int count) throws IOException{
		byte[] bytes = new byte[count];
		int nextInt;
		int counter = 0;
		while(counter < count){
			nextInt = read();
			if(isWhiteSpace(nextInt)){
				// skip
			}
			else{
				bytes[counter] = (byte)nextInt;
				counter ++;
			}
		}
		return bytes;
	}
	
	public String getNonWhiteCharsUntilNextOrEOF(char targetChar, int nextSeqSizeEstimate) throws IOException {
		StringBuilder chars = new StringBuilder(nextSeqSizeEstimate);
		try {
			while(true){
				char nextChar = readChar();
				
				if(nextChar == targetChar){
					break;
				}else if(isWhiteSpace(nextChar)){
					// skip
				}else{
					chars.append(nextChar);
				}
			}
		} catch (EOFException e) {
			// Do nothing - return what is in buffer
		}
		return chars.toString();	
	}

    public char readChar() throws IOException{
	    return (char) read();
	}
	
    public int read() throws IOException{
        lastReadInt = reader.read();
        if(lastReadInt == -1){
        	throw new EOFException();
        }
        return lastReadInt;
    }
    
    public boolean isEOF(){
    	return lastReadInt == -1;
    }
    
	public void skipPastNextline() throws IOException {
		while(readChar() != '\n');
	}

	public void skipPastNext(char c) throws IOException {
		while(readChar() != c);
	}
	
	public void readNextLine() throws IOException{
		nextLine = reader.readLine();
	}
	
	public String getNextLine() throws IOException{
		return nextLine;
	}
	
	public boolean isNextLineEOF() throws IOException{
		return (nextLine == null);
	}
	
	public boolean isNextLineContainingNonWhitespaceChars() throws IOException{
		if(isNextLineEOF()){
			return false;
		}
		return isStringContainingNonWhitespaceChars(nextLine);
	}
	
	public boolean isNextLineEmptyOrOnlyWhitespaceChars() throws IOException{
		if(isNextLineEOF()){
			return false;
		}
		return !isStringContainingNonWhitespaceChars(nextLine);
	}
	
	public boolean isNextLineStartingWithNonBlankChar(){
		if(nextLine != null){
			if(nextLine.length() > 0){
				if(!isWhiteSpace(nextLine.charAt(0))){
					return true;
				}
			}
		}
		return false;
	}
	
	public void readUntilNextNonBlankLine() throws IOException{
		readNextLine();
		while(isNextLineEmptyOrOnlyWhitespaceChars()){
			readNextLine();
		}
	}

	public boolean readUntilNextLineContains(String target) throws IOException {
		readNextLine();
		while(!isNextLineEOF()){
			if(StringUtils.containsIgnoreCase(nextLine, target)){
				return true;
			}
			readNextLine();
		}
		return false;
	}
	
	
	

	//
	//	Utility methods
	//
	
	
	private static boolean isPhylipNameChar(char c) {
		if(c == '\t' || c=='\r' || c=='\n'){
			return false;
		}
		else{
			return true;
		}
	}	
	
	private static boolean isWhiteSpace(int c) {
		return isWhiteSpace((char)c);
	}
	
	private static boolean isSpaceOrTab(char c) {
		if(c==' ' || c=='\t'){
			return true;
		}
		else{
			return false;
		}
	}
	
	private static boolean isWhiteSpace(char c) {
		if(c==' ' || c == '\t' || c=='\r' || c=='\n'){
			return true;
		}
		else{
			return false;
		}
	}
	
	private static boolean isWhiteSpaceNotNewline(char c) {
		if(c==' ' || c == '\t' || c=='\r'){
			return true;
		}
		else{
			return false;
		}
	}
	
	private static boolean isNewline(char c) {
		if(c=='\n'){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	
	public static boolean isStringContainingNonWhitespaceChars(String input){
		if(input == null){
			return false;
		}
		for(int n = 0; n < input.length(); n++){
			if(! isWhiteSpace(input.charAt(n))){
				return true;
			}
		}
		return false;
	}
	
	public static String removeSpace(String text) {
		if(text.indexOf(' ')>-1){
			text = StringUtils.remove(text, ' ');
		}
		return text;
	}
	
	public static String removeTab(String text) {
		if(text.indexOf('\t')>-1){
			text = StringUtils.remove(text, '\t');
		}
		return text;
	}


	public static String removeSpaceAndTab(String text) {
		text = removeSpace(text);
		text = removeTab(text);
		return text;
	}
	
	public static int indexOfFirstNonWhiteCharAfterWhiteChar(String text) {
		boolean whiteFound = false;
		int index = -1;
		for(int n = 0; n< text.length(); n++){
			if(isWhiteSpace(text.charAt(n))){
				whiteFound = true;
			}
			if(whiteFound && !isWhiteSpace(text.charAt(n))){
				index = n;
				break;
			}
		}
		return index;
	}
	
	public static int indexOfFirstNonWhiteChar(String text) {
		int index = -1;
		for(int n = 0; n< text.length(); n++){
			if(! isWhiteSpace(text.charAt(n))){
				index = n;
				break;
			}
		}
		return index;
	}
	
}
