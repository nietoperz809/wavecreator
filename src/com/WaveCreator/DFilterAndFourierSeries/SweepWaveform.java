package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:32:35
*/
class SweepWaveform extends Waveform
{
    int ix;
    double omega, nextOmega, t, startOmega;

    public SweepWaveform(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start()
    {
        createBuffer();
        ix = 0;
        startOmega = nextOmega = omega = 2 * DFilterFrame.pi * 40 / dFilterFrame.sampleRate;
        t = 0;
        return true;
    }

    int getData()
    {
        int i;
        double nmul = 1;
        double nadd = 0;
        double maxspeed = 1 / (.66 * dFilterFrame.sampleRate);
        double minspeed = 1 / (dFilterFrame.sampleRate * 16);
        if (dFilterFrame.logFreqCheckItem.getState())
        {
            nmul = Math.pow(2 * DFilterFrame.pi / startOmega,
                            2 * (minspeed + (maxspeed - minspeed) * dFilterFrame.inputBar.getValue() / 1000.));
        }
        else
        {
            nadd = (2 * DFilterFrame.pi - startOmega) *
                   (minspeed + (maxspeed - minspeed) * dFilterFrame.inputBar.getValue() / 1000.);
        }
        for (i = 0; i != buffer.length; i++)
        {
            ix++;
            t += omega;
            if (t > 2 * DFilterFrame.pi)
            {
                t -= 2 * DFilterFrame.pi;
                omega = nextOmega;
                if (nextOmega > DFilterFrame.pi)
                {
                    omega = nextOmega = startOmega;
                }
            }
            buffer[i] = (short) (Math.sin(t) * 32000);
            nextOmega = nextOmega * nmul + nadd;
        }
        return buffer.length;
    }

    String getInputText()
    {
        return "Sweep Speed";
    }

    boolean needsFrequency()
    {
        return false;
    }
}
