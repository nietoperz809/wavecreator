package com.WaveCreator.Menus;

import com.WaveCreator.FrameManager;
import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;
import com.WaveCreator.WaveGroup;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.WaveCreator.Helpers.Tools.listPackage;


/**
 * Created by Administrator on 4/9/2016.
 */
public class SubMenuWaveGroup extends JMenu
{
    /**
     * Constructor for this JMenu
     *
     * @param scopeWindow Ref. to correspondent frame
     */
    SubMenuWaveGroup (final ScopeWindow scopeWindow)
    {
        super("Load Wavegroup ...");

        String[] p = listPackage("sounds", true);
        for (String s : p)
        {
            add(new AbstractAction(s.replace("/",""))
            {
                @Override
                public void actionPerformed (ActionEvent e)
                {
                    try
                    {
                        WaveGroup wv = new WaveGroup(e.getActionCommand());
                        Wave16[] waves = wv.getWaves();
                        for (Wave16 w : waves)
                        {
                            FrameManager.getInstance().createFrame(w, w.name);
                        }
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });
        }
    }
}
