/*
 * $Id: MapExplorer.java,v 1.16 2006/01/05 13:33:37 breitko Exp $
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Map Explorer application.
 * 
 * @author Christoph Breitkopf
 */
public class MapExplorer implements ActionListener, ItemListener {

    public static final String VERSION = "20060105";
    
    // ActionCommands
    private static final String ACTION_LOAD_MAP = "loadMap";
    private static final String ACTION_CLEAR_LOS = "clearLos";
    
    private JFrame appFrame;
    private JToolBar toolBar;
    private MapPanel mapPanel;
    private JToolBar statusPanel;
    private JLabel lblStatus;
    private JLabel lblResults;
    private JCheckBox chkSmoke;
    private JComboBox cmbSize;
    private boolean busy;
    private JFileChooser fileChooser;
    private JProgressBar progress;
    
    private final MapExplorerModel model;

    public MapExplorer(MapExplorerModel model) {
	this.model = model;
	fileChooser = null;
	busy = false;
    }
    
    private void setTitle() {
	appFrame.setTitle("Map Explorer (version " + VERSION + ") - " + model.getMap().getName());
    }
    
    public void setMap(Map map) {
	model.setMap(map);
	setTitle();
	mapPanel.setMap(map);
	mapPanel.setLosMap(model.getLosMap());
	mapPanel.setCreatures(model.getCreatures());
	mapPanel.repaint();
	chkSmoke.setEnabled(map.has(MapFeature.SMOKE));
    }

