package com.WaveCreator;

/**
 * This class serves as amplitude information
 */
public final class Wave16AmplitudeInfo
{
    /**
     * Minimum amplitude
     */
    public float min;
    /**
     * Maximum amplitude
     */
    public float max;
    /**
     * Total amplitude span
     */
    public float span;

    public Wave16AmplitudeInfo()
    {
    }

    public Wave16AmplitudeInfo(Wave16 w)
    {
        calc (w.data);
    }

    /**
     * Does calculation so that members are valid
     * @param arr Array to be used as base object
     */
    public void calc(float arr[])
    {
        min = Float.MAX_VALUE;
        max = -Float.MAX_VALUE;

        // Find min and max
        for (float anIn : arr)
        {
            // Force forbidden values to zero
            if (Double.isInfinite(anIn) || Double.isNaN(anIn))
                anIn = 0.0f;

            if (anIn < min)
            {
                min = anIn;
            }
            if (anIn > max)
            {
                max = anIn;
            }
        }
        span = max - min;
    }

    @Override
    public String toString()
    {
        return "Min:"+ min + " Max:" + max + " Span:" + span;
    }
}
