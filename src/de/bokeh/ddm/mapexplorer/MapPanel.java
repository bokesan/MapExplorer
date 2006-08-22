/*
 * $Id: MapPanel.java,v 1.20 2006/08/22 11:44:37 breitko Exp $
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
import javax.swing.*;
import java.util.Set;


/**
 * GUI component to display a Map with LOS and Creatures.
 * 
 * @see Map
 * @author Christoph Breitkopf
 */
public class MapPanel extends JPanel {
    
    static final long serialVersionUID = 8768687641202390L;
    
    private static final int PREFERRED_TILE_WIDTH = 25;
    private static final int PREFERRED_TILE_HEIGHT = 25;
    
    private Map map;
    private LosMap losMap;
    private MovementMap movementMap;

    private ColorSettings colors;
    
    private int tileWidth;
    private int tileHeight;
    private int wallWidth;
    
    private Set<Creature> creatures;
    
    /**
     * Constructs and initializes a new MapPanel for a given map.
     * @param map a Map
     */
    public MapPanel(Map map) {
	super();
	colors = new ColorSettings();
	setMap(map);
    }

    private void computeSizes() {
	// allow 2 additional rows and columns for labels
	tileWidth = this.getWidth() / (map.getWidth() + 2);
	tileHeight = this.getHeight() / (map.getHeight() + 2);
	wallWidth = (tileWidth + tileHeight) / 20;
	if (wallWidth == 0) wallWidth = 1;
    }
    
    static private enum FontUse {
	NORMAL, IN_SQUARE
    };
    
    private void setFont(Graphics g, FontUse use) {
	Font f = g.getFont();
	String name = f.getName();
	int style = f.getStyle();
	int size;
	switch (use) {
	default:
	    size = (tileWidth + tileHeight) / 4;
	    break;
	case IN_SQUARE:
	    size = (tileWidth + tileHeight) / 6;
	}
	g.setFont(new Font(name, style, size));
    }

    private java.awt.Point locPoint(int sx, int sy) {
	int x = tileWidth * (sx + 1);
	int y = tileHeight * (map.getHeight() - sy);
	return new java.awt.Point(x, y);
    }

    /**
     * Return map square Location of a pixel.
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel.
     * @return The map location containing the pixel at x,y.
     */
    public Location getLocation(int x, int y) {
	computeSizes();
	int col = x / tileWidth - 1;
	int row = map.getHeight() - y / tileHeight;
	if (col < 0 || col >= map.getWidth())
	    return null;
	if (row < 0 || row >= map.getHeight())
	    return null;
	return new Location(col, row);
    }

    @Override
    public void paintComponent(Graphics g) {
	setFont(g, FontUse.NORMAL);
	setAntiAliasing(g);
	computeSizes();
	super.paintComponent(g);

	for (int row = 0; row < map.getHeight(); row++) {
	    java.awt.Point p = locPoint(-1, row);
	    drawCenteredString(g, p, Location.rowToString(row));
	    p = locPoint(map.getWidth(), row);
	    drawCenteredString(g, p, Location.rowToString(row));
	    for (int col = 0; col < map.getWidth(); col++) {
		drawTile(g, col, row);
	    }
	}
	if (creatures != null) {
	    for (Creature c : creatures)
		drawCreature(g, c);
	}
	for (int col = 0; col < map.getWidth(); col++) {
	    drawCenteredString(g, locPoint(col, -1), Location.columnToString(col));
	    drawCenteredString(g, locPoint(col, map.getHeight()), Location.columnToString(col));
	}
	g.setColor(Color.BLACK);
    }
    
