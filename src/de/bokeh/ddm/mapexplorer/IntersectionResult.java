/*
 * $Id: IntersectionResult.java,v 1.3 2005/12/19 11:35:18 breitko Exp $
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
 * Result of a line intersection test.
 * 
 * @author Christoph Breitkopf
 */
public class IntersectionResult {
    
    public static IntersectionResult coincident() {
	return new IntersectionResult(COINCIDENT);
    }
    
    public static IntersectionResult parallel() {
	return new IntersectionResult(PARALLEL);
    }
    
    public static IntersectionResult at(Point p) {
	return new IntersectionResult(INTERSECTION, p);
    }
    
    public static IntersectionResult outside(Point p) {
	return new IntersectionResult(OUTSIDE, p);
    }
    
    private static final int INTERSECTION = 0;
    private static final int COINCIDENT = 1;
    private static final int PARALLEL = 2;
    private static final int OUTSIDE = 3;
    
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

    public boolean isParallel() {
	return type == PARALLEL;
    }
    
    public boolean isCoincident() {
	return type == COINCIDENT;
    }
    
    public boolean isIntersection() {
	return type == INTERSECTION;
    }
    
    public boolean isOutsideIntersection() {
	return type == OUTSIDE;
    }
    
    /**
     * The intersection point.
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
