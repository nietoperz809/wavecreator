package com.WaveCreator;

import java.util.HashMap;
import com.WaveCreator.Functions.*;
/**
 * Creates DTMF samples
 */
public class DTMF
{
    /**
     * Sampling rate of all samples
     */
    private final int samplingRate;
    /**
     * delay between numbers
     */
    private final int delay;

    /**
     * Map of all samples
     */
    private final HashMap<Character, Wave16> wavemap = new HashMap<Character, Wave16>();
    

    /**
     * Main Constructor: Builds all DTMF samples
     * @param rate         Sampling rate
     * @param samples      Samples per number
     * @param delaysamples Number of delay samples
     */
    private DTMF(int rate, int samples, int delaysamples)
    {
        samplingRate = rate;
        Wave16 s1 = FunctionsGenerators.curveSine(samplingRate, new double[]{1209, 697}, samples);
        Wave16 s2 = FunctionsGenerators.curveSine(samplingRate, new double[]{1336, 697}, samples);
        Wave16 s3 = FunctionsGenerators.curveSine(samplingRate, new double[]{1477, 697}, samples);
        Wave16 SA = FunctionsGenerators.curveSine(samplingRate, new double[]{1633, 697}, samples);

        Wave16 s4 = FunctionsGenerators.curveSine(samplingRate, new double[]{1209, 770}, samples);
        Wave16 s5 = FunctionsGenerators.curveSine(samplingRate, new double[]{1336, 770}, samples);
        Wave16 s6 = FunctionsGenerators.curveSine(samplingRate, new double[]{1477, 770}, samples);
        Wave16 SB = FunctionsGenerators.curveSine(samplingRate, new double[]{1633, 770}, samples);

        Wave16 s7 = FunctionsGenerators.curveSine(samplingRate, new double[]{1209, 770}, samples);
        Wave16 s8 = FunctionsGenerators.curveSine(samplingRate, new double[]{1336, 770}, samples);
        Wave16 s9 = FunctionsGenerators.curveSine(samplingRate, new double[]{1477, 770}, samples);
        Wave16 SC = FunctionsGenerators.curveSine(samplingRate, new double[]{1633, 770}, samples);

        Wave16 sstar = FunctionsGenerators.curveSine(samplingRate, new double[]{1209, 941}, samples);
        Wave16 s0 = FunctionsGenerators.curveSine(samplingRate, new double[]{1336, 941}, samples);
        Wave16 ssharp = FunctionsGenerators.curveSine(samplingRate, new double[]{1477, 941}, samples);
        Wave16 SD = FunctionsGenerators.curveSine(samplingRate, new double[]{1633, 941}, samples);

        wavemap.put('1', s1);
        wavemap.put('2', s2);
        wavemap.put('3', s3);
        wavemap.put('A', SA);
        wavemap.put('4', s4);
        wavemap.put('5', s5);
        wavemap.put('6', s6);
        wavemap.put('B', SB);
        wavemap.put('7', s7);
        wavemap.put('8', s8);
        wavemap.put('9', s9);
        wavemap.put('C', SC);
        wavemap.put('*', sstar);
        wavemap.put('0', s0);
        wavemap.put('#', ssharp);
        wavemap.put('D', SD);
        
        delay = delaysamples;
    }

    /**
     * Secondary constructor: Calls main constructor but without delay value
     * @param rate    Sampling rate
     * @param samples Number of samples per DTMF tone
     */
    public DTMF(int rate, int samples)
    {
        this(rate, samples, 0);
    }

    /**
     * Creates sampling object from string that contains valid key IDs
     * @param in Input string
     * @return Sampling object
     */
    public Wave16 dtmfFromString(String in)
    {
        Wave16[] inWave;
        if (delay == 0)
        {
            inWave = new Wave16[in.length()];
            for (int s = 0; s < in.length(); s++)
            {
                inWave[s] = wavemap.get(in.charAt(s));
            }
        }
        else
        {
            inWave = new Wave16[in.length() * 2];
            Wave16 delWave = new Wave16(delay, samplingRate);
            for (int s = 0; s < in.length(); s++)
            {
                inWave[s * 2] = wavemap.get(in.charAt(s));
                inWave[s * 2 + 1] = delWave;
            }
        }
        return new Wave16().combineAppend(inWave);
    }
}
