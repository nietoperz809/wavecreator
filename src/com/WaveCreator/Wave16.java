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
     * Separated sample deletion functions: These will be listed in wave functions menu
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
     * Builds a new Wave16 object
     * @param size Size of array
     * @param rate Sampling rate
     */
    public Wave16(int size, int rate)
    {
        data = new double[size];
        samplingRate = rate;
        //name = Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public Wave16()
    {
        data = null;
        samplingRate = 0;
    }

    public Wave16 (byte[] in, int rate, int bytesInBuffer)
    {
        double[] d = new double[bytesInBuffer/2];
        for (int s=0; s<bytesInBuffer; s+=2)
        {
            int i = (in[s] & 0xff | in[s+1]<<8) & 0xffff;
            d[s/2] = (short)i;
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

    public Wave16(Double[] d, int rate)
    {
        double[] dd = new double[d.length];
        for (int s = 0; s < d.length; s++)
        {
            dd[s] = d[s];
        }
        data = dd;
        samplingRate = rate;
    }

    public Wave16(Integer[] d, int rate)
    {
        double[] dd = new double[d.length];
        for (int s = 0; s < d.length; s++)
        {
            dd[s] = d[s];
        }
        data = dd;
        samplingRate = rate;
    }

    public Wave16(int[] d, int rate)
    {
        double[] dd = new double[d.length];
        for (int s = 0; s < d.length; s++)
        {
            dd[s] = d[s];
        }
        data = dd;
        samplingRate = rate;
    }

    public int maxIndex()
    {
        double d = Double.MIN_VALUE;
        int res = 0;
        for (int s=0; s<data.length; s++)
        {
            if (data[s] > d)
            {
                d = data[s];
                res = s;
            }
        }
        return res;
    }

    public int minIndex()
    {
        double d = Double.MAX_VALUE;
        int res = 0;
        for (int s=0; s<data.length; s++)
        {
            if (data[s] < d)
            {
                d = data[s];
                res = s;
            }
        }
        return res;
    }

    public double[][] createQuaraticMatrix()
    {
        Wave16 w1 = functionsLength.stretchToQuadratic();
        int sqr = (int)Math.sqrt(w1.data.length);
        double[][] ret = new double[sqr][sqr];
        for (int i=0; i<sqr; i++)
        {
            System.arraycopy(w1.data, i * sqr, ret[i], 0, sqr);
        }
        return ret;
    }

    public Wave16 fromMatrix (double[][] mat)
    {
        double[] d = new double[mat.length*mat.length];
        for (int i=0; i<mat.length; i++)
        {
            System.arraycopy(mat[i], 0, d, i * mat.length, mat.length);
        }
        return new Wave16 (d, samplingRate);
    }

    /**
     * Local factory function that builds a new SamplingData16 object from this one
     * All samples are empty
     * @return The new object
     */
    public Wave16 createEmptyCopy()
    {
        return new Wave16(data.length, samplingRate);
        //out.setName(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Calculates next 2's power of input value
     * Thus, 127 gives 128, 128 gives 128 and 129 gives 256
     * @param in The input value
     * @return The 2's power
     */
    public int nextPowerOfTwo(int in)
    {
        return (int) Math.pow(2.0, Math.ceil(Math.log(in) / Math.log(2.0)));
    }

    /**
     * Build a new SamplingData16 object from this one
     * All samples are copies of this object
     * @return The new object
     */
    public Wave16 copy()
    {
        Wave16 out = createEmptyCopy();
        System.arraycopy(data, 0, out.data, 0, data.length);
        return out;
    }

    /**
     * Gives an optional name
     * @param n The name
     * @return This Wave16 object
     */
    public Wave16 setName(String n)
    {
        name = n;
        return this;
    }

    /**
     * Returns whole array as 'short' values
     * @return The new 'short' array
     */
    public short[] toShortArray()
    {
        short[] res = new short[data.length];
        for (int s = 0; s < data.length; s++)
        {
            res[s] = (short) data[s];
        }
        return res;
    }

    public int[] toIntArray()
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
     * @param idx array index
     * @return Short value
     */
    public short getShortAt(int idx)
    {
        return (short) data[idx];
    }

//    /**
//     * Prints array as derive compatible vector
//     * @param ps PrintStream that receives output
//     */
//    public void printAsVector(PrintStream ps)
//    {
//        ps.print('[');
//        for (int s = 0; s < data.length; s++)
//        {
//            ps.printf("[%d,%f]", s, data[s]);
//            if (s != data.length - 1)
//            {
//                ps.print(",");
//            }
//        }
//        ps.println(']');
//    }

//    public void printIndexesAsDoubleCArray (PrintStream ps)
//    {
//        ps.print("double array[] = {");
//        for (int s=0; s<data.length; s++)
//        {
//            ps.printf ("%d.0", s);
//            if (s != data.length-1)
//                ps.print(", ");
//        }
//        ps.println("};");
//    }
//
//
//    public void printAsDoubleCArray (PrintStream ps)
//    {
//        ps.print("double array[] = {");
//        for (int s=0; s<data.length; s++)
//        {
//            ps.printf ("%f.0", data[s]);
//            if (s != data.length-1)
//                ps.print(", ");
//        }
//        ps.println("};");
//    }



//    /**
//     * Cuts of samples that are over or below <b>absvalue</b>
//     * @param absvalue Cut-off value
//     * @return The new sampling data
//     */
//    public Wave16 cut (int absvalue)
//    {
//        Wave16 out = createEmptyCopy();
//        for (int s=0; s<data.length; s++)
//        {
//            double sign = Math.signum(data[s]);
//            double v = Math.abs(data[s]);
//            if (v > absvalue)
//                out.data[s] = sign*absvalue;
//            else
//                out.data[s] = data[s];
//        }
//        return out;
//    }

//    private double[] makeSplineConstants ()
//    {
//        int i,k;
//        double p,qn,sig,un;
//        double[] u = new double[data.length];
//        double[] y2 = new double[data.length];
//        y2[0]=0.0;
//        u[0]=0.0;
//        for (i=1;i<=(data.length-2);i++)
//        {
//            sig=((double)i-(double)(i-1))/((double)(i+1)-(double)(i-1));
//            p=sig*y2[i-1]+2.0;
//            y2[i]=(sig-1.0)/p;
//            u[i] = (data[i + 1] - data[i]) / ((double) (i + 2) - (double) (i + 1)) - (data[i] - data[i - 1]) / ((double) (i + 1) - (double) i);
//            u[i]=(6.0*u[i]/((double)(i+2)-(double)(i))-sig*u[i-1])/p;
//        }
//        qn=0.0;
//        un=0.0;
//        y2[data.length-1]=(un-qn*u[data.length-2])/(qn*y2[data.length-2]+1.0);
//        for (k=data.length-2;k>=0;k--)
//        {
//            y2[k]=y2[k]*y2[k+1]+u[k];
//        }
//
//        return y2;
//    }

//    private double splineFunc (double y2a[], double x)
//    {
//        int klo,khi,k;
//        double h,b,a;
//        klo=1;
//        khi=data.length;
//        while (khi-klo > 1)
//        {
//            k=(khi+klo) >>> 1;
//            if ((double)(k) > x)
//                khi=k;
//            else
//                klo=k;
//        }
//        h=(double)(khi)-(double)(klo);
//        if (h == 0.0)
//        {
//            System.exit(0);
//        }
//        a=((double)(khi-1)-x)/h;
//        b=(x-(double)(klo-1))/h;
//        return (a * data[klo - 1]) + (b * data[khi - 1]) + ((((((a * a * a) - a) * y2a[klo - 1]) + (((b * b * b) - b) * y2a[khi - 1])) * h * h) / 6.0);
//    }

//    private short integrateSimpson (int a, int b)
//    {
//        double sum = 0;
//        for (int s=a; s<b; s++)
//        {
//            int x1 = (s)%data.length;
//            int x2 = (s+1)%data.length;
//            int x3 = (s+2)%data.length;
//            double f1 = data[x1];
//            double f2 = data[x2];
//            double f3 = data[x3];
//            //sum = sum + ((f1+4.0*f2+f3)/6.0);
//            sum = sum + (s*f1+(s+1)*f2+(s+2)*f3);
//        }
//        return (short)sum;
//    }

//    private double qgaus (double a, double b, double[] splineconst)
//    {
//        int j;
//        double xr,xm,dx,s;
//        final double x[]={0.0,0.1488743389,0.4333953941,
//            0.6794095682,0.8650633666,0.9739065285};
//        final double w[]={0.0,0.2955242247,0.2692667193,
//            0.2190863625,0.1494513491,0.0666713443};
//        xm=0.5*(b+a);
//        xr=0.5*(b-a);
//        s=0;
//        for (j=1;j<=5;j++)
//        {
//            dx=xr*x[j];
//            s += w[j]*(splineFunc(splineconst, xm+dx)+splineFunc(splineconst, xm-dx));
//        }
//        return s * xr;
//    }

//    public Wave16 getInterpolatedSampingData()
//    {
//        Wave16 out = createEmptyCopy();
//        double[] splineconst = makeSplineConstants();
//        for (int s=0; s<data.length; s++)
//        {
//            out.data[s] = (short)splineFunc (splineconst, s);
//        }
//        return out;
//    }

    /**
     * Fit values into defined range
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValues(double[] in)
    {
        double out[] = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc (in);

        // Calc absolute span in short range
        double div = am.span / (MAX_VALUE - MIN_VALUE);
        // Divide them and shift into 'short' range
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = in[s] / div + MIN_VALUE - am.min;
            // Force forbidden values to zero
            if (Double.isInfinite(out[s]) || Double.isNaN(out[s]))
                out[s] = 0.0;
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValuesToByteRange(double[] in)
    {
        double out[] = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc (in);

        // Calc absolute span in short range
        double div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        // Divide them and shift into 'short' range
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValuesToPositiveByteRange(double[] in)
    {
        double out[] = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc (in);

        // Calc absolute span in short range
        double div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        // Divide them and shift into 'short' range
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = 128 + Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }

    /**
     * Inserts Wave16 <b>in</b> at position <b>pos</b>
     * Increases number of samples
     * @param in  Sampling object to insert
     * @param pos Position to insert
     * @return The new (bigger) sampling object
     */
    public Wave16 combineInsert(Wave16 in, int pos)
    {
        int total = data.length + in.data.length;
        Wave16 out = new Wave16(total, samplingRate);

        System.arraycopy(data, 0, out.data, 0, pos - 1);
        System.arraycopy(in.data, 0, out.data, pos - 1, in.data.length);
        System.arraycopy(data, pos, out.data, in.data.length + pos - 1, total - in.data.length - pos);

        return out;
    }

//    public Wave16 curvePulse(int samplingrate, int frequency)
//    {
//        int len = samplingrate / frequency;
//        Wave16 t = new Wave16(len, samplingrate);
//        for (int x = 0; x < len; x++)
//        {
//            double v = Math.asin(Math.sin(x * PI / samplingrate * frequency)) / Math.asin(1);
//            double w = MAX_VALUE * v * Math.pow(-1, 2 * x * frequency / samplingrate);
//            t.data[x] = -w + w % (MAX_VALUE - (double) frequency / 1.46488845);
//        }
//        return t;
//    }

    /**
     * Combines sampling arrays by calculating the average
     * All sampling arrays must be equal in length
     * @param arr array of sampling objects
     * @return The combination
     */
    public Wave16 combineArithmeticAverage(Wave16[] arr)
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
     * Combines sampling arrays by taking one sample from each array
     * All sampling arrays must be equal in length
     * @param arr array of sampling objects
     * @return The combination
     */
    public Wave16 combineMix(Wave16[] arr)
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
     * Puts sampling objects together
     * First object determines sampling rate
     * @param in Array of sampling objects
     * @return Concatenated sampling object
     */
    public Wave16 combineAppend(Wave16[] in)
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

    /**
     * Implements the standard toString
     * @return A string describing this object
     */
    @Override
    public String toString()
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
    public Wave16AmplitudeInfo getAmplitude()
    {
        return new Wave16AmplitudeInfo(this);
    }

    /**
     * Get integer array at specific point intervals
     * @param parts partition parameter
     * @return An array of integers
     */
    public int[] getIntegerOffsetPoints(int parts)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        double step = ((double) data.length - 1.0) / ((double) parts - 1.0) + (1.0 / (double) parts);
        for (double s = 0; s < data.length; s += step)
        {
            list.add((int) data[(int) s]);
        }
        int[] arr = new int[list.size()];
        for (int s = 0; s < list.size(); s++)
        {
            arr[s] = list.get(s);
        }
        return arr;
    }
}

