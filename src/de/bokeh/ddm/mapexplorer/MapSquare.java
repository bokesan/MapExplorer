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

import java.awt.Color;
import java.util.EnumSet;


/**
 * A square on the battle map.
 * @author Christoph Breitkopf
 */
public class MapSquare {
    
    private final EnumSet<MapFeature> features;
    private boolean solid; // "impassable" might be better
    private Color color;
    
    /**
     * Constructs and initializes an empty map square.
     */
    public MapSquare() {
	features = EnumSet.noneOf(MapFeature.class);
	color = java.awt.Color.WHITE;
        solid = false;
    }
    
    /**
     * Add or remove feature.
     * @param f a MapFeature
     * @param val boolean which specifies whether to add or to remove this feature
     */
    public void setFeature(MapFeature f, boolean val) {
	if (val)
	    features.add(f);
	else
	    features.remove(f);
    }
    
    /**
     * Add feature.
     * @param f a MapFeature
     */
    public void addFeature(MapFeature f) {
	features.add(f);
    }
    
    /**
     * Remove feature.
     * @param f a MapFeature
     */
    public void removeFeature(MapFeature f) {
	features.remove(f);
    }

    /**
     * Does this square contain a specific feature?
     * @param f a MapFeature
     * @return Returns true if this square contains MapFeature f.
     */
    public boolean has(MapFeature f) {
	return features.contains(f);
    }
    
    
    /**
     * Return the color of this square.
     * @return the Color of this MapSquare.
     */
    public Color getColor() {
	return color;
    }
    
    /**
     * Set the color of this square.
     * @param color a Color
     */
    public void setColor(Color color) {
	this.color = color;
    }

    /**
     * @return Returns the solid.
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * @param solid The solid to set.
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
    }
}
