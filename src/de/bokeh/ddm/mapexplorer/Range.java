/*
 * $Id: Range.java,v 1.2 2005/12/19 11:34:02 breitko Exp $
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

public class Range {

    private final double lowerBound;
    private final double higherBound;
    
    public Range(double a, double b) {
	if (a < b) {
	    lowerBound = a;
	    higherBound = b;
	} else {
	    lowerBound = b;
	    higherBound = a;
	}
    }
    
    public boolean contains(double x) {
	return lowerBound <= x && x <= higherBound;
    }
    
    public Range extend(Range r2) {
	return new Range(min(lowerBound, r2.lowerBound), max(higherBound, r2.higherBound));
    }

    private static double min(double a, double b) {
	return (a < b) ? a : b;
    }
    
    private static double max(double a, double b) {
	return (a > b) ? a : b;
    }

    /**
     * @return Returns the higherBound.
     */
    public double getHigherBound() {
        return higherBound;
    }

    /**
     * @return Returns the lowerBound.
     */
    public double getLowerBound() {
        return lowerBound;
    }
    
}
