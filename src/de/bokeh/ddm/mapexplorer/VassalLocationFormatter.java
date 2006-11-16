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
	int n = col / 26 + 1;
	char c = (char) ('A' + col % 26);
	char[] r = new char[n];
	java.util.Arrays.fill(r, c);
	return String.valueOf(r);
    }

    public String formatRow(int row) {
	return Integer.toString(height - row);
    }

}
