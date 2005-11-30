package de.bokeh.ddm.mapexplorer;

/**
 * Creature size categories.
 * @author Christoph Breitkopf
 */
public enum CreatureSize {
    
    TINY, SMALL, MEDIUM, LARGE, HUGE, GARGANTUAN, COLOSSAL;
    
    /**
     * Size in number of map squares.
     * @return The size in map squares for this size category.
     */
    public int sizeSquares() {
	switch (this) {
	case LARGE: return 2;
	case HUGE: return 3;
	case GARGANTUAN: return 4;
	case COLOSSAL: return 6;
	default: return 1;
	}
    }
	
}
