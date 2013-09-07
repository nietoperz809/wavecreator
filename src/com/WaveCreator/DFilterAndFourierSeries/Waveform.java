package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:26:37
*/
public abstract class Waveform
{
    short buffer[];
    public final DFilterFrame dFilterFrame;

    public Waveform(DFilterFrame dFilterFrame)
    {
        this.dFilterFrame = dFilterFrame;
    }

    boolean start()
    {
        return true;
    }

    abstract int getData();

    void createBuffer()
    {
        buffer = new short[dFilterFrame.getPower2(dFilterFrame.sampleRate / 12)];
    }

    String getInputText()
    {
        return "Input Frequency";
    }

    boolean needsFrequency()
    {
        return true;
    }
}
