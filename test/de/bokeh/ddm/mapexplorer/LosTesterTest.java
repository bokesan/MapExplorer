package de.bokeh.ddm.mapexplorer;

import junit.framework.TestCase;
import java.util.Arrays;


public class LosTesterTest extends TestCase {

    public LosTesterTest(String name) {
	super(name);
    }

    public void testMakeTestOffsets() {
	assertTrue(Arrays.equals(testOffsetsNormal, LosTester.makeTestOffsets(32)));
	assertTrue(Arrays.equals(testOffsetsFine, LosTester.makeTestOffsets(64)));
    }
    
    private static final double[] testOffsetsNormal = {
	0, 1,
	1/32.0, 31/32.0,
	2/32.0, 30/32.0,
	3/32.0, 29/32.0, //
	4/32.0, 28/32.0,
	5/32.0, 27/32.0, //
	6/32.0, 26/32.0,
	7/32.0, 25/32.0,
	8/32.0, 24/32.0,
	9/32.0, 23/32.0,
	10/32.0, 22/32.0,
	11/32.0, 21/32.0, //
	12/32.0, 20/32.0,
	13/32.0, 19/32.0, //
	14/32.0, 18/32.0,
	15/32.0, 17/32.0,
	1/512.0, 511/512.0,
	0.5
    };
    
    private static final double[] testOffsetsFine = {
        0, 1,
         1/64.0, 63/64.0,
         2/64.0, 62/64.0,
         3/64.0, 61/64.0,
         4/64.0, 60/64.0,
         5/64.0, 59/64.0,
         6/64.0, 58/64.0,
         7/64.0, 57/64.0,
         8/64.0, 56/64.0,
         9/64.0, 55/64.0,
        10/64.0, 54/64.0,
        11/64.0, 53/64.0,
        12/64.0, 52/64.0,
        13/64.0, 51/64.0,
        14/64.0, 50/64.0,
        15/64.0, 49/64.0,
        16/64.0, 48/64.0,
        17/64.0, 47/64.0,
        18/64.0, 46/64.0,
        19/64.0, 45/64.0,
        20/64.0, 44/64.0,
        21/64.0, 43/64.0,
        22/64.0, 42/64.0,
        23/64.0, 41/64.0,
        24/64.0, 40/64.0,
        25/64.0, 39/64.0,
        26/64.0, 38/64.0,
        27/64.0, 37/64.0,
        28/64.0, 36/64.0,
        29/64.0, 35/64.0,
        30/64.0, 34/64.0,
        31/64.0, 33/64.0,
        1/512.0, 511/512.0,
        0.5
    };

}
