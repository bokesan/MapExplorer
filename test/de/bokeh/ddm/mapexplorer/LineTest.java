package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class LineTest extends TestCase {

    public LineTest(String method) {
	super(method);
    }
    
    public void testConstructor() {
	Point p1 = new Point(1, 1);
	Point p2 = new Point(2, 2);
	Line ln1 = new Line(p1, p2);
	Line ln2 = new Line(p2, p1);
	
	assertEquals(p1, ln1.getStart());
	assertEquals(p2, ln1.getEnd());
	assertEquals(p1, ln2.getStart());
	assertEquals(p2, ln2.getEnd());
	assertTrue(ln1.equals(ln2));
    }
    
    
    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Line.intersects(Line)'
     */
    public void testIntersects() {
	Line g1 = new Line(new Point(1, 1), new Point(5, 1));
	Line g2 = new Line(new Point(1, 2), new Point(5, 2));
	Line g3 = new Line(new Point(2, 0), new Point(2, 5));
	
	IntersectionResult r = g1.intersects(g2);
	assertTrue(r.isParallel());
	
	r = g1.intersects(g3);
	assertTrue(r.isIntersection());
	assertEquals(new Point(2, 1), r.getIntersection());
	
	r = g1.intersects(new Line(new Point(2, 1), new Point(2.5, 1)));
	assertTrue(r.isCoincident());
	
	r = g1.intersects(new Line(new Point(1, 1), new Point(2, 2)));
	assertTrue(r.isIntersection());
	assertEquals(new Point(1, 1), r.getIntersection());
	
	r = g1.intersects(new Line(new Point(2, 2), new Point(2, 3)));
	assertTrue(r.isOutsideIntersection());
	assertEquals(new Point(2, 1), r.getIntersection());
    }

    /*
     * Test isHorizontal() and isVertical()
     */
    public void testOrientation() {
	Line hline = new Line(new Point(1,3), new Point(5, 3));
	Line vline = new Line(new Point(1,3), new Point(1, 8));
	Line sline = new Line(new Point(1,3), new Point(5, 4));
	assertTrue(hline.isHorizontal());
	assertTrue(vline.isVertical());
	assertFalse(vline.isHorizontal());
	assertFalse(hline.isVertical());
	assertFalse(sline.isHorizontal());
	assertFalse(sline.isVertical());
    }
}
