package com.WaveCreator;

import com.WaveCreator.Functions.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * To change this template use File | Settings | File Templates.
 */
public class Wave16 implements Serializable
{
    /**
     * Upper level constant
     */
    public static final double MAX_VALUE = Short.MAX_VALUE;
    /**
     * Lower level constant
     */
    public static final double MIN_VALUE = Short.MIN_VALUE;
    /**
     * Circle constant PI
     */
    public static final double PI = Math.PI;
    /**
     *
     */
    private static final long serialVersionUID = 3070090589210322951L;
    /**
     * Separated generator functions
     */
    public final FunctionsGenerators functionsGenerators = new FunctionsGenerators(this);
    /**
     * Separated two-wave modification functions
     */
    public final FunctionsTwoWaves functionsTwoWaves = new FunctionsTwoWaves(this);
    /**
     * Separated FFT functions: These will be listed in FFT functions menu
     */
    public final FunctionsFFT functionsFFT = new FunctionsFFT(this);
    /**
     * Separated coder functions: These will be listed in wave functions menu
     */
    public final FunctionsBinary functionsBinary = new FunctionsBinary(this);
    /**
     * Separated sample deletion functions: These will be listed in wave
     * functions menu
     */
    public final FunctionsDeletions functionsDeletions = new FunctionsDeletions(this);
    /*
     * Separated single-wave modification functions: These will be listed in wave functions menu
     */
    public final FunctionsSpecialEffects functionsSpecialEffects = new FunctionsSpecialEffects(this);
    /**
     * Math related functions
     */
    public final FunctionsMathematical functionsMathematical = new FunctionsMathematical(this);
    /**
     * Filter functions
     */
    public final FunctionsFilters functionsFilters = new FunctionsFilters(this);
    /**
     * Amplitude manipulating functions
     */
    public final FunctionsAmplitude functionsAmplitude = new FunctionsAmplitude(this);
    /**
     * Sample length manipulating functions
     */
    public final FunctionsLength functionsLength = new FunctionsLength(this);
    /**
     * Test candidates
     */
    public final FunctionsTesting functionsTesting = new FunctionsTesting(this);
    public final FunctionsReorder functionsReorder = new FunctionsReorder(this);
    /**
     * Data array that holds sampling data
     */
    public double[] data;
    /**
     * Sampling rate
     */
    public int samplingRate;
    /**
     * Optional name of this wave
     */
    public String name = "unnamed";

