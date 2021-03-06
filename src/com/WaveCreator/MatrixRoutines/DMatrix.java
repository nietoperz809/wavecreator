/**
 * **************************************************************************
 * Copyright (c) 2005, Colorado School of Mines and others. All rights reserved.
 * This program and accompanying materials are made available under the terms of
 * the Common Public License - v1.0, which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
***************************************************************************
 */
package com.WaveCreator.MatrixRoutines;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A double-precision matrix. Matrix elements are stored in an array of arrays
 * of doubles a[m][n], such that array element a[i][j] corresponds to the i'th
 * row and the j'th column of the m-by-n matrix.
 * <p>
 * This class was adapted from the package Jama, which was developed by Joe
 * Hicklin, Cleve Moler, and Peter Webb of The MathWorks, Inc., and by Ronald
 * Boisvert, Bruce Miller, Roldan Pozo, and Karin Remington of the National
 * Institue of Standards and Technology.
 *
 * @author Dave Hale, Colorado School of Mines
 * @version 2005.12.01
 */
public class DMatrix
{

    /**
     * Returns a new matrix with random elements. The distribution of the random
     * numbers is uniform in the interval [0,1).
     *
     * @param m the number of rows.
     * @param n the number of columns.
     * @return the random matrix.
     */
    public static DMatrix random(int m, int n)
    {
        DMatrix x = new DMatrix(m, n);
        Array.rand(x._a);
        return x;
    }

    /**
     * Returns a new identity matrix.
     *
     * @param m the number of rows.
     * @param n the number of columns.
     * @return the identity matrix.
     */
    public static DMatrix identity(int m, int n)
    {
        DMatrix x = new DMatrix(m, n);
        float[][] xa = x._a;
        int mn = min(m, n);
        for (int i = 0; i < mn; ++i)
        {
            xa[i][i] = 1.0f;
        }
        return x;
    }

    // Used in implementation of toString() above.

