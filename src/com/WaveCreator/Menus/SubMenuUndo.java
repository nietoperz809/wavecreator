package com.WaveCreator.Menus;

import com.WaveCreator.FrameManager;
import com.WaveCreator.UndeleteManager;
import com.WaveCreator.Wave16;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * New Class.
 * User: Administrator
 * Date: 13.03.2009
 * Time: 00:02:01
 */
public class SubMenuUndo extends JMenu
{
    SubMenuUndo()
    {
        super ("Restore wave...");
        Wave16[] waves = UndeleteManager.getInstance().getAllWaves();
        for (final Wave16 w : waves)
        {
            add(new AbstractAction(w.name)
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    FrameManager.getInstance().createFrame(w, "Restored");
                    UndeleteManager.getInstance().remove(w);
                }
            });
        }
    }
}
