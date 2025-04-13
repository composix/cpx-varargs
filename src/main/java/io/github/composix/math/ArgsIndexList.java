/**
 * class ArgsIndexList
 *
 * This class provides the base list implementation of the ArgsList interface.
 * The implemented list is immutable in terms of its contents, but the order of
 * the elements can be changed by modifying the sort order based on the ordinals
 * associated with the tabular data.
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

import java.util.AbstractList;
import java.util.Comparator;
import java.util.stream.LongStream;

class ArgsIndexList<E> extends AbstractList<E> implements ArgsList<E> {

  final Matrix matrix;
  final byte pos;
  final Index refs;
  final ArgsSet<?> elements;

  ArgsIndexList(Matrix matrix, byte pos) {
    this.matrix = matrix;
    this.pos = pos;
    switch (source()) {
      case String[] strings:
        refs = Index.of(strings.length);
        elements = new ArgsObjSet<>(ArgsOrdinal.S, null, strings);
        break;
      case Object[] objects:
        refs = Index.of(objects.length);
        elements = new ArgsObjSet<>(null, null, objects);
        break;
      case long[] longs:
        refs = Index.of(longs.length);
        elements = new ArgsLongSet(null, longs);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  void initialize() {
    final int size = refs.size();
    short i = 0, j = 0;
    while (i < size) {
      final int limit = elements.indices().getInt(i);
      while (i < limit) {
        refs.setInt(i++, j);
      }
      ++j;
    }
  }

  Object source() {
    final VarArgs varargs = matrix.varArgs();
    final int mask = varargs.mask(), offset = (matrix.offset() + pos) & mask;
    return varargs.argv[offset & mask];
  }

  @Override
  public long getLong(int index) {
    return elements.getLong(matrix.rank(refs.getInt(index)));
  }

  @Override
  public LongStream longStream() {
    return refs.intStream().map(matrix::rank).mapToLong(elements::getLong);
  }
  
  @Override
  public int size() {
    return matrix.amount();
  }

  @Override
  public E get(int index) {
    return (E) elements.get(matrix.rank(refs.getInt(index)));
  }

  @Override
  public ArgsSet<E> asArgsSet() {
    return (ArgsSet<E>) elements;
  }

  @Override
  public void sort(Comparator<? super E> comparator) {
    if (elements.indices() == null) {
      switch (elements) {
        case ArgsLongSet longSet:
          if (comparator != null) {
            throw new UnsupportedOperationException();
          }
          matrix.reorder((lhs, rhs) ->
            Long.compare(
              longSet.array[lhs.intValue()],
              longSet.array[rhs.intValue()]
            )
          );
          break;
        case ArgsObjSet<?> objSet:
          if (comparator == null) {
            matrix.reorder((lhs, rhs) ->
              ((String) objSet.array[lhs.intValue()]).compareTo(
                  (String) objSet.array[rhs.intValue()]
                )
            );
          } else {
            matrix.reorder((lhs, rhs) ->
              comparator.compare(
                (E) objSet.array[lhs.intValue()],
                (E) objSet.array[rhs.intValue()]
              )
            );
          }
          break;
        default:
          throw new UnsupportedOperationException();
      }
    }
    // TODO: override the sort method to sort the elements based on the ordinals
  }
}
