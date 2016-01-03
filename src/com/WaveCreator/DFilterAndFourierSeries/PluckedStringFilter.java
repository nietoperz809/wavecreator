package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:18:23
 */
class PluckedStringFilter extends IIRFilterType
{
    int n;
    double mult;

    public PluckedStringFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Fundamental");
        dFilterFrame.auxBars[0].setValue(20);
        dFilterFrame.auxLabels[1].setText("Sharpness");
        dFilterFrame.auxBars[1].setValue(970);
        return 2;
    }

    void setup ()
    {
        n = 2000 / dFilterFrame.auxBars[0].getValue();
        mult = .5 * Math.exp(-.5 + dFilterFrame.auxBars[1].getValue() / 2000.);
    }

    void getInfo (String x[])
    {
        x[0] = "Plucked String (IIR); Resonance every " + dFilterFrame.getOmegaText(2 * DFilterFrame.pi / n);
        x[1] = "Delay: " + n + " samples, " +
                dFilterFrame.getUnitText(n / (double) dFilterFrame.sampleRate, "s");
    }

    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[]{1, 1, 0, 0};
        f.bList = new double[]{1, 0, -mult, -mult};
        f.nList = new int[]{0, 1, n, n + 1};
        setResponse(f);
        return f;
    }
}
