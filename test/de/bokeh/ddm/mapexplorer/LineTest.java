package de.bokeh.ddm.mapexplorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class LineTest {

    @Test public void testConstructor() {
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
    
    
    private IntersectionResult intersect(Line ln1, Line ln2) {
	IntersectionResult r = ln1.intersects(ln2);
	assertEquals("intersect is symmetric", r, ln2.intersects(ln1));
	boolean r1 = ln1.intersectsOrCoincides(ln2);
	assertTrue("intersectsOrCoincides is symmetric", r1 == ln2.intersectsOrCoincides(ln1));
	assertTrue("intersectsOrCoincides matches intersects", r1 == (r.isIntersection() || r.isCoincident()));
	return r;
    }
    
    
    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Line.intersects(Line)'
     */
    @Test public void testIntersects() {
	final Line g1 = new Line(new Point(1, 1), new Point(5, 1));
	final Line g2 = new Line(new Point(1, 2), new Point(5, 2));
	final Line g3 = new Line(new Point(2, 0), new Point(2, 5));
	
	IntersectionResult r = intersect(g1, g2);
	assertTrue(r.isParallel());
	
	r = intersect(g1, g3);
	assertTrue(r.isIntersection());
	assertEquals(new Point(2, 1), r.getIntersection());
	
	r = intersect(g1, new Line(new Point(2, 1), new Point(2.5, 1)));
	assertTrue(r.isCoincident());
	assertEquals(new Line(2,1,2.5,1), r.getSharedSegment());
	
	r = intersect(g1, new Line(new Point(1, 1), new Point(2, 2)));
	assertTrue(r.isIntersection());
	assertEquals(new Point(1, 1), r.getIntersection());
	
	r = intersect(g1, new Line(new Point(2, 2), new Point(2, 3)));
	assertTrue(r.isOutsideIntersection());
	assertEquals(new Point(2, 1), r.getIntersection());
	
	// Partially coincident
	r = intersect(g1, new Line(4,1, 7,1));
	assertTrue(r.isCoincident());
	assertEquals(new Line(4,1,5,1), r.getSharedSegment());

	// Extension of line
	r = intersect(g1, new Line(5,1, 7,1));
	assertTrue(r.isCoincident());
	assertEquals(new Point(5,1), r.getIntersection());
	
	// Extension of line
	r = intersect(g1, new Line(6,1, 7,1));
	assertTrue(r.isOutsideCoincident());

	r = intersect(new Line(27,2,27,0), new Line(27,0,27,1));
	assertEquals(IntersectionResult.coincident(new Line(27,0,27,1)), r);
	
	r = intersect(new Line(27,2,27,0), new Line(27,1,27,2));
	assertEquals(IntersectionResult.coincident(new Line(27,1,27,2)), r);
	
	assertTrue(new Line(27,0,27,1).intersectsOrCoincides(27, 2, 27, 0));
	assertTrue(new Line(27,1,27,2).intersectsOrCoincides(27, 2, 27, 0));
    }

    /*
     * Test isHorizontal() and isVertical()
     */
    @Test public void testOrientation() {
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
    
    @Test public void testContainsEdge() {
        Line hline = new Line(new Point(1,3), new Point(5, 3));
        Line vline = new Line(new Point(1,3), new Point(1, 8));
        Line sline = new Line(new Point(1,3), new Point(5, 4));
        
        assertTrue(hline.containsEdge(new Line(2,3,3,3)));
        assertTrue(hline.containsEdge(new Line(4,3,5,3)));
        assertFalse(hline.containsEdge(new Line(4,3,6,3)));
        assertFalse(hline.containsEdge(new Line(4,4,5,5)));
	assertFalse(hline.containsEdge(new Line(2,2,3,2)));
        assertTrue(vline.containsEdge(new Line(1,3,1,4)));
        assertFalse(vline.containsEdge(new Line(4,4,5,5)));
        assertFalse(sline.containsEdge(new Line(4,4,5,5)));
        
        // Don't test this - it's unspecied behavior
        // assertFalse("edge cannot be sloped", sline.containsEdge(sline));
    }
}
