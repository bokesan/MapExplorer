/*
 * $Id: LosComputation.java,v 1.4 2005/12/23 16:31:39 breitko Exp $
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
import javax.swing.*;

public class LosComputation extends Thread {

    private final MapExplorer app;
    private final MapExplorerModel model;
    private final LosCalculator losCalculator;
    private final Logger logger;
    
    private final MapPanel mapPanel;
    
    public LosComputation(MapExplorer app) {
	this.app = app;
	model = app.getModel();
	this.logger = Logger.getLogger(this.getClass().getPackage().getName());
	mapPanel = app.getMapPanel();
	losCalculator = model.getLosCalculator();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
	model.clearLos();
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		mapPanel.repaint();
		app.setProgressMax(100);
	    }
	});
	
	long startTime = System.currentTimeMillis();
	Thread bgCalc = new Thread() {
	    public void run() {
		model.computeLos();
	    }
	};
	bgCalc.start();
	for (;;) {
	    try {
		bgCalc.join(100);
		if (!bgCalc.isAlive())
		    break;
	    }
	    catch (InterruptedException ex) {
		logger.warning("join() interrupted");
	    }
	    SwingUtilities.invokeLater(new ProgressSetter(losCalculator.getPercentCompleted()));
	}
	long elapsedTime = System.currentTimeMillis() - startTime;
	final String resultMsg = "LOS elapsed time: " + elapsedTime + "ms";
	
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
	    // This can make the program rather slow:
	    // app.getMapPanel().repaint();
	}
    }
}
