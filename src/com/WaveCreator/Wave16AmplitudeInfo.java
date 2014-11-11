package com.WaveCreator;

/**
 * This class serves as amplitude information
 */
public final class Wave16AmplitudeInfo
{
    /**
     * Minimum amplitude
     */
    public double min;
    /**
     * Maximum amplitude
     */
    public double max;
    /**
     * Total amplitude span
     */
    public double span;

    Wave16AmplitudeInfo()
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
    public void calc(double arr[])
    {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;

        // Find min and max
        for (double anIn : arr)
        {
            // Force forbidden values to zero
            if (Double.isInfinite(anIn) || Double.isNaN(anIn))
                anIn = 0.0;

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
