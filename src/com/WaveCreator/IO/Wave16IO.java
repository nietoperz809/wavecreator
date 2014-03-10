package com.WaveCreator.IO;

import com.WaveCreator.Functions.FunctionsGenerators;
import com.WaveCreator.Wave16;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * To change this template use File | Settings | File Templates.
 */
public class Wave16IO
{
    /**
     * Saves a Wave's raw data
     * @param dat Wave to supply data
     * @param filename Name of the new file
     * @throws Exception Thrown if file operation failed
     */
    public static void saveRaw (Wave16 dat, String filename) throws Exception
    {
        FileImageOutputStream fileImageOutputStream = new FileImageOutputStream (new File(filename));
        fileImageOutputStream.writeInt(dat.data.length);  // First write the length
        fileImageOutputStream.writeInt(dat.samplingRate);  // Then the samplingrate
        fileImageOutputStream.writeDoubles(dat.data, 0, dat.data.length);
        fileImageOutputStream.close();
    }

    /**
     * Load a wave from raw data
     * @param filename Name of file that contains raw samples
     * @return A new Wave object
     * @throws Exception Thrown if file operation failed
     */
    public static Wave16 loadRaw (String filename) throws Exception
    {
        FileImageInputStream fileImageInputStream = new FileImageInputStream(new File(filename));
        int size = fileImageInputStream.readInt(); // First read the length
        int samplingRate = fileImageInputStream.readInt(); // Then get the sampling rate
        double[] arr = new double[size];
        fileImageInputStream.readFully (arr, 0, size);
        fileImageInputStream.close();
        return new Wave16(arr, samplingRate);
    }

    /**
     * Save data as mono WAV file
     * @param dat      Points to sampling data
     * @param filename Name of new file
     * @throws Exception If something's gone wrong
     */
    public static void saveWave16(Wave16 dat, String filename) throws Exception
    {
        FileOutputStream fo = new FileOutputStream(filename);
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
        fo.close();
    }

    /**
     * Loads Wave16 from file
     * @param filename file path
     * @return The loaded Wave16 object
     * @throws Exception if anything's gone wrong
     */
    public static Wave16 loadWave16(String filename) throws Exception
    {
        FileInputStream fi = new FileInputStream(filename);
        ReverseInputStream reverseInputStream = new ReverseInputStream(fi);
        byte[] tmp4 = new byte[4];

        reverseInputStream.readByteArray(tmp4);
        if (!Arrays.equals(tmp4, "RIFF".getBytes()))
        {
            throw new Exception("Not a WAVE file (RIFF)");
        }

        // Length of file
        int filelength = reverseInputStream.readReverseInt() - 8;

        reverseInputStream.readByteArray(tmp4);
        if (!Arrays.equals(tmp4, "WAVE".getBytes()))
        {
            throw new Exception("Not a WAVE file (WAVE)");
        }

        reverseInputStream.readByteArray(tmp4);
        if (!Arrays.equals(tmp4, "fmt ".getBytes()))
        {
            throw new Exception("Not a WAVE file (fmt)");
        }

        int c1 = reverseInputStream.readReverseInt();  // Constant
        short c2 = reverseInputStream.readShort(); // Constant

        int channels = reverseInputStream.readReverseShort();
        if (channels != 1)
        {
            throw new Exception("Can't work with multiple channels");
        }

        int samplingrate = reverseInputStream.readReverseInt();
        int average = reverseInputStream.readReverseInt();
        int bits_per_sample = reverseInputStream.readReverseShort() * 8;

        short c3 = reverseInputStream.readShort(); // Format specific stuff

        //System.out.println (c1+"/"+c2+"/"+c3+"/"+average+"/"+channels+"/"+samplingrate+"/"+bits_per_sample);

        reverseInputStream.readByteArray(tmp4);
        if (!Arrays.equals(tmp4, "data".getBytes()))
        {
            throw new Exception("Not a WAVE file (data)");
        }

        int data_length = reverseInputStream.readReverseInt();

        // Read sampling data
        short[] dat;
        Wave16 samp;
        switch (bits_per_sample)
        {
            case 8:
                dat = reverseInputStream.readByteArrayAsShort(data_length);
                samp = FunctionsGenerators.fromShortArray(dat, samplingrate / 2);
                break;

            case 16:
                dat = reverseInputStream.readShortArray(data_length);
                samp = FunctionsGenerators.fromShortArray(dat, samplingrate);
                break;

            default:
                throw new Exception("Unsupported sample size: " + bits_per_sample);
        }

        fi.close();

        return samp;
    }

}

