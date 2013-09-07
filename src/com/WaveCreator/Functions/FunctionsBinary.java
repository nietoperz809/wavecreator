package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Wave16;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 10:21:18
 */
public final class FunctionsBinary extends Functions
{
    public FunctionsBinary(Wave16 base)
    {
        super(base);
    }

    /**
     * Converts this wave into gray code
     * @return The encoded wave
     */
    public Wave16 grayCode()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            int x = (int) (m_base.data[s] - Wave16.MIN_VALUE);
            x = (x ^ (x & 0xffff) >>> 1);
            out.data[s] = x + Wave16.MIN_VALUE;
        }
        return out;
    }

    /**
     * Converts this wave into reverse gray code
     * @return The converted wave
     */
    public Wave16 grayCodeInverse()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            int r = 16;
            int x = (int) (m_base.data[s] - Wave16.MIN_VALUE);
            while (--r != 0)
            {
                x ^= (x & 0xffff) >>> 1;
            }
            out.data[s] = x + Wave16.MIN_VALUE;
        }
        return out;
    }

    public Wave16 blueCode()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int n=0; n<m_base.data.length; n++)
        {
            short a = (short)m_base.data[n];
            short s = 16 >> 1;
            short m = (short)(0xffff << s);
            int sign;
            if (a < 0)
            {
                sign = -1;
                a = (short)-a;
            }
            else
                sign = 1;
            while (s != 0)
            {
                a ^= ( (a&m) >> s );
                s >>= 1;
                m ^= (m>>s);
            }
            out.data[n] = a * sign;
        }
        return out;
    }

    public Wave16 yellowCode16()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int n=0; n<m_base.data.length; n++)
        {
            short a = (short)m_base.data[n];
            short s = 16 >> 1;
            short m = (short)(0xffff >> s);
            while ( s != 0 )
            {
                a ^= ( (a&m) << s );
                s >>= 1;
                m ^= (m<<s);
            }
            out.data[n] = a;
        }
        return out;
    }

    public Wave16 binaryXor (@ParamDesc("Value to be xor'd with")int value)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double a = m_base.data[s];
            double frac = a-Math.rint(a);
            double sign = Math.signum(a);
            a = (int)Math.abs(a) ^ value;
            out.data[s] = sign*a + frac;
        }
        return out;
    }

    public Wave16 binaryAnd (@ParamDesc("Value to be and'd with")int value)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double a = m_base.data[s];
            double frac = a-Math.rint(a);
            double sign = Math.signum(a);
            a = (int)Math.abs(a) & value;
            out.data[s] = sign*a + frac;
        }
        return out;
    }

    public Wave16 binaryOr (@ParamDesc("Value to be or'd with")int value)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double a = m_base.data[s];
            double frac = a-Math.rint(a);
            double sign = Math.signum(a);
            a = (int)Math.abs(a) | value;
            out.data[s] = sign*a + frac;
        }
        return out;
    }

    public Wave16 xorChain (@ParamDesc("Initial XOR value")int init)
    {
        Wave16 out = m_base.createEmptyCopy();
        out.data[0] = (int)m_base.data[0] ^ init;
        for (int s=1; s<out.data.length; s++)
        {
            out.data[s] = (int)m_base.data[s] ^(int)out.data[s-1];
        }
        return out;
    }

    public Wave16 unxorChain (@ParamDesc("Initial XOR value")int init)
    {
        Wave16 out = m_base.createEmptyCopy();
        out.data[0] = (int)m_base.data[0] ^ init;
        for (int s=out.data.length-1; s>0; s--)
        {
            out.data[s] = (int)m_base.data[s-1] ^(int)m_base.data[s];
        }
        out.data[0] = (int)m_base.data[0] ^ init;
        return out;
    }

}
