package com.WaveCreator.Equalizer;

/**
 * New Class.
 * User: Administrator
 * Date: 03.01.2009
 * Time: 01:16:27
 */
public class IIRCoefficients
{
    final double beta;
    final double alpha;
    final double gamma;

    public IIRCoefficients(double b, double a, double g)
    {
        this.beta = b;
        this.alpha = a;
        this.gamma = g;
    }
}
