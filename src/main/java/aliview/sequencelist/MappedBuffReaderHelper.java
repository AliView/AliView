package aliview.sequencelist;

import java.io.EOFException;
import java.io.IOException;

import sun.nio.cs.ext.ISCII91;

import it.unimi.dsi.io.ByteBufferInpStream;

public class MappedBuffReaderHelper {
	private ByteBufferInpStream mappedBuff;
	private byte lastRead;

	public MappedBuffReaderHelper(ByteBufferInpStream mappedBuff) {
		this.mappedBuff = mappedBuff;
	}

	public void setPosition(long position) {
		mappedBuff.position(position);	
	}
	
	public long findNext(byte target){
		while(true){
			if(target == mappedBuff.read()){
				return mappedBuff.position() - 1;
			}
		}
	}
	
	public long findNextOrEOF(byte target) {

			while(true){
				if(target == mappedBuff.read()){
					return mappedBuff.position() - 1;
				}
			}
	}
	
	private boolean isEOF(){
		return lastRead == -1;
	}

	private byte read() throws EOFException {
		lastRead = (byte)mappedBuff.read();
		if(lastRead == -1){
			throw new EOFException();
		}
		return lastRead;
	}
	
	
	public long appendBytesUntilNextLF(StringBuilder stringBuff) throws EOFException{
		while(true){
			int nextByte = read();
			if(isLF(nextByte)){
				return mappedBuff.position() - 1;
			}else{
				stringBuff.append((char)nextByte);
			}
		}
	}

	private boolean isReturnOrLF(int nextByte) {
		if(nextByte == '\n' || nextByte == '\r'){
			return true;
		}
		return false;
	}
	
	private boolean isLF(int nextByte) {
		if(nextByte == '\n'){
			return true;
		}
		return false;
	}
	
	
	
	private boolean isSpaceOrTab(int nextByte) {
		if(nextByte == ' ' || nextByte == '\t'){
			return true;
		}
		return false;
	}
	
	private boolean isWhiteOrLF(int nextByte){
		if(nextByte == ' ' || nextByte == '\t' || nextByte == '\r' || nextByte == '\n'){
			return true;
		}
		return false;
	}
	
	
	private boolean isSpace(int nextByte) {
		if(nextByte == ' '){
			return true;
		}
		return false;
	}

	public boolean hasNext() {
		return !isEOF();
	}

	public String readLine() throws EOFException {
		StringBuilder buff = new StringBuilder();
		appendBytesUntilNextLF(buff);
		return buff.toString();
	}

	public long posOfFirstNonWhiteCharAfterWhiteChar() throws EOFException{
		boolean whiteFound = false;
		long foundPos = -1;
		while(true){
			byte next = read();
			if(isSpaceOrTab(next)){
				whiteFound = true;
			}
			if(whiteFound && !isSpaceOrTab(next)){
				foundPos = mappedBuff.position() - 1;
				break;
			}
		}
		return foundPos;
	}
	
	public boolean hasLineOnlyOneContinousSpace() throws EOFException {
		byte previous = -1;
		int spaceCount = 0;
		while(true){
			byte next = read();
			if(isLF(next)){
				return spaceCount == 1;
			}
			if(isSpaceOrTab(next) && !isSpaceOrTab(previous)){
				spaceCount ++;
				if(spaceCount > 1){
					return false;
				}
			}
			previous = next;
		}
	}
	

	public long posOfNextNewline() throws EOFException {
		long foundPos = -1;
		while(true){
			byte next = read();
			if(isLF(next)){
				foundPos = mappedBuff.position() - 1;
				break;
			}
		}
		return foundPos;
		
	}

	public int countSpaceBetween(long seqStartPos, long firstNewlinePos) throws EOFException {
		mappedBuff.position(seqStartPos);
		int spaceCount = 0;
		long len = firstNewlinePos - seqStartPos;
		for(int n = 0; n < len; n++){
			byte next = read();
			if(isSpace(next)){
				spaceCount ++;
			}
		}
		return spaceCount;
	}
	
	public long posAtNSequenceCharacters(long seqStartPos, int countTarget) throws EOFException {
		mappedBuff.position(seqStartPos);
		
		int charCount = 0;
		while(charCount < countTarget){
			byte next = read();
			if(isWhiteOrLF(next) == false){
				charCount ++;
			}
		}
		return mappedBuff.position() - 1;
	}

	public boolean isNextLF() throws EOFException {
		byte next = read();
		return isReturnOrLF(next);
	}

	public String readString(long startPos, long endPos) throws IOException{
		long length = endPos - startPos;
		byte[] nameArray = new byte[(int)length];
		mappedBuff.position(startPos);
		mappedBuff.read(nameArray);
		return new String(nameArray);
	}

	public long posOfNextNonWhiteChar() throws EOFException {
		long foundPos = -1;
		while(true){
			byte next = read();
			if(! isWhiteOrLF(next)){
				foundPos = mappedBuff.position() - 1;
				break;
			}
		}
		return foundPos;
	}
}
