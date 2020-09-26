package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:23:40
 */
class SincBandStopFilter extends FIRFilterType
{
    int n;
    double wc1, wc2, mult, peak;
    double resp[];
    boolean invert;

    public SincBandStopFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Center Frequency");
        dFilterFrame.auxLabels[1].setText(invert ? "Passband Width" : "Stopband Width");
        dFilterFrame.auxLabels[2].setText("Order");
        dFilterFrame.auxBars[0].setValue(500);
        dFilterFrame.auxBars[1].setValue(50);
        dFilterFrame.auxBars[2].setValue(140);
        dFilterFrame.auxBars[2].setMaximum(1600);
        return 3;
    }

    void setup ()
    {
        double wcmid = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        double width = dFilterFrame.auxBars[1].getValue() * DFilterFrame.pi / 1000.;
        wc1 = wcmid - width;
        wc2 = wcmid + width;
        if (wc1 < 0)
        {
            wc1 = 0;
        }
        if (wc2 > DFilterFrame.pi)
        {
            wc2 = DFilterFrame.pi;
        }
        n = dFilterFrame.auxBars[2].getValue();
    }

    int getPoleCount ()
    {
        return 0;
    }

    void getPole (int i, InternalComplex c1)
    {
    }

    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[n + 1];
        double xlist[] = new double[n + 1];
        int n2 = n / 2;
        int i;

        // generate low-pass filter
        double sum = 0;
        for (i = 0; i != n; i++)
        {
            int ii = i - n2;
            f.aList[i] = ((ii == 0) ? wc1 : Math.sin(wc1 * ii) / ii) *
                    getWindow(i, n);
            sum += f.aList[i];
        }
        if (sum > 0)
        {
            // normalize
            for (i = 0; i != n; i++)
            {
                f.aList[i] /= sum;
            }
        }

        // generate high-pass filter
        sum = 0;
        for (i = 0; i != n; i++)
        {
            int ii = i - n2;
            xlist[i] = ((ii == 0) ? wc2 : Math.sin(wc2 * ii) / ii) *
                    getWindow(i, n);
            sum += xlist[i];
        }
        // normalize
        for (i = 0; i != n; i++)
        {
            xlist[i] /= sum;
        }
        // invert and combine with lopass
        for (i = 0; i != n; i++)
        {
            f.aList[i] -= xlist[i];
        }
        f.aList[n2] += 1;
        if (invert)
        {
            for (i = 0; i != n; i++)
            {
                f.aList[i] = -f.aList[i];
            }
            f.aList[n2] += 1;
        }
        if (n == 1)
        {
            f.aList[0] = 1;
        }
        setResponse(f);
        return f;
    }

    void getInfo (String x[])
    {
        x[0] = (invert) ? "Passband: " : "Stopband: ";
        x[0] += dFilterFrame.getOmegaText(wc1) + " - " + dFilterFrame.getOmegaText(wc2);
        x[1] = "Order: " + n;
    }

    boolean needsWindow ()
    {
        return true;
    }
}
