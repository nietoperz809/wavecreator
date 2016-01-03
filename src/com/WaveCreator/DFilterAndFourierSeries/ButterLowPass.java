package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:03:00
 */
class ButterLowPass extends ButterFilterType
{
    int sign;

    ButterLowPass (DFilterFrame ddFilterFrame)
    {
        super(ddFilterFrame);
        sign = 1;
    }

    int select ()
    {
        return selectLowPass();
    }

    void setup ()
    {
        setupLowPass();
    }

    int getPoleCount ()
    {
        return n;
    }

    int getZeroCount ()
    {
        return n;
    }

    void getZero (int i, Complex c1)
    {
        c1.set(-sign);
    }

    void getInfo (String x[])
    {
        x[0] = "Butterworth (IIR), " + getPoleCount() + "-pole";
        getInfoLowPass(x);
    }
}
