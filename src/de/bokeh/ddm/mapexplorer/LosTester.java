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
     * Check if Location col,row has LOS to x,y.
     * @param col the column
     * @param row the row
     * @return 0 if LOS was found with the normal tests. A number greater than
     * 0 if LOS was found after that many random tests. A negative number
     * if no LOS was found.
     */
    public int testLocation(int col, int row) {
	//final double[] off = { 0, 1.0/1024, 0.5, 1-1.0/1024, 1 };
	//final double[] off = { 0, 1/1024, 0.5, 1-1/1024, 1 };
	final double[] off = { 0, 1.0/1024, 1.0/8, 1.0/4, 0.5, 3.0/4, 1 - 1.0/8, 1 - 1.0/1024, 1 };
	
	getRelevantWalls(new Rectangle(location, new Location(col, row)));
	
	// normal tests
	for (double sxoff : off) {
	    for (double syoff : off) {
		for (double dxoff : off) {
		    for (double dyoff : off) {
			if (los(x + sxoff, y + syoff, col + dxoff, row + dyoff))
			    return 0;
		    }
		}
	    }
	}
	
	// random tests
	for (int i = 1; i <= rndTests; i++) {
	    double sx = x + rng.nextDouble();
	    double sy = y + rng.nextDouble();
	    double dx = col + rng.nextDouble();
	    double dy = row + rng.nextDouble();
	    if (los(sx, sy, dx, dy)) {
		logger.warning("Found random " + sx + "," + sy + " - " + dx + "," + dy + " (" + new Location((int)Math.floor(dx), (int)Math.floor(dy)) + ")");
		return i;
	    }
	}
	return -1;
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
		r = testEdges(loc, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH);
	    } else {
		// upper left quadrant
		// logger.info("source is in upper left quadrant");
		r = testEdges(loc, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.NORTH);
	    }
	} else {
	    if (location.y() < loc.y()) {
		// lower right quadrant
		// logger.info("source is in lower right quadrant");
		r = testEdges(loc, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH);
	    } else {
		// upper right quadrant
		// logger.info("source is in upper right quadrant");
		r = testEdges(loc, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.NORTH);
	    }
	}
	return r;
    }
    
    private int testEdges(Location loc, Direction locationDir1, Direction locationDir2,
	    Direction locDir1, Direction locDir2) {
	int r;
	r = testEdges(getEdge(location, locationDir1), getEdge(loc, locDir1));
	if (r >= 0) return r;
	r = testEdges(getEdge(location, locationDir1), getEdge(loc, locDir2));
	if (r >= 0) return r;
	r = testEdges(getEdge(location, locationDir2), getEdge(loc, locDir1));
	if (r >= 0) return r;
	r = testEdges(getEdge(location, locationDir2), getEdge(loc, locDir2));
	return r;
    }
    
    
    private int testEdges(Line e1, Line e2) {
	{
	    Range e1x = new Range(e1.getStart().getX(), e1.getEnd().getX());
	    Range e2x = new Range(e2.getStart().getX(), e2.getEnd().getX());
	    Range rx = e1x.extend(e2x);
	    Range e1y = new Range(e1.getStart().getY(), e1.getEnd().getY());
	    Range e2y = new Range(e2.getStart().getY(), e2.getEnd().getY());
	    Range ry = e1y.extend(e2y);
	    getRelevantWalls(new Rectangle(new Location((int) rx.getLowerBound() - 1, (int) ry.getLowerBound() - 1),
		    	                   new Location((int) rx.getHigherBound() + 1, (int) ry.getHigherBound() + 1)));
	}

	final double[] off = { 0, 1.0/1024, 1.0/8, 1.0/4, 0.5, 3.0/4, 1 - 1.0/8, 1 - 1.0/1024, 1 };

	for (double e1off : off) {
	    for (double e2off : off) {
		if (los(edgePoint(e1, e1off), edgePoint(e2, e2off))) {
		    return 0;
		}
	    }
	}

	// random tests
	for (int i = 1; i <= rndTests; i++) {
	    Point p1 = edgePoint(e1, rng.nextDouble());
	    Point p2 = edgePoint(e2, rng.nextDouble());
	    if (los(p1, p2)) {
		logger.warning("Found random " + p1 + " - " + p2);
		return i;
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
    
    private static Line getEdge(Location loc, Direction dir) {
	switch (dir) {
	case SOUTH:
	    return new Line(new Point(loc), new Point(loc.x() + 1, loc.y()));
	case WEST:
	    return new Line(new Point(loc), new Point(loc.x(), loc.y() + 1));
	case NORTH:
	    return new Line(new Point(loc.x(), loc.y() + 1), new Point(loc.x() + 1, loc.y() + 1));
	case EAST:
	    return new Line(new Point(loc.x() + 1, loc.y()), new Point(loc.x() + 1, loc.y() + 1));
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    private static Point edgePoint(Line edge, double offset) {
	Point start = edge.getStart();
	if (edge.isHorizontal())
	    return new Point(start.getX() + offset, start.getY());
	if (edge.isVertical())
	    return new Point(start.getX(), start.getY() + offset);
	throw new IllegalArgumentException();
    }
}
