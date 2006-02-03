/*
 * $Id: MapSquare.java,v 1.7 2006/02/03 15:43:29 breitko Exp $
 * 
 * This file is part of Map Explorer.
 * 
 * Copyright © 2005-2006 Christoph Breitkopf
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
    
    private EnumSet<MapFeature> features;
    
    private EnumSet<Direction> wall;
    
    private Color color;
    
    /**
     * Constructs and initializes an empty map square.
     */
    public MapSquare() {
	features = EnumSet.noneOf(MapFeature.class);
	wall = EnumSet.noneOf(Direction.class);
	color = java.awt.Color.WHITE;
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
     * Does this square have a wall in the specified direction?
     * @param dir a Direction
     * @return true if the square has a wall in the Direction dir.
     */
    public boolean getWall(Direction dir) {
	return wall.contains(dir);
    }
    
    /**
     * Add or remove a wall in a specified direction.
     * @param dir a Direction
     * @param value wether to add or to remove the wall
     */
    public void setWall(Direction dir, boolean value) {
	if (value)
	    wall.add(dir);
	else
	    wall.remove(dir);
    }
    
    /**
     * Does this square have at least one wall?
     * @return True if this square has at least one wall.
     */
    public boolean hasWall() {
	return !wall.isEmpty();
    }
    
    /**
     * Check if this square is solid rock.
     * @return true if this square is solid rock.
     */
    public boolean isSolid() {
	return wall.contains(Direction.NORTH)
	    && wall.contains(Direction.EAST)
	    && wall.contains(Direction.SOUTH)
	    && wall.contains(Direction.WEST);
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

    public boolean isDifficult() {
	return (features.contains(MapFeature.DIFFICULT)
		|| features.contains(MapFeature.SPIKE_STONES));
    }
}
