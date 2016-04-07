package com.WaveCreator.IO;

import com.WaveCreator.Tools;
import com.WaveCreator.Wave16;
import de.jarnbjo.ogg.EndOfOggStreamException;
import de.jarnbjo.ogg.FileStream;
import de.jarnbjo.ogg.LogicalOggStream;
import de.jarnbjo.ogg.PhysicalOggStream;
import de.jarnbjo.vorbis.VorbisStream;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import static com.WaveCreator.Functions.FunctionsGenerators.fromDoubleArray;
import static com.WaveCreator.Functions.FunctionsGenerators.fromShortArray;

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
            fileImageOutputStream.writeDoubles(dat.data, 0, dat.data.length);
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
        double[] arr;
        try (FileImageInputStream fileImageInputStream = new FileImageInputStream(new File(filename)))
        {
            int size = fileImageInputStream.readInt(); // First read the length
            samplingRate = fileImageInputStream.readInt(); // Then get the sampling rate
            arr = new double[size];
            fileImageInputStream.readFully(arr, 0, size);
        } // First read the length
        return new Wave16(arr, samplingRate);
    }

    /**
     * Loads Ogg Vorbis file
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static Wave16 loadOgg (String filename) throws Exception
    {
        PhysicalOggStream os = new FileStream(new RandomAccessFile(filename, "r"));
        LogicalOggStream los = (LogicalOggStream) os.getLogicalStreams().iterator().next();
        System.out.println(os);

        // exit, if it is not a Vorbis stream
        if (los.getFormat() != LogicalOggStream.FORMAT_VORBIS)
        {
            throw new Exception("Not OGG");
        }

        final VorbisStream vs = new VorbisStream(los);
        byte[] buffer=new byte[65536];
        long len = 0;

        try
        {
            // read pcm data from the vorbis channel and
            // write the data to the wav file
            while (true)
            {
                int read = vs.readPcm(buffer, 0, buffer.length);
                for (int i = 0; i < read; i += 2)
                {
                    // swap from big endian to little endian
                    byte tB = buffer[i];
                    buffer[i] = buffer[i + 1];
                    buffer[i + 1] = tB;
                }
                //outFile.write(buffer, 0, read);
                len += read;
            }
        }
        catch (EndOfOggStreamException e)
        {
            // not really an error, but we've
            // reached the end of the vorbis stream
            // and so exit the loop
        }
        return null;
    }

    /**
     * Save data as mono WAV file
     *
     * @param dat      Points to sampling data
     * @param filename Name of new file
     * @throws Exception If something's gone wrong
     */
    public static void saveWave16 (Wave16 dat, String filename) throws Exception
    {
        try (FileOutputStream fo = new FileOutputStream(filename))
        {
            ReverseOutputStream reverseOutputStream = new ReverseOutputStream(fo);

            // Write WAVE header
            reverseOutputStream.write("RIFF".getBytes());
            reverseOutputStream.writeReverseInt(2 * dat.data.length + 44 - 8);
            reverseOutputStream.write("WAVE".getBytes());
            reverseOutputStream.write("fmt ".getBytes());
            reverseOutputStream.writeInt(0x10000000);
            reverseOutputStream.writeShort(0x0100);
            reverseOutputStream.writeReverseShort((short) 1);
            reverseOutputStream.writeReverseInt(dat.samplingRate);
            reverseOutputStream.writeReverseInt(16 / 8 * dat.samplingRate);
            reverseOutputStream.writeReverseShort((short) (16 / 8));
            reverseOutputStream.writeReverseShort((short) 16);
            reverseOutputStream.write("data".getBytes());
            reverseOutputStream.writeReverseInt(dat.data.length * 2);

            // Write WAVE data
            reverseOutputStream.writeReverseShortArray(dat.toShortArray());

            fo.flush();
        }
    }


    /**
     * Loads Wave16 from file
     *
     * @param filename file path
     * @return The loaded Wave16 object
     * @throws Exception if anything's gone wrong
     */
    public static Wave16 loadWave16 (String filename) throws Exception
    {
        WavFile wv = WavFile.openWavFile(new File(filename));
        double[] frames = new double[(int)wv.getNumFrames() * wv.getNumChannels()];

        wv.readFrames(frames, frames.length);
        frames = Tools.fitValues(frames);
        if (wv.getNumChannels() == 2)
            frames = Tools.combineStereo(frames); // Make mono
        return fromDoubleArray(frames, (int) wv.getSampleRate());
    }
}

