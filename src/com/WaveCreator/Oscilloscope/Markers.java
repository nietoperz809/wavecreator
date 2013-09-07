package com.WaveCreator.Oscilloscope;

/**
 * New Class.
 * User: Administrator
 * Date: 01.01.2009
 * Time: 18:27:03
 */
public class Markers
{
    public int m_marker_1 = -1;
    public int m_marker_2 = -1;

    private final Oscilloscope m_scope;

    public Markers (Oscilloscope scope)
    {
        m_scope = scope;
    }

    public boolean hasMarkers()
    {
        return !(m_marker_1 == -1 || m_marker_2 == -1);
    }

    public int[] get()
    {
        if (!hasMarkers())
            return null;
        return new int[]{m_marker_1, m_marker_2+1};
    }

    public void reset()
    {
        m_marker_1 = -1;
        m_marker_2 = -1;
        m_scope.repaint();
    }

    public void set (int xp)
    {
        if (m_marker_1 == -1)
        {
            m_marker_1 = xp;
        }
        else
        {
            m_marker_2 = xp;
        }
        if (m_marker_1 >= m_marker_2)
        {
            int tmp = m_marker_1;
            m_marker_1 = m_marker_2;
            m_marker_2 = tmp;
        }
        m_scope.repaint();
    }
}
