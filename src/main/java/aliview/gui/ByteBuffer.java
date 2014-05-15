package aliview.gui;

public class ByteBuffer {
	byte[] buffer;
	int pointer = 0;
	
	public ByteBuffer(int size) {
		buffer = new byte[size];
	}
	
	public void clear(){
		pointer = 0;
	}
	
	public void append(int i){
		buffer[pointer] = (byte) i;
		pointer ++;
	}
	
	
	
	
	public int length(){
		return pointer;
	}

	public byte[] getArray() {
		return buffer;
	}

}
