package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:21:09
 */
class ResonatorFilter extends IIRFilterType
{
    double r, wc;

    public ResonatorFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Resonant Frequency");
        dFilterFrame.auxBars[0].setValue(500);
        dFilterFrame.auxLabels[1].setText("Sharpness");
        dFilterFrame.auxBars[1].setValue(900);
        return 2;
    }

    void setup ()
    {
        wc = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        double rolldb = -dFilterFrame.auxBars[1].getValue() * 3 / 1000.;
        r = 1 - Math.pow(10, rolldb);
    }

    int getPoleCount ()
    {
        return 2;
    }

    void getPole (int i, Complex c1)
    {
        c1.setMagPhase(r, (i == 1) ? -wc : wc);
    }

    void getInfo (String x[])
    {
        x[0] = "Reson (IIR)";
        x[1] = "Res. Frequency: " + dFilterFrame.getOmegaText(wc);
    }
}
