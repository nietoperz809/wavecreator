package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Wave16;

import java.util.ArrayList;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 10:21:27
 */
public final class FunctionsDeletions extends Functions
{
    public FunctionsDeletions(Wave16 base)
    {
        super(base);
    }

    /**
     * Extracts samples
     * @param from beginning
     * @param to   end
     * @return New samplingdata that is <b>from</b> to <b>to</b>
     */
    public Wave16 extractSamples (@ParamDesc("First sample")int from,
                                  @ParamDesc("Last sample")int to)
    {
        int len = to - from;
        Wave16 res = new Wave16(len, m_base.samplingRate);
        System.arraycopy(m_base.data, from, res.data, 0, len);
        return res;
    }

    /**
     * Deletes samples from beginning
     * @param num Number of samples to delete
     * @return The new sampling array
     */
    public Wave16 deleteSamplesFromBeginning (@ParamDesc("Number of samples to delete") int num)
    {
        return deleteSamples(0, num);
    }

    /**
     * Deletes samples from end
     * @param num Number of samples to delete
     * @return The new sampling array
     */
    public Wave16 deleteSamplesFromEnd (@ParamDesc("Number of samples to delete") int num)
    {
        return deleteSamples(m_base.data.length - num, m_base.data.length);
    }

    /**
     * Deletes samples greater than min and less or equal to max
     * @param min Min value
     * @param max Max value
     * @return The new sampling array
     */
    public Wave16 deleteSamplesBetween (@ParamDesc("Minimum value to delete") int min,
                                        @ParamDesc("Maximum value to delete") int max)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        for (double aData : m_base.data)
        {
            if (aData >= min && aData <= max)
            {
                continue;
            }
            al.add((int) aData);
        }
        Integer[] d = new Integer[al.size()];
        al.toArray(d);
        return new Wave16(d, m_base.samplingRate);
    }

    /**
     * Deletes samples periodical
     * @param keep number of samples to keep
     * @param miss number of samples to throw away
     * @param mode TRUE if we start with a 'keep' sequence
     * @return The new sampling array
     */
    public Wave16 deleteSamplesEvery (@ParamDesc("Number of samples to keep")int keep,
                                      @ParamDesc("Number of samples to throw away")int miss,
                                      @ParamDesc ("TRUE if first samples should be kept") boolean mode)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        int cnt = 0;
        int kept = 0;
        for (double aData : m_base.data)
        {
            if (mode)
            {
                al.add((int) aData);
                kept++;
                cnt++;
                if (cnt == keep)
                {
                    cnt = 0;
                    mode = !mode;
                }
            }
            else
            {
                cnt++;
                if (cnt == miss)
                {
                    cnt = 0;
                    mode = !mode;
                }
            }
        }
        Integer[] d = new Integer[al.size()];
        al.toArray(d);
        double fact = (double)m_base.data.length/(double)kept;
        double newrate = (double)m_base.samplingRate /fact;
        return new Wave16(d, (int)newrate);
    }


    /**
     * Deletes samples, thus shrinking the array
     * @param from First sample to delete
     * @param to   Last sample to delete
     * @return The new sampling array
     */
    public Wave16 deleteSamples (@ParamDesc("First sample to delete") int from,
                                 @ParamDesc("Last sample to delete") int to)
    {
        Wave16 out = new Wave16(m_base.data.length - to + from, m_base.samplingRate);
        System.arraycopy(m_base.data, 0, out.data, 0, from);
        System.arraycopy(m_base.data, to, out.data, from, m_base.data.length - to);
        return out;
    }

    /**
     * Deletes consecutive samples with low difference
     * @param difference Threshold
     * @return A new sampling array
     */
    public Wave16 deleteConsecutiveSameSamples (@ParamDesc("Difference threshold") int difference)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add((int) m_base.data[0]);
        for (int s = 1; s < m_base.data.length; s++)
        {
            double d = Math.abs(m_base.data[s] - m_base.data[s - 1]);
            if (d <= difference)
            {
                continue;
            }
            al.add((int) m_base.data[s]);
        }
        Integer[] d = new Integer[al.size()];
        al.toArray(d);
        return new Wave16(d, m_base.samplingRate);
    }

    public Wave16 deleteSamplesWithSameValue()
    {
        return deleteConsecutiveSameSamples(1);
    }

    /**
     * Deletes all postive samples
     * @return The new sampling array
     */
    public Wave16 deletePositiveSamples()
    {
        return deleteSamplesBetween(0, Integer.MAX_VALUE);
    }

    /**
     * Deletes all negative samples
     * @return The new sampling array
     */
    public Wave16 deleteNegativeSamples()
    {
        return deleteSamplesBetween(Integer.MIN_VALUE, 0);
    }

    public Wave16 deleteNullSamples()
    {
        return deleteSamplesBetween(0, 0);
    }
}
