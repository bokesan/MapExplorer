/*
 * $Id: MapExplorer.java,v 1.25 2006/08/22 12:43:37 breitko Exp $
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
import java.util.zip.*;
import java.net.URL;


/**
 * Map Explorer application.
 * 
 * @author Christoph Breitkopf
 */
public class MapExplorer implements ActionListener, ItemListener {

    public static final String VERSION = "20061205-1";

    // ActionCommands
    private static final String ACTION_LOAD_MAP = "loadMap";
    private static final String ACTION_CLEAR = "clear";
    private static final String ACTION_WALL = "wall";
    private static final String ACTION_PLACE = "place";
    
    private JFrame appFrame;
    private JToolBar toolBar;
    private MapPanel mapPanel;
    private JToolBar statusPanel;
    private JLabel lblStatus;
    private JLabel lblResults;
    private JCheckBox chkLOS;
    private JCheckBox chkSmoke;
    private JCheckBox chkMovement;
    private JCheckBox chkMapImage;
    private JCheckBox chkVassalCoordinates;
    private JComboBox cmbSize;
    private JPopupMenu contextMenu;
    private boolean busy;
    private JFileChooser fileChooser;
    private JProgressBar progress;

    private Location selectedSquare;
    
    private String imagesArchiveName;
    
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
	mapPanel.setMovementMap(model.getMovementMap());
	
	if (model.isUseMapImage()) {
	    Image img = loadImage(map.getImageFile());
	    mapPanel.setMapImage(img);
	} else {
	    mapPanel.setMapImage(null);
	}
	
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
	mapPanel.setMovementMap(model.getMovementMap());
	mapPanel.setCreatures(model.getCreatures());
	
	createContextMenu();
	createStatusPanel();

	appFrame.getContentPane().add(toolBar, BorderLayout.NORTH);
	appFrame.getContentPane().add(mapPanel, BorderLayout.CENTER);
	appFrame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
	
