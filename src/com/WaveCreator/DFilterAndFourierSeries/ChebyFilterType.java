package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:59:33
 */
abstract class ChebyFilterType extends PoleFilterType
{
    double epsilon;
    int sign;

    public ChebyFilterType (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    void selectCheby (int s)
    {
        dFilterFrame.auxLabels[s].setText("Passband Ripple");
        dFilterFrame.auxBars[s].setValue(60);
    }

    void setupCheby (int a)
    {
        int val = dFilterFrame.auxBars[a].getValue();
        double ripdb;
        if (val < 300)
        {
            ripdb = 5 * val / 300.;
        }
        else
        {
            ripdb = 5 + 45 * (val - 300) / 700.;
        }
        double ripval = Math.exp(-ripdb * .1 * DFilterFrame.log10);
        epsilon = Math.sqrt(1 / ripval - 1);
    }

    void getSPole (int i, InternalComplex c1, double wc)
    {
        //InternalComplex c2 = new InternalComplex();
        double alpha = 1 / epsilon + Math.sqrt(1 + 1 / (epsilon * epsilon));
        double a = .5 * (Math.pow(alpha, 1. / n) - Math.pow(alpha, -1. / n));
        double b = .5 * (Math.pow(alpha, 1. / n) + Math.pow(alpha, -1. / n));
        double theta = DFilterFrame.pi / 2 + (2 * i + 1) * DFilterFrame.pi / (2 * n);
        if (sign == -1)
        {
            wc = DFilterFrame.pi - wc;
        }
        c1.setMagPhase(Math.tan(wc * .5), theta);
        c1.re *= a;
        c1.im *= b;
        c1.setMagPhase();
    }

    void getInfoCheby (String x[])
    {
        x[2] = "Ripple: " +
                dFilterFrame.showFormat.format(-10 * Math.log(1 / (1 + epsilon * epsilon)) /
                        DFilterFrame.log10) + " dB";
    }
}
