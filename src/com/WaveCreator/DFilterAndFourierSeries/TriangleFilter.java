package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:24:26
 */
class TriangleFilter extends FIRFilterType
{
    int ni;
    double n;

    public TriangleFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Cutoff Frequency");
        dFilterFrame.auxBars[0].setValue(500);
        return 1;
    }

    void setup ()
    {
        n = 4000. / dFilterFrame.auxBars[0].getValue();
        if (n > 1000)
        {
            n = 1000;
        }
        ni = (int) n;
    }

    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[ni + 1];
        int i;
        double sum = 0;
        double n2 = n / 2;
        for (i = 0; i < n; i++)
        {
            double q;
            if (i < n2)
            {
                q = i / n2;
            }
            else
            {
                q = 2 - (i / n2);
            }
            sum += q;
            f.aList[i] = q;
        }
        for (i = 0; i != f.aList.length; i++)
        {
            f.aList[i] /= sum;
        }
        setResponse(f);
        return f;
    }

    void getInfo (String x[])
    {
        x[0] = "Triangle (FIR)";
        x[1] = "Cutoff: " + dFilterFrame.getOmegaText(4 * DFilterFrame.pi / n);
        x[2] = "Length: " + dFilterFrame.showFormat.format(n);
    }
}
