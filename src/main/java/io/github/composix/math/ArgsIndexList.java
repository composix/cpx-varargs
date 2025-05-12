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

import java.util.Comparator;
import java.util.List;

class ArgsIndexList<E extends Comparable<E>> extends OrdinalList<E> implements Column<E> {
  private static final Constants CONSTANTS = Constants.getInstance();

  final ArgsSet<?> elements;

  String header;
  MutableOrder order;
  Index refs, indices;

  ArgsIndexList(byte tpos, long[] array) {
    elements = new ArgsLongSet(tpos, array);
    header = ":";
    order = null;
    refs = CONSTANTS.index();
    indices = null;
  }

  ArgsIndexList(byte tpos, Object[] array) {
    elements = new ArgsObjSet<>(tpos, array);
    header = ":";
    order = null;
    refs = CONSTANTS.index();
    indices = null;
  }

  public Ordinal getType() {
    return elements.getType();
  }

  @Override
  public Object source() {
    if (elements.indices() == null) {
      return elements.array();
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public Args attach() {
    return order.ordinal().extend(this);
  }
  
  @Override
  public void attachOrder(MutableOrder order) {
    if (refs == CONSTANTS.index()) {
      if (elements.size() < order.amount()) {
        throw new IllegalArgumentException(
          "The order is larger than the number of elements"
        );
      }
    } else {
      if (refs.size() < order.amount()) {
        throw new IllegalArgumentException(
          "The order is larger than the number of elements"
        );
      }
    }
    this.order = order;
  }

  @Override
  public int getInt(int index) {
    return elements.getInt(order.rank(refs.getInt(index)));
  }

  @Override
  public long getLong(int index) {
    return elements.getLong(order.rank(refs.getInt(index)));
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof List) {
      return super.equals(o);
    }
    if (o instanceof String) {
      return toString() == ((String) o).intern();
    }
    return false;
  }
  
  @Override
  public char charAt(int index) {
    return header.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return header.subSequence(start, end);
  }
  
  @Override
  public int length() {
    return header.length();
  }

  @Override
  public int size() {
    return order.amount();
  }

  @Override
  public E get(int index) {
    return (E) elements.get(order.rank(refs.getInt(index)));
  }

  @Override
  public ArgsSet<E> asListSet() {
    if (elements.indices() == null) {
      refs = elements.initialize(order);
    }
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
          order.reorder((lhs, rhs) ->
            Long.compare(
              longSet.array[lhs.intValue()],
              longSet.array[rhs.intValue()]
            )
          );
          break;
        case ArgsObjSet<?> objSet:
          if (comparator == null) {
            order.reorder((lhs, rhs) ->
              ((String) objSet.array[lhs.intValue()]).compareTo(
                  (String) objSet.array[rhs.intValue()]
                )
            );
          } else {
            order.reorder((lhs, rhs) ->
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
