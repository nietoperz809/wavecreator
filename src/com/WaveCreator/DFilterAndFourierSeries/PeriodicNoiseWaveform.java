package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:27:27
*/
class PeriodicNoiseWaveform extends Waveform
{
    short smbuf[];
    int ix;

    public PeriodicNoiseWaveform(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start()
    {
        createBuffer();
        smbuf = new short[1];
        ix = 0;
        return true;
    }

    int getData()
    {
        int period = (int) (2 * DFilterFrame.pi / dFilterFrame.inputW);
        if (period != smbuf.length)
        {
            smbuf = new short[period];
            int i;
            for (i = 0; i != period; i++)
            {
                smbuf[i] = (short) dFilterFrame.random.nextInt();
            }
        }
        int i;
        for (i = 0; i != buffer.length; i++, ix++)
        {
            if (ix >= period)
            {
                ix = 0;
            }
            buffer[i] = smbuf[ix];
        }
        return buffer.length;
    }
}
