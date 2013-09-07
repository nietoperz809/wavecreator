package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
* User: Administrator
* Date: 06.01.2009
* Time: 02:11:10
*/
class NoFilter extends FilterType
{
    public NoFilter(DFilterFrame d)
    {
        super(d);
    }

    void getResponse(double w, Complex c)
    {
        c.set(1);
    }

    Filter genFilter()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        f.aList = new double[1];
        f.aList[0] = 1;
        return f;
    }
}
