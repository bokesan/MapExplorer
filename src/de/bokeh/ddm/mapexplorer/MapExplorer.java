// $Id: MapExplorer.java,v 1.1 2005/11/30 10:54:58 breitko Exp $

package de.bokeh.ddm.mapexplorer;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Cursor;
import javax.swing.*;
import java.io.*;
import java.util.logging.*;


/**
 * Map Explorer application.
 * 
 * @author Christoph Breitkopf
 */
public class MapExplorer {

    public static final String VERSION = "20051130";
    
    static Logger logger = Logger.getLogger(MapReader.class.getName());

    private JFrame frame;
    private MapPanel mapPanel;
    private JPanel statusPanel;
    private Map map;
    private JLabel lblStatus;
    private int randomTestsPerSquare;
    private boolean testAll;
    private boolean busy;

    public MapExplorer() {
	randomTestsPerSquare = 100;
	testAll = false;
	busy = false;
    }
    
    public void setMap(Map m) {
	map = m;
    }

    private void start() {
	frame = new JFrame("Map Explorer (version " + VERSION + ") - " + map.getName());
	mapPanel = new MapPanel(map);
	
	frame.getContentPane().add(mapPanel, BorderLayout.CENTER);
	frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	});
	
	statusPanel = new JPanel();
	//statusPanel.setPreferredSize(new java.awt.Dimension(800, 300));
	lblStatus = new JLabel("Status");
	statusPanel.add(lblStatus);
	frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
	
	frame.pack();
	frame.setVisible(true);
	
	if (testAll) {
	    logger.info("Analyzig map " + map.getName());
	    long startTime = System.currentTimeMillis();
	    for (int row = 0; row < map.getHeight(); row++) {
		for (int col = 0; col < map.getWidth(); col++) {
		    if (!map.get(col, row).isSolid())
		        losFromLocation(new Location(col, row));
		}
	    }
	    long elapsed = System.currentTimeMillis() - startTime;
	    logger.info("Total elapsed time: " + (elapsed / 1000.0) + " seconds.");
	} else {
	    mapPanel.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    if (busy) {
			// beep?
		    } else {
			busy = true;
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Location loc = mapPanel.getLocation(e.getX(), e.getY());
			if (loc != null)
			    losFromLocation(loc);
			frame.setCursor(Cursor.getDefaultCursor());
			busy = false;
		    }
		}
	    });
	}
    }

    private void losFromLocation(Location loc) {
	map.clearLos();
	map.clearMarks();
	map.get(loc).setMarked(true);
	mapPanel.repaint();
	LosTester los = new LosTester(loc, map.getDimension(), map.getWalls(), randomTestsPerSquare, logger);
	int numLos = 0;
	int numRndLos = 0;
	int testsLosSquares = 0;
	int squaresTested = 0;
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
			    mapPanel.repaint();
			}
		    }
		}
	    }
	}
	long elapsedTime = System.currentTimeMillis() - startTime;
	if (numRndLos != 0)
	    logger.warning(loc + ": " + numRndLos + " squares found by sampling (avg. " + ((double) testsLosSquares / numRndLos) + " random tests needed)\n");
	lblStatus.setText(loc + ": " + numLos + " LOS squares out of " + squaresTested + ", " + elapsedTime + "ms"
	    + " (" + numRndLos + " rnd[" + randomTestsPerSquare + "]) [" + los + "]");
    }
    
    public static void main(String[] args) throws IOException {
	MapExplorer app = new MapExplorer();

	String mapFile = "Fane_of_Lolth.map";
	for (String s : args) {
	    if (s.equals("-version")) {
		System.out.println("Map Explorer version " + VERSION);
		return;
	    }
	    mapFile = s;
	}

	try {
	    app.setMap(new MapReader().read(mapFile));
	    //app.setTestAll(true);
	    app.setRandomTestsPerSquare(100);
	    app.start();
	}
	catch (SyntaxError err) {
	    String msg = "Syntax error in file '" + err.getFile() + "', line " + err.getLine();
	    msg += ":\n" + err.getMessage();
	    JOptionPane.showMessageDialog(null, msg, "Error loading map", JOptionPane.ERROR_MESSAGE);
	}
	catch (FileNotFoundException ex) {
	    String msg = "File not found:\n" + mapFile;
	    JOptionPane.showMessageDialog(null, msg, "Error loading map", JOptionPane.ERROR_MESSAGE);
	}
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
     * @return Returns the testAll.
     */
    public boolean isTestAll() {
        return testAll;
    }

    /**
     * @param testAll The testAll to set.
     */
    public void setTestAll(boolean testAll) {
        this.testAll = testAll;
    }
}
