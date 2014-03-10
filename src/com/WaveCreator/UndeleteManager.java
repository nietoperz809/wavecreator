package com.WaveCreator;

import java.util.ArrayList;

/**
 * New Class.
 * User: Administrator
 * Date: 12.03.2009
 * Time: 23:44:13
 */
public class UndeleteManager
{
    final ArrayList<Wave16> m_waves = new ArrayList<>();
    private static UndeleteManager m_instance = null;

    private UndeleteManager()
    {
        
    }

    public static UndeleteManager getInstance()
    {
        if (m_instance == null)
            m_instance = new UndeleteManager();
        return m_instance;
    }

    public void add (Wave16 w)
    {
        m_waves.add(w);
    }

    public void remove (Wave16 w)
    {
        m_waves.remove(w);
    }

    public Wave16[] getAllWaves()
    {
        Wave16[] wa = new Wave16[m_waves.size()];
        return m_waves.toArray(wa);
    }
}
