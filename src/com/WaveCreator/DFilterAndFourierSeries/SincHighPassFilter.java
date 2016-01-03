package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:23:52
 */
class SincHighPassFilter extends SincLowPassFilter
{
    SincHighPassFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
        invert = true;
    }
}
