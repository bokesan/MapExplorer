package de.bokeh.ddm.mapexplorer;

/**
 * A dimension of size width &times; height, specified in integer
 * units.
 * 
 * @author Christoph Breitkopf
 */
public class Dimension {

	private final int width;
	private final int height;
	
	/**
	 * Constructs and initializes a new Dimension of width and height.
	 * 
	 * @param width  the width
	 * @param height the height
	 */
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the height of the dimension.
	 * @return the height of this Dimension.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width of the dimension.
	 * @return the width of this Dimension.
	 */
	public int getWidth() {
		return width;
	}

}
