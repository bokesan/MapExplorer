/*
 * $Id: ColorSettings.java,v 1.7 2005/12/27 17:02:07 breitko Exp $
 * 
 * This file is part of Map Explorer.
 * 
 * Copyright © 2005 Christoph Breitkopf
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

import java.awt.Color;
import java.util.EnumMap;

/**
 * Color settings for Map rendering.
 * 
 * @see MapPanel
 * @author Christoph Breitkopf
 */
public class ColorSettings {

    /** Special features. */
    public enum Special {
	/** Grid lines. */
	GRID,
	/** Walls and solid rock. */
	WALL,
	/** Line-of-Sight markers. */
	LOS,
	/** Creature markers. */
	MARK
    };
    
    private EnumMap<MapFeature, Color> featureColor;
    private EnumMap<Special, Color> specialColor;
    
    private Color defaultColor;
    
    /**
     * Construct a new ColorSettings object and initialize it
     * to the default colors.
     */
    public ColorSettings() {
	featureColor = new EnumMap<MapFeature,Color>(MapFeature.class);
	specialColor = new EnumMap<Special, Color>(Special.class);
	defaultColor = Color.BLACK;
	setDefault();
    }
    
    /**
     * Return the color for a map feature.
     * <p>
     * Returns defaultColor if no color is set for the specified feature.
     * 
     * @param f a MapFeature
     * @return the Color for the MapFeature f.
     */
    public Color getColor(MapFeature f) {
	Color c = featureColor.get(f);
	return (c == null) ? defaultColor : c;
    }
    
    /**
     * Return the color for a map feature.
     * <p>
     * Returns defaultColor if no color is set for the specified feature.
     * 
     * @param s a Special feature
     * @return the Color for the Special feature s.
     */
    public Color getColor(Special s) {
	Color c = specialColor.get(s);
	return (c == null) ? defaultColor : c;
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
     * @param s a Special feature
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
	featureColor.put(MapFeature.SUMMONING_CIRCLE, new Color(30,255,160));
	featureColor.put(MapFeature.SPIKE_STONES, Color.ORANGE);
	featureColor.put(MapFeature.RISKY, Color.ORANGE);
	featureColor.put(MapFeature.BLOOD_ROCK, Color.RED);
	featureColor.put(MapFeature.HAUNTED, new Color(255, 0, 255, 208));
	featureColor.put(MapFeature.PIT, Color.GRAY);
	featureColor.put(MapFeature.LAVA, new Color(255, 128, 0));
	featureColor.put(MapFeature.SMOKE, new Color(48, 48, 64, 208));
	
	featureColor.put(MapFeature.START_A, Color.LIGHT_GRAY);
	featureColor.put(MapFeature.START_B, Color.LIGHT_GRAY);
	featureColor.put(MapFeature.VICTORY_A, Color.PINK);
	featureColor.put(MapFeature.VICTORY_B, Color.PINK);
	
	specialColor.put(Special.WALL, Color.BLACK);
	specialColor.put(Special.GRID, Color.GRAY);
	specialColor.put(Special.LOS, Color.GREEN);
	specialColor.put(Special.MARK, new Color(255,80,40));
    }

    /**
     * @return Returns the defaultColor.
     */
    public Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * @param defaultColor The defaultColor to set.
     */
    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }
    
}