    private void drawTile(Graphics g, int col, int row) {
	MapSquare t = map.get(col, row);
	java.awt.Point p = locPoint(col, row);
	int x = p.x;
	int y = p.y;
	g.setColor(colors.getColor(ColorSettings.Special.GRID));
	g.drawRect(x, y, tileWidth-1, tileHeight-1);
	String playerMarker = null;
	{
	    Color c = null;
	    if (t.has(MapFeature.START_A)) {
		c = colors.getColor(MapFeature.START_A);
		playerMarker = "A";
	    }
	    if (t.has(MapFeature.START_B)) {
		c = ColorSettings.blend(c, colors.getColor(MapFeature.START_B));
		playerMarker = (playerMarker == null) ? "B" : null;
	    }
	    if (t.has(MapFeature.VICTORY_A)) {
		c = ColorSettings.blend(c, colors.getColor(MapFeature.VICTORY_A));
		playerMarker = "A";
	    }
	    if (t.has(MapFeature.VICTORY_B)) {
		c = ColorSettings.blend(c, colors.getColor(MapFeature.VICTORY_B));
		playerMarker = (playerMarker == null) ? "B" : null;
	    }
	    if (c == null)
		c = t.getColor();
	    g.setColor(c);
	    g.fillRect(x+1, y+1, tileWidth-2, tileHeight-2);
	}
	if (t.has(MapFeature.BLOOD_ROCK)) {
	    int h2 = tileHeight / 4;
	    int w2 = tileWidth / 4;
	    g.setColor(colors.getColor(MapFeature.BLOOD_ROCK));
	    g.fillRect(x+w2,y+h2, tileWidth-(2*w2), tileHeight-(2*h2));
	}
	if (t.has(MapFeature.ZONE_OF_DEATH)) {
	    int h2 = tileHeight / 4;
	    int w2 = tileWidth / 4;
	    g.setColor(colors.getColor(MapFeature.ZONE_OF_DEATH));
	    g.fillRect(x+w2,y+h2, tileWidth-(2*w2), tileHeight-(2*h2));
	}
	if (t.has(MapFeature.LAVA)) {
	    g.setColor(colors.getColor(MapFeature.LAVA));
	    g.fillRect(x+1, y+1, tileWidth-2, tileHeight-2);
	}
	if (t.has(MapFeature.SMOKE)) {
	    g.setColor(colors.getColor(MapFeature.SMOKE));
	    g.fillRect(x+1, y+1, tileWidth-2, tileHeight-2);
	}
	if (t.has(MapFeature.HAUNTED)) {
	    g.setColor(colors.getColor(MapFeature.HAUNTED));
	    g.fillRect(x+1,y+1, tileWidth-2, tileHeight-2);
	}
	if (t.has(MapFeature.TELEPORTER)) {
	    g.setColor(colors.getColor(MapFeature.TELEPORTER));
	    g.fillRect(x+1,y+1, tileWidth-2, tileHeight-2);
	}
	if (t.has(MapFeature.FOREST)) {
	    paintForest(g, x, y);
	}
	if (t.has(MapFeature.DIFFICULT)) {
	    paintDifficult(g, x, y);
	}
	if (t.has(MapFeature.SPIKE_STONES)) {
	    paintSpikestones(g, x, y);
	}
	if (t.has(MapFeature.STEEP_SLOPE)) {
	    paintSteepSlope(g, x, y);
	}
	if (t.has(MapFeature.RISKY)) {
	    paintRisky(g, x, y);
	}
	if (t.has(MapFeature.SACRED_CIRCLE)) {
	    g.setColor(colors.getColor(MapFeature.SACRED_CIRCLE));
	    g.fillRect(x+1,y+1, tileWidth/2, tileHeight/2);
	}
	if (t.has(MapFeature.SUMMONING_CIRCLE)) {
	    g.setColor(colors.getColor(MapFeature.SUMMONING_CIRCLE));
	    g.fillRect(x+3,y+3, tileWidth/2, tileHeight/2);
	}
	if (t.has(MapFeature.STATUE)) {
	    g.setColor(colors.getColor(MapFeature.STATUE));
	    g.drawOval(x+2,y+2, tileWidth-5, tileHeight-5);
	}
	
	if (playerMarker != null) {
	    g.setColor(Color.BLACK);
	    setFont(g, FontUse.IN_SQUARE);
	    drawTCString(g, p, playerMarker);
	    setFont(g, FontUse.NORMAL);
	}
	
	if (t.has(MapFeature.EXIT_A) || t.has(MapFeature.EXIT_B))
	    paintExit(g, p, t);

	paintFullSquareFeature(g, p, t, MapFeature.PIT);
	paintFullSquareFeature(g, p, t, MapFeature.ELEMENTAL_WALL);
	paintFullSquareFeature(g, p, t, MapFeature.WATERFALL);

	// Walls
	if (t.hasWall()) {
	    g.setColor(colors.getColor(ColorSettings.Special.WALL));
	    if (t.isSolid()) {
		g.fillRect(x, y, tileWidth, tileHeight);
	    } else {
		if (t.getWall(Direction.NORTH))
		    g.fillRect(x, y, tileWidth, wallWidth);
		if (t.getWall(Direction.WEST))
		    g.fillRect(x, y, wallWidth, tileHeight);
		if (t.getWall(Direction.SOUTH))
		    g.fillRect(x, y+tileHeight-wallWidth, tileWidth, wallWidth);
		if (t.getWall(Direction.EAST))
		    g.fillRect(x+tileWidth-wallWidth, y, wallWidth, tileHeight);
	    }
	}
	
	if (losMap.get(col, row)) {
	    g.setColor(colors.getColor(ColorSettings.Special.LOS));
	    drawLosIcon(g, p);
	}
	g.setColor(Color.BLACK);
	int move = movementMap.getMove(new Location(col,row));
	if (move != MovementMap.UNREACHABLE) {
	    setFont(g, FontUse.IN_SQUARE);
	    drawBLString(g, p, "" + move);
	    setFont(g, FontUse.NORMAL);
	}
    }
    
