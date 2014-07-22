package aliview.importer;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;


public class ByteBufferAutogrow {
	private static final Logger logger = Logger.getLogger(ByteBufferAutogrow.class);
	private ByteBuffer backend;
	
	private double ALLOCATE_MULTIPLIER = 1.5;
	
	public ByteBufferAutogrow(int initialCapacity) {
		backend = ByteBuffer.allocate(initialCapacity);
	}

	public void append(String more) {
		if(more == null || more.length() == 0){
			return;
		}
		
		byte[] moreBytes = more.getBytes();
		
		// Ensure size
		if(backend.remaining() < moreBytes.length){
			int exactSize = backend.position() + moreBytes.length;
			int newCapacity = (int) (exactSize * ALLOCATE_MULTIPLIER);
			reallocate(newCapacity);
			
		}
		
		backend.put(moreBytes);
		
	}
	
	public void ensureCapacity(int requestCapacity) {
		if(requestCapacity > backend.limit()){
			reallocate(requestCapacity);
		}
	}
	
	public void clear(){
		backend.clear();
	}
	
	
	/*
	 * 
	 * method inspired by org.deftserver.io.buffer;
	 * 
	 */
	// Preserves position.
	private void reallocate(int newCapacity) {
//		logger.info("reallocate");
		int oldPosition = backend.position();
		byte[] newBuffer = new byte[newCapacity];
		System.arraycopy(backend.array(), 0, newBuffer, 0, backend.position());
		backend = ByteBuffer.wrap(newBuffer);
		backend.position(oldPosition);
	}

	public byte[] getBytes(){
	    return Arrays.copyOfRange(backend.array(), 0, backend.position());    
	}
	
	public String toString(){
	    return new String(getBytes());    
	}

	public int position() {
		return backend.position();
	}

	
}
