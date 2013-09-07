package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:02:46
*/
class ButterHighPass extends ButterLowPass
{
    ButterHighPass(DFilterFrame f)
    {
        super (f);
        sign = -1;
    }
}
