/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turtle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 * Double buffered Turtle
 * @author Administrator
 */
public class DoubleBufferedTurtle extends Turtle
{
    private final GraphicsConfiguration gconf = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();

    /**
     * Off-screen image used as canvas
     */
    private final BufferedImage offImg;
    
    /**
     * Must be called at end of drawing/moving the turtle
     * @throws Exception 
     */
    @Override
    void doneGraphics() throws Exception
    {
        super.drawTurtleSteps(offImg.getGraphics());
        super.doneGraphics();
    }
    
    /**
     * Constructor taking width/height of image
     * @param width Width
     * @param height Height
     */
    public DoubleBufferedTurtle(int width, int height)
    {
        super(width, height);
        offImg = gconf.createCompatibleImage(width, height);
        penColor = Color.WHITE;
    }

    /**
     * Overridden function that draws image to screen
     * @param g Graphics context
     */
    @Override
    public void paint(Graphics g)
    {
        g.drawImage(offImg, 0, 0, this);
    }
    
    /**
     * Overridden and empty to prevent unnecessary background clearing
     * @param g Graphics context
     */
    @Override
    public void update (Graphics g)
    {
        
    }
}
