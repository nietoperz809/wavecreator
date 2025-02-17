package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:12:04
 */
class EllipticLowPass extends EllipticFilterType
{
    public EllipticLowPass (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        int s = selectLowPass();
        selectElliptic(s);
        return s + 2;
    }

    void setup ()
    {
        setupLowPass();
        setupElliptic(2);
    }

    void getZero (int i, InternalComplex c1)
    {
        getEllipticZero(i, c1, wc);
    }

    void getInfo (String x[])
    {
        x[0] = "Elliptic (IIR), " + getPoleCount() + "-pole";
        getInfoLowPass(x);
        getInfoElliptic(x);
    }
}
