package com.WaveCreator.Oscilloscope;

import com.WaveCreator.Wave16;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * This class implements an oscilloscope
 */
public class Oscilloscope extends JPanel
{
    /**
     * Value used for serialization
     */
    private static final long serialVersionUID = -4159385898572899800L;
    /**
     * Array of points
     */
    float[] m_points;
    /**
     * Scale factor
     */
    private final int yscale;
    /**
     * Path used for drawing
     */
    private final GeneralPath m_path = new GeneralPath();
    /**
     * Current mode. true == draw lines
     */
    private int m_drawmode = 0;
    /**
     * Number of pixels to draw
     */
    int m_length;
    /**
     * First position of Wave16 used for drawing
     */
    int m_offset;

    public final Markers m_markers = new Markers(this);

    /**
     * Constructor for any kind of double[] array
     *
     * @param ysc Y scale factor
     * @param points Array of points to draw
     */
    private Oscilloscope(int ysc, float[] points)
    {
        yscale = ysc;
        m_points = points;
        setBackground(Color.BLACK);
    }

    private Oscilloscope(int ysc, Float[] points)
    {
        float[] pts = new float[points.length];
        for (int s = 0; s < points.length; s++)
        {
            pts[s] = points[s];
        }
        yscale = ysc;
        m_points = pts;
        setBackground(Color.BLACK);
    }

    /**
     * Constructor for Wave16 objects
     *
     * @param w Wave16 object to be used
     */
    public Oscilloscope(Wave16 w)
    {
        this(65536, w.data);
    }

    /**
     * Draws part of array
     *
     * @param x Starting position
     * @param length Number of pixels to draw
     */
    public void drawNow(int x, int length)
    {
        if (m_points.length < 2)
        {
            x = 0;
            m_points = new float[]
            {
                0f, 0f
            };
            length = 2;
        }
        else if ((x + length) >= m_points.length)
        {
            x = m_points.length - length;
        }
        m_offset = x;
        m_length = length;
        repaint();
    }

    /**
     * Changes draw mode from lines to pixels to curve and so on
     */
    public void toggleDrawMode()
    {
        m_drawmode = (m_drawmode + 1) % 4;
        repaint();
    }

    /**
     * Paint method that actually draws the scope
     *
     * @param gg Graphics context
     */
    @Override
    public void paint(Graphics gg)
    {
        Graphics2D g = (Graphics2D) gg;
        int xe = getWidth();
        int ye = getHeight();
        int y2 = ye / 2;
        int x2 = xe * m_length / (2 * m_length - 2);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, xe, ye);

        // draw cross
        g.setColor(Color.GRAY);
        g.drawLine(0, y2, xe, y2);
        g.drawLine(x2, 0, x2, ye);

        float xstep = (float) xe / (m_length - 1);
        float ystep = (float) ye / yscale;

        //draw markers
        if (m_markers.hasMarkers())
        {
            g.setColor(Color.RED);
            int m1 = (int) ((m_markers.m_marker_1 - m_offset) * xstep);
            int m2 = (int) ((m_markers.m_marker_2 - m_offset) * xstep);
            g.drawLine(m1, 0, m1, ye);
            g.drawLine(m2, 0, m2, ye);
        }

        // draw lines
        if (m_drawmode == 0)
        {
            m_path.moveTo(0, y2 - m_points[m_offset] * ystep);
            for (int s = 1; s < m_length; s++)
            {
                m_path.lineTo(s * xstep, y2 - m_points[m_offset + s] * ystep);
            }
        }
        // draw pixels
        else
        {
            setPoint(0, y2 - m_points[m_offset] * ystep);
            for (int s = 1; s < m_length; s++)
            {
                setPoint(s * xstep, y2 - m_points[m_offset + s] * ystep);
            }
        }

        g.setColor(Color.YELLOW);
        g.draw(m_path);
        m_path.reset();
    }

    /**
     * Draws a point in pixel mode
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    private void setPoint(double x, double y)
    {
        switch (m_drawmode)
        {
            case 1:
                m_path.moveTo(x - 2, y);
                m_path.lineTo(x + 2, y);
                m_path.moveTo(x, y - 2);
                m_path.lineTo(x, y + 2);
                break;

            case 2:
                m_path.moveTo(x, y);
                m_path.lineTo(x, y);
                break;

            case 3:
                m_path.moveTo(x, y);
                m_path.lineTo(x, getHeight() / 2);
                break;
        }
    }
}
