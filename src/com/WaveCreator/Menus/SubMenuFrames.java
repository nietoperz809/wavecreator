package com.WaveCreator.Menus;

import com.WaveCreator.ScopeWindow;
import static com.WaveCreator.FrameManager.getInstance;

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

        ScopeWindow[] frames = getInstance().getAllFrames();

        for (int s=0; s<frames.length; s++)
        {
            final ScopeWindow f = frames[s];
            add(new AbstractAction("("+s+") "+f.m_wave.name)
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    f.toFront();
                }
            });
        }
    }
}
