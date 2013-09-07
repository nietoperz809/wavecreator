package com.WaveCreator.Menus;

import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;
import com.WaveCreator.FrameManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * New Class.
 * User: Administrator
 * Date: 01.01.2009
 * Time: 19:48:16
 */
class SubMenuMarkerFunctions extends JMenu
{
    SubMenuMarkerFunctions (final ScopeWindow scopeWindow)
    {
        super ("Marker functions ...");

        add(new AbstractAction("Extract")
        {
            public void actionPerformed(ActionEvent e)
            {
                int[] mark = scopeWindow.m_oscilloscope.m_markers.get();
                Wave16 w = scopeWindow.m_wave.functionsDeletions.extractSamples(mark[0], mark[1]);
                FrameManager.getInstance().createFrame(w, "Marker extracted");
            }
        });

        add(new AbstractAction("Delete")
        {
            public void actionPerformed(ActionEvent e)
            {
                int[] mark = scopeWindow.m_oscilloscope.m_markers.get();
                Wave16 left = scopeWindow.m_wave.functionsDeletions.extractSamples(0, mark[0]);
                Wave16 right = scopeWindow.m_wave.functionsDeletions.extractSamples(mark[1], scopeWindow.m_wave.data.length);
                Wave16[] arr = {left, right};
                Wave16 w = scopeWindow.m_wave.combineAppend(arr);
                FrameManager.getInstance().createFrame(w, "Marker deleted");
            }
        });

    }
}
