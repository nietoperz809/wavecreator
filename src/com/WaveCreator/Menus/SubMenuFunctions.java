package com.WaveCreator.Menus;

import com.WaveCreator.Functions.Functions;
import com.WaveCreator.ScopeWindow;
import com.WaveCreator.MethodInvoking.MethodComparator;
import com.WaveCreator.MethodInvoking.MethodInvoker;
import com.WaveCreator.Wave16;
import com.WaveCreator.FrameManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Menu class for single-wave results
 */
class SubMenuFunctions extends JMenu
{
    /**
     * Constructor for this JMenu
     * @param scopeWindow Ref. to main frame
     * @param functions Object that contains the method to be called
     * @param menuName Name of submenu entry
     */
    SubMenuFunctions(final ScopeWindow scopeWindow, final Functions functions, final String menuName)
    {
        super(menuName+" ...");

        // Get all the FunctionsSpecialEffects methods and sortAscending them alphabetically
        Method[] methods = functions.getClass().getMethods();
        Arrays.sort(methods, new MethodComparator());

        // Some running variables
        JMenu currentMenu = this;
        int menuEntryCounter = 0;
        String firstThree = "";

        // Build the menu entries
        for (final Method method : methods)
        {
            // Method must not be derived
            if (method.getDeclaringClass().equals(functions.getClass()))
            {
                // Get name of the current method
                final String name = method.getName();

                // Make new submenu if this one gets too large
                if ((menuEntryCounter++ > 19) && !(firstThree.equals(name.substring(0,3))))
                {
                    menuEntryCounter = 0;
                    JMenu newMenu = new JMenu("more...");
                    currentMenu.add(newMenu);
                    currentMenu = newMenu;
                }
                firstThree = name.substring(0,3);

                // Add a menu entry
                currentMenu.add(new AbstractAction(name + MethodInvoker.getMethodParameterDescriptions(method))
                {
                    // User clicked on menu entry
                    public void actionPerformed(ActionEvent e)
                    {
                        // Invoke the method and get the result
                        // A dialog may ask for some arguments
                        MethodInvoker mi = new MethodInvoker(scopeWindow, functions, method);
                        Object res = mi.getResult().resultobject;

                        // Result is a single Wave
                        if (res instanceof Wave16)
                        {
                            Wave16 w = (Wave16)res;

                            // Get left and right from original if we have markers
                            if (scopeWindow.m_oscilloscope.m_markers.hasMarkers())
                            {
                                int[] mark = scopeWindow.m_oscilloscope.m_markers.get();
                                Wave16 left = scopeWindow.m_wave.functionsDeletions.extractSamples(0, mark[0]);
                                Wave16 right = scopeWindow.m_wave.functionsDeletions.extractSamples(mark[1], scopeWindow.m_wave.data.length);
                                Wave16[] arr = {left, w, right};
                                w = scopeWindow.m_wave.combineAppend(arr);
                            }

                            w.setName(scopeWindow.m_wave.name+"->"+name);
                            FrameManager.getInstance().createFrame(w, mi.getResult().parameters);
                        }

                        // Result is an array of waves
                        else if (res instanceof Wave16[])
                        {
                            Wave16[] w = (Wave16[])res;
                            for (int s=0; s<w.length; s++)
                            {
                                w[s].setName(scopeWindow.m_wave.name+"->"+name+"("+s+")");
                                FrameManager.getInstance().createFrame(w[s], mi.getResult().parameters);
                            }
                        }
                    }
                });
            }
        }
    }
}
