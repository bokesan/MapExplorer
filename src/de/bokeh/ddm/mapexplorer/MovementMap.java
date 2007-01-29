/*
 * This file is part of Map Explorer.
 * 
 * Copyright © 2005-2007 Christoph Breitkopf
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including
 * commercial applications, and to alter it and redistribute it freely, subject to
 * the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not claim
 *      that you wrote the original software. If you use this software in a product,
 *      an acknowledgment in the product documentation would be appreciated but is
 *      not required.
 *
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *
 *   3. This notice may not be removed or altered from any source distribution.
 */

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
    public final void clear() {
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
	if (!m.getDimension().equals(size))
	    throw new IllegalArgumentException("Wrong map size");
	if (c.getSize().compareTo(CreatureSize.HUGE) > 0)
	    throw new IllegalArgumentException("can't compute movement for gargantuan or larger");
	move(m, c, c.getLocation(), 0, false);
	if (c.getSize().compareTo(CreatureSize.MEDIUM) > 0)
	    postProcessSize(c.getSize());
    }
    
    private void postProcessSize(CreatureSize size) {
	int sq = size.sizeSquares();
	if (sq <= 1)
	    throw new IllegalArgumentException();
	int lastCol = this.width - sq;
	for (int row = this.size.getHeight() - sq; row >= 0; row--) {
	    for (int col = lastCol; col >= 0; col--) {
		byte val = move[row * width + col];
		for (int xoff = 0; xoff < sq; xoff++) {
		    for (int yoff = 0; yoff < sq; yoff++) {
			int idx = (row + yoff) * width + col + xoff;
			if (move[idx] > val)
			    move[idx] = val;
		    }
		}
	    }
	}
    }

    private void move(Map m, Creature c, Location loc, int current,
	    boolean movedDiagonal) {
	if (moveImproved(loc, current, movedDiagonal)) {
	    setMove(loc, (byte) current, movedDiagonal);
	    for (int dir = 0; dir < 8; dir++) {
		TryMoveResult tmove = tryMove(m, c, loc, dir);
		if (tmove != null) {
		    Location dest = tmove.loc;
		    boolean diag = movedDiagonal;
		    int cost = 1;
		    if ((dir & 1) != 0) {
			// diagonal
			if (tmove.difficult) {
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
			if (tmove.difficult)
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
	return moveLoc(loc, coff[dir], roff[dir]);
    }
    
    /**
     * Return Location offset by xoff, yoff, or null if off map.
     * @param loc a Location
     * @param xoff column offset
     * @param yoff row offset
     * @return The location offset by xoff, yoff, or null if off map.
     */
    private final Location moveLoc(Location loc, int xoff, int yoff) {
	int col = loc.getColumn() + xoff;
	if (col < 0 || col >= size.getWidth())
	    return null;
	int row = loc.getRow() + yoff;
	if (row < 0 || row >= size.getHeight())
	    return null;
	return new Location(col, row);
    }

    private static final class TryMoveResult {
	public final Location loc;
	public final boolean difficult;
	public TryMoveResult(Location loc, boolean difficult) {
	    this.loc = loc;
	    this.difficult = difficult;
	}
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
    private TryMoveResult tryMove(Map m, Creature c, Location loc, int dir) {
	boolean difficult = false;
	for (MoveCheck mc : moveCheck[c.getSize().sizeSquares()][dir]) {
	    Location destLoc = moveLoc(loc, mc.xoff, mc.yoff);
	    if (destLoc == null)
		return null;
	    MapSquare dest = m.get(destLoc);
	    if (mc.checkTerrain) {
		if (dest.has(MapFeature.PIT) || dest.has(MapFeature.LAVA))
		    return null;
		if (dest.isDifficult())
		    difficult = true;
	    }
	    for (Direction d : mc.wallsToCheck) {
		if (dest.getWall(d))
		    return null;
	    }
	}
	return new TryMoveResult(moveLoc(loc, dir), difficult);
    }
    
    private static class MoveCheck {
	public static final int NO_TERRAIN = 1;
	public final int xoff;
	public final int yoff;
	public final Direction[] wallsToCheck;
	public final boolean checkTerrain;
	public MoveCheck(int xoff, int yoff) {
	    this.xoff = xoff;
	    this.yoff = yoff;
	    this.wallsToCheck = new Direction[0];
	    checkTerrain = true;
	}
	public MoveCheck(int xoff, int yoff, Direction d) {
	    this.xoff = xoff;
	    this.yoff = yoff;
	    this.wallsToCheck = new Direction[]{d};
	    if (xoff == 0 && yoff == 0)
		checkTerrain = false;
	    else
		checkTerrain = true;
	}
	public MoveCheck(int xoff, int yoff, Direction d1, Direction d2) {
	    this.xoff = xoff;
	    this.yoff = yoff;
	    this.wallsToCheck = new Direction[]{d1,d2};
	    if (xoff == 0 && yoff == 0)
		checkTerrain = false;
	    else
		checkTerrain = true;
	}
	public MoveCheck(int xoff, int yoff, Direction d, int flags) {
	    this.xoff = xoff;
	    this.yoff = yoff;
	    this.wallsToCheck = new Direction[]{d};
	    if ((flags & NO_TERRAIN) != 0)
		checkTerrain = false;
	    else
		checkTerrain = true;
	}
    }
    
    private static final MoveCheck[][][] moveCheck = {
	{ },
	// Size 1
	{
	    
	    { /* N  */ new MoveCheck(0, 1, Direction.SOUTH) },
	    { /* NE */ new MoveCheck(0, 0, Direction.NORTH, Direction.EAST),
		       new MoveCheck(1, 1, Direction.SOUTH, Direction.WEST)
	    },
	    { /* E  */ new MoveCheck(1, 0, Direction.WEST) },
	    { /* SE */ new MoveCheck(0, 0, Direction.SOUTH, Direction.EAST),
		new MoveCheck(1, -1, Direction.NORTH, Direction.WEST)
	    },
	    { /* S  */ new MoveCheck(0, -1, Direction.NORTH) },
	    { /* SW */ new MoveCheck(0, 0, Direction.SOUTH, Direction.WEST),
		new MoveCheck(-1, -1, Direction.NORTH, Direction.EAST)
	    },
	    { /* W  */ new MoveCheck(-1, 0, Direction.EAST) },
	    { /* NW */ new MoveCheck(0, 0, Direction.NORTH, Direction.WEST),
		new MoveCheck(-1, 1, Direction.SOUTH, Direction.EAST)
	    }
	},
	// Size 2
	{
	    { /* N  */
		new MoveCheck(0, 2, Direction.SOUTH, Direction.EAST),
		new MoveCheck(1, 2, Direction.SOUTH),
		new MoveCheck(0, 1), new MoveCheck(1, 1)
	    },
	    { /* NE */
		new MoveCheck(0, 2, Direction.SOUTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(1, 2, Direction.SOUTH, Direction.WEST),
		new MoveCheck(2, 2, Direction.SOUTH, Direction.WEST),
		new MoveCheck(2, 1, Direction.SOUTH, Direction.WEST),
		new MoveCheck(2, 0, Direction.WEST, MoveCheck.NO_TERRAIN),
		new MoveCheck(1, 1)
	    },
	    { /* E  */
		new MoveCheck(2, 1, Direction.WEST),
		new MoveCheck(2, 0, Direction.WEST, Direction.NORTH),
		new MoveCheck(1, 0), new MoveCheck(1, 1)
	    },
	    { /* SE */
		new MoveCheck(2, 1, Direction.WEST, MoveCheck.NO_TERRAIN),
		new MoveCheck(2, 0, Direction.WEST, Direction.NORTH),
		new MoveCheck(2, -1, Direction.WEST, Direction.NORTH),
		new MoveCheck(1, -1, Direction.WEST, Direction.NORTH),
		new MoveCheck(0, -1, Direction.NORTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(1, 0)
	    },
	    { /* S */
		new MoveCheck(0, -1, Direction.NORTH, Direction.EAST),
		new MoveCheck(1, -1, Direction.NORTH),
		new MoveCheck(0, 0), new MoveCheck(1, 0)
	    },
	    { /* SW */
		new MoveCheck(-1, 1, Direction.EAST, MoveCheck.NO_TERRAIN),
		new MoveCheck(-1, 0, Direction.EAST, Direction.NORTH),
		new MoveCheck(-1, -1, Direction.EAST, Direction.NORTH),
		new MoveCheck(0, -1, Direction.EAST, Direction.NORTH),
		new MoveCheck(1, -1, Direction.NORTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(0, 0)
	    },
	    { /* W  */
		new MoveCheck(-1, 1, Direction.EAST, Direction.SOUTH),
		new MoveCheck(-1, 0, Direction.EAST),
		new MoveCheck(0, 0), new MoveCheck(0, 1)
	    },
	    { /* NW */
		new MoveCheck(-1, 0, Direction.EAST, MoveCheck.NO_TERRAIN),
		new MoveCheck(-1, 1, Direction.EAST, Direction.SOUTH),
		new MoveCheck(-1, 2, Direction.EAST, Direction.SOUTH),
		new MoveCheck(0, 2, Direction.EAST, Direction.SOUTH),
		new MoveCheck(1, 2, Direction.SOUTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(0, 1)
	    }
	},
	// Size 3
	{
	    { // N
		new MoveCheck(0, 3, Direction.SOUTH, Direction.EAST),
		new MoveCheck(1, 3, Direction.SOUTH, Direction.EAST),
		new MoveCheck(2, 3, Direction.SOUTH),
		new MoveCheck(0, 1), new MoveCheck(1, 1), new MoveCheck(2, 1),
		new MoveCheck(0, 2), new MoveCheck(1, 2), new MoveCheck(2, 2)
	    },
	    { // NE
		new MoveCheck(0, 3, Direction.SOUTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(1, 3, Direction.SOUTH, Direction.WEST),
		new MoveCheck(2, 3, Direction.SOUTH, Direction.WEST),
		new MoveCheck(3, 3, Direction.SOUTH, Direction.WEST),
		new MoveCheck(3, 2, Direction.SOUTH, Direction.WEST),
		new MoveCheck(3, 1, Direction.SOUTH, Direction.WEST),
		new MoveCheck(3, 0, Direction.WEST, MoveCheck.NO_TERRAIN),
		new MoveCheck(1, 1), new MoveCheck(2, 1),
		new MoveCheck(1, 2), new MoveCheck(2, 2)
	    },
	    { // E
		new MoveCheck(3, 2, Direction.WEST, Direction.SOUTH),
		new MoveCheck(3, 1, Direction.WEST, Direction.SOUTH),
		new MoveCheck(3, 0, Direction.WEST),
		new MoveCheck(1, 2), new MoveCheck(1, 1), new MoveCheck(1, 0),
		new MoveCheck(2, 2), new MoveCheck(2, 1), new MoveCheck(2, 0)
	    },
	    { // SE
		new MoveCheck(3, 2, Direction.WEST, MoveCheck.NO_TERRAIN),
		new MoveCheck(3, 1, Direction.WEST, Direction.NORTH),
		new MoveCheck(3, 0, Direction.WEST, Direction.NORTH),
		new MoveCheck(3, -1, Direction.WEST, Direction.NORTH),
		new MoveCheck(2, -1, Direction.WEST, Direction.NORTH),
		new MoveCheck(1, -1, Direction.WEST, Direction.NORTH),
		new MoveCheck(0, -1, Direction.NORTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(1, 1), new MoveCheck(2, 1),
		new MoveCheck(1, 0), new MoveCheck(2, 0)
	    },
	    { // S
		new MoveCheck(0, -1, Direction.NORTH, Direction.EAST),
		new MoveCheck(1, -1, Direction.NORTH, Direction.EAST),
		new MoveCheck(2, -1, Direction.NORTH),
		new MoveCheck(0, 1), new MoveCheck(1, 1), new MoveCheck(2, 1),
		new MoveCheck(0, 0), new MoveCheck(1, 0), new MoveCheck(2, 0)
	    },
	    { // SW
		new MoveCheck(-1, 2, Direction.EAST, MoveCheck.NO_TERRAIN),
		new MoveCheck(-1, 1, Direction.EAST, Direction.NORTH),
		new MoveCheck(-1, 0, Direction.EAST, Direction.NORTH),
		new MoveCheck(-1, -1, Direction.EAST, Direction.NORTH),
		new MoveCheck(0, -1, Direction.EAST, Direction.NORTH),
		new MoveCheck(1, -1, Direction.EAST, Direction.NORTH),
		new MoveCheck(2, -1, Direction.NORTH, MoveCheck.NO_TERRAIN),
		new MoveCheck(0, 0), new MoveCheck(1, 0),
		new MoveCheck(0, 1), new MoveCheck(1, 1)
	    },
	    { // W
		new MoveCheck(-1, 2, Direction.EAST, Direction.SOUTH),
		new MoveCheck(-1, 1, Direction.EAST, Direction.SOUTH),
		new MoveCheck(-1, 0, Direction.EAST),
		new MoveCheck(0, 0), new MoveCheck(0, 1), new MoveCheck(0, 2),
		new MoveCheck(1, 0), new MoveCheck(1, 1), new MoveCheck(1, 2)
	    },
	    { // NW
		new MoveCheck(-1, 0, Direction.EAST, MoveCheck.NO_TERRAIN),
		new MoveCheck(-1, 1, Direction.EAST, Direction.SOUTH),
		new MoveCheck(-1, 2, Direction.EAST, Direction.SOUTH),
		new MoveCheck(-1, 3, Direction.EAST, Direction.SOUTH),
		new MoveCheck(0, 3, Direction.EAST, Direction.SOUTH),
		new MoveCheck(1, 3, Direction.EAST, Direction.SOUTH),
		new MoveCheck(2, 3, Direction.SOUTH, MoveCheck.NO_TERRAIN)
	    }
	}
    };
    
    
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
