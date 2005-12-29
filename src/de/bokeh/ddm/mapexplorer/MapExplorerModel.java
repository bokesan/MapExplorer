/*
 * $Id: MapExplorerModel.java,v 1.2 2005/12/29 16:09:07 breitko Exp $
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

import java.util.*;

/**
 * Map Explorer data structures and logic.
 * 
 * @author Christoph Breitkopf
 */
public class MapExplorerModel {

    private Map map;
    private LosMap losMap;
    
    private Set<Creature> creatures; // LOS sources
    
    private boolean smokeBlocksLos;
    
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
     * Compute LOS.
     */
    public void computeLos() {
	losCalculator.setMap(map, losMap);
	losCalculator.setCreatures(creatures);
	losCalculator.setSmokeBlocksLos(smokeBlocksLos);
	losCalculator.computeLos();
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
    public void setMap(Map map) {
        this.map = map;
        this.losMap = new LosMap(map.getDimension());
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
    
    public void removeAllCreatures() {
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
}
