package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;
import java.io.File;

public class ExtensionFileFilterTest extends TestCase {

    public ExtensionFileFilterTest(String name) {
	super(name);
    }

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.ExtensionFileFilter.accept(File)'
     */
    public void testAcceptFile() {
	ExtensionFileFilter ff = new ExtensionFileFilter("test");
	ff.addExtension("map");
	
	assertTrue("simple filename", ff.accept(new File("test.map")));
	assertTrue("multiple dots", ff.accept(new File("test.1.map")));
	assertTrue("multiple successive dots", ff.accept(new File("test...map")));
	
	// this is a bit dubious, at least under unix.
	// it should really be regarded as a file without extension.
	// we treat it as extension only, though
	assertTrue("extension only", ff.accept(new File(".map")));
	
	assertFalse("different extension", ff.accept(new File("map.txt")));
	assertFalse("embedded extension", ff.accept(new File("a.map.1")));
    }
    
    public void testAcceptFileEmpty() {
	ExtensionFileFilter ff = new ExtensionFileFilter("test");
	ff.addExtension("map");

	// empty extension is initially disabled
	assertFalse(ff.accept(new File("map")));
	ff.setEmptyExtensionAllowed(true);
	assertTrue(ff.accept(new File("map")));
    }

    public void testAcceptFileMulti() {
	// does it still work when multiple extensions are allowed?
	
	ExtensionFileFilter ff = new ExtensionFileFilter("test");
	ff.addExtension("map");
	ff.addExtension("1");
	ff.addExtension("html");
	
	assertTrue(ff.accept(new File("test.map")));
	assertTrue(ff.accept(new File("test.1")));
	assertTrue(ff.accept(new File("test.html")));
    }
}