    /**
     * Create the GUI. 
     */
    private void createComponents() {
	appFrame = new JFrame();
	setTitle();
	
	createToolBar();
	
	mapPanel = new MapPanel(model.getMap());
	mapPanel.setLosMap(model.getLosMap());
	mapPanel.setCreatures(model.getCreatures());
	
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
	
	toolBar.addSeparator();
	JLabel lblSize = new JLabel("Size: ");
	lblSize.setAlignmentY(Component.CENTER_ALIGNMENT);
	toolBar.add(lblSize);
	cmbSize = new JComboBox(CreatureSize.values());
	cmbSize.setSelectedItem(CreatureSize.MEDIUM);
	cmbSize.setEditable(false);
	cmbSize.setToolTipText("The creature size");
	cmbSize.addActionListener(this);
	// cmbSize.setPreferredSize(new java.awt.Dimension(100, cmbSize.getHeight()));
	toolBar.add(cmbSize);
	
	chkSmoke = new JCheckBox("Smoke blocks LOS");
	chkSmoke.addItemListener(this);
	chkSmoke.setEnabled(false);
	toolBar.add(chkSmoke);
	
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
	
	mapPanel.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		if (isBusy()) {
		    Toolkit.getDefaultToolkit().beep();
		} else {
		    Location loc = mapPanel.getLocation(e.getX(), e.getY());
		    if (loc != null) {
			if (placeCreature(loc)) {
			    computeLos();
			    mapPanel.repaint();
			} else
			    Toolkit.getDefaultToolkit().beep();
		    }
		}
	    }
	});
    }

    private boolean placeCreature(Location loc) {
	Set<Creature> oldState = model.getCreatures();
	model.removeAllCreatures();
	Creature c = new Creature((CreatureSize) cmbSize.getModel().getSelectedItem());
	c.setLocation(loc);
	if (model.addCreature(c)) {
	    mapPanel.setCreatures(model.getCreatures());
	    mapPanel.repaint();
	    return true;
	}
	for (Creature cr : oldState)
	    model.addCreature(cr);
	return false;
    }
    
    private void computeLos() {
	if (!model.getCreatures().isEmpty()) {
	    setBusy(true, "computing LOS...");
	    model.setSmokeBlocksLos(chkSmoke.getModel().isSelected());
	    LosComputation c = new LosComputation(this);
	    c.start();
	}
    }
    
    public void setStatusText(String s) {
	lblStatus.setText(s);
    }
    
    public static void main(String[] args) {
	boolean benchmark = false;
	String fileSep = System.getProperty("file.separator");
	if (fileSep == null)
	    fileSep = "/";
	String mapFile = "Fane_of_Lolth.map";
	String p = System.getProperty("mapexplorer.home");
	if (p != null)
	    mapFile = p + fileSep + mapFile;
	
	int numCPUs = Runtime.getRuntime().availableProcessors();
	int rndTests = 100;

	Properties properties = loadProperties();
	p = properties.getProperty("mapexplorer.rndtests");
	if (p != null)
	    rndTests = Integer.parseInt(p);
	p = properties.getProperty("mapexplorer.threads");
	if (p != null)
	    numCPUs = Integer.parseInt(p);
	
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-version")) {
		System.out.println("Map Explorer version " + VERSION);
		return;
	    }
	    if (args[i].equals("-benchmark")) {
		benchmark = true;
	    }
	    else if (args[i].equals("-j")) {
		numCPUs = Integer.parseInt(args[++i]);
	    }
	    else if (args[i].equals("-rnd")) {
		rndTests = Integer.parseInt(args[++i]);
	    }
	    else {
		mapFile = args[i];
	    }
	}

	if (rndTests < 0)
	    rndTests = 0;
	if (numCPUs <= 0)
	    numCPUs = 1;
	
	if (benchmark) {
	    try {
		Map map = new MapReader().read(mapFile);
		LosBenchmark b = new LosBenchmark(map, numCPUs, rndTests);
		b.run();
	    }
	    catch (SyntaxError err) {
		err.printStackTrace();
	    }
	    catch (IOException ex) {
		ex.printStackTrace();
	    }
	} else {
	    MapExplorerModel model = new MapExplorerModel(numCPUs);
	    model.getLosCalculator().setRandomTestsPerSquare(rndTests);
	    MapExplorer app = new MapExplorer(model);
	    app.start();
	    app.getMapPanel().setColors(new ColorSettings(properties));
	    app.loadMap(mapFile);
	}
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (isBusy()) {
	    Toolkit.getDefaultToolkit().beep();
	} else {
	    if (e.getSource() == cmbSize) {
		clearLos();
		return;
	    }
	    String cmd = e.getActionCommand();
	    if (cmd.equals(ACTION_LOAD_MAP)) {
		if (fileChooser == null) {
		    fileChooser = new JFileChooser();
		    ExtensionFileFilter ff = new ExtensionFileFilter("Map files");
		    ff.addExtension("map");
		    fileChooser.addChoosableFileFilter(ff);
		    String homeDir = System.getProperty("mapexplorer.home");
		    if (homeDir != null)
			fileChooser.setCurrentDirectory(new File(homeDir));
		}
		int retval = fileChooser.showOpenDialog(appFrame);
		if (retval == JFileChooser.APPROVE_OPTION) {
		    File file = fileChooser.getSelectedFile();
		    loadMap(file.getPath());
		}
	    }
	    else if (cmd.equals(ACTION_CLEAR_LOS)) {
		clearLos();
	    }
	}
    }

    private void clearLos() {
	model.clearLos();
	model.removeAllCreatures();
	lblResults.setText("");
	mapPanel.repaint();
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
	if (isBusy()) {
	    Toolkit.getDefaultToolkit().beep();
	    return;
	}
	Object source = e.getItemSelectable();
	if (source == chkSmoke) {
	    computeLos();
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
	    cmbSize.setEnabled(false);
        } else {
            progress.setVisible(false);
            if (msg != null) {
        	lblResults.setText(msg);
        	lblResults.setVisible(true);
            }
            appFrame.setCursor(Cursor.getDefaultCursor());
            lblStatus.setText("Click on map to show LOS");
            cmbSize.setEnabled(true);
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

    /**
     * @return Returns the model.
     */
    public MapExplorerModel getModel() {
        return model;
    }
    

    private static Properties loadProperties() {
	Properties result = new Properties();
	String[] keys = { "mapexplorer.home", "user.home", "user.dir" };
	String fs = System.getProperty("file.separator");
	if (fs == null)
	    fs = "/";
	for (String key : keys) {
	    String dir = System.getProperty(key);
	    if (dir != null) {
		File f = new File(dir + fs + "mapexplorer.properties");
		try {
		    result.load(new FileInputStream(f));
		}
		catch (IOException e) {
		    // just ignore this.
		}
	    }
	}
	return result;
    }
}
