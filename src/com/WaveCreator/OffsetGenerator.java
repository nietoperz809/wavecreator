package com.WaveCreator;

/**
 * This class generates offsets.
 */
public class OffsetGenerator
{
    private final float m_start;
    private final float m_stop;
    private final float m_parts;
    private final float m_linearDiv;
    private final float m_logDiv;

    /**
     * Constructor that sets all parameters
     * @param start Value at position 0
     * @param stop  Last value
     * @param parts Zero-based maximum index
     */
    public OffsetGenerator(float start, float stop, float parts)
    {
        m_start = start;
        m_stop = stop;
        m_parts = parts;
        m_linearDiv = (stop - start) / (parts - 1);
        m_logDiv = (float) ((parts - 1) / Math.log(parts));
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
    public float getLinearValue(int idx) throws Exception
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
    public float getExponentialValue(int idx) throws Exception
    {
        checkIndex(idx);
        if (idx == m_parts - 1)
        {
            return m_stop;
        }
        return (float) (m_start + (Math.exp(idx / m_logDiv) - 1) * m_linearDiv);
    }
}
