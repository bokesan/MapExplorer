package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class CreatureTest extends TestCase {

    public CreatureTest(String method) {
	super(method);
    }

    public void testOverlaps() {
	Creature a = new Creature(CreatureSize.HUGE);
	Creature b = new Creature(CreatureSize.MEDIUM);
	a.setLocation(new Location(1, 1));
	b.setLocation(new Location(4, 4));
	assertFalse(a.overlaps(b));
	assertFalse(b.overlaps(a));
	b.setLocation(new Location(3, 3));
	assertTrue(a.overlaps(b));
	assertTrue(b.overlaps(a));
	b.setLocation(new Location(3, 4));
	assertFalse(a.overlaps(b));
	assertFalse(b.overlaps(a));
	a.setLocation(new Location(1, 2));
	b.setLocation(new Location(1, 1));
	assertFalse(a.overlaps(b));
	assertFalse(b.overlaps(a));
    }
    
}
