package com.WaveCreator;

import com.WaveCreator.Audio.DirectPlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Manages all sampling frames.
 * User: Administrator
 * Date: 20.12.2008
 * Time: 11:42:06
 */
public class FrameManager
{
    /**
     * List that holds all frames
     */
    private final ArrayList<ScopeWindow> m_list = new ArrayList<ScopeWindow>();

    private final DirectPlay m_directPlay = new DirectPlay();
    private ScopeWindow m_lastActive = null;

    private static FrameManager m_instance = null;

    /**
     * private Constructor (This is a singleton object)
     * Sets "look and feel"
     */
    private FrameManager()
    {
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e)
        {
            // Doesn't matter if that fails
        }
    }

    /**
     * Single method to get the one and only object
     * @return The frame manager
     */
    public static FrameManager getInstance()
    {
        if (m_instance == null)
            m_instance = new FrameManager();
        return m_instance;
    }

    /**
     * Starts audio output
     */
    public void startDirectPlay()
    {
        m_directPlay.stop();
        m_directPlay.start(m_lastActive.m_wave);
    }

    /**
     * Stops audio output
     */
    public void stopDirectPlay()
    {
        m_directPlay.stop();
    }

    /**
     * Listener class to handle window events
     */
    private class LocalWindowListener extends WindowAdapter
    {
        /**
         * The window is closing
         * --> Stop playin'
         * --> Remove it from the list
         * --> Dispose it
         * --> Exit the app if it's the last window
         * @param e The window event that causes the action
         */
        @Override
        public void windowClosing(WindowEvent e)
        {
            ScopeWindow sc = (ScopeWindow) e.getSource();
            // Last wave?
            if (m_list.size() == 1)
            {
                if (Dialogs.closeApplication(sc))
                {
                    System.exit(0);
                }
                return;
            }
            sc.m_player.stop();
            m_list.remove(sc);
            sc.dispose();
        }

        /**
         * Activates DirectPlay on the current active window
         * @param e Window event that causes the action
         */
        @Override
        public void windowActivated (WindowEvent e)
        {
            m_lastActive = (ScopeWindow)e.getSource();
            if (m_directPlay.isRunning())
                startDirectPlay();
        }
    }

    /**
     * Priavte function to set the position of a new Frame
     * @param c The newly created frame
     */
    private void setFramePosition(Component c)
    {
        if (m_list.size() == 0)
        {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = c.getSize();
            int x = (screenSize.width - frameSize.width) / 2;
            int y = (screenSize.height - frameSize.height) / 2;
            c.setLocation(x, y);
        }
        else
        {
            ScopeWindow last = m_list.get(m_list.size()-1);
            Point pt = last.getLocation();
            c.setLocation(pt.x+10, pt.y+10);
        }
    }

    /**
     * Creates a new ScopeScrollFrame
     * @param wav Wave16 object that is the conntent
     * @param arguments Arguments from Invoker
     * @return The newly created frame
     */
    public ScopeWindow createFrame(Wave16 wav, String arguments)
    {
        final int extent_x = 500;
        final int extent_y = 500;

        ScopeWindow frame = new ScopeWindow(wav, 1000, arguments);
        frame.setSize(extent_x, extent_y);
        setFramePosition(frame);

        frame.setTitle(wav.toString());

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        frame.addWindowListener(new LocalWindowListener());
        m_list.add(frame);
        m_lastActive = frame;
        return frame;
    }

    /**
     * Retrieves a list of all frames handled by this FrameManager
     * @return The list as array
     */
    public ScopeWindow[] getAllFrames()
    {
        return m_list.toArray(new ScopeWindow[m_list.size()]);
    }
}
