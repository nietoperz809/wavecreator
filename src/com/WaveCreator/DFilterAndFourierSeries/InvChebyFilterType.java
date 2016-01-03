package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:14:50
 */
public abstract class InvChebyFilterType extends ChebyFilterType
{
    double scale;

    public InvChebyFilterType (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void selectCheby (int s)
    {
        dFilterFrame.auxLabels[s].setText("Stopband Attenuation");
        dFilterFrame.auxBars[s].setValue(600);
    }

    void setupCheby (int a)
    {
        epsilon = Math.exp(-dFilterFrame.auxBars[a].getValue() / 120.);
        scale = dFilterFrame.cosh(dFilterFrame.acosh(1 / epsilon) / n);
    }

    void getSPole (int i, Complex c1, double wc)
    {
        wc = DFilterFrame.pi - wc;
        super.getSPole(i, c1, wc);
        c1.recip();
        c1.mult(scale);
    }

    void getInfoCheby (String x[])
    {
        x[2] = "Stopband attenuation: " +
                dFilterFrame.showFormat.format(-10 * Math.log(1 + 1 / (epsilon * epsilon)) /
                        DFilterFrame.log10) + " dB";
    }

    void getChebyZero (int i, Complex c1, double wc)
    {
        double bk = 1 / Math.cos((2 * i + 1) * DFilterFrame.pi / (2 * n)) * scale;
        double a = Math.sin(DFilterFrame.pi / 4 - wc / 2) / Math.sin(DFilterFrame.pi / 4 + wc / 2);
        c1.set(1 + a, bk * (1 - a));
        Complex c2 = new Complex(1 + a, bk * (a - 1));
        c1.div(c2);
    }

    int getPoleCount ()
    {
        return n;
    }

    int getZeroCount ()
    {
        return n;
    }
}
