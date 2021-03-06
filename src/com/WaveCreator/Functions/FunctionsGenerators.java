package com.WaveCreator.Functions;

import com.WaveCreator.*;
import com.WaveCreator.Helpers.Tools;
import com.WaveCreator.Math.PiDigits;
import com.WaveCreator.Helpers.DynamicFunction;
import com.WaveCreator.TinySynth.TinySynth;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 10:20:44
 */
public final class FunctionsGenerators extends Functions
{
    public FunctionsGenerators(Wave16 base)
    {
        super (base);
    }

    public Wave16 copy()
    {
        return m_base.copy();
    }

    private static int maxKey, key, range;
    private static float whiteValues[];
    private static float maxSumEver;

    private static void initPink()
    {
        maxKey = 0x1f;
        range = 128;
        maxSumEver = 90;
        key = 0;
        whiteValues = new float[6];
        for (int i = 0; i < 6; i++)
            whiteValues[i] = ((float) Math.random() * Long.MAX_VALUE) % (range / 6);
    }

    // return a pink noise value
    private static float pink()
    {
        int last_key = key;
        float sum;

        key++;
        if (key > maxKey) key = 0;
        // Exclusive-Or previous value with current value. This gives
        // a list of bits that have changed.
        int diff = last_key ^ key;
        sum = 0;
        for (int i = 0; i < 6; i++)
        {
            // If bit changed get new random number for corresponding
            // white_value
            if ((diff & (1 << i)) != 0)
            {
                whiteValues[i] = ((float) Math.random() * Long.MAX_VALUE) % (range / 6);
            }
            sum += whiteValues[i];
        }
        if (sum > maxSumEver) maxSumEver = sum;
        sum = 2f * (sum / maxSumEver) - 1f;
        return sum;
    }

    static public Wave16 pinkNoise (@ParamDesc("Sampling rate") int samplingrate,
                                    @ParamDesc("Number of samples") int samples)
    {
        initPink();
        Wave16 t = new Wave16(samples, samplingrate);
        for (int s=0; s<t.data.length; s++)
        {
            t.data[s] = pink();
        }
        t.data = Tools.fitValues(t.data);
        return t;
    }

    static public Wave16 tinySynth (@ParamDesc("Text") String text)
    {
        short[] dat = TinySynth.generate(text);
        Wave16 wav = new Wave16(dat, TinySynth.SAMPLE_FREQUENCY);
        return wav;
    }

    static public Wave16 dtmf (@ParamDesc("Phone number")String numbers)
    {
        return new DTMF(10000, 1500).dtmfFromString(numbers);
    }

    static public Wave16 fromString (@ParamDesc("String used as values") String str,
                              @ParamDesc("Sampling rate") int rate)
    {
        short[] arr = new short[str.length()];
        for (int s=0; s<str.length(); s++)
        {
            arr[s] = (short)str.charAt(s);
        }
        Wave16 wave = fromShortArray (arr, rate);
        wave.data = Tools.fitValues(wave.data);
        return wave;
    }

    static public Wave16 fromPiDigits (@ParamDesc("First digit of PI sequence") int first,
                                @ParamDesc("Last digit of PI sequence") int last,
                                @ParamDesc("Sampling rate") int rate)
    {
        String pi = PiDigits.getPiString (first, last);
        return fromString (pi, rate); 
    }

    /**
     * Creates a sampling object of equal y-values
     * @param samplingrate Sampling rate
     * @param samples      Number of samples
     * @param value        Value of all Y-values
     * @return The new sampling object
     */
    static public Wave16 constant(@ParamDesc("Sampling rate") int samplingrate,
                           @ParamDesc("Number of samples")int samples,
                           @ParamDesc("Constant value") double value)
    {
        return new Wave16(0, samplingrate).functionsLength.insertSamples(0, samples, value);
    }

