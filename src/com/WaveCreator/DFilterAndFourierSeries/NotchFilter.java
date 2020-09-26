package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:18:11
 */
class NotchFilter extends IIRFilterType
{
    double wc, a, b, bw;

    public NotchFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Notch Frequency");
        dFilterFrame.auxBars[0].setValue(500);
        dFilterFrame.auxLabels[1].setText("Bandwidth");
        dFilterFrame.auxBars[1].setValue(900);
        return 2;
    }

    void setup ()
    {
        wc = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        bw = dFilterFrame.auxBars[1].getValue() * DFilterFrame.pi / 2000.;
        a = (1 - Math.tan(bw / 2)) / (1 + Math.tan(bw / 2));
        b = Math.cos(wc);
    }

    int getPoleCount ()
    {
        return 2;
    }

    int getZeroCount ()
    {
        return 2;
    }

    void getPole (int i, InternalComplex c1)
    {
        c1.set(-4 * a + (b + a * b) * (b + a * b));
        c1.sqrt();
        if (i == 1)
        {
            c1.mult(-1);
        }
        c1.add(b + a * b);
        c1.mult(.5);
    }

    void getZero (int i, InternalComplex c1)
    {
        c1.set(b * b - 1);
        c1.sqrt();
        if (i == 1)
        {
            c1.mult(-1);
        }
        c1.add(b);
    }

    void getInfo (String x[])
    {
        x[0] = "Notch (IIR)";
        x[1] = "Notch Frequency: " + dFilterFrame.getOmegaText(wc);
        x[2] = "Bandwidth: " + dFilterFrame.getOmegaText(bw);
    }
}
