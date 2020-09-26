package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:16:51
 */
class InvChebyHighPass extends InvChebyLowPass
{
    public InvChebyHighPass (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void getPole (int i, InternalComplex c1)
    {
        getSPole(i, c1, DFilterFrame.pi - wc);
        bilinearXform(c1);
        c1.mult(-1);
    }

    void getZero (int i, InternalComplex c1)
    {
        getChebyZero(i, c1, DFilterFrame.pi - wc);
        c1.mult(-1);
    }
}
