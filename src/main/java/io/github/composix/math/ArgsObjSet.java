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

public class ArgsObjSet<E> extends AbstractList<E> implements ArgsSet<E> {

  final Ordinal type;
  Index indices;
  E[] array;

  ArgsObjSet(Ordinal type, Index indices, E[] array) {
    this.type = type;
    this.indices = indices;
    this.array = array;
  }

  @Override
  public Ordinal getType() {
    return ArgsOrdinal.O;
  }

  @Override
  public Index indices() {
    return indices;
  }

  @Override
  public long getLong(int index) {
    return ((Long) get(index)).longValue();
  }

  @Override
  public E get(int index) {
    return array[index];
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public Comparator<? super E> comparator() {
    return null;
  }

  @Override
  public E first() {
    return get(0);
  }

  @Override
  public E last() {
    return get(size() - 1);
  }

  @Override
  public ArgsSet<E> subSet(E fromElement, E toElement) {
    int fromIndex = Arrays.binarySearch(array, fromElement);
    int toIndex = Arrays.binarySearch(array, toElement);
    return subList(
      fromIndex < 0 ? -++fromIndex : fromIndex,
      toIndex < 0 ? -toIndex : toIndex
    );
  }

  @Override
  public ArgsSet<E> headSet(E toElement) {
    int toIndex = Arrays.binarySearch(array, toElement);
    return subList(0, toIndex < 0 ? -toIndex : toIndex);
  }

  @Override
  public ArgsSet<E> tailSet(E fromElement) {
    int fromIndex = Arrays.binarySearch(array, fromElement);
    return subList(fromIndex < 0 ? -++fromIndex : fromIndex, array.length - 1);
  }

  @Override
  public ArgsSet<E> subList(int fromIndex, int toIndex) {
    return new SubArgsSet<>(type, array, fromIndex, toIndex - fromIndex);
  }

  static class SubArgsSet<E> extends ArgsObjSet<E> {

    private final int offset, size;

    SubArgsSet(Ordinal type, E[] array, int offset, int size) {
      super(type, null, array);
      this.offset = offset;
      this.size = size;
    }

    @Override
    public E get(int index) {
      return array[index + offset];
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public ArgsSet<E> subList(int fromIndex, int toIndex) {
      return new SubArgsSet<>(type, array, fromIndex, toIndex - fromIndex);
    }
  }
}
