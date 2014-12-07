package com.WaveCreator.Menus;


import com.WaveCreator.DFilterAndFourierSeries.DFilterFrame;
import com.WaveCreator.DFilterAndFourierSeries.FourierFrame;
import static com.WaveCreator.Dialogs.closeApplication;
import static com.WaveCreator.Dialogs.loadWave;
import static com.WaveCreator.Dialogs.saveWave;
import static com.WaveCreator.Dialogs.waveInfo;
import static com.WaveCreator.FrameManager.getInstance;
import static com.WaveCreator.Monitor.MemoryMonitorFrame.go;
import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.exit;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

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
        getRuntime().gc();

        add(new AbstractAction("Info")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                waveInfo(scopeWindow);
            }
        });

        add(new AbstractAction("Monitor")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                go ();
            }
        });

        if (scopeWindow.hasScrollBar())
        {
            add(new AbstractAction("First Maximum")
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    scopeWindow.moveToPosition(scopeWindow.m_wave.maxIndex());
                }
            });

            add(new AbstractAction("First Minimum")
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    scopeWindow.moveToPosition(scopeWindow.m_wave.minIndex());
                }
            });

            if (scopeWindow.m_oscilloscope.m_markers.hasMarkers())
            {
                add(new AbstractAction("Go to Marker 1")
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        scopeWindow.moveToPosition(scopeWindow.m_oscilloscope.m_markers.m_marker_1);
                    }
                });

                add(new AbstractAction("Go to Marker 2")
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        scopeWindow.moveToPosition(scopeWindow.m_oscilloscope.m_markers.m_marker_2);
                    }
                });
            }
        }
        add(new AbstractAction("Toggle draw mode")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_oscilloscope.toggleDrawMode();
            }
        });

        add(new AbstractAction("Create drawable copy")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Wave16 w = scopeWindow.m_wave.copy();
                ScopeWindow sc = getInstance().createFrame(w, "drawable copy");
                sc.m_drawing_allowed = true;
            }
        });

        addSeparator();

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

        addSeparator();

        add(new AbstractAction("Play")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scopeWindow.m_player.play();
            }
        });

        add(new AbstractAction("DirectPlay")
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

        addSeparator();

        add(new AbstractAction("Save")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveWave(scopeWindow);
            }
        });

        add(new AbstractAction("Load")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                loadWave(scopeWindow);
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
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DFilterFrame ogf = new DFilterFrame(scopeWindow);
                ogf.init();
            }
        });

        add(new AbstractAction("Run Fourier Series Applet")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                FourierFrame ogf = new FourierFrame(scopeWindow);
                ogf.init();
            }
        });

        addSeparator();
        add(new AbstractAction("Exit")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (closeApplication(scopeWindow))
                {
                    exit(0);
                }
            }
        });

        show(scopeWindow.m_oscilloscope, ev.getX() + 2, ev.getY() + 2);
    }
}
