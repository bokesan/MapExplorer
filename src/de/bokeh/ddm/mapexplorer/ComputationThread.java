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

import java.util.logging.*;
import javax.swing.*;

/**
 * Run map computations in the background.
 * <p>
 * An instance of this class is used by the Map Explorer GUI to
 * run the LOS and movement computation.
 * 
 * @author Christoph Breitkopf
 */
public class ComputationThread extends Thread {

    private static final int PROGRESS_REPORT_INTERVAL = 100; // in milliseconds
    
    private final MapExplorer app;
    private final MapExplorerModel model;
    private final LosCalculator losCalculator;
    private final Logger logger;
    private final boolean computeLos;
    private final boolean computeMovement;
    
    /**
     * Constructs and initializes a new LosComputation object.
     * @param app the application context
     */
    public ComputationThread(MapExplorer app, boolean computeLos, boolean computeMovement) {
	this.app = app;
	this.computeLos = computeLos;
	this.computeMovement = computeMovement;
	model = app.getModel();
	this.logger = Logger.getLogger(this.getClass().getPackage().getName());
	losCalculator = model.getLosCalculator();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
	model.clearLos();
	model.clearMovement();
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		app.setProgressMax(100);
	    }
	});
	
	long startTime = System.currentTimeMillis();
	Thread bgCalcLos = null;
	Thread bgCalcMovement = null;
	
	if (computeLos) {
	    bgCalcLos = new Thread() {
		public void run() {
		    model.computeLos();
		}
	    };
	    bgCalcLos.start();
	}
	if (computeMovement) {
	    bgCalcMovement = new Thread() {
		public void run() {
		    model.computeMovement();
		}
	    };
	    bgCalcMovement.start();
	}
	while (computeLos) {
	    try {
		bgCalcLos.join(PROGRESS_REPORT_INTERVAL);
		if (!bgCalcLos.isAlive())
		    break;
	    }
	    catch (InterruptedException ex) {
		logger.warning("join() interrupted");
	    }
	    SwingUtilities.invokeLater(new ProgressSetter(losCalculator.getPercentCompleted()));
	}
	while (computeMovement) {
	    try {
		bgCalcMovement.join();
		break;
	    }
	    catch (InterruptedException ex) {
		logger.warning("join() interrupted");
	    }
	}
	long elapsedTime = System.currentTimeMillis() - startTime;
	final String resultMsg = "LOS elapsed time: " + elapsedTime + "ms";
	
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		app.setBusy(false, resultMsg);
	    }
	});
    }

    /**
     * A Runnable to call the setProgress method of the application context
     * with a specific value.
     */
    private class ProgressSetter implements Runnable {
	private final int value;
	public ProgressSetter(int n) {
	    this.value = n;
	}
	public void run() {
	    app.setProgress(value);
	    // This can make the program rather slow:
	    // app.getMapPanel().repaint();
	}
    }
}
