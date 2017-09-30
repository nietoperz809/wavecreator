package com.WaveCreator.Helpers;

//import com.google.common.math.LongMath;

import com.WaveCreator.Wave16;
import com.WaveCreator.Wave16AmplitudeInfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class Tools
{

    /**
     * @param in
     * @return
     */
    public static long lcm (long[] in)
    {
        if (in.length == 1)
        {
            return in[0];
        }
        long res = lcm(in[0], in[1]);
        if (in.length > 2)
        {
            for (int s = 2; s < in.length; s++)
            {
                res = lcm(res, in[s]);
            }
        }
        return res;
    }

    public static long lcm (long a, long b)
    {
        return a * b / gcd(a, b);
    }

    /**
     * Computes the GCD of a and b
     *
     * @param a Input a
     * @param b Input b
     * @return The GCD
     */
    public static long gcd (long a, long b)
    {
        // a must be > b
        if (a <= b)
        {
            long x = a;
            a = b;
            b = x;
        }
        while (b != 0)
        {
            long r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    /**
     * Calculates next 2's power of input value Thus, 127 gives 128, 128 gives
     * 128 and 129 gives 256
     *
     * @param in The input value
     * @return The 2's power
     */
    public static int nextPowerOfTwo (int in)
    {
        return (int) Math.pow(2.0, Math.ceil(Math.log(in) / Math.log(2.0)));
    }

    public static float[] combineStereo (float[] in)
    {
        float[] out = new float[in.length / 2];
        for (int s = 0; s < in.length; s += 2)
        {
            out[s / 2] = (in[s] + in[s + 1]) / 2;
        }
        return out;
    }

    /**
     * Fit values into defined range
     *
     * @param in array of float value
     * @return array of converted float values
     */
    public static float[] fitValues (float[] in)
    {
        float[] out = new float[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        float div = am.span / (Wave16.MAX_VALUE - Wave16.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = in[s] / div + Wave16.MIN_VALUE - am.min;
            if (Float.isInfinite(out[s]) || Float.isNaN(out[s]))
            {
                out[s] = 0.0f;
            }
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     *
     * @param in array of float value
     * @return array of converted float values
     */
    public static float[] fitValuesToPositiveByteRange (float[] in)
    {
        float[] out = new float[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        float div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = 128 + Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     *
     * @param in array of float value
     * @return array of converted float values
     */
    public static float[] fitValuesToByteRange (float[] in)
    {
        float[] out = new float[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        float div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }

    public static String[] listPackage (String path, boolean maindironly)
    {
        try
        {
            int pathLen = path.length();
            URL url1 = ClassLoader.getSystemResource(path);

            String jarFileName;
            JarFile jf;
            Enumeration<JarEntry> jarEntries;
            String entryName;
            ArrayList<String> list = new ArrayList<>();

            jarFileName = URLDecoder.decode(url1.getFile(), "UTF-8");
            jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
            jf = new JarFile(jarFileName);
            jarEntries = jf.entries();
            while (jarEntries.hasMoreElements())
            {
                entryName = jarEntries.nextElement().getName();
                if (entryName.startsWith(path))
                {
                    entryName = entryName.substring(pathLen + 1);
                    if (!entryName.isEmpty())
                    {
                        if (!maindironly)
                            list.add(entryName);
                        else if (entryName.indexOf("/") == entryName.length()-1)
                            list.add(entryName);
                    }
                }
            }
            String[] arr = new String[list.size()];
            list.toArray(arr);
            return arr;
        }
        catch (Exception ex)
        {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void copyStream (InputStream is, OutputStream os) throws Exception
    {
        final int buffer_size = 1024;

        byte[] bytes = new byte[buffer_size];
        for (;;)
        {
            int count = is.read(bytes, 0, buffer_size);
            if (count == -1)
            {
                break;
            }
            os.write(bytes, 0, count);
        }
    }

    /**
     * Read BE bytes inzo float array
     * @param inputBytes array of bytes
     * @param bytesPerFrame size of frame, 1...8
     * @return newly created float array
     */
    public static float[] readBE (byte[] inputBytes, int bytesPerFrame)
    {
        int numframes = inputBytes.length/bytesPerFrame;
        float[] outputWords = new float[numframes];
        for (int i=0; i<inputBytes.length; i+=bytesPerFrame)
        {
            float n = 0;
            for (int s = 0; s < bytesPerFrame; s++)
            {
                n = (n * 256) + (inputBytes[i+s]);
            }
            outputWords[i/bytesPerFrame] = n;
        }
        return outputWords;
    }

    public static float[] readLE (byte[] inputBytes, int bytesPerFrame)
    {
        int numframes = inputBytes.length/bytesPerFrame;
        float[] outputWords = new float[numframes];
        for (int i=0; i<inputBytes.length; i+=bytesPerFrame)
        {
            float n = 0;
            for (int s = bytesPerFrame-1; s > -1; s--)
            {
                n = (n * 256) + (inputBytes[i+s]);
            }
            outputWords[i/bytesPerFrame] = n;
        }
        return outputWords;
    }

    public static int multiHash (Object... obs)
    {
        int ret = 1;
        for (Object o: obs )
        {
            ret = ret * o.hashCode();
        }
        return ret;
    }
}
