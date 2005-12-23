/*
 * $Id: LosBenchmark.java,v 1.3 2005/12/23 16:20:57 breitko Exp $
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

import java.util.logging.*;


public class LosBenchmark {

    private final Map map;
    private Logger logger;
    private final int randomTestsPerSquare;
    private boolean smokeBlocksLos;
    private int totalLos;
    
    public LosBenchmark(Map map, int randomTestsPerSquare) {
	this.map = map;
	this.randomTestsPerSquare = randomTestsPerSquare;
	smokeBlocksLos = false;
	logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    public void run() {
	logger.info("Starting LOS benchmark for map " + map.getName());
	long startTime = System.currentTimeMillis();
	
	final int height = map.getHeight();
	final int width = map.getWidth();
	int numSquaresTested = 0;
	int numRnd = 0;
	totalLos = 0;
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		Location loc = new Location(col, row);
		MapSquare s = map.get(loc);
		if (s.isSolid()) {
		    logger.info(loc + ": solid rock");
		} else {
		    LosTester lt = new LosTester(loc, map.getDimension(), map.getWalls(smokeBlocksLos), randomTestsPerSquare, logger);
		    numRnd += testLos(loc, lt);
		    numSquaresTested++;
		}
	    }
	}
	
	long elapsedTime = System.currentTimeMillis() - startTime;
	logger.info("Total time: " + elapsedTime + " ms.");
	logger.info("Squares tested: " + numSquaresTested);
	logger.info("LOS squares total: " + totalLos);
	if (numSquaresTested > 0) {
	    logger.info("Avg. " + (elapsedTime / numSquaresTested) + " ms. per tested square.");
	}
	logger.info(numRnd + " found by random testing.");
    }
    
    private int testLos(Location loc, LosTester t) {
	final int height = map.getHeight();
	final int width = map.getWidth();
	int numLos = 0;
	int numRndLos = 0;
	long start = System.currentTimeMillis();
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		int r = t.testLocation(new Location(col, row));
		if (r >= 0) {
		    if (r > 0) {
			numRndLos++;
		    }
		    numLos++;
		}
	    }
	}
	long elapsed = System.currentTimeMillis() - start;
	totalLos += numLos;
	String msg = loc + ": " + numLos + " LOS squares, " + elapsed + " ms.";
	if (numRndLos > 0)
	    msg += " [" + numRndLos + " rnd]";
	logger.info(msg);
	return numRndLos;
    }

    /**
     * @param smokeBlocksLOS The smokeBlocksLOS to set.
     */
    public void setSmokeBlocksLos(boolean smokeBlocksLOS) {
        this.smokeBlocksLos = smokeBlocksLOS;
    }

    /**
     * @return Returns the smokeBlocksLOS.
     */
    public boolean isSmokeBlocksLos() {
        return smokeBlocksLos;
    }
    
}
