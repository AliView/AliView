package aliview.gui;

public class InvalidAlignmentPositionException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3246089512355077679L;
	//constructor without parameters
	public InvalidAlignmentPositionException() {}
	//constructor for exception description
	public InvalidAlignmentPositionException(String description)
	{
		super(description);
	}
}