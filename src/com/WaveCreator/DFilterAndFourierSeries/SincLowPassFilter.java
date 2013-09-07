package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:24:05
*/
class SincLowPassFilter extends FIRFilterType
{
    int n;
    double wc, mult, peak;
    double resp[];
    boolean invert;

    public SincLowPassFilter(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select()
    {
        dFilterFrame.auxLabels[0].setText("Cutoff Frequency");
        dFilterFrame.auxLabels[1].setText("Order");
        dFilterFrame.auxBars[0].setValue(invert ? 500 : 100);
        dFilterFrame.auxBars[1].setValue(120);
        dFilterFrame.auxBars[1].setMaximum(1600);
        return 2;
    }

    void setup()
    {
        wc = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        n = dFilterFrame.auxBars[1].getValue();
    }

    Filter genFilter()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[n];
        int n2 = n / 2;
        int i;
        double sum = 0;
        for (i = 0; i != n; i++)
        {
            int ii = i - n2;
            f.aList[i] = ((ii == 0) ? wc : Math.sin(wc * ii) / ii) * getWindow(i, n);
            sum += f.aList[i];
        }
        // normalize
        for (i = 0; i != n; i++)
        {
            f.aList[i] /= sum;
        }
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

    void getInfo(String x[])
    {
        x[0] = "Cutoff freq: " + dFilterFrame.getOmegaText(wc);
        x[1] = "Order: " + n;
    }

    boolean needsWindow()
    {
        return true;
    }
}
