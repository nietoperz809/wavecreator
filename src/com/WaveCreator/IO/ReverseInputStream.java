package com.WaveCreator.IO;

import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Data input stream that does some little-endian stuff
 */
class ReverseInputStream extends DataInputStream
{
    /**
     * Constructor: Use as replacement for InputStream
     * @param o Input Stream to reverse
     */
    ReverseInputStream(InputStream o)
    {
        super(o);
    }

    /**
     * Reads a byte array from the stream
     * @param arr Array to be filled
     * @throws Exception If read operation failed
     */
    void readByteArray (byte[] arr) throws Exception
    {
        if (arr.length != read (arr))
        {
            throw new Exception ("ReadByte failed");
        }
    }

    /**
     * Reads a little-endian <b>integer</b>
     * @return The int value
     * @throws Exception the base class exception
     */
    int readReverseInt() throws Exception
    {
        int i = readInt();
        i = i << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >>> 8 | (i >>> 24);
        return i;
    }

    /**
     * Reads a little-endian <b>short</b>
     * @return The int value
     * @throws Exception the base class exception
     */
    short readReverseShort() throws Exception
    {
        int i = readShort();
        i = i << 8 | i >>> 8;
        return ((short) i);
    }

    /**
     * Read x Bytes and contructs a <b>short</b> array
     * @param size Number of bytes
     * @return The new short array
     * @throws Exception the base class exception
     */
    short[] readShortArray(int size) throws Exception
    {
        byte[] b = new byte[size];
        short[] res = new short[size / 2];
        if (read(b) != size)
        {
            throw new Exception("Can't read all requested data");
        }
        for (int s = 0; s < b.length; s += 2)
        {
            res[s/2] = (short)((b[s] & 0xff) + 256 * (b[s+1] & 0xff));
        }
        return res;
    }

    short[] readIntArray(int size) throws Exception
    {
        byte[] b = new byte[size];
        short[] res = new short[size / 4];
        if (read(b) != size)
        {
            throw new Exception("Can't read all requested data");
        }
        for (int s = 0; s < b.length; s += 4)
        {
            int temp = (short)((b[s] & 0xff)
                    + 256 * (b[s+1] & 0xff)
                    + 65536 * (b[s+2] & 0xff)
                    + 16777216 * (b[s+3] & 0xff)
            );
            res[s/4] = (short)(temp);
        }
        return res;
    }

    /**
     * ???
     * @param size Number of bytes
     * @return The new short array
     * @throws Exception If read failed
     */
    short[] readByteArrayAsShort(int size) throws Exception
    {
        byte[] b = new byte[size];
        short[] res = new short[size / 2];
        if (read(b) != size)
        {
            throw new Exception("Can't read all requested data");
        }
        for (int s = 0; s < b.length; s += 2)
        {
            res[s/2] = (short)((b[s] & 0xff)*128);
        }
        return res;
    }
}
