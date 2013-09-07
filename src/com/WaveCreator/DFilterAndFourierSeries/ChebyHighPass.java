package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:05:15
*/
class ChebyHighPass extends ChebyLowPass
{
    ChebyHighPass(DFilterFrame f)
    {
        super(f);
        sign = -1;
    }
}
