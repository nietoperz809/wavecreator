package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:05:02
*/
class ChebyBandStop extends ChebyBandPass
{
    public ChebyBandStop(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void getPole(int i, Complex c1)
    {
        getBandStopPole(i, c1);
    }

    void getZero(int i, Complex c1)
    {
        getBandStopZero(i, c1);
    }
}
