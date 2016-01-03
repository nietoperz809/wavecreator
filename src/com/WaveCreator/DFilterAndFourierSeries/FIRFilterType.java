package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:43:47
 */
public abstract class FIRFilterType extends FilterType
{
    double response[];

    public FIRFilterType (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    @Override
    void getResponse (double w, Complex c)
    {
        if (response == null)
        {
            c.set(0);
            return;
        }
        int off = (int) (response.length * w / (2 * DFilterFrame.pi));
        off &= ~1;
        if (off < 0)
        {
            off = 0;
        }
        if (off >= response.length)
        {
            off = response.length - 2;
        }
        c.set(response[off], response[off + 1]);
    }

    double getWindow (int i, int n)
    {
        if (n == 1)
        {
            return 1;
        }
        double x = 2 * DFilterFrame.pi * i / (n - 1);
        double n2 = (double) n / 2; // int
        switch (dFilterFrame.windowChooser.getSelectedIndex())
        {
            case 0:
                return 1; // rect
            case 1:
                return .54 - .46 * Math.cos(x); // hamming
            case 2:
                return .5 - .5 * Math.cos(x); // hann
            case 3:
                return .42 - .5 * Math.cos(x) + .08 * Math.cos(2 * x); // blackman
            case 4:
            {
                double kaiserAlphaPi = dFilterFrame.kaiserBar.getValue() * DFilterFrame.pi / 120.;
                double q = (2 * i / (double) n) - 1;
                return dFilterFrame.bessi0(kaiserAlphaPi * Math.sqrt(1 - q * q));
            }
            case 5:
                return (i < n2) ? i / n2 : 2 - i / n2; // bartlett
            case 6:
            {
                double xt = (i - n2) / n2;
                return 1 - xt * xt;
            } // welch
        }
        return 0;
    }

    void setResponse (DirectFilter f)
    {
        response = new double[8192];
        int i;
        if (f.nList.length != f.aList.length)
        {
            f.nList = new int[f.aList.length];
            for (i = 0; i != f.aList.length; i++)
            {
                f.nList[i] = i;
            }
        }
        for (i = 0; i != f.aList.length; i++)
        {
            response[f.nList[i] * 2] = f.aList[i];
        }
        new FFT(response.length / 2).transform(response, false);
        double maxresp = 0;
        int j;
        for (j = 0; j != response.length; j += 2)
        {
            double r2 = response[j] * response[j] + response[j + 1] * response[j + 1];
            if (maxresp < r2)
            {
                maxresp = r2;
            }
        }
        // normalize response
        maxresp = Math.sqrt(maxresp);
        for (j = 0; j != response.length; j++)
        {
            response[j] /= maxresp;
        }
        for (j = 0; j != f.aList.length; j++)
        {
            f.aList[j] /= maxresp;
        }
    }
}
