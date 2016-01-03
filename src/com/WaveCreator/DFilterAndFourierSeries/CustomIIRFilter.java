package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:08:52
 */
class CustomIIRFilter extends IIRFilterType
{
    int npoles, nzeros;

    public CustomIIRFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    int select ()
    {
        dFilterFrame.auxLabels[0].setText("# of Pole Pairs");
        dFilterFrame.auxBars[0].setMaximum(10);
        dFilterFrame.auxBars[0].setValue(dFilterFrame.lastPoleCount / 2);
        return 1;
    }

    void setup ()
    {
        npoles = nzeros = dFilterFrame.auxBars[0].getValue() * 2;
    }

    int getPoleCount ()
    {
        return npoles;
    }

    int getZeroCount ()
    {
        return nzeros;
    }

    void getPole (int i, Complex c1)
    {
        c1.set(dFilterFrame.customPoles[i]);
    }

    void getZero (int i, Complex c1)
    {
        c1.set(dFilterFrame.customZeros[i]);
    }

    void getInfo (String x[])
    {
        x[0] = "Custom IIR";
        x[1] = npoles + " poles and zeros";
    }

    void editPoleZero (Complex c)
    {
        if (c.mag > 1.1)
        {
            return;
        }
        if (dFilterFrame.selectedPole != -1)
        {
            dFilterFrame.customPoles[dFilterFrame.selectedPole].set(c);
            dFilterFrame.customPoles[dFilterFrame.selectedPole ^ 1].set(c.re, -c.im);
        }
        if (dFilterFrame.selectedZero != -1)
        {
            dFilterFrame.customZeros[dFilterFrame.selectedZero].set(c);
            dFilterFrame.customZeros[dFilterFrame.selectedZero ^ 1].set(c.re, -c.im);
        }
    }
}
