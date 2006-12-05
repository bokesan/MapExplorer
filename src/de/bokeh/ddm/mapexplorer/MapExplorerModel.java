/*
 * $Id: MapExplorerModel.java,v 1.7 2006/03/14 14:47:32 breitko Exp $
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

import java.util.*;

/**
 * Map Explorer data structures and logic.
 * 
 * @author Christoph Breitkopf
 */
public class MapExplorerModel {

    private Map map;
    private LosMap losMap;
    private MovementMap movementMap;
    
    private final Set<Creature> creatures; // LOS sources
    
    private boolean smokeBlocksLos;
    private boolean useMapImage = false;
    private boolean useVassalCoordinates = false;
    
    private final LosCalculator losCalculator;
    
    public MapExplorerModel(int numThreads) {
	smokeBlocksLos = false;
	creatures = new HashSet<Creature>();
	losCalculator = new LosCalculator(numThreads);
	losCalculator.setRandomTestsPerSquare(100);
	setMap(new Map(new Dimension(30,21), "empty"));
    }
    
    /**
     * Clear LOS info.
     */
    public void clearLos() {
	losMap.clear();
    }
    
    /**
     *  Clear movement info.
     */
    public void clearMovement() {
	movementMap.clear();
    }
    
    /**
     * Compute LOS.
     */
    public void computeLos() {
	losCalculator.setMap(map, losMap);
	losCalculator.setCreatures(creatures);
	losCalculator.setSmokeBlocksLos(smokeBlocksLos);
	losCalculator.computeLos();
    }
    
    public void computeMovement() {
	// long start = System.currentTimeMillis();
	movementMap.clear();
	for (Creature c : creatures)
	    movementMap.computeMovement(map, c);
	// long elapsed = System.currentTimeMillis() - start;
	// System.out.println("Movement: " + elapsed + " ms.");
    }

    /**
     * @return Returns the losMap.
     */
    public LosMap getLosMap() {
        return losMap;
    }
    
    /**
     * @return Returns the map.
     */
    public Map getMap() {
        return map;
    }
    
    /**
     * @param map The map to set.
     */
    public final void setMap(Map map) {
        this.map = map;
        this.losMap = new LosMap(map.getDimension());
        this.movementMap = new MovementMap(map.getDimension());
        removeAllCreatures();
    }
    
    public boolean addCreature(Creature c) {
	for (Creature f : creatures) {
	    if (c.overlaps(f))
		return false;
	}
	if (map.canPlaceCreature(c)) {
	    creatures.add(c);
	    return true;
	}
	return false;
    }
    
    public Set<Creature> getCreatures() {
	return Collections.unmodifiableSet(creatures);
    }
    
    public final void removeAllCreatures() {
	creatures.clear();
    }

    /**
     * @return Returns the smokeBlocksLos.
     */
    public boolean isSmokeBlocksLos() {
        return smokeBlocksLos;
    }

    /**
     * @param smokeBlocksLos The smokeBlocksLos to set.
     */
    public void setSmokeBlocksLos(boolean smokeBlocksLos) {
        this.smokeBlocksLos = smokeBlocksLos;
    }

    /**
     * @return Returns the losCalculator.
     */
    public LosCalculator getLosCalculator() {
        return losCalculator;
    }

    /**
     * @return Returns the movementMap.
     */
    public MovementMap getMovementMap() {
        return movementMap;
    }

    /**
     * Toggle Elemental Wall of size x size squares at loc.
     * @param loc a Location
     * @param size size of the wall
     * @return true if the wall could be placed.
     */
    public boolean toggleElementalWall(Location loc, int size) {
	int row = loc.getRow();
	int col = loc.getColumn();
	if (map.get(loc).has(MapFeature.ELEMENTAL_WALL)) {
	    // Remove the wall
	    for (int roff = 0; roff < size; roff++) {
		for (int coff = 0; coff < size; coff++) {
		    MapSquare t = map.get(col + coff, row + roff);
		    if (!t.has(MapFeature.ELEMENTAL_WALL))
			return false;
		}
	    }
	    for (int roff = 0; roff < size; roff++) {
		for (int coff = 0; coff < size; coff++) {
		    map.get(loc.getColumn() + coff, loc.getRow() + roff).removeFeature(MapFeature.ELEMENTAL_WALL);
		}
	    }
	} else {
	    // Add a wall
	    for (int roff = 0; roff < size; roff++) {
		for (int coff = 0; coff < size; coff++) {
		    MapSquare t = map.get(col + coff, row + roff);
		    if (t.hasWall() || t.has(MapFeature.ELEMENTAL_WALL))
			return false;
		}
	    }
	    for (int roff = 0; roff < size; roff++) {
		for (int coff = 0; coff < size; coff++) {
		    map.get(loc.getColumn() + coff, loc.getRow() + roff).addFeature(MapFeature.ELEMENTAL_WALL);
		}
	    }
	}
	return true;
    }

    /**
     * @return Returns the useMapImage.
     */
    public boolean isUseMapImage() {
        return useMapImage;
    }

    /**
     * @param useMapImage The useMapImage to set.
     */
    public void setUseMapImage(boolean useMapImage) {
        this.useMapImage = useMapImage;
    }

    /**
     * @return Returns the useVassalCoordinates.
     */
    public boolean isUseVassalCoordinates() {
        return useVassalCoordinates;
    }

    /**
     * @param useVassalCoordinates The useVassalCoordinates to set.
     */
    public void setUseVassalCoordinates(boolean useVassalCoordinates) {
        this.useVassalCoordinates = useVassalCoordinates;
    }
}
