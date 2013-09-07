package com.WaveCreator.Menus;


import com.WaveCreator.DFilterAndFourierSeries.DFilterFrame;
import com.WaveCreator.DFilterAndFourierSeries.FourierFrame;
import com.WaveCreator.Dialogs;
import com.WaveCreator.Monitor.MemoryMonitorFrame;
import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;
import com.WaveCreator.FrameManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Class that implements the main context menu
 */
public class MainMenu extends JPopupMenu
{
    /**
     * Constructor
     * @param scopeWindow Frame associated with this menu
     * @param ev          Mouse event that causes this menu to display
     */
    public MainMenu(final ScopeWindow scopeWindow, MouseEvent ev)
    {
        Runtime.getRuntime().gc();

        add(new AbstractAction("Info")
        {
            public void actionPerformed(ActionEvent e)
            {
                Dialogs.waveInfo(scopeWindow);
            }
        });

        add(new AbstractAction("Monitor")
        {
            public void actionPerformed(ActionEvent e)
            {
                MemoryMonitorFrame.go ();
            }
        });

        if (scopeWindow.hasScrollBar())
        {
            add(new AbstractAction("First Maximum")
            {
                public void actionPerformed(ActionEvent e)
                {
                    scopeWindow.moveToPosition(scopeWindow.m_wave.maxIndex());
                }
            });

            add(new AbstractAction("First Minimum")
            {
                public void actionPerformed(ActionEvent e)
                {
                    scopeWindow.moveToPosition(scopeWindow.m_wave.minIndex());
                }
            });

            if (scopeWindow.m_oscilloscope.m_markers.hasMarkers())
            {
                add(new AbstractAction("Go to Marker 1")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        scopeWindow.moveToPosition(scopeWindow.m_oscilloscope.m_markers.m_marker_1);
                    }
                });

                add(new AbstractAction("Go to Marker 2")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        scopeWindow.moveToPosition(scopeWindow.m_oscilloscope.m_markers.m_marker_2);
                    }
                });
            }
        }
        add(new AbstractAction("Toggle draw mode")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_oscilloscope.toggleDrawMode();
            }
        });

        add(new AbstractAction("Create drawable copy")
        {
            public void actionPerformed(ActionEvent e)
            {
                Wave16 w = scopeWindow.m_wave.copy();
                ScopeWindow sc = FrameManager.getInstance().createFrame(w, "drawable copy");
                sc.m_drawing_allowed = true;
            }
        });

        addSeparator();

        add(new AbstractAction("Zoom in")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(scopeWindow.m_showWindow / 2);
            }
        });

        add(new AbstractAction("Zoom out")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(scopeWindow.m_showWindow * 2);
            }
        });

        add(new AbstractAction("Maximal zoom")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(0);
            }
        });

        add(new AbstractAction("Minimal zoom")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.changeShowWindow(Integer.MAX_VALUE);
            }
        });

        addSeparator();

        add(new AbstractAction("Play")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.play();
            }
        });

        add(new AbstractAction("DirectPlay")
        {
            public void actionPerformed(ActionEvent e)
            {
                FrameManager.getInstance().startDirectPlay();
            }
        });

        add(new AbstractAction("Stop")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.stop();
                FrameManager.getInstance().stopDirectPlay();
            }
        });

        add(new AbstractAction("Rewind")
        {
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.setFramePosition(0);
                scopeWindow.updateView(0);
            }
        });

        addSeparator();

        add(new AbstractAction("Save")
        {
            public void actionPerformed(ActionEvent e)
            {
                Dialogs.saveWave(scopeWindow);
            }
        });

        add(new AbstractAction("Load")
        {
            public void actionPerformed(ActionEvent e)
            {
                Dialogs.loadWave(scopeWindow);
            }
        });

        addSeparator();
        Wave16 w;
        if (scopeWindow.m_oscilloscope.m_markers.hasMarkers())
        {
            add(new SubMenuMarkerFunctions(scopeWindow));
            int[] mark = scopeWindow.m_oscilloscope.m_markers.get();
            w = scopeWindow.m_wave.functionsDeletions.extractSamples(mark[0], mark[1]);
        }
        else
        {
            w = scopeWindow.m_wave;
        }
        add(new SubMenuFunctions(scopeWindow, w.functionsGenerators, "Generator Functions"));
        add(new SubMenuFunctions(scopeWindow, w.functionsFFT, "FFT Functions"));
        add(new SubMenuFunctions(scopeWindow, w.functionsDeletions, "Sample deleting Functions"));
        add(new SubMenuFunctions(scopeWindow, w.functionsBinary, "Binary Functions"));
        add(new SubMenuFunctions(scopeWindow, w.functionsMathematical, "Math Functions"));
        add(new SubMenuFunctions(scopeWindow, w.functionsFilters, "Filters"));
        add(new SubMenuFunctions(scopeWindow, w.functionsAmplitude, "Amplitude"));
        add(new SubMenuFunctions(scopeWindow, w.functionsLength, "Length"));
        add(new SubMenuFunctions(scopeWindow, w.functionsSpecialEffects, "Special Effects"));
        add(new SubMenuFunctions(scopeWindow, w.functionsReorder, "Reordering"));
        add(new SubMenuFunctions(scopeWindow, w.functionsTesting, "Test Candidates"));

        add(new SubMenuTwoWaveFunctions(scopeWindow));

        addSeparator();
        add (new SubMenuFrames());
        add (new SubMenuUndo());

        addSeparator();
        add(new AbstractAction("Run DFilter Applet")
        {
            public void actionPerformed(ActionEvent e)
            {
                DFilterFrame ogf = new DFilterFrame(scopeWindow);
                ogf.init();
            }
        });

        add(new AbstractAction("Run Fourier Series Applet")
        {
            public void actionPerformed(ActionEvent e)
            {
                FourierFrame ogf = new FourierFrame(scopeWindow);
                ogf.init();
            }
        });

        addSeparator();
        add(new AbstractAction("Exit")
        {
            public void actionPerformed(ActionEvent e)
            {
                if (Dialogs.closeApplication(scopeWindow))
                {
                    System.exit(0);
                }
            }
        });

        show(scopeWindow.m_oscilloscope, ev.getX() + 2, ev.getY() + 2);
    }
}
