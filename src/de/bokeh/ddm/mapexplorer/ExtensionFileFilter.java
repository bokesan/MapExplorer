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
