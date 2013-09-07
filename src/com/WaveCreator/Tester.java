package com.WaveCreator;

/**
 * To change this template use File | Settings | File Templates.
 */
public class Tester
{
    public static void main(String... args) throws Exception
    {
        Wave16 s1 = new Wave16().functionsGenerators.curveSine(22050, 1000);
        FrameManager.getInstance().createFrame(s1, "Starting frame");
    }
}

