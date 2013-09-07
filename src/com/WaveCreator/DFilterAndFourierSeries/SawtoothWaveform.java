package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:31:51
*/
class SawtoothWaveform extends Waveform
{
    int ix;
    short smbuf[];

    public SawtoothWaveform(DFilterFrame dFilterFrame)
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
            for (i = 0; i != period; i++)
            {
                smbuf[i] = (short) ((i / p2 - 1) * 32000);
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
