package com.WaveCreator.Functions;

import com.WaveCreator.Helpers.Tools;
import com.WaveCreator.Wave16;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 10:20:53
 */
public final class FunctionsTwoWaves extends Functions
{
    public FunctionsTwoWaves(Wave16 base)
    {
        super (base);
    }

    /**
     * Concatenates two sampling objects
     * The sampling rate is that of this object
     * @param b second array
     * @return concatenation
     */
    public Wave16 combineAppend(Wave16 b)
    {
        Wave16 out = new Wave16(m_base.data.length + b.data.length, m_base.samplingRate);
        System.arraycopy(m_base.data, 0, out.data, 0, m_base.data.length);
        System.arraycopy(b.data, 0, out.data, m_base.data.length, b.data.length);
        return out;
    }

    /*
        template <typename Type>
        void slow_convolution(const Type *f, const Type *g, Type *h, ulong n)
        // (cyclic) convolution: h[] := f[] (*) g[]
        // n := array length
        {
            for (ulong tau=0; tau<n; ++tau)
            {
                Type s = 0.0;
                for (ulong k=0; k<n; ++k)
                {
                    ulong k2 = tau - k;
                    if ( (long)k2<0 )
                        k2 += n; // modulo n
                    s += (f[k]*g[k2]);
                }
                h[tau] = s;
            }
        }
*/

    public Wave16 combineCyclicConvolution (Wave16 other)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int tau = 0; tau<m_base.data.length; tau++)
        {
            double s = 0.0;
            for (int k=0; k<m_base.data.length; k++)
            {
                int k2 = tau - k;
                if (k2 < 0)
                    k2 += m_base.data.length;
                s += (m_base.data[k] * other.data[k2]);
            }
            out.data[tau] = s;
        }
        out.data = Tools.fitValues (out.data);
        return out;
    }

    /**
     * Combines two sampling arrays
     * The <b>in</b> wave may be smaller
     * @param in Wave that is used to supply other values
     * @return The combination
     */
    public Wave16 combineArithmeticAverage(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = (m_base.data[s] + in.data[s % in.data.length]) / 2.0;
        }
        return out;
    }

    /**
     * Combines the difference of two sampling arrays
     * @param in Another Wave16 object
     * @return The difference of this and the other object
     */
    public Wave16 combineDifference (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            int in_idx = s % in.data.length;
            out.data[s] = m_base.data[s] - in.data[in_idx];
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineSum (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            int in_idx = s % in.data.length;
            out.data[s] = m_base.data[s] + in.data[in_idx];
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Calculates numeric quadrature over all samples
     * @param in Othr wave object
     * @return New sampling data object
     */
    public Wave16 combineDftLike (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        double sum = 0.0;

        for (int k = 0; k < m_base.data.length; k++)
        {
            int in_idx = k % in.data.length;
            sum = sum + m_base.data[k] - in.data[in_idx];
            out.data[k] = sum;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineMult (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            int in_idx = s % in.data.length;
            out.data[s] = m_base.data[s] * in.data[in_idx];
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineDiv (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s] / in.data[s % in.data.length];
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineExp (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.pow(m_base.data[s], in.data[s % in.data.length]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineLog (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.log(m_base.data[s])/Math.log(in.data[s % in.data.length]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineMod (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s] % in.data[s % in.data.length];
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineBigger (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = in.data[s % in.data.length];
            double v2 = m_base.data[s];
            if (v1 > v2)
                out.data[s] = v1;
            else
                out.data[s] = v2;
        }
        return out;
    }

    public Wave16 combineBiggerSign (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = Math.abs(in.data[s % in.data.length]);
            double v2 = Math.abs(m_base.data[s]);
            if (v1 > v2)
                out.data[s] = in.data[s % in.data.length];
            else
                out.data[s] = m_base.data[s];
        }
        return out;
    }


    public Wave16 combineSmaller (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = in.data[s % in.data.length];
            double v2 = m_base.data[s];
            if (v1 < v2)
                out.data[s] = v1;
            else
                out.data[s] = v2;
        }
        return out;
    }

    public Wave16 combineSmallerSign (Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = Math.abs(in.data[s % in.data.length]);
            double v2 = Math.abs(m_base.data[s]);
            if (v1 < v2)
                out.data[s] = in.data[s % in.data.length];
            else
                out.data[s] = m_base.data[s];
        }
        return out;
    }


    /**
     * Mixes two waves
     * @param in Wave to be mixed with this wave
     * @return A new wave that is the mix
     */
    public Wave16 combineMix(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            if (s%2 == 0)
            {
                out.data[s] = m_base.data[s];
            }
            else
            {
                out.data[s] = in.data[s % in.data.length];    
            }
        }
        return out;
    }

    public Wave16 combineAdd(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = (int) m_base.data[s] + (int) in.data[s % in.data.length];
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * XORs two waves
     * The <b>in</b> wave may be smaller
     * @param in Wave that is used to supply XOR values
     * @return The new sampling array
     */
    public Wave16 combineBinaryXor(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = (int) m_base.data[s] ^ (int) in.data[s % in.data.length];
        }
        return out;
    }

    /**
     * ANDs two waves
     * The <b>in</b> wave may be smaller
     * @param in Wave that is used to supply XOR values
     * @return The new sampling array
     */
    public Wave16 combineBinaryAnd(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = (int) m_base.data[s] & (int) in.data[s % in.data.length];
        }
        return out;
    }

    /**
     * ORs two waves
     * The <b>in</b> wave may be smaller
     * @param in Wave that is used to supply XOR values
     * @return The new sampling array
     */
    public Wave16 combineBinaryOr(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = (int) m_base.data[s] | (int) in.data[s % in.data.length];
        }
        return out;
    }

    public Wave16 combineQuadraticAverage(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            int idx = s % in.data.length;
            double q1 = m_base.data[s] * m_base.data[s];
            double q2 = in.data[idx] * in.data[idx];
            double sign;
            if (q1 > q2)
                sign = Math.signum (m_base.data[s]);
            else
                sign = Math.signum (in.data[idx]);
            out.data[s] = sign * Math.sqrt(q1 + q2);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Computes the GCD of a and b
     * @param a Input a
     * @param b Input b
     * @return The GCD
     */
    private long gcd(long a, long b)
    {
        // a must be > b
        if (a <= b)
        {
            long x = a;
            a = b;
            b = x;
        }
        while (b != 0)
        {
            long r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    private long lcm (long a, long b)
    {
        return a*b/gcd(a,b);
    }

    public Wave16 combineGcd(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = gcd ((long)m_base.data[s], (long)in.data[s % in.data.length]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineLcm(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = lcm ((long)m_base.data[s], (long)in.data[s % in.data.length]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineTheta(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            out.data[s] = Math.atan2 (m_base.data[s], in.data[s % in.data.length]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 combineSign(Wave16 in)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double sign = Math.signum(in.data[s % in.data.length]);
            out.data[s] = Math.abs(m_base.data[s]) * sign;
        }
        return out;
    }
}
