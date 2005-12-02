// $Id: MapExplorer.java,v 1.5 2005/12/02 13:33:35 breitko Exp $

package de.bokeh.ddm.mapexplorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.logging.*;


/**
 * Map Explorer application.
 * 
 * @author Christoph Breitkopf
 */
public class MapExplorer implements ActionListener {

    public static final String VERSION = "20051201";
    
    // ActionCommands
    private static final String ACTION_LOAD_MAP = "loadMap";
    private static final String ACTION_CLEAR_LOS = "clearLOS";
    
    
    static Logger logger = Logger.getLogger(MapReader.class.getName());

    private JFrame appFrame;
    private JToolBar toolBar;
    private MapPanel mapPanel;
    private JToolBar statusPanel;
    private Map map;
    private JLabel lblStatus;
    private JLabel lblResults;
    private int randomTestsPerSquare;
    private boolean testAll;
    private boolean busy;
    
    private JFileChooser fileChooser;
    
    private JProgressBar progress;

    public MapExplorer() {
	fileChooser = null;
	randomTestsPerSquare = 100;
	testAll = false;
	busy = false;
	map = new Map(new Dimension(30, 21), "no map loaded");
    }
    
    private void setTitle() {
	appFrame.setTitle("Map Explorer (version " + VERSION + ") - " + map.getName());
    }
    
    public void setMap(Map m) {
	map = m;
	setTitle();
	mapPanel.setMap(map);
    }

    /**
     * Create the GUI. 
     */
    private void createComponents() {
	appFrame = new JFrame();
	setTitle();
	
	createToolBar();
	
	mapPanel = new MapPanel(map);
	
	createStatusPanel();

	appFrame.getContentPane().add(toolBar, BorderLayout.NORTH);
	appFrame.getContentPane().add(mapPanel, BorderLayout.CENTER);
	appFrame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
	
	appFrame.pack();
	appFrame.setVisible(true);
    }
    
    private void createToolBar() {
	toolBar = new JToolBar();
	
	JButton btn = new JButton("load Map");
	btn.setActionCommand(ACTION_LOAD_MAP);
	btn.addActionListener(this);
	toolBar.add(btn);
	
	toolBar.addSeparator();
	
	btn = new JButton("clear LOS");
	btn.setActionCommand(ACTION_CLEAR_LOS);
	btn.addActionListener(this);
	toolBar.add(btn);
	
	toolBar.setFloatable(false);
    }
    
    private void createStatusPanel() {
	statusPanel = new JToolBar();
	lblStatus = new JLabel("Click on map to show LOS");
	lblStatus.setAlignmentY(Component.CENTER_ALIGNMENT);
	statusPanel.add(lblStatus);
	statusPanel.addSeparator(new java.awt.Dimension(50, 1));
	
	lblResults = new JLabel();
	statusPanel.add(lblResults);
	lblResults.setVisible(false);

	progress = new JProgressBar(0, 30 * 21);
	progress.setStringPainted(true);
	statusPanel.add(progress);
	progress.setVisible(false);
	
	statusPanel.setFloatable(false);
    }
    
    
    private void start() {
	createComponents();

	appFrame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	});
	
	final MapExplorer thisApp = this;
	mapPanel.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		if (isBusy()) {
		    Toolkit.getDefaultToolkit().beep();
		} else {
		    setBusy(true, "computing LOS...");
		    Location loc = mapPanel.getLocation(e.getX(), e.getY());
		    if (loc != null) {
			LosComputation lc = new LosComputation(thisApp, loc, randomTestsPerSquare, logger);
			lc.start();
		    } else {
			setBusy(false, null);
		    }
		}
	    }
	});
    }
    
    public void setStatusText(String s) {
	lblStatus.setText(s);
    }
    
    public static void main(String[] args) {
	MapExplorer app = new MapExplorer();

	String mapFile = "Fane_of_Lolth.map";
	for (String s : args) {
	    if (s.equals("-version")) {
		System.out.println("Map Explorer version " + VERSION);
		return;
	    }
	    mapFile = s;
	}

	//app.setTestAll(true);
	app.setRandomTestsPerSquare(100);
	app.start();
	app.loadMap(mapFile);
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

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (isBusy()) {
	    Toolkit.getDefaultToolkit().beep();
	} else {
	    String cmd = e.getActionCommand();
	    if (cmd.equals(ACTION_LOAD_MAP)) {
		if (fileChooser == null) {
		    fileChooser = new JFileChooser();
		    ExtensionFileFilter ff = new ExtensionFileFilter("Map files");
		    ff.addExtension("map");
		    fileChooser.addChoosableFileFilter(ff);
		}
		int retval = fileChooser.showOpenDialog(appFrame);
		if (retval == JFileChooser.APPROVE_OPTION) {
		    File file = fileChooser.getSelectedFile();
		    loadMap(file.getPath());
		}
	    }
	    else if (cmd.equals(ACTION_CLEAR_LOS)) {
		map.clearLos();
		map.clearMarks();
		lblResults.setText("");
		mapPanel.repaint();
	    }
	}
    }
    
    public void loadMap(String fileName) {
	try {
	    setMap(new MapReader().read(fileName));
	    lblResults.setVisible(false);
	}
	catch (SyntaxError err) {
	    String msg = "Syntax error in file '" + err.getFile() + "', line " + err.getLine();
	    msg += ":\n" + err.getMessage();
	    JOptionPane.showMessageDialog(appFrame, msg, "Error loading map", JOptionPane.ERROR_MESSAGE);
	}
	catch (FileNotFoundException ex) {
	    String msg = "File not found:\n" + fileName;
	    JOptionPane.showMessageDialog(appFrame, msg, "Error loading map", JOptionPane.ERROR_MESSAGE);
	}
	catch (IOException ex) {
	    String msg = "IO-Error:\n" + ex.toString();
	    JOptionPane.showMessageDialog(appFrame, msg, "Error loading map", JOptionPane.ERROR_MESSAGE);
	}
	
    }

    /**
     * @return Returns the busy.
     */
    public synchronized boolean isBusy() {
        return busy;
    }

    /**
     * @param busy The busy to set.
     */
    public synchronized void setBusy(boolean busy, String msg) {
        this.busy = busy;
        if (busy) {
            lblResults.setVisible(false);
            progress.setVisible(true);
	    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    lblStatus.setText(msg);
        } else {
            progress.setVisible(false);
            if (msg != null) {
        	lblResults.setText(msg);
        	lblResults.setVisible(true);
            }
            appFrame.setCursor(Cursor.getDefaultCursor());
            lblStatus.setText("Click on map to show LOS");
        }
    }

    /**
     * @return Returns the mapPanel.
     */
    public MapPanel getMapPanel() {
        return mapPanel;
    }
    
    public void setProgressMax(int max) {
	progress.setMaximum(max);
	progress.setValue(0);
    }
    
    public void setProgress(int n) {
	progress.setValue(n);
    }
}
