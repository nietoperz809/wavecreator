package com.WaveCreator.Oscilloscope;

import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Menus.MainMenu;

import javax.swing.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: Administrator
* Date: 09.12.2008
* Time: 00:47:20
* To change this template use File | Settings | File Templates.
*/
public class ScopeMouseAdapter implements MouseMotionListener, MouseListener
{
    private final JWindow m_labelWindow;
    private final JLabel m_label;
    private double m_xstep;
    private final ScopeWindow m_scopeWindow;
    /**
     * Current, mouse-related x-position in Wave16 object
     */
    private int m_xp;

    private void calcXP (MouseEvent e)
    {
        m_xp = (int) (0.5 + (float) e.getX() / m_xstep) + m_scopeWindow.m_oscilloscope.m_offset;
    }

    private void showPositionLabel (MouseEvent e)
    {
        String sb = " X:" + m_xp + " Y:" + m_scopeWindow.m_oscilloscope.m_points[m_xp] + " ";
        m_label.setText(sb);

        Dimension labeldimension = m_label.getPreferredSize();
        m_labelWindow.setLocation(e.getXOnScreen() - labeldimension.width / 2,
                           e.getYOnScreen() - labeldimension.height - 2);
        m_labelWindow.setSize(labeldimension);
    }

    /**
     * Constructor
     * @param sc JavaScope to use
     */
    public ScopeMouseAdapter (ScopeWindow sc)
    {
        m_scopeWindow = sc;
        sc.m_oscilloscope.addMouseMotionListener(this);
        sc.m_oscilloscope.addMouseListener(this);
        m_labelWindow = new JWindow((Window) null);
        m_label = new JLabel();
        m_labelWindow.getContentPane().add(m_label);
    }

    /**
     * Enables drawing or setting of markers
     * @param e The current mouse event
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        // Button 1 must be down
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0)
            return;

        Dimension d = m_scopeWindow.m_oscilloscope.getSize();
        int x = e.getX();
        int y = e.getY();

        // Mouse must be in m_labelWindow
        if (x < 0 || y < 0 || x > d.width || y > d.height)
        {
            return;
        }

        calcXP(e);

        if (m_scopeWindow.m_drawing_allowed)
        {
            // Draw in Wave16 object
            float yp = 32768-((float)y/(float)d.height)*65535;
            m_scopeWindow.m_wave.data[m_xp] = yp;
            m_scopeWindow.m_oscilloscope.repaint();
        }
        else
        {
            // Move markers
            m_scopeWindow.m_oscilloscope.m_markers.set(m_xp);
        }
        showPositionLabel(e);
    }

    /**
     * Draws text label to show X and Y values
     * @param e The current mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        calcXP(e);
        showPositionLabel(e);
    }

    /**
     * Click event
     * @param e The current mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // Button 1 must be down
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0)
            return;

        m_scopeWindow.m_oscilloscope.m_markers.reset();
    }

    /**
     * Dummy function: Does nothing but must exist because of inherited interfaces
     * @param e The current mouse event
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        switch (e.getButton())
        {
            // Left button
            case 1:
            break;

            // Right button
            // Display menu
            case 3:
            new MainMenu(m_scopeWindow, e);
            break;
        }
    }

    /**
     * Dummy function: Does nothing but must exist because of inherited interfaces
     * @param e The current mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    /**
     * Activates the edit control
     * @param e The current mouse event
     */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        m_xstep = (float) m_scopeWindow.m_oscilloscope.getWidth() / (m_scopeWindow.m_oscilloscope.m_length - 1);
        m_labelWindow.setVisible(true);
    }

    /**
     * Hides the edit control
     * @param e The current mouse event
     */
    @Override
    public void mouseExited(MouseEvent e)
    {
        m_labelWindow.setVisible(false);
    }
}