    /**
     * Creates new wave object from short array
     * @param array The short array
     * @param rate  The sampling rate
     * @return The new object
     */
    static public Wave16 fromShortArray(@ParamDesc("Array of values")short[] array,
                                 @ParamDesc("Sampling rate") int rate)
    {
        Wave16 out = new Wave16(array.length, rate);
        for (int s = 0; s < array.length; s++)
        {
            out.data[s] = array[s];
        }
        return out;
    }

    static public Wave16 fromDoubleArray(@ParamDesc("Array of values")float[] array,
                                        @ParamDesc("Sampling rate") int rate)
    {
        Wave16 out = new Wave16(array.length, rate);
        out.data = array;
        return out;
    }

    /**
     * Generates 16-bit array of very simple pseudo-random data
     * @param samplingrate The sampling rate
     * @param samples      number of samples
     * @return random array
     */
    static public Wave16 random (@ParamDesc("Sampling rate") int samplingrate,
                          @ParamDesc("Number of samples") int samples)
    {
        Wave16 t = new Wave16(samples, samplingrate);
        SecureRandom rnd = new SecureRandom();
        byte[] b = new byte[t.data.length*2];
        rnd.nextBytes (b);
        for (int s=0; s<b.length; s+=2)
        {
            t.data[s/2] = b[s] + (b[s+1]*256);
        }
        return t;
    }

