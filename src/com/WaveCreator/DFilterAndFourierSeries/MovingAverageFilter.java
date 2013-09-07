package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:17:57
*/
class MovingAverageFilter extends FIRFilterType
{
    double n;
    int ni;

    public MovingAverageFilter(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select()
    {
        dFilterFrame.auxLabels[0].setText("Cutoff Frequency");
        dFilterFrame.auxBars[0].setValue(500);
        return 1;
    }

    void setup()
    {
        n = 2000. / dFilterFrame.auxBars[0].getValue();
        if (n > 1000)
        {
            n = 1000;
        }
        ni = (int) n;
    }

    Filter genFilter()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[ni + 1];
        int i;
        for (i = 0; i != ni; i++)
        {
            f.aList[i] = 1. / n;
        }
        f.aList[i] = (n - ni) / n;
        setResponse(f);
        return f;
    }

    void getInfo(String x[])
    {
        x[0] = "Moving Average (FIR)";
        x[1] = "Cutoff: " + dFilterFrame.getOmegaText(2 * DFilterFrame.pi / n);
        x[2] = "Length: " + dFilterFrame.showFormat.format(n);
    }
}
