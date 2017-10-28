package aliview.importer;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;


public class ByteBufferAutogrow {
	private static final Logger logger = Logger.getLogger(ByteBufferAutogrow.class);
	private ByteBuffer backend;

	private double ALLOCATE_MULTIPLIER = 1.5;
	private double ALLOCATE_MULTIPLIER_AFTER_100MB = 1.1;
	private double MB = 1000*1000;
	private boolean DIRECT_BUFF = false;

	public ByteBufferAutogrow(int initialCapacity) {
		if(DIRECT_BUFF){
			backend = ByteBuffer.allocateDirect(initialCapacity);
		}else{
			backend = ByteBuffer.allocate(initialCapacity);
		}
	}

	public void append(String more) {
		if(more == null || more.length() == 0){
			return;
		}

		byte[] moreBytes = more.getBytes();

		// Ensure size
		if(backend.remaining() < moreBytes.length){
			int exactSize = backend.position() + moreBytes.length;

			double multiplier = ALLOCATE_MULTIPLIER;
			if(exactSize > 100*MB ){
				multiplier = ALLOCATE_MULTIPLIER_AFTER_100MB;
			}
			int newCapacity = (int) (exactSize * multiplier);
			logger.info("newCap=" + newCapacity);
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

		if(backend.hasArray()){
			byte[] newBuffer = new byte[newCapacity];
			System.arraycopy(backend.array(), 0, newBuffer, 0, backend.position());
			backend = ByteBuffer.wrap(newBuffer);
			backend.position(oldPosition);
		}else{
			ByteBuffer newOne = ByteBuffer.allocateDirect(newCapacity);
			int endPos = backend.position();
			for(int n = 0; n < endPos; n++){
				newOne.put(backend.get(n));
			}
			backend.clear();
			backend = newOne;
		}
	}

	public byte[] getBytes(){
		if(backend.hasArray()){
			return Arrays.copyOfRange(backend.array(), 0, backend.position());  
		}else{
			int length = backend.position();
			byte[] retVal = new byte[length];
			for(int n = 0; n < retVal.length; n++){
				retVal[n] = backend.get(n);
			}
			return retVal;
		}
	}

	public String toString(){
		return new String(getBytes());    
	}

	public int position() {
		return backend.position();
	}


}
