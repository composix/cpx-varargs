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

import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Interface for maintaining and manipulating an index. An index is useful for
 * rearranging and/or selecting a subset of elements in a list. Effectively, it
 * maps elements in a list to new positions, potentially changing the list's length.
 *
 * Technically, an index behaves like a list of ordinals, but it is backed by a
 * primitive array of bytes, shorts, ints, or longs. This allows for efficient
 * storage and manipulation of the index.
 *
 * @author dr. ir. J. M. Valk
 * @since April 2025
 */
interface Index {
  /**
   * Creates an index of the specified length. The index is intended to represent
   * a mapping that maintains the length of the original list, with the maximum index
   * being {@code length - 1}. For mappings that also change the length of the list,
   * use {@link #of(int, int)}.
   *
   * @param length - the length of the index
   * @return an index of the specified length
   */
  static Index of(int length) {
    final Index result = of(length, --length);
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
  static Index of(int length, int lastIndex) {
    if (lastIndex <= Short.MAX_VALUE) {
      return OrdinalList.of(length, (short) lastIndex);
    }
    return OrdinalList.ofLong(length, lastIndex);
  }

  int size();

  /**
   * Returns the value at the specified index as a primitive {@code int},
   * if the element can be safely and efficiently converted to an {@code int}
   * without narrowing or overflow.
   * <p>
   * This method is designed for performance, and will only succeed if
   * the underlying value already fits within the {@code int} range.
   * If the value is wider than {@code int} (e.g., a {@code long} or {@code BigInteger}),
   * an {@link ArithmeticException} will be thrown.
   *
   * @param index the index of the element to retrieve
   * @return the element at the given index, as an {@code int}
   * @throws ArithmeticException if the value cannot be represented as an {@code int}
   * @throws IndexOutOfBoundsException if the index is out of range
   */
  int getInt(int index);

  /**
   * Returns the value at the specified index as a primitive {@code long},
   * if the element can be safely and efficiently converted to a {@code long}
   * without narrowing or overflow.
   * <p>
   * This method is intended for high-performance access to numeric values.
   * It will only succeed if the underlying element is already stored as,
   * or can be directly represented as, a {@code long}. If the value is of
   * a wider type (e.g., {@code BigInteger}) or not a numeric type at all,
   * an {@link ArithmeticException} will be thrown.
   *
   * @param index the index of the element to retrieve
   * @return the element at the given index, as a {@code long}
   * @throws ArithmeticException if the value cannot be represented as a {@code long}
   * @throws IndexOutOfBoundsException if the index is out of range
   */
  long getLong(int index);

  /**
   * Returns the value at the specified index as a {@code long}, which encodes
   * both the index and the value. The value is stored as a {@code long} where
   * the index is packed alongside the actual value.
   * <p>
   * This method is designed for performance when dealing with data structures
   * that need both the value and its index. The index is encoded within the
   * {@code long} using OMEGA, allowing both the element's value and its position
   * to be represented in a single return value.
   * <p>
   * If the operation results in an invalid encoding (e.g., due to overflow
   * or misrepresentation of the data), an {@link ArithmeticException} will be thrown.
   *
   * @param index the index of the element to retrieve
   * @return the element at the given index, encoded as a {@code long},
   *         where the index and the value are combined
   * @throws ArithmeticException if the index and value cannot be
   *         correctly encoded into a {@code long}
   * @throws IndexOutOfBoundsException if the index is out of range
   */
  long getIndexedLong(int index);

  /**
   * Replaces the element at the specified position in this list with the
   * specified element (optional operation).
   *
   * @param index index of the element to replace
   * @param element element to be stored at the specified position
   * @throws ArithmeticException if the element cannot be represented as an {@code int}
   * @throws IndexOutOfBoundsException if the index is out of range
   * @throws UnsupportedOperationException if the {@code set} operation
   *         is not supported by this list
   */
  void setInt(int index, int element);

  /**
   * Returns an {@code IntStream} of the target indices of this index. The target indices
   * represent the new positions of the elements in the list after rearrangement.
   *
   * @return an {@code IntStream} of target indices
   */
  default IntStream intStream() {
    return IntStream.range(0, size()).map(this::getInt);
  }

  /**
   * Get a primitive LongStream of the elements in this column provided that the
   * ordinal type matches primitive long.
   *
   * @return LongStream - stream of elements in this column
   * @throws UnsupportedOperationException - if type of this list does not match
   * primitive long.
   */
  LongStream longStream();

  /**
   * Get a primitive LongStream of the elements in this column as a pairing of indices
   * with the corresponding integer values. This methods works both for column whose ordinal
   * type matches both int and long primitives. However, only for int primitives the pairing
   * is guaranteed to work. If a column is paired in this way and if overflow occurs,
   * unexpected results may happen.
   *
   * @return a stream of index/value combinations: item / omega is the value and item % omega
   * the index.
   * @throws UnsupportedOperationException - if type of this. list does not match
   * primitive int or primitive long.
   */
  LongStream indexedStream();

  void reorder(MutableOrder order);
}
