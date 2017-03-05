package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Wave16;
import com.WaveCreator.Helpers.Tools;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 19:54:16
 */
public final class FunctionsLength extends Functions
{
    public FunctionsLength (Wave16 base)
    {
        super (base);
    }

    /**
     * Pads WaveArray end with value
     * @param padvalue Value to append
     * @param num      number of new samples to add
     * @return The new sampling array
     */
    public Wave16 insertSamplesAtEnd(@ParamDesc("Value to append") double padvalue,
                                     @ParamDesc("Number of samples to add") int num)
    {
        return insertSamples(m_base.data.length, m_base.data.length + num, padvalue);
    }

    /**
     * Pads WaveArray begin with value
     * @param padvalue Value to append to the beginning
     * @param num      number of new samples to add
     * @return The new sampling array
     */
    public Wave16 insertSamplesAtBeginning (@ParamDesc("PaddingValue") double padvalue,
                                            @ParamDesc("Number of Samples") int num)
    {
        return insertSamples(0, num, padvalue);
    }

    /**
     * Pads WaveArray at the end so that number of samples is a power of 2
     * @param padvalue Value do add
     * @return The new sampling array
     */
    public Wave16 padToNextPowerOfTwo (@ParamDesc("Value to append") int padvalue)
    {
        return insertSamplesAtEnd(padvalue, Tools.nextPowerOfTwo(m_base.data.length) - m_base.data.length);
    }

    /**
     * Grows the array by inserting new samples of same value
     * @param from   Position of insertion
     * @param to     Last insertion
     * @param newval Value to insert
     * @return The new sampling array
     */
    public Wave16 insertSamples (@ParamDesc("First position")int from,
                                 @ParamDesc("Last position")int to,
                                 @ParamDesc("Value to insert")double newval)
    {
        double[] v = new double[to - from];
        for (int s = 0; s < (to - from); s++)
        {
            v[s] = newval;
        }
        return insertSamples(from, v);
    }

    /**
     * Grows the array by inserting new samples
     * @param from   Position of insertion
     * @param newval Array of new samples to be inserted
     * @return The new sampling array
     */
    public Wave16 insertSamples(@ParamDesc("Position of first insertion")int from,
                         @ParamDesc("Array of new samples")double[] newval)
    {
        Wave16 out = new Wave16(m_base.data.length + newval.length, m_base.samplingRate);
        System.arraycopy(m_base.data, 0, out.data, 0, from);
        System.arraycopy(m_base.data, from, out.data, newval.length + from, m_base.data.length - from);
        System.arraycopy(newval, 0, out.data, from, newval.length);
        return out;
    }

    /**
     * Appends wave to itself multiple times
     * @param num how often
     * @return The new sampling array
     */
    public Wave16 appendSelf (@ParamDesc("How often") int num)
    {
        Wave16 out = new Wave16(m_base.data.length * num, m_base.samplingRate);
        for (int s = 0; s < num; s++)
        {
            System.arraycopy(m_base.data, 0, out.data, m_base.data.length * s, m_base.data.length);
        }
        return out;
    }

    /**
     * Stretches or shrinks number of samples to <b>num</b>
     * @param num Number of new samples
     * @return The new sampling array
     */
    public Wave16 stretchToNumberOfSamples (@ParamDesc("Number of new samples") int num)
    {
        Wave16 out = new Wave16(num, m_base.samplingRate);
        double div = (double) m_base.data.length / (double) num;
        for (int s = 0; s < num; s++)
        {
            out.data[s] = m_base.data[(int) (s * div)];
        }
        return out;
    }

    public Wave16 stretchToQuadratic()
    {
        int val = (int)Math.ceil(Math.sqrt(m_base.data.length));
        return stretchToNumberOfSamples (val*val);
    }

    public Wave16 stretchToCubic()
    {
        int val = (int)Math.ceil(Math.cbrt(m_base.data.length));
        return stretchToNumberOfSamples (val*val*val);
    }

    /**
     * Same as stretch but adjusts sampling rate
     * @param num New number of new samples
     * @return The new sampling array
     */
    public Wave16 stretchKeepSpeed (@ParamDesc("Number of new samples") int num)
    {
        double div = (double) m_base.data.length / (double) num;
        Wave16 out = stretchToNumberOfSamples(num);
        out.samplingRate = (int) ((double) out.samplingRate / div);
        return out;
    }

    /**
     * Stretches or shrinks number of samples to factor <b>fact</b>
     * @param fact stretching factor
     * @return The new sampling array
     */
    public Wave16 stretchByFactor (@ParamDesc("Stretching factor") double fact)
    {
        double f = fact * m_base.data.length;
        return stretchToNumberOfSamples((int) f);
    }

    /**
     * Same as stretchByFactor but adjust sampling rate to keep speed
     * @param fact streching factor
     * @return The new sampling array
     */
    public Wave16 stretchByFactorKeepSpeed (@ParamDesc("Streching factor") double fact)
    {
        double f = fact * m_base.data.length;
        return stretchKeepSpeed((int) f);
    }

    /**
     * Pads WaveArray at the end with zero
     * so that number of samples is a power of 2
     * @return The new sampling array
     */
    public Wave16 padToNextPowerOfTwo()
    {
        return insertSamplesAtEnd(0, Tools.nextPowerOfTwo(m_base.data.length) - m_base.data.length);
    }

    /**
     * Stretches number of samples to the next power of 2
     * @return The new sampling array
     */
    public Wave16 stretchToNextPowerOfTwo()
    {
        return stretchToNumberOfSamples(Tools.nextPowerOfTwo(m_base.data.length));
    }

}
