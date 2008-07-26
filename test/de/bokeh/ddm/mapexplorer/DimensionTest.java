package de.bokeh.ddm.mapexplorer;

import org.junit.Test;
import static org.junit.Assert.*;


public class DimensionTest {

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Dimension.getHeight()'
     */
    @Test public void testGetHeight() {
	Dimension d = new Dimension(13, 12);
	assertEquals(12, d.getHeight());
    }

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Dimension.getWidth()'
     */
    @Test public void testGetWidth() {
	Dimension d = new Dimension(13, 12);
	assertEquals(13, d.getWidth());
    }

    /*
     * Test method for 'de.bokeh.ddm.mapexplorer.Dimension.equals(Object)'
     */
    @Test public void testEqualsObject() {
	Dimension d1 = new Dimension(12, 17);
	Dimension d2 = new Dimension(12, 17);
	assertTrue(d1.equals(d2));
    }

}
