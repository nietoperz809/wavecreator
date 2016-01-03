package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:11:47
 */
class EllipticHighPass extends EllipticLowPass
{
    public EllipticHighPass (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void getPole (int i, Complex c1)
    {
        getSPole(i, c1, DFilterFrame.pi - wc);
        bilinearXform(c1);
        c1.mult(-1);
    }

    void getZero (int i, Complex c1)
    {
        getEllipticZero(i, c1, DFilterFrame.pi - wc);
        c1.mult(-1);
    }
}
