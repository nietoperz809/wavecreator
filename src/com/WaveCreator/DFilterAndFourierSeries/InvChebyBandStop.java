package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:16:40
*/
class InvChebyBandStop extends InvChebyBandPass
{
    public InvChebyBandStop(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void getPole(int i, Complex c1)
    {
        getBandStopPole(i, c1);
    }

    void getZero(int i, Complex c1)
    {
        getChebyZero(i / 2, c1, DFilterFrame.pi * .5);
        bandStopXform(i, c1);
    }
}
