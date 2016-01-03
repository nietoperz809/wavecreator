package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:11:37
 */
class EllipticBandStop extends EllipticBandPass
{
    public EllipticBandStop (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void getZero (int i, Complex c1)
    {
        getEllipticZero(i / 2, c1, DFilterFrame.pi * .5);
        bandStopXform(i, c1);
    }

    void getPole (int i, Complex c1)
    {
        getBandStopPole(i, c1);
    }
}
