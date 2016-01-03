package com.WaveCreator.DFilterAndFourierSeries;

import java.awt.*;

/**
 * New Class.
 * User: Administrator
 * Date: 21.04.2009
 * Time: 00:54:21
 */
class FourierCanvas extends Canvas
{
    final FourierFrame pg;

    FourierCanvas (FourierFrame p)
    {
        pg = p;
    }

    public Dimension getPreferredSize ()
    {
        return new Dimension(300, 400);
    }

    public void paint (Graphics g)
    {
        pg.updateFourier(g);
    }

    public void update (Graphics g)
    {
        pg.updateFourier(g);
    }
}
