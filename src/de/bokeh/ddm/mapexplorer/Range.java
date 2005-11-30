package de.bokeh.ddm.mapexplorer;

public class Range {

    private final double lowerBound;
    private final double higherBound;
    
    public Range(double a, double b) {
	if (a < b) {
	    lowerBound = a;
	    higherBound = b;
	} else {
	    lowerBound = b;
	    higherBound = a;
	}
    }
    
    public boolean contains(double x) {
	return lowerBound <= x && x <= higherBound;
    }
    
    public Range extend(Range r2) {
	return new Range(min(lowerBound, r2.lowerBound), max(higherBound, r2.higherBound));
    }

    private static double min(double a, double b) {
	return (a < b) ? a : b;
    }
    
    private static double max(double a, double b) {
	return (a > b) ? a : b;
    }

    /**
     * @return Returns the higherBound.
     */
    public double getHigherBound() {
        return higherBound;
    }

    /**
     * @return Returns the lowerBound.
     */
    public double getLowerBound() {
        return lowerBound;
    }
    
}
