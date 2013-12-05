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

import java.util.*;
import java.util.logging.*;
import java.io.*;


/**
 * Run a benchmark by computing LOS for all map squares that are not
 * solid rock.
 * 
 * @author Christoph Breitkopf
 */
public class LosBenchmark {

    private final Map map;
    private final LosMap losMap;
    private LosCalculator losCalculator;
    private final Logger logger;
    private int numThreads;
    private final int randomTestsPerSquare;
    private boolean writeLosFile = false;
    private int repetitions = 5;
    
    /**
     * Create new LosBenchmark.
     * 
     * @param map the Map to test
     * @param numThreads number of threads to use
     * @param randomTestsPerSquare number of random samples per squares
     */
    public LosBenchmark(Map map, int numThreads, int randomTestsPerSquare) {
	this.map = map;
	this.losMap = new LosMap(map.getDimension());
 	losCalculator = new LosCalculator(numThreads);
	losCalculator.setRandomTestsPerSquare(randomTestsPerSquare);
	losCalculator.setSmokeBlocksLos(false);
	losCalculator.setMap(map, losMap);
	logger = Logger.getLogger(this.getClass().getPackage().getName());
	this.numThreads = numThreads;
	this.randomTestsPerSquare = randomTestsPerSquare;
    }
    
    public void setLogLevel(Level level) {
        logger.setLevel(level);
    }

    public void fullBenchmark() {
	int nCpus = Runtime.getRuntime().availableProcessors();
	logger.info("Starting full benchmark.");
	logger.info("Number of processors: " + nCpus);
	Stack<Integer> threads = new Stack<Integer>();
	for (int n = nCpus; n >= 1; n /= 2)
	    threads.push(n);

	losCalculator.shutdown();

	while (!threads.empty()) {
	    numThreads = threads.pop();
	    losCalculator = new LosCalculator(numThreads);
	    losCalculator.setRandomTestsPerSquare(randomTestsPerSquare);
	    losCalculator.setSmokeBlocksLos(false);
	    losCalculator.setMap(map, losMap);
	    long[] times = new long[repetitions];
	    for (int i = 0; i < repetitions; i++) {
		logger.info("Threads: " + numThreads + ", run " + (i + 1) + " of " + repetitions);
		long[] result = run();
		times[i] = result[3];
	    }
	    Arrays.sort(times);
	    logger.info("Threads: " + numThreads + " times: " + Arrays.toString(times));
	    losCalculator.shutdown();
	    try {
		Thread.sleep(2000);
	    } catch (InterruptedException ex) {
		logger.warning("sleep interrupted");
	    }
	}
    }
    
    /**
     * Run benchmark.
     * 
     * @return A four-element array of <code>long</code> values.
     * The value at index 0 contains the number of squares tested for LOS.
     * Index 1 contains the total number of LOS-Squares found.
     * Index 2 contains the number of squares found by random sampling.
     * Index 3 contains the elapsed time in milliseconds.
     */
    public long[] run() {
	logger.info("Starting LOS benchmark for map " + map.getName());
	logger.info("Number of threads: " + numThreads);
	
	PrintWriter losFile = null;
	if (writeLosFile) {
	    String name = map.getName();
	    if (losCalculator.isSmokeBlocksLos())
	        name += ".smoke";
	    name += ".los";
	    try {
	        losFile = new PrintWriter(name);
	    } catch (FileNotFoundException ex) {
	        logger.warning("can't write los file " + name);
	    }
	}
	
	long startTime = System.nanoTime();

	Creature creature = new Creature(CreatureSize.MEDIUM);
	Set<Creature> creatures = new HashSet<Creature>();
	creatures.add(creature);
	losCalculator.setCreatures(creatures);
	
	final int height = map.getHeight();
	final int width = map.getWidth();
	int numSquaresTested = 0;
	int numRnd = 0;
	int totalLos = 0;
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		Location loc = new Location(col, row);
		MapSquare s = map.get(loc);
		if (s.isSolid()) {
		    // logger.info(loc + ": solid rock");
		    if (losFile != null)
		        losFile.println(loc + " rock");
		} else {
		    if (losFile != null)
		        losFile.print(loc);
		    creature.setLocation(loc);
		    long start = System.nanoTime();
		    losCalculator.computeLos();
		    long elapsed = (System.nanoTime() - start) / 1000000;
		    if (losFile != null) {
		        LosMap los = losCalculator.getLos();
		        for (int y = 0; y < height; y++) {
		            for (int x = 0; x < width; x++) {
		                if (los.get(x, y)) {
		                    losFile.print(' ');
		                    losFile.print(new Location(x, y));
		                }
		            }
		        }
			losFile.println();
		    }
		    
		    int numLos = losCalculator.getNumLos();
		    int numRndLos = losCalculator.getNumRndLos();
		    String msg = loc + ": " + numLos + " LOS squares, " + elapsed + " ms.";
		    if (numRndLos > 0)
			msg += " [" + numRndLos + " rnd]";
		    // logger.info(msg);
		    // LosTester lt = new LosTester(loc, map.getDimension(), map.getWalls(smokeBlocksLos), randomTestsPerSquare, logger);
		    // numRnd += testLos(loc, lt);
		    numSquaresTested++;
		    totalLos += numLos;
		    numRnd += numRndLos;
		}
	    }
	}
	
	long elapsedTime = (System.nanoTime() - startTime) / 1000000;
	if (losFile != null)
	    losFile.close();
	logger.info("Total time: " + elapsedTime + " ms.");
	logger.info("Squares tested: " + numSquaresTested);
	logger.info("LOS squares total: " + totalLos);
	if (numSquaresTested > 0) {
	    logger.info("Avg. " + (elapsedTime / numSquaresTested) + " ms. per tested square.");
	}
	logger.info(numRnd + " found by random testing.");
	//losCalculator.shutdown();
        return new long[]{numSquaresTested, totalLos, numRnd, elapsedTime};
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

    public boolean isWriteLosFile() {
        return writeLosFile;
    }

    public void setWriteLosFile(boolean writeLosFile) {
        this.writeLosFile = writeLosFile;
    }
    
}
