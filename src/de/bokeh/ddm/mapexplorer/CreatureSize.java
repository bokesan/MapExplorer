package de.bokeh.ddm.mapexplorer;

/**
 * Creature size categories.
 * @author Christoph Breitkopf
 */
public enum CreatureSize {

	TINY, SMALL, MEDIUM, LARGE, HUGE;

	/**
	 * Size in number of map squares.
	 * @return The size in map squares for this size category.
	 */
	public int sizeSquares() {
		switch (this) {
		case LARGE: return 2;
		case HUGE: return 3;
		default: return 1;
		}
	}
	
}
