package de.bokeh.ddm.mapexplorer;

public class LocationFormatterFactory {

    public static LocationFormatter getDefaultFormatter(Dimension size) {
	return new DefaultLocationFormatter();
    }

    public static LocationFormatter getVassalFormatter(Dimension size) {
	return new VassalLocationFormatter(size);
    }
    
}
