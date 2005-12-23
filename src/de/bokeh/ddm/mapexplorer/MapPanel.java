/*
 * $Id: MapPanel.java,v 1.7 2005/12/23 16:31:39 breitko Exp $
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

import java.awt.*;
import javax.swing.*;
import java.util.Set;


/**
 * @author breitko
 */
public class MapPanel extends JPanel {
    
    static final long serialVersionUID = 8768687641202390L;
    
    private static final int PREFERRED_TILE_WIDTH = 25;
    private static final int PREFERRED_TILE_HEIGHT = 25;
    
    private Map map;
    private LosMap losMap;

    private ColorSettings colors;
    
    private int tileWidth;
    private int tileHeight;
    private int wallWidth;
    
    private Set<Creature> creatures;
    
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

    private java.awt.Point locPoint(int sx, int sy) {
	int x = tileWidth * (sx + 1);
	int y = tileHeight * (map.getHeight() - sy);
	return new java.awt.Point(x, y);
    }

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

    
    public void paintComponent(Graphics g) {
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
	{
	    Color c;
	    if (t.has(MapFeature.START_A))
		c = colors.getColor(MapFeature.START_A);
	    else if (t.has(MapFeature.START_B))
		c = colors.getColor(MapFeature.START_B);
	    else if (t.has(MapFeature.VICTORY_A))
		c = colors.getColor(MapFeature.VICTORY_A);
	    else if (t.has(MapFeature.VICTORY_B))
		c = colors.getColor(MapFeature.VICTORY_B);
	    else
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
	if (t.has(MapFeature.DIFFICULT)) {
	    paintDifficult(g, x, y);
	}
	if (t.has(MapFeature.SPIKE_STONES)) {
	    paintSpikestones(g, x, y);
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
	
	if (t.has(MapFeature.EXIT_A) || t.has(MapFeature.EXIT_B)) {
	    // g.drawString("Exit", x+2, y+2);
	    g.setColor(colors.getColor(MapFeature.EXIT_A));
	    g.drawLine(x, y, x+tileWidth-1, y+tileHeight-1);
	    g.drawLine(x+tileWidth-1, y, x, y+tileHeight-1);
	}
	
	if (t.has(MapFeature.PIT)) {
	    g.setColor(colors.getColor(MapFeature.PIT));
	    g.fillRect(x, y, tileWidth, tileHeight);
	}

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
	    g.drawArc(x, y+2*tileHeight/5, tileWidth, tileHeight, 45, 90);
	    g.drawArc(x, y-2*tileHeight/5, tileWidth, tileHeight, 225, 90);
	    g.fillOval(x+tileWidth/2-1, y+tileHeight/2-1, 3, 3);
	}
	g.setColor(Color.BLACK);
    }
    
    private void drawCreature(Graphics g, Creature c) {
	g.setColor(colors.getColor(ColorSettings.Special.MARK));
	int col = c.getLocation().getColumn();
	int row = c.getLocation().getRow();
	int sz = c.getSize().sizeSquares();
	for (int xoff = 0; xoff < sz; xoff++) {
	    for (int yoff = 0; yoff < sz; yoff++) {
		java.awt.Point p = locPoint(col+xoff, row+yoff);
		int x = p.x;
		int y = p.y;
		g.fillOval(x+2, y+2, tileWidth-4, tileHeight-4);
	    }
	}
	g.setColor(Color.BLACK);
    }
    
    
    public void setMap(Map map) {
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
    
    
    
    /**
     * @return Returns the map.
     */
    public Map getMap() {
        return map;
    }
    
    private void drawCenteredString(Graphics g, java.awt.Point p, String s) {
	FontMetrics fm = getFontMetrics(getFont());
	int x = p.x + tileWidth / 2 - fm.stringWidth(s) / 2;
	int y = p.y + tileHeight / 2 + fm.getAscent() / 2;
	g.drawString(s, x, y);
    }

    /**
     * @return Returns the los.
     */
    public LosMap getLosMap() {
        return losMap;
    }

    /**
     * @param los The los to set.
     */
    public void setLosMap(LosMap los) {
        this.losMap = los;
    }
    
    public void setCreatures(Set<Creature> cs) {
	creatures = cs;
    }
    
}
