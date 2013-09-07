package com.WaveCreator;

import java.util.Arrays;

/**
 */
public class Tools
{
    /**
     * Computes the GCD of a and b
     * @param a Input a
     * @param b Input b
     * @return The GCD
     */
    public static long gcd(long a, long b)
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

    public static long lcm (long a, long b)
    {
        return a*b/gcd(a,b);
    }

    public static long lcm (long[] in)
    {
        if (in.length == 1)
            return in[0];
        long res = lcm (in[0], in[1]);
        if (in.length > 2)
        {
            for (int s = 2; s<in.length; s++)
            {
                res = lcm (res, in[s]);
            }
        }
        return res;
    }
}
