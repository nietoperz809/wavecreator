package com.WaveCreator;

/**
 * Galois field 2^8
 */
public class GaloisField256
{
    // define the Size & Prime Polynomial of this Galois field (2^8)
    final static int GF = 256;
    // the Galois field for 2^8, with a 'prime polynomial' of x^8 + x^5 + x^3 + x^2 + 1
    // whose equivalent value P is binary 100101101 or decimal 301.
    final static int PP = 301;

    // establish global Log and Antilog arrays
    private final int[] Log = new int[GF];
    private final int[] ALog = new int[GF];

    /**
     *
     */
    public GaloisField256()
    {
        int i;
        Log[0] = 1-GF;
        ALog[0] = 1;
        for (i=1; i<GF; i++)
        {
            ALog[i] = ALog[i-1] * 2;
            if (ALog[i] >= GF)
                ALog[i] ^= PP;
            Log[ALog[i]] = i;
        }
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public int product (int A, int B)
    {
        if ((A == 0) || (B == 0))
            return (0);
        else
        
            return (ALog[(Log[A] + Log[B]) % (GF-1)]);
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public int quotient (int A, int B)
    { // namely A divided by B
        if (B == 0)
            return (1-GF); // signifying an error!
        else if (A == 0)
            return (0);
        else
            return (ALog[(Log[A] - Log[B] + (GF-1)) % (GF-1)]);
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public int sum (int A, int B)
    {
        return (A ^ B);
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public int difference (int A, int B)
    {
        return (A ^ B);
    }
}
