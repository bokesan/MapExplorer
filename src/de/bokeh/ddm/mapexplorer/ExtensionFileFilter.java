/*
 * $Id: ExtensionFileFilter.java,v 1.2 2005/12/19 11:35:18 breitko Exp $
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

import java.io.File;
import java.util.*;

import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter {

    private final String description;
    private final Set<String> allowedExtensions;
    private boolean emptyExtensionAllowed;
    
    public ExtensionFileFilter(String description) {
	allowedExtensions = new HashSet<String>();
	emptyExtensionAllowed = false;
	this.description = description;
    }
    
    public void addExtension(String ext) {
	allowedExtensions.add(ext);
    }
    
    public void setEmptyExtensionAllowed(boolean val) {
	emptyExtensionAllowed = val;
    }
    
    
    @Override
    public boolean accept(File f) {
	if (f.isDirectory())
	    return true;
	String extension = getExtension(f);
	if (extension == null)
	    return emptyExtensionAllowed;
	return allowedExtensions.contains(extension);
    }

    @Override
    public String getDescription() {
	return description;
    }

    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
}
