/*
 * $Id: MapReader.java,v 1.5 2006/01/05 12:55:51 breitko Exp $
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

import java.io.*;
import java.awt.Color;

/**
 * Read Map from file.
 * 
 * @author Christoph Breitkopf
 */
public class MapReader {
    
    public Map read(String file) throws IOException, SyntaxError {
	BufferedReader in = new BufferedReader(new FileReader(file));
	Map m = null;
	String name = null;
	int width = 0;
	int height = 0;
	
	int lineNumber = 0;
	String line = null;
	try {
	    for (;;) {
		line = in.readLine();
		if (line == null)
		    break;
		lineNumber++;
		line = line.trim();
		if (line.length() == 0 || line.charAt(0) == '#')
		    continue;
		String[] f = line.split("\\ +");
		if (f[0].equals("map")) {
		    name = line.substring(4).trim();
		}
		else if (f[0].equals("width")) {
		    width = Integer.parseInt(f[1]);
		    if (height > 0)
			m = new Map(new Dimension(width, height), name);
		}
		else if (f[0].equals("height")) {
		    height = Integer.parseInt(f[1]);
		    if (width > 0)
			m = new Map(new Dimension(width, height), name);
		}
		else if (f[0].equals("exit")) {
		    handlePlayerFeature(m, f, MapFeature.EXIT_A, MapFeature.EXIT_B);
		}
		else if (f[0].equals("victory")) {
		    handlePlayerFeature(m, f, MapFeature.VICTORY_A, MapFeature.VICTORY_B);
		}
		else if (f[0].equals("start")) {
		    handlePlayerFeature(m, f, MapFeature.START_A, MapFeature.START_B);
		}
		else if (f[0].equals("solid")) {
		    handleSolid(m, f);
		}
		else if (f[0].equals("wall")) {
		    handleWall(m, f);
		}
		else if (f[0].equals("difficult")) {
		    handleFeature(m, f, MapFeature.DIFFICULT);
		}
		else if (f[0].equals("smoke")) {
		    handleFeature(m, f, MapFeature.SMOKE);
		}
		else if (f[0].equals("pit")) {
		    handleFeature(m, f, MapFeature.PIT);
		}
		else if (f[0].equals("spikestones")) {
		    handleFeature(m, f, MapFeature.SPIKE_STONES);
		}
		else if (f[0].equals("lava")) {
		    handleFeature(m, f, MapFeature.LAVA);
		}
		else if (f[0].equals("risky")) {
		    handleFeature(m, f, MapFeature.RISKY);
		}
		else if (f[0].equals("magic")) {
		    handleFeature(m, f, MapFeature.SACRED_CIRCLE);
		}
		else if (f[0].equals("summoning")) {
		    handleFeature(m, f, MapFeature.SUMMONING_CIRCLE);
		}
		else if (f[0].equals("statue")) {
		    handleFeature(m, f, MapFeature.STATUE);
		}
		else if (f[0].equals("bloodrock")) {
		    handleFeature(m, f, MapFeature.BLOOD_ROCK);
		}
		else if (f[0].equals("haunted")) {
		    handleFeature(m, f, MapFeature.HAUNTED);
		}
		else if (f[0].equals("basecolor")) {
		    m.setColor(parseColor(f[1]));
		}
		else {
		    throw new SyntaxError(file, lineNumber, line);
		}
	    }
	}
	catch (NumberFormatException ex) {
	    throw new SyntaxError(file, lineNumber, line);
	}
	catch (IllegalArgumentException ex) {
	    throw new SyntaxError(file, lineNumber, line);
	}
	finally {
	    in.close();
	}
	return m;
    }

    private void handleFeature(Map m, String[] f, MapFeature feature) {
	for (int i = 1; i < f.length; i++) {
	    setFeature(m, Rectangle.parse(f[i]), feature);
	}
    }
    
    
    private void handlePlayerFeature(Map m, String[] f, MapFeature featureA, MapFeature featureB) {
	MapFeature feature = null;
	for (int i = 1; i < f.length; i++) {
	    if (f[i].equals("A"))
		feature = featureA;
	    else if (f[i].equals("B"))
		feature = featureB;
	    else {
		setFeature(m, Rectangle.parse(f[i]), feature);
	    }
	}
    }
    
    private void setFeature(Map m, Rectangle r, MapFeature f) {
	for (Location loc : r.getLocations())
	    m.get(loc).addFeature(f);
    }
    
    
    private void handleWall(Map m, String[] f) {
	Direction dir = null;
	for (int i = 1; i < f.length; i++) {
	    if (f[i].equals("north")) {
		dir = Direction.NORTH;
	    } else if (f[i].equals("east")) {
		dir = Direction.EAST;
	    } else if (f[i].equals("south")) {
		dir = Direction.SOUTH;
	    } else if (f[i].equals("west")) {
		dir = Direction.WEST;
	    } else {
		Rectangle r = Rectangle.parse(f[i]);
		for (Location loc : r.getLocations())
		    m.dblWall(loc.getColumn(), loc.getRow(), dir);
	    }
	}
    }
    
    private void handleSolid(Map m, String[] f) {
	for (int i = 1; i < f.length; i++) {
	    Rectangle r = Rectangle.parse(f[i]);
	    for (Location loc : r.getLocations())
		m.setSolid(loc);
	}
    }
    
    public static Color parseColor(String s) {
	long c = Long.parseLong(s, 16);
	if (s.length() <= 6)
	    return new Color((int)c);
	return new Color((int) ((c & 0xff000000) >> 24),
			 (int) ((c & 0x00ff0000) >> 16),
			 (int) ((c & 0x0000ff00) >> 8),
			 (int) c & 0xff);
    }
}
