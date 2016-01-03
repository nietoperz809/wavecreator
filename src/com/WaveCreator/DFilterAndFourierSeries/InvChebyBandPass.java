package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:16:29
 */
class InvChebyBandPass extends InvChebyFilterType
{
    public InvChebyBandPass (DFilterFrame dFilterFrame)
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

    void getZero (int i, Complex c1)
    {
        getChebyZero(i / 2, c1, DFilterFrame.pi * .5);
        bandPassXform(i, c1);
    }

    void getInfo (String x[])
    {
        x[0] = "Inv Cheby (IIR), " + getPoleCount() + "-pole";
        getInfoBandPass(x, this instanceof InvChebyBandStop);
        getInfoCheby(x);
    }

    int getPoleCount ()
    {
        return n * 2;
    }

    int getZeroCount ()
    {
        return n * 2;
    }

    void getPole (int i, Complex c1)
    {
        getBandPassPole(i, c1);
    }
}
