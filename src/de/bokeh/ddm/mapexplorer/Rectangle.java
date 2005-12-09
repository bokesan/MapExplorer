package de.bokeh.ddm.mapexplorer;

public class Rectangle {

    private final int bottom;
    private final int top;
    private final int left;
    private final int right;
    
    /**
     * Constructs and initializes a Rectangle from two Locations.
     * @param e1 a Location
     * @param e2 a Location
     */
    public Rectangle(Location e1, Location e2) {
	int y1 = e1.y();
	int y2 = e2.y();
	if (y1 < y2) {
	    bottom = y1;
	    top = y2;
	} else {
	    bottom = y2;
	    top = y1;
	}

	int x1 = e1.x();
	int x2 = e2.x();
	if (x1 < x2) {
	    left = x1;
	    right = x2;
	} else {
	    left = x2;
	    right = x1;
	}
    }
    
    /**
     * Constructs and initializes a square Rectangle from a Location and a size.
     * @param e a Location
     * @param size the size of the square
     */
    public Rectangle(Location e, int size) {
	bottom = e.y();
	left = e.x();
	size--;
	top = bottom + size;
	right = left + size;
    }
    
    /**
     * Constructs and initializes square a Rectangle from a single Location.
     * @param p a Location
     */
    public Rectangle(Location p) {
	top = bottom = p.y();
	left = right = p.x();
    }
    
    public static Rectangle parse(String s) {
	int i = s.indexOf("-");
	if (i >= 0) {
	    return new Rectangle(new Location(s.substring(0, i)), new Location(s.substring(i+1)));
	}
	return new Rectangle(new Location(s));
    }

    /**
     * @return Returns the bottom.
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * @return Returns the left.
     */
    public int getLeft() {
        return left;
    }

    /**
     * @return Returns the right.
     */
    public int getRight() {
        return right;
    }

    /**
     * @return Returns the top.
     */
    public int getTop() {
        return top;
    }

    /**
     * Return the bottom-left square location.
     * @return the Location of the bottom-left square of this Rectangle.
     */
    public Location getBottomLeft() {
	return new Location(left, bottom);
    }

    /**
     * Return the top-right square location.
     * @return the Location of the top-right square of this Rectangle.
     */
    public Location getTopRight() {
	return new Location(right, top);
    }
    
    public int getWidth() {
	return right - left + 1;
    }
    
    public int getHeight() {
	return top - bottom + 1;
    }
    
    
    /**
     * @return Resturns all locations this Rectangle contains.
     */
    public Location[] getLocations() {
	Location[] locs = new Location[getWidth() * getHeight()];
	int i = 0;
	for (int row = bottom; row <= top; row++)
	    for (int col = left; col <= right; col++)
		locs[i++] = new Location(col, row);
	return locs;
    }
}
