package com.WaveCreator.Audio;

import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;

/**
 * This is the asychronuous clip player class
 */
public class ClipPlayer
{
    /**
     * Main frame reference
     */
    private final ScopeWindow m_scopeFrame;
    /**
     * Used as 'playing' flag
     * Don't need synchronisation here 'cause player thread
     * only checks if that reference is null
     */
    private PlayerThread m_playerThread = null;
    /**
     * Clip object to be played
     */
    private Clip m_clip;

    /**
     * Plays a data file as 16-bits WAV
     * @param dat Sampling data
     * @param framePos Position of first sample that we hear
     * @return the clip that is now playing
     */
    Clip playClip (Wave16 dat, int framePos)
    {
        byte[] data = new byte[dat.data.length * 2];
        for (int s = 0; s < dat.data.length; s++)
        {
            data[s * 2 + 1] = (byte) (dat.getShortAt(s) >>> 8);
            data[s * 2] = (byte) (dat.getShortAt(s) & 0xff);
        }
        ByteArrayInputStream ba = new ByteArrayInputStream(data);
        AudioFormat frm = new AudioFormat(dat.samplingRate, 16, 1, true, false);
        AudioInputStream ais = new AudioInputStream(ba, frm, data.length);

        Clip clip = null;
        try
        {
            clip = AudioSystem.getClip();
            clip.open(ais);
        }
        catch (LineUnavailableException | IOException e)
        {
            e.printStackTrace();
            return null;
        }
        clip.setFramePosition (framePos);
        clip.start();
        return clip;
    }

    /**
     * Stops a running clip
     * Waits until clip is really stopped
     * @param clip Clip to be stopped
     */
    void stopClip (Clip clip)
    {
        clip.stop();
        do
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        while (clip.isRunning());
        clip.close();
    }

    /**
     * This is the heart of the player
     */
    private class PlayerThread extends Thread
    {
        /**
         * Thread function
         */
        @Override
        public void run()
        {
            /**
             * First, Run the clip from actual position
             */
            m_clip = playClip (m_scopeFrame.m_wave, m_scopeFrame.getFirstSamplePosition());

            /**
             * Enter monitoring and updating loop
             */
            while (true)
            {
                /**
                 * A small delay so that we don't consume too much time
                 */
                try
                {
                    sleep (10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace(); // FUCK!!
                }

                /**
                 * Move the scrollbar that shifts also the scope view
                 */
                m_scopeFrame.updateView(m_clip.getFramePosition());

                /**
                 * Determine when to stop playing
                 * Either if clip ended or we were stopped
                 */
                if (m_playerThread == null || m_clip.getFrameLength() == m_clip.getFramePosition())
                {
                    stopClip(m_clip);
                    break;
                }
            }
        }
    }

    /**
     * Constructor
     * @param sc Ref. to main frame object
     */
    public ClipPlayer (ScopeWindow sc)
    {
        m_scopeFrame = sc;
    }

    /**
     * Start playin'
     */
    public void play()
    {
        /**
         * Stop currently running clip if there is one
         */
         stop();
        /**
         * Start a new player job
         */
        m_playerThread = new PlayerThread();
        m_playerThread.start();
    }

    /**
     * Stop playing
     */
    public void stop()
    {
        if (!isPlaying())
            return;
        /**
         * Get the player thread 'cause we must overwrite the ref flag
         */
        PlayerThread w = m_playerThread;
        m_playerThread = null;
        /**
         * Wait for playing thread to finish
         */
        try
        {
            w.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        /**
         * Give GC a chance to throw away our clip
         */
        m_clip = null;
    }

    /**
     * Just return clip status
     * @return true if clip is active
     */
    boolean isPlaying()
    {
        return m_playerThread != null;
    }

    /**
     * Convenience function for clients to move the frame position
     * @param v new frame pos.
     */
    public void setFramePosition (int v)
    {
        /**
         * Move frame only if playing
         */
        if (isPlaying())
        {
            if (m_clip != null)
                m_clip.setFramePosition(v);
        }
    }
}
