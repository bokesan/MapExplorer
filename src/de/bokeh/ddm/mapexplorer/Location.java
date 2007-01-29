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

/**
 * A map location column, row.
 * <p>
 * The external representation for columns are the numbers 1,2,3,...
 * <p>
 * The external representation for rows are the upper-case letters
 * A,B,C,...
 * <p>
 * The external representation for a Location is the row Letter followed
 * by the column number, for example C4, D17, U30.
 * 
 * @author Christoph Breitkopf
 */
public class Location {
    
    private static final int MAX_ROWS = 26;
    private static final int MAX_COLUMNS = 99; // Arbitrary limitation
    
    /** The column. 0-based. */
    private final int column;
    
    /** The row. 0-based. */
    private final int row;
    
    /**
     * Create a new Location.
     * @param column the column
     * @param row the row
     */
    public Location(int column, int row) {
	if (column < 0 || column >= MAX_COLUMNS)
	    throw new IllegalArgumentException("column out of range: " + column);
	if (row < 0 || row >= MAX_ROWS)
	    throw new IllegalArgumentException("row out of range: " + row);
	this.column = column;
	this.row = row;
    }
    
    /**
     * Creates a new Location from an existing one.
     * @param loc a Location
     */
    public Location(Location loc) {
	column = loc.column;
	row = loc.row;
    }
    
    /**
     * Create a new Location from a String like "A12".
     * @param s a String donating a map square
     */
    public Location(String s) {
	if (s.length() <= 1)
	    throw new IllegalArgumentException(s);
	row = charToInt(s.charAt(0));
	column = Integer.parseInt(s.substring(1)) - 1;
	if (row < 0 || row >= MAX_ROWS)
	    throw new IllegalArgumentException(s);
	if (column < 0 || column >= MAX_COLUMNS)
	    throw new IllegalArgumentException(s);
    }
    
    /**
     * The column.
     * @return The Column.
     */
    public int getColumn() {
	return column;
    }
    
    /**
     * Get the x coordinate, that is the column.
     * @return The column.
     */
    public int x() { return column; }
    
    
    /**
     * Get the y coordinate, that is the row.
     * @return The row.
     */
    public int y() { return row; }
    
    /**
     * @return The row.
     */
    public int getRow() {
	return row;
    }
    
    @Override
    public String toString() {
	return rowToString(row) + columnToString(column);
    }
    
    static public String rowToString(int row) {
	int[] codes = new int[1];
	codes[0] = row + 'A';
	return new String(codes, 0, 1);
    }
    
    private int charToInt(char c) {
	return Character.toUpperCase(c) - 'A';
    }
    
    static public String columnToString(int column) {
	return "" + (column + 1);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Location))
	    return false;
	Location o = (Location) obj;
	return (o.column == this.column && o.row == this.row);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return 17 * column + row + 61612357;
    }
    
    /**
     * Check whether another Location is adjecent to this Location.
     * <p>
     * Also returns <code>true</code> for equal locations.
     * 
     * @param loc a Location
     * @return <code>true</code> if the Location is adjacent to this Location,
     *    <code>false</code> otherwise.
     */
    public boolean isNeighborOf(Location loc) {
	int dx = column - loc.column;
	if (dx < -1 || dx > 1)
	    return false;
	int dy = row - loc.row;
	return (dy >= -1 && dy <= 1);
    }
}
