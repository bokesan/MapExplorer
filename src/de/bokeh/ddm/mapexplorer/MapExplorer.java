// $Id: MapExplorer.java,v 1.9 2005/12/12 16:18:18 breitko Exp $

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
public class MapExplorer implements ActionListener, ItemListener {

    public static final String VERSION = "20051212";
    
    // ActionCommands
    private static final String ACTION_LOAD_MAP = "loadMap";
    private static final String ACTION_CLEAR_LOS = "clearLOS";
    
    
    static Logger logger = Logger.getLogger(MapExplorer.class.getName());

    private JFrame appFrame;
    private JToolBar toolBar;
    private MapPanel mapPanel;
    private JToolBar statusPanel;
    private Map map;
    private JLabel lblStatus;
    private JLabel lblResults;
    private JCheckBox chkSmoke;
    private JComboBox cmbSize;
    private int randomTestsPerSquare;
    private boolean testAll;
    private boolean busy;
    private Location creaturePos;
    
    private JFileChooser fileChooser;
    
    private JProgressBar progress;

    public MapExplorer() {
	fileChooser = null;
	randomTestsPerSquare = 100;
	testAll = false;
	busy = false;
	map = new Map(new Dimension(30, 21), "no map loaded");
	creaturePos = null;
    }
    
    private void setTitle() {
	appFrame.setTitle("Map Explorer (version " + VERSION + ") - " + map.getName());
    }
    
    public void setMap(Map m) {
	map = m;
	setTitle();
	mapPanel.setMap(map);
	chkSmoke.setEnabled(map.has(MapFeature.SMOKE));
	creaturePos = null;
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
			if (placeCreature(loc))
			    computeLos();
			else
			    Toolkit.getDefaultToolkit().beep();
		    }
		}
	    }
	});
    }

    private boolean placeCreature(Location loc) {
	CreatureSize size = (CreatureSize) cmbSize.getModel().getSelectedItem();
	if (map.canPlaceCreature(loc, size)) {
	    creaturePos = loc;
	    map.clearMarks();
	    int sq = size.sizeSquares();
	    for (int x = 0; x < sq; x++) {
		for (int y = 0; y < sq; y++) {
		    map.get(loc.getColumn() + x, loc.getRow() + y).setMarked(true);
		}
	    }
	    return true;
	}
	return false;
    }
    
    private void computeLos() {
	setBusy(true, "computing LOS...");
	LosComputation lc = new LosComputation(this, creaturePos, randomTestsPerSquare, logger);
	lc.setSmokeBlocksLos(chkSmoke.getModel().isSelected());
	lc.setCreatureSize((CreatureSize) cmbSize.getModel().getSelectedItem());
	lc.start();
    }
    
    public void setStatusText(String s) {
	lblStatus.setText(s);
    }
    
    public static void main(String[] args) {
	boolean benchmark = false;
	String mapFile = "Fane_of_Lolth.map";

	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-version")) {
		System.out.println("Map Explorer version " + VERSION);
		return;
	    }
	    if (args[i].equals("-benchmark")) {
		benchmark = true;
	    }
	    else {
		mapFile = args[i];
	    }
	}
	
	if (benchmark) {
	    try {
		Map map = new MapReader().read(mapFile);
		LosBenchmark b = new LosBenchmark(map, 100);
		b.run();
	    }
	    catch (SyntaxError err) {
		err.printStackTrace();
	    }
	    catch (IOException ex) {
		ex.printStackTrace();
	    }
	} else {
	    MapExplorer app = new MapExplorer();
	    app.setRandomTestsPerSquare(100);
	    app.start();
	    app.loadMap(mapFile);
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
	creaturePos = null;
	map.clearLos();
	map.clearMarks();
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
	    if (creaturePos != null)
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
}
