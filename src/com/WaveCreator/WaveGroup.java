package com.WaveCreator;

import de.jarnbjo.ogg.FileStream;
import de.jarnbjo.ogg.LogicalOggStream;
import de.jarnbjo.ogg.PhysicalOggStream;
import de.jarnbjo.vorbis.VorbisStream;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * Created by Administrator on 4/7/2016.
 */
public class WaveGroup
{
    public WaveGroup (String path)
    {

    }

    /**
     * Test func
     *
     * @param args
     */
    public static void main (String[] args) throws Exception
    {
//        String path = "sounds/basses";
//        String[] s = Tools.listPackage(path);
//        System.out.println(Arrays.toString(s));
//
//        Path temp = Files.createTempFile("resource-", ".ext");
//        Files.copy(ClassLoader.getSystemResourceAsStream(path + "/" + s[0]), temp, StandardCopyOption.REPLACE_EXISTING);
//        FileInputStream input = new FileInputStream(temp.toFile());
//
//        PhysicalOggStream os = new FileStream(new RandomAccessFile(temp.toFile(), "r"));
//        LogicalOggStream los=(LogicalOggStream)os.getLogicalStreams().iterator().next();
//        System.out.println(os);
//
//        // exit, if it is not a Vorbis stream
//        if (los.getFormat() != LogicalOggStream.FORMAT_VORBIS)
//        {
//            System.out.println("This tool only supports Ogg files with Vorbis content.");
//            System.exit(0);
//        }
//
//        long t0 = System.currentTimeMillis();
//
//        // create a Vorbis stream from the logical Ogg stream
//        final VorbisStream vs = new VorbisStream(los);
    }
}
