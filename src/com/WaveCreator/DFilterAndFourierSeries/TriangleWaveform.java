package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:32:47
*/
class TriangleWaveform extends Waveform
{
    int ix;
    short smbuf[];

    public TriangleWaveform(DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    boolean start()
    {
        createBuffer();
        ix = 0;
        smbuf = new short[1];
        return true;
    }

    int getData()
    {
        int i;
        int period = (int) (2 * DFilterFrame.pi / dFilterFrame.inputW);
        if (period != smbuf.length)
        {
            smbuf = new short[period];
            double p2 = period / 2.;
            for (i = 0; i < p2; i++)
            {
                smbuf[i] = (short) (i / p2 * 64000 - 32000);
            }
            for (; i != period; i++)
            {
                smbuf[i] = (short) ((2 - i / p2) * 64000 - 32000);
            }
        }
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
