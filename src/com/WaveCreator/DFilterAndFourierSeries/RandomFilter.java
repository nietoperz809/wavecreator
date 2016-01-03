package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:18:34
 */
class RandomFilter extends FIRFilterType
{
    int n;

    public RandomFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Order");
        dFilterFrame.auxBars[0].setMaximum(1600);
        dFilterFrame.auxBars[0].setValue(100);
        return 1;
    }

    void setup ()
    {
        n = dFilterFrame.auxBars[0].getValue();
    }

    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[n];
        int i;
        for (i = 0; i != n; i++)
        {
            f.aList[i] = dFilterFrame.random.nextInt() * getWindow(i, n);
        }
        setResponse(f);
        return f;
    }

    void getInfo (String x[])
    {
        x[0] = "Random (FIR)";
        x[1] = "Order: " + n;
    }

    boolean needsWindow ()
    {
        return true;
    }

    void setCutoff (double f)
    {
    }
}
