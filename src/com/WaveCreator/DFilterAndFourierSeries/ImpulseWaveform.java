package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:27:04
*/
public class ImpulseWaveform extends Waveform
{
    int ix;

    public ImpulseWaveform(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start()
    {
        createBuffer();
        ix = 0;
        return true;
    }

    int getData()
    {
        int i;
        int ww = dFilterFrame.inputBar.getValue() / 51 + 1;
        int period = 10000 / ww;
        for (i = 0; i != buffer.length; i++)
        {
            short q = 0;
            if (ix % period == 0)
            {
                q = 32767;
            }
            ix++;
            buffer[i] = q;
        }
        return buffer.length;
    }

    String getInputText()
    {
        return "Impulse Frequency";
    }

    boolean needsFrequency()
    {
        return false;
    }
}
