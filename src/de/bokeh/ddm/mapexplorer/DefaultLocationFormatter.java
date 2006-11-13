package de.bokeh.ddm.mapexplorer;

public class DefaultLocationFormatter implements LocationFormatter {

    public String format(Location loc) {
	return loc.toString();
    }

    public String formatColumn(int col) {
	return Location.columnToString(col);
    }

    public String formatRow(int row) {
	return Location.rowToString(row);
    }

}
