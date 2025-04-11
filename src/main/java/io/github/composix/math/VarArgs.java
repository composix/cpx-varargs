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

import java.util.AbstractList;

public final class VarArgs extends AbstractList<ArgsIndexList<?>> implements Cloneable {

  public static final VarArgs VARARGS = new VarArgs(Short.SIZE);

  private static final IndexOutOfBoundsException OUT_OF_BOUNDS = new IndexOutOfBoundsException();

  public final Object[] argv;
  private final ArgsIndexList[] columns;

  VarArgs(final int bits) {
    argv = new Object[1 << bits];
    columns = new ArgsIndexList[1 << bits];
  }

  @Override
  public int size() {
    return argv.length;
  }

  @Override
  public ArgsIndexList<?> get(int index) {
    return columns[index];
  }

  @Override
  public ArgsIndexList<?> set(int index, ArgsIndexList<?> value) {
    ArgsIndexList<?> result = columns[index];
    columns[index] = value;
    return result;
  }

  public VarArgs clone() {
    final int mask = mask();
    int size = 0, offset = hashCode();
    boolean flag = true;
    while (argv[offset++] != null) {
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

  public int mask() {
    int result = argv.length;
    return --result;
  }

  public byte position(final int offset, final int mask, int index, byte pos) {
    if (pos < 0) {
      throw OUT_OF_BOUNDS;
    }
    int i = offset;
    Object value = argv[i];
    if (value == null) {
      throw OUT_OF_BOUNDS;
    }
    Class<?> type = value.getClass();
    if (!type.isArray()) {
      throw OUT_OF_BOUNDS;
    }
    while (index-- > 0) {
      final Class<?> expected = type;
      while((value = argv[++i & mask]) != null && expected == (type = value.getClass()));
      if (value == null || !type.isArray()) {
        throw OUT_OF_BOUNDS;
      }
    }
    while(pos-- > 0) {
      if ((value = argv[++i & mask]) == null || value.getClass() != type) {
        throw OUT_OF_BOUNDS;
      }
    }
    return (byte) (i - offset);
  }
  
  public byte position(final int offset, final int mask, Class<?> type, byte pos) {
    Object value;
    int i = offset;
    while ((value = argv[i++]) != null) {
      if (value.getClass() == type) {
        while(pos-- > 0) {
          if ((value = argv[i++ & mask]) == null || value.getClass() != type) {
            throw OUT_OF_BOUNDS;
          }
        }
        return (byte) (i - offset);    
      }
    }
    throw OUT_OF_BOUNDS;
  }

  public byte position(
    final int offset,
    final int mask,
    final CharSequence header
  ) {
    int i = offset;
    CharSequence[] value = (CharSequence[]) argv[i++];
    while(!value[0].equals(header)) {
      value = (CharSequence[]) argv[i++ & mask];
    }
    return (byte) (--i - offset);
  }

  public final byte length(final int offset, int mask) {
    int i = offset;
    Object value = argv[i++];
    Class<?> type = value.getClass();
    if (type.isArray()) {
      while((value = argv[i++ & mask]) != null && value.getClass() == type);
      return (byte) (i - offset);
    }
    throw OUT_OF_BOUNDS;
  }

  public boolean declare(int offset, final Object value) {
    if (argv[offset] != null) {
      return false;
    }
    argv[offset] = value;
    return true;
  }

  final void export(int sourceHash, int size, int targetHash, VarArgs target) {
    export(
      argv,
      sourceHash,
      mask(),
      target.argv,
      targetHash,
      target.mask(),
      size
    );
  }

  final void export(int sourceHash, int size, int targetHash) {
    final int mask = mask();
    export(argv, sourceHash, mask, argv, targetHash, mask, size);
  }

  private static final void export(
    Object source,
    int sourceHash,
    int sourceMask,
    Object target,
    int targetHash,
    int targetMask,
    int size
  ) {
    if (size <= 0) {
      throw new IllegalArgumentException("size must be greater than 0");
    }
    if (size <= sourceMask && size <= targetMask) {
      final int sourcePos = sourceHash & sourceMask, targetPos =
        targetHash & targetMask;
      if (sourcePos < ((sourcePos + size) & sourceMask)) {
        if (targetPos < ((targetPos + size) & targetMask)) {
          System.arraycopy(source, sourcePos, target, targetPos, size);
        } else {
          System.arraycopy(
            source,
            sourcePos,
            target,
            targetPos,
            targetMask - targetPos + 1
          );
          System.arraycopy(
            source,
            sourcePos + targetMask - targetPos + 1,
            target,
            0,
            size - targetMask + targetPos - 1
          );
        }
      } else {
        if (targetPos < ((targetPos + size) & targetMask)) {
          System.arraycopy(
            source,
            sourcePos,
            target,
            targetPos,
            sourceMask - sourcePos + 1
          );
          System.arraycopy(
            source,
            0,
            target,
            sourceMask - sourcePos + 1,
            size - sourceMask + sourcePos - 1
          );
        } else {
          throw new UnsupportedOperationException();
        }
      }
    } else {
      throw new IndexOutOfBoundsException("size exceeds mask");
    }
  }
}
