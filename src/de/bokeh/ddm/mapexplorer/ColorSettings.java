package de.bokeh.ddm.mapexplorer;

import java.awt.Color;
import java.util.EnumMap;

public class ColorSettings {

    /** Special features. */
    public enum Special { GRID, WALL, LOS, MARK };
    
    private EnumMap<MapFeature, Color> featureColor;
    private EnumMap<Special, Color> specialColor;
    
    /**
     * Construct a new ColorSettings object and initialize it
     * to the default colors.
     */
    public ColorSettings() {
	featureColor = new EnumMap<MapFeature,Color>(MapFeature.class);
	specialColor = new EnumMap<Special, Color>(Special.class);
	setDefault();
    }
    
    /**
     * Return the color for a map feature.
     * <p>
     * Returns null if no color is set for the specified feature.
     * 
     * @param f a MapFeature
     * @return the Color for the MapFeature f.
     */
    public Color getColor(MapFeature f) {
	return featureColor.get(f);
    }
    
    /**
     * Return the color for a map feature.
     * <p>
     * Returns null if no color is set for the specified feature.
     * 
     * @param s a Special feature
     * @return the Color for the Special feature s.
     */
    public Color getColor(Special s) {
	return specialColor.get(s);
    }
    
    /**
     * Set the Color for MapFeature f.
     * @param f a MapFeature
     * @param c a Color
     */
    public void setColor(MapFeature f, Color c) {
	featureColor.put(f, c);
    }
    
    /**
     * Set the Color for Special feature s.
     * @param f a Special feature
     * @param c a Color
     */
    public void setColor(Special s, Color c) {
	specialColor.put(s, c);
    }
    
    /**
     * Set default colors.
     */
    public void setDefault() {
	featureColor.put(MapFeature.DIFFICULT, Color.YELLOW);
	featureColor.put(MapFeature.SACRED_CIRCLE, Color.CYAN);
	featureColor.put(MapFeature.SPIKE_STONES, Color.ORANGE);
	featureColor.put(MapFeature.BLOOD_ROCK, Color.RED);
	featureColor.put(MapFeature.HAUNTED, new Color(255, 0, 255));
	featureColor.put(MapFeature.PIT, Color.GRAY);
	
	featureColor.put(MapFeature.START_A, Color.LIGHT_GRAY);
	featureColor.put(MapFeature.START_B, Color.LIGHT_GRAY);
	featureColor.put(MapFeature.VICTORY_A, Color.PINK);
	featureColor.put(MapFeature.VICTORY_B, Color.PINK);
	
	specialColor.put(Special.WALL, Color.BLACK);
	specialColor.put(Special.GRID, Color.GRAY);
	specialColor.put(Special.LOS, Color.GREEN);
	specialColor.put(Special.MARK, new Color(255,140,32));
    }
    
}
