package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:05:27
 */
class ChebyLowPass extends ChebyFilterType
{
    ChebyLowPass (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
        sign = 1;
    }

    int select ()
    {
        int s = selectLowPass();
        selectCheby(s++);
        return s;
    }

    void setup ()
    {
        setupLowPass();
        setupCheby(2);
    }

    int getPoleCount ()
    {
        return n;
    }

    int getZeroCount ()
    {
        return n;
    }

    void getZero (int i, InternalComplex c1)
    {
        c1.set(-sign);
    }

    void getInfo (String x[])
    {
        x[0] = "Chebyshev (IIR), " + getPoleCount() + "-pole";
        getInfoLowPass(x);
        getInfoCheby(x);
    }

    void getPole (int i, InternalComplex c1)
    {
        super.getPole(i, c1);
        c1.mult(sign);
    }
}
