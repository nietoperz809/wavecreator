package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:17:44
*/
class InverseCombFilter extends FIRFilterType
{
    int n;
    double mult, peak;

    public InverseCombFilter(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select()
    {
        dFilterFrame.auxLabels[0].setText("2nd Zero");
        dFilterFrame.auxBars[0].setValue(60);
        dFilterFrame.auxLabels[1].setText("Sharpness");
        dFilterFrame.auxBars[1].setValue(1000);
        return 2;
    }

    void setup()
    {
        n = 1990 / dFilterFrame.auxBars[0].getValue();
        mult = dFilterFrame.auxBars[1].getValue() / 1000.;
        peak = 1 + mult;
    }

    void getZero(int i, Complex c1)
    {
        c1.setMagPhase(Math.pow(mult, 1. / n), DFilterFrame.pi * 2 * i / n);
    }

    int getZeroCount()
    {
        return n;
    }

    Filter genFilter()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[]{1 / peak, -mult / peak};
        f.nList = new int[]{0, n};
        setResponse(f);
        return f;
    }

    void getInfo(String x[])
    {
        x[0] = "Inverse Comb (FIR)";
        x[1] = "Zeros every " + dFilterFrame.getOmegaText(2 * DFilterFrame.pi / n);
    }
}
