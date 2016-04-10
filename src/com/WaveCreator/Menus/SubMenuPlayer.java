package com.WaveCreator.Menus;

import com.WaveCreator.ScopeWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.WaveCreator.FrameManager.getInstance;

/**
 * Created by Administrator on 4/9/2016.
 */
public class SubMenuPlayer extends JMenu
{
    SubMenuPlayer (final ScopeWindow scopeWindow)
    {
        super ("Play ...");

        add(new AbstractAction("Play")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.play();
            }
        });

        add(new AbstractAction("Play repeatedly")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                getInstance().startDirectPlay();
            }
        });

        add(new AbstractAction("Stop")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.stop();
                getInstance().stopDirectPlay();
            }
        });

        add(new AbstractAction("Rewind")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.setFramePosition(0);
                scopeWindow.updateView(0);
            }
        });

    }

}
