package de.bokeh.ddm.mapexplorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class MovementMapTest {

    private Map wallMap() {
	Dimension d = new Dimension(13, 12);
	Map map = new Map(d, "Map for movement tests");
        //map.addWall(new Line(9, 3, 9, 7));
        map.addWall(new Line(6,7,9,7));
        Rectangle r = new Rectangle(new Location(3,3), new Location(5,6));
        map.addWall(new Polygon(r));
        for (Location loc : r.getLocations())
            map.get(loc).setSolid(true);
        return map;
    }
    
    private Map diffMap() {
	Dimension d = new Dimension(13, 12);
	Map map = new Map(d, "Map for movement tests");
	for (int row = d.getHeight() - 1; row >= 0; row--)
	    map.get(9, row).addFeature(MapFeature.DIFFICULT);
	return map;
    }

    private void testResults(MovementMap mm, int... expected) {
        final int width = mm.getSize().getWidth();
        final int height = mm.getSize().getHeight();
        int i = 0;
        for (int row = height - 1; row >= 0; row--) {
            for (int col = 0; col < width; col++) {
                Location loc = new Location(col, row);
                assertEquals(loc.toString(), expected[i], mm.getMove(loc));
                i++;
            }
        }
    }


    @Test
    public void testSimple1() {
        Map map = new Map(new Dimension(4,3), "Map for movement tests");
        MovementMap mm = new MovementMap(map.getDimension());
        Creature c = new Creature(CreatureSize.MEDIUM, new Location(0,0));
        mm.computeMovement(map, c);
        testResults(mm, 2, 2, 3, 4,
                        1, 1, 2, 3,
                        0, 1, 2, 3);
    }

    @Test
    public void testSimple2() {
        Map map = new Map(new Dimension(4,3), "Map for movement tests");
        Rectangle r = new Rectangle(new Location(1,1));
        map.addWall(new Polygon(r));
        for (Location loc : r.getLocations())
            map.get(loc).setSolid(true);
        MovementMap mm = new MovementMap(map.getDimension());
        Creature c = new Creature(CreatureSize.MEDIUM, new Location(0,0));
        mm.computeMovement(map, c);
        testResults(mm, 2,   3, 4, 4,
                        1, 127, 3, 3,
                        0,   1, 2, 3);
    }

    @Test
    public void testSimple21() {
        Map map = new Map(new Dimension(4,3), "Map for movement tests");
        Rectangle r = new Rectangle(new Location(1,0));
        map.addWall(new Polygon(r));
        for (Location loc : r.getLocations())
            map.get(loc).setSolid(true);
        MovementMap mm = new MovementMap(map.getDimension());
        Creature c = new Creature(CreatureSize.MEDIUM, new Location(0,0));
        mm.computeMovement(map, c);
        testResults(mm, 2,   2, 3, 4,
                        1,   2, 3, 4,
                        0, 127, 4, 4);
    }
    
    @Test
    public void testMedium() {
	Creature c = new Creature(CreatureSize.MEDIUM, new Location(2, 2));
	Map map = diffMap();
	MovementMap mm = new MovementMap(map.getDimension());
	mm.computeMovement(map, c);
	assertEquals("origin", 0, mm.getMove(new Location(2, 2)));
	assertEquals("diag1", 1, mm.getMove(new Location(3, 3)));
	assertEquals("diag2", 3, mm.getMove(new Location(4, 4)));
	assertEquals("diag3", 4, mm.getMove(new Location(5, 5)));
	assertEquals("diag4", 6, mm.getMove(new Location(6, 6)));
	// Difficult:
	assertEquals(6, mm.getMove(new Location(8,2)));
	assertEquals(8, mm.getMove(new Location(9,2)));
	assertEquals(9, mm.getMove(new Location(10,2)));
	
	map = wallMap();
	mm = new MovementMap(map.getDimension());
	mm.computeMovement(map, c);
	assertEquals("around wall", 6, mm.getMove(new Location(3, 7)));
	assertEquals("diagonal", 3, mm.getMove(new Location(0,0)));
    }

    @Test
    public void testLarge() {
	Creature c = new Creature(CreatureSize.LARGE, new Location(1, 1));
	Map map = diffMap();
	MovementMap mm = new MovementMap(map.getDimension());
	mm.computeMovement(map, c);
	assertEquals("origin", 0, mm.getMove(new Location(1, 1)));
	assertEquals("diag1", 1, mm.getMove(new Location(3, 3)));
	assertEquals("diag2", 3, mm.getMove(new Location(4, 4)));
	assertEquals("diag3", 4, mm.getMove(new Location(5, 5)));
	assertEquals("diag4", 6, mm.getMove(new Location(6, 6)));
	// Difficult:
	assertEquals(6, mm.getMove(new Location(8,1)));
	assertEquals(8, mm.getMove(new Location(9,1)));
	assertEquals(10, mm.getMove(new Location(10,1)));
	assertEquals(11, mm.getMove(new Location(11,1)));

	map = wallMap();
	mm = new MovementMap(map.getDimension());
	mm.computeMovement(map, c);
	assertEquals("around wall", 8, mm.getMove(new Location(4, 7)));
    }
    
}
