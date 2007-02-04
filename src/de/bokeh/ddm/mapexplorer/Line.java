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
 * A line segment.
 * <p>
 * Start and end point are always ordered to simplify the implementation
 * of the <code>equals</code> and <code>extend</code> methods.
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
    
    public Line(double x1, double y1, double x2, double y2) {
        this(new Point(x1, y1), new Point(x2, y2));
    }

    /**
     * Compute line intersection.
     * @param ln a line
     * @return A new IntersectionResult for this line and line ln.
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
	    return resolveCoincident(ln);
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
	    Point pt = new Point(x1 + ua * (x2 - x1), y1 + ua * (y2 - y1));
	    if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1)
		return IntersectionResult.at(pt);
	    return IntersectionResult.outside(pt);
	}
    }
    

    /**
     * Compute line intersection.
     * <p>
     * This could be defined as:
     * <pre>
     *    IntersectionResult r = intersects(ln);
     *    return r.isIntersection() || r.isCoincident();
     * </pre>
     * <p>
     * However, it is completely inlined to avoid creating a new objects
     * for the intersection points.
     * 
     * @param ln a line
     * @return true if ln intersects or coincides with this line.
     */
    public boolean intersectsOrCoincides(Line ln) {
	return intersectsOrCoincides(ln.start.getX(), ln.start.getY(), ln.end.getX(), ln.end.getY());
    }
    
    /**
     * Compute line intersection.
     * <p>
     * This could be defined as:
     * <pre>
     *    IntersectionResult r = intersects(ln);
     *    return r.isIntersection() || r.isCoincident();
     * </pre>
     * <p>
     * However, it is completely inlined to avoid creating a new objects
     * for the intersection points.
     * 
     * @param x3 x coordinate of start point
     * @param y3 y coordinate fo start point
     * @param x4 x coordinate of end point
     * @param y4 y coordinate fo end point
     * @return true if ln intersects or coincides with this line.
     */
    public boolean intersectsOrCoincides(double x3, double y3, double x4, double y4) {
	final double x1 = start.getX();
	final double y1 = start.getY();
	final double x2 = end.getX();
	final double y2 = end.getY();
	
	final double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
	final double d1 = y1 - y3;
	final double d2 = x1 - x3;
	final double num_Ua = (x4 - x3) * d1 - (y4 - y3) * d2;
	final double num_Ub = (x2 - x1) * d1 - (y2 - y1) * d2;
	
	if (denom == 0 && num_Ua == 0 && num_Ub == 0) {
	    if (Point.compare(x3, y3, x4, y4) > 0)
		return coincidentWithOverlap(x1, y1, x2, y2, x4, y4, x3, y3);
	    return coincidentWithOverlap(x1, y1, x2, y2, x3, y3, x4, y4);
	} else if (denom == 0) {
	    return false;
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
	    if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1)
		return true;
	    return false;
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
     * Does this line contain the wall edge?
     * <p>
     * Edge must be either horizontal or vertical.
     * @param edge a Line
     * @return <code>true</code> if this line contains <code>edge</code>;
     *   <code>false</code> otherwise.
     */
    public boolean containsEdge(Line edge) {
        if (isVertical()) {
            if (edge.isVertical()) {
                return edge.start.getX() == start.getX() && edge.start.getY() >= start.getY() && edge.end.getY() <= end.getY();
            }
            return false;
        }
        if (isHorizontal()) {
            if (edge.isHorizontal()) {
                return edge.start.getY() == start.getY() && edge.start.getX() >= start.getX() && edge.end.getX() <= end.getX();
            }
        }
        return false;
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

    
    public boolean intersectsSquareAsPerForest(Location loc) {
	int x = loc.getColumn();
	int y = loc.getRow();

	boolean hadIntersection = intersectsOrCoincides(x, y, x+1, y);
	if (intersectsOrCoincides(x, y, x, y+1)) {
	    if (hadIntersection) return true;
	    hadIntersection = true;
	}
	if (intersectsOrCoincides(x+1, y, x+1, y+1)) {
	    if (hadIntersection) return true;
	    hadIntersection = true;
	}
	if (intersectsOrCoincides(x, y+1, x+1, y+1)) {
	    if (hadIntersection) return true;
	}
	return false;
    }
    
    private IntersectionResult resolveCoincident(Line ln) {
	final double d1 = start.distOrigSquare();
	final double d2 = end.distOrigSquare();
	final double d3 = ln.start.distOrigSquare();
	final double d4 = ln.end.distOrigSquare();
	
	double lo, hi;
	Point loPt, hiPt;
	if (d1 > d3) {
	    lo = d1;
	    loPt = start;
	} else {
	    lo = d3;
	    loPt = ln.start;
	}
	if (d2 < d4) {
	    hi = d2;
	    hiPt = end;
	} else {
	    hi = d4;
	    hiPt = ln.end;
	}
	if (lo > hi)
	    return IntersectionResult.outsideCoincident();
	if (lo < hi)
	    return IntersectionResult.coincident(new Line(loPt, hiPt));
	return IntersectionResult.coincident(loPt);
    }

    private static boolean coincidentWithOverlap(double x1, double y1, double x2, double y2,
	    double x3, double y3, double x4, double y4)
    {
	final double d1 = x1*x1 + y1*y1;
	final double d2 = x2*x2 + y2*y2;
	final double d3 = x3*x3 + y3*y3;
	final double d4 = x4*x4 + y4*y4;
	final double lo = (d1 > d3) ? d1 : d3;
	final double hi = (d2 < d4) ? d2 : d4;
	return lo <= hi;
    }

}
