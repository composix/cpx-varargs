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
import java.util.List;

interface Index extends List<Ordinal> {
  static Index of(int length) {
    final Index result = of(length, --length);
    for (int i = 0; i <= length; ++i) {
      result.setInt(i, i);
    }
    return result;
  }

  static Index of(int length, int amount) {
    if (amount <= Byte.MAX_VALUE) {
      return new ByteIndex(length);
    }
    return new ShortIndex(length);
  }

  int getInt(int index);

  void setInt(int index, int ord);

  static final class ByteIndex extends AbstractList<Ordinal> implements Index {

    private final byte[] index;

    ByteIndex(final int length) {
      index = new byte[length];
    }

    @Override
    public int size() {
      return index.length;
    }

    @Override
    public Ordinal get(int i) {
      return Ordinal.of(index[i]);
    }

    @Override
    public int getInt(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = (byte) j;
    }
  }

  static final class ShortIndex extends AbstractList<Ordinal> implements Index {

    private final short[] index;

    ShortIndex(final int length) {
      index = new short[length];
    }

    @Override
    public int size() {
      return index.length;
    }

    @Override
    public Ordinal get(int i) {
      return Ordinal.of(index[i]);
    }

    @Override
    public int getInt(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = (short) j;
    }
  }
}
