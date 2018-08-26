package com.WaveCreator.Functions;

import com.WaveCreator.Helpers.Tools;
import com.WaveCreator.Wave16;

import java.util.function.Function;

public class Primitives
{
    static public double saw (double x)
    {
        return (2*x/ Wave16.PI+1)%2 - 1;
    }

    static public double sin (double x)
    {
        return Math.sin(x);
    }

    static public double squ (double x)
    {
        return Math.signum(Math.sin(x));
    }

    static public double tri (double x)
    {
        return Math.asin(Math.sin(x))*2/Wave16.PI;
    }

    /**
     * apply generator func
     * @param rate Sampling rate
     * @param freq Array of frequencies
     * @param samples Number of samples
     * @return new Wave16 object
     */
    static public Wave16 apply (int rate, double[] freq, int samples,
                                Function<Double, Double> func)
    {
        Wave16 out = new Wave16(samples, rate);

        for (int x = 0; x < samples; x++)
        {
            double v = 0;
            for (double f : freq)
            {
                v = v + (Wave16.MAX_VALUE * Math.sin(x * Wave16.TWOPI / rate * f));
            }
            out.data[x] = (float)(v / freq.length);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Makes a sine wave that runs from Fstart to Fend
     * @param samplingrate Sampling rate
     * @param fstart       start frequency
     * @param fend         end frequency
     * @param samples      number of samples to generate
     * @return The new sampling data
     */
    static public Wave16 sweep(int samplingrate, int fstart, int fend, int samples,
                                Function<Double, Double> func)
    {
        Wave16 out = new Wave16(samples, samplingrate);
        double step = Math.abs(((double) fend - (double) fstart) / samples / Wave16.PI);
        double fact = fstart < fend ? fstart : fend;
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = (float) (Wave16.MAX_VALUE * func.apply(Wave16.TWOPI * fact * ((double) x / samplingrate)));
            fact += step;
        }
        if (fstart < fend)
            return out;
        else
            return out.functionsReorder.reverse();
    }

    /**
     * Make a FM ware
     * @param samplingrate Samplingrate
     * @param samples  No of samples
     * @param freq   Carrier
     * @param freq2  Modulation
     * @param beta Modulation strength
     * @param func Waveform function
     * @return
     */
    static public Wave16 fmod(int samplingrate, int samples, double freq, double freq2, int beta,
                                       Function<Double, Double> func)

    {
        Wave16 out = new Wave16(samples, samplingrate);

        for (int x = 0; x < samples; x++)
        {
            out.data[x] = (float)(Wave16.MAX_VALUE * func.apply(x * Wave16.TWOPI / samplingrate * freq
                    - beta * sin(x * Wave16.TWOPI / samplingrate * freq2)));
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

}
