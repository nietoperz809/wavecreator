package com.WaveCreator;

import com.WaveCreator.Functions.*;

/**
 * To change this template use File | Settings | File Templates.
 */
public class Tester
{

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception
    {
        //FunctionsTesting.testHenon();
        
        Wave16 s1 = FunctionsGenerators.curveSine(22050, 2000);
        FrameManager.getInstance().createFrame(s1, "Starting frame");
    }
}

