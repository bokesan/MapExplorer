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

public class VassalLocationFormatter implements LocationFormatter {

    private final int height;
    
    public VassalLocationFormatter(Dimension size) {
	height = size.getHeight();
    }
    
    public String format(Location loc) {
	return formatColumn(loc.getColumn()) + formatRow(loc.getRow());
    }

    public String formatColumn(int col) {
	int n = col / 26 + 1;
	char c = (char) ('A' + col % 26);
	char[] r = new char[n];
	java.util.Arrays.fill(r, c);
	return String.valueOf(r);
    }

    public String formatRow(int row) {
	return Integer.toString(height - row);
    }

}
