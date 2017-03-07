package com.WaveCreator.Audio;

import com.WaveCreator.Wave16;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

/**
 * Direct Player
 * User: Administrator
 * Date: 09.01.2009
 * Time: 20:36:22
 */
public class DirectPlay implements Runnable
{
    Wave16 m_wave = null;
    SourceDataLine m_dataline;
    final static int PLAYBUFFERSIZE = 65536;
    Thread m_thread;

    private boolean openLine()
    {
        try
        {
            AudioFormat playFormat = new AudioFormat(m_wave.samplingRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, playFormat);
            m_dataline = (SourceDataLine) AudioSystem.getLine(info);
            m_dataline.open(playFormat, PLAYBUFFERSIZE);
            m_dataline.start();
        }
        catch (LineUnavailableException e)
        {
            return false;
        }
        return true;
    }

    public DirectPlay (Wave16 wave)
    {
        m_wave = wave;
    }

    public DirectPlay()
    {

    }

    public void start()
    {
        openLine();
        if (isRunning())
            return;
        m_thread = new Thread(this);
        m_thread.start();
    }

    public void start (Wave16 wave)
    {
        m_wave = wave;
        start();
    }

    public boolean isRunning()
    {
        return m_thread != null;
    }

    public void stop()
    {
        if (isRunning())
        {
            m_dataline.flush();
            m_dataline.drain();
            m_dataline.close();
            m_thread = null;
        }
    }

    @Override
    public void run()
    {
        byte[] buffer = new byte[PLAYBUFFERSIZE];
        int waveidx = 0;

        while (m_thread != null)
        {
            int how_much = m_dataline.available();
            if (how_much <= 0)
                continue;
            if (how_much > buffer.length)
                how_much = buffer.length;
            for (int s=0; s<how_much; s+=2)
            {
                short q = (short)m_wave.data[waveidx];
                buffer[s+1] = (byte)(q>>8);
                buffer[s] = (byte)q;
                waveidx++;
                if (waveidx == m_wave.data.length)
                    waveidx = 0;
            }
            m_dataline.write(buffer, 0, how_much);
        }
    }
}
