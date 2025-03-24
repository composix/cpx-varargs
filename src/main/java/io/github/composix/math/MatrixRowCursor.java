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

class MatrixRowCursor implements Cursor {
    private final byte[] positions;
    private Object[] argv;
    private int length, omega, row, col, amount, size, offset, mask;

    MatrixRowCursor(byte[] positions) {
        this.positions = positions;
    }
    
    @Override
    public void position(int index, Args args) {
        final Matrix matrix = (Matrix) args;
        final int ordinal = matrix.ordinal;
        argv = matrix.argv();
        mask = matrix.mask();
        offset = matrix.hashCode();
        omega = Ordinal.OMEGA.intValue();
        size = ordinal / omega;
        amount = ordinal % omega;
        omega = Ordinal.OMEGA.intValue();
        row = index % omega;
        col = offset + index / omega;
        Object current, actual = argv[col & mask];
        int i = 0;
        byte pos = 0;
        while (actual != null) {
            positions[i++] = pos;
            while ((current = argv[(col + ++pos) & mask]) != null && current.getClass() == actual.getClass()) {
                ++length;
            }
            actual = current;
        }
        positions[i] = -1;
        length = --pos;
    }

    @Override
    public boolean advance(int delta) {
        if (col + length <= size && row < amount) {
            row += delta % omega;
            col += delta / omega;
            return true;
        }
        return false;
    }

    @Override
    public boolean recede(int delta) {
        row -= delta % omega;
        col -= delta / omega;
        if (row < 0 || col < 0) {
            row += delta % omega;
            col += delta / omega;
            return false;
        }
        return true;
    }

    @Override
    public Object get(Ordinal kind, int pos) {
        return ((Object[]) argv[col + positions[kind.intValue()] + pos & mask])[row];
    }
}
