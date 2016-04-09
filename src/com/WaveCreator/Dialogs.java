package com.WaveCreator;

import com.WaveCreator.IO.Wave16IO;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

/**
 * This class has static members to display dialogs.
 * User: Administrator
 * Date: 20.12.2008
 * Time: 20:39:17
 */
public class Dialogs
{
    /**
     * Warning box if application should exit
     * @param f Reference to parent component
     * @return  true if user clicked OK
     */
    public static boolean closeApplication(Component f)
    {
        Object[] options = {"OK", "NO! Please DON'T!!"};
        return JOptionPane.showOptionDialog(f,
                                            "All waves will be lost. Click OK if you really want that.",
                                            "Warning",
                                            JOptionPane.DEFAULT_OPTION,
                                            JOptionPane.WARNING_MESSAGE, null, options, options[1]
        ) == 0;
    }

    /**
     * Warning box if a single frame is about to be disposed
     * @param f Reference to parent component
     * @return  true if user clicked OK
     */
//    static boolean closeSingleFrame(Component f)
//    {
//        Object[] options = {"OK", "NO! NEVER!!"};
//        return JOptionPane.showOptionDialog(f,
//                                            "Dispose this frame and lose its wave?",
//                                            "Warning",
//                                            JOptionPane.DEFAULT_OPTION,
//                                            JOptionPane.WARNING_MESSAGE, null, options, options[1]
//        ) == 0;
//    }

    /**
     * Show the INFO dialog
     * @param owner Current active Scopewindow
     */
    public static void waveInfo (ScopeWindow owner)
    {
        JDialog dlg = new JDialog (owner, true);
        dlg.setLayout (new GridLayout(0,1));

        Wave16AmplitudeInfo amp = new Wave16AmplitudeInfo(owner.m_wave);

        dlg.add (new JLabel ("------------------ Wave ------------------"));
        dlg.add (new JTextArea("Name: "+ owner.m_wave.name));
        dlg.add (new JTextArea ("Samplingrate: "+ owner.m_wave.samplingRate));
        dlg.add (new JTextArea ("Size: "+ owner.m_wave.data.length));
        dlg.add (new JTextArea ("Min: "+ amp.min));
        dlg.add (new JTextArea ("Max: "+ amp.max));
        dlg.add (new JTextArea ("Span: "+ amp.span));

        if (owner.m_arguments != null)
        {
            dlg.add (new JLabel ("------------------ Generator aguments ------------------"));
            dlg.add (new JTextArea (owner.m_arguments));
        }

        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    /**
     *
     * @param scopeWindow
     */
    public static void loadWave (ScopeWindow scopeWindow)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setApproveButtonText("Get it!");
        String[] ext = {"wav", "ogg", "aif", "aiff"};
        chooser.setFileFilter(new FileNameExtensionFilter("Ogg or Wave files", ext));
        if (chooser.showOpenDialog(scopeWindow) == JFileChooser.APPROVE_OPTION)
        {
            String name = chooser.getSelectedFile().getAbsolutePath();
            try
            {
                Wave16 w = null;
                String nm = chooser.getSelectedFile().getName();
                if (nm.endsWith("wav"))
                {
                    w = Wave16IO.loadWave(name);
                }
                else if (nm.endsWith("aif"))
                {
                    w = Wave16IO.loadAiff(name);
                }
                else
                {
                    w = Wave16IO.loadOgg(name);
                }
                w.setName(chooser.getSelectedFile().getName());
                FrameManager.getInstance().createFrame(w, "loaded from file");
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }

    /**
     *
     * @param scopeWindow
     */
    public static void saveWave (ScopeWindow scopeWindow)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setApproveButtonText("Save now!");
        chooser.setFileFilter(new FileNameExtensionFilter("16Bit Wave files", "wav"));
        if (chooser.showSaveDialog(scopeWindow) == JFileChooser.APPROVE_OPTION)
        {
            String name = chooser.getSelectedFile().getAbsolutePath();
            try
            {
                Wave16IO.saveWave16(scopeWindow.m_wave, name);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
