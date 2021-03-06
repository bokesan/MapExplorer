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

import java.util.*;


/**
 * A map is organized into rows and columns, starting from the bottom left with
 * column and row 0.
 * 
 * @author Christoph Breitkopf
 */
public class Map {
    
    private final String name;
    private final int height;
    private final int width;
    private final MapSquare[] map;
    private final List<Line> thinWalls;
    private final List<Polygon> thickWalls;
    
    private String imageFile = null;
    
    public Map(Dimension size) {
	this(size.getWidth(), size.getHeight(), null);
    }
    
    public Map(Dimension size, String name) {
	this(size.getWidth(), size.getHeight(), name);
    }
    
    private Map(int width, int height, String name) {
	this.width = width;
	this.height = height;
	int size = width * height;
	this.map = new MapSquare[size];
	for (int i = 0; i < size; i++)
	    map[i] = new MapSquare();
	this.name = name;
        thinWalls = new ArrayList<Line>();
        thickWalls = new ArrayList<Polygon>();
    }

    
    /**
     * Add a thick wall.
     * @param wall a Polygon defining the wall
     */
    public void addWall(Polygon wall) {
        thickWalls.add(wall);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (wall.contains(col + 0.5, row + 0.5))
                    get(col, row).setSolid(true);
            }
        }
    }
    
    /**
     * Add a thin wall.
     * @param wall a Line
     */
    public void addWall(Line wall) {
        for (Line w : thinWalls) {
            Line ext = w.extend(wall);
            if (ext != null) {
                thinWalls.remove(w);
                addWall(ext);
                return;
            }
        }
        thinWalls.add(wall);
    }
    
    /**
     * Get the set of Walls for the LOS computation.
     * @param smokeBlocksLOS a boolean indicating whether smoke or fog blocks LoS
     * @return A set of Lines.
     */
    public Set<Line> getWalls(boolean smokeBlocksLOS) {
	Set<Line> walls = new HashSet<Line>();
	for (Line w : thinWalls) {
	    if (!isBorderWall(w))
	        walls.add(w);
	}
        for (Polygon wall : thickWalls) {
            for (Line e : wall.getEdges())
                addWall(walls, e);
        }
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {
		MapSquare s = get(x, y);
		if ((smokeBlocksLOS && s.has(MapFeature.SMOKE)) || s.has(MapFeature.ELEMENTAL_WALL)) {
		    addSquareWalls(walls, x, y, 1);
		}
	    }
	}
	return walls;
    }
    
    private void addSquareWalls(Set<Line> walls, int x, int y, int size) {
	Point bl = new Point(x, y);
	Point br = new Point(x + size, y);
	Point tl = new Point(x, y + size);
	Point tr = new Point(x + size, y + size);
	addWall(walls, new Line(bl, br));
	addWall(walls, new Line(bl, tl));
	addWall(walls, new Line(tl, tr));
	addWall(walls, new Line(br, tr));
    }
    
    private void addWall(Set<Line> walls, Line wall) {
	if (isBorderWall(wall))
	    return;
	for (Line w : walls) {
	    Line ext = w.extend(wall);
	    if (ext != null) {
		walls.remove(w);
		walls.add(ext);
		return;
	    }
	}
	walls.add(wall);
    }

    private boolean isBorderWall(Line wall) {
        if (wall.getStart().getX() == 0 && wall.getEnd().getX() == 0)
            return true;
        if (wall.getStart().getX() == width && wall.getEnd().getX() == width)
            return true;
        if (wall.getStart().getY() == 0 && wall.getEnd().getY() == 0)
            return true;
        if (wall.getStart().getY() == height && wall.getEnd().getY() == height)
            return true;
        return false;
    }
    
    
    /**
     * @return Returns the name.
     */
    public String getName() {
	return name;
    }
    
    /**
     * Set all squares of this map to color.
     * 
     * @param color a Color
     */
    public void setColor(java.awt.Color color) {
	int size = width * height;
	for (int i = 0; i < size; i++)
	    map[i].setColor(color);
    }
    
    /**
     * Place a smaller map on this map.
     * 
     * @param loc a Location
     * @param m a Map
     */
    public void place(Location loc, Map m) {
	place(loc.getColumn(), loc.getRow(), m);
    }
    
    /**
     * Place a smaller map on this map.
     * 
     * @param col left column
     * @param row bottom row
     * @param m a Map
     */
    public void place(int col, int row, Map m) {
	for (int r = 0; r < m.height; r++)
	    for (int c = 0; c < m.width; c++)
		set(c + col, r + row, m.get(c, r));
    }
    
    /**
     * Clear a rectangle.
     * 
     * @param bottomLeft
     *                bottom left corner
     * @param size
     *                size
     */
    public void clear(Location bottomLeft, Dimension size) {
	clear(bottomLeft.getColumn(), bottomLeft.getRow(), size.getWidth(),
		size.getHeight());
    }
    
    /**
     * Clear a rectangle.
     * 
     * @param left
     *                left column
     * @param bottom
     *                bottom row
     * @param width
     *                width
     * @param height
     *                height
     */
    public void clear(int left, int bottom, int width, int height) {
	for (int row = 0; row < height; row++)
	    for (int col = 0; col < width; col++)
		set(col + left, row + bottom, new MapSquare());
    }
    
    /**
     * Return a rectangular submap. The individual squares are shared, not
     * cloned.
     * 
     * @param col
     *                left column
     * @param row
     *                bottom row
     * @param width
     *                width
     * @param height
     *                height
     * @return Returns a rectangular submap.
     */
    public Map extract(int col, int row, int width, int height) {
	Map m = new Map(width, height, null);
	for (int r = 0; r < height; r++) {
	    for (int c = 0; c < width; c++) {
		m.set(c, r, this.get(c + col, r + row));
	    }
	}
	return m;
    }
    
    /**
     * Returns the tile at the specified location.
     * 
     * @param col
     *                the column
     * @param row
     *                the row
     * @return the tile at column and row
     */
    public MapSquare get(int col, int row) {
	assert 0 <= col && col < width;
	assert 0 <= row && row < height;
	return map[width * row + col];
    }
    
    /**
     * Returns the tile at the specified location.
     * 
     * @param loc
     *                the Location
     * @return the tile at loc
     */
    public MapSquare get(Location loc) {
	return get(loc.getColumn(), loc.getRow());
    }
    
    private void set(int col, int row, MapSquare tile) {
	assert 0 <= col && col < width;
	assert 0 <= row && row < height;
	map[width * row + col] = tile;
    }
    
    /**
     * @return Returns the height.
     */
    public int getHeight() {
	return height;
    }
    
    /**
     * @return Returns the width.
     */
    public int getWidth() {
	return width;
    }
    
    /**
     * @return Return the Dimension.
     */
    public Dimension getDimension() {
	return new Dimension(width, height);
    }


    /**
     * Does any square of this map have feature f?
     * @param f a MapFeature
     * @return true if any square of this map has MapFeature f.
     */
    public boolean has(MapFeature f) {
	for (MapSquare s : map) {
	    if (s.has(f))
		return true;
	}
	return false;
    }
    
    /**
     * Try to place a creature at loc.
     * 
     * @param c a Creature
     * @return true, if the creature can be placed at loc.
     */
    public boolean canPlaceCreature(Creature c) {
	return !isBlocked(c.getLocation(), c.getSize().sizeSquares());
    }
    
    private boolean isBlocked(Location loc, int size) {
	int left = loc.getColumn();
	int bottom = loc.getRow();
	int right = left + size - 1;
	int top = bottom + size - 1;
	for (int row = bottom; row <= top; row++) {
	    for (int col = left; col <= right; col++) {
                Location loc1 = new Location(col, row);
		MapSquare s = get(col, row);
		if (s.isSolid())
		    return true;
		if (col > left && wallExists(loc1.getEdge(Direction.WEST)))
		    return true;
		if (col < right && wallExists(loc1.getEdge(Direction.EAST)))
		    return true;
		if (row > bottom && wallExists(loc1.getEdge(Direction.SOUTH)))
		    return true;
		if (row < top && wallExists(loc1.getEdge(Direction.NORTH)))
		    return true;
	    }
	}
	return false;
    }
    
    protected boolean wallExists(Line edge) {
        for (Line w : thinWalls) {
            if (w.containsEdge(edge))
                return true;
        }
        for (Polygon p : thickWalls) {
            for (Line e : p.getEdges()) {
                if (e.containsEdge(edge))
                    return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the imageFile.
     */
    public String getImageFile() {
        return imageFile;
    }

    /**
     * @param imageFile The imageFile to set.
     */
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * @return Returns the thickWalls.
     */
    public List<Polygon> getThickWalls() {
        return thickWalls;
    }

    /**
     * @return Returns the thinWalls.
     */
    public List<Line> getThinWalls() {
        return thinWalls;
    }
    
}
