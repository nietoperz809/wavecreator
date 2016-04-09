import com.WaveCreator.FrameManager;
import com.WaveCreator.Functions.FunctionsGenerators;
import com.WaveCreator.Wave16;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

/**
 * To change this template use File | Settings | File Templates.
 */
public class Starter
{

    /**
     * @param args
     * @throws Exception
     */
    public static void main (String... args) throws Exception
    {
//        String[] p = listPackage("sounds", true);
//        System.out.println(Arrays.toString(p));
        //FunctionsTesting.testHenon();

//        Wave16 s1 = FunctionsGenerators.curveSine(22050, 2000);
//        FrameManager.getInstance().createFrame(s1, "Starting frame");
        test();
    }

    public static void test ()
    {
        int totalFramesRead = 0;
        File fileIn = new File("F:\\wavs\\modular hits\\modular hits\\1 kicks\\modular hits#03.01.aif");
// somePathName is a pre-existing string whose value was
// based on a user selection.
        try
        {
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(fileIn);
            int bytesPerFrame =
                    audioInputStream.getFormat().getFrameSize();
            int sampleRate =
                    (int)audioInputStream.getFormat().getSampleRate();
            int channels = audioInputStream.getFormat().getChannels();

            System.out.println (sampleRate);
            System.out.println (channels);

            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED)
            {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }
            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            try
            {
                int numBytesRead = 0;
                int numFramesRead = 0;
                // Try to read numBytes bytes from the file.
                while ((numBytesRead =
                        audioInputStream.read(audioBytes)) != -1)
                {
                    // Calculate the number of frames actually read.
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;
                    // Here, do something useful with the audio data that's
                    // now in the audioBytes array...
                }
            }
            catch (Exception ex)
            {
                // Handle the error...
            }
        }
        catch (Exception e)
        {
            // Handle the error...
        }
    }
}

