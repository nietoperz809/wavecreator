package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:29:05
 */
public abstract class FilterType
{
    public DFilterFrame dFilterFrame;

    public FilterType (DFilterFrame dFilterFrame)
    {
        this.dFilterFrame = dFilterFrame;
    }

    int select ()
    {
        return 0;
    }

    void setup ()
    {
    }

    abstract void getResponse (double w, Complex c);

    int getPoleCount ()
    {
        return 0;
    }

    int getZeroCount ()
    {
        return 0;
    }

    void getPole (int i, Complex c)
    {
        c.set(0);
    }

    void getZero (int i, Complex c)
    {
        c.set(0);
    }

    abstract Filter genFilter ();

    void getInfo (String x[])
    {
    }

    boolean needsWindow ()
    {
        return false;
    }

    void setCutoff (double f)
    {
        dFilterFrame.auxBars[0].setValue((int) (2000 * f));
    }
}
