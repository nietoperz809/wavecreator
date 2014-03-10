package com.WaveCreator.Functions;

import com.WaveCreator.GaloisField256;
import com.WaveCreator.MatrixRoutines.DMatrix;
import com.WaveCreator.MatrixRoutines.DMatrixEvd;
import com.WaveCreator.MatrixRoutines.DMatrixLud;
import com.WaveCreator.MatrixRoutines.DMatrixQrd;
import com.WaveCreator.ParamDesc;
import com.WaveCreator.Tools;
import com.WaveCreator.Wave16;
import java.util.Random;
import com.WaveCreator.FFT.*;
import java.util.Arrays;

/**
 * New Class.
 * User: Administrator
 * Date: 31.12.2008
 * Time: 18:56:12
 */
public final class FunctionsTesting extends Functions
{
    public FunctionsTesting(Wave16 base)
    {
        super(base);
    }

///////////////////////////////////////////////// BEGIN Test functions //////////////////////////////////////////////////

    private static final short[] bz1 = {0x0001, 0x0002, 0x0004, 0x0008,
                                 0x0010, 0x0020, 0x0040, 0x0080,
                                 0x0100, 0x0200, 0x0400, 0x0800,
                                 0x1000, 0x2000, 0x4000, (short)0x8000};

    private static final short[] bz2 = {0x0001, 0x0100, 0x0002, 0x0200,
                                 0x0004, 0x0400, 0x0008, 0x0800,
                                 0x0010, 0x1000, 0x0020, 0x2000,
                                 0x0040, 0x4000, 0x0080, (short)0x8000};

    private short bitZip (short a)
    {
        short x = 0;
        for (int s=0; s<16; s++)
        {
            if ((a & bz1[s]) != 0)
                x |= bz2[s];
        }
        return x;
    }

    private short bitUnzip (short a)
    {
        short x = 0;
        for (int s=0; s<16; s++)
        {
            if ((a & bz2[s]) != 0)
                x |= bz1[s];
        }
        return x;
    }

