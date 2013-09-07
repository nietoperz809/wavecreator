package com.WaveCreator.Menus;

import com.WaveCreator.ScopeWindow;
import com.WaveCreator.FrameManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * New Class.
 * User: Administrator
 * Date: 20.12.2008
 * Time: 12:39:34
 */
class SubMenuFrames extends JMenu
{
    SubMenuFrames()
    {
        super("Go to frame ...");

        ScopeWindow[] frames = FrameManager.getInstance().getAllFrames();

        for (int s=0; s<frames.length; s++)
        {
            final ScopeWindow f = frames[s];
            add(new AbstractAction("("+s+") "+f.m_wave.name)
            {
                public void actionPerformed(ActionEvent e)
                {
                    f.toFront();
                }
            });
        }
    }
}
