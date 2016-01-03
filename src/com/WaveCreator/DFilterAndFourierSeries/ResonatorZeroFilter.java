package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:21:24
 */
class ResonatorZeroFilter extends ResonatorFilter
{
    public ResonatorZeroFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int getZeroCount ()
    {
        return 2;
    }

    void getZero (int i, Complex c1)
    {
        c1.set(i == 0 ? 1 : -1);
    }
}
