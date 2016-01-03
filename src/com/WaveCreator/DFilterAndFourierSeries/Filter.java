package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:17:09
 */
public abstract class Filter
{
    final DFilterFrame dFilterFrame;

    public Filter (DFilterFrame dFilterFrame)
    {
        this.dFilterFrame = dFilterFrame;
    }

    abstract void evalTransfer (Complex c);

    abstract int getImpulseOffset ();

    abstract int getStepOffset ();

    abstract int getLength ();

    boolean useConvolve ()
    {
        return false;
    }

    double[] getImpulseResponse (int offset)
    {
        int pts = 1000;
        double inbuf[] = new double[offset + pts];
        double outbuf[] = new double[offset + pts];
        inbuf[offset] = 1;
        double state[] = createState();
        run(inbuf, outbuf, offset, ~0, pts, state);
        return outbuf;
    }

    double[] createState ()
    {
        return null;
    }

    abstract void run (double inBuf[], double outBuf[], int bp, int mask, int count, double x[]);

    double[] getStepResponse (int offset)
    {
        int pts = 1000;
        double inbuf[] = new double[offset + pts];
        double outbuf[] = new double[offset + pts];
        int i;
        for (i = offset; i != inbuf.length; i++)
        {
            inbuf[i] = 1;
        }
        double state[] = createState();
        run(inbuf, outbuf, offset, ~0, pts, state);
        return outbuf;
    }

    int getImpulseLen (int offset, double buf[])
    {
        return dFilterFrame.countPoints(buf, offset);
    }

    int getStepLen (int offset, double buf[])
    {
        return dFilterFrame.countPoints(buf, offset);
    }
}
