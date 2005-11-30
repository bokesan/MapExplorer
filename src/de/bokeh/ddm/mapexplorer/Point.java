package de.bokeh.ddm.mapexplorer;

/**
 * Map locations used for LOS calculations.
 * <p>
 * Like Location, but with floating-point coordinates.
 * 
 * @author Christoph Breitkopf
 *
 */
public class Point implements Comparable {

    private final double x;
    private final double y;
    
    /**
     * Construct a new Point.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point(double x, double y) {
	this.x = x;
	this.y = y;
    }
    
    /**
     * Construct a new Point from a Location.
     * @param loc a Location
     */
    public Point(Location loc) {
	x = loc.x();
	y = loc.y();
    }

    /**
     * The x coordinate.
     * @return Returns the x coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * The y coordinate.
     * @return Returns the y coordinate.
     */
    public double getY() {
        return y;
    }

    public Location getLocation() {
	return new Location((int) x, (int) y);
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Point))
	    return false;
	Point p = (Point) obj;
	// FIXME: should use an epsilon here?
	return x == p.x && y == p.y;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return (int) (31 * x + 7 * y);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Point{" + x + ";" + y + "}";
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object o) {
	Point p = (Point) o;
	if (x < p.x)
	    return -1;
	if (x > p.x)
	    return 1;
	// x == p.x
	if (y < p.y)
	    return -1;
	if (y > p.y)
	    return 1;
	return 0;
    }
    
    
}
