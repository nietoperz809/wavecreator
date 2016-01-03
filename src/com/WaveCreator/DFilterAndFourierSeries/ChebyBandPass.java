package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:04:49
 */
class ChebyBandPass extends ChebyFilterType
{
    public ChebyBandPass (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        int s = selectBandPass();
        selectCheby(s++);
        return s;
    }

    void setup ()
    {
        setupBandPass();
        setupCheby(3);
    }

    int getPoleCount ()
    {
        return n * 2;
    }

    int getZeroCount ()
    {
        return n * 2;
    }

    void getZero (int i, Complex c1)
    {
        getBandPassZero(i, c1);
    }

    void getInfo (String x[])
    {
        x[0] = "Chebyshev (IIR), " + getPoleCount() + "-pole";
        getInfoBandPass(x, this instanceof ChebyBandStop);
        getInfoCheby(x);
    }

    void getPole (int i, Complex c1)
    {
        getBandPassPole(i, c1);
    }
}
