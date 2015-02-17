package aliview.sequences;

import aliview.AminoAcid;

public interface Bases {

	public abstract Bases getCopy();

	public abstract int getLength();

	public abstract byte get(int n);

	public abstract char charAt(int n);

	public abstract byte[] toByteArray();

	public abstract byte[] toByteArray(int startIndexInclusive,
			int endIndexInclusive);

	public abstract String toString();

	public abstract void set(int n, byte newBase);

	public abstract void insertAt(int n, byte[] newBytes);

	public abstract void replace(int startReplaceIndex, int stopReplaceIndex,
			byte[] insertBases);

	public abstract void delete(int[] toDelete);

	// ?????
	public abstract void complement();

	// ?????
	public abstract void reverse();

	// convenience method
	public abstract void set(int n, char c);

	// convenience
	public abstract void delete(int pos);

	// convenience
	public abstract void insertAt(int n, byte newByte);

	// convenience
	public abstract void append(byte[] newBytes);

	public abstract byte[] getBackend();

	public abstract void moveBaseLeft(int n);

	public abstract void moveBaseRight(int n);

}