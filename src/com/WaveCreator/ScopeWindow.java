package com.WaveCreator;

import com.WaveCreator.Audio.ClipPlayer;
import com.WaveCreator.Oscilloscope.Oscilloscope;
import com.WaveCreator.Oscilloscope.ScopeMouseAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class ScopeWindow extends JFrame implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1482712301983074195L;
	/**
     * Oscilloscope object
     */
    public Oscilloscope m_oscilloscope;
    /**
     * Amount of wave to be shown
     */
    public int m_showWindow;
    /**
     * Scrollbar of this window
     */
    private JScrollBar m_scrollbar = null;
    /**
     * Panel to be used to display the oscilloscope
     */
    private JPanel m_scopePanel;
    /**
     * Central wave object
     */
    public Wave16 m_wave = null;
    /**
     * Wave player object
     */
    public ClipPlayer m_player = null;
    /**
     * Generator aguments (if available)
     */
    final String m_arguments;

    public boolean m_drawing_allowed = false;

    /**
     * Dispose this frame and "nullify" all its objects
     * Hope this helps the GC to free the memory
     */
    @Override
    public void dispose()
    {
        UndeleteManager.getInstance().add(m_wave);
        super.dispose();
        m_oscilloscope = null;
        m_scrollbar = null;
        m_scopePanel = null;
        m_wave = null;
        m_player = null;
        System.runFinalization();
        System.gc();
    }

    /**
     * Local scroll bar handler
     */
    private class ScrollBarListener implements AdjustmentListener
    {
        /**
         * Scrollbar has changed value
         * @param e Scrollbar change event
         */
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e)
        {
            /**
             * Draw the scope
             */
            m_oscilloscope.drawNow (e.getValue(), m_showWindow -1);
            /**
             * Manual change?
             * update clip player position
             */
            if (e.getValueIsAdjusting())
            {
                 m_player.setFramePosition(e.getValue());
            }
        }
    }

    /**
     * Constructs a scrollbar if neccessary
     * @param wave Central Wave16 object
     * @param showwindow Amount of samples to be shown
     * @return A Scrollbar or null, if we don't need one
     */
    private JScrollBar makeScrollBar (Wave16 wave, int showwindow)
    {
        /**
         * No scrollbar?
         */
        if (wave.data.length <= showwindow)
        {
            return null;
        }

        /**
         * Need a scrollbar
         */
        JScrollBar scr = new JScrollBar(JScrollBar.HORIZONTAL);
        scr.setMinimum (0);
        scr.setMaximum (wave.data.length- m_showWindow + scr.getVisibleAmount());
        scr.addAdjustmentListener (new ScrollBarListener());
        return scr;
    }

    /**
     * Builds the inner frame of this frame
     * @param wave Central Wave16 object
     * @param showwindow Amount of samples to be shown
     */
    private void makeFrameContent(Wave16 wave, int showwindow)
    {
        m_wave = wave;
        m_oscilloscope = new Oscilloscope(wave);
        m_player = new ClipPlayer(this);
        
        /**
         * Class to implement the borders
         */
        final class Spacer extends JPanel
        {
            Spacer()
            {
                super();
                setPreferredSize(new Dimension(2, 2));
                setOpaque(true);
                setBackground(Color.ORANGE);
            }
        }

        JPanel scopePanel = new JPanel();
        scopePanel.setLayout(new BorderLayout());
        scopePanel.add(BorderLayout.WEST, new Spacer());
        scopePanel.add(BorderLayout.EAST, new Spacer());
        scopePanel.add(BorderLayout.NORTH, new Spacer());
        scopePanel.add(BorderLayout.SOUTH, new Spacer());
        scopePanel.add(BorderLayout.CENTER, m_oscilloscope);
        m_scopePanel = scopePanel;
        add (BorderLayout.CENTER, m_scopePanel);

        addKeyListener (new KeyAdapter()  // Key events
        {
            @Override
            public void keyTyped (KeyEvent e)
            {
                switch (e.getKeyChar())
                {
                    case 'p':
                        m_player.play();
                        break;
                    case 's':
                        m_player.stop();
                        break;
                    case 'i':
                        Dialogs.waveInfo(ScopeWindow.this);
                        break;
                }
            }
        });

        JScrollBar scr = makeScrollBar (wave, showwindow);

        if (scr != null)
        {
            m_showWindow = showwindow;
            m_scrollbar = scr;
            add (BorderLayout.SOUTH, scr);
        }
        else
        {
            m_showWindow = wave.data.length;
        }

        /**
         * Show scope immediately on construction
         */
        m_oscilloscope.drawNow (0, m_showWindow);
    }

    /**
     * Constructor
     * But does not show the window (client must call setSize and setLocation)
     * @param wave Wave16 object to be shown
     * @param showwindow window (amount) to be shown
     * @param arguments Arguments from Invoker
     */
    public ScopeWindow(Wave16 wave, int showwindow, String arguments)
    {
        super();
        m_arguments = arguments;
        makeFrameContent(wave, showwindow);
        new ScopeMouseAdapter(this);
    }

    /**
     * Changes how many samples are shown in oscilloscope
     * @param value Number of samples to be shown
     */
    public void changeShowWindow (int value)
    {
        if (hasScrollBar())
        {
            /**
             * Adjust input if it is too big or too small
             */
            if (value < 10)
                value = 10;
            else if (value > 100000)
                value = 100000;
            /**
             * The show window must not exceed number of samples
             */
            if (value > m_wave.data.length)
                value = m_wave.data.length;

            m_showWindow = value;
            m_scrollbar.setMaximum (m_wave.data.length- m_showWindow + m_scrollbar.getVisibleAmount());
            m_oscilloscope.drawNow (m_scrollbar.getValue(), m_showWindow);
        }
    }

    /**
     * Returns true if this object has a JScrollBar
     * @return true or false
     */
    public boolean hasScrollBar()
    {
        return m_scrollbar != null;
    }

    /**
     *
     * @param x
     */
    public void moveToPosition (int x)
    {
        if (x < 0)
            return;
        if (hasScrollBar())
            m_scrollbar.setValue (x);
    }

    /**
     * Returns the sample that is currently the first sample (ScrollBar dependent)
     * @return The sample pos
     */
    public int getFirstSamplePosition()
    {
        if (hasScrollBar())
            return m_scrollbar.getValue();
        else
            return 0;
    }

    /**
     * Updates the view using scrollbar events
     * @param v New value that is the starting value of view
     */
    public void updateView (int v)
    {
        if (hasScrollBar())
            m_scrollbar.setValue (v);
    }
}
