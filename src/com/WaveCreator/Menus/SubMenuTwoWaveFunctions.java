package com.WaveCreator.Menus;

import com.WaveCreator.FrameManager;
import com.WaveCreator.Functions.FunctionsTwoWaves;
import com.WaveCreator.MethodInvoking.MethodComparator;
import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.JMenu;

/**
 * Menu class for wave combinations
 * User: Administrator
 * Date: 20.12.2008
 * Time: 14:02:20
 */
class SubMenuTwoWaveFunctions extends JMenu
{
    /**
     * Constructor for this JMenu
     * @param scopeWindow Ref. to correspondent frame
     */
    SubMenuTwoWaveFunctions(final ScopeWindow scopeWindow)
    {
        super("Two Wave Functions ...");

        Method[] methods = FunctionsTwoWaves.class.getMethods();
        Arrays.sort(methods, new MethodComparator());

        JMenu menu = this;
        int threshold = 0;

        for (final Method method : methods)
        {
            if (method.getDeclaringClass().equals(FunctionsTwoWaves.class))
            {
                menu.add (new ActionMenu (scopeWindow, method));
                // Make new submenu if this one gets too large
                threshold++;
                if (threshold == 20)
                {
                    threshold = 0;
                    JMenu m1 = new JMenu("more...");
                    menu.add(m1);
                    menu = m1;
                }
            }
        }
    }

    /**
     * Sub menu class that executes actions on two waves
     */
    class ActionMenu extends JMenu
    {
        /**
         * Constructor
         * @param sc Corresponding frame, containing primary Wave16 object
         * @param method The method to execute
         */
        ActionMenu(final ScopeWindow sc, final Method method)
        {
            /**
             * Sub menu text is the name of the algorithm
             */
            super (method.getName());
            /**
             * Get a list of all open frames
             */
            ScopeWindow[] frames = FrameManager.getInstance().getAllFrames();
            /**
             * Traverse thru all frames, adding an action entry that is the name of the Wave16 object
             */
            for (final ScopeWindow frame : frames)
            {
                add(new AbstractAction(frame.m_wave.name)
                {
                    /**
                     * User hits this menu entry
                     * @param e SWING supplied action information
                     */
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        try
                        {
                            /**
                             * Invoke the method of the 'twoWaveFunctions' section with one of the existing Wave16s
                             */
                            Wave16 w = (Wave16)method.invoke(sc.m_wave.functionsTwoWaves, frame.m_wave);
                            /**
                             * Set the name of new Wave16 object to the algorithm name
                             */
                            w.setName(method.getName());
                            /**
                             * Create a new frame that immediately shows the new object
                             */
                            FrameManager.getInstance().createFrame(w, "from TwoWaveFunktions");
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
                        {
                        }
                    }
                });
            }
        }
    }
}