    private void drawCreature(Graphics g, Creature c) {
	g.setColor(colors.getColor(ColorSettings.Special.CREATURE));
	int col = c.getLocation().getColumn();
	int row = c.getLocation().getRow();
	int sz = c.getSize().sizeSquares();
	java.awt.Point p = locPoint(col, row+sz-1);
	g.fillOval(p.x+2, p.y+2, sz*tileWidth-4, sz*tileHeight-4);
	g.setColor(Color.BLACK);
    }
    
    
    public final void setMap(Map map) {
	this.map = map;
	setPreferredSize(new java.awt.Dimension(PREFERRED_TILE_WIDTH * (map.getWidth() + 1),
		PREFERRED_TILE_HEIGHT * (map.getHeight() + 1)));
    }


    private void paintDifficult(Graphics g, int x, int y) {
	// Triangle
	int h3 = tileHeight / 3;
	int w3 = tileWidth / 3;
	int w2 = tileWidth / 2;
	g.setColor(colors.getColor(MapFeature.DIFFICULT));
	g.drawLine(x+w3, y+2*h3, x+2*w3, y+2*h3);
	g.drawLine(x+w3, y+2*h3, x+w2, y+h3);
	g.drawLine(x+2*w3, y+2*h3, x+w2, y+h3);
	g.setColor(Color.BLACK);
    }

    private void paintSteepSlope(Graphics g, int x, int y) {
	int h6 = tileHeight / 6;
	int v2h6 = 2 * tileHeight / 6;
	int w6 = tileWidth / 6;
	int v2w6 = 2 * tileWidth / 6;
	g.setColor(colors.getColor(MapFeature.STEEP_SLOPE));
	g.drawLine(x+w6, y+tileHeight-v2h6, x+tileWidth-v2w6, y+h6);
	g.drawLine(x+v2w6, y+tileHeight-h6, x+tileWidth-w6, y+v2h6);
	g.setColor(Color.BLACK);
    }
    
    
    private void paintRisky(Graphics g, int x, int y) {
	// Skull
	g.setColor(colors.getColor(MapFeature.RISKY));
	int dx = tileWidth / 3;
	int dy = tileHeight / 3;
	g.drawLine(x+dx, y+dy, x+tileWidth-dx, y+tileHeight-dy);
	g.drawLine(x+dx, y+tileHeight-dy, x+tileWidth-dx, y+dy);
	g.setColor(Color.BLACK);
    }

    private void paintSpikestones(Graphics g, int x, int y) {
	// "Caltrop"
	g.setColor(colors.getColor(MapFeature.SPIKE_STONES));
	int midx = x + tileWidth / 2;
	int midy = y + tileHeight / 2;
	g.drawLine(x+tileWidth/2, y+tileHeight/3, midx, midy);
	g.drawLine(midx, midy, x+tileWidth/3, y+tileHeight-tileHeight/3);
	g.drawLine(midx, midy, x+tileWidth-tileWidth/3, y+tileHeight-tileHeight/3);
	g.setColor(Color.BLACK);
    }
    
