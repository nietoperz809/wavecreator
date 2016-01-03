package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Tools;
import com.WaveCreator.Wave16;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 19:07:19
 */
public final class FunctionsMathematical extends Functions
{
    public FunctionsMathematical (Wave16 base)
    {
        super(base);
    }

    public Wave16 u_law ()
    {
        Wave16 out = m_base.functionsMathematical.normalize();
        double u = Wave16.MAX_VALUE;

        for (int s = 0; s < m_base.data.length; s++)
        {
            double m;
            m = Math.signum(out.data[s]) * Math.log(1 + u * Math.abs(out.data[s])) / Math.log(1 + u);
            m *= Wave16.MAX_VALUE;
            out.data[s] = m;
        }
        return out;
    }

    public Wave16 haarTransform ()
    {
        Wave16 out = m_base.functionsLength.padToNextPowerOfTwo(0);
        int ldn = (int) (Math.log(out.data.length) / Math.log(2));
        int n = 1 << ldn;
        double s2 = Math.sqrt(0.5);
        double v = 1.0;
        for (int js = 2; js <= n; js <<= 1)
        {
            v *= s2;
            for (int j = 0, t = js >> 1; j < n; j += js, t += js)
            {
                double x = out.data[j];
                double y = out.data[t];
                out.data[j] = x + y;
                out.data[t] = (x - y) * v;
            }
        }
        out.data[0] *= v; // v==1.0/sqrt(n);
        //out = out.functionsDeletions.deleteSamplesFromEnd(diff);
        //out.data = Wave16.fitValues(out.data);
        return out;
    }

    public Wave16 inverseHaarTransform ()
    {
        Wave16 out = m_base.functionsLength.padToNextPowerOfTwo(0);
        int ldn = (int) (Math.log(out.data.length) / Math.log(2));
        int n = 1 << ldn;
        double s2 = Math.sqrt(2.0);
        double v = 1.0 / Math.sqrt((double) n);
        out.data[0] *= v;
        for (int js = n; js >= 2; js >>= 1)
        {
            for (int j = 0, t = js >> 1; j < n; j += js, t += js)
            {
                double x = out.data[j];
                double y = out.data[t] * v;
                out.data[j] = x + y;
                out.data[t] = x - y;
            }
            v *= s2;
        }
        //out = out.functionsDeletions.deleteSamplesFromEnd(diff);
        //out.data = Wave16.fitValues(out.data);
        return out;
    }

    public Wave16 inverse ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.pow(m_base.data[s], -1.0);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 cubeRoot ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.cbrt(m_base.data[s]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 log ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.log(m_base.data[s] + Wave16.MAX_VALUE);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

//    public Wave16 exp()
//    {
//        Wave16 out = m_base.createEmptyCopy();
//        for (int s = 0; s < m_base.data.length; s++)
//        {
//            out.data[s] = Math.exp(m_base.data[s]+Wave16.MAX_VALUE);
//        }
//        out.data = Wave16.fitValues(out.data);
//        return out;
//    }

    /**
     * Makes all samples positive
     *
     * @return New sampling data
     */
    public Wave16 abs ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.abs(m_base.data[s]);
        }
        return out;
    }

    /**
     * Negative <b>abs()</b> function
     * All positive samples are made <0
     *
     * @return A new sampling object
     */
    public Wave16 absNegative ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = -Math.abs(m_base.data[s]);
        }
        return out;
    }

    public Wave16 floorTo (@ParamDesc("Rounding level") double lev)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.floor(m_base.data[s] * lev) / lev;
        }
        return out;
    }

    public Wave16 mod (@ParamDesc("Second Mod operand") double m)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s] % m;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Makes positive square roots from 16-bit sampling data
     *
     * @return Output array
     */
    public Wave16 sqrt ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = m_base.data[s];
            if (v1 > 0)
            {
                v1 = Math.sqrt(Wave16.MAX_VALUE * v1);
            }
            else
            {
                v1 = -Math.sqrt(Wave16.MAX_VALUE * -v1);
            }
            out.data[s] = (short) v1;
        }
        return out;
    }

    /**
     * Calculates the derivative of this wave
     *
     * @return The new wave
     */
    public Wave16 derive ()
    {
        double f1;
        double f2;
        Wave16 out = m_base.createEmptyCopy();

        for (int s = 0; s < (m_base.data.length - 1); s++)
        {
            f1 = m_base.data[s];
            f2 = m_base.data[s + 1];
            out.data[s] = f2 - f1;
        }
        // Last sample
        out.data[m_base.data.length - 1] = out.data[m_base.data.length - 2];
        return out;
    }

    public Wave16 deriveAndFitValues ()
    {
        Wave16 derived = derive();
        derived.data = Tools.fitValues(derived.data);
        return derived;
    }

    /**
     * Calculates numeric quadrature over all samples
     *
     * @return New sampling data object
     */
    public Wave16 integrateRaw ()
    {
        Wave16 out = m_base.createEmptyCopy();
        double sum = 0.0;

        for (int k = 0; k < m_base.data.length; k++)
        {
            sum = sum + m_base.data[k];
            out.data[k] = sum;
        }
        return out;
    }

    /**
     * Calculates numeric quadrature over all samples
     * Values are normalized before and expanded after
     *
     * @return New sampling data object
     */
    public Wave16 integrateAndFitValues ()
    {
        Wave16 out = normalize().functionsMathematical.integrateRaw();
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Normalizes sampling array to -1 ...0 ... +1
     *
     * @return The normalized aray
     */
    public Wave16 normalize ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s] / Wave16.MAX_VALUE;
        }
        return out;
    }

    /**
     * Stretches a normalized array so that it is hearable
     *
     * @return The stretched(samples) array
     */
    public Wave16 deNormalize ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s] * Wave16.MAX_VALUE;
        }
        return out;
    }

    /**
     * Creates a new sampling array that is the energy of the source
     *
     * @param step Window size
     * @return The new array
     */
    public Wave16 energy (@ParamDesc("Window size") int step)
    {
        Wave16 norm = m_base.functionsMathematical.normalize();
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < norm.data.length; s++)
        {
            double sum = 0.0;
            for (int n = 0; n < step; n++)
            {
                int idx = s + n;
                if (idx == m_base.data.length)
                {
                    break;
                }
                sum = sum + (norm.data[idx] * norm.data[idx]);
            }
            out.data[s] = sum;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }
}
