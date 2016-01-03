package com.WaveCreator.Functions;

import com.WaveCreator.ParamDesc;
import com.WaveCreator.Wave16;

import java.util.Arrays;

/**
 * New Class.
 * User: Administrator
 * Date: 31.12.2008
 * Time: 21:07:25
 */
public final class FunctionsReorder extends Functions
{
    public FunctionsReorder (Wave16 base)
    {
        super(base);
    }

    /**
     * --> Stretches this object to have a quadratic number of samples
     * --> Makes a matrix from it an transposes the matrix
     * @return The new object
     */
    public Wave16 transposeQuadraticMatrix()
    {
        Wave16 w1 = m_base.functionsLength.stretchToQuadratic();
        Wave16 out = w1.createEmptyCopy();
        int sqr = (int)Math.sqrt(w1.data.length);
        {
            for (int i=0; i<sqr; i++)
            {
                for (int j=0; j<sqr; j++)
                {
                    out.data[j*sqr+i] = w1.data[i*sqr+j];
                }
            }
        }
        return out;
    }

    /**
     * --> Stretches this object to have a number of samples that is devidable by <b>rowlen</b>
     * --> Makes a matrix from it an transposes the matrix
     * @param rowlen Length of matrix rows
     * @return The new object
     */
    public Wave16 transposeMatrix (@ParamDesc("Length of matrix rows")int rowlen)
    {
        int collen = (int)Math.ceil((double)m_base.data.length/(double)rowlen);
        Wave16 w1 = m_base.functionsLength.stretchToNumberOfSamples (collen*rowlen);
        Wave16 out = w1.createEmptyCopy();
        {
            for (int s=0; s<rowlen; s++)
            {
                for (int n=0; n<collen; n++)
                {
                    out.data[n*rowlen+s] = w1.data[s*collen+n];
                }
            }
        }
        return out;
    }

    /**
     * Inverse function of transposeMatrix
     * @param rowlen Length of matrix rows
     * @return The new object
     */
    public Wave16 transposeMatrixReverse (@ParamDesc("Length of matrix rows")int rowlen)
    {
        int collen = (int)Math.ceil((double)m_base.data.length/(double)rowlen);
        Wave16 w1 = m_base.functionsLength.stretchToNumberOfSamples (collen*rowlen);
        Wave16 out = w1.createEmptyCopy();
        {
            for (int s=0; s<collen; s++)
            {
                for (int n=0; n<rowlen; n++)
                {
                    out.data[n*collen+s] = w1.data[s*rowlen+n];
                }
            }
        }
        return out;
    }
    
    /**
     * Swaps sampling values
     * @param num Number of consecutive values to swap
     * @return Swapped sampling array
     */
    public Wave16 swap (@ParamDesc("Number of consecutive samples to swap") int num)
    {
        Wave16[] parts = m_base.functionsSpecialEffects.partitionize(num);
        int start;
        if ((parts.length & 1) == 1)
        {
            start = 1;
        }
        else
        {
            start = 0;
        }
        for (int s = start; s < parts.length; s += 2)
        {
            Wave16 tmp = parts[s];
            parts[s] = parts[s + 1];
            parts[s + 1] = tmp;
        }
        return Wave16.combineAppend(parts);
    }

    /**
     * Rotates sample array to the right
     * @param num number of rotations
     * @return The rotated array
     */
    public Wave16 rotateRight (@ParamDesc("Number of samples to rotata") int num)
    {
        int rot = num % m_base.data.length;     // --> to the right
        int diff = m_base.data.length - rot;    // difference
        Wave16 res = m_base.createEmptyCopy();
        System.arraycopy(m_base.data, 0, res.data, rot, diff);
        System.arraycopy(m_base.data, diff, res.data, 0, rot);
        return res;
    }

    /**
     * Shifts sampling array to the right
     * First <b>num</b> samples become zero
     * @param num number of samples to shift
     * @return new sampling array
     */
    public Wave16 shiftRight (@ParamDesc("Number of samples to shift") int num)
    {
        int rot = num % m_base.data.length;     // --> to the right
        int diff = m_base.data.length - rot;    // difference
        Wave16 res = m_base.createEmptyCopy();
        System.arraycopy(m_base.data, 0, res.data, rot, diff);
        return res;
    }

    /**
     * Rotates sample array left
     * @param num Number of rotations
     * @return The rotated array
     */
    public Wave16 rotateLeft (@ParamDesc("Number of samples to rotate") int num)
    {
        int rot = num % m_base.data.length;     // to the left
        int diff = m_base.data.length - rot;    // difference
        Wave16 res = m_base.createEmptyCopy();
        System.arraycopy(m_base.data, rot, res.data, 0, diff);
        System.arraycopy(m_base.data, 0, res.data, diff, rot);
        return res;
    }

    /**
     * Rotates sample array left
     * @param num Number of rotations
     * @return The rotated array
     */
    public Wave16 shiftLeft (@ParamDesc("Number of samples to rotate") int num)
    {
        int rot = num % m_base.data.length;     // to the left
        int diff = m_base.data.length - rot;    // difference
        Wave16 res = m_base.createEmptyCopy();
        System.arraycopy(m_base.data, rot, res.data, 0, diff);
        //System.arraycopy(m_base.data, 0, res.data, diff, rot);
        return res;
    }

    /**
     * Sorts all samples in ascending order
     * @return The sorted Wave16 object
     */
    public Wave16 sortAscending()
    {
        Wave16 out = m_base.createEmptyCopy();
        System.arraycopy(m_base.data, 0, out.data, 0, m_base.data.length);
        Arrays.sort(out.data);
        return out;
    }

    /**
     * Sorts all samples in descending order
     * @return The sorted Wave16 object
     */
    public Wave16 sortDescending()
    {
        return sortAscending().functionsReorder.reverse();
    }

    /**
     * Reverses a sampling object
     * @return The reverted object
     */
    public Wave16 reverse()
    {
        Wave16 res = m_base.createEmptyCopy();

        for (int s = 0; s < m_base.data.length; s++)
        {
            res.data[m_base.data.length - 1 - s] = m_base.data[s];
        }
        return res;
    }
}
