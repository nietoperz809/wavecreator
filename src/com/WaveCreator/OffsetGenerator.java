package com.WaveCreator;

/**
 * This class generates offsets.
 */
public class OffsetGenerator
{
    private final double m_start;
    private final double m_stop;
    private final double m_parts;
    private final double m_linearDiv;
    private final double m_logDiv;

    /**
     * Constructor that sets all parameters
     * @param start Value at position 0
     * @param stop  Last value
     * @param parts Zero-based maximum index
     */
    public OffsetGenerator(double start, double stop, double parts)
    {
        m_start = start;
        m_stop = stop;
        m_parts = parts;
        m_linearDiv = (stop - start) / (parts - 1);
        m_logDiv = (parts - 1) / Math.log(parts);
    }

    /**
     * Checks range of an index
     * @param idx Index to check
     * @throws Exception Thrown if index is out of range
     */
    private void checkIndex(int idx) throws Exception
    {
        if (idx < 0 || idx >= m_parts)
        {
            throw new Exception("Index out of range");
        }
    }

    /**
     * Gets linear value at <b>idx</b>
     * @param idx Index of value
     * @return The linear value
     * @throws Exception from CheckIndex
     */
    public double getLinearValue(int idx) throws Exception
    {
        checkIndex(idx);
        return m_start + idx * m_linearDiv;
    }

    /**
     * Gets exponantial value at <b>idx</b>
     * @param idx Index of value
     * @return The linear value
     * @throws Exception from CheckIndex
     */
    public double getExponentialValue(int idx) throws Exception
    {
        checkIndex(idx);
        if (idx == m_parts - 1)
        {
            return m_stop;
        }
        return m_start + (Math.exp(idx / m_logDiv) - 1) * m_linearDiv;
    }
}
