package de.bokeh.ddm.mapexplorer;

public class MovementMap {

    public static final byte UNREACHABLE = Byte.MAX_VALUE;
    
    private final Dimension size;
    private final int width; // Performance
    private final byte[] move;
    private final boolean[] diagMove;
    
    public MovementMap(Dimension size) {
	this.size = size;
	width = size.getWidth();
	int length = width * size.getHeight();
	move = new byte[length];
	diagMove = new boolean[length];
	clear();
    }
    
    /**
     * Reset movement info.
     */
    public void clear() {
	int size = move.length;
	for (int i = 0; i < size; i++) {
	    move[i] = UNREACHABLE;
	    diagMove[i] = false;
	}
    }
    
    
    /**
     * Compute movement for a creature.
     * @param m a Map
     * @param c a Creature
     */
    public void computeMovement(Map m, Creature c) {
	if (c.getSize().compareTo(CreatureSize.MEDIUM) > 0)
	    throw new IllegalArgumentException("Large or larger not implemented");
	if (!m.getDimension().equals(size))
	    throw new IllegalArgumentException("Wrong map size");
	move(m, c, c.getLocation(), 0, false);
    }

    private void move(Map m, Creature c, Location loc, int current,
	    boolean movedDiagonal) {
	if (moveImproved(loc, current, movedDiagonal)) {
	    setMove(loc, (byte) current, movedDiagonal);
	    for (int dir = 0; dir < 8; dir++) {
		Location dest = canMove(m, c, loc, dir);
		if (dest != null) {
		    boolean diag = movedDiagonal;
		    int cost = 1;
		    if ((dir & 1) != 0) {
			// diagonal
			if (m.get(dest).isDifficult()) {
			    cost = 3;
			} else {
			    if (movedDiagonal) {
				cost = 2;
				diag = false;
			    } else {
				diag = true;
			    }
			}
			
		    } else {
			if (m.get(dest).isDifficult())
			    cost = 2;
		    }
		    move(m, c, dest, current + cost, diag);
		}
	    }
	}
    }
    
    // These two are used by moveLoc()
    private static final int[] roff = { 1, 1, 0, -1, -1, -1, 0, 1 };
    private static final int[] coff = { 0, 1, 1, 1, 0, -1, -1, -1 };
    
    /**
     * Return next Location in direction, or null if off map.
     * @param loc a Location
     * @param dir direction
     * @return The next location in that direction, or null if off map.
     */
    private final Location moveLoc(Location loc, int dir) {
	int col = loc.getColumn() + coff[dir];
	if (col < 0 || col >= size.getWidth())
	    return null;
	int row = loc.getRow() + roff[dir];
	if (row < 0 || row >= size.getHeight())
	    return null;
	return new Location(col, row);
    }

    /**
     * Check if creature can move in a given direction.
     * @param m a Map
     * @param c a Creature
     * @param loc the current Location
     * @param dir direction
     * @return The new Location, if the move is possible,
     *   or null if the move is not possible.
     */
    private Location canMove(Map m, Creature c, Location loc, int dir) {
	switch (c.getSize()) {
	case TINY: case SMALL: case MEDIUM:
	    return canMoveMedium(m, loc, dir);
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    private Location canMoveMedium(Map m, Location loc, int dir) {
	Location destLoc = moveLoc(loc, dir);
	if (destLoc == null)
	    return null;
	MapSquare dest = m.get(destLoc);
	if (dest.has(MapFeature.PIT) || dest.has(MapFeature.LAVA))
	    return null;
	MapSquare here = m.get(loc);
	switch (dir) {
	case 0: // North
	    if (here.getWall(Direction.NORTH))
		return null;
	    if (dest.getWall(Direction.SOUTH))
		return null;
	    break;
	case 1:
	    if (here.getWall(Direction.NORTH) || here.getWall(Direction.EAST))
		return null;
	    if (dest.getWall(Direction.SOUTH) || dest.getWall(Direction.WEST))
		return null;
	    break;
	case 2:
	    if (here.getWall(Direction.EAST))
		return null;
	    if (dest.getWall(Direction.WEST))
		return null;
	    break;
	case 3:
	    if (here.getWall(Direction.EAST) || here.getWall(Direction.SOUTH))
		return null;
	    if (dest.getWall(Direction.WEST) || dest.getWall(Direction.NORTH))
		return null;
	    break;
	case 4:
	    if (here.getWall(Direction.SOUTH))
		return null;
	    if (dest.getWall(Direction.NORTH))
		return null;
	    break;
	case 5:
	    if (here.getWall(Direction.SOUTH) || here.getWall(Direction.WEST))
		return null;
	    if (dest.getWall(Direction.NORTH) || dest.getWall(Direction.EAST))
		return null;
	    break;
	case 6:
	    if (here.getWall(Direction.WEST))
		return null;
	    if (dest.getWall(Direction.EAST))
		return null;
	    break;
	case 7:
	    if (here.getWall(Direction.WEST) || here.getWall(Direction.NORTH))
		return null;
	    if (dest.getWall(Direction.EAST) || dest.getWall(Direction.SOUTH))
		return null;
	    break;
	}
	return destLoc;
    }
    
    /**
     * @return Returns the size of the map.
     */
    public Dimension getSize() {
        return size;
    }

    
    /**
     * Get the movement cost to a location.
     * @param loc a Location
     * @return The cost to move to the location.
     */
    public byte getMove(Location loc) {
	return move[loc.getRow() * width + loc.getColumn()];
    }
    
    /**
     * Check if the move value is better than the current one.
     * @param loc the Location
     * @param val the new movement value
     * @param diag whether the last move was diagonal
     * @return Returns true if the new move is better than the current one.
     */
    private boolean moveImproved(Location loc, int val, boolean diag) {
	int i = loc.getRow() * width + loc.getColumn();
	int m = move[i];
	return (val < m) || (val == m && !diag && diagMove[i]);
    }
    
    /**
     * Set a move value on the map.
     * @param loc a Location
     * @param val the new movement value
     * @param diag whether the last move was diagonal
     */
    private void setMove(Location loc, byte val, boolean diag) {
	int i = loc.getRow() * width + loc.getColumn();
	move[i] = val;
	diagMove[i] = diag;
    }
}
