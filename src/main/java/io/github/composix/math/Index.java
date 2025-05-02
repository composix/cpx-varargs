/**
 * interface Index
 *
 * Provides a practical way to represent mappings used to rearrange or filter
 * elements in a list, transforming it into another list that may be shorter or longer.
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

import java.util.List;
import java.util.stream.IntStream;

/**
 * Interface for maintaining and manipulating an index. An index is useful for
 * rearranging and/or selecting a subset of elements in a list. Effectively, it
 * maps elements in a list to new positions, potentially changing the list's length.
 *
 * The terms "index" and "target index" refer to:
 * - "Index": the position of an element in the original list.
 * - "Target index": the position where the element will be rearranged in the list.
 *
 * @author dr. ir. J. M. Valk
 * @since April 2025
 */
interface Index<T extends Comparable<T>> extends List<T> {
  /**
   * Creates an index of the specified length. The index is intended to represent
   * a mapping that maintains the length of the original list, with the maximum index
   * being {@code length - 1}. For mappings that also change the length of the list,
   * use {@link #of(int, int)}.
   *
   * @param length - the length of the index
   * @return an index of the specified length
   */
  static Index<Ordinal> of(int length) {
    final Index<Ordinal> result = OrdinalList.of(length, --length);
    for (int i = 0; i <= length; ++i) {
      result.setInt(i, i);
    }
    return result;
  }

  /**
   * Creates an index of the specified length and lastIndex. The index is intended to
   * map into a list of length lastIndex + 1; that is, lastIndex is the maximum index
   * that may occur; as 0 is the smallest index, the length of the target list is thus
   * lastIndex + 1.
   *
   * @param length - the length of the index
   * @param lastIndex - the maximum index that may occur
   * @return an index of the specified length and target range (0 to lastIndex)
   */
  static Index<Ordinal> of(int length, int lastIndex) {
    return OrdinalList.of(length, lastIndex);
  }

  /**
   * Returns the target index corresponding to the given index.
   * The target index indicates where the element at the given index should be placed.
   *
   * @param index - the source index
   * @return the target index
   */
  int getInt(int index);

  /**
   * Returns the target index corresponding to the given index as long.
   * The target index indicates where the element at the given index should be placed.
   *
   * @param index - the source index
   * @return the target index as long
   */
  long getLong(int index);

  /**
   * Sets the target index for the given index. The target index is the index to
   * where the element at index is rearranged.
   *
   * @param index - the index to set the target index for
   * @param ord   - the target index
   */
  void setInt(int index, int ord);

  /**
   * Returns an {@code IntStream} of the target indices of this index. The target indices
   * represent the new positions of the elements in the list after rearrangement.
   *
   * @return an {@code IntStream} of target indices
   */
  default IntStream intStream() {
    return IntStream.range(0, size()).map(this::getInt);
  }
}
