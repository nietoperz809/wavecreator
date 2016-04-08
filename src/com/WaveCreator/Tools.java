package com.WaveCreator;

//import com.google.common.math.LongMath;

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

    public static double[] combineStereo (double[] in)
    {
        double[] out = new double[in.length / 2];
        for (int s = 0; s < in.length; s += 2)
        {
            out[s / 2] = (in[s] + in[s + 1]) / 2;
        }
        return out;
    }

    /**
     * Fit values into defined range
     *
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValues (double[] in)
    {
        double[] out = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Wave16.MAX_VALUE - Wave16.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = in[s] / div + Wave16.MIN_VALUE - am.min;
            if (Double.isInfinite(out[s]) || Double.isNaN(out[s]))
            {
                out[s] = 0.0;
            }
        }
        return out;
    }

    /**
     * Fit values into BYTE range
     *
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValuesToPositiveByteRange (double[] in)
    {
        double[] out = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
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
     * @param in array of double value
     * @return array of converted double values
     */
    public static double[] fitValuesToByteRange (double[] in)
    {
        double[] out = new double[in.length];
        Wave16AmplitudeInfo am = new Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Byte.MAX_VALUE - Byte.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = Math.round(in[s] / div + Byte.MIN_VALUE - am.min);
        }
        return out;
    }

    public static String[] listPackage (String path)
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
}