    private void paintForest(Graphics g, int x, int y) {
	// Tree
	g.setColor(colors.getColor(MapFeature.FOREST));
	g.drawArc(x + tileWidth/3, y + tileHeight/6, tileWidth/3, tileHeight/3, 0, 360);
	g.drawLine(x+tileWidth/2, y+tileHeight/2, x+tileWidth/2, y+5*tileHeight/6);
	g.setColor(Color.BLACK);
    }
    
    
    
    /**
     * @return Returns the map.
     */
    public Map getMap() {
        return map;
    }
    
    private void drawCenteredString(Graphics g, java.awt.Point p, String s) {
	FontMetrics fm = getFontMetrics(g.getFont());
	int x = p.x + (tileWidth - fm.stringWidth(s)) / 2;
	int y = p.y + (tileHeight  + fm.getAscent()) / 2;
	g.drawString(s, x, y);
    }
    
    private void drawBLString(Graphics g, java.awt.Point p, String s) {
	int x = p.x + 2;
	int y = p.y + tileHeight - 3;
	g.drawString(s, x, y);
    }
    
    private void drawTCString(Graphics g, java.awt.Point p, String s) {
	FontMetrics fm = getFontMetrics(g.getFont());
	int x = p.x + (tileWidth - fm.stringWidth(s)) / 2;
	int y = p.y + fm.getAscent() + 1;
	g.drawString(s, x, y);
    }

    /**
     * @return Returns the LosMap.
     */
    public LosMap getLosMap() {
        return losMap;
    }

    /**
     * @param los The LosMap to set
     */
    public void setLosMap(LosMap los) {
        this.losMap = los;
    }
    
    public void setMovementMap(MovementMap m) {
	movementMap = m;
    }
    
    public void setCreatures(Set<Creature> cs) {
	creatures = cs;
    }

    /**
     * @return Returns the colors.
     */
    public ColorSettings getColors() {
        return colors;
    }

    /**
     * @param colors The colors to set.
     */
    public void setColors(ColorSettings colors) {
        this.colors = colors;
    }

    private void setAntiAliasing(Graphics g) {
	try {
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	} catch (ClassCastException ex) {
	    // Ignore
	}
    }

    /**
     * Draw Line-of-sight icon in upper right corner of map square.
     * @param g a Graphics
     * @param p the upper left corner of the map square
     */
    private void drawLosIcon(Graphics g, java.awt.Point p) {
	int y = p.y + 2;
	int height = 2 * tileHeight / 10;
	int width = 4 * tileWidth / 10;
	int x = p.x + tileWidth - (width + 3);
	
	g.drawOval(x, y, width, height);
	g.fillOval(x+width/2-1, y+height/2-1, 3, 3);
    }

    private void paintFullSquareFeature(Graphics g, java.awt.Point p, MapSquare t, MapFeature f) {
	if (t.has(f)) {
	    g.setColor(colors.getColor(f));
	    g.fillRect(p.x, p.y, tileWidth, tileHeight);
	}
    }
    
    private void paintExit(Graphics g, java.awt.Point p, MapSquare t) {
	boolean a = t.has(MapFeature.EXIT_A);
	boolean b = t.has(MapFeature.EXIT_B);
	
	Color c;
	String legend;
	if (a && b) {
	    c = ColorSettings.blend(colors.getColor(MapFeature.EXIT_A), colors.getColor(MapFeature.EXIT_B));
	    legend = "";
	} else if (a) {
	    c = colors.getColor(MapFeature.EXIT_A);
	    legend = "A";
	} else {
	    c = colors.getColor(MapFeature.EXIT_B);
	    legend = "B";
	}
	g.setColor(c);
	g.drawLine(p.x, p.y, p.x+tileWidth-1, p.y+tileHeight-1);
	g.drawLine(p.x+tileWidth-1, p.y, p.x, p.y+tileHeight-1);
	setFont(g, FontUse.IN_SQUARE);
	drawTCString(g, p, legend);
	setFont(g, FontUse.NORMAL);
    }

}
