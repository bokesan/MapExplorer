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
	row = charToInt(s.charAt(0));
	column = Integer.parseInt(s.substring(1)) - 1;
	if (row < 0 || row >= 22)
	    throw new IllegalArgumentException();
	if (column < 0 || column >= 34)
	    throw new IllegalArgumentException();
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
    
}
