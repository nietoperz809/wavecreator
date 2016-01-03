package com.WaveCreator.DFilterAndFourierSeries;

/**
 * New Class.
 * User: Administrator
 * Date: 06.01.2009
 * Time: 02:00:53
 */
class BoxFilter extends FIRFilterType
{
    double cw;
    double r;
    //double norm;
    int n;

    public BoxFilter (DFilterFrame dFilterFrame)
    {
        super(dFilterFrame);
    }

    @Override
    int select ()
    {
        dFilterFrame.auxLabels[0].setText("Fundamental Freq");
        dFilterFrame.auxBars[0].setValue(500);
        dFilterFrame.auxLabels[1].setText("Position");
        dFilterFrame.auxBars[1].setValue(300);
        dFilterFrame.auxLabels[2].setText("Length/Width");
        dFilterFrame.auxBars[2].setValue(100);
        dFilterFrame.auxLabels[3].setText("Order");
        dFilterFrame.auxBars[3].setMaximum(1600);
        dFilterFrame.auxBars[3].setValue(100);
        return 4;
    }

    @Override
    void setup ()
    {
        cw = dFilterFrame.auxBars[0].getValue() * DFilterFrame.pi / 1000.;
        if (cw < .147)
        {
            cw = .147;
        }
        r = dFilterFrame.auxBars[1].getValue() / 1000.;
        n = dFilterFrame.auxBars[3].getValue();
    }

    @Override
    Filter genFilter ()
    {
        DirectFilter f = new DirectFilter(dFilterFrame);
        int nn = 20;
        double ws[][] = new double[nn][nn];
        double mg[][] = new double[nn][nn];
        int i, j, k;
        double px = r * DFilterFrame.pi;
        double py = DFilterFrame.pi / 2;
        double ly = dFilterFrame.auxBars[2].getValue() / 100.;
        for (i = 0; i != nn; i++)
        {
            for (j = 0; j != nn; j++)
            {
                ws[i][j] = cw * Math.sqrt(i * i + j * j / ly);
                mg[i][j] = Math.cos(i * px) * Math.cos(j * py);
            }
        }
        mg[0][0] = 0;
        f.aList = new double[n];
        double sum = 0;
        double ecoef = -2.5 / n;
        for (k = 0; k != n; k++)
        {
            double q = 0;
            for (i = 0; i != nn; i++)
            {
                for (j = 0; j != nn; j++)
                {
                    double ph = k * ws[i][j];
                    q += mg[i][j] * Math.cos(ph);
                }
            }
            f.aList[k] = q * Math.exp(ecoef * k);
            sum += q;
        }
        // normalize
        for (i = 0; i != n; i++)
        {
            f.aList[i] /= sum;
        }
        setResponse(f);
        return f;
    }

    @Override
    void getInfo (String x[])
    {
        x[0] = "Order: " + n;
    }

    @Override
    void setCutoff (double f)
    {
    }
}
