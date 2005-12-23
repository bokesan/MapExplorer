/*
 * $Id: Creature.java,v 1.1 2005/12/23 16:31:39 breitko Exp $
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

/**
 * A creature.
 * <p>
 * Currently used only as a LOS source.
 * 
 * @author Christoph Breitkopf
 */
public class Creature {

    private Location location;
    private final CreatureSize size;
    
    public Creature(CreatureSize size) {
	this.size = size;
    }
    
    public Creature(CreatureSize size, Location loc) {
	this.size = size;
	location = loc;
    }
    
    public boolean overlaps(Creature other) {
	return (overlaps(this, other) || overlaps(other, this));
    }
    
    private boolean overlaps(Creature a, Creature b) {
	int sz = a.size.sizeSquares() - 1;
	int aCol = a.location.getColumn();
	int aRow = a.location.getRow();
	int bCol = b.location.getColumn();
	int bRow = b.location.getRow();
	return (aCol <= bCol && aCol + sz >= bCol
		&& aRow <= bRow && aRow + sz >= bRow);
    }

    /**
     * @return Returns the location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location The location to set.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return Returns the size.
     */
    public CreatureSize getSize() {
        return size;
    }
    
    
}
