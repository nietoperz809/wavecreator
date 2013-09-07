package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:01:58
*/
class ButterBandPass extends ButterFilterType
{
    public ButterBandPass(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select()
    {
        return selectBandPass();
    }

    void setup()
    {
        setupBandPass();
    }

    void getPole(int i, Complex c1)
    {
        getBandPassPole(i, c1);
    }

    void getZero(int i, Complex c1)
    {
        getBandPassZero(i, c1);
    }

    int getPoleCount()
    {
        return n * 2;
    }

    int getZeroCount()
    {
        return n * 2;
    }

    void getInfo(String x[])
    {
        x[0] = "Butterworth (IIR), " + getPoleCount() + "-pole";
        getInfoBandPass(x, this instanceof ButterBandStop);
    }
}
