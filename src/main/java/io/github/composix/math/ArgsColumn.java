/**
 * class ArgsColumn
 *
 * This class provides the base list implementation of the Column interface.
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

class ArgsColumn<E extends Comparable<E>>
  extends OrdinalList<E>
  implements Column<E> {

  private static final Constants CONSTANTS = Constants.getInstance();

  byte tpos;
  OrdinalList<E> elements;

  String header;
  MutableOrder order;
  Index refs, indices;

  ArgsColumn(byte tpos, Range<E> range) {
    this.tpos = tpos;
    elements = range;
    header = ":";
    order = null;
    refs = CONSTANTS.index();
    indices = null;
  }

  ArgsColumn(byte tpos, long[] array) {
    this.tpos = tpos;
    elements = (OrdinalList<E>) new OrdinalList.LongIndex(array);
    header = ":";
    order = null;
    refs = CONSTANTS.index();
    indices = null;
  }

  ArgsColumn(byte tpos, Object[] array) {
    this.tpos = tpos;
    elements = new ArgsObjSet<>(array);
    header = ":";
    order = null;
    refs = CONSTANTS.index();
    indices = null;
  }

  // from Object

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

  // from CharSequence

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

  // from Index

  @Override
  public int getInt(int index) {
    return elements.getInt(order.rank(refs.getInt(index)));
  }

  @Override
  public long getLong(int index) {
    return elements.getLong(order.rank(refs.getInt(index)));
  }

  // from List

  @Override
  public int size() {
    return order.amount();
  }

  @Override
  public E get(int index) {
    return (E) elements.get(order.rank(refs.getInt(index)));
  }

  @Override
  public void sort(Comparator<? super E> comparator) {
    if (!elements.isRange()) {
      if (comparator == null) {
        elements.reorder(order);
      } else {
        elements.reorder(order, comparator);
      }
    } else {
      if (comparator == null) {
        refs.reorder(order);
      } else {
        reorder(order, comparator);
      }
    }
  }

  // from RangedList

  @Override
  public Range<E> range() {
    if (!elements.isRange()) {
      initialize();
    }
    return (Range<E>) elements;
  }

  @Override
  public Index cumulativeCounts() {
    if (!elements.isRange()) {
      initialize();
    }
    return ((Range<?>) elements).indices;
  }

  @Override
  public void cumulativeCounts(Index result) {
    if (!elements.isRange()) {
      initialize();
    }
    final Index source = ((Range<?>) elements).indices;
    final int size = elements.size();
    for (int i = 0; i < size; ++i) {
      indices.setInt(i, source.getInt(i));
    }
  }

  @Override
  public Index ranks() {
    if (!elements.isRange()) {
      initialize();
    }
    return refs;
  }

  @Override
  public void ranks(Index result) {
    if (!elements.isRange()) {
      initialize();
    }
    final int size = elements.size();
    for (int i = 0; i < size; ++i) {
      indices.setInt(i, refs.getInt(i));
    }
  }

  // from Column

  public Ordinal getType() {
    return OrdinalNumber.ORDINALS[tpos];
  }

  @Override
  public Object source() {
    if (!elements.isRange()) {
      return elements.asArray();
    }
    return null;
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

  private void initialize() {
    final int amount = order.amount();
    elements.reorder(order);
    refs = Index.of(amount);
    int count;
    if (elements.asArray() instanceof Object[]) {
      count = elements.ranks(order, refs);
    } else {
      count = elements.ranks(order, refs);
      elements = elements.range(count, amount, refs, order);
    }
  }
}
