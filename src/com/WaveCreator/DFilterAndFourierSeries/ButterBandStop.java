package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:02:33
*/
class ButterBandStop extends ButterBandPass
{
    public ButterBandStop(DFilterFrame dFilterFrame)
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
