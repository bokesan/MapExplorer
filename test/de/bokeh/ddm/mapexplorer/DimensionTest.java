package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;

public class DimensionTest extends TestCase {

    public DimensionTest(String name) {
	super(name);
    }

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Dimension.getHeight()'
     */
    public void testGetHeight() {
	Dimension d = new Dimension(13, 12);
	assertEquals(12, d.getHeight());
    }

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Dimension.getWidth()'
     */
    public void testGetWidth() {
	Dimension d = new Dimension(13, 12);
	assertEquals(13, d.getWidth());
    }

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Dimension.equals(Object)'
     */
    public void testEqualsObject() {
	Dimension d1 = new Dimension(12, 17);
	Dimension d2 = new Dimension(12, 17);
	assertTrue(d1.equals(d2));
    }

}
