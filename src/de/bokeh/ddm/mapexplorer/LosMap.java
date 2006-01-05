/*
 * $Id: LosMap.java,v 1.4 2006/01/05 12:55:51 breitko Exp $
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

import java.util.BitSet;

/**
 * LOS information for a map.
 * <p>
 * Just stores a single boolean value for each square.
 * All methods are synchronized, so that multiple threads
 * can access a LosMap concurrently.
 * 
 * @author Christoph Breitkopf
 */
public class LosMap {

    private final Dimension size;
    private final int width; // performance
    private final BitSet los;

    /**
     * Create a new LosMap.
     * 
     * @param size size of the map, a Dimension
     */
    public LosMap(Dimension size) {
	this.size = size;
	this.width = size.getWidth();
	this.los = new BitSet(width * size.getHeight());
    }

    /**
     * Return the size of this LosMap.
     * @return the size of this LosMap.
     */
    public Dimension getSize() {
	return size;
    }

    /**
     * Clear all LOS information.
     * <p>
     * All squares are reset to false.
     */
    synchronized public void clear() {
	los.clear();
    }
    
    /**
     * Get the LOS state of a map square.
     * 
     * @param col the column
     * @param row the row
     * @return the LOS state of the map square at col,row.
     */
    synchronized public boolean get(int col, int row) {
	return los.get(row * width + col);
    }
    
    /**
     * Get the LOS state of a map square.
     * 
     * @param loc a Location
     * @return the LOS state of the map square at loc
     */
    synchronized public boolean get(Location loc) {
	return los.get(loc.getRow() * width + loc.getColumn());
    }
    
    /**
     * Set the LOS state of a map square.
     * 
     * @param col the column
     * @param row the row
     * @param value the LOS state to set
     */
    synchronized public void set(int col, int row, boolean value) {
	los.set(row * width + col, value);
    }
    
    /**
     * Set the LOS state of a map square to true.
     * 
     * @param col the column
     * @param row the row
     */
    synchronized public void set(int col, int row) {
	los.set(row * width + col);
    }
    
    /**
     * Set the LOS state of a map square to true.
     * 
     * @param loc a Location
     */
    synchronized public void set(Location loc) {
	los.set(loc.getRow() * width + loc.getColumn());
    }
    
    /**
     * Return the numer of LOS squares.
     * @return The number of LOS squares. 
     */
    synchronized public int getLosCount() {
	return los.cardinality();
    }
    
}
