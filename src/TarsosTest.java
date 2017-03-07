import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.PitchShifter;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import com.WaveCreator.Functions.FunctionsGenerators;
import com.WaveCreator.Wave16;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TarsosTest
{
    public static void main (String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        Wave16 wv = FunctionsGenerators.curveSine(22000, 1000, 1000);
        byte[] arr = wv.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        AudioInputStream inputStream = new AudioInputStream(bais, wv.getAudioFormat(), arr.length);
        int buffsize = 4096;
        int overlap = buffsize-128;
        //File f = new File ("c:\\violin.wav");
        //AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
        AudioFormat format = inputStream.getFormat(); //new AudioFormat(44100,16,1,true,false); ;
        JVMAudioInputStream i2 = new JVMAudioInputStream(inputStream);
        AudioDispatcher dispatcher = new AudioDispatcher(i2, (int) buffsize, overlap);
        dispatcher.addAudioProcessor(new PitchShifter(3.5,format.getSampleRate(), buffsize, overlap));
        //dispatcher.addAudioProcessor(new LowPassFS(100, 44100));
        //dispatcher.addAudioProcessor(new HighPass(1000, 44100));
        dispatcher.addAudioProcessor(new Wave16Creator(format, "c:\\filtered.wav"));
        //dispatcher.addAudioProcessor(new AudioPlayer(format));
        dispatcher.run();
        //dispatcher.
    }
}

class Wave16Creator extends WaveformWriter
{
    ArrayList<Wave16> waves = new ArrayList<>();
    Wave16 result;
    float samplerate;

    public Wave16Creator (AudioFormat format, String fileName)
    {
        super(format, fileName);
        samplerate = format.getSampleRate();
    }

    @Override
    public boolean process (AudioEvent audioEvent)
    {
        //System.out.println("tick");
        byte[] fb = audioEvent.getByteBuffer();
        Wave16 wv = new Wave16 (fb, (int)samplerate, fb.length);
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
