/*
 * $Id: LosTester.java,v 1.3 2005/12/19 11:34:53 breitko Exp $
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

import java.util.*;
import java.util.logging.*;


public class LosTester {

    private final Logger logger;
    
    private final Location location;
    private final double x;
    private final double y;
    
    private final int width;
    private final int height;
    
    private final Set<Line> allWalls;
    private Set<Line> walls;
    
    private final Random rng;
    private final int rndTests;
    
    /**
     * Construct a new LOS Tester.
     * @param loc the Location to test LOS from
     * @param size the size of the map
     * @param walls the walls
     * @param rndTests the number of random tests to perform
     * @param logger a Logger
     */
    public LosTester(Location loc, Dimension size, Set<Line> walls, int rndTests, Logger logger) {
	this.location = loc;
	this.rndTests = rndTests;
	x = loc.x();
	y = loc.y();
	width = size.getWidth();
	height = size.getHeight();
	this.allWalls = walls;
	rng = new Random();
	this.logger = logger;
    }
    
    /**
     * Check if Location loc has LOS to location.
     * @param loc a Location
     * @return 0 if LOS was found with the normal tests. A number greater than
     * 0 if LOS was found after that many random tests. A negative number
     * if no LOS was found.
     */
    public int testLocation(Location loc) {
	if (loc.equals(location))
	    return 0;
	if (loc.x() == location.x()) {
	    // same column
	    int botRow, topRow;
	    if (loc.y() < location.y()) {
		botRow = loc.y(); topRow = location.y();
	    } else {
		botRow = location.y(); topRow = loc.y();
	    }
	    int col = loc.getColumn();
	    for (Line w : allWalls) {
		if (w.isHorizontal()) {
		    double row = w.getStart().getY();
		    if (row > botRow && row <= topRow) {
			if (w.getStart().getX() <= col && w.getEnd().getX() >= (col+1)) {
			    // logger.info("same column, intervening walls: no LOS");
			    return -1;
			}
		    }
		}
	    }
	    // logger.info("same column, no intervening walls: LOS");
	    return 0;
	}
	if (loc.y() == location.y()) {
	    // same row
	    int leftCol, rightCol;
	    if (loc.x() < location.x()) {
		leftCol = loc.x(); rightCol = location.x();
	    } else {
		leftCol = location.x(); rightCol = loc.x();
	    }
	    int row = loc.getRow();
	    for (Line w : allWalls) {
		if (w.isVertical()) {
		    double col = w.getStart().getX();
		    if (col > leftCol && col <= rightCol) {
			if (w.getStart().getY() <= row && w.getEnd().getY() >= (row+1)) {
			    // logger.info("same row, intervening walls: no LOS");
			    return -1;
			}
		    }
		}
	    }
	    // logger.info("same row, no intervening walls: LOS");
	    return 0;
	}
	
	// different row/column
	int r;
	if (location.x() < loc.x()) {
	    if (location.y() < loc.y()) {
		// lower left quadrant
		// logger.info("source is in lower left quadrant");
		r = testEdges(loc, 1);
	    } else {
		// upper left quadrant
		// logger.info("source is in upper left quadrant");
		r = testEdges(loc, 0);
	    }
	} else {
	    if (location.y() < loc.y()) {
		// lower right quadrant
		// logger.info("source is in lower right quadrant");
		r = testEdges(loc, 0);
	    } else {
		// upper right quadrant
		// logger.info("source is in upper right quadrant");
		r = testEdges(loc, 1);
	    }
	}
	return r;
    }
    
    /**
     * Test two diagonals
     * @param loc target Location
     * @param slope the slope. 0: ascending, 1: descending
     * @return Something
     */
    private int testEdges(Location loc, int slope) {
	walls = allWalls;
	
	final double[] off = {
		0, 1,
		1/32.0, 31/32.0,
		2/32.0, 30/32.0,
		3/32.0, 29/32.0, //
		4/32.0, 28/32.0,
		5/32.0, 27/32.0, //
		6/32.0, 26/32.0,
		7/32.0, 25/32.0,
		8/32.0, 24/32.0,
		9/32.0, 23/32.0,
		10/32.0, 22/32.0,
		11/32.0, 21/32.0, //
		12/32.0, 20/32.0,
		13/32.0, 19/32.0, //
		14/32.0, 18/32.0,
		15/32.0, 17/32.0,
		1/512.0, 511/512.0,
		0.5
	};
	
	final double x1 = location.getColumn();
	final double x2 = loc.getColumn();
	if (slope == 0) {
	    // ascending
	    final double y1 = location.getRow();
	    final double y2 = loc.getRow();
	    for (double e1off : off) {
		for (double e2off : off) {
		    if (los(x1 + e1off, y1 + e1off, x2 + e2off, y2 + e2off)) {
			return 0;
		    }
		}
	    }
	    // random tests
	    for (int i = 1; i <= rndTests; i++) {
		final double e1off = rng.nextDouble();
		final double e2off = rng.nextDouble();
		Point p1 = new Point(x1 + e1off, y1 + e1off);
		Point p2 = new Point(x2 + e2off, y2 + e2off);
		if (los(p1, p2)) {
		    logger.warning("Found random " + p1 + " - " + p2);
		    return i;
		}
	    }
	} else {
	    // descending
	    final double y1 = location.getRow() + 1;
	    final double y2 = loc.getRow() + 1;
	    for (double e1off : off) {
		for (double e2off : off) {
		    if (los(x1 + e1off, y1 - e1off, x2 + e2off, y2 - e2off)) {
			return 0;
		    }
		}
	    }
	    // random tests
	    for (int i = 1; i <= rndTests; i++) {
		double e1off = rng.nextDouble();
		double e2off = rng.nextDouble();
		Point p1 = new Point(x1 + e1off, y1 - e1off);
		Point p2 = new Point(x2 + e2off, y2 - e2off);
		if (los(p1, p2)) {
		    logger.warning("Found random " + p1 + " - " + p2);
		    return i;
		}
	    }
	}
	
	return -1;
    }
    
    
    private boolean los(double x1, double y1, double x2, double y2) {
	return los(new Point(x1, y1), new Point(x2, y2));
    }
    
    private boolean los(Point p1, Point p2) {
	Line ln = new Line(p1, p2);
	for (Line wall : walls) {
	    IntersectionResult r = wall.intersects(ln);
	    if (r.isIntersection() || r.isCoincident()) {
		return false;
	    }
	}
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "LosTester{(" + x + ";" + y + "),(" + width + ";" + height + "),"
		+ allWalls.size() + " unique inside walls}";
    }

    
    private void getRelevantWalls(Rectangle bounds) {
	walls = new HashSet<Line>();
	double x1 = bounds.getLeft();
	double x2 = bounds.getRight() + 1;
	double y1 = bounds.getBottom();
	double y2 = bounds.getTop() + 1;
	for (Line w : allWalls) {
	    if (w.getStart().getX() <= x1 && w.getEnd().getX() <= x1)
		continue;
	    if (w.getStart().getX() >= x2 && w.getEnd().getX() >= x2)
		continue;
	    if (w.getStart().getY() <= y1 && w.getEnd().getY() <= y1)
		continue;
	    if (w.getStart().getY() >= y2 && w.getEnd().getY() >= y2)
		continue;
	    walls.add(w);
	}
    }
}
