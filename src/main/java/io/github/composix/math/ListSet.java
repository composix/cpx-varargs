/**
 * class ListSet
 *
 * The ListSet interface provides the functionality of a SortedSet combined with
 * random-access capabilities of a List. It enables efficient retrieval of elements
 * based on their sorted order and index.
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

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;

public interface ListSet<E> extends SortedSet<E>, List<E>, RandomAccess {
  /**
   * In case this ListSet is backed by a primitive long array, this method
   * allows direct access to the long values.
   *
   * @param index - index for which to retrieve the long value
   * @return long value at rank index
   * @throws NoSuchElementException - if this list is not of the primitive
   * long type
   */
  long getLong(int index);

  @Override
  ListSet<E> subSet(E fromElement, E toElement);

  @Override
  ListSet<E> headSet(E toElement);

  @Override
  ListSet<E> tailSet(E fromElement);

  @Override
  ListSet<E> subList(int fromIndex, int toIndex);

  @Override
  default E getFirst() {
    if (this.isEmpty()) {
      throw new NoSuchElementException();
    } else {
      return this.get(0);
    }
  }

  @Override
  default E getLast() {
    if (this.isEmpty()) {
      throw new NoSuchElementException();
    } else {
      return this.get(this.size() - 1);
    }
  }

  @Override
  default E removeFirst() {
    if (this.isEmpty()) {
      throw new NoSuchElementException();
    } else {
      return this.remove(0);
    }
  }

  @Override
  default E removeLast() {
    if (this.isEmpty()) {
      throw new NoSuchElementException();
    } else {
      return this.remove(this.size() - 1);
    }
  }

  @Override
  default boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException(
      "Cannot add element without preserving sort order in ListSet."
    );
  }

  @Override
  default boolean add(E e) {
    throw new UnsupportedOperationException(
      "Cannot add element without preserving sort order in ListSet."
    );
  }

  @Override
  default void add(int index, E element) {
    throw new UnsupportedOperationException(
      "Cannot insert at arbitrary index in a sorted ListSet."
    );
  }

  @Override
  default E set(int index, E element) {
    throw new UnsupportedOperationException(
      "Cannot replace elements in a sorted ListSet."
    );
  }

  @Override
  default void replaceAll(java.util.function.UnaryOperator<E> operator) {
    throw new UnsupportedOperationException(
      "Cannot apply replaceAll on a sorted ListSet."
    );
  }

  @Override
  default void addFirst(E e) {
    throw new UnsupportedOperationException(
      "Cannot insert at arbitrary index in a sorted ListSet."
    );
  }

  @Override
  default void addLast(E e) {
    throw new UnsupportedOperationException(
      "Cannot add element without preserving sort order in ListSet."
    );
  }

  @Override
  default ListSet<E> reversed() {
    throw new UnsupportedOperationException();
  }

  @Override
  default Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, Spliterator.ORDERED);
  }
}
