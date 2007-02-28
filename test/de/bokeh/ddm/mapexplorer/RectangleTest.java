package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class RectangleTest extends TestCase {

    public RectangleTest(String arg0) {
        super(arg0);
    }

    public void testRectangleLocationLocation() {
        Rectangle r = new Rectangle(new Location(1,2), new Location(5,4));
        assertEquals(1, r.getLeft());
        assertEquals(2, r.getBottom());
        assertEquals(5, r.getRight());
        assertEquals(4, r.getTop());
    }

    public void testRectangleLocationInt() {
        Rectangle r = new Rectangle(new Location(1, 2), 2);
        assertEquals(1, r.getLeft());
        assertEquals(2, r.getBottom());
        assertEquals(2, r.getRight());
        assertEquals(3, r.getTop());
    }

    public void testContainsLocation() {
        Rectangle r = new Rectangle(new Location(1,2), new Location(5,4));
        assertTrue(r.contains(new Location(1,3)));
        assertTrue(r.contains(new Location(4,4)));
        assertTrue(r.contains(new Location(5,2)));
        assertTrue(r.contains(new Location(3,3)));
        assertFalse(r.contains(new Location(4,5)));
        assertFalse(r.contains(new Location(1,1)));
    }

    public void testContainsPoint() {
        Rectangle r = new Rectangle(new Location(1,2), new Location(5,4));
        assertTrue(r.contains(new Point(new Location(6,5))));
        assertTrue(r.contains(new Point(1,2)));
        assertTrue(r.contains(new Point(1.5, 2.0)));
        assertFalse(r.contains(new Point(5,5.1)));
    }

    public void testGetEdge() {
        Rectangle r = new Rectangle(new Location(1,2), new Location(5,4));
        assertEquals(new Line(1,2, 6,2), r.getEdge(Direction.SOUTH));
        assertEquals(new Line(1,2, 1,5), r.getEdge(Direction.WEST));
        assertEquals(new Line(1,5, 6,5), r.getEdge(Direction.NORTH));
        assertEquals(new Line(6,2, 6,5), r.getEdge(Direction.EAST));
    }
}
