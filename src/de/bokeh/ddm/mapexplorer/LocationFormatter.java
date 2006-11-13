package de.bokeh.ddm.mapexplorer;

public interface LocationFormatter {

    public String formatColumn(int col);
    public String formatRow(int row);
    public String format(Location loc);
    
}
