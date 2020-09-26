package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 04.01.2009
 * Time: 19:59:51
 */
public class ByteBucket
{
    final byte[] m_array;
    int m_count;
    int m_space;

    public ByteBucket (int size)
    {
        m_array = new byte[size];
        reset();
    }

    public void reverseEndian16()
    {
        for (int s=0; s<m_array.length; s+=2)
        {
            byte b1 = m_array[s];
            m_array[s] = m_array[s+1];
            m_array[s+1] = b1;
        }
    }

    public void reset ()
    {
        m_count = 0;
        m_space = m_array.length;
    }

    public void put (byte[] in, int length)
    {
        int num;
        if (length < m_space)
        {
            num = length;
        }
        else
        {
            num = m_space;
        }
        System.arraycopy(in, 0, m_array, m_count, num);
        m_space -= num;
        m_count += num;
    }

    public boolean isFull ()
    {
        return m_space == 0;
    }

    public byte[] getArray ()
    {
        return m_array;
    }

    public int getSize ()
    {
        return m_array.length;
    }
}
