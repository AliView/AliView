package aliview.sequences;

import java.util.Arrays;

import org.apache.log4j.Logger;

public final class SequenceUtils {
	private static final Logger logger = Logger.getLogger(SequenceUtils.class);
	public static final byte GAP_SYMBOL = (byte) '-';
	public static int TYPE_AMINO_ACID = 0;
	public static int TYPE_NUCLEIC_ACID = 1;
	public static int TYPE_UNKNOWN = 2;
	public static int id_counter;
	
	
	public static byte[] createGapByteArray(int length) {
		byte[] byteSeq = new byte[length];
		Arrays.fill(byteSeq, SequenceUtils.GAP_SYMBOL);
		return byteSeq;
	}
	
	public static int createID() {
	//	logger.info("create ID=" + id_counter);
		id_counter ++;
		return id_counter;
	}
}
