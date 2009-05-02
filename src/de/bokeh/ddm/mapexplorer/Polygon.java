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

import java.awt.geom.*;


/**
 * A polygon defined by a list of vertices.
 * <p>
 * The edges of the polygon are from each vertex to the next one, and from
 * the last vertex to the first.
 */
public class Polygon {

    private final Point[] vertices;
    
    /**
     * Construct a new polygon with the given vertices.
     * @param vertices the vertices
     */
    public Polygon(Point... vertices) {
        final int n = vertices.length;
        this.vertices = new Point[n];
        System.arraycopy(vertices, 0, this.vertices, 0, n);
    }
    
    /**
     * Construct a new Polygon from a rectangle.
     * @param rect a Rectangle
     */
    public Polygon(Rectangle rect) {
        double x1 = rect.getLeft();
        double y1 = rect.getBottom();
        double x2 = rect.getRight() + 1;
        double y2 = rect.getTop() + 1;
        
        vertices = new Point[4];
        vertices[0] = new Point(x1, y1);
        vertices[1] = new Point(x2, y1);
        vertices[2] = new Point(x2, y2);
        vertices[3] = new Point(x1, y2);
    }
    
    /**
     * The edges of this polygon.
     * @return An array containing the edges of this polygon.
     */
    public Line[] getEdges() {
	Line[] result = new Line[vertices.length];
	final int n = vertices.length - 1;
	for (int i = 0; i < n; i++) {
	    result[i] = new Line(vertices[i], vertices[i+1]);
	}
	result[n] = new Line(vertices[n], vertices[0]);
	return result;
    }
    
    public Point[] getVertices() {
        return vertices; // TODO: replace by iterable
    }
    
    /**
     * Does this Polygon contain a given point?
     * <p>
     * FIXME: depends on <code>java.awt.geom.GeneralPath</code>. Should be rewritten completely.
     * 
     * @param x the x-coordinate of the point to test
     * @param y the y-coordinate of the point to test
     * @return <code>true</code> if this Polygon contains the point <code>x</code>, <code>y</code>;
     *  <code>false</code> otherwise.
     */
    public boolean contains(double x, double y) {
        GeneralPath p = new GeneralPath();
        p.moveTo((float) vertices[0].getX(), (float) vertices[0].getY());
        for (int i = 1; i < vertices.length; i++) {
            Point pt = vertices[i];
            p.lineTo((float) pt.getX(), (float) pt.getY());
        }
        p.closePath();
        return p.contains(x, y);
    }
}