    /**
     * Builds a new Wave16 object
     *
     * @param size Size of array
     * @param rate Sampling rate
     */
    public Wave16 (int size, int rate)
    {
        data = new double[size];
        samplingRate = rate;
        //name = Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public Wave16 ()
    {
        data = null;
        samplingRate = 0;
    }

    public Wave16 (byte[] in, int rate, int bytesInBuffer)
    {
        double[] d = new double[bytesInBuffer / 2];
        for (int s = 0; s < bytesInBuffer; s += 2)
        {
            int i = (in[s] & 0xff | in[s + 1] << 8) & 0xffff;
            d[s / 2] = (double) (short) i;
        }
        data = d;
        samplingRate = rate;
    }

    public Wave16 (double[] d, int rate)
    {
        data = d;
        samplingRate = rate;
    }

    public Wave16 (short[] sh, int rate)
    {
        double[] dd = new double[sh.length];
        for (int s = 0; s < dd.length; s++)
        {
            dd[s] = sh[s];
        }
        data = dd;
        samplingRate = rate;
    }

    public Wave16 (Integer[] d, int rate)
    {
        double[] dd = new double[d.length];
        for (int s = 0; s < d.length; s++)
        {
            dd[s] = d[s];
        }
        data = dd;
        samplingRate = rate;
    }

    public Wave16 (int[] d, int rate)
    {
        double[] dd = new double[d.length];
        for (int s = 0; s < d.length; s++)
        {
            dd[s] = (double) d[s];
        }
        data = dd;
        samplingRate = rate;
    }

    /**
     * Puts sampling objects together First object determines sampling rate
     *
     * @param in Array of sampling objects
     * @return Concatenated sampling object
     */
    static public Wave16 combineAppend (Wave16... in)
    {
        int total = 0;
        for (Wave16 anIn : in)
        {
            total = total + anIn.data.length;
        }
        Wave16 out = new Wave16(total, in[0].samplingRate);
        int pos = 0;
        for (Wave16 anIn : in)
        {
            System.arraycopy(anIn.data, 0, out.data, pos, anIn.data.length);
            pos += anIn.data.length;
        }
        return out;
    }

    public int maxIndex ()
    {
        double d = Double.MIN_VALUE;
        int res = 0;
        for (int s = 0; s < data.length; s++)
        {
            if (data[s] > d)
            {
                d = data[s];
                res = s;
            }
        }
        return res;
    }

    public int minIndex ()
    {
        double d = Double.MAX_VALUE;
        int res = 0;
        for (int s = 0; s < data.length; s++)
        {
            if (data[s] < d)
            {
                d = data[s];
                res = s;
            }
        }
        return res;
    }

    public double[][] createQuaraticMatrix ()
    {
        Wave16 w1 = functionsLength.stretchToQuadratic();
        int sqr = (int) Math.sqrt(w1.data.length);
        double[][] ret = new double[sqr][sqr];
        for (int i = 0; i < sqr; i++)
        {
            System.arraycopy(w1.data, i * sqr, ret[i], 0, sqr);
        }

        return ret;
    }

    public Wave16 fromMatrix (double[][] mat)
    {
        double[] d = new double[mat.length * mat.length];
        for (int i = 0; i < mat.length; i++)
        {
            System.arraycopy(mat[i], 0, d, i * mat.length, mat.length);
        }
        return new Wave16(d, samplingRate);
    }

    /**
     * Build a new SamplingData16 object from this one All samples are copies of
     * this object
     *
     * @return The new object
     */
    public Wave16 copy ()
    {
        Wave16 out = createEmptyCopy();
        System.arraycopy(data, 0, out.data, 0, data.length);
        return out;
    }

    /**
     * Local factory function that builds a new SamplingData16 object from this
     * one All samples are empty
     *
     * @return The new object
     */
    public Wave16 createEmptyCopy ()
    {
        return new Wave16(data.length, samplingRate);
        //out.setName(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Gives an optional name
     *
     * @param n The name
     * @return This Wave16 object
     */
    public Wave16 setName (String n)
    {
        name = n;
        return this;
    }

    /**
     * Returns whole array as 'short' values
     *
     * @return The new 'short' array
     */
    public short[] toShortArray ()
    {
        short[] res = new short[data.length];
        for (int s = 0; s < data.length; s++)
        {
            res[s] = (short) data[s];
        }
        return res;
    }

    public int[] toIntArray ()
    {
        int[] res = new int[data.length];
        for (int s = 0; s < data.length; s++)
        {
            res[s] = (int) data[s];
        }
        return res;
    }

    /**
     * Returns one array element as short array
     *
     * @param idx array index
     * @return Short value
     */
    public short getShortAt (int idx)
    {
        return (short) data[idx];
    }

    /**
     * Inserts Wave16 <b>in</b> at position <b>pos</b>
     * Increases number of samples
     *
     * @param in  Sampling object to insert
     * @param pos Position to insert
     * @return The new (bigger) sampling object
     */
    public Wave16 combineInsert (Wave16 in, int pos)
    {
        int total = data.length + in.data.length;
        Wave16 out = new Wave16(total, samplingRate);

        System.arraycopy(data, 0, out.data, 0, pos - 1);
        System.arraycopy(in.data, 0, out.data, pos - 1, in.data.length);
        System.arraycopy(data, pos, out.data, in.data.length + pos - 1, total - in.data.length - pos);

        return out;
    }

    /**
     * Combines sampling arrays by calculating the average All sampling arrays
     * must be equal in length
     *
     * @param arr array of sampling objects
     * @return The combination
     */
    public Wave16 combineArithmeticAverage (Wave16[] arr)
    {
        Wave16 res = new Wave16(arr[0].data.length, arr[0].samplingRate);

        for (int s = 0; s < res.data.length; s++)
        {
            double v = 0;
            for (Wave16 anArr : arr)
            {
                v = v + anArr.data[s];
            }
            res.data[s] = v / arr.length;
        }
        return res;
    }

    /**
     * Combines sampling arrays by taking one sample from each array All
     * sampling arrays must be equal in length
     *
     * @param arr array of sampling objects
     * @return The combination
     */
    public Wave16 combineMix (Wave16[] arr)
    {
        Wave16 res = new Wave16(arr[0].data.length * arr.length, arr[0].samplingRate);
        int ctr = 0;
        for (int s = 0; s < arr[0].data.length; s++)
        {
            for (Wave16 anArr : arr)
            {
                res.data[ctr] = anArr.data[s];
                ctr++;
            }
        }
        return res;
    }

    /**
     * Implements the standard toString
     *
     * @return A string describing this object
     */
    @Override
    public String toString ()
    {
        Wave16AmplitudeInfo inf = getAmplitude();
        return new StringBuilder()
                .append(name)
                .append(" size:")
                .append(data.length)
                .append(" rate:")
                .append(samplingRate)
                .append(" min:")
                .append(inf.min)
                .append(" max:")
                .append(inf.max)
                .append(" span:")
                .append(inf.span)
                .toString();
    }

    // Gets information about amplitudes of this Wave16
    public Wave16AmplitudeInfo getAmplitude ()
    {
        return new Wave16AmplitudeInfo(this);
    }

    /**
     * Get integer array at specific point intervals
     *
     * @param parts partition parameter
     * @return An array of integers
     */
    public int[] getIntegerOffsetPoints (int parts)
    {
        ArrayList<Integer> list = new ArrayList<>();
        double step = ((double) data.length - 1.0) / ((double) parts - 1.0) + (1.0 / (double) parts);
        for (double s = 0; s < data.length; s += step)
        {
            list.add((int) (double) data[(int) s]);
        }
        int[] arr = new int[list.size()];
        for (int s = 0; s < list.size(); s++)
        {
            arr[s] = list.get(s);
        }
        return arr;
    }
}
