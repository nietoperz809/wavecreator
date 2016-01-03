package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:27:15
 */
class NoiseWaveform extends Waveform
{
    public NoiseWaveform (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start ()
    {
        createBuffer();
        return true;
    }

    int getData ()
    {
        int i;
        for (i = 0; i != buffer.length; i++)
        {
            buffer[i] = (short) dFilterFrame.random.nextInt();
        }
        return buffer.length;
    }

    String getInputText ()
    {
        return null;
    }

    boolean needsFrequency ()
    {
        return false;
    }
}
