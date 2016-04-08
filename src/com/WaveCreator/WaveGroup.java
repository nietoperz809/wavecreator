package com.WaveCreator;

import com.WaveCreator.IO.Wave16IO;
import de.jarnbjo.ogg.FileStream;
import de.jarnbjo.ogg.LogicalOggStream;
import de.jarnbjo.ogg.PhysicalOggStream;
import de.jarnbjo.vorbis.VorbisStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 4/7/2016.
 */
public class WaveGroup
{
    private final String _dir = "sounds";
    private final ArrayList<Wave16> _waves = new ArrayList<>();

    public WaveGroup (String subdir) throws Exception
    {
        createGroup (subdir);
    }

    public Wave16[] getWaves()
    {
        return _waves.toArray(new Wave16[_waves.size()]);
    }

    private void createGroup (String subdir) throws Exception
    {
        String path = _dir+"/"+subdir;
        String[] names = Tools.listPackage (path);
        for (String name : names)
        {
            InputStream in = ClassLoader.getSystemResourceAsStream(path + "/" + name);

            Path temp = Files.createTempFile("tmp", ".tmp");
            Files.copy (in, temp, StandardCopyOption.REPLACE_EXISTING);
            Wave16 wv;
            if (name.endsWith("ogg"))
                wv = Wave16IO.loadOgg(temp.toFile());
            else
                wv = Wave16IO.loadWave(temp.toFile());
            temp.toFile().delete();
            _waves.add(wv);
        }
    }

    /**
     * Test func
     *
     * @param args
     */
    public static void main (String[] args) throws Exception
    {
        WaveGroup wv = new WaveGroup ("bassloopes");
        Wave16[] waves = wv.getWaves();
        System.exit(1);
        for (Wave16 w : waves)
        {
            FrameManager.getInstance().createFrame(w, "?");
        }
    }
}
