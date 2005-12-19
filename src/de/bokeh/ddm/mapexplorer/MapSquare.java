/*
 * $Id: MapSquare.java,v 1.4 2005/12/19 11:36:31 breitko Exp $
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
import java.util.EnumSet;


/**
 * A square on the battle map.
 * @author Christoph Breitkopf
 */
public class MapSquare {
    
    private EnumSet<MapFeature> features;
    
    private boolean[] wall; // 0: north, 1: east, 2: south, 3: west
    
    private int directionToIndex(Direction dir) {
	switch (dir) {
	case NORTH: return 0;
	case EAST: return 1;
	case SOUTH: return 2;
	case WEST: return 3;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    private Color color;
    
    private boolean marked;
    
    /**
     * Constructs and initializes an empty map square.
     */
    public MapSquare() {
	features = EnumSet.noneOf(MapFeature.class);
	wall = new boolean[4];
	for (int i = 0; i < 4; i++)
	    wall[i] = false;
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
	return wall[directionToIndex(dir)];
    }
    
    /**
     * Add or remove a wall in a specified direction.
     * @param dir a Direction
     * @param value wether to add or to remove the wall
     */
    public void setWall(Direction dir, boolean value) {
	wall[directionToIndex(dir)] = value;
    }
    
    /**
     * Does thios square have at least one wall?
     * @return True if this square has at least one wall.
     */
    public boolean hasWall() {
	for (int i = 0; i < 4; i++)
	    if (wall[i])
		return true;
	return false;
    }
    
    /**
     * Check if this square is solid rock.
     * @return true if this square is solid rock.
     */
    public boolean isSolid() {
	for (int i = 0; i < 4; i++)
	    if (!wall[i])
		return false;
	return true;
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
     * @return Returns the mark.
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * @param mark The mark to set.
     */
    public void setMarked(boolean mark) {
        this.marked = mark;
    }

}
