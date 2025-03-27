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

    final void export(int sourceHash, int size, int targetHash, VarArgs target) {
        export(argv, sourceHash, mask(), target.argv, targetHash, target.mask(), size);
    }

    final void export(int sourceHash, int size, int targetHash) {
        final int mask = mask();
        export(argv, sourceHash, mask, argv, targetHash, mask, size);
    }

    private static void export(Object source, int sourceHash, int sourceMask, Object target, int targetHash, int targetMask, int size) {
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
