package com.WaveCreator;

import com.google.common.math.LongMath;

/**
 */
public class Tools
{

    /**
     * Computes the GCD of a and b
     *
     * @param a Input a
     * @param b Input b
     * @return The GCD
     */
//    public static long gcd(long a, long b)
//    {
//        // a must be > b
//        if (a <= b)
//        {
//            long x = a;
//            a = b;
//            b = x;
//        }
//        while (b != 0)
//        {
//            long r = a % b;
//            a = b;
//            b = r;
//        }
//        return a;
//    }

    public static long lcm(long a, long b)
    {
        return a * b / LongMath.gcd(a, b);
    }

    /**
     *
     * @param in
     * @return
     */
    public static long lcm(long[] in)
    {
        if (in.length == 1)
        {
            return in[0];
        }
        long res = lcm(in[0], in[1]);
        if (in.length > 2)
        {
            for (int s = 2; s < in.length; s++)
            {
                res = lcm(res, in[s]);
            }
        }
        return res;
    }

    /**
     * Calculates next 2's power of input value Thus, 127 gives 128, 128 gives
     * 128 and 129 gives 256
     *
     * @param in The input value
     * @return The 2's power
     */
    public static int nextPowerOfTwo(int in)
    {
        return (int) Math.pow(2.0, Math.ceil(Math.log(in) / Math.log(2.0))); 
    }

    /**
     * Fit values into defined range
     *
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValues(double[] in)
    {
        double[] out = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Wave16.MAX_VALUE - Wave16.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = in[s] / div + Wave16.MIN_VALUE - am.min;
            if (Double.isInfinite(out[s]) || Double.isNaN(out[s]))
            {
                out[s] = 0.0;
            }
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     *
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValuesToPositiveByteRange(double[] in)
    {
        double[] out = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = 128 + Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     *
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValuesToByteRange(double[] in)
    {
        double[] out = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }
}
