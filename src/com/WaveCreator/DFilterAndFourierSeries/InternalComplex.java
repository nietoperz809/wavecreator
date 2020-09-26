package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 01:24:39
 */
class InternalComplex
{
    public double re, im, mag, phase;

    InternalComplex ()
    {
        re = im = mag = phase = 0;
    }

    InternalComplex (double r, double i)
    {
        set(r, i);
    }

    void set (double aa, double bb)
    {
        re = aa;
        im = bb;
        setMagPhase();
    }

    void setMagPhase ()
    {
        mag = Math.sqrt(re * re + im * im);
        phase = Math.atan2(im, re);
    }

    InternalComplex (InternalComplex c)
    {
        set(c.re, c.im);
    }

    double magSquared ()
    {
        return mag * mag;
    }

    void set (double aa)
    {
        re = aa;
        im = 0;
        setMagPhase();
    }

    void set (InternalComplex c)
    {
        re = c.re;
        im = c.im;
        mag = c.mag;
        phase = c.phase;
    }

    void add (double r)
    {
        re += r;
        setMagPhase();
    }

    void add (double r, double i)
    {
        re += r;
        im += i;
        setMagPhase();
    }

    void add (InternalComplex c)
    {
        re += c.re;
        im += c.im;
        setMagPhase();
    }

    void addMult (double x, InternalComplex z)
    {
        re += z.re * x;
        im += z.im * x;
        setMagPhase();
    }

    void square ()
    {
        set(re * re - im * im, 2 * re * im);
    }

    void sqrt ()
    {
        setMagPhase(Math.sqrt(mag), phase * .5);
    }

    void setMagPhase (double m, double ph)
    {
        mag = m;
        phase = ph;
        re = m * Math.cos(ph);
        im = m * Math.sin(ph);
    }

    void mult (double c)
    {
        re *= c;
        im *= c;
        mag *= c;
    }

    void mult (InternalComplex c)
    {
        mult(c.re, c.im);
    }

    void mult (double c, double d)
    {
        set(re * c - im * d, re * d + im * c);
    }

    void recip ()
    {
        double n = re * re + im * im;
        set(re / n, -im / n);
    }

    void div (InternalComplex c)
    {
        double n = c.re * c.re + c.im * c.im;
        mult(c.re / n, -c.im / n);
    }

    void rotate (double a)
    {
        setMagPhase(mag, (phase + a) % (2 * DFilterFrame.pi));
    }

    void conjugate ()
    {
        im = -im;
        phase = -phase;
    }

    void pow (double p)
    {
        //double arg = java.lang.Math.atan2(im, re);
        phase *= p;
        double abs = Math.pow(re * re + im * im, p * .5);
        setMagPhase(abs, phase);
    }
}
