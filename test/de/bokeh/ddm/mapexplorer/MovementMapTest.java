package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class MovementMapTest extends TestCase {

    public MovementMapTest(String method) {
	super(method);
    }

    private Map wallMap() {
	Dimension d = new Dimension(13, 12);
	Map map = new Map(d, "Map for movement tests");
	for (int row = 3; row < 7; row++)
	    for (int col = 3; col < 6; col++) {
		// Block (3,3) - (5,6)
		map.setSolid(col, row);
		// Thin wall east from (8,3) - (8,6)
		map.dblWall(8, row, Direction.EAST);
	    }
	return map;
    }
    
    private Map diffMap() {
	Dimension d = new Dimension(13, 12);
	Map map = new Map(d, "Map for movement tests");
	for (int row = d.getHeight() - 1; row >= 0; row--)
	    map.get(9, row).addFeature(MapFeature.DIFFICULT);
	return map;
    }
    
    
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
