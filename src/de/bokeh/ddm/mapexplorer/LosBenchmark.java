/*
 * $Id: LosBenchmark.java,v 1.6 2006/03/14 14:47:16 breitko Exp $
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
import java.util.logging.*;


/**
 * Run a benchmark by computing LOS for all map squares that are not
 * solid rock.
 * 
 * @author Christoph Breitkopf
 */
public class LosBenchmark {

    private final Map map;
    private final LosMap losMap;
    private final LosCalculator losCalculator;
    private final Logger logger;
    private int totalLos;
    
    public LosBenchmark(Map map, int numThreads, int randomTestsPerSquare) {
	this.map = map;
	this.losMap = new LosMap(map.getDimension());
	losCalculator = new LosCalculator(numThreads);
	losCalculator.setRandomTestsPerSquare(randomTestsPerSquare);
	losCalculator.setSmokeBlocksLos(false);
	losCalculator.setMap(map, losMap);
	logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /**
     * Run benchmark. 
     */
    public void run() {
	logger.info("Starting LOS benchmark for map " + map.getName());
	long startTime = System.currentTimeMillis();

	Creature creature = new Creature(CreatureSize.MEDIUM);
	Set<Creature> creatures = new HashSet<Creature>();
	creatures.add(creature);
	losCalculator.setCreatures(creatures);
	
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
		    creature.setLocation(loc);
		    long start = System.currentTimeMillis();
		    losCalculator.computeLos();
		    long elapsed = System.currentTimeMillis() - start;
		    int numLos = losCalculator.getNumLos();
		    int numRndLos = losCalculator.getNumRndLos();
		    String msg = loc + ": " + numLos + " LOS squares, " + elapsed + " ms.";
		    if (numRndLos > 0)
			msg += " [" + numRndLos + " rnd]";
		    logger.info(msg);
		    // LosTester lt = new LosTester(loc, map.getDimension(), map.getWalls(smokeBlocksLos), randomTestsPerSquare, logger);
		    // numRnd += testLos(loc, lt);
		    numSquaresTested++;
		    totalLos += numLos;
		    numRnd += numRndLos;
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
	losCalculator.shutdown();
    }
    
    /**
     * @param smokeBlocksLOS The smokeBlocksLOS to set.
     */
    public void setSmokeBlocksLos(boolean smokeBlocksLOS) {
	losCalculator.setSmokeBlocksLos(smokeBlocksLOS);
    }

    /**
     * @return Returns the smokeBlocksLOS.
     */
    public boolean isSmokeBlocksLos() {
        return losCalculator.isSmokeBlocksLos();
    }
    
}
