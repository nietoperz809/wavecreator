package com.WaveCreator.DFilterAndFourierSeries;

import java.awt.*;

/**
 * New Class.
 * User: Administrator
 * Date: 04.01.2009
 * Time: 12:45:03
 */
class DFilterCanvas extends Canvas
{
    final DFilterFrame pg;

    DFilterCanvas(final DFilterFrame p)
    {
        pg = p;
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(300, 400);
    }

    public void update(final Graphics g)
    {
        pg.updateDFilter(g);
    }

    public void paint(final Graphics g)
    {
        pg.updateDFilter(g);
    }
}
