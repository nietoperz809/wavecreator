package com.WaveCreator.FFT;

import com.WaveCreator.Wave16;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DFT
{
    /**
     * Get frequencies in first n samples of Wave
     * @param in Wave object
     * @param maxsamples num of samples to use
     * @return List of frequencies
     */
    public static List<Float> processWave (Wave16 in, int maxsamples)
    {
        int len = Math.min (in.data.length, maxsamples);
        double[] dat = new double[len];
        for (int s=0; s<len; s++)
            dat[s] = in.data[s];
        float[] outR = new float[len];
        float[] outI = new float[len];

        // dft (in.data, outR, outI);
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex resultC[] = fft.transform (dat, TransformType.FORWARD);

        double results[] = new double[outR.length];
        for (int i = 0; i < resultC.length; i++)
        {
            double real = resultC[i].getReal();
            double imaginary = resultC[i].getImaginary();
            results[i] = Math.sqrt(real*real + imaginary*imaginary);
        }

//        for (int i = 0; i < outR.length; i++)
//        {
//            results[i] = Math.sqrt(outR[i]*outR[i] + outI[i]*outI[i]);
//        }
        return  process(results, in.samplingRate, len, 4);
    }

    private static void dft (float[] inR, float[] outR, float[] outI)
    {
        for (int k = 0; k < outR.length; k++)
        {
            for (int t = 0; t < outR.length; t++)
            {
                outR[k] += inR[t] * Math.cos(2 * Math.PI * t * k / outR.length);
                outI[k] -= inR[t] * Math.sin(2 * Math.PI * t * k / outR.length);
            }
        }
    }

    private static List<Float> process (double results[], float sampleRate, int numSamples, int sigma)
    {
        double average = Arrays.stream(results).sum();
        average = average / results.length;

        double sums = 0;
        for (double result : results)
        {
            sums += (result - average) * (result - average);
        }

        double stdev = Math.sqrt(sums / (results.length - 1));

        ArrayList<Float> found = new ArrayList<>();
        double max = Integer.MIN_VALUE;
        int maxF = -1;
        for (int f = 0; f < results.length / 2; f++)
        {
            if (results[f] > average + sigma * stdev)
            {
                if (results[f] > max)
                {
                    max = results[f];
                    maxF = f;
                }
            }
            else
            {
                if (maxF != -1)
                {
                    found.add(maxF * sampleRate / numSamples);
                    max = Integer.MIN_VALUE;
                    maxF = -1;
                }
            }
        }

        return (found);
    }
}
