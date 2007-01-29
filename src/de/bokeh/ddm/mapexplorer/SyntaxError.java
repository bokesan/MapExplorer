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

public class SyntaxError extends Exception {

    static final long serialVersionUID = 8728687643252590L;
    
    private final String file;
    private final int line;
    private final String message;
    
    public SyntaxError(String file, int line, String message) {
	this.file = file;
	this.line = line;
	this.message = message;
    }

    /**
     * @return Returns the file.
     */
    public String getFile() {
        return file;
    }

    /**
     * @return Returns the line.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }
    
    
    
    
    
}
