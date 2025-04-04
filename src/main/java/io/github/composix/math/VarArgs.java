/**
 * class VarArgs
 *
 * VarArgs is the main helper class for the class Matrix to implement the Args
 * and Keys interfaces. It maintains tabular-like data in a varargs kind of way.
 *
 * Author: dr. ir. J. M. Valk
 * Date: April 2025
 */

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

public final class VarArgs implements Cloneable{
    public static final VarArgs VARARGS = new VarArgs(Short.SIZE);

    private static IndexOutOfBoundsException OUT_OF_BOUNDS = new IndexOutOfBoundsException("position out of bounds");

    public final Object[] argv;

    VarArgs(final int bits) {
        argv = new Object[1 << bits];
    }

    public final VarArgs clone() {
        final int mask = mask();
        int size = 0, offset = hashCode();
        boolean flag = true;
        while(argv[offset++] != null) {
            ++size;
            if (offset > mask) {
                if (flag) {
                    offset = 0;
                    flag = false;
                } else {
                    throw new IndexOutOfBoundsException("mask exceeded");
                }
            }
        }
        try {
            VarArgs result = (VarArgs) super.clone();
            export(hashCode(), size, result.hashCode());
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public final int mask() {
        int result = argv.length;
        return --result;
    }

    public final int offset(int offset, Ordinal col, byte pos) {
        return offset(offset, mask(), col.intValue(), pos);
    }

    public final int offset(int offset, Class<?> type, int size, byte pos) {
        final int mask = mask();
        return (offset(offset, mask, type, size) + pos) & mask;
    }

    public final int offset(int offset, int position) {
        return (offset + position) & mask();
    }

    final void export(int sourceHash, int size, int targetHash, VarArgs target) {
        export(argv, sourceHash, mask(), target.argv, targetHash, target.mask(), size);
    }

    final void export(int sourceHash, int size, int targetHash) {
        final int mask = mask();
        export(argv, sourceHash, mask, argv, targetHash, mask, size);
    }

    private int offset(int offset, int mask, int index, byte pos) {
        if (pos < 1) {
            throw OUT_OF_BOUNDS;
        }
        int length;
        do {
            final Class<?> type = argv[offset & mask].getClass();
            length = length(offset, mask, type);
            offset += length;
        } while(index-- > 0);
        if (pos > length) {
            throw OUT_OF_BOUNDS;
        }
        return (offset + --pos) & mask;
    }

    private int offset(int offset, int mask, Class<?> type, int size) {
        offset &= mask;
        if (argv[offset] == type) {
            return offset;
        }
        while (--size > 0 && argv[++offset & mask] != type);
        if (size > 0) {
            return offset & mask;
        }
        throw new IndexOutOfBoundsException();
    }

    private int length(int offset, final int mask, final Class<?> type) {
        int result = 0;
        while (argv[offset++ & mask].getClass() == type) {
            ++result;
        }
        return result;
    }

    private static final void export(Object source, int sourceHash, int sourceMask, Object target, int targetHash, int targetMask, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");  
        }
        if (size <= sourceMask && size <= targetMask) {
            final int sourcePos = sourceHash & sourceMask, targetPos = targetHash & targetMask;
            if (sourcePos < ((sourcePos + size) & sourceMask)) {
                if (targetPos < ((targetPos + size) & targetMask)) {
                    System.arraycopy(source, sourcePos, target, targetPos, size);
                } else {
                    System.arraycopy(source, sourcePos, target, targetPos, targetMask - targetPos + 1);
                    System.arraycopy(source, sourcePos + targetMask - targetPos + 1, target, 0, size - targetMask + targetPos - 1);
                }
            } else {
                if (targetPos < ((targetPos + size) & targetMask)) {
                    System.arraycopy(source, sourcePos, target, targetPos, sourceMask - sourcePos + 1);
                    System.arraycopy(source, 0, target, sourceMask - sourcePos + 1, size - sourceMask + sourcePos - 1);
                } else {
                    throw new UnsupportedOperationException();
                }
            }    
        } else {
            throw new IndexOutOfBoundsException("size exceeds mask");
        }
    }
}
