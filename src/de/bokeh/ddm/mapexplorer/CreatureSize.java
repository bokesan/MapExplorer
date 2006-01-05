/*
 * $Id: CreatureSize.java,v 1.4 2006/01/05 12:55:51 breitko Exp $
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

/**
 * Creature size categories.
 * @author Christoph Breitkopf
 */
public enum CreatureSize {
    
    TINY, SMALL, MEDIUM, LARGE, HUGE, GARGANTUAN, COLOSSAL;
    
    /**
     * Size in number of map squares.
     * @return The size in map squares for this size category.
     */
    public int sizeSquares() {
	switch (this) {
	case LARGE: return 2;
	case HUGE: return 3;
	case GARGANTUAN: return 4;
	case COLOSSAL: return 6;
	default: return 1;
	}
    }
	
}
