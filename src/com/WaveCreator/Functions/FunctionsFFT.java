package com.WaveCreator.Functions;

import com.WaveCreator.FFT.CepstrumTransform;
import com.WaveCreator.FFT.FFT;
import com.WaveCreator.Helpers.Tools;
import com.WaveCreator.Wave16;
import org.apache.commons.math3.complex.Complex;

//import com.WaveCreator.FFT.InternalComplex;

/**
 * New Class.
 * User: Administrator
 * Date: 29.12.2008
 * Time: 10:21:02
 */
public final class FunctionsFFT extends Functions
{
    public FunctionsFFT(Wave16 base)
    {
        super(base);
    }

    public Wave16 fftFFTImaginary()
    {
        return fftHalf (m_base, FFTTYPE.IMAGINARY, FFTALGORITHM.FFT);
    }

    public Wave16 fftFFTReal()
    {
        return fftHalf (m_base, FFTTYPE.REAL, FFTALGORITHM.FFT);
    }

    public Wave16 fftFFTAbsolute()
    {
        return fftHalf (m_base, FFTTYPE.ABSOLUTE, FFTALGORITHM.FFT);
    }

    public Wave16 fftInverseFFTImaginary()
    {
        return fftHalf (m_base, FFTTYPE.IMAGINARY, FFTALGORITHM.INVERSE_FFT);
    }

    public Wave16 fftInverseFFTReal()
    {
        return fftHalf (m_base, FFTTYPE.REAL, FFTALGORITHM.INVERSE_FFT);
    }

    public Wave16 fftInverseFFTAbsolute()
    {
        return fftHalf (m_base, FFTTYPE.ABSOLUTE, FFTALGORITHM.INVERSE_FFT);
    }

    public Wave16 fftConvolutionImaginary()
    {
        return fft (m_base, FFTTYPE.IMAGINARY, FFTALGORITHM.CONVOLUTION);
    }

    public Wave16 fftConvolutionFReal()
    {
        return fft (m_base, FFTTYPE.REAL, FFTALGORITHM.CONVOLUTION);
    }

    public Wave16 fftConvolutionAbsolute()
    {
        return fft (m_base, FFTTYPE.ABSOLUTE, FFTALGORITHM.CONVOLUTION);
    }

    public Wave16 fftCircularConvolutionImaginary()
    {
        return fft (m_base, FFTTYPE.IMAGINARY, FFTALGORITHM.CIRCULAR_CONVOLUTION);
    }

    public Wave16 fftCircularConvolutionFReal()
    {
        return fft (m_base, FFTTYPE.REAL, FFTALGORITHM.CIRCULAR_CONVOLUTION);
    }

    public Wave16 fftCircularConvolutionAbsolute()
    {
        return fft (m_base, FFTTYPE.ABSOLUTE, FFTALGORITHM.CIRCULAR_CONVOLUTION);
    }

    public Wave16 cepstrumLinear()
    {
        Wave16 out = m_base.functionsLength.padToNextPowerOfTwo().functionsMathematical.normalize();
        CepstrumTransform cp = new CepstrumTransform();
        out.data = cp.doCepstrumTransform(out.data,false);
        out = out.functionsDeletions.extractSamples(0, out.data.length / 2);
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 cepstrumLogarithmic()
    {
        Wave16 out = m_base.functionsLength.padToNextPowerOfTwo().functionsMathematical.normalize();
        CepstrumTransform cp = new CepstrumTransform();
        out.data = cp.doCepstrumTransform(out.data, true);
        out = out.functionsDeletions.extractSamples(0, out.data.length / 2);
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Constants for FFT method
     */
    public enum FFTALGORITHM
    {
        /**
         *
         */
        FFT,
        /**
         *
         */
        INVERSE_FFT,
        /**
         *
         */
        CONVOLUTION,
        /**
         *
         */
        CIRCULAR_CONVOLUTION
    }

    /**
     * Constants for FFT method
     */
    public enum FFTTYPE
    {
        /**
         *
         */
        REAL,
        /**
         *
         */
        IMAGINARY,
        /**
         *
         */
        ABSOLUTE
    }

    /**
     * Calculates FFT, convolution and inverses
     * @param in Wave16 to be used as input
     * @param type Type of output
     * @param alg  Algorithm
     * @return the new sampling object
     */
    private Wave16 fft (Wave16 in, FFTTYPE type, FFTALGORITHM alg)
    {
        Wave16 out = in.functionsLength.padToNextPowerOfTwo().functionsMathematical.normalize();
        Complex[] c = new Complex[out.data.length];
        for (int s = 0; s < out.data.length; s++)
        {
            c[s] = new Complex(out.data[s], 0);
        }
        Complex[] y = null;
        switch (alg)
        {
            case FFT:
                y = FFT.fft(c);
                break;

            case INVERSE_FFT:
                y = FFT.ifft(c);
                break;

            case CONVOLUTION:
                y = FFT.convolve(c, c);
                break;

            case CIRCULAR_CONVOLUTION:
                y = FFT.cconvolve(c, c);
                break;
        }
        switch (type)
        {
            case IMAGINARY:
                for (int s = 0; s < in.data.length; s++)
                {
                    out.data[s] = (float)y[s].getImaginary();
                }
                break;

            case REAL:
                for (int s = 0; s < in.data.length; s++)
                {
                    out.data[s] = (float)y[s].getReal();
                }
                break;

            case ABSOLUTE:
                for (int s = 0; s < in.data.length; s++)
                {
                    out.data[s] = (float)y[s].abs();
                }
                break;
        }
        out = out.functionsDeletions.deleteSamplesFromEnd(out.data.length-in.data.length);
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Calculates FFT, convolution and inverses and divides number of samples by 2
     * @param in Wave object to use
     * @param type Type of output
     * @param alg  Algorithm
     * @return the new sampling object
     */
    private Wave16 fftHalf (Wave16 in, FFTTYPE type, FFTALGORITHM alg)
    {
        return fft(in, type, alg).functionsDeletions.extractSamples(0, in.data.length / 2);
    }
}
