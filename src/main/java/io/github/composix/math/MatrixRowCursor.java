/**
 * MIT License
 *
 * Copyright (c) 2025 ComPosiX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.composix.math;

class MatrixRowCursor extends OrdinalInt implements Cursor {
    Matrix matrix;
    private final Object[] values;
    private int length;

    MatrixRowCursor(int ordinal, Object[] values) {
        super(ordinal);
        matrix = null;
        this.values = values;
    }
    
    @Override
    public void position(Ordinal position, Args args) {
        this.matrix = (Matrix) args;
        ordinal = position.intValue();
    }

    @Override
    public void rows(int size) {
        if (size > 0) {
            if (size > 1) {
                throw new IndexOutOfBoundsException("size must not exceed 1");
            }
        } else {
            throw new IndexOutOfBoundsException("size must be greater than 0");
        }
    }

    @Override
    public void cols(int size) {
        final int length = values.length;
        if (size > 0) {
            if (size > length) {
                throw new IndexOutOfBoundsException("size must not exceed " + length);
            } else {
                this.length = size;
            }
        } else {
            throw new IndexOutOfBoundsException("size must be greater than 0");
        }
    }

    @Override
    public boolean advance(Ordinal delta) {
        final int omega = OMEGA.intValue(),
            col = ordinal / omega,
            row = ordinal % omega,
            dim = matrix.ordinal;
        if (col + length <= dim / omega && row < dim % omega) {
            retrieve(row, col);
            ordinal += delta.intValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean recede(Ordinal delta) {
        final int omega = OMEGA.intValue(),
            ord = ordinal - delta.intValue();
        if (ord < 0) {
            return false;
        }
        final int col = ord / omega,
            row = ord % omega,
            dim = matrix.ordinal;
        if (row < dim % omega) {
            ordinal = ord;
            retrieve(row, col);
            return true;
        }
        return false;
    }

    private void retrieve(final int row, final int col) {
        final int mask = matrix.mask();
        final Object[] argv = matrix.argv();
        for (int i = 0; i < length; ++i) {
            values[i] = ((Object[]) argv[(col + i) & mask])[row];
        } 
    }
}
