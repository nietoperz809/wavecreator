package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Wave16;
import com.WaveCreator.OtherFilters.MoogFilter;
import com.WaveCreator.Equalizer.Equalizer;
import com.WaveCreator.Helpers.Tools;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 19:30:54
 */
public final class FunctionsFilters extends Functions
{
    public FunctionsFilters (Wave16 base)
    {
        super (base);
    }

    public Wave16 moog (@ParamDesc("Frequency") int cutoff,
                        @ParamDesc("Energy (0...1.5)") double res)
    {
        Wave16 wave = m_base.copy();
        MoogFilter mf = new MoogFilter (m_base.samplingRate);
        mf.setFilterParams(cutoff, res);
        mf.performFilter(wave.data, wave.data.length);
        return wave;
    }

    public Wave16 removeDC (@ParamDesc("Filte coeff (usually 0.999 ... 0.95") double m)
    {
        Wave16 res = m_base.createEmptyCopy();
        res.data[0] = m_base.data[0];
        for (int s=1; s<m_base.data.length; s++)
        {
            res.data[s] = m_base.data[s] - m_base.data[s-1] + m * res.data[s-1];
        }
        return res;
    }

    public Wave16 equalizer(@ParamDesc("10 +/- values as EQ factors")int[] vals)
    {
        Wave16 res = m_base.copy();
        Equalizer equalizer = new Equalizer(Equalizer.EQ_22050_RATE);
        for (int s=0; s<vals.length; s++)
        {
            equalizer.setBandDbValue(s, vals[s]);
        }
        equalizer.iir (res.data);
        res.data = Tools.fitValues (res.data);
        return res;
    }

    public Wave16 eqLow()
    {
        final int[] vals = {50,50,-50,-50,-50,-50,-50,-50,-50,-50};
        return equalizer(vals);
    }

    public Wave16 eqHigh()
    {
        final int[] vals = {-50,-50,-50,-50,-50,-50,-50,-50,50,50};
        return equalizer(vals);
    }

    public Wave16 eqMid()
    {
        final int[] vals = {-50,-50,-50,-50, 50,50, -50,-50,-50,-50};
        return equalizer(vals);
    }

    /**
     * Calculates 'moving' average over the samples.
     * @param num samples to be combined
     * @return The new sampling object
     */
    public Wave16 movingAverageFilter (@ParamDesc("Number of samples to be combined") int num)
    {
        Wave16 out = m_base.createEmptyCopy();
        double[] win = new double[num];
        for (int n = 0; n < m_base.data.length; n++)
        {
            int vals;
            double res = 0;
            if (n < num)
            {
                win[n] = m_base.data[n];
                vals = n+1;
            }
            else
            {
                System.arraycopy (win, 1, win, 0, num-1);
                win[num-1] = m_base.data[n];
                vals = num;
            }
            for (int s=0; s<vals; s++)
            {
                res = res + win[s];
            }
            out.data[n] = res/vals;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Smoothes the sampling array
     * @param num Smoothing rounds
     * @return The new sampling array
     */
    public Wave16 smoothe (@ParamDesc("Number of smoothing rounds") int num)
    {
        Wave16 out = m_base.copy();
        for (int n=0; n<num; n++)
        {
            out.data[1] = (out.data[0] + out.data[1] + out.data[2]) / 3.0;
            out.data[0] = (out.data[0] + out.data[1]) / 2.0;
            out.data[out.data.length - 2] = (out.data[out.data.length - 1] + out.data[out.data.length - 2] + out.data[out.data.length - 3]) / 3.0;
            out.data[out.data.length - 1] = (out.data[out.data.length - 1] + out.data[out.data.length - 2]) / 2.0;
            for (int i = 2; i < (out.data.length - 2); i++)
            {
                out.data[i] = (out.data[i] + out.data[i + 1] + out.data[i - 1] + out.data[i + 2] + out.data[i - 2]) / 5.0;
            }
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Implements a smple IIR filter
     * @return The filtered wave
     */
    public Wave16 iirFilter()
    {
        Wave16 out = m_base.createEmptyCopy();
        out.data[0] = m_base.data[0];
        for (int s = 1; s < m_base.data.length; s++)
        {
            out.data[s] = (m_base.data[s] + out.data[s - 1]) / 2;
        }
        return out;
    }

    /**
     * Implements a FIR filter
     * @param coeffs filter coefficents
     * @return The filtered new wave
     */
    public Wave16 firFilter (@ParamDesc("Array of coefficients") double[] coeffs)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s = 0; s < m_base.data.length; s++)
        {
            double v1 = 0;
            for (int n = 0; n < coeffs.length; n++)
            {
                int idx = (s + n) % m_base.data.length;
                v1 = v1 + m_base.data[idx] * coeffs[n];
            }
            out.data[s] = v1;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }
}
