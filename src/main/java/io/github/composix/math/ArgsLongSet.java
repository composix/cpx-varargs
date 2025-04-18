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
import java.util.Arrays;
import java.util.Comparator;

public class ArgsLongSet extends AbstractList<Long> implements ArgsSet<Long> {

  Index indices;
  long[] array;

  ArgsLongSet(Index indices, long[] array) {
    this.indices = indices;
    this.array = array;
  }

  @Override
  public Ordinal getType() {
    return ArgsOrdinal.L;
  }

  @Override
  public Index indices() {
    return indices;
  }

  @Override
  public long getLong(int index) {
    return array[index];
  }

  @Override
  public Long get(int index) {
    return array[index];
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public Comparator<Long> comparator() {
    return null;
  }

  @Override
  public Long first() {
    return get(0);
  }

  @Override
  public Long last() {
    return get(size() - 1);
  }

  @Override
  public ArgsSet<Long> subSet(Long fromElement, Long toElement) {
    int fromIndex = Arrays.binarySearch(array, fromElement);
    int toIndex = Arrays.binarySearch(array, toElement);
    return subList(
      fromIndex < 0 ? -++fromIndex : fromIndex,
      toIndex < 0 ? -toIndex : toIndex
    );
  }

  @Override
  public ArgsSet<Long> headSet(Long toElement) {
    int toIndex = Arrays.binarySearch(array, toElement);
    return subList(0, toIndex < 0 ? -toIndex : toIndex);
  }

  @Override
  public ArgsSet<Long> tailSet(Long fromElement) {
    int fromIndex = Arrays.binarySearch(array, fromElement);
    return subList(fromIndex < 0 ? -++fromIndex : fromIndex, array.length - 1);
  }

  @Override
  public ArgsSet<Long> subList(int fromIndex, int toIndex) {
    return new SubArgsSet(array, fromIndex, toIndex - fromIndex);
  }

  static class SubArgsSet extends ArgsLongSet {

    private final int offset, size;

    SubArgsSet(long[] array, int offset, int size) {
      super(null, array);
      this.offset = offset;
      this.size = size;
    }

    @Override
    public Long get(int index) {
      return Long.valueOf(array[index + offset]);
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public ArgsSet<Long> subList(int fromIndex, int toIndex) {
      return new SubArgsSet(array, fromIndex, toIndex - fromIndex);
    }
  }
}
