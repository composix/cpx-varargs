/**
 * class Range
 *
 * Abstract class representing the range of values that can occur in a column.
 *
 * Author: dr. ir. J. M. Valk
 * Date: May 2025
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

abstract class Range<E extends Comparable<E>> extends OrdinalList<E> {
  static <E extends Comparable<E>> Range<E> of(Index cumulativeCounts, E[] array) {
    final ArgsObjSet<E> result = new ArgsObjSet<>(array);
    result.indices = cumulativeCounts;
    return result;
  }

  static Range<Long> ofLongs(Index cumulativeCounts, long[] array) {
    final ArgsLongSet result = new ArgsLongSet(array);
    result.indices = cumulativeCounts;
    return result;
  }

  Index indices;

  Range(Index indices) {
    this.indices = indices;
  }

  abstract Index initialize(int count, int amount, Index result, Order order);

  @Override
  public Index cumulativeCounts() {
    final int size = size();
    final Index result = Index.of(size);
    int i = 0;
    while (i < size) {
      result.setInt(i, ++i);
    }
    return result;
  }

  @Override
  public void ranks(Index result) {
    final int size = size();
    for (int i = 0; i < size; ++i) {
      result.setInt(i, i);
    }
  }

  @Override
  boolean isRange() {
    return indices != null;
  }
}
