package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:31:27
 */
abstract class PoleFilterType extends IIRFilterType
{
    int n;
    double wc, wc2;

    public PoleFilterType (DFilterFrame d)
    {
        super(d);
    }

    @Override
    void getPole (int i, InternalComplex c1)
    {
        getSPole(i, c1, wc);
        bilinearXform(c1);
    }

    abstract void getSPole (int i, InternalComplex c1, double wc);

    void bilinearXform (InternalComplex c1)
    {
        InternalComplex c2 = new InternalComplex(c1);
        c1.add(1);
        c2.mult(-1);
        c2.add(1);
        c1.div(c2);
    }

    int selectLowPass ()
    {
        dFilterFrame.auxLabels[0].setText("Cutoff Frequency");
        dFilterFrame.auxLabels[1].setText("Number of Poles");
        dFilterFrame.auxBars[1].setMaximum(40);
        dFilterFrame.auxBars[0].setValue(100);
        dFilterFrame.auxBars[1].setValue(4);
        return 2;
    }

    int selectBandPass ()
    {
        dFilterFrame.auxLabels[0].setText("Center Frequency");
        dFilterFrame.auxLabels[1].setText("Passband Width");
        dFilterFrame.auxLabels[2].setText("Number of Poles");
        dFilterFrame.auxBars[2].setMaximum(20);
        dFilterFrame.auxBars[0].setValue(500);
        dFilterFrame.auxBars[1].setValue(200);
        dFilterFrame.auxBars[2].setValue(6);
        return 3;
    }

    void getBandPassPole (int i, InternalComplex z)
    {
        getSPole(i / 2, z, DFilterFrame.pi * .5);
        bilinearXform(z);
        bandPassXform(i, z);
    }

    void bandPassXform (int i, InternalComplex z)
    {
        double a = Math.cos((wc + wc2) * .5) /
                Math.cos((wc - wc2) * .5);
        double b = 1 / Math.tan(.5 * (wc - wc2));
        InternalComplex c2 = new InternalComplex();
        c2.addMult(4 * (b * b * (a * a - 1) + 1), z);
        c2.add(8 * (b * b * (a * a - 1) - 1));
        c2.mult(z);
        c2.add(4 * (b * b * (a * a - 1) + 1));
        c2.sqrt();
        if ((i & 1) == 0)
        {
            c2.mult(-1);
        }
        c2.addMult(2 * a * b, z);
        c2.add(2 * a * b);
        InternalComplex c3 = new InternalComplex();
        c3.addMult(2 * (b - 1), z);
        c3.add(2 * (1 + b));
        c2.div(c3);
        z.set(c2);
    }

    void getBandStopPole (int i, InternalComplex z)
    {
        getSPole(i / 2, z, DFilterFrame.pi * .5);
        bilinearXform(z);
        bandStopXform(i, z);
    }

    void bandStopXform (int i, InternalComplex z)
    {
        double a = Math.cos((wc + wc2) * .5) /
                Math.cos((wc - wc2) * .5);
        double b = Math.tan(.5 * (wc - wc2));
        InternalComplex c2 = new InternalComplex();
        c2.addMult(4 * (b * b + a * a - 1), z); // z^2 terms
        c2.add(8 * (b * b - a * a + 1)); // z terms
        c2.mult(z);
        c2.add(4 * (a * a + b * b - 1));
        c2.sqrt(); // c2 = discrim.
        c2.mult(((i & 1) == 0) ? .5 : -.5);
        c2.add(a);
        c2.addMult(-a, z);
        InternalComplex c3 = new InternalComplex(b + 1, 0);
        c3.addMult(b - 1, z);
        c2.div(c3);
        z.set(c2);
    }

    void getBandStopZero (int i, InternalComplex z)
    {
        z.set(-1, 0);
        bandStopXform(i, z);
    }

    void getBandPassZero (int i, InternalComplex c1)
    {
        if (i >= n)
        {
            c1.set(1);
        }
        else
        {
            c1.set(-1);
        }
    }

    void setupLowPass ()
    {
        wc = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        n = dFilterFrame.auxBars[1].getValue();
    }

    void setupBandPass ()
    {
        double wcmid = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        double width = dFilterFrame.auxBars[1].getValue() * DFilterFrame.pi / 1000.;
        wc = wcmid + width / 2;
        wc2 = wcmid - width / 2;
        if (wc2 < 0)
        {
            wc2 = 1e-8;
        }
        if (wc >= DFilterFrame.pi)
        {
            wc = DFilterFrame.pi - 1e-8;
        }
        n = dFilterFrame.auxBars[2].getValue();
    }

    void getInfoLowPass (String x[])
    {
        x[1] = "Cutoff freq: " + dFilterFrame.getOmegaText(wc);
    }

    void getInfoBandPass (String x[], boolean stop)
    {
        x[1] = (stop ? "Stopband: " : "Passband: ") +
                dFilterFrame.getOmegaText(wc2) + " - " + dFilterFrame.getOmegaText(wc);
    }
}
