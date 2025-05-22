/**
 * class ArgsObjSet
 *
 * Sorted set of long values backed by an array, with support for subrange views.
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

import java.util.Arrays;

public class ArgsLongSet extends Range<Long> {

  long[] array;

  ArgsLongSet(long[] array) {
    super(null);
    this.array = array;
  }

  @Override
  public int count(Long element) {
    final int index = Arrays.binarySearch(array, element.longValue());
    return index < 0 ? 0 : 1;
  }

  @Override
  int ranks(final Order order, Index ranks) {
    final int size = ranks.size();
    int count = 0, rank = order.rank(0);
    ranks.setInt(rank, count);
    long current = getLong(rank);
    for (int i = 1; i < size; ++i) {
      rank = order.rank(i);
      if (current != (current = getLong(rank))) {
        ++count;
      }
      ranks.setInt(rank, count);
    }
    return ++count;
  }

  @Override
  Index initialize(int count, int amount, Index result, final Order order) {
    indices = Index.of(count, amount);
    final long[] array = new long[count];
    count = 0;
    int rank = order.rank(0);
    int current = result.getInt(rank);
    array[0] = getLong(rank);
    for (int i = 1; i < amount; ++i) {
      rank = order.rank(i);
      if (current != (current = result.getInt(rank))) {
        indices.setInt(count++, i);
        array[count] = getLong(rank);
      }
    }
    indices.setInt(count, amount);
    this.array = array;
    return result;
  }

  @Override
  Object asArray() {
    return array;
  }

  @Override
  public int getInt(int index) {
    throw new UnsupportedOperationException();
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

}
