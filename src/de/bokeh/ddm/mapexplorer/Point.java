/*
 * This file is part of Map Explorer.
 * 
 * Copyright © 2005-2007 Christoph Breitkopf
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including
 * commercial applications, and to alter it and redistribute it freely, subject to
 * the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not claim
 *      that you wrote the original software. If you use this software in a product,
 *      an acknowledgment in the product documentation would be appreciated but is
 *      not required.
 *
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *
 *   3. This notice may not be removed or altered from any source distribution.
 */

package de.bokeh.ddm.mapexplorer;

/**
 * Map locations used for LOS calculations.
 * <p>
 * Like Location, but with floating-point coordinates.
 * <p>
 * In Map Explorer, all points have x and y coordinates &gt;= 0.
 * The rest of the code, such as class <code>Line</code> depend on this,
 * and will break if this is changed.
 * 
 * @author Christoph Breitkopf
 *
 */
public class Point implements Comparable<Point> {

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
    public int compareTo(Point p) {
	return compare(x, y, p.x, p.y);
    }
    
    /**
     * Compare two points given by x and y coordinates.
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return Returns -1 if the first point is less than the second point,
     *  0 if the points are equal, and 1 if the first point is greater than the
     *  second point.
     */
    public static int compare(double x1, double y1, double x2, double y2) {
	if (x1 < x2)
	    return -1;
	if (x1 > x2)
	    return 1;
	if (y1 < y2)
	    return -1;
	if (y1 > y2)
	    return 1;
	return 0;
    }
    
    /**
     * Distance to origin, squared.
     * @return Returns the square of the distance to the origin.
     */
    public double distOrigSquare() {
	return x*x + y*y;
    }
}
