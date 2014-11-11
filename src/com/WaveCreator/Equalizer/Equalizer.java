package com.WaveCreator.Equalizer;

/**
 * Generic wrapper around IIR algorithm.
 * Author: Dmitry Vaguine
 * Date: 02.05.2004
 * Time: 12:00:29
 */
public class Equalizer implements EqualizerBase
{
    /**
     * Max bands supported by the code
     */
    private final static int EQ_MAX_BANDS = 10;
    /**
     * Supported sample rates
     */
    private final static float EQ_11025_RATE = 11025;
    public final static float EQ_22050_RATE = 22050;
    private final static float EQ_44100_RATE = 44100;
    private final static float EQ_48000_RATE = 48000;

    /* Indexes for the history arrays
     * These have to be kept between calls to this function
     * hence they are static */
    private int i;
    private int j;
    private int k;

    /* History for two filters */
    private final XYData[] dataHistory = new XYData[EQ_MAX_BANDS];
    //private final XYData[] dataHistory2 = new XYData[EQ_MAX_BANDS];

    /* Coefficients */
    private IIRCoefficients[] iircf;

    /* rate */
    private final float rate;

    /**
     * Volume gain
     * values should be between 0.0 and 1.0
     */
    private float preamp;
    /**
     * Gain for each band
     * values should be between -0.2 and 1.0
     */
    private final float[] bands = new float[Equalizer.EQ_MAX_BANDS];

    static class XYData
    {
        /**
         * X data
         */
        public final double[] x = new double[3]; /* x[n], x[n-1], x[n-2] */
        /**
         * Y data
         */
        public final double[] y = new double[3]; /* y[n], y[n-1], y[n-2] */

// --Commented out by Inspection START (03.01.09 21:20):
//        /**
//         * Constructs new XYData object
//         */
//        public XYData()
//        {
//            zero();
//        }
// --Commented out by Inspection STOP (03.01.09 21:20)

        /**
         * Fills all content with zero
         */
        public void zero()
        {
            for (int i = 0; i < 3; i++)
            {
                x[i] = 0;
                y[i] = 0;
            }
        }
    }
    
    /**
     * Constructs equalizer with given config
     * @param r  is the sample rate of equalizer
     */
    public Equalizer(float r)
    {
        this.rate = r;
        preamp = 1.0f;
        for (int ii = 0; ii < Equalizer.EQ_MAX_BANDS; ii++)
        {
            bands[ii] = 0f;
        }
        initIIR();
    }

    /* Init the filters */
    private void initIIR()
    {
        setFilters();
        for (int ii = 0; ii < EQ_MAX_BANDS; ii++)
        {
            dataHistory[ii] = new XYData();
            //dataHistory2[ii] = new XYData();
        }
        i = 0;
        j = 2;
        k = 1;
    }

    private void setFilters()
    {
        if (rate == EQ_11025_RATE)
        {
            iircf = iir_cf10_11k_11025;
        }
        else if (rate == EQ_22050_RATE)
        {
            iircf = iir_cf10_22k_22050;
        }
        else if (rate == EQ_44100_RATE)
        {
            iircf = iir_cf10_44100;
        }
        else if (rate == EQ_48000_RATE)
        {
            iircf = iir_cf10_48000;
        }
    }

    /**
     * Main filtering method.
     * @param data   - data to be filtered
     */
    public void iir(double[] data)
    {
        int index, band;
        float eqpreamp = preamp;
        float eqbands[] = bands;
        double pcm, out;

        /**
         * IIR filter equation is
         * y[n] = 2 * (alpha*(x[n]-x[n-2]) + gamma*y[n-1] - beta*y[n-2])
         *
         * NOTE: The 2 factor was introduced in the coefficients to save
         * 			a multiplication
         *
         * This algorithm cascades two filters to get nice filtering
         * at the expense of extra CPU cycles
         */
        IIRCoefficients tempcf;
        XYData tempd;
        for (index = 0; index < data.length; index += 1)
        {
            pcm = data[index] * eqpreamp;

            out = 0f;
            /* For each band */
            for (band = 0; band < EQ_MAX_BANDS; band++)
            {
                /* Store Xi(n) */
                tempd = dataHistory[band];
                tempd.x[i] = pcm;
                /* Calculate and store Yi(n) */
                tempcf = iircf[band];
                tempd.y[i] =
                        (
                                /* 		= alpha * [x(n)-x(n-2)] */
                                tempcf.alpha * (pcm - tempd.x[k])
                                /* 		+ gamma * y(n-1) */
                                + tempcf.gamma * tempd.y[j]
                                /* 		- beta * y(n-2) */
                                - tempcf.beta * tempd.y[k]
                        );
                /*
                 * The multiplication by 2.0 was 'moved' into the coefficients to save
                 * CPU cycles here */
                /* Apply the gain  */
                out += (tempd.y[i] * eqbands[band]); // * 2.0;
            } /* For each band */

            /* Volume stuff
        Scale down original PCM sample and add it to the filters
        output. This substitutes the multiplication by 0.25
        Go back to use the floating point multiplication before the
        conversion to give more dynamic range
        */
            out += (pcm * 0.25);

            /* Normalize the output */
            out *= 4;

            /* Round and convert to integer */
            data[index] = out;

            i++;
            j++;
            k++;

            /* Wrap around the indexes */
            if (i == 3)
            {
                i = 0;
            }
            else if (j == 3)
            {
                j = 0;
            }
            else
            {
                k = 0;
            }

        }/* For each pair of samples */
    }

    /**
     * Setter for value of control for given band
     * @param band    is the index of band
     * @param value   is the new value
     */
    public void setBandValue(int band, float value)
    {
        bands[band] = value;
    }

    /**
     * Setter for value of control for given band(in Db)
     * @param band    is the index of band
     * @param value   is the new value
     */
    public void setBandDbValue(int band, float value)
    {
        /* Map the gain and preamp values */
        /* -12dB .. 12dB mapping */
        bands[band] = (float) (2.5220207857061455181125E-01 *
                                        Math.exp(8.0178361802353992349168E-02 * value)
                                        - 2.5220207852836562523180E-01);
    }

    /**
     * Setter for value of preamp control
     * @param value   is the new value
     */
    public void setPreampValue(float value)
    {
        preamp = value;
    }

    /**
     * Setter for value of preamp control (in Db)
     * @param value   is the new value
     */
    public void setPreampDbValue(float value)
    {
        /* -12dB .. 12dB mapping */
        preamp = (float) (9.9999946497217584440165E-01 *
                                   Math.exp(6.9314738656671842642609E-02 * value)
                                   + 3.7119444716771825623636E-07);
    }
}
