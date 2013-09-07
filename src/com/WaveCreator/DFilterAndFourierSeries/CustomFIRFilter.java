package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:08:40
*/
class CustomFIRFilter extends FIRFilterType
{
    CustomFIRFilter(DFilterFrame dFilterFrame)
    {
        super (dFilterFrame);
        if (dFilterFrame.uresp == null)
        {
            dFilterFrame.uresp = new double[1024];
        }
    }

    int select()
    {
        dFilterFrame.auxLabels[0].setText("Order");
        dFilterFrame.auxBars[0].setValue(120);
        dFilterFrame.auxBars[0].setMaximum(1600);
        int i;
        for (i = 0; i != 512; i++)
        {
            dFilterFrame.uresp[i] = 1.;
        }
        return 1;
    }

    void setup()
    {
    }

    double getUserResponse(double w)
    {
        double q = dFilterFrame.uresp[(int) (w * dFilterFrame.uresp.length / DFilterFrame.pi)];
        return q * q;
    }

    void edit(double x, double x2, double y)
    {
        int xi1 = (int) (x * dFilterFrame.uresp.length);
        int xi2 = (int) (x2 * dFilterFrame.uresp.length);
        for (; xi1 < xi2; xi1++)
        {
            if (xi1 >= 0 && xi1 < dFilterFrame.uresp.length)
            {
                dFilterFrame.uresp[xi1] = y;
            }
        }
    }

    Filter genFilter()
    {
        int n = dFilterFrame.auxBars[0].getValue();
        int nsz = dFilterFrame.uresp.length * 4;
        double fbuf[] = new double[nsz];
        int i;
        int nsz2 = nsz / 2;
        int nsz4 = nsz2 / 2;
        for (i = 0; i != nsz4; i++)
        {
            double ur = dFilterFrame.uresp[i] / nsz2;
            fbuf[i * 2] = ur;
            if (i > 0)
            {
                fbuf[nsz - i * 2] = ur;
            }
        }
        new FFT(nsz2).transform(fbuf, true);

        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[n];
        f.nList = new int[n];
        for (i = 0; i != n; i++)
        {
            int i2 = (i - n / 2) * 2;
            f.aList[i] = fbuf[i2 & (nsz - 1)] * getWindow(i, n);
            f.nList[i] = i;
        }
        setResponse(f);
        return f;
    }

    void getInfo(String x[])
    {
        int n = dFilterFrame.auxBars[0].getValue();
        x[0] = "Order: " + n;
    }

    boolean needsWindow()
    {
        return true;
    }
}
