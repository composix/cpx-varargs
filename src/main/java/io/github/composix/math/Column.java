/**
 * Interface Column<E>
 * 
 * Defines the structure for a single column in a tabular data structure,
 * enabling operations such as data retrieval and manipulation.
 * 
 * Author: J. M. Valk
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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.stream.LongStream;

/**
 * The {@code Column} interface extends Java's {@link List} to represent a column view 
 * on tabular data, backed by the {@link Args} interface. It reflects the values of a 
 * single column and maintains consistency with the row ordering in the underlying table.
 * 
 * <p>Sorting this list via {@link List#sort} affects the row order in the backing {@code Args}, 
 * and likewise, calling {@link Args#orderBy} reorders this list view accordingly.
 *
 * <p><h3>Additional features</h3>
 * <ul>
 *   <li><strong>Column header:</strong> Provided via {@link #toString()}
 *   <li><strong>Distinct values:</strong> Viewable as a {@link ListSet} of naturally-ordered
 *        unique elements via {@link #asListSet()}
 *   <li><strong>Ordinal typing:</strong> Supports primitive types using the {@code Ordinal}
 *        system (e.g., {@code L} for {@code long}, {@code S} for {@code String}).
 *   <li><strong>Factory methods:</strong> ArgsList instances can be created easily via static
 *        factory calls such as {@code AL.any(0L, 1L)} or {@code S.all("one", "two")}.
 * </ul>
 * 
 * <p>ArgsList supports random access and may expose primitive views (e.g., {@link #getLong(int)}, {@link #longStream()}) 
 * if the underlying type supports them.
 * 
 * @param <E> the type of elements in this list
 * 
 * @author: dr. ir. J. M. Valk
 * @since: April 2025
 * @see Args
 */
public interface Column<E> extends CharSequence, RangedList<E> {
  /**
   * Returns the set view consisting of the distinct elements of this column. As a
   * side-effect this compacts the column and no longer directly maintains its
   * elements in an array, but instead it references elements in the set view
   * using an {@code Index}. As a result, the {@code source} method will throw an
   * UnsupportedOperationException when called after asListSet().
   *
   * @return ListSet - set of distinct elements in this column
   */
  RangedList<E> asListSet();

  /**
   * Get the ordinal type of the elements in this column. See the methods Ordinal::any
   * and Ordinal::all for details on how the ordinal type system works.
   *
   * @return Ordinal - the ordinal type
   */
  Ordinal getType();

  /**
   * Get the source array from which this list was originally created, if still available.
   * The returned array reflects the order at creation time, and is not guaranteed
   * to reflect subsequent sorting or mutation.
   *
   * @return original source array, or throws if unavailable
   * @throws UnsupportedOperationException if source array is not accessible
   */
  Object source();

  /**
   * Attaches this column to where it originates from. This method is useful in combination
   * with methods such as {@code combine} of the Args interface that construct a new column
   * from an existing tabular datastructure and you want the new column to be part of the table
   * it originates from.
   * 
   * @return the tabular datastructure from where the column originates
   */
  Args attach();
  
  /**
   * Attaches a mutable order to this column. This will have an immediate effect on the
   * sort order of the elements in this column. Also changes in the order will be 
   * visible on the sort order. Vice versa, changing the order using the sort method
   * will be updated into the mutable order.
   * 
   * @param order - mutable order to attach to
   */
  void attachOrder(MutableOrder order);

  /**
   * Always returns false, because tabular data of empty columns is not allowed.
   *
   * @return always false
   */
  @Override
  boolean isEmpty();

  default int binarySearch(Object item) {
    return Collections.binarySearch(
      (List<? extends Comparable<? super Comparable<?>>>) this,
      (Comparable<?>) item
    );
  }
}