	appFrame.pack();
	appFrame.setVisible(true);
    }
    
    private void createContextMenu() {
	contextMenu = new JPopupMenu();
	JMenuItem mi = new JMenuItem("place acting creature");
	mi.setActionCommand(ACTION_PLACE);
	mi.addActionListener(this);
	contextMenu.add(mi);
	mi = new JMenuItem("toggle elemental wall");
	mi.setActionCommand(ACTION_WALL);
	mi.addActionListener(this);
	contextMenu.add(mi);
    }
    
    private void createToolBar() {
	toolBar = new JToolBar();
	
	JButton btn = makeIconButton("Open", "Open map", "load a new map");
	btn.setActionCommand(ACTION_LOAD_MAP);
	btn.addActionListener(this);
	toolBar.add(btn);
	
	btn = makeIconButton("Clear", "Clear", "remove creature, clear LOS info");
	btn.setActionCommand(ACTION_CLEAR);
	btn.addActionListener(this);
	toolBar.add(btn);
	
	toolBar.addSeparator();
	
	chkLOS = new JCheckBox(loadIcon("LOS.png", "LOS"), true);
	chkLOS.addItemListener(this);
	chkLOS.setToolTipText("compute line-of-sight");
	chkLOS.setSelectedIcon(loadIcon("LOS-enabled.png", "LOS"));
	chkLOS.setContentAreaFilled(false);
	// toolBar.add(chkLOS);

	chkSmoke = new JCheckBox(loadIcon("Smoke.png", "Smoke"), true);
	chkSmoke.setSelectedIcon(loadIcon("Smoke-enabled.png", "Smoke"));
	chkSmoke.addItemListener(this);
	chkSmoke.setEnabled(false);
	chkSmoke.setToolTipText("toggle fog/smoke");
	chkSmoke.setContentAreaFilled(false);
	toolBar.add(chkSmoke);

	chkMovement = new JCheckBox(loadIcon("Movement.png", "Movement"));
	chkMovement.setSelectedIcon(loadIcon("Movement-enabled.png", "Movement"));
	chkMovement.addItemListener(this);
	chkMovement.setToolTipText("compute movement range");
	chkMovement.setContentAreaFilled(false);
	// toolBar.add(chkMovement);
	
	chkMapImage = new JCheckBox("Map image", model.isUseMapImage());
	chkMapImage.addItemListener(this);
	chkMapImage.setToolTipText("use scanned image for map display");
	toolBar.add(chkMapImage);
	
	chkVassalCoordinates = new JCheckBox("Vassal coordinates", model.isUseVassalCoordinates());
	chkVassalCoordinates.addItemListener(this);
	chkVassalCoordinates.setToolTipText("use Vassal coordinates");
	toolBar.add(chkVassalCoordinates);

	cmbSize = new JComboBox(CreatureSize.values());
	cmbSize.setSelectedItem(CreatureSize.MEDIUM);
	cmbSize.setEditable(false);
	cmbSize.setToolTipText("choose creature size");
	cmbSize.addActionListener(this);
	// cmbSize.setPreferredSize(new java.awt.Dimension(100, cmbSize.getHeight()));
	toolBar.add(cmbSize);
	
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
    
    private static class MyMouseListener extends MouseAdapter {

	private final MapExplorer app;
	
	public MyMouseListener(MapExplorer app) {
	    this.app = app;
	}
	
	public void mousePressed(MouseEvent e) {
	    if (app.isBusy()) {
		Toolkit.getDefaultToolkit().beep();
	    } else {
		maybeShowPopup(e);
	    }
	}
	
	public void mouseReleased(MouseEvent e) {
	    if (app.isBusy()) {
		Toolkit.getDefaultToolkit().beep();
	    } else {
		maybeShowPopup(e);
	    }
	}
	
	public void mouseClicked(MouseEvent e) {
	    if (app.isBusy()) {
		Toolkit.getDefaultToolkit().beep();
	    } else {
		Location loc = app.mapPanel.getLocation(e.getX(), e.getY());
		if (loc != null) {
		    if (app.placeCreature(loc)) {
			app.compute();
			app.mapPanel.repaint();
		    } else
			Toolkit.getDefaultToolkit().beep();
		}
	    }
	}
	
	private void maybeShowPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		app.selectedSquare = app.mapPanel.getLocation(e.getX(), e.getY());
		app.contextMenu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
	
    }
    
    
    
    private void start() {
	createComponents();

	appFrame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	});
	
	mapPanel.addMouseListener(new MyMouseListener(this));
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
    
    /**
     * Compute LOS, Movement, etc.
     */
    private void compute() {
	if (!model.getCreatures().isEmpty()) {
	    setBusy(true, "computing...");
	    model.setSmokeBlocksLos(chkSmoke.getModel().isSelected());
	    boolean los = chkLOS.getModel().isSelected();
	    boolean movement = chkMovement.getModel().isSelected();
	    ComputationThread c = new ComputationThread(this, los, movement);
	    c.start();
	}
    }
    
    public void setStatusText(String s) {
	lblStatus.setText(s);
    }
    
    public static void main(String[] args) {
	boolean benchmark = false;
	String mapFile = "Fane_of_Lolth.map";
	String p = System.getProperty("mapexplorer.home");
	if (p != null)
	    mapFile = p + File.separator + mapFile;
	
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
	    model.setUseMapImage(properties.getProperty("mapexplorer.usemapimage", "false").equals("true"));
	    model.setUseVassalCoordinates(properties.getProperty("mapexplorer.usevassalcoordinates", "false").equals("true"));
	    model.getLosCalculator().setRandomTestsPerSquare(rndTests);
	    MapExplorer app = new MapExplorer(model);
	    app.imagesArchiveName = properties.getProperty("mapexplorer.images", "DDM_1-11-2.mod");
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
		clear();
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
	    else if (cmd.equals(ACTION_CLEAR)) {
		clear();
	    }
	    else if (cmd.equals(ACTION_PLACE)) {
		if (placeCreature(selectedSquare)) {
		    compute();
		    mapPanel.repaint();
		} else {
		    Toolkit.getDefaultToolkit().beep();
		}
	    }
	    else if (cmd.equals(ACTION_WALL)) {
		if (!model.toggleElementalWall(selectedSquare, 2))
		    Toolkit.getDefaultToolkit().beep();
		else {
		    compute();
		    mapPanel.repaint();
		}
	    }
	}
    }

    private void clear() {
	model.clearLos();
	model.clearMovement();
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
	if (source == chkSmoke || source == chkLOS || source == chkMovement) {
	    compute();
	}
	else if (source == chkVassalCoordinates) {
	    model.setUseVassalCoordinates(chkVassalCoordinates.getModel().isSelected());
	    if (model.isUseVassalCoordinates())
		mapPanel.setLocationFormatter(LocationFormatterFactory.getVassalFormatter(model.getMap().getDimension()));
	    else
		mapPanel.setLocationFormatter(LocationFormatterFactory.getDefaultFormatter(model.getMap().getDimension()));
	    mapPanel.repaint();
	}
	else if (source == chkMapImage) {
	    model.setUseMapImage(chkMapImage.getModel().isSelected());
	    if (model.isUseMapImage()) {
		mapPanel.setMapImage(loadImage(model.getMap().getImageFile()));
	    } else {
		mapPanel.setMapImage(null);
	    }
	    mapPanel.repaint();
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

    private ImageIcon loadIcon(String name, String description) {
        URL iconURL = ClassLoader.getSystemResource("icons/" + name);
        if (iconURL != null)
	    return new ImageIcon(iconURL, description);
	return null;
    }

    private JButton makeIconButton(String icon, String text, String tooltip) {
	String iconSuffix = ".png";
	ImageIcon icn = loadIcon(icon + iconSuffix, text);
	JButton btn;
	if (icn != null)
	    btn = new JButton(icn);
	else
	    btn = new JButton(text);
	btn.setToolTipText(tooltip);
	btn.setBorderPainted(false);
	btn.setContentAreaFilled(false);
	return btn;
    }
    

    private static Properties loadProperties() {
	Properties result = new Properties();
	String[] keys = { "mapexplorer.home", "user.home", "user.dir" };
	for (String key : keys) {
	    String dir = System.getProperty(key);
	    if (dir != null) {
		File f = new File(dir + File.separator + "mapexplorer.properties");
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
    
    private Image loadImage(String name) {
	if (name == null) {
	    JOptionPane.showMessageDialog(appFrame, "No image available for this map", "No map image", JOptionPane.WARNING_MESSAGE);
	    return null;
	}
	String[] keys = { "mapexplorer.home", "user.home", "user.dir" };
	Toolkit tk = Toolkit.getDefaultToolkit();
	for (String key : keys) {
	    String dir = System.getProperty(key);
	    if (dir != null) {
		File f = new File(dir + File.separator + imagesArchiveName);
		try {
		    ZipFile mod = new ZipFile(f);
		    ZipEntry e = mod.getEntry("images/" + name);
		    if (e != null) {
			byte[] data = new byte[(int) e.getSize()];
			InputStream s = mod.getInputStream(e);
			int offs = 0;
			while (offs < data.length) {
			    int r = s.read(data, offs, data.length - offs);
			    if (r <= 0)
				break;
			    offs += r;
			}
			s.close();
			return tk.createImage(data);
		    }
		    mod.close();
		}
		catch (IOException e) {
		    // just ignore this.
		}
	    }
	}
	JOptionPane.showMessageDialog(appFrame, "Could not load map image " + name, "Error loading map image",
		JOptionPane.ERROR_MESSAGE);
	return null;
    }
}
