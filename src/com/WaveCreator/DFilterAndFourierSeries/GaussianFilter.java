package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:14:27
*/
class GaussianFilter extends FIRFilterType
{
    int n;
    double cw;

    public GaussianFilter(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select()
    {
        dFilterFrame.auxLabels[0].setText("Offset");
        dFilterFrame.auxBars[0].setMaximum(1000);
        dFilterFrame.auxBars[0].setValue(100);
        dFilterFrame.auxLabels[1].setText("Width");
        dFilterFrame.auxBars[1].setMaximum(1000);
        dFilterFrame.auxBars[1].setValue(100);
        dFilterFrame.auxLabels[2].setText("Order");
        dFilterFrame.auxBars[2].setMaximum(1600);
        dFilterFrame.auxBars[2].setValue(160);
        return 3;
    }

    void setup()
    {
        n = dFilterFrame.auxBars[2].getValue();
        cw = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
    }

    Filter genFilter()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[n];
        int i;
        double w = dFilterFrame.auxBars[1].getValue() / 100000.;
        int n2 = n / 2;
        for (i = 0; i != n; i++)
        {
            int ii = i - n2;
            f.aList[i] = Math.exp(-w * ii * ii) * Math.cos(ii * cw) * getWindow(i, n);
        }
        setResponse(f);
        return f;
    }

    boolean needsWindow()
    {
        return true;
    }

    void getInfo(String x[])
    {
        x[0] = "Gaussian (FIR)";
        x[1] = "Order: " + n;
    }
}
