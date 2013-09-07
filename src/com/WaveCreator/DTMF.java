package com.WaveCreator;

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
    private final Wave16[] wavemap;
    /**
     * Map of characters that map to Wave16 objects
     */
    private final char[] charmap;

    /**
     * Main Constructor: Builds all DTMF samples
     * @param rate         Sampling rate
     * @param samples      Samples per number
     * @param delaysamples Number of delay samples
     */
    private DTMF(int rate, int samples, int delaysamples)
    {
        samplingRate = rate;
        Wave16 wv = new Wave16();
        Wave16 s1 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1209, 697}, samples);
        Wave16 s2 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1336, 697}, samples);
        Wave16 s3 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1477, 697}, samples);
        Wave16 SA = wv.functionsGenerators.curveSine(samplingRate, new double[]{1633, 697}, samples);

        Wave16 s4 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1209, 770}, samples);
        Wave16 s5 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1336, 770}, samples);
        Wave16 s6 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1477, 770}, samples);
        Wave16 SB = wv.functionsGenerators.curveSine(samplingRate, new double[]{1633, 770}, samples);

        Wave16 s7 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1209, 770}, samples);
        Wave16 s8 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1336, 770}, samples);
        Wave16 s9 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1477, 770}, samples);
        Wave16 SC = wv.functionsGenerators.curveSine(samplingRate, new double[]{1633, 770}, samples);

        Wave16 sstar = wv.functionsGenerators.curveSine(samplingRate, new double[]{1209, 941}, samples);
        Wave16 s0 = wv.functionsGenerators.curveSine(samplingRate, new double[]{1336, 941}, samples);
        Wave16 ssharp = wv.functionsGenerators.curveSine(samplingRate, new double[]{1477, 941}, samples);
        Wave16 SD = wv.functionsGenerators.curveSine(samplingRate, new double[]{1633, 941}, samples);

        wavemap = new Wave16[]{s1, s2, s3, SA, s4, s5, s6, SB, s7, s8, s9, SC, sstar, s0, ssharp, SD};
        charmap = new char[]{'1', '2', '3', 'A', '4', '5', '6', 'B', '7', '8', '9', 'C', '*', '0', '#', 'D'};
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
     * Gets DTMF object from character
     * @param c Character that should be converted
     * @return The sampling object or <b>null</b> if not found
     */
    private Wave16 fromChar(char c)
    {
        for (int s = 0; s < charmap.length; s++)
        {
            if (charmap[s] == c)
            {
                return wavemap[s];
            }
        }
        return null;
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
                inWave[s] = fromChar(in.charAt(s));
            }
        }
        else
        {
            inWave = new Wave16[in.length() * 2];
            Wave16 delWave = new Wave16(delay, samplingRate);
            for (int s = 0; s < in.length(); s++)
            {
                inWave[s * 2] = fromChar(in.charAt(s));
                inWave[s * 2 + 1] = delWave;
            }
        }
        return new Wave16().combineAppend(inWave);
    }
}
