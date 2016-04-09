package com.WaveCreator.Menus;

import com.WaveCreator.ScopeWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static java.lang.Integer.MAX_VALUE;

/**
 * Created by Administrator on 4/9/2016.
 */
public class SubMenuZoom extends JMenu
{
    SubMenuZoom (final ScopeWindow scopeWindow)
    {
        super("Zoom ...");
        add(new AbstractAction("Zoom in")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(scopeWindow.m_showWindow / 2);
            }
        });

        add(new AbstractAction("Zoom out")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(scopeWindow.m_showWindow * 2);
            }
        });

        add(new AbstractAction("Maximal zoom")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(0);
            }
        });

        add(new AbstractAction("Minimal zoom")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(MAX_VALUE);
            }
        });
    }

}
