package de.bokeh.ddm.mapexplorer;

import java.util.logging.*;


public class LosBenchmark {

    private final Map map;
    private Logger logger;
    private final int randomTestsPerSquare;
    private boolean smokeBlocksLOS;
    private int totalLos;
    
    public LosBenchmark(Map map, int randomTestsPerSquare) {
	this.map = map;
	this.randomTestsPerSquare = randomTestsPerSquare;
	smokeBlocksLOS = false;
    }
    
    public void run() {
	logger = Logger.getLogger(this.getClass().getName());
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
		    LosTester lt = new LosTester(loc, map.getDimension(), map.getWalls(smokeBlocksLOS), randomTestsPerSquare, logger);
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
    public void setSmokeBlocksLOS(boolean smokeBlocksLOS) {
        this.smokeBlocksLOS = smokeBlocksLOS;
    }

    /**
     * @return Returns the smokeBlocksLOS.
     */
    public boolean isSmokeBlocksLOS() {
        return smokeBlocksLOS;
    }
    
}
