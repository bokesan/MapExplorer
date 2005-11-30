package de.bokeh.ddm.mapexplorer;


public class IntersectionResult {
    
    public static IntersectionResult coincident() {
	return new IntersectionResult(COINCIDENT);
    }
    
    public static IntersectionResult parallel() {
	return new IntersectionResult(PARALLEL);
    }
    
    public static IntersectionResult at(Point p) {
	return new IntersectionResult(INTERSECTION, p);
    }
    
    public static IntersectionResult outside(Point p) {
	return new IntersectionResult(OUTSIDE, p);
    }
    
    private static final int INTERSECTION = 0;
    private static final int COINCIDENT = 1;
    private static final int PARALLEL = 2;
    private static final int OUTSIDE = 3;
    
    private final int type;
    private final Point intersection;
    
    private IntersectionResult(int type, Point intersection) {
	assert intersection != null;
	this.type = type;
	this.intersection = intersection;
    }
    
    private IntersectionResult(int type) {
	this.type = type;
	this.intersection = null;
    }

    public boolean isParallel() {
	return type == PARALLEL;
    }
    
    public boolean isCoincident() {
	return type == COINCIDENT;
    }
    
    public boolean isIntersection() {
	return type == INTERSECTION;
    }
    
    public boolean isOutsideIntersection() {
	return type == OUTSIDE;
    }
    
    /**
     * The intersection point.
     * @return The intersection point.
     */
    public Point getIntersection() {
	return intersection;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	switch (type) {
	case INTERSECTION: return "IntersectionResult{INTERSECTION," + intersection + "}";
	case OUTSIDE: return "IntersectionResult{OUTSIDE," + intersection + "}";
	case PARALLEL: return "IntersectionResult{PARALLEL}";
	case COINCIDENT: return "IntersectionResult{COINCIDENT}";
	default:
	    throw new AssertionError(type);
	}
    }
    
}
