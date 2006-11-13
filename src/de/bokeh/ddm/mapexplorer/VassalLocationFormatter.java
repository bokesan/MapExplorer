package de.bokeh.ddm.mapexplorer;

public class VassalLocationFormatter implements LocationFormatter {

    private final int height;
    
    public VassalLocationFormatter(Dimension size) {
	height = size.getHeight();
    }
    
    public String format(Location loc) {
	return formatColumn(loc.getColumn()) + formatRow(loc.getRow());
    }

    public String formatColumn(int col) {
	if (col < 26)
	    return Character.toString((char) ('A' + col));
	return "A" + formatColumn(col - 26);
    }

    public String formatRow(int row) {
	return Integer.toString(height - row);
    }

}
