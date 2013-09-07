package com.WaveCreator.Monitor;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;

/**
 * New Class.
 * User: Administrator
 * Date: 05.01.2009
 * Time: 22:46:57
 */
public class MemoryMonitorFrame extends JFrame
{
    private class LocaWindowListener extends WindowAdapter
    {
        /**
         * The window is closing
         * @param e The window event that causes the action
         */
        @Override
        public void windowClosing(WindowEvent e)
        {
            mon.surf.stop();
            ((MemoryMonitorFrame)e.getSource()).dispose();
            m_instance = null;
        }
    }

    MemoryMonitor mon;
    static MemoryMonitorFrame m_instance = null;

    private MemoryMonitorFrame()
    {
        super();
        if (m_instance != null)
        {
            dispose();
            return;
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new LocaWindowListener());
        mon = new MemoryMonitor();
        add (mon);
        setSize (600,200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        setTitle ("Memory Monitor");
        setLocation(x, y);
        setVisible (true);
        mon.surf.start();
        m_instance = this;
    }

    public static MemoryMonitorFrame go()
    {
        new MemoryMonitorFrame();
        m_instance.toFront();
        return m_instance;
    }
}
