package aliview.gui.pane;

public class RGBArray {

	public int[] backend;
	private int scanWidth;
	private int height;

	public RGBArray(int[] array, int scanWidth, int height) {
		this.scanWidth = scanWidth;
		this.backend = array;
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	public int getScanWidth() {
		return scanWidth;
	}

	public int[] getBackend() {
		return backend;
	}

	public int getLength() {
		return backend.length;
	}



}
