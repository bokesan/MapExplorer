/*
 * $Id: Line.java,v 1.2 2005/12/19 11:35:39 breitko Exp $
 * 
 * This file is part of Map Explorer.
 * 
 * Copyright © 2005 Christoph Breitkopf
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
 * A line segment.
 * <p>
 * Start and end point are always ordered.
 * 
 * @author Christoph Breitkopf
 */
public class Line {

    private final Point start;
    private final Point end;

    public Line(Point start, Point end) {
	if (start.compareTo(end) <= 0) {
	    this.start = start;
	    this.end = end;
	} else {
	    this.start = end;
	    this.end = start;
	}
    }

    /**
     * Compute line intersection
     * @param ln a line
     * @return true if this line intersects line ln, false otherwise.
     */
    public IntersectionResult intersects(Line ln) {
	/*
	if (start.equals(ln.start) || start.equals(ln.end))
	    return IntersectionResult.at(start);
	if (end.equals(ln.start) || end.equals(ln.end))
	    return IntersectionResult.at(end);
	  */  
	final double x1 = start.getX();
	final double y1 = start.getY();
	final double x2 = end.getX();
	final double y2 = end.getY();
	final double x3 = ln.start.getX();
	final double y3 = ln.start.getY();
	final double x4 = ln.end.getX();
	final double y4 = ln.end.getY();
	
	final double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
	final double d1 = y1 - y3;
	final double d2 = x1 - x3;
	final double num_Ua = (x4 - x3) * d1 - (y4 - y3) * d2;
	final double num_Ub = (x2 - x1) * d1 - (y2 - y1) * d2;
	
	if (denom == 0 && num_Ua == 0 && num_Ub == 0) {
	    return IntersectionResult.coincident();
	} else if (denom == 0) {
	    return IntersectionResult.parallel();
	} else {
	    /*
	     * Substituting either of these into the corresponding equation for the line gives the intersection point. For example the intersection point (x,y) is
	    x = x1 + ua (x2 - x1)

	    y = y1 + ua (y2 - y1)

	    Notes:

	        * The denominators for the equations for ua and ub are the same.

	        * If the denominator for the equations for ua and ub is 0 then the
	        * two lines are parallel.

	        * If the denominator and numerator for the equations for ua and ub are 0
	        * then the two lines are coincident.

	        * The equations apply to lines, if the intersection of line segments is
	        * required then it is only necessary to test if ua and ub lie
	        * between 0 and 1. Whichever one lies within that range then the
	        * corresponding line segment contains the intersection point.
	        * If both lie within the range of 0 to 1 then the intersection
	        * point is within both line segments. 
	     */
	    double ua = num_Ua / denom;
	    double ub = num_Ub / denom;
	    double x = x1 + ua * (x2 - x1);
	    double y = y1 + ua * (y2 - y1);
	    if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1)
		return IntersectionResult.at(new Point(x,y));
	    return IntersectionResult.outside(new Point(x,y));
	}
    }
    
    
    /**
     * Extend line by another.
     * @param ln a Line
     * @return a new line, or null.
     */
    public Line extend(Line ln) {
	Direction d = getDirection();
	if (d.equals(ln.getDirection())) {
	    if (end.equals(ln.start))
		return new Line(start, ln.end);
	    if (start.equals(ln.end))
		return new Line(ln.start, end);
	}
	return null;
    }

    private Direction getDirection() {
	if (isVertical()) {
	    return Direction.NORTH;
	}
	assert isHorizontal();
	return Direction.EAST;
    }
    
    
    /**
     * The End.
     * @return Returns the end.
     */
    public Point getEnd() {
        return end;
    }

    /**
     * The start.
     * @return Returns the start.
     */
    public Point getStart() {
        return start;
    }
    
    public boolean isHorizontal() {
	return start.getY() == end.getY();
    }
    
    public boolean isVertical() {
	return start.getX() == end.getX();
    }
    
    

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Line))
	    return false;
	Line ln2 = (Line) obj;
	return start.equals(ln2.start) && end.equals(ln2.end);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return 3 * start.hashCode() + end.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Line{" + start + "," + end + "}";
    }

}