    static public Wave16 curveBow(@ParamDesc("Sampling rate")int samplingrate,
                           @ParamDesc("Array of frequencies")int[] freq,
                           @ParamDesc("Number of Samples")int samples)
    {
        Wave16 out = new Wave16(samples, samplingrate);

        for (int x = 0; x < samples; x++)
        {
            float f = 0;
            for (int aFreq : freq)
            {
                int s = samplingrate / aFreq;  // s is number of samples for a whole curve
                int xx = x % s;
                f = (float) (f + (-Wave16.MAX_VALUE) * Math.sqrt(s * s - Math.pow(4 * xx + s * (Math.signum(s - 2 * xx) - 2), 2)) * Math.signum(2 * xx - s) / s);
            }
            out.data[x] = f / freq.length;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    static public Wave16 curveBow (@ParamDesc("Sampling rate") int samplingrate,
                            @ParamDesc("Frequency") int frequency)
    {
        int[] f = {frequency};
        int samples = samplingrate/frequency;
        return curveBow (samplingrate, f, samples);
    }


    static public Wave16 curvePulse(@ParamDesc("Sampling rate")int s,
                             @ParamDesc("Array of frequencies")double[] freq,
                             @ParamDesc("Number of samples")int samples)
    {
        return curveSquare (s, freq, samples).functionsMathematical.deriveAndFitValues();
    }

    static public Wave16 curvePulse (@ParamDesc("Sampling rate") int samplingrate,
                              @ParamDesc("Frequency") double frequency)
    {
        double[] f = {frequency};
        int samples = (int) (samplingrate/frequency);
        return curvePulse (samplingrate, f, samples);
    }


    /**
     * Makes an exponential curve
     * @param samplingrate Samplingrate
     * @param frequency    Frequency of curve
     * @return The new sampling array
     * @throws Exception thrown if somethinmg goes wrong
     */
    static public Wave16 curveExponential (@ParamDesc("Sampling rate") int samplingrate,
                                    @ParamDesc("Frequency") int frequency) throws Exception
    {
        int samples = samplingrate / frequency;
        Wave16[] w = new Wave16[4];
        Wave16 a = rampLogarithmicPlus(samplingrate, 1 + samples / 4).
                functionsAmplitude.multiply(0.5f).
                functionsAmplitude.shift(Wave16.MAX_VALUE / 2);
        w[0] = a.functionsDeletions.deleteSamplesFromEnd(1);
        w[1] = a.functionsReorder.reverse().functionsDeletions.deleteSamplesFromEnd(1);
        w[2] = a.functionsAmplitude.invert().functionsDeletions.deleteSamplesFromEnd(1);
        w[3] = a.functionsAmplitude.invert().functionsReorder.reverse().functionsDeletions.deleteSamplesFromEnd(1);
        return Wave16.combineAppend(w);
    }

    static public Wave16 curveSquareFromValues(@ParamDesc("Sampling rate")int samplingrate,
                                           @ParamDesc("Number of samples")int samples,
                                           @ParamDesc("Array of segments")int[] segments)
    {
        Wave16 out = new Wave16(samples, samplingrate);
        double div = (double) samples / segments.length;
        for (int n = 0; n < segments.length; n++)
        {
            for (double s = 0; s < div; s++)
            {
                out.data[(int) (n * div + s)] = segments[n];
            }
        }
        return out;
    }

    /**
     * Makes a wave of multiple Triangle curves
     * @param samplingrate The sampling rate
     * @param freq         The frequencies
     * @param samples      Number of samples of output array
     * @return the output array
     */
    static public Wave16 curveSquare (@ParamDesc("Sampling rate")int samplingrate,
                                 @ParamDesc("Array of frequencies")double[] freq,
                                 @ParamDesc("Number of samples")int samples)
    {
        return Primitives.apply(samplingrate,freq,samples, Primitives::squ);
    }

    /**
     * Creates a single square wave
     * @param samplingrate the sampling rate
     * @param frequency    the frequency
     * @return A single square Wave
     */
    static public Wave16 curveSquare (@ParamDesc("Sampling rate") int samplingrate,
                                  @ParamDesc("Frequency") double frequency)
    {
        double[] f = {frequency};
        int samples = (int) (samplingrate/frequency);
        return curveSquare (samplingrate, f, samples);
    }

    /**
     * Makes a sine wave that runs from Fstart to Fend
     * @param samplingrate Sampling rate
     * @param fstart       start frequency
     * @param fend         end frequency
     * @param samples      number of samples to generate
     * @return The new sampling data
     */
    static public Wave16 sweepSine(@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Starting frequency") int fstart,
                             @ParamDesc("Last frequency") int fend,
                             @ParamDesc("Number of samples") int samples)
    {
        return Primitives.sweep(samplingrate, fstart, fend, samples, Primitives::sin);
    }

    static public Wave16 sweepSine(@ParamDesc("Sampling rate") int samplingrate,
                                   @ParamDesc("Starting frequency") int fstart,
                                   @ParamDesc("Last frequency") int fend,
                                   @ParamDesc("Seconds for whole curve") double seconds)
    {
        double time = seconds * samplingrate;
        return sweepSine(samplingrate, fstart, fend, (int)time);
    }

    static public Wave16 sweepTriangle(@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Starting frequency") int fstart,
                             @ParamDesc("Last frequency") int fend,
                             @ParamDesc("Number of samples") int samples)
    {
        return Primitives.sweep(samplingrate, fstart, fend, samples, Primitives::tri);
    }

    static public Wave16 sweepTriangle(@ParamDesc("Sampling rate") int samplingrate,
                                   @ParamDesc("Starting frequency") int fstart,
                                   @ParamDesc("Last frequency") int fend,
                                   @ParamDesc("Seconds for whole curve") double seconds)
    {
        double time = seconds * samplingrate;
        return sweepTriangle(samplingrate, fstart, fend, (int)time);
    }

    static public Wave16 sweepSquare(@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Starting frequency") int fstart,
                             @ParamDesc("Last frequency") int fend,
                             @ParamDesc("Number of samples") int samples)
    {
        return Primitives.sweep(samplingrate, fstart, fend, samples, Primitives::squ);
    }

    static public Wave16 sweepPulse(@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Starting frequency") int fstart,
                             @ParamDesc("Last frequency") int fend,
                             @ParamDesc("Number of samples") int samples)
    {
        return sweepSquare(samplingrate, fstart, fend, samples).functionsMathematical.deriveAndFitValues();
    }

    /**
     * Creates a sequence of sine tones
     * @param rate Sampling rate
     * @param frequencies Array of frequencies
     * @param lengths Length of tone (in seconds)
     * @return The new sampling array
     */
    static public Wave16 sequenceSine (@ParamDesc("Sampling rate")int rate,
                                @ParamDesc("Array of frequencies")int[] frequencies,
                                @ParamDesc("Length of tones in seconds")double lengths[])
    {
        Wave16[] parts = new Wave16[frequencies.length];
        for(int s=0; s<parts.length; s++)
        {
            parts[s] = curveSine(rate, frequencies[s]);
            parts[s] = parts[s].functionsLength.appendSelf((int)(lengths[s]*frequencies[s]));
        }
        return Wave16.combineAppend(parts);
    }

    static public Wave16 sequenceSine (@ParamDesc("Sampling rate")int rate,
                                @ParamDesc("Array of frequencies")int[] frequencies,
                                @ParamDesc("Length of all tones in seconds")double length)
    {
        double[] l = new double[frequencies.length];
        Arrays.fill(l, length);
        return sequenceSine (rate, frequencies, l);
    }

//    static public Wave16 sequenceSine (@ParamDesc("Sampling rate")int rate,
//                                       @ParamDesc("Low frequency")double low,
//                                       @ParamDesc("High frequency")double high,
//                                       @ParamDesc("Entire Length in seconds")double length)
//    {
//        int step = 100;
//        double singlesampletime = length/rate;
//        double diff = Math.abs(high-low)/100;
//        int[] l = new int[step];
//        if (low == 0.0)
//            low = 0.01;
//        for (int s=0; s<step; s++)
//        {
//            l[s] = (int)low;
//            low += diff;
//        }
//        return sequenceSine (rate, l, singlesampletime);
//    }


    static public Wave16 sequenceSquare (@ParamDesc("Sampling rate")int rate,
                                     @ParamDesc("Array of frequencies")int[] frequencies,
                                     @ParamDesc("Length of tones in seconds")double lengths[])
    {
        Wave16[] parts = new Wave16[frequencies.length];
        for(int s=0; s<parts.length; s++)
        {
            parts[s] = curveSquare(rate, frequencies[s]);
            parts[s] = parts[s].functionsLength.appendSelf((int)(lengths[s]*frequencies[s]));
        }
        return Wave16.combineAppend(parts);
    }

    static public Wave16 sequenceSquare (@ParamDesc("Sampling rate")int rate,
                                @ParamDesc("Array of frequencies")int[] frequencies,
                                @ParamDesc("Length of all tones in seconds")double length)
    {
        double[] l = new double[frequencies.length];
        Arrays.fill(l, length);
        return sequenceSquare (rate, frequencies, l);
    }

    static public Wave16 sequenceTriangle (@ParamDesc("Sampling rate")int rate,
                                    @ParamDesc("Array of frequencies")int[] frequencies,
                                    @ParamDesc("Length of tones in seconds")double lengths[])
    {
        Wave16[] parts = new Wave16[frequencies.length];
        for(int s=0; s<parts.length; s++)
        {
            parts[s] = curveTriangle(rate, frequencies[s]);
            parts[s] = parts[s].functionsLength.appendSelf((int)(lengths[s]*frequencies[s]));
        }
        return Wave16.combineAppend(parts);
    }

    static public Wave16 sequenceTriangle (@ParamDesc("Sampling rate")int rate,
                                @ParamDesc("Array of frequencies")int[] frequencies,
                                @ParamDesc("Length of all tones in seconds")double length)
    {
        double[] l = new double[frequencies.length];
        Arrays.fill(l, length);
        return sequenceTriangle (rate, frequencies, l);
    }

    static public Wave16 sequencePulse (@ParamDesc("Sampling rate")int rate,
                                 @ParamDesc("Array of frequencies")int[] frequencies,
                                 @ParamDesc("Length of tones in seconds")double lengths[])
    {
        Wave16[] parts = new Wave16[frequencies.length];
        for(int s=0; s<parts.length; s++)
        {
            parts[s] = curvePulse(rate, frequencies[s]);
            parts[s] = parts[s].functionsLength.appendSelf((int)(lengths[s]*frequencies[s]));
        }
        return Wave16.combineAppend(parts);
    }

    static public Wave16 sequencePulse (@ParamDesc("Sampling rate")int rate,
                                @ParamDesc("Array of frequencies")int[] frequencies,
                                @ParamDesc("Length of all tones in seconds")double length)
    {
        double[] l = new double[frequencies.length];
        Arrays.fill(l, length);
        return sequencePulse (rate, frequencies, l);
    }

    
    /**
     * Generates wave object of multiple sine curves
     * @param samplingrate The sampling rate
     * @param freq         The frequencies
     * @param samples      Number of samples of output array
     * @return A single wave
     */
    static public Wave16 curveSine(@ParamDesc("Sampling rate")int samplingrate,
                            @ParamDesc("Number of samples")int samples,
                            @ParamDesc("Array of frequencies")double... freq)
    {
        return Primitives.apply(samplingrate,freq,samples, Primitives::sin);
    }

    static public Wave16 curveFModSine(@ParamDesc("Sampling rate")int samplingrate,
                                   @ParamDesc("Number of samples")int samples,
                                   @ParamDesc("frequency")double freq,
                                   @ParamDesc("Modulation freq")double freq2,
                                   @ParamDesc("Modulation index")int beta)

    {
        return Primitives.fmod(samplingrate, samples, freq, freq2, beta, Primitives::sin);
    }

    static public Wave16 curveFModSaw(@ParamDesc("Sampling rate")int samplingrate,
                                       @ParamDesc("Number of samples")int samples,
                                       @ParamDesc("frequency")double freq,
                                       @ParamDesc("Modulation freq")double freq2,
                                       @ParamDesc("Modulation index")int beta)

    {
        return Primitives.fmod(samplingrate, samples, freq, freq2, beta, Primitives::saw);
    }

    static public Wave16 curveFModSquare(@ParamDesc("Sampling rate")int samplingrate,
                                       @ParamDesc("Number of samples")int samples,
                                       @ParamDesc("frequency")double freq,
                                       @ParamDesc("Modulation freq")double freq2,
                                       @ParamDesc("Modulation index")int beta)

    {
        return Primitives.fmod(samplingrate, samples, freq, freq2, beta, Primitives::squ);
    }

    static public Wave16 curveFModTriangle(@ParamDesc("Sampling rate")int samplingrate,
                                         @ParamDesc("Number of samples")int samples,
                                         @ParamDesc("frequency")double freq,
                                         @ParamDesc("Modulation freq")double freq2,
                                         @ParamDesc("Modulation index")int beta)

    {
        return Primitives.fmod(samplingrate, samples, freq, freq2, beta, Primitives::tri);
    }

    /**
     * Generates wave object of multiple sinus cardinalis curves
     * @param samplingrate The sampling rate
     * @param freq         The frequencies
     * @param samples      Number of samples of output array
     * @param offset       offset on x axis
     * @return A single wave
     */
    static public Wave16 curveSinC(@ParamDesc("Sampling rate")int samplingrate,
                                   @ParamDesc("Number of samples")int samples,
                                   @ParamDesc("x-offset")int offset,
                                   @ParamDesc("Array of frequencies")float... freq)
    {
        Wave16 out = new Wave16(samples, samplingrate);
        for (int x = 0; x < samples; x++)
        {
            float f = 0;
            for (float aFreq : freq)
            {
                int n = x-offset;
                f = (float) (f + (Wave16.MAX_VALUE * Math.sin(n * Wave16.TWOPI / samplingrate * aFreq)/n));
            }
            out.data[x] = (float) (f / freq.length);
        }
        out = out.functionsAmplitude.multiply(3); //Tools.fitValues(out.data);
        return out;
    }

    // Experimental
    static public Wave16 curveUser(@ParamDesc("Sampling rate")int samplingrate,
                                   @ParamDesc("Number of samples")int samples,
                                   @ParamDesc("Frequency")int freq,
                                   @ParamDesc("Function")String func) throws Exception
    {
        Wave16 out = new Wave16(samples, samplingrate);

        DynamicFunction df = new DynamicFunction();
        df.createSource (func);
        df.compile();

        for (int x = 0; x < samples; x++)
        {
            out.data[x] = (float) df.runIt(x * Wave16.TWOPI / samplingrate * freq);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Generates a DUR chord
     * @param samplingrate  Samplingrate
     * @param startfreq Base frequency
     * @param samples Number of samples to generate
     * @return A new WAVE object
     */
    static public Wave16 sineDur (@ParamDesc("Sampling rate") int samplingrate,
                           @ParamDesc("Base frequency") double startfreq,
                           @ParamDesc("Number of samples") int samples)
    {
        double[] freq = {startfreq, startfreq*Math.pow (2.0,4.0/12.0), startfreq*Math.pow (2.0,7.0/12.0)};
        return curveSine (samplingrate, samples, freq);
    }

    /**
     * Generates a MOLL chord
     * @param samplingrate  Samplingrate
     * @param startfreq Base frequency
     * @param samples Number of samples to generate
     * @return A new WAVE object
     */
    static public Wave16 sineMoll (@ParamDesc("Sampling rate") int samplingrate,
                           @ParamDesc("Base frequency") double startfreq,
                           @ParamDesc("Number of samples") int samples)
    {
        double[] freq = {startfreq, startfreq*Math.pow (2.0,3.0/12.0), startfreq*Math.pow (2.0,7.0/12.0)};
        return curveSine (samplingrate, samples, freq);
    }

    /**
     * Makes a single sine wave
     * @param samplingrate The sampling rate
     * @param frequency    The frequency
     * @return A single wave
     */
    static public Wave16 curveSine (@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Frequency") double frequency)
    {
        double[] f = {frequency};
        int samples = (int)Math.round(0.5+samplingrate/frequency);
        return curveSine (samplingrate, samples, f);
    }

    /**
     * Makes a timed sine wave
     * @param samplingrate The sampling rate
     * @param frequency    The frequency
     * @param msecs        Length of output in milliseconds
     * @return A single wave
     */
    static public Wave16 curveSine (@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Frequency") double frequency,
                             @ParamDesc ("Milliseconds") int msecs)
    {
        double[] f = {frequency};
        int time = samplingrate / 1000 * msecs;
        return curveSine (samplingrate, time, f);
    }
   
    

    /**
     * Makes a wave of multiple Triangle curves
     * @param samplingrate The sampling rate
     * @param freq         The frequencies
     * @param samples      Number of samples of output array
     * @return the output array
     */
    static public Wave16 curveTriangle(@ParamDesc("Sampling rate")int samplingrate,
                                @ParamDesc("Array of frequencies")double[] freq,
                                @ParamDesc("Number of samples")int samples)
    {
        return Primitives.apply(samplingrate,freq,samples, Primitives::tri);
    }

    /**
     * Creates a single triangle wave
     * @param samplingrate the sampling rate
     * @param frequency    the frequency
     * @return A single triangle wave
     */
    static public Wave16 curveTriangle (@ParamDesc("Sampling rate") int samplingrate,
                                 @ParamDesc("Frequency") double frequency)
    {
        double[] f = {frequency};
        int samples = (int) (samplingrate/frequency);
        return curveTriangle (samplingrate, f, samples);
    }

    /**
     * Creates multiple sawtooth array
     * @param s       Sampling rate
     * @param freq    Frequencies
     * @param samples Number of samples
     * @return The new sampling object
     */
    static public Wave16 curveSaw (@ParamDesc("Sampling rate")int s,
                            @ParamDesc("Array of frequencies")double[] freq,
                            @ParamDesc("Number of samples")int samples)
    {
        return Primitives.apply(s,freq,samples, Primitives::saw);
    }


    /**
     * Creates a single sawtooth wave
     * @param samplingrate the sampling rate
     * @param frequency the frequency
     * @return A single sawtooth wave
     */
    static public Wave16 curveSaw (@ParamDesc("Sampling rate") int samplingrate,
                            @ParamDesc("Frequency") double frequency)
    {
        double[] f = {frequency};
        int samples = (int) (samplingrate/frequency);
        return curveSaw (samplingrate, f, samples);
    }

    /**
     * Generates ramp from ... to
     * @param rate    Sampling rate
     * @param start   First sample value
     * @param stop    Last sample value
     * @param samples Number of samples
     * @return New sampling array
     */
    static public Wave16 ramp (@ParamDesc("Sampling rate")int rate,
                        @ParamDesc("First value")int start,
                        @ParamDesc("Last value")int stop,
                        @ParamDesc("Number of samples")int samples)
    {
        float s = start;
        float diff = (float)(stop - start) / (samples - 1);
        Wave16 out = new Wave16(samples, rate);
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = s;
            s = s + diff;
        }
        return out;
    }

    /**
     * Creates a ramp from min to max
     * @param samplingrate Samplinggrate of new data
     * @param samples      Number of samples to create
     * @return New object
     */
    static public Wave16 rampPlus (@ParamDesc("Sampling rate")int samplingrate,
                            @ParamDesc("Number of samples") int samples)
    {
        return ramp(samplingrate, (int) Wave16.MIN_VALUE, (int) Wave16.MAX_VALUE, samples);
    }

    static public Wave16 rampExponentialPlus (@ParamDesc("Sampling rate")int samplingrate,
                                       @ParamDesc("Number of samples") int samples) throws Exception
    {
        Wave16 out = new Wave16(samples, samplingrate);
        OffsetGenerator gen = new OffsetGenerator(Wave16.MIN_VALUE, Wave16.MAX_VALUE, samples);

        for (int s = 0; s < samples; s++)
        {
            out.data[s] = (float) gen.getExponentialValue(s);
        }
        return out;
    }

    static public Wave16 rampLogarithmicPlus (@ParamDesc("Sampling rate")int samplingrate,
                                       @ParamDesc("Number of samples")int samples) throws Exception
    {
        return rampExponentialMinus (samplingrate, samples).functionsReorder.reverse();
    }

    /**
     * Creates a ramp from max to min
     * @param samplingrate Samplingrate of new data
     * @param samples      NUmber of samples to create
     * @return New object
     */
    static public Wave16 rampMinus (@ParamDesc("Sampling rate") int samplingrate,
                             @ParamDesc("Number of samples")int samples)
    {
        return ramp(samplingrate, (int) Wave16.MAX_VALUE, (int) Wave16.MIN_VALUE, samples);
    }

    static public Wave16 rampExponentialMinus (@ParamDesc("Sampling rate")int samplingrate,
                                        @ParamDesc("Number of samples") int samples) throws Exception
    {
        Wave16 out = new Wave16(samples, samplingrate);
        OffsetGenerator gen = new OffsetGenerator(Wave16.MIN_VALUE, Wave16.MAX_VALUE, samples);

        for (int s = 0; s < samples; s++)
        {
            out.data[s] = (float) (-gen.getExponentialValue(s) - 1);
        }
        return out;
    }

    static public Wave16 rampLogarithmicMinus (@ParamDesc("Sampling rate")int samplingrate,
                                        @ParamDesc("Number of samples")int samples) throws Exception
    {
        return rampExponentialPlus(samplingrate, samples).functionsReorder.reverse();
    }
}
