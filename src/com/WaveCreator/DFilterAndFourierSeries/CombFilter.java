package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:08:22
 */
class CombFilter extends IIRFilterType
{
    final int sign;
    int n;
    double mult, peak;

    CombFilter (DFilterFrame dFilterFrame, int s)
    {
        super(dFilterFrame);
        sign = s;
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("1st Pole");
        dFilterFrame.auxBars[0].setValue(60);
        dFilterFrame.auxLabels[1].setText("Sharpness");
        dFilterFrame.auxBars[1].setValue(700);
        return 2;
    }

    void setup ()
    {
        n = 2000 / dFilterFrame.auxBars[0].getValue();
        mult = dFilterFrame.auxBars[1].getValue() / 1000.;
        peak = 1 / (1 - mult);
    }

    int getPoleCount ()
    {
        return n;
    }

    int getZeroCount ()
    {
        return n;
    }

    void getPole (int i, Complex c1)
    {
        int odd = (sign == 1) ? 0 : 1;
        c1.setMagPhase(Math.pow(mult, 1. / n), DFilterFrame.pi * (odd + 2 * i) / n);
    }

    void getZero (int i, Complex c1)
    {
        c1.set(0);
    }

    void getInfo (String x[])
    {
        x[0] = "Comb (IIR); Resonance every " + dFilterFrame.getOmegaText(2 * DFilterFrame.pi / n);
        x[1] = "Delay: " + n + " samples, " +
                dFilterFrame.getUnitText(n / (double) dFilterFrame.sampleRate, "s");
        double tl = 340. * n / (dFilterFrame.sampleRate * 2);
        x[2] = "Tube length: " + dFilterFrame.getUnitText(tl, "m");
        if (sign == -1)
        {
            x[2] += " (closed)";
        }
        else
        {
            x[2] += " (open)";
        }
    }

    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[]{1 / peak, 0};
        f.bList = new double[]{0, -sign * mult};
        f.nList = new int[]{0, n};
        setResponse(f);
        return f;
    }
}
