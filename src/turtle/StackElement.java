/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package turtle;

import java.awt.Point;

/**
 * Stack Element class
 * Contains angle and position to be stored on stack
 * @author Administrator
 */
public class StackElement
{
    private final double angle;
    private final Point point;

    /**
     * Constructor: supplies values to be stored
     * @param d The angle
     * @param p The point
     */
    public StackElement(double d, Point p)
    {
        angle = d;
        point = new Point(p);
    }

    /**
     * Get Angle
     * @return the angle
     */
    public double getAngle()
    {
        return angle;
    }

    /**
     * Get the point
     * @return  the Point
     */
    public Point getPoint()
    {
        return point;
    }
    
}
