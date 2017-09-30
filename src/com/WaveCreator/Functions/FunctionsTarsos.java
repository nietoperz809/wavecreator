package com.WaveCreator.Functions;

import be.tarsos.dsp.AutoCorrelation;
import be.tarsos.dsp.BitDepthProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.PitchShifter;
import be.tarsos.dsp.effects.FlangerEffect;
import be.tarsos.dsp.filters.BandPass;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.filters.LowPassFS;
import com.WaveCreator.ParamDesc;
import com.WaveCreator.TarsosWrapper.TarsosRunner;
import com.WaveCreator.Wave16;

/**
 * Created by Administrator on 3/7/2017.
 */
public class FunctionsTarsos extends Functions
{
    public FunctionsTarsos (Wave16 base)
    {
        super (base);
    }
    private static final int buffsize = 4096;

    public Wave16 pitchShift (@ParamDesc("Factor") float factor)
    {
        int overlap = buffsize-128;
        PitchShifter ps = new PitchShifter(factor, m_base.getAudioFormat().getSampleRate(), buffsize, overlap);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 bandPass (@ParamDesc("Freq") float freq,
                            @ParamDesc("Width") float width)
    {
        BandPass ps = new BandPass(freq, width, m_base.samplingRate);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 autoCorrelation ()
    {
        AutoCorrelation ps = new AutoCorrelation();
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 bitDepth (@ParamDesc("bitDepth") int bd)
    {
        BitDepthProcessor ps = new BitDepthProcessor();
        ps.setBitDepth(bd);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 flanger (@ParamDesc("Length(sec)") double length,
                           @ParamDesc("Wet(0...1)") double wet,
                           @ParamDesc("LfoFreq(Hz)") double lfo)
    {
        FlangerEffect ps = new FlangerEffect(length, wet, m_base.samplingRate, lfo);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 gain (@ParamDesc("Gain") double gain)
    {
        GainProcessor ps = new GainProcessor(gain);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 lowPassFS (@ParamDesc("Frequency") float freq)
    {
        LowPassFS ps = new LowPassFS(freq, m_base.samplingRate);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }

    public Wave16 highPass (@ParamDesc("Frequency") float freq)
    {
        HighPass ps = new HighPass(freq, m_base.samplingRate);
        return TarsosRunner.execute(m_base, ps, buffsize);
    }


    // DOES NOT WORK!!
//    public Wave16 rateTransposer (@ParamDesc("Factor") double fact)
//    {
//        RateTransposer ps = new RateTransposer(fact);
//        Wave16 w = TarsosRunner.execute(m_base, ps, buffsize);
//        w.samplingRate = (int) (m_base.samplingRate * fact);
//        return w;
//    }

    //    public Wave16 constantQ (@ParamDesc("Min") float min,
//                             @ParamDesc("Max") float max,
//                             @ParamDesc("BinsPerOctave") float bins)
//    {
//        ConstantQ ps = new ConstantQ(m_base.samplingRate, min, max, bins);
//        return TarsosRunner.execute(m_base, ps, ps.getFFTlength());
//    }
//
//    public Wave16 delay (@ParamDesc("Length") double length,
//                         @ParamDesc("Decay") double decay)
//    {
//        DelayEffect ps = new DelayEffect (length, decay, m_base.samplingRate);
//        return TarsosRunner.execute(m_base, ps, buffsize);
//    }
}
