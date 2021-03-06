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

    private final double startX;
    private final double startY;
    private final double endX;
    private final double endY;
    
    public Line(Point start, Point end) {
        Point s, e;
	if (start.compareTo(end) <= 0) {
	    s = start;
	    e = end;
	} else {
	    s = end;
	    e = start;
	}
	startX = s.getX();
	startY = s.getY();
	endX = e.getX();
	endY = e.getY();
    }
    
    public Line(double x1, double y1, double x2, double y2) {
        if (Point.compare(x1, y1, x2, y2) <= 0) {
            startX = x1;
            startY = y1;
            endX = x2;
            endY = y2;
        } else {
            startX = x2;
            startY = y2;
            endX = x1;
            endY = y1;
        }
    }

    /**
     * Compute line intersection.
     * @param ln a line
     * @return A new IntersectionResult for this line and line ln.
     */
    public IntersectionResult intersects(Line ln) {
	double x1 = startX;
	double y1 = startY;
	double x2 = endX;
	double y2 = endY;
	double x3 = ln.startX;
	double y3 = ln.startY;
	double x4 = ln.endX;
	double y4 = ln.endY;
	
	double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
	double d1 = y1 - y3;
	double d2 = x1 - x3;
	double num_Ua = (x4 - x3) * d1 - (y4 - y3) * d2;
	double num_Ub = (x2 - x1) * d1 - (y2 - y1) * d2;
	
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
	return intersectsOrCoincides(ln.startX, ln.startY, ln.endX, ln.endY);
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
        double dx = endX - startX;
        double dy = endY - startY;
        double dx2 = x4 - x3;
        double dy2 = y4 - y3;
	
        double d1 = startY - y3;
        double d2 = startX - x3;
	double denom = dy2 * dx - dx2 * dy;
	double num_Ua = dx2 * d1 - dy2 * d2;
	double num_Ub = dx * d1 - dy * d2;
	
        if (denom > 0) {
            return (num_Ua >= 0 && num_Ua <= denom && num_Ub >= 0 && num_Ub <= denom);
        }
	if (denom < 0) {
            return (num_Ua <= 0 && num_Ua >= denom && num_Ub <= 0 && num_Ub >= denom);
	}
	// denom == 0
	if (num_Ua == 0 && num_Ub == 0) {
	    if (Point.compare(x3, y3, x4, y4) > 0)
	        return coincidentWithOverlap(startX, startY, endX, endY, x4, y4, x3, y3);
	    return coincidentWithOverlap(startX, startY, endX, endY, x3, y3, x4, y4);
	}
	return false;
    }
    
    /**
     * Extend line by another.
     * @param ln a Line
     * @return a new line, or null.
     */
    public Line extend(Line ln) {
        if (isVertical()) {
            if (ln.isVertical() && ln.startX == startX) {
                if (startY <= ln.startY) {
                    if (endY < ln.startY)
                        return null;
                    return new Line(startX, startY, startX, Math.max(endY, ln.endY));
                }
                if (ln.endY < startY)
                    return null;
                return new Line(ln.startX, ln.startY, startX, Math.max(endY, ln.endY));
            }
            return null;
        }
        if (isHorizontal()) {
            if (ln.isHorizontal() && ln.startY == startY) {
                if (startX <= ln.startX) {
                    if (endX < ln.startX)
                        return null;
                    return new Line(startX, startY, Math.max(endX, ln.endX), startY);
                }
                if (ln.endX < startX)
                    return null;
                return new Line(ln.startX, ln.startY, Math.max(endX, ln.endX), startY);
            }
            return null;
        }
	return null;
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
                return edge.startX == startX && edge.startY >= startY && edge.endY <= endY;
            }
            return false;
        }
        if (isHorizontal()) {
            if (edge.isHorizontal()) {
                return edge.startY == startY && edge.startX >= startX && edge.endX <= endX;
            }
        }
        return false;
    }
    
    
    /**
     * The End.
     * @return Returns the end.
     */
    public Point getEnd() {
        return new Point(endX, endY);
    }

    /**
     * The start.
     * @return Returns the start.
     */
    public Point getStart() {
        return new Point(startX, startY);
    }
    
    public boolean isHorizontal() {
	return startY == endY;
    }
    
    public boolean isVertical() {
	return startX == endX;
    }
    
    public boolean isDiagonal() {
        return startX != endX && startY != endY;
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Line))
	    return false;
	Line ln2 = (Line) obj;
	return startX == ln2.startX && startY == ln2.startY && endX == ln2.endX && endY == ln2.endY;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return (int) (17 * startX + (7 * startY + (23 * endX + 3 * endY)));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Line{" + getStart() + "," + getEnd() + "}";
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
    
    
    public boolean intersects(Rectangle r) {
        if (r.contains(startX, startY) || r.contains(endX, endY)) return true;
        if (this.intersectsOrCoincides(r.getEdge(Direction.NORTH))) return true;
        if (this.intersectsOrCoincides(r.getEdge(Direction.EAST))) return true;
        if (this.intersectsOrCoincides(r.getEdge(Direction.SOUTH))) return true;
        if (this.intersectsOrCoincides(r.getEdge(Direction.WEST))) return true;
        return false;
    }
    
    private IntersectionResult resolveCoincident(Line ln) {
	double d1 = getStart().distOrigSquare();
	double d2 = getEnd().distOrigSquare();
	double d3 = ln.getStart().distOrigSquare();
	double d4 = ln.getEnd().distOrigSquare();
	
	double lo, hi;
	Point loPt, hiPt;
	if (d1 > d3) {
	    lo = d1;
	    loPt = getStart();
	} else {
	    lo = d3;
	    loPt = ln.getStart();
	}
	if (d2 < d4) {
	    hi = d2;
	    hiPt = getEnd();
	} else {
	    hi = d4;
	    hiPt = ln.getEnd();
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
	double d1 = x1*x1 + y1*y1;
	double d2 = x2*x2 + y2*y2;
	double d3 = x3*x3 + y3*y3;
	double d4 = x4*x4 + y4*y4;
	double lo = (d1 > d3) ? d1 : d3;
	double hi = (d2 < d4) ? d2 : d4;
	return lo <= hi;
    }

}