    public Wave16 zip ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            out.data[s] = bitZip ((short)m_base.data[s]);
        }
        return out;
    }

    public Wave16 unzip ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            out.data[s] = bitUnzip ((short)m_base.data[s]);
        }
        return out;
    }

    /////////////////////////////////////////////////////////////////////////////
    
    public Wave16 addX ()
    {
        Wave16 out = m_base.createEmptyCopy();
        double step = (Wave16.MAX_VALUE-Wave16.MIN_VALUE) / m_base.data.length;
        for (int s=0; s<m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s]+(s*step);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 subX ()
    {
        Wave16 out = m_base.createEmptyCopy();
        double step = (Wave16.MAX_VALUE-Wave16.MIN_VALUE) / m_base.data.length;
        for (int s=0; s<m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s]-(s*step);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Multiplies samples with x-value
     * @return The new sample array
     */
    public Wave16 multX ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s]*(s+1);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    /**
     * Divides samples by x-value
     * @return The new sample array
     */
    public Wave16 divX ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            out.data[s] = m_base.data[s]/(s+1);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

//    private Point zorder_next (Point p)
//    {
//        int b = 1;
//        Point out = (Point) p.clone();
//        do
//        {
//            out.x ^= b;
//            b &= ~out.x;
//            out.y ^= b;
//            b &= ~out.y;
//            b <<= 1;
//        }
//        while (b != 0);
//        return out;
//    }
//
//    private int zorder_next_int (int x)
//    {
//        Point pt = new Point (x%256, x/256);
//        pt = zorder_next (pt);
//        return pt.y * 256 + pt.x;
//    }
//
//    private Point zorder_prev (Point p)
//    {
//        int b = 1;
//        Point out = (Point) p.clone();
//        do
//        {
//            out.x ^= b;
//            b &= out.x;
//            out.y ^= b;
//            b &= out.y;
//            b <<= 1;
//        }
//        while (b != 0);
//        return out;
//    }
//
//    private int zorder_prev_int (int x)
//    {
//        Point pt = new Point (x/256, x%256);
//        pt = zorder_prev (pt);
//        return pt.x * 256 + pt.y;
//    }
//
//    public Wave16 zorder ()
//    {
//        Wave16 out = m_base.createEmptyCopy();
//        for (int s=0; s<m_base.data.length; s++)
//        {
//            out.data[s] = zorder_next_int ((int)m_base.data[s]);
//        }
//        return out;
//    }

    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns list of best two divisors
     * @param x Input value
     * @return Array of two elements
     */
    private long[] bestTwoDivisors(long x)
    {
        long[] out = new long[2];
        long sqr = (long)Math.sqrt(x);
        for (long s=sqr; s>=1; s--)
        {
            //System.out.printf ("%d %d %d\n", s, x%s, x/s);
            if (x%s == 0)
            {
                out[0] = s;
                out[1] = x/s;
                break;
            }
        }
        return out;
    }

    /**
     * Calculates sum of 'best' divisors
     * that is, both integer divisors which are nearest to the square root
     * @return A new wave
     */
    public Wave16 bestDivisorsSum ()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            double sign = Math.signum(m_base.data[s]);
            long[] d = bestTwoDivisors ((long)Math.abs(m_base.data[s]));
            out.data[s] = (d[0]+d[1]) * sign;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////

    // Field helper function
    private double myfield (long x, double base)
    {
        double mult = 1.0;
        double out = 0;
        while (x != 0)
        {
            if ((x & 1) == 1)
            {
                out = out + mult;
            }
            mult *= base;
            x >>>= 1;
        }
        return out;
    }

    // QSum helper function
    private int qSum (int v, int mod)
    {
        int sum = 0;
        while (v != 0)
        {
            sum = sum + (v % mod);
            v = v / mod;
        }
        return sum;
    }

    private int qSumSum (int v, int max)
    {
        int sum = 0;
        for (int s=2; s<=max; s++)
        {
            sum = sum + qSum (v, s);
        }
        return sum;
    }

    private int qSumMult (int v, int max)
    {
        int sum = qSum (v, 2);
        for (int s=3; s<=max; s++)
        {
            sum = sum * qSum (v, s);
        }
        return sum;
    }

    public Wave16 fieldFunction (@ParamDesc("Field Base")double base)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            double sign = Math.signum(m_base.data[s]);
            double v = myfield ((long)Math.abs(m_base.data[s]), base);
            out.data[s] = v * sign;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 toPositiveByteRange()
    {
        Wave16 out = new Wave16();
        out.samplingRate = m_base.samplingRate;
        out.data = Tools.fitValuesToPositiveByteRange(m_base.data);
        return out;
    }

    public Wave16 toByteRange()
    {
        Wave16 out = new Wave16();
        out.samplingRate = m_base.samplingRate;
        out.data = Tools.fitValuesToByteRange(m_base.data);
        return out;
    }

    public Wave16 galoisMult (@ParamDesc("Positive value 1...255")int mult)
    {
        Wave16 w = toPositiveByteRange();
        GaloisField256 gf = new GaloisField256();
        for (int s=0; s<w.data.length; s++)
        {
            w.data[s] = gf.Product((int)w.data[s], mult);
        }
        w.data = Tools.fitValues(w.data);
        return w;
    }

    public Wave16 galoisDiv (@ParamDesc("Positive value 1...255")int mult)
    {
        Wave16 w = toPositiveByteRange();
        GaloisField256 gf = new GaloisField256();
        for (int s=0; s<w.data.length; s++)
        {
            w.data[s] = gf.Quotient((int)w.data[s], mult);
        }
        w.data = Tools.fitValues(w.data);
        return w;
    }

    public Wave16 digitSum (@ParamDesc("Base of numbering system")int mod)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            int v = (int)Math.abs(Math.round(m_base.data[s]));
            out.data[s] = qSum(v, mod);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 digitSumSum (@ParamDesc("2...Last base")int max)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            int v = (int)Math.abs(Math.round(m_base.data[s]));
            out.data[s] = qSumSum(v, max);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 digitSumMult (@ParamDesc("2...Last base")int max)
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            int v = (int)Math.abs(Math.round(m_base.data[s]));
            out.data[s] = qSumMult(v, max);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 pythagoras()
    {
        Wave16 out = new Wave16(m_base.data.length-1, m_base.samplingRate);
        for (int s=1; s<m_base.data.length; s++)
        {
            double p = Math.sqrt(m_base.data[s]*m_base.data[s]+m_base.data[s-1]*m_base.data[s-1]);
            out.data[s-1] = p * Math.signum(m_base.data[s]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    // Works like FFT
    public Wave16 dftImaginary() throws Exception
    {
        if (m_base.data.length > 20000)
        {
            throw new Exception ("Wave too big");
        }
        Wave16 out = new Wave16 (m_base.data.length/2, m_base.samplingRate);
        for (int k=0; k<m_base.data.length/2; k++)
        {
            for (int i=0; i<m_base.data.length; i++)
            {
                out.data[k] = out.data[k] - m_base.data[i] * Math.sin (2*Math.PI*k*i/m_base.data.length);
            }
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    // Works like FFT
    public Wave16 dftReal() throws Exception
    {
        if (m_base.data.length > 20000)
        {
            throw new Exception ("Wave too big");
        }
        Wave16 out = new Wave16 (m_base.data.length/2, m_base.samplingRate);
        for (int k=0; k<m_base.data.length/2; k++)
        {
            for (int i=0; i<m_base.data.length; i++)
            {
                out.data[k] = out.data[k] + m_base.data[i] * Math.cos (2*Math.PI*k*i/m_base.data.length);
            }
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 aaMatrixEigenVectors()
    {
        double[][] mat = m_base.createQuaraticMatrix();
        DMatrix dm = new DMatrix (mat);
        DMatrixEvd evd = new DMatrixEvd (dm);
        Wave16 wv = m_base.fromMatrix (evd.getV().getArray());
        wv.data = Tools.fitValues(wv.data);
        return wv;
    }

    public Wave16 aaMatrixBlockDiagonalEigenValues()
    {
        double[][] mat = m_base.createQuaraticMatrix();
        DMatrix dm = new DMatrix (mat);
        DMatrixEvd evd = new DMatrixEvd (dm);
        Wave16 wv = m_base.fromMatrix (evd.getD().getArray());
        wv.data = Tools.fitValues(wv.data);
        return wv;
    }

    public Wave16 aaMatrixLud_U()
    {
        double[][] mat = m_base.createQuaraticMatrix();
        DMatrix dm = new DMatrix (mat);
        DMatrixLud evd = new DMatrixLud (dm);
        Wave16 wv = m_base.fromMatrix (evd.getU().getArray());
        wv.data = Tools.fitValues(wv.data);
        return wv;
    }

    public Wave16 aaMatrixLud_L()
    {
        double[][] mat = m_base.createQuaraticMatrix();
        DMatrix dm = new DMatrix (mat);
        DMatrixLud evd = new DMatrixLud (dm);
        Wave16 wv = m_base.fromMatrix (evd.getL().getArray());
        wv.data = Tools.fitValues(wv.data);
        return wv;
    }

    public Wave16 aaMatrixQrd_Q()
    {
        double[][] mat = m_base.createQuaraticMatrix();
        DMatrix dm = new DMatrix (mat);
        DMatrixQrd evd = new DMatrixQrd (dm);
        Wave16 wv = m_base.fromMatrix (evd.getQ().getArray());
        wv.data = Tools.fitValues(wv.data);
        return wv;
    }

    public Wave16 aaMatrixQrd_R()
    {
        double[][] mat = m_base.createQuaraticMatrix();
        DMatrix dm = new DMatrix (mat);
        DMatrixQrd evd = new DMatrixQrd (dm);
        Wave16 wv = m_base.fromMatrix (evd.getR().getArray());
        wv.data = Tools.fitValues(wv.data);
        return wv;
    }

    public Wave16 aaTestTransform(@ParamDesc("Length of partitions")int step) // Test !!!
    {
        Wave16[] arr = m_base.functionsSpecialEffects.partitionize(step);
        for (int s=0; s<arr.length; s++)
        {
            arr[s] = arr[s].functionsMathematical.floorTo((double)1/(double)step);
            for (int n=0; n<arr[s].data.length; n++)
            {
                if (arr[s].data[n] < 0)
                    arr[s].data[n] -= n;
                else
                    arr[s].data[n] += n;
            }
            if ((s & 1) == 0)
                arr[s] = arr[s].functionsReorder.sortAscending();
            else
                arr[s] = arr[s].functionsReorder.sortDescending();

        }
        return m_base.combineAppend (arr);
    }

    public Wave16 aaInverseTestTransform(@ParamDesc("Length of partitions")int step) // Test !!!
    {
        Wave16[] arr = m_base.functionsSpecialEffects.partitionize(step);
        Wave16[] at = new Wave16[arr.length];
        for (int s=0; s<arr.length; s++)
        {
            Wave16 a0 = arr[s];

            at[s] = a0.createEmptyCopy();
            for (int n=0; n<a0.data.length; n++)
            {
                int idx = (int)(Math.abs(a0.data[n]%step));
                if (a0.data[n] < 0)
                    at[s].data[idx] = a0.data[n] + idx;
                else
                    at[s].data[idx] = a0.data[n] - idx;
            }
        }
        return m_base.combineAppend (at);
    }

    public Wave16 aalog()
    {
        Wave16 out = m_base.functionsMathematical.normalize();
        for (int s=0; s<m_base.data.length; s++)
        {
            double sign = Math.signum(out.data[s]);
            if (out.data[s] != 0.0)
                out.data[s] = sign*Math.log(Math.abs(out.data[s]));
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    public Wave16 aaexp()
    {
        Wave16 out = m_base.functionsMathematical.normalize();
        for (int s=0; s<m_base.data.length; s++)
        {
            if (out.data[s] != 0.0)
                out.data[s] = Math.exp(out.data[s]);
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    private double[] aaReorder (double[] in)
    {
        double[] out = new double[in.length];
        int half = in.length/2;
        for (int s=0; s<half; s++)
        {
            int idx = s*2;
            out[s] = in[idx];
            out[s+half] = in[idx+1];
        }
        return out;
    }

    public Wave16 aaReorder (@ParamDesc("Number of reordering steps")int num)
    {
        double[] out = m_base.data.clone();
        for (int s=0; s<num; s++)
        {
            out = aaReorder(out);
        }
        return new Wave16(out, m_base.samplingRate);
    }

    public Wave16 aa3NPlus1()
    {
        Wave16 out = m_base.createEmptyCopy();
        for (int s=0; s<m_base.data.length; s++)
        {
            double sign = Math.signum (m_base.data[s]);
            int sh = (short)Math.abs(m_base.data[s]);
            if ((sh & 1) == 1)
            {
                out.data[s] = (3*sh+1)/2;
            }
            else
            {
                out.data[s] = sh/2;
            }
            out.data[s] *= sign;
        }
        out.data = Tools.fitValues(out.data);
        return out;
    }

    static class MathMusic
    {
        static final double A = 1.4;      //Default value for a in Henon equations
        static final double B = 0.3;      //Default value for b in Henon equations
        static final double X_MAX = 2.0;  //Max value for X, used for normalizing values
        static final double Y_MAX = 0.5;  //Analogous to X_MAX

        protected int[] minorScale = {440, 470, 496, 528, 564, 634, 660, 704, 760, 792, 844, 880, 939, 1056, 1276};
        
        static final int SAMPLERATE = 22000;
        
        Wave16 pause = FunctionsGenerators.constant(SAMPLERATE, 200, 0);
        
        public Wave16 makeMandel (int notes)
        {
            Random r = new Random(System.currentTimeMillis());
            Complex c, z, zold;
            double normr, normi;
            int pitchr, pitchi, pitchOld, repeatCount;
            Wave16 ret = new Wave16(0, SAMPLERATE);
            final int ITERATIONS = 20;

            while (notes-- >= 0)
            {
                c = new Complex(r.nextDouble() * 4.0 - 2.0, r.nextDouble() * 4.0 - 2.0);
                z = new Complex(0,0);
                zold = z;
                repeatCount = 0;
                pitchOld = -1;
                
                for (int k = 0; k < ITERATIONS; k++)
                {
                    z = (z.times(z)).plus(c); // z^2 + c
                    if (modulus(z) > 2)
                    {
                        break;
                    }
                    normr = (z.re + 2.0) / 4.0;
                    normi = (z.im + 2.0) / 4.0;
                    pitchr = (int) (normr * (minorScale.length - 1));
                    pitchi = (int) (normi * (minorScale.length - 1));

                    Wave16 w1 = FunctionsGenerators.curveSine(SAMPLERATE, SAMPLERATE, minorScale[pitchr], minorScale[pitchi]);
                    ret = Wave16.combineAppend(ret, w1, pause);

                    if (zold.equals(z) || modulus(z) > 2)
                    {
                        break;
                    }

                    if (pitchr == pitchOld)
                    {
                        repeatCount++;
                    }
                    if (repeatCount >= 3)
                    {
                        break;
                    }
                    pitchOld = pitchr;
                    zold = z;
                }
            }
            return ret;
        }

        private double modulus(Complex c)
        {
            return Math.sqrt(c.re * c.re + c.im * c.im);
        }
        
        public Wave16 makeHenon (int notes)
        {
            double x = Math.random(); //0.0;
            double y = Math.random(); //0.0;
            double xnew, ynew, normx, normy;
            int pitchx, pitchy;
            
            Wave16 ret = new Wave16(0, SAMPLERATE);
            
            while (notes-- >= 0)
            {
                normx = (x + X_MAX)/(2*X_MAX);
                normy = (y + Y_MAX)/(2*Y_MAX);
                pitchx = (int)(normx*(minorScale.length-1));
                pitchy = (int)(normy*(minorScale.length-1));
                
                Wave16 w1 = FunctionsGenerators.curveSine(SAMPLERATE, SAMPLERATE, minorScale[pitchx], minorScale[pitchy]);
                ret = Wave16.combineAppend(ret, w1, pause);
                
                //System.out.println ("("+minorScale[pitchx]+"/"+minorScale[pitchy]+")");
                //System.out.println ("("+pitchx+"/"+pitchy+")");

                //Henon equations:
                xnew = 1 + y - A*x*x;
                ynew = B*x;
        
                x = xnew;
                y = ynew;
            }
            return ret;
        }
    }
    
    /*
    public static void testHenon ()
    {
        Henon h = new Henon();
        h.make(100);
    }
    */
    
    public Wave16 HenonMusic (@ParamDesc("notes")int num)
    {
        MathMusic h = new MathMusic();
        return h.makeHenon(num);
    }

    public Wave16 MandelMusic (@ParamDesc("notes")int num)
    {
        MathMusic h = new MathMusic();
        return h.makeMandel(num);
    }
    
    public Wave16 lindenMayer (@ParamDesc("Axiom")String axiom, 
                               @ParamDesc("Rules")String[] rules,
                               @ParamDesc("Recursions")int[] recursions,
                               @ParamDesc("Final Rules")String[] endrules)
    {
        System.out.println (Arrays.toString(rules));
        System.out.println (Arrays.toString(recursions));
        Wave16 out = m_base.createEmptyCopy();
        return out;
    }
    
    /////////////////////////////////////////////////// END Test functions //////////////////////////////////////////////////
}
