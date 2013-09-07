package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:23:29
*/
class SincBandPassFilter extends SincBandStopFilter
{
    SincBandPassFilter(DFilterFrame dFilterFrame)
    {
        super (dFilterFrame);
        invert = true;
    }
}
