package com.WaveCreator.FFT;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p/>
 * Cepstrum transformation class.
 * @author Christoph Lauer
 * @version 1.0, Begin Current 26/09/2002
 */
public final class CepstrumTransform
{
    //----------------------------------------------------------------------
    private int n, nu;
    private double[] xre;
    private double[] xim;

    //----------------------------------------------------------------------
    /**
     * Here transformation is done
     * how descibed in many theory books.
     * @param vec
     * @param logarithm
     * @return
     */
    public float[] doCepstrumTransform(float[] vec, boolean logarithm)
    {
        n = vec.length;
        float ld = (float) (Math.log(n) / Math.log(2.0));
        nu = (int) ld;
        if ((int)ld - ld != 0)
        {
            System.out.println("The Given Vector's length is not a power of two!!!");
            System.exit(-1);
        }
        xre = new double[n];
        xim = new double[n];
        //First Transform
        for (int i = 0; i < n; i++)
        {
            xre[i] = vec[i];
            xim[i] = 0.0;
        }
        FFT();
        // Logarithm and inverse Transform
        if (logarithm)
        {
            for (int i = 0; i < n; i++)
            {
                xre[i] = Math.log(Math.abs(100000000.0 * xre[i]) + 1.0);
                xim[i] = -Math.log(Math.abs(100000000.0 * xim[i]) + 1.0);
            }
        }
        if (!logarithm)
        {
            for (int i = 0; i < n; i++)
            {
                xre[i] = Math.log(Math.abs(xre[i]) + 1.0);
                xim[i] = -Math.log(Math.abs(xim[i]) + 1.0);
            }
        }
        FFT();
        float[] cep = new float[n];
        for (int i = 0; i < n; i++)
        {
            cep[i] = (float) (2 * Math.sqrt(xre[i] * xre[i] + xim[i] * xim[i]) / n);
        }
        cep[0] = 0.0f;
        cep[1] = 0.0f;
        return cep;
    }

    //----------------------------------------------------------------------
    /**
     * Implementation of FFT Algorithm
     */
    private void FFT()
    {
        int n2 = n / 2;
        int nu1 = nu - 1;
        double tr, ti, p, arg, c, s;
        int k = 0;

        for (int l = 1; l <= nu; l++)
        {
            while (k < n)
            {
                for (int i = 1; i <= n2; i++)
                {
                    p = bitrev(k >> nu1);
                    arg = 2 * Math.PI * p / n;
                    c = Math.cos(arg);
                    s = Math.sin(arg);
                    tr = xre[k + n2] * c + xim[k + n2] * s;
                    ti = xim[k + n2] * c - xre[k + n2] * s;
                    xre[k + n2] = xre[k] - tr;
                    xim[k + n2] = xim[k] - ti;
                    xre[k] += tr;
                    xim[k] += ti;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 = n2 / 2;
        }
        k = 0;
        int r;
        while (k < n)
        {
            r = bitrev(k);
            if (r > k)
            {
                tr = xre[k];
                ti = xim[k];
                xre[k] = xre[r];
                xim[k] = xim[r];
                xre[r] = tr;
                xim[r] = ti;
            }
            k++;
        }
    }

    //----------------------------------------------------------------------
    /**
     * Internal calcuating Class
     * @param j
     * @return
     */
    private int bitrev(int j)
    {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++)
        {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }
    //----------------------------------------------------------------------
}
