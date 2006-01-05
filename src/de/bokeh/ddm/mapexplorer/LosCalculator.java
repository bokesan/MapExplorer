/*
 * $Id: LosCalculator.java,v 1.5 2006/01/05 12:55:51 breitko Exp $
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
import java.util.concurrent.*;

/**
 * Line-of-sight calculator.
 * <p>
 * Provides a method to calculate line-of-sight for a complete map.
 * 
 * @author Christoph Breitkopf
 */
public class LosCalculator {
    
    private final ExecutorService tpe;

    private Map map;
    private LosMap los;
    private int numRndLos;
    
    private Set<Creature> creatures;
    private boolean smokeBlocksLos;
    private int totalTasks;
    private int tasksDone;
    private int randomTestsPerSquare;
    private final Logger logger;
    private Set<Line> walls;

    
    /**
     * Constructs and initializes a new LosCalculator.
     * 
     * @param numThreads number of threads to use for LOS calculation
     */
    public LosCalculator(int numThreads) {
	tpe = Executors.newFixedThreadPool(numThreads);
	randomTestsPerSquare = 100;
	logger = Logger.getLogger(this.getClass().getPackage().getName());
	// logger.setLevel(Level.WARNING);
	if (numThreads == 1)
	    logger.info("using 1 thread to compute LoS");
	else
	    logger.info("using " + numThreads + " threads to compute LoS");
    }
    
    /**
     * Shut down worker threads.
     */
    public void shutdown() {
	tpe.shutdown();
    }
    
    /**
     * Compute line-of-sight for the complete map.
     */
    public void computeLos() {
	walls = map.getWalls(smokeBlocksLos);
	List<Callable<Object>> ts = new ArrayList<Callable<Object>>();
	
	los.clear();
	synchronized (this) {
	    numRndLos = 0;
	}
	for (Creature c : creatures) {
	    addTasksFor(c, ts);
	}
	setTasksDone(0);
	totalTasks = ts.size();
	for (;;) {
	    try {
		tpe.invokeAll(ts);
		break;
	    }
	    catch (InterruptedException ex) {
		logger.warning("invokeAll() interrupted");
	    }
	}
    }
    
    public void setMap(Map map, LosMap losMap) {
	this.map = map;
	los = losMap;
    }
    
    public void setCreatures(Set<Creature> creatures) {
	this.creatures = creatures;
    }
    
    public int getPercentCompleted() {
	if (totalTasks == 0)
	    return 0;
	int done = getTasksDone();
	return 100 * done / totalTasks;
    }
    
    private void addTasksFor(Location source, List<Callable<Object>> ts) {
	final int height = map.getHeight();
	final int width = map.getWidth();
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		Location target = new Location(col, row);
		if (!target.equals(source))
		    ts.add(new LosTask(source, target, this));
	    }
	}
    }
    
    private void addTasksFor(Creature c, List<Callable<Object>> ts) {
	Location loc = c.getLocation();
	int col = loc.getColumn();
	int row = loc.getRow();
	int sz = c.getSize().sizeSquares();
	for (int xoff = 0; xoff < sz; xoff++) {
	    for (int yoff = 0; yoff < sz; yoff++) {
		addTasksFor(new Location(col+xoff, row+yoff), ts);
	    }
	}
    }

    /**
     * @return Returns the smokeBlocksLOS.
     */
    public boolean isSmokeBlocksLos() {
        return smokeBlocksLos;
    }

    /**
     * @param smokeBlocksLOS The smokeBlocksLOS to set.
     */
    public void setSmokeBlocksLos(boolean smokeBlocksLOS) {
        this.smokeBlocksLos = smokeBlocksLOS;
    }

    /**
     * @return Returns the los.
     */
    public LosMap getLos() {
        return los;
    }

    public Map getMap() {
	return map;
    }

    /**
     * @return Returns the logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @return Returns the walls.
     */
    public Set<Line> getWalls() {
        return walls;
    }
    
    public synchronized void bumpTasksDone() {
	tasksDone++;
    }

    /**
     * @return Returns the tasksDone.
     */
    public synchronized int getTasksDone() {
        return tasksDone;
    }

    /**
     * @param tasksDone The tasksDone to set.
     */
    public synchronized void setTasksDone(int tasksDone) {
        this.tasksDone = tasksDone;
    }

    /**
     * @return Returns the randomTestsPerSquare.
     */
    public int getRandomTestsPerSquare() {
        return randomTestsPerSquare;
    }

    /**
     * @param randomTestsPerSquare The randomTestsPerSquare to set.
     */
    public void setRandomTestsPerSquare(int randomTestsPerSquare) {
        this.randomTestsPerSquare = randomTestsPerSquare;
    }

    /**
     * @return Returns the numLos.
     */
    public int getNumLos() {
	return los.getLosCount();
    }

    /**
     * @return Returns the numRndLos.
     */
    synchronized public int getNumRndLos() {
	return numRndLos;
    }

    public synchronized void bumpNumRndLos() {
	numRndLos++;
    }
}


class LosTask implements Callable<Object> {
    
    private final Location source;
    private final Location target;
    private final LosCalculator context;
    
    public LosTask(Location source, Location target, LosCalculator context) {
	this.source = source;
	this.target = target;
	this.context = context;
    }
    
    public Object call() {
	Map map = context.getMap();
	LosMap losMap = context.getLos();
	LosTester t = new LosTester(source, map.getDimension(), context.getWalls(), context.getRandomTestsPerSquare(), context.getLogger());
	MapSquare s = map.get(target);
	if (!s.isSolid()) {
	    int r = t.testLocation(target);
	    if (r >= 0) {
		losMap.set(target);
		if (r > 0)
		    context.bumpNumRndLos();
	    }
	}
	context.bumpTasksDone();
	return null;
    }
}
