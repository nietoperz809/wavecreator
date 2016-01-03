package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:35:37
 */
class Wave16Waveform extends Waveform
{
    int idx;

    public Wave16Waveform (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start ()
    {
        try
        {
            dFilterFrame.sampleRate = dFilterFrame.m_sourceWindow.m_wave.samplingRate;
            createBuffer();
            idx = 0;
        }
        catch (Exception ix)
        {
            return false;
        }
        return true;
    }

    int getData ()
    {
        for (int s = 0; s < buffer.length; s++)
        {
            buffer[s] = (short) dFilterFrame.m_sourceWindow.m_wave.data[idx];
            idx++;
            if (idx == dFilterFrame.m_sourceWindow.m_wave.data.length)
            {
                idx = 0;
            }
        }
        return buffer.length;
    }

    boolean needsFrequency ()
    {
        return false;
    }
}
