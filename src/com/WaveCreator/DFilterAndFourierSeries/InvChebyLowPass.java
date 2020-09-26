package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:17:01
 */
class InvChebyLowPass extends InvChebyFilterType
{
    public InvChebyLowPass (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
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

    void getZero (int i, InternalComplex c1)
    {
        getChebyZero(i, c1, wc);
    }

    void getInfo (String x[])
    {
        x[0] = "Inverse Chebyshev (IIR), " + getPoleCount() + "-pole";
        getInfoLowPass(x);
        getInfoCheby(x);
    }
}
