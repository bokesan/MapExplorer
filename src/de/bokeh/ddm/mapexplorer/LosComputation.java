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
    private MapPanel mapPanel;
    
    public LosComputation(MapExplorer app, Location loc, int rndTests, Logger logger) {
	this.loc = loc;
	this.app = app;
	this.randomTestsPerSquare = rndTests;
	this.logger = logger;
	mapPanel = app.getMapPanel();
	map = mapPanel.getMap();
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
	map.clearLos();
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
			if (!(s.isSolid() || s.isMarked() || s.isLos())) {
			    squaresTested++;
			    int r = los.testLocation(new Location(col, row));
			    if (r >= 0) {
				if (r > 0) {
				    numRndLos++;
				    testsLosSquares += r;
				}
				numLos++;
				s.setLos(true);
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
