/**
 * class ArgsObjSet
 *
 * Sorted set of object values backed by an array, with support for subrange views.
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

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArgsObjSet<E extends Comparable<E>> extends Range<E> {

  Object[] array;

  ArgsObjSet(byte tpos, Object[] array) {
    super(tpos);
    this.array = array;
  }

  @Override
  public int count(E element) {
    final int index = Arrays.binarySearch(array, element);
    return index < 0 ? 0 : 1;
  }

  @Override
  public Index initialize(final MutableOrder order) {
    order.reorder((lhs, rhs) ->
      get(lhs.intValue()).compareTo(get(rhs.intValue()))
    );
    final E[] array = (E[]) this.array;
    final int amount = order.amount();
    int count = 1, rank = order.rank(0);
    E current = array[rank];
    for (int i = 1; i < amount; ++i) {
      if (!current.equals(current = array[order.rank(i)])) {
        ++count;
      }
    }
    indices = Index.of(count, amount);
    this.array = (E[]) Array.newInstance(
      array.getClass().getComponentType(),
      count
    );
    final Index result = Index.of(amount, --count);
    count = 0;
    result.setInt(rank, count);
    current = array[rank];
    this.array[0] = current;
    for (int i = 1; i < amount; ++i) {
      rank = order.rank(i);
      result.setInt(rank, count);
      if (current != (current = array[rank])) {
        indices.setInt(count++, i);
        this.array[count] = current;
      }
    }
    indices.setInt(count, amount);
    return result;
  }

  @Override
  public Object array() {
    return array;
  }

  @Override
  public Ordinal getType() {
    return OrdinalNumber.ORDINALS[tpos];
  }

  @Override
  public Index indices() {
    return indices;
  }

  @Override
  public int getInt(int index) {
    throw new ArithmeticException();
  }

  @Override
  public long getLong(int index) {
    throw new ArithmeticException();
  }

  @Override
  public long getIndexedLong(int index) {
    throw new ArithmeticException();
  }

  @Override
  public E get(int index) {
    return (E) array[index];
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  Object asArray() {
    return array;
  }
}
