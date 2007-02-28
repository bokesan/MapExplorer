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

import java.util.*;
import java.util.logging.*;


/**
 * Compute line-of-sight between map squares.
 * 
 * @author Christoph Breitkopf
 */
public class LosTester {

    private final Logger logger;
    
    private final Location location;
    private final double x;
    private final double y;
    
    private final int width;
    private final int height;
    
    private final Set<Line> allWalls;
    private Line[] walls;
    private boolean haveDiagonalWalls;
    private final Set<Location> allForestSquares;
    private Location[] forestSquares;
    
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
    public LosTester(Location loc, Dimension size, Set<Line> walls, Set<Location> forestSquares, int rndTests, Logger logger) {
	this.location = loc;
	this.rndTests = rndTests;
	x = loc.x();
	y = loc.y();
	width = size.getWidth();
	height = size.getHeight();
	this.allWalls = walls;
	this.allForestSquares = forestSquares;
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

    // protected for testing only
    protected static double[] makeTestOffsets(int steps) {
	assert steps > 0 && Integer.bitCount(steps) == 1;
	double[] offs = new double[steps + (steps < 512 ? 3 : 1)];
	final double frac = 1 / (double) steps;
	offs[0] = 0.0;
	offs[1] = 1.0;
	final int half = steps / 2;
	for (int i = 1; i < half; i++) {
	    offs[2 * i] = i * frac;
	    offs[2 * i + 1] = (steps - i) * frac;
	}
	if (steps < 512) {
	    offs[offs.length - 3] = 1.0 / 512.0;
	    offs[offs.length - 2] = 511.0 / 512.0;
	}
	offs[offs.length - 1] = 0.5;
	return offs;
    }

    private static final int TEST_STEPS_NORMAL = 32;
    private static final int TEST_STEPS_FINE = 64;
    
    /**
     * Test two diagonals
     * @param loc target Location
     * @param slope the slope. 0: ascending, 1: descending
     * @return Something
     */
    private int testEdges(Location loc, int slope) {
	getRelevantWalls(loc);
	
	final double x1 = location.getColumn();
	final double x2 = loc.getColumn();
	
        final double[] testOffsets = makeTestOffsets(haveDiagonalWalls ? TEST_STEPS_FINE : TEST_STEPS_NORMAL);
	if (slope == 0) {
	    // ascending
	    final double y1 = location.getRow();
	    final double y2 = loc.getRow();
	    for (double e1off : testOffsets) {
		for (double e2off : testOffsets) {
		    if (los(x1 + e1off, y1 + e1off, x2 + e2off, y2 + e2off)) {
			return 0;
		    }
		}
	    }
	    // random tests
	    for (int i = 1; i <= rndTests; i++) {
		final double e1off = rng.nextDouble();
		final double e2off = rng.nextDouble();
		if (los(x1 + e1off, y1 + e1off, x2 + e2off, y2 + e2off)) {
		    Point p1 = new Point(x1 + e1off, y1 + e1off);
		    Point p2 = new Point(x2 + e2off, y2 + e2off);
		    logger.warning("Found random " + p1.getLocation() + " - " + p2.getLocation()
                                   + " [" + p1 + ", " + p2 + "]");
		    return i;
		}
	    }
	} else {
	    // descending
	    final double y1 = location.getRow() + 1;
	    final double y2 = loc.getRow() + 1;
	    for (double e1off : testOffsets) {
		for (double e2off : testOffsets) {
		    if (los(x1 + e1off, y1 - e1off, x2 + e2off, y2 - e2off)) {
			return 0;
		    }
		}
	    }
	    // random tests
	    for (int i = 1; i <= rndTests; i++) {
		double e1off = rng.nextDouble();
		double e2off = rng.nextDouble();
		if (los(x1 + e1off, y1 - e1off, x2 + e2off, y2 - e2off)) {
		    Point p1 = new Point(x1 + e1off, y1 - e1off);
		    Point p2 = new Point(x2 + e2off, y2 - e2off);
                    logger.warning("Found random " + p1.getLocation() + " - " + p2.getLocation()
                                   + " [" + p1 + ", " + p2 + "]");
		    return i;
		}
	    }
	}
	
	return -1;
    }
    
    
    private boolean los(double x1, double y1, double x2, double y2) {
	for (Line wall : walls) {
	    if (wall.intersectsOrCoincides(x1, y1, x2, y2))
		return false;
	}
	if (forestSquares != null) {
	    Line line = new Line(new Point(x1, y1), new Point(x2, y2));
	    for (Location sq : forestSquares) {
		if (line.intersectsSquareAsPerForest(sq))
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

    
    private void getRelevantWalls(Location dest) {
	Rectangle bounds = new Rectangle(location, dest);
	HashSet<Line> walls = new HashSet<Line>();
        haveDiagonalWalls = false;
	for (Line w : allWalls) {
            if (w.intersects(bounds)) {
                walls.add(w);
                if (w.isDiagonal())
                    haveDiagonalWalls = true;
            }
	}
	this.walls = walls.toArray(new Line[walls.size()]);
	
	if (dest.isNeighborOf(location))
	    this.forestSquares = null;
	else {
	    HashSet<Location> forestSquares = new HashSet<Location>();
	    for (Location sq : allForestSquares) {
		if (bounds.contains(sq) && !sq.equals(dest) && !sq.equals(location))
		    forestSquares.add(sq);
	    }
	    if (forestSquares.size() == 0)
		this.forestSquares = null;
	    else
		this.forestSquares = forestSquares.toArray(new Location[forestSquares.size()]);
	}
    }
}
