package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class MapExplorerTest extends TestCase {

    public MapExplorerTest(String name) {
	super(name);
    }

    public void testFormatRuntime() {
	assertEquals("0:00:00", MapExplorer.formatRuntime(499));
	assertEquals("0:00:01", MapExplorer.formatRuntime(500));
	assertEquals("0:31:02", MapExplorer.formatRuntime((31 * 60 + 2) * 1000));
	assertEquals("12:07:51", MapExplorer.formatRuntime((12 * 3600 + 7 * 60 + 51) * 1000L));
    }

}
