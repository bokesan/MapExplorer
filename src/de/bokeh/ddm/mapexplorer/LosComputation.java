package de.bokeh.ddm.mapexplorer;

import java.util.logging.*;
import javax.swing.*;

public class LosComputation extends Thread {

    private final Location loc;
    private final MapExplorer app;
    private final int randomTestsPerSquare;
    private final Logger logger;
    
    private Map map;
    private MapPanel mapPanel;
    
    public LosComputation(MapExplorer app, Location loc, int rndTests, Logger logger) {
	this.loc = loc;
	this.app = app;
	this.randomTestsPerSquare = rndTests;
	this.logger = logger;
	mapPanel = app.getMapPanel();
	map = mapPanel.getMap();
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
	
	map.clearLos();
	map.clearMarks();
	map.get(loc).setMarked(true);
	LosTester los = new LosTester(loc, map.getDimension(), map.getWalls(), randomTestsPerSquare, logger);
	int numLos = 0;
	int numRndLos = 0;
	int testsLosSquares = 0;
	int squaresTested = 0;

	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		mapPanel.repaint();
		app.setProgressMax(map.getHeight());
	    }
	});
	
	long startTime = System.currentTimeMillis();
	for (int row = 0; row < map.getHeight(); row++) {
	    for (int col = 0; col < map.getWidth(); col++) {
		if (row != loc.getRow() || col != loc.getColumn()) {
		    MapSquare s = map.get(col, row);
		    if (!s.isSolid()) {
			squaresTested++;
			// int r = los.testLocation(new Location(col, row));
			int r = los.testLocation(col, row);
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
	    }
	    SwingUtilities.invokeLater(new ProgressSetter(row+1));
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
    
}
