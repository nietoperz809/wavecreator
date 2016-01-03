package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Tools;
import com.WaveCreator.Wave16;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 10:21:10
 */
public final class FunctionsSpecialEffects extends Functions
{
    public FunctionsSpecialEffects(Wave16 base)
    {
        super (base);
    }

    /**
     * Adds reverse wave to itself
     * @return The resulting wave
     */
    public Wave16 reverseAdd ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            double sum;
            sum = m_base.data[s] + m_base.data[m_base.data.length-s-1];
            out.data[s] = sum;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Multiplies reverse wave to itself
     * @return The resulting wave
     */
    public Wave16 reverseMult ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            double sum;
            sum = m_base.data[s] * m_base.data[m_base.data.length-s-1];
            out.data[s] = sum;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Squelch function. Values below the squelch level are set to zero
     * @param level Absolute value that opens the squelch
     * @param delay Delay in samples before squelch will close
     * @return The new Wave16 object
     */
    public Wave16 squelchToZero (@ParamDesc("Absolute value that opens the squelch")int level,
                                 @ParamDesc("Delay in samples before squelch will close")int delay)
    {
        Wave16 out = m_base.createEmptyCopy();
        int count = 0;
        int state = 0;
        for (int s=0; s<m_base.data.length; s++)
        {
            switch (state)
            {
                case 0:  // init
                if (Math.abs(m_base.data[s]) <= level)
                {
                    state = 1; // closed
                    out.data[s] = 0;
                }
                else
                {
                    state = 2; // open
                    count = delay;
                    out.data[s] = m_base.data[s];
                }
                break;

                case 1: // closed
                out.data[s] = 0;
                if (Math.abs(m_base.data[s]) > level)
                {
                    state = 2; // open
                    count = delay;
                }
                break;

                case 2: // open
                out.data[s] = m_base.data[s];
                if (Math.abs(m_base.data[s]) <= level)
                {
                    count--;
                    if (count == 0)
                    {
                        state = 1; // closed
                    }
                }
                else
                {
                    count = delay;
                }
                break;
            }
        }
        return out;
    }

    /**
     * Changes the sampling rate. Also stretches or shrinks the sampling array
     * @param newrate The new sampling rate
     * @return A new Wave16 object
     */
    public Wave16 changeSamplingRate (@ParamDesc("The new sampling rate")int newrate)
    {
        double factor = (double)newrate/(double)m_base.samplingRate;
        Wave16 out = m_base.functionsLength.stretchByFactor(factor);
        out.samplingRate = newrate;
        return out;
    }

    /**
     * Hacks sampling object into partitions
     * @param partlen Length of single partition
     * @return Array of partitions. Last element may contain fewer samples
     */
    public Wave16[] partitionize (@ParamDesc("Length of single partition")int partlen)
    {
        int full = m_base.data.length / partlen;
        int rest = m_base.data.length % partlen;
        Wave16[] out;
        if (rest == 0)
        {
            out = new Wave16[full];
        }
        else
        {
            out = new Wave16[full + 1];
        }
        for (int s = 0; s < full; s++)
        {
            out[s] = m_base.functionsDeletions.extractSamples(s * partlen, s * partlen + partlen);
        }
        if (rest != 0)
        {
            out[full] = m_base.functionsDeletions.extractSamples(full * partlen, full * partlen + rest);
        }
        return out;
    }

    /**
     * Hacks sampling object into partitions
     * @param num Number of partitions to generate
     * @return Array of partitions. Last element may contain fewer samples
     */
    public Wave16[] partitionizeTo (@ParamDesc("Number of partitions")int num)
    {
        return partitionize (m_base.data.length/num);
    }

    /**
     * Changes samples that are between <b>min</b> and <b>max</b>
     * @param min    lower level
     * @param max    upper level
     * @param newval the value substitutes them
     * @return The new sampling object
     */
    public Wave16 substitute (@ParamDesc("Lower level")int min,
                              @ParamDesc("Upper level")int max,
                              @ParamDesc("Substitution value") int newval)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            if (m_base.data[s] >= min && m_base.data[s] <= max)
            {
                out.data[s] = newval;
            }
            else
            {
                out.data[s] = m_base.data[s];
            }
        }
        return out;
    }


    /**
     * Copies samples to subsequent samples
     * @param step When to use actual sample
     * @return The new sample array
     */
    public Wave16 sampleDoubler (@ParamDesc("When to use actual sample") int step)
    {
        Wave16 out = m_base.createEmptyCopy();
        int cnt = 0;
        int idx = 0;
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[idx];
            cnt++;
            if (cnt % step == 0)
            {
                idx = s;
            }
        }
        return out;
    }

    /**
     * +- Alternates samples beginning with <b>step</b> original samples
     * @param step alternating period
     * @return The new sample array
     */
    public Wave16 alternate (@ParamDesc("Alternating period")int step)
    {
        Wave16 out = m_base.createEmptyCopy();
        boolean flag = false;
        int cnt = 0;
        for (int s = 0; s < m_base.data.length; s++)
        {
            if (!flag)
            {
                out.data[s] = m_base.data[s];
            }
            else
            {
                out.data[s] = -m_base.data[s];
            }
            cnt++;
            if (cnt >= step)
            {
                cnt = 0;
                flag = !flag;
            }
        }
        return out;
    }

    /**
     * Decreases sample resolution
     * @param d Resolution shrinking factor
     * @return The new generated object
     */
    public Wave16 decreaseResolution (@ParamDesc("Decreasing factor")double d)
    {
        Wave16 out = m_base.functionsAmplitude.multiply(1.0/d);
        out = out.functionsMathematical.floorTo(1);
        out = out.functionsAmplitude.multiply(d);
        return out;
    }

    /**
     * Changes all values to +/- newval
     * @param newval The new value
     * @return The new sampling object
     */
    public Wave16 onlySigns (@ParamDesc("New Value that is used as +/- value") int newval)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            if (m_base.data[s] < 0.0)
            {
                out.data[s] = -newval;
            }
            else
            {
                out.data[s] = newval;
            }
        }
        return out;
    }
}
