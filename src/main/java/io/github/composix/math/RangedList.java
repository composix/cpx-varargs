/**
 * interface RangedList
 *
 * RangedList is a specialized List that allows efficient ranking and counting of
 * elements relative to a fixed, sorted range of allowed values. It ensures all
 * list elements must be present in the range, and it extends Index for efficient
 * primitive access. Key features include methods for counting occurrences (count()),
 * computing cumulative counts (cumulativeCounts()), and determining ranks (ranks())
 * of list elements based on the fixed range. This makes RangedList suitable for
 * applications like histogram-style analysis and rank computations.
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
import java.util.RandomAccess;

/**
 * A {@code RangedList} is a {@link java.util.List} that supports efficient ranking and
 * counting of its elements relative to a fixed, sorted range of allowed values.
 *
 * <p>The range defines the set of valid, comparable elements and may include values
 * that do not appear in the list. The range must be sorted in ascending order (either
 * naturally or via a comparator) and contain no duplicates.
 *
 * <p>This interface enables histogram-style analysis and rank computations by using
 * the range as a canonical reference frame. All elements in the list must be contained
 * in the range. Implementations are expected to enforce this invariant.
 *
 * <p>{@code RangedList} also extends the {@link Index} interface, allowing direct access
 * to the list's contents as primitive values using methods like {@code intValue(int)} and
 * {@code longValue(int)}. This is especially useful for numerical data (e.g., {@code byte},
 * {@code short}, {@code int}, {@code long}) where boxing and unboxing would be inefficient.
 *
 * <p>For example, given the list: {@code [2, 3, 1, 3, 1, 3]} and a fixed range:
 * {@code [0, 1, 2, 3, 4]}:
 *
 * <ul>
 *   <li>{@code ranks()} returns {@code [2, 3, 1, 3, 1, 3]} — the same as the list itself
 *       since each element's rank matches its value in a zero-based, sorted range.</li>
 *   <li>{@code cumulativeCounts()} returns {@code [0, 2, 1, 3, 0]}</li>
 *   <li>{@code count(4)} returns {@code 0}</li>
 * </ul>
 *
 * @param <E> the type of elements in this list and its range
 *
 * @author: dr. ir. J. M. Valk
 * @since: May 2025
 */
public interface RangedList<E> extends Index, List<E>, RandomAccess {
  /**
   * Returns the fixed, sorted range of allowed values that defines the reference domain
   * for ranking and counting operations. All elements in this list must be contained in
   * the range.
   *
   * @return the fixed, ordered range backing this list
   */
  RangedList<E> range();

  /**
   * Returns the number of occurrences of the specified element in this list.
   *
   * <p>This method counts only occurrences in the list and does not depend on whether
   * the element exists in the range.
   *
   * @param element the element whose frequency is to be determined
   * @return the number of occurrences of the specified element
   */
  int count(E element);

  /**
   * Returns an {@code Index} of counts for each element in the range,
   * representing their frequency in the list.
   *
   * <p>The result is aligned with the range: the i-th position in the result
   * corresponds to the i-th element in the range.
   *
   * <p>For example, for the list {@code [2, 3, 1, 3, 1, 3]} and range {@code [0, 1, 2, 3, 4]},
   * the method returns {@code [0, 2, 1, 3, 0]}.
   *
   * @return an {@code Index} of counts aligned with the range
   */
  Index cumulativeCounts();

  /**
   * Populates the given {@code result} index with the number of occurrences of each
   * element in the range.
   *
   * <p>The result is aligned with the range: the i-th position in {@code result}
   * corresponds to the i-th element in the range.
   *
   * <p>For example, for the list {@code [2, 3, 1, 3, 1, 3]} and range {@code [0, 1, 2, 3, 4]},
   * this method fills {@code result} with {@code [0, 2, 1, 3, 0]}.
   *
   * @param result the index to populate with counts
   */
  void cumulativeCounts(Index result);

  /**
   * Returns the rank (i.e., index in the range) of each element in this list.
   *
   * <p>The result is an {@code Index} of the same length as the list.
   * Each value represents the index (rank) of the corresponding list element in the range.
   *
   * <p>For example, for the list {@code [2, 3, 1, 3, 1, 3]} and range {@code [0, 1, 2, 3, 4]},
   * the method returns {@code [2, 3, 1, 3, 1, 3]}, which is identical to the list itself
   * because the values are drawn directly from their index positions in the range.
   *
   * <p>If the range is {@code [1, 2, 3]}, the result is {@code [1, 2, 0, 2, 0, 2]}.
   *
   * @return an {@code Index} representing the rank of each element in the range
   */
  Index ranks();

  /**
   * Populates the given {@code result} index with the rank (i.e., index in the range)
   * of each element in this list.
   *
   * <p>The result is aligned with the list: the i-th value in {@code result}
   * corresponds to the rank of the i-th element in this list.
   *
   * @param result the index to populate with rank values
   */
  void ranks(Index result);
}
