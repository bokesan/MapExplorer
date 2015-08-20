/*
 * This file is part of Map Explorer.
 * 
 * Copyright 2005-2007 Christoph Breitkopf
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
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;


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
        setLogLevel(Level.WARNING);
        System.out.println("MapExplorer Benchmark - version " + MapExplorer.VERSION);
	int nCpus = Runtime.getRuntime().availableProcessors();
	logger.info("Starting full benchmark.");
	logger.info("Number of processors: " + nCpus);
	logConfig();
	System.out.println("Available threads: " + nCpus);
	Stack<Integer> threads = new Stack<Integer>();
	for (int n = nCpus;; ) {
	    threads.push(n);
	    if (n == 1)
	        break;
	    if ((n & 1) != 0) {
	        n = n / 2 + 1;
	    } else {
	        n = n / 2;
	    }
	}

	losCalculator.shutdown();

	long[][] times = new long[nCpus + 1][];
	
	while (!threads.empty()) {
	    numThreads = threads.pop();
	    if (numThreads == 1) {
	        System.out.print("1 thread:  ");
	    } else {
	        System.out.print(numThreads + " threads: ");
	    }
            System.out.flush();
	    losCalculator = new LosCalculator(numThreads);
	    losCalculator.setRandomTestsPerSquare(randomTestsPerSquare);
	    losCalculator.setSmokeBlocksLos(false);
	    losCalculator.setMap(map, losMap);
	    times[numThreads] = new long[repetitions];
	    for (int i = 0; i < repetitions; i++) {
		logger.info("Threads: " + numThreads + ", run " + (i + 1) + " of " + repetitions);
		long[] result = run();
		times[numThreads][i] = result[3];
		System.out.print('#');
		System.out.flush();
		System.gc();
		try {
		    Thread.sleep(100);
		} catch (InterruptedException ex) {
		    logger.warning("sleep interrupted");
		}
	    }
	    System.out.println();
	    losCalculator.shutdown();
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException ex) {
		logger.warning("sleep interrupted");
	    }
	}
	
	long seqTime = 0;
	for (int i = 1; i <= nCpus; i++) {
	    long[] ts = times[i];
	    if (ts != null) {
	        Arrays.sort(ts);
	        long min = ts[0];
	        long med = ts[repetitions / 2];
	        if (i == 1)
	            seqTime = min;
	        String msg = String.format("%d %s: %.3f seconds (median %.3f seconds), speedup: %.2fx",
	                                   i, (i == 1) ? "thread" : "threads",
	                                   min / 1000.0, med / 1000.0,
	                                   (double) seqTime / min);
	        logger.info(msg);
	        System.out.println(msg);
	    }
	}
    }
    
    private void logConfig() {
        RuntimeMXBean b = ManagementFactory.getRuntimeMXBean();
        System.out.println("  \"java.vm.name\" : \"" + b.getVmName() + "\",");
        System.out.println("  \"java.vm.version\" : \"" + b.getVmVersion() + "\",");
        System.out.print("  \"vm_args\" : [");
        String sep = "";
        for (String s : b.getInputArguments()) {
            System.out.print(sep + " \"" + s + "\"");
            sep = ",";
        }
        System.out.println("],");
        OperatingSystemMXBean o = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("  \"os.name\" : \"" + o.getName() + "\",");
        System.out.println("  \"os.arch\" : \"" + o.getArch() + "\",");
        System.out.println("  \"os.version\" : \"" + o.getVersion() + "\",");
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
	// losCalculator.shutdown();
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
