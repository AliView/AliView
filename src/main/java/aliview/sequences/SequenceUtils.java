package aliview.sequences;

import java.util.Arrays;

public final class SequenceUtils {
	
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
		id_counter ++;
		return id_counter;
	}
}
