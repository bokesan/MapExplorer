package de.bokeh.ddm.mapexplorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class LocationTest {

    @Test public void testLocationString() {
	final String[] sx = { "A1", "A2", "B1", "A10", "A21", "C21", "Z34" };
	final int[] cx    = {    0,    1,    0,     9,    20,    20,    33  };
	final int[] rx    = {    0,    0,    1,     0,     0,     2,    25  };
	
	for (int i = 0; i < sx.length; i++) {
	    Location loc = new Location(sx[i]);
	    assertEquals(cx[i], loc.getColumn());
	    assertEquals(rx[i], loc.getRow());
	    loc = new Location(sx[i].toLowerCase());
	    assertEquals(cx[i], loc.getColumn());
	    assertEquals(rx[i], loc.getRow());
	}
    }
    
    @Test public void testLocationInvalid() {
	try {
	    new Location((String)null);
	    fail("should have thrown NullPointerException()");
	}
	catch (NullPointerException ex) {
	    assertTrue(true);
	}
	
	final String[] bad = { "A0", "A-1", "", "A", "1", "12" };
	for (String s : bad) {
	    try {
		new Location(s);
		fail("should have thrown IllegalArgumentException()");
	    }
	    catch (IllegalArgumentException ex) {
		assertTrue(true);
	    }
	}
    }

    @Test public void testGetEdge() {
        Location a = new Location(1, 10);
        assertEquals(new Line(1,10,1,11), a.getEdge(Direction.WEST));
        assertEquals(new Line(1,10,2,10), a.getEdge(Direction.SOUTH));
        assertEquals(new Line(1,11,2,11), a.getEdge(Direction.NORTH));
        assertEquals(new Line(2,10,2,11), a.getEdge(Direction.EAST));
    }
}