    private static String[][] format(float[][] d)
    {
        int m = d.length;
        int n = d[0].length;
        int pg = 6;
        String fg = "% ." + pg + "g";
        int pemax = -1;
        int pfmax = -1;
        for (int i = 0; i < m; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                String s = String.format(fg, d[i][j]);
                s = clean(s);
                int ls = s.length();
                if (s.contains("e"))
                {
                    int pe = (ls > 7) ? ls - 7 : 0;
                    if (pemax < pe)
                    {
                        pemax = pe;
                    }
                }
                else
                {
                    int ip = s.indexOf('.');
                    int pf = (ip >= 0) ? ls - 1 - ip : 0;
                    if (pfmax < pf)
                    {
                        pfmax = pf;
                    }
                }
            }
        }
        String[][] s = new String[m][n];
        String f;
        if (pemax >= 0)
        {
            if (pfmax > pg - 1)
            {
                pfmax = pg - 1;
            }
            int pe = (pemax > pfmax) ? pemax : pfmax;
            f = "% ." + pe + "e";
        }
        else
        {
            f = "% ." + pfmax + "f";
        }
        for (int i = 0; i < m; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                s[i][j] = String.format(f, d[i][j]);
            }
        }
        return s;
    }

    private static String clean(String s)
    {
        int len = s.length();
        int iend = s.indexOf('e');
        if (iend < 0)
        {
            iend = s.indexOf('E');
        }
        if (iend < 0)
        {
            iend = len;
        }
        int ibeg = iend;
        if (s.indexOf('.') > 0)
        {
            while (ibeg > 0 && s.charAt(ibeg - 1) == '0')
            {
                --ibeg;
            }
            if (ibeg > 0 && s.charAt(ibeg - 1) == '.')
            {
                --ibeg;
            }
        }
        if (ibeg < iend)
        {
            String sb = s.substring(0, ibeg);
            s = (iend < len) ? sb + s.substring(iend, len) : sb;
        }
        return s;
    }

    private static int maxlen(String[][] s)
    {
        int max = 0;
        int m = s.length;
        int n = s[0].length;
        for (int i = 0; i < m; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                int len = s[i][j].length();
                if (max < len)
                {
                    max = len;
                }
            }
        }
        return max;
    }

  ///////////////////////////////////////////////////////////////////////////
    // private
    private final int _m; // number of rows
    private final int _n; // number of columns
    private final float[][] _a; // array[_m][_n] of matrix elements

    /**
     * Constructs an m-by-n matrix of zeros.
     *
     * @param m the number of rows.
     * @param n the number of columns.
     */
    public DMatrix(int m, int n)
    {
        _m = m;
        _n = n;
        _a = new float[m][n];
    }

    /**
     * Constructs an m-by-n matrix filled with the specified value.
     *
     * @param m the number of rows.
     * @param n the number of columns.
     * @param v the value.
     */
    public DMatrix(int m, int n, float v)
    {
        this(m, n);
        Array.fill(v, _a);
    }

    /**
     * Constructs a matrix from the specified array. Does not copy array
     * elements into a new array. Rather, the new matrix simply references the
     * specified array.
     * <p>
     * The specified array must be regular. That is, each row much contain the
     * same number of columns, and each column must contain the same number of
     * rows.
     *
     * @param a the array.
     */
    public DMatrix(float[][] a)
    {
        Check.argument(Array.isRegular(a), "array a is regular");
        _m = a.length;
        _n = a[0].length;
        _a = a;
    }

    /**
     * Constructs a copy of the specified matrix.
     *
     * @param a the matrix.
     */
    public DMatrix(DMatrix a)
    {
        this(a._m, a._n, Array.copy(a._a));
    }

    ///////////////////////////////////////////////////////////////////////////

    // package
    DMatrix(int m, int n, float[][] a)
    {
        _m = m;
        _n = n;
        _a = a;
    }

    /**
     * Gets the number of rows in this matrix.
     *
     * @return the number of rows.
     */
    public int getM()
    {
        return _m;
    }

    /**
     * Gets the number of rows in this matrix.
     *
     * @return the number of rows.
     */
    public int getRowCount()
    {
        return _m;
    }

    /**
     * Gets the number of columns in this matrix.
     *
     * @return the number of columns.
     */
    public int getN()
    {
        return _n;
    }

    /**
     * Gets the number of columns in this matrix.
     *
     * @return the number of columns.
     */
    public int getColumnCount()
    {
        return _n;
    }

    /**
     * Gets the array in which matrix elements are stored.
     *
     * @return the array; by reference, not by copy.
     */
    public float[][] getArray()
    {
        return _a;
    }

    /**
     * Determines whether this matrix is square.
     *
     * @return true, if square; false, otherwise.
     */
    public boolean isSquare()
    {
        return _m == _n;
    }

    /**
     * Determines whether this matrix is symmetric (and square).
     *
     * @return true, if symmetric (and square); false, otherwise.
     */
    public boolean isSymmetric()
    {
        if (!isSquare())
        {
            return false;
        }
        for (int i = 0; i < _n; ++i)
        {
            for (int j = i + 1; j < _n; ++j)
            {
                if (_a[i][j] != _a[j][i])
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets all elements of this matrix into a new array.
     *
     * @return the array.
     */
    public float[][] get()
    {
        return Array.copy(_a);
    }

    /**
     * Gets all elements of this matrix into the specified array.
     *
     * @param a the array.
     */
    public void get(float[][] a)
    {
        Array.copy(_a, a);
    }

    /**
     * Gets a matrix element.
     *
     * @param i the row index.
     * @param j the column index.
     * @return the element.
     */
    public float get(int i, int j)
    {
        return _a[i][j];
    }

    /**
     * Gets the specified submatrix a[i0:i1][j0:j1] of this matrix.
     *
     * @param i0 the index of first row.
     * @param i1 the index of last row.
     * @param j0 the index of first column.
     * @param j1 the index of last column.
     * @return 
     */
    public DMatrix get(int i0, int i1, int j0, int j1)
    {
        checkI(i0, i1);
        checkJ(j0, j1);
        int m = i1 - i0 + 1;
        int n = j1 - j0 + 1;
        DMatrix x = new DMatrix(m, n);
        Array.copy(n, m, j0, i0, _a, 0, 0, x._a);
        return x;
    }

    /**
     * Gets a new matrix from the specified rows and columns of this matrix.
     *
     * @param r the array of row indices; null, for all rows.
     * @param c the array of column indices; null, for all columns.
     * @return 
     */
    public DMatrix get(int[] r, int[] c)
    {
        if (r == null && c == null)
        {
            return new DMatrix(_m, _n, Array.copy(_a));
        }
        else
        {
            int m = (r != null) ? r.length : _m;
            int n = (c != null) ? c.length : _n;
            float[][] b = new float[m][n];
            if (r == null)
            {
                for (int i = 0; i < m; ++i)
                {
                    for (int j = 0; j < n; ++j)
                    {
                        b[i][j] = _a[i][c[j]];
                    }
                }
            }
            else if (c == null)
            {
                for (int i = 0; i < m; ++i)
                {
                    System.arraycopy(_a[r[i]], 0, b[i], 0, n);
                }
            }
            else
            {
                for (int i = 0; i < m; ++i)
                {
                    for (int j = 0; j < n; ++j)
                    {
                        b[i][j] = _a[r[i]][c[j]];
                    }
                }
            }
            return new DMatrix(m, n, b);
        }
    }

    /**
     * Gets a matrix from specified one row and columns of this matrix.
     *
     * @param i the row index.
     * @param c the array of column indices; null, for all columns.
     * @return 
     */
    public DMatrix get(int i, int[] c)
    {
        return get(i, i, c);
    }

    /**
     * Gets a matrix from specified rows and one column of this matrix.
     *
     * @param r the array of row indices; null, for all rows.
     * @param j the column index.
     * @return 
     */
    public DMatrix get(int[] r, int j)
    {
        return get(r, j, j);
    }

    /**
     * Gets a matrix from specified rows and columns of this matrix.
     *
     * @param i0 the index of the first row.
     * @param i1 the index of the last row.
     * @param c the array of column indices; null, for all columns.
     * @return 
     */
    public DMatrix get(int i0, int i1, int[] c)
    {
        checkI(i0, i1);
        if (c == null)
        {
            return get(i0, i1, 0, _n - 1);
        }
        else
        {
            int m = i1 - i0 + 1;
            int n = c.length;
            float[][] b = new float[m][n];
            for (int i = i0; i <= i1; ++i)
            {
                for (int j = 0; j < n; ++j)
                {
                    b[i - i0][j] = _a[i][c[j]];
                }
            }
            return new DMatrix(m, n, b);
        }
    }

    /**
     * Gets a matrix from specified rows and columns of this matrix.
     *
     * @param r the array of row indices; null, for all rows.
     * @param j0 the index of the first column.
     * @param j1 the index of the last column.
     * @return 
     */
    public DMatrix get(int[] r, int j0, int j1)
    {
        checkJ(j0, j1);
        if (r == null)
        {
            return get(0, _m - 1, j0, j1);
        }
        else
        {
            int m = r.length;
            int n = j1 - j0 + 1;
            float[][] b = new float[m][n];
            for (int i = 0; i < m; ++i)
            {
                for (int j = j0; j <= j1; ++j)
                {
                    b[i][j - j0] = _a[r[i]][j];
                }
            }
            return new DMatrix(m, n, b);
        }
    }

    /**
     * Gets the elements of this matrix packed by columns.
     *
     * @return the array of matrix elements packed by columns.
     */
    public float[] getPackedColumns()
    {
        float[] c = new float[_m * _n];
        for (int i = 0; i < _m; ++i)
        {
            for (int j = 0; j < _n; ++j)
            {
                c[i + j * _m] = _a[i][j];
            }
        }
        return c;
    }

    /**
     * Gets the elements of this matrix packed by rows.
     *
     * @return the array of matrix elements packed by rows.
     */
    public float[] getPackedRows()
    {
        float[] r = new float[_m * _n];
        for (int i = 0; i < _m; ++i)
        {
            System.arraycopy(_a[i], 0, r, i * _n, _n);
        }
        return r;
    }

    /**
     * Sets all elements of this matrix from the specified array. Copies each
     * array element into this matrix.
     *
     * @param a the array.
     */
    public void set(float[][] a)
    {
        for (int i = 0; i < _m; ++i)
        {
            System.arraycopy(a[i], 0, _a[i], 0, _n);
        }
    }

    /**
     * Sets a matrix element.
     *
     * @param i the row index.
     * @param j the column index.
     * @param v the element value.
     */
    public void set(int i, int j, float v)
    {
        _a[i][j] = v;
    }

    /**
     * Sets the specified submatrix a[i0:i1][j0:j1] of this matrix.
     *
     * @param i0 the index of first row.
     * @param i1 the index of last row.
     * @param j0 the index of first column.
     * @param j1 the index of last column.
     * @param x the matrix from which to copy elements.
     */
    public void set(int i0, int i1, int j0, int j1, DMatrix x)
    {
        checkI(i0, i1);
        checkJ(j0, j1);
        int m = i1 - i0 + 1;
        int n = j1 - j0 + 1;
        Check.argument(m == x._m, "i1-i0+1 equals number of rows in x");
        Check.argument(n == x._n, "j1-j0+1 equals number of columns in x");
        Array.copy(n, m, 0, 0, x._a, j0, i0, _a);
    }

    /**
     * Sets the specified rows and columns of this matrix.
     *
     * @param r the array of row indices; null, for all rows.
     * @param c the array of column indices; null, for all columns.
     * @param x the matrix from which to copy elements.
     */
    public void set(int[] r, int[] c, DMatrix x)
    {
        if (r == null)
        {
            Check.argument(_m == x._m, "number of rows equal in this and x");
        }
        else
        {
            Check.argument(r.length == x._m, "r.length equals number of rows in x");
        }
        if (c == null)
        {
            Check.argument(_n == x._n, "number of columns equal in this and x");
        }
        else
        {
            Check.argument(c.length == x._n, "c.length equals number of columns in x");
        }
        if (r == null && c == null)
        {
            Array.copy(x._a, _a);
        }
        else
        {
            int m = (r != null) ? r.length : _m;
            int n = (c != null) ? c.length : _n;
            float[][] b = x._a;
            if (r == null)
            {
                for (int i = 0; i < m; ++i)
                {
                    for (int j = 0; j < n; ++j)
                    {
                        _a[i][c[j]] = b[i][j];
                    }
                }
            }
            else if (c == null)
            {
                for (int i = 0; i < m; ++i)
                {
                    System.arraycopy(b[i], 0, _a[r[i]], 0, n);
                }
            }
            else
            {
                for (int i = 0; i < m; ++i)
                {
                    for (int j = 0; j < n; ++j)
                    {
                        _a[r[i]][c[j]] = b[i][j];
                    }
                }
            }
        }
    }

    /**
     * Sets the specified one row and columns of this matrix.
     *
     * @param i the row index.
     * @param c the array of column indices; null, for all columns.
     * @param x the matrix from which to copy elements.
     */
    public void set(int i, int[] c, DMatrix x)
    {
        set(i, i, c, x);
    }

    /**
     * Sets the specified rows and one column of this matrix.
     *
     * @param r the array of row indices; null, for all rows.
     * @param j the column index.
     * @param x the matrix from which to copy elements.
     */
    public void set(int[] r, int j, DMatrix x)
    {
        set(r, j, j, x);
    }

    /**
     * Sets the specified rows and columns of this matrix.
     *
     * @param i0 the index of the first row.
     * @param i1 the index of the last row.
     * @param c the array of column indices; null, for all columns.
     * @param x the matrix from which to copy elements.
     */
    public void set(int i0, int i1, int[] c, DMatrix x)
    {
        checkI(i0, i1);
        Check.argument(i1 - i0 + 1 == x._m, "i1-i0+1 equals number of rows in x");
        if (c == null)
        {
            set(i0, i1, 0, _n - 1, x);
        }
        else
        {
            int n = c.length;
            float[][] b = x._a;
            for (int i = i0; i <= i1; ++i)
            {
                for (int j = 0; j < n; ++j)
                {
                    _a[i][c[j]] = b[i - i0][j];
                }
            }
        }
    }

    /**
     * Sets the specified rows and columns of this matrix.
     *
     * @param r the array of row indices; null, for all rows.
     * @param j0 the index of the first column.
     * @param j1 the index of the last column.
     * @param x the matrix from which to copy elements.
     */
    public void set(int[] r, int j0, int j1, DMatrix x)
    {
        checkJ(j0, j1);
        Check.argument(j1 - j0 + 1 == x._n, "j1-j0+1 equals number of columns in x");
        if (r == null)
        {
            set(0, _m - 1, j0, j1, x);
        }
        else
        {
            int m = r.length;
            float[][] b = x._a;
            for (int i = 0; i < m; ++i)
            {
                for (int j = j0; j <= j1; ++j)
                {
                    _a[r[i]][j] = b[i][j - j0];
                }
            }
        }
    }

    /**
     * Sets the elements of this matrix from an array packed by columns.
     *
     * @param c the array of matrix elements packed by columns.
     */
    public void setPackedColumns(float[] c)
    {
        for (int i = 0; i < _m; ++i)
        {
            for (int j = 0; j < _n; ++j)
            {
                _a[i][j] = c[i + j * _m];
            }
        }
    }

    /**
     * Sets the elements of this matrix from an array packed by rows.
     *
     * @param r the array of matrix elements packed by rows.
     */
    public void setPackedRows(float[] r)
    {
        for (int i = 0; i < _m; ++i)
        {
            System.arraycopy(r, i * _n, _a[i], 0, _n);
        }
    }

    /**
     * Returns the transpose of this matrix.
     *
     * @return the transpose.
     */
    public DMatrix transpose()
    {
        DMatrix x = new DMatrix(_n, _m);
        float[][] b = x._a;
        for (int i = 0; i < _m; ++i)
        {
            for (int j = 0; j < _n; ++j)
            {
                b[j][i] = _a[i][j];
            }
        }
        return x;
    }

    /**
     * Returns the one-norm (maximum column sum) of this matrix.
     *
     * @return the one-norm.
     */
    public float norm1()
    {
        float f = 0.0f;
        for (int j = 0; j < _n; ++j)
        {
            float s = 0.0f;
            for (int i = 0; i < _m; ++i)
            {
                s += abs(_a[i][j]);
            }
            f = max(f, s);
        }
        return f;
    }

    /**
     * Returns the two-norm (maximum singular value) of this matrix.
     *
     * @return the two-norm.
     */
    public float norm2()
    {
        return 1.0f; // TODO: use the SVD.
    }

    /**
     * Returns the infinity-norm (maximum row sum) of this matrix.
     *
     * @return the infinity-norm.
     */
    public float normI()
    {
        float f = 0.0f;
        for (int i = 0; i < _m; ++i)
        {
            float s = 0.0f;
            for (int j = 0; j < _n; ++j)
            {
                s += abs(_a[i][j]);
            }
            f = max(f, s);
        }
        return f;
    }

    /**
     * Returns the Frobenius norm (sqrt of sum of squares) of this matrix.
     *
     * @return the Frobenius norm.
     */
    public float normF()
    {
        float f = 0.0f;
        for (int i = 0; i < _m; ++i)
        {
            for (int j = 0; j < _n; ++j)
            {
                f = (float) hypot(f, _a[i][j]);
            }
        }
        return f;
    }

    /**
     * Returns C = -A, where A is this matrix.
     *
     * @return C = -A.
     */
    public DMatrix negate()
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.neg(_a, c._a);
        return c;
    }

    /**
     * Returns C = A + B, where A is this matrix.
     *
     * @param b the matrix B.
     * @return C = A + B.
     */
    public DMatrix plus(DMatrix b)
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.add(_a, b._a, c._a);
        return c;
    }

    /**
     * Returns A = A + B, where A is this matrix.
     *
     * @param b the matrix B.
     * @return A = A + B.
     */
    public DMatrix plusEquals(DMatrix b)
    {
        Array.add(_a, b._a, _a);
        return this;
    }

    /**
     * Returns C = A - B, where A is this matrix.
     *
     * @param b the matrix B.
     * @return C = A - B.
     */
    public DMatrix minus(DMatrix b)
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.sub(_a, b._a, c._a);
        return c;
    }

    /**
     * Returns A = A - B, where A is this matrix.
     *
     * @param b the matrix B.
     * @return A = A - B.
     */
    public DMatrix minusEquals(DMatrix b)
    {
        Array.sub(_a, b._a, _a);
        return this;
    }

    /**
     * Returns C = A .* B, where A is this matrix. The symbol .* denotes
     * element-by-element multiplication.
     *
     * @param b the matrix B.
     * @return C = A .* B.
     */
    public DMatrix arrayTimes(DMatrix b)
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.mul(_a, b._a, c._a);
        return c;
    }

    /**
     * Returns A = A .* B, where A is this matrix. The symbol .* denotes
     * element-by-element multiplication.
     *
     * @param b the matrix B.
     * @return A = A .* B.
     */
    public DMatrix arrayTimesEquals(DMatrix b)
    {
        Array.mul(_a, b._a, _a);
        return this;
    }

    /**
     * Returns C = A ./ B, where A is this matrix. The symbol ./ denotes
     * element-by-element right division.
     *
     * @param b the matrix B.
     * @return C = A ./ B.
     */
    public DMatrix arrayRightDivide(DMatrix b)
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.div(_a, b._a, c._a);
        return c;
    }

    /**
     * Returns A = A ./ B, where A is this matrix. The symbol ./ denotes
     * element-by-element right division.
     *
     * @param b the matrix B.
     * @return A = A ./ B.
     */
    public DMatrix arrayRightDivideEquals(DMatrix b)
    {
        Array.div(_a, b._a, _a);
        return this;
    }

    /**
     * Returns C = A .\ B, where A is this matrix. The symbol .\ denotes
     * element-by-element left division.
     *
     * @param b the matrix B.
     * @return C = A .\ B.
     */
    public DMatrix arrayLeftDivide(DMatrix b)
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.div(b._a, _a, c._a);
        return c;
    }

    /**
     * Returns A = A .\ B, where A is this matrix. The symbol .\ denotes
     * element-by-element left division.
     *
     * @param b the matrix B.
     * @return A = A .\ B.
     */
    public DMatrix arrayLeftDivideEquals(DMatrix b)
    {
        Array.div(b._a, _a, _a);
        return this;
    }

    /**
     * Returns C = A * s, where A is this matrix, and s is a scalar.
     *
     * @param s the scalar s.
     * @return C = A * s.
     */
    public DMatrix times(float s)
    {
        DMatrix c = new DMatrix(_m, _n);
        Array.mul(_a, s, c._a);
        return c;
    }

    /**
     * Returns A = A * s, where A is this matrix, and s is a scalar.
     *
     * @param s the scalar s.
     * @return A = A * s.
     */
    public DMatrix timesEquals(float s)
    {
        Array.mul(_a, s, _a);
        return this;
    }

    /**
     * Returns C = A * B, where A is this matrix. The number of columns in this
     * matrix A must equal the number of rows in the specified matrix B.
     *
     * @param b the matrix B.
     * @return C = A * B.
     */
    public DMatrix times(DMatrix b)
    {
        Check.argument(_n == b._m,
                "number of columns in A equals number of rows in B");
        DMatrix c = new DMatrix(_m, b._n);
        float[][] aa = _a;
        float[][] ba = b._a;
        float[][] ca = c._a;
        float[] bj = new float[_n];
        for (int j = 0; j < b._n; ++j)
        {
            for (int k = 0; k < _n; ++k)
            {
                bj[k] = ba[k][j];
            }
            for (int i = 0; i < _m; ++i)
            {
                float[] ai = aa[i];
                float s = 0.0f;
                for (int k = 0; k < _n; ++k)
                {
                    s += ai[k] * bj[k];
                }
                ca[i][j] = s;
            }
        }
        return c;
    }

  /**
     * Returns the trace (sum of diagonal elements) of this matrix.
     *
     * @return the trace.
     */
    public float trace()
    {
        int mn = min(_m, _n);
        float t = 0.0f;
        for (int i = 0; i < mn; ++i)
        {
            t += _a[i][i];
        }
        return t;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass())
        {
            return false;
        }
        DMatrix that = (DMatrix) obj;
        if (this._m != that._m || this._n != that._n)
        {
            return false;
        }
        float[][] a = this._a;
        float[][] b = that._a;
        for (int i = 0; i < _m; ++i)
        {
            for (int j = 0; j < _n; ++j)
            {
                if (a[i][j] != b[i][j])
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int h = _m ^ _n;
        for (int i = 0; i < _m; ++i)
        {
            for (int j = 0; j < _n; ++j)
            {
                long bits = Double.doubleToLongBits(_a[i][j]);
                h ^= (int) (bits ^ (bits >>> 32));
            }
        }
        return h;
    }

    @Override
    public String toString()
    {
        String ls = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        String[][] s = format(_a);
        int max = maxlen(s);
        String format = "%" + max + "s";
        sb.append("[[");
        int ncol = 77 / (max + 2);
        if (ncol >= 5)
        {
            ncol = (ncol / 5) * 5;
        }
        for (int i = 0; i < _m; ++i)
        {
            int nrow = 1 + (_n - 1) / ncol;
            if (i > 0)
            {
                sb.append(" [");
            }
            for (int irow = 0, j = 0; irow < nrow; ++irow)
            {
                for (int icol = 0; icol < ncol && j < _n; ++icol, ++j)
                {
                    sb.append(String.format(format, s[i][j]));
                    if (j < _n - 1)
                    {
                        sb.append(", ");
                    }
                }
                if (j < _n)
                {
                    sb.append(ls);
                    sb.append("  ");
                }
                else
                {
                    if (i < _m - 1)
                    {
                        sb.append("],");
                        sb.append(ls);
                    }
                    else
                    {
                        sb.append("]]");
                        sb.append(ls);
                    }
                }
            }
        }
        return sb.toString();
    }

    private void checkI(int i)
    {
        if (i < 0 || i >= _m)
        {
            Check.argument(0 <= i && i < _m, "row index i=" + i + " is in bounds");
        }
    }
    private void checkJ(int j)
    {
        if (j < 0 || j >= _n)
        {
            Check.argument(0 <= j && j < _n, "column index j=" + j + " is in bounds");
        }
    }

    private void checkI(int i0, int i1)
    {
        checkI(i0);
        checkI(i1);
        Check.argument(i0 <= i1, "i0<=i1");
    }

    private void checkJ(int j0, int j1)
    {
        checkJ(j0);
        checkJ(j1);
        Check.argument(j0 <= j1, "j0<=j1");
    }
}
