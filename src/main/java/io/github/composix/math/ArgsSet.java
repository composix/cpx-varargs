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
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.SortedSet;
import java.util.Spliterator;

interface ArgsSet<E> extends SortedSet<E>, List<E>, RandomAccess {
  Ordinal getType();

  Index indices();

  long getLong(int index);

  @Override
  ArgsSet<E> subSet(E fromElement, E toElement);

  @Override
  ArgsSet<E> headSet(E toElement);

  @Override
  ArgsSet<E> tailSet(E fromElement);

  @Override
  ArgsSet<E> subList(int fromIndex, int toIndex);

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
  default void addFirst(E e) {
    this.add(0, e);
  }

  @Override
  default void addLast(E e) {
    this.add(e);
  }

  @Override
  default ArgsSet<E> reversed() {
    throw new UnsupportedOperationException();
  }

  @Override
  default Spliterator<E> spliterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'spliterator'"
    );
  }
}
