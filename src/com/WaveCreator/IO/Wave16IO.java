package com.WaveCreator.IO;

import com.WaveCreator.Helpers.Tools;
import com.WaveCreator.Wave16;
import com.jcraft.jorbis.OggDecoder;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

import static com.WaveCreator.Functions.FunctionsGenerators.fromDoubleArray;

/**
 * To change this template use File | Settings | File Templates.
 */
public class Wave16IO
{
    /**
     * Saves a Wave's raw data
     *
     * @param dat      Wave to supply data
     * @param filename Name of the new file
     * @throws Exception Thrown if file operation failed
     */
    public static void saveRaw (Wave16 dat, String filename) throws Exception
    {
        try (FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(filename)))
        {
            fileImageOutputStream.writeInt(dat.data.length);  // First write the length
            fileImageOutputStream.writeInt(dat.samplingRate);  // Then the samplingrate
            fileImageOutputStream.writeFloats(dat.data, 0, dat.data.length);
        } // First write the length
    }

    /**
     * Load a wave from raw data
     *
     * @param filename Name of file that contains raw samples
     * @return A new Wave object
     * @throws Exception Thrown if file operation failed
     */
    public static Wave16 loadRaw (String filename) throws Exception
    {
        int samplingRate;
        float[] arr;
        try (FileImageInputStream fileImageInputStream = new FileImageInputStream(new File(filename)))
        {
            int size = fileImageInputStream.readInt(); // First read the length
            samplingRate = fileImageInputStream.readInt(); // Then get the sampling rate
            arr = new float[size];
            fileImageInputStream.readFully(arr, 0, size);
        } // First read the length
        return new Wave16(arr, samplingRate);
    }


    /**
     * Save data as mono WAV file
     *
     * @param wav      Points to sampling data
     * @param filename Name of new file
     * @throws Exception If something's gone wrong
     */
    public static void saveWave16 (Wave16 wav, String filename) throws Exception
    {
        byte[] pcm_data = wav.toByteArray();
        AudioFormat frmt = new AudioFormat (wav.samplingRate,
                16,1,true,false);
        AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(pcm_data)
                ,frmt
                ,pcm_data.length);
        AudioSystem.write (ais, AudioFileFormat.Type.WAVE, new File(filename));
    }

    public static Wave16 loadAiff (String filename)
    {
        return loadAiff(new File(filename));
    }

    public static Wave16 loadAiff (File fileIn)
    {
        try
        {
            System.out.println(fileIn.getName());
            int totalFramesRead = 0;
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(fileIn);
            int bytesPerFrame =
                    audioInputStream.getFormat().getFrameSize();
            int sampleRate =
                    (int) audioInputStream.getFormat().getSampleRate();
            int channels = audioInputStream.getFormat().getChannels();

            System.out.println(sampleRate);
            System.out.println(channels);

            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED)
            {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }
            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            ByteArrayOutputStream ba = new ByteArrayOutputStream(65536);
            int numBytesRead;
            int numFramesRead;
            // Try to read numBytes bytes from the file.
            while ((numBytesRead =
                    audioInputStream.read(audioBytes)) != -1)
            {
                // Calculate the number of frames actually read.
                numFramesRead = numBytesRead / bytesPerFrame;
                totalFramesRead += numFramesRead;
                // Here, do something useful with the audio data that's
                // now in the audioBytes array...
                ba.write(audioBytes, 0, numBytesRead);
            }
            float[] dx = Tools.readBE(ba.toByteArray(), bytesPerFrame);
            return new Wave16(Tools.fitValues(dx), sampleRate);
        }
        catch (Exception ex)
        {
            System.out.println(ex);
            return null;
        }
    }

    public static Wave16 loadWave (String filename)
    {
        return loadWave(new File(filename));
    }

    /**
     * Loads Wave16 from wav ile
     *
     * @return The loaded Wave16 object
     * @throws Exception if anything's gone wrong
     */
    public static Wave16 loadWave (File file)
    {
        WavFile wv = null;
        try
        {
            wv = WavFile.openWavFile(file);
        }
        catch (Exception e)
        {
            System.err.println(e);
            return null;
        }
        float[] frames = new float[(int) wv.getNumFrames() * wv.getNumChannels()];

        try
        {
            wv.readFrames(frames, frames.length);
        }
        catch (Exception e)
        {
            System.err.println(e);
            return null;
        }
        frames = Tools.fitValues(frames);
        if (wv.getNumChannels() == 2)
        {
            frames = Tools.combineStereo(frames); // Make mono
        }
        return fromDoubleArray(frames, (int) wv.getSampleRate());
    }

    /**
     * Loads Wave16 from ogg ile
     *
     * @param filename file path
     * @return The loaded Wave16 object
     * @throws Exception if anything's gone wrong
     */
    public static Wave16 loadOgg (String filename) throws Exception
    {
        File f = new File(filename);
        return loadOgg(new FileInputStream(f));
    }

    public synchronized static Wave16 loadOgg (InputStream in)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(65536);
        OggDecoder dec = new OggDecoder();
        try
        {
            dec.decode(in, out);
        }
        catch (Exception ex)
        {
            return null;
        }
        byte[] arr = out.toByteArray();
        return new Wave16(arr, dec.getSampleRate(), arr.length);
    }
}

