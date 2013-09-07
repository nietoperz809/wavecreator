package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:09:05
*/
class DelayFilter extends CombFilter
{
    DelayFilter(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame, 1);
        this.dFilterFrame = dFilterFrame;
    }

    void getResponse(double w, Complex c)
    {
        if (n > 212)
        {
            c.set(1);
        }
        else
        {
            super.getResponse(w, c);
        }
    }

    void setCutoff(double f)
    {
    }

    int select()
    {
        dFilterFrame.auxLabels[0].setText("Delay");
        dFilterFrame.auxBars[0].setValue(300);
        dFilterFrame.auxLabels[1].setText("Strength");
        dFilterFrame.auxBars[1].setValue(700);
        return 2;
    }

    void setup()
    {
        n = dFilterFrame.auxBars[0].getValue() * 16384 / 1000;
        mult = dFilterFrame.auxBars[1].getValue() / 1250.;
        peak = 1 / (1 - mult);
    }

    void getInfo(String x[])
    {
        x[0] = "Delay (IIR)";
        x[1] = "Delay: " + n + " samples, " +
               dFilterFrame.getUnitText(n / (double) dFilterFrame.sampleRate, "s");
        double tl = 340. * n / dFilterFrame.sampleRate / 2;
        x[2] = "Echo Distance: " + dFilterFrame.getUnitText(tl, "m");
        if (tl > 1)
        {
            x[2] += " (" + dFilterFrame.showFormat.format(tl * 3.28084) + " ft)";
        }
    }
}
