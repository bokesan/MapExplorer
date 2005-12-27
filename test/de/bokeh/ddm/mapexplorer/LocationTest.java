package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class LocationTest extends TestCase {

    public LocationTest(String name) {
	super(name);
    }

    public void testLocationString() {
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
    
    public void testLocationInvalid() {
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

}
