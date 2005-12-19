/*
 * $Id: LosComputation.java,v 1.3 2005/12/19 11:36:31 breitko Exp $
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
import java.util.logging.*;
import javax.swing.*;

public class LosComputation extends Thread {

    private final Location loc;
    private final MapExplorer app;
    private final int randomTestsPerSquare;
    private final Logger logger;
    private boolean smokeBlocksLos;
    private CreatureSize creatureSize;
    
    private Map map;
    private LosMap losMap;
    private MapPanel mapPanel;
    
    public LosComputation(MapExplorer app, Location loc, int rndTests, Logger logger) {
	this.loc = loc;
	this.app = app;
	this.randomTestsPerSquare = rndTests;
	this.logger = logger;
	mapPanel = app.getMapPanel();
	map = mapPanel.getMap();
	losMap = mapPanel.getLosMap();
	smokeBlocksLos = false;
	creatureSize = CreatureSize.MEDIUM;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
	Runnable repaintMap = new Runnable() {
	    public void run() {
		mapPanel.repaint();
	    }
	};
	
	final int sq = creatureSize.sizeSquares();
	losMap.clear();
	int numLos = 0;
	int numRndLos = 0;
	int testsLosSquares = 0;
	int squaresTested = 0;
	
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		mapPanel.repaint();
		app.setProgressMax(map.getHeight() * sq * sq);
	    }
	});
	
	long startTime = System.currentTimeMillis();
	final int height = map.getHeight();
	final int width = map.getWidth();
	final Set<Line> walls = map.getWalls(smokeBlocksLos);
	int progressOffs = 0;
	for (int y = 0; y < sq; y++) {
	    for (int x = 0; x < sq; x++) {
		Location xloc = new Location(loc.getColumn() + x, loc.getRow() + y);
		LosTester los = new LosTester(xloc, map.getDimension(), walls, randomTestsPerSquare, logger);
		for (int row = 0; row < height; row++) {
		    for (int col = 0; col < width; col++) {
			MapSquare s = map.get(col, row);
			if (!(s.isSolid() || s.isMarked() || losMap.get(col, row))) {
			    squaresTested++;
			    int r = los.testLocation(new Location(col, row));
			    if (r >= 0) {
				if (r > 0) {
				    numRndLos++;
				    testsLosSquares += r;
				}
				numLos++;
				losMap.set(col, row);
				SwingUtilities.invokeLater(repaintMap);
			    }
			}
		    }
		    SwingUtilities.invokeLater(new ProgressSetter(progressOffs + row+1));
		}
		progressOffs += height;
	    }
	}
	long elapsedTime = System.currentTimeMillis() - startTime;
	if (numRndLos != 0)
	    logger.warning(loc + ": " + numRndLos + " squares found by sampling (avg. " + ((double) testsLosSquares / numRndLos) + " random tests needed)\n");
	final String resultMsg;
	if (numRndLos == 0)
	    resultMsg = loc + ": LOS to " + numLos + " squares (" + elapsedTime + "ms)";
	else
	    resultMsg = loc + ": LOS to " + numLos + " squares [" + numRndLos + " rnd] (" + elapsedTime + "ms)";
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		app.setBusy(false, resultMsg);
	    }
	});
    }

    class ProgressSetter implements Runnable {
	private final int value;
	public ProgressSetter(int n) {
	    this.value = n;
	}
	public void run() {
	    app.setProgress(value);
	}
    }

    public void setCreatureSize(CreatureSize size) {
	creatureSize = size;
    }
    
    /**
     * @param smokeBlocksLos The smokeBlocksLos to set.
     */
    public void setSmokeBlocksLos(boolean smokeBlocksLos) {
        this.smokeBlocksLos = smokeBlocksLos;
    }
    
}
