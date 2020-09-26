package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:56:50
 */
public class AllPassFilter extends IIRFilterType
{
    double a;

    public AllPassFilter (DFilterFrame d)
    {
        super(d);
    }

    @Override
    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Phase Delay");
        dFilterFrame.auxBars[0].setValue(500);
        return 1;
    }

    @Override
    void setup ()
    {
        double delta = dFilterFrame.auxBars[0].getValue() / 1000.;
        a = (1 - delta) / (1 + delta);
    }

    @Override
    int getPoleCount ()
    {
        return 1;
    }

    @Override
    void getPole (int i, InternalComplex c1)
    {
        c1.set(-a);
    }

    @Override
    void getInfo (String x[])
    {
        x[0] = "Allpass Fractional Delay (IIR)";
    }

    @Override
    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[2];
        f.bList = new double[2];
        f.nList = new int[]{0, 1};
        f.aList[0] = a;
        f.aList[1] = 1;
        f.bList[0] = 1;
        f.bList[1] = a;
        setResponse(f);
        return f;
    }
}
