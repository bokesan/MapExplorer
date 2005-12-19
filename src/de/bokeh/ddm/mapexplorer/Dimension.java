/*
 * $Id: Dimension.java,v 1.2 2005/12/19 11:35:18 breitko Exp $
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

/**
 * A dimension of size width &times; height, specified in integer
 * units.
 * 
 * @author Christoph Breitkopf
 */
public class Dimension {

	private final int width;
	private final int height;
	
	/**
	 * Constructs and initializes a new Dimension of width and height.
	 * 
	 * @param width  the width
	 * @param height the height
	 */
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the height of the dimension.
	 * @return the height of this Dimension.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width of the dimension.
	 * @return the width of this Dimension.
	 */
	public int getWidth() {
		return width;
	}

}
