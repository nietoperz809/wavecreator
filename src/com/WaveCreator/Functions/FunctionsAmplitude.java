package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Helpers.Tools;
import com.WaveCreator.Wave16;
import com.WaveCreator.Wave16AmplitudeInfo;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 19:40:21
 */
public final class FunctionsAmplitude extends Functions
{
    public FunctionsAmplitude (Wave16 base)
    {
        super (base);
    }

    /**
     * Changes amplitude by multiplying every sample
     * @param mult Multiplier
     * @return New sampling data
     */
    public Wave16 multiply(@ParamDesc("Multiplier") double mult)
    {
        double[] m = {mult};
        return multiply(m);
    }

    /**
     * Changes amplitude by multiplying with array
     * @param mult Array o multipliers
     * @return New sampling data
     */
    public Wave16 multiply(@ParamDesc("Array of multipliers")double[] mult)
    {
        Wave16 out = m_base.createEmptyCopy();
        int m_idx = 0;
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = m_base.data[s];
            v1 *= mult[m_idx++];
            if (m_idx == mult.length)
            {
                m_idx = 0;
            }
            out.data[s] = v1;
        }
        return out;
    }

    public Wave16 multiply(@ParamDesc("First sample to be changed")int from,
                                   @ParamDesc("Last sample to be changed")int to,
                                   @ParamDesc("Multiplicator")double factor)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            if (s >= from && s <= to)
            {
                out.data[s] = m_base.data[s] * factor;
            }
            else
            {
                out.data[s] = m_base.data[s];
            }
        }
        return out;
    }

    /**
     * Shifts amplitude by offset
     * @param shift Offset
     * @return New sampling data
     */
    public Wave16 shift(@ParamDesc("Constant value to add") double shift)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s] + shift;
        }
        return out;
    }

    /**
     * Moves wave to the center if possible
     * @return The new generated wave
     */
    public Wave16 moveToCenter()
    {
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo(m_base);
        return shift(-am.min-Math.floor(am.span/2));
    }

    /**
     * Moves wave to the top if possible
     * @return The new generated wave
     */
    public Wave16 moveToTop()
    {
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo(m_base);
        return shift(Wave16.MAX_VALUE - am.max);
    }

    /**
     * Moves wave to the bottom if possible
     * @return The new generated wave
     */
    public Wave16 moveToBottom()
    {
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo(m_base);
        return shift(Wave16.MIN_VALUE - am.min);
    }

    public Wave16 divideToTop()
    {
        return fitValues().functionsAmplitude.multiply(0.5).functionsAmplitude.moveToTop();
    }

    public Wave16 divideToBottom()
    {
        return fitValues().functionsAmplitude.multiply(0.5).functionsAmplitude.moveToBottom();
    }

    /**
     * Changes amplitude from 0 to max
     * @param samples First samples to be faded
     * @return The new output
     */
    public Wave16 fadeIn (@ParamDesc("Samples used for fading")int samples)
    {
        Wave16 out = m_base.copy();
        double step = 1.0 / samples;
        double mult = 0.0;
        for (int s = 0; s < samples; s++)
        {
            mult += step;
            out.data[s] = m_base.data[s] * mult;
        }
        return out;
    }

    /**
     * Changes amplitude from 0 to max
     * @param samples Last samples to be faded
     * @return The new output
     */
    public Wave16 fadeOut(@ParamDesc("Samples used for fading")int samples)
    {
        Wave16 out = m_base.copy();
        double step = 1.0 / samples;
        double mult = 1.0;
        int start = m_base.data.length - samples;
        for (int s = start; s < m_base.data.length; s++)
        {
            mult -= step;
            out.data[s] = m_base.data[s] * mult;
        }
        return out;
    }

    /**
     * Adjusts whole sampling array so that it fits into min/max range
     * @return The adjusted value
     */
    public Wave16 fitValues()
    {
        Wave16 out = m_base.createEmptyCopy();
        out.data = Tools.fitValues(m_base.data);
        return out;
    }

    /**
     * Reverses sampling data
     * @return New sampling data
     */
    public Wave16 invert()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = -m_base.data[s];
        }
        return out;
    }

    /**
     * Sets all negative values to zero
     * @return The new sampling object
     */
    public Wave16 zeroAllNegatives()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            if (m_base.data[s] < 0.0)
            {
                out.data[s] = 0;
            }
            else
            {
                out.data[s] = m_base.data[s];
            }
        }
        return out;
    }

    /**
     * Sets all positive values to zero
     * @return The new sampling object
     */
    public Wave16 zeroAllPositives()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            if (m_base.data[s] > 0.0)
            {
                out.data[s] = 0;
            }
            else
            {
                out.data[s] = m_base.data[s];
            }
        }
        return out;
    }

    /**
     * Clips values that are too big or too small
     * @param clipvalue Value used as border for +/-
     * @return The new sampling object
     */
    public Wave16 clipTo (@ParamDesc("Absolute value used for clipping")int clipvalue)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            double a = Math.abs (m_base.data[s]);
            if (a > clipvalue)
            {
                out.data[s] = clipvalue * Math.signum(m_base.data[s]);
            }
            else
            {
                out.data[s] = m_base.data[s];
            }
        }
        return out;
    }
}
