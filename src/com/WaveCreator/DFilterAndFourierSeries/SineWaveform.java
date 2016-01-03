package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:32:13
 */
class SineWaveform extends Waveform
{
    int ix;

    public SineWaveform (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start ()
    {
        createBuffer();
        ix = 0;
        return true;
    }

    int getData ()
    {
        int i;
        for (i = 0; i != buffer.length; i++)
        {
            ix++;
            buffer[i] = (short) (Math.sin(ix * dFilterFrame.inputW) * 32000);
        }
        return buffer.length;
    }
}
