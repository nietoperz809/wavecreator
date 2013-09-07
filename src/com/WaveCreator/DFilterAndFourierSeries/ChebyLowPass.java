package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:05:27
*/
class ChebyLowPass extends ChebyFilterType
{
    ChebyLowPass(DFilterFrame dFilterFrame)
    {
        super (dFilterFrame);
        sign = 1;
    }

    int select()
    {
        int s = selectLowPass();
        selectCheby(s++);
        return s;
    }

    void setup()
    {
        setupLowPass();
        setupCheby(2);
    }

    void getPole(int i, Complex c1)
    {
        super.getPole(i, c1);
        c1.mult(sign);
    }

    void getZero(int i, Complex c1)
    {
        c1.set(-sign);
    }

    int getPoleCount()
    {
        return n;
    }

    int getZeroCount()
    {
        return n;
    }

    void getInfo(String x[])
    {
        x[0] = "Chebyshev (IIR), " + getPoleCount() + "-pole";
        getInfoLowPass(x);
        getInfoCheby(x);
    }
}
