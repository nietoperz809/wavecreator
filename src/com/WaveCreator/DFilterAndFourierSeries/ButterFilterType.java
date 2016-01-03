package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:34:57
 */
public abstract class ButterFilterType extends PoleFilterType
{
    public ButterFilterType (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void getSPole (int i, Complex c1, double wc)
    {
        double theta = DFilterFrame.pi / 2 + (2 * i + 1) * DFilterFrame.pi / (2 * n);
        c1.setMagPhase(Math.tan(wc * .5), theta);
    }
}
