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
 * Result of a line intersection test.
 * <p>
 * This class has no public constructor. Instead the factory methods
 * <code>at</code>, <code>outside</code>, <code>coincident</code>, and <code>parallel</code> are used to create
 * instances.
 * 
 * @author Christoph Breitkopf
 */
public class IntersectionResult {
    
    /**
     * Factory method returning a new IntersectionResult for conincident lines.
     * @return Returns a new IntersectionResult for which isCoincident() will return true.
     * @see #isCoincident()
     */
    public static IntersectionResult coincident() {
	return S_COINCIDENT;
    }
    
    /**
     * Factory method returning a new IntersectionResult for parallel lines.
     * @return Returns a new IntersectionResult for which isParallel() will return true.
     * @see #isParallel()
     */
    public static IntersectionResult parallel() {
	return S_PARALLEL;
    }
    
    /**
     * Factory method returning a new IntersectionResult for a normal line intersection.
     * 
     * @param p a Point
     * @return Returns a new IntersectionResult for which isIntersection() will return true
     * and getIntersection() will return p.
     * @see #isIntersection()
     * @see #getIntersection()
     */
    public static IntersectionResult at(Point p) {
	return new IntersectionResult(INTERSECTION, p);
    }
    
    /**
     * Factory method returning a new IntersectionResult where the intersection 
     * point lies outside the line segments.
     * 
     * @param p a Point
     * @return Returns a new IntersectionResult for which isOutsideIntersection() will return true
     * and getIntersection() will return p.
     * @see #isOutsideIntersection()
     * @see #getIntersection()
     */
    public static IntersectionResult outside(Point p) {
	return new IntersectionResult(OUTSIDE, p);
    }
    
    private static final int INTERSECTION = 0;
    private static final int COINCIDENT = 1;
    private static final int PARALLEL = 2;
    private static final int OUTSIDE = 3;

    private static final IntersectionResult S_COINCIDENT = new IntersectionResult(COINCIDENT);
    private static final IntersectionResult S_PARALLEL = new IntersectionResult(PARALLEL);
    
    private final int type;
    private final Point intersection;
    
    private IntersectionResult(int type, Point intersection) {
	assert intersection != null;
	this.type = type;
	this.intersection = intersection;
    }
    
    private IntersectionResult(int type) {
	this.type = type;
	this.intersection = null;
    }

    /**
     * Determine whether the lines were parallel.
     * @return true if the lines were parallel.
     */
    public boolean isParallel() {
	return type == PARALLEL;
    }
    
    /**
     * Determine whether the lines were conincident.
     * @return true if the lines were coincident.
     */
    public boolean isCoincident() {
	return type == COINCIDENT;
    }
    
    /**
     * Determine whether the lines intersected normally.
     * <p>
     * The intersection point can be retrieved with getIntersection().
     * 
     * @return true if the lines intersected normally.
     * @see #getIntersection()
     */
    public boolean isIntersection() {
	return type == INTERSECTION;
    }
    
    /**
     * Determine whether the intersection point was outside of the line segments.
     * <p>
     * The intersection point can be retrieved with getIntersection().
     * 
     * @return true if the lines intersected at a point outside of the line segments. 
     * @see #getIntersection()
     */
    public boolean isOutsideIntersection() {
	return type == OUTSIDE;
    }
    
    /**
     * The intersection point.
     * <p>
     * For parallel or coincident lines, this method returns null.
     * 
     * @return The intersection point.
     */
    public Point getIntersection() {
	return intersection;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	switch (type) {
	case INTERSECTION: return "IntersectionResult{INTERSECTION," + intersection + "}";
	case OUTSIDE: return "IntersectionResult{OUTSIDE," + intersection + "}";
	case PARALLEL: return "IntersectionResult{PARALLEL}";
	case COINCIDENT: return "IntersectionResult{COINCIDENT}";
	default:
	    throw new AssertionError(type);
	}
    }
    
}
