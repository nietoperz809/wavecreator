package com.WaveCreator.TarsosWrapper;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import com.WaveCreator.Wave16;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 3/7/2017.
 */
public class Tarsos
{
    static class Wave16Creator extends WaveformWriter
    {
        ArrayList<Wave16> waves = new ArrayList<>();
        Wave16 result;
        float samplerate;
        AudioFormat format;

        public Wave16Creator (AudioFormat format, String fileName)
        {
            super(format, fileName);
            samplerate = format.getSampleRate();
            this.format = format;
        }

        @Override
        public boolean process (AudioEvent audioEvent)
        {
            int byteOverlap = audioEvent.getOverlap() * format.getFrameSize();
            int byteStepSize = audioEvent.getBufferSize() * format.getFrameSize() - byteOverlap;
            if(audioEvent.getTimeStamp() == 0){
                byteOverlap = 0;
                byteStepSize = audioEvent.getBufferSize() * format.getFrameSize();
            }
            AudioPlayer ap;

            byte[] fb = audioEvent.getByteBuffer();
            Wave16 wv = new Wave16 (fb, (int)samplerate, byteOverlap, byteStepSize);
            waves.add(wv);
            return true;
        }

        @Override
        public void processingFinished ()
        {
            Wave16[] arr = new Wave16[waves.size()];
            waves.toArray(arr);
            result = Wave16.combineAppend(arr);
            //System.out.println("finished");
        }

    }

    public static Wave16 process (Wave16 in, AudioProcessor proc)
    {
        byte[] arr = in.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        AudioInputStream inputStream = new AudioInputStream(bais, in.getAudioFormat(), arr.length);
        int buffsize = 4096;
        int overlap = buffsize-128;
        AudioFormat format = inputStream.getFormat(); //new AudioFormat(44100,16,1,true,false); ;
        JVMAudioInputStream i2 = new JVMAudioInputStream(inputStream);
        AudioDispatcher dispatcher = new AudioDispatcher(i2, (int) buffsize, overlap);
        dispatcher.addAudioProcessor(proc);
        Wave16Creator wc = new Wave16Creator(format, "tarsosCreated");
        dispatcher.addAudioProcessor(wc);
        dispatcher.run();
        return wc.result;
    }
}
