/**
 * class OrdinalList
 *
 * An abstract indexed list of Ordinal elements backed by the most compact
 * possible representation, such as BitSet, byte[], short[], etc. It is
 * primarily intended for internal use in scenarios involving large-scale
 * tabular data processing where compact storage and fast lookups are required.
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
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Abstract base class for efficiently storing and accessing ordinal indices in a
 * compact form. This class represents an indexed list of Ordinal elements and
 * provides methods to directly access these indices in their most efficient primitive
 * representation, such as {@code BitSet}, {@code byte[]}, {@code short[]}, etc.
 *
 * @author dr. ir. J. M. Valk
 * @since April 2025
 */
abstract class OrdinalList<E extends Comparable<E>>
  extends AbstractList<E>
  implements Comparable<List<E>>, RangedList<E> {

  /**
   * Factory method for creating an {@code OrdinalList} of the specified length,
   * choosing the most compact internal representation based on the maximum ordinal value.
   *
   * Ordinals are used to support {@code BitSet}, {@code byte[]} and {@code short[]} under
   * the same boxed representation as an {@code Ordinal} for convienience. Use specialized
   * factory methods for creating large numerical values: e.g., for {@code int[]},
   * and {@code long[]} representations.
   *
   * @param length - the length of the list to create
   * @param lastIndex - the maximum ordinal value to be stored
   * @return an {@code OrdinalList} instance with appropriate backing
   * or null if no suitable representation is found
   */
  static OrdinalList<Ordinal> of(int length, short lastIndex) {
    if (lastIndex < 2) {
      return new BitIndex(length);
    }
    if (lastIndex <= Byte.MAX_VALUE) {
      return new ByteIndex(length);
    }
    return new ShortIndex(length);
  }

  /**
   * Factory method for creating an {@code OrdinalList} of the specified length,
   * choosing the most compact internal representation based on the maximum ordinal value.
   *
   * Longs are used to support {@code int[]}, and {@code long[]} under the same boxed
   * representation as an {@code Ordinal} for convienience.
   *
   * @param length - the length of the list to create
   * @param lastIndex - the maximum ordinal value to be stored
   * @return an {@code OrdinalList} instance with appropriate backing
   * or null if no suitable representation is found
   */
  static OrdinalList<Long> ofLong(int length, long lastIndex) {
    if (lastIndex <= Integer.MAX_VALUE) {
      return new IntIndex(length);
    }
    return new LongIndex(length);
  }

  // from Comparable

  @Override
  public int compareTo(List<E> other) {
    throw new UnsupportedOperationException();
  }

  // from Index

  @Override
  public int getInt(final int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getLong(final int index) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public long getIndexedLong(int index) {
    final long omega = ArgsOrdinal.OMEGA.longValue();
    return getLong(index) * omega + index;
  }

  @Override
  public IntStream intStream() {
    return IntStream.range(0, size()).map(this::getInt);
  }

  @Override
  public LongStream longStream() {
    return IntStream.range(0, size()).mapToLong(this::getLong);
  }

  @Override
  public LongStream indexedStream() {
    return IntStream.range(0, size()).mapToLong(this::getIndexedLong);
  }

  @Override
  public void setInt(int index, int element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reorder(final MutableOrder order) {
    order.reorder((lhs, rhs) ->
      get(lhs.intValue()).compareTo(get(rhs.intValue()))
    );
  }

  // from RangedList

  @Override
  public RangedList<E> range() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int count(E element) {
    RangedList<E> range = range();
    final int index = Collections.binarySearch(range, element);
    if (index < 0) {
      return 0;
    }
    return cumulativeCounts().getInt(index);
  }

  @Override
  public Index cumulativeCounts() {
    final Index result = Index.of(range().size());
    cumulativeCounts(result);
    return result;
  }

  @Override
  public void cumulativeCounts(Index result) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Index ranks() {
    final Index result = Index.of(size());
    ranks(result);
    return result;
  }

  @Override
  public void ranks(Index result) {
    throw new UnsupportedOperationException();
  }

  // package-private

  boolean isRange() {
    return false;
  }
  
  void reorder(final MutableOrder order, Comparator<? super E> comparator) {
    order.reorder((lhs, rhs) -> comparator.compare(get(lhs.intValue()), get(rhs.intValue())));
  }

  int ranks(final Order order, Index ranks) {
    final int size = ranks.size();
    int count = 0, rank = order.rank(0);
    ranks.setInt(rank, count);
    E current = get(rank);
    for (int i = 1; i < size; ++i) {
      rank = order.rank(i);
      if (!current.equals(current = get(rank))) {
        ++count;
      }
      ranks.setInt(rank, count);
    }
    return ++count;
  }

  Range<E> range(int count, int amount, Index result, Order order) {
    throw new UnsupportedOperationException();
  }

  Object asArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the backing representation of this list only if it is a
   * byte array. This method is only implemented by subclasses that use
   * a {@code BitSet} as backing.
   *
   * @return the backing representation of this list
   * @throws UnsupportedOperationException if the backing representation
   * is not a BitSet
   */
  BitSet asBitSet() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the backing representation of this list only if it is a
   * byte array. This method is only implemented by subclasses that use
   * a {@code byte[]} as backing.
   *
   * @return the backing representation of this list
   * @throws UnsupportedOperationException if the backing representation
   * is not a byte array
   */
  byte[] asByteArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the backing representation of this list only if it is a
   * byte array. This method is only implemented by subclasses that use
   * a {@code short[]} as backing.
   *
   * @return the backing representation of this list
   * @throws UnsupportedOperationException if the backing representation
   * is not a short array
   */
  short[] asShortArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the backing representation of this list only if it is a
   * byte array. This method is only implemented by subclasses that use
   * a {@code int[]} as backing.
   *
   * @return the backing representation of this list
   * @throws UnsupportedOperationException if the backing representation
   * is not an int array
   */
  int[] asIntArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the backing representation of this list only if it is a
   * byte array. This method is only implemented by subclasses that use
   * a {@code long[]} as backing.
   *
   * @return the backing representation of this list
   * @throws UnsupportedOperationException if the backing representation
   * is not a long array
   */
  long[] asLongArray() {
    throw new UnsupportedOperationException();
  }

  static final class ComparableList<E extends Comparable<E>>
    extends OrdinalList<E> {

    private final E[] array;

    ComparableList(E[] array) {
      this.array = array;
    }

    @Override
    Object asArray() {
      return array;
    }

    @Override
    public int size() {
      return array.length;
    }

    @Override
    public E get(int index) {
      return array[index];
    }
  }

  /**
   * OrdinalList backed by a compact {@code byte[]} index.
   */
  static final class BitIndex extends OrdinalList<Ordinal> {

    private final byte slack;
    private final BitSet index;

    BitIndex(final int length) {
      final int size = Long.SIZE;
      slack = (byte) ((size - (length % size)) % size);
      index = new BitSet(slack == 0 ? length / size : length / size + 1);
    }

    @Override
    Object asArray() {
      final int size = size();
      final boolean[] result = new boolean[size];
      for (int i = 0; i < size; ++i) {
        result[i] = index.get(i);
      }
      return result;
    }

    @Override
    BitSet asBitSet() {
      return index;
    }

    @Override
    public int size() {
      return index.size() - slack;
    }

    @Override
    public Ordinal get(int i) {
      return Ordinal.of(getInt(i));
    }

    @Override
    public int getInt(int i) {
      return index.get(i) ? 1 : 0;
    }

    @Override
    public long getLong(int i) {
      return index.get(i) ? 1L : 0L;
    }

    @Override
    public void setInt(int i, int j) {
      index.set(i, j != 0);
    }
  }

  /**
   * OrdinalList backed by a compact {@code byte[]} index.
   */
  static final class ByteIndex extends OrdinalList<Ordinal> {

    private final byte[] index;

    ByteIndex(final int length) {
      index = new byte[length];
    }

    @Override
    public void reorder(final MutableOrder order) {
      order.reorder((lhs, rhs) ->
        Byte.compare(index[lhs.intValue()], index[rhs.intValue()])
      );
    }
    
    @Override
    Object asArray() {
      return index;
    }

    @Override
    byte[] asByteArray() {
      return index;
    }

    @Override
    public int size() {
      return index.length;
    }

    @Override
    public Ordinal get(int i) {
      return Ordinal.of(getInt(i));
    }

    @Override
    public int getInt(int i) {
      return index[i];
    }

    @Override
    public long getLong(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = (byte) j;
    }
  }

  /**
   * OrdinalList backed by a compact {@code short[]} index.
   */
  static final class ShortIndex extends OrdinalList<Ordinal> {

    private final short[] index;

    ShortIndex(final int length) {
      index = new short[length];
    }

    @Override
    public void reorder(final MutableOrder order) {
      order.reorder((lhs, rhs) ->
        Short.compare(index[lhs.intValue()], index[rhs.intValue()])
      );
    }
    
    @Override
    Object asArray() {
      return index;
    }

    @Override
    short[] asShortArray() {
      return index;
    }

    @Override
    public int size() {
      return index.length;
    }

    @Override
    public Ordinal get(int i) {
      return Ordinal.of(getInt(i));
    }

    @Override
    public int getInt(int i) {
      return index[i];
    }

    @Override
    public long getLong(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = (short) j;
    }
  }

  /**
   * OrdinalList backed by a compact {@code int[]} index.
   */
  static final class IntIndex extends OrdinalList<Long> {

    private final int[] index;

    IntIndex(final int length) {
      this.index = new int[length];
    }

    @Override
    public void reorder(final MutableOrder order) {
      order.reorder((lhs, rhs) ->
        Integer.compare(index[lhs.intValue()], index[rhs.intValue()])
      );
    }
    
    @Override
    Object asArray() {
      return index;
    }

    @Override
    int[] asIntArray() {
      return index;
    }

    @Override
    public int size() {
      return index.length;
    }
 
    @Override
    public Long get(int i) {
      return getLong(i);
    }

    @Override
    public int getInt(int i) {
      return index[i];
    }

    @Override
    public long getLong(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = j;
    }
  }

  /**
   * OrdinalList backed by a {@code long[]} index.
   */
  static final class LongIndex extends OrdinalList<Long> {

    private final long[] index;

    LongIndex(final long[] index) {
      this.index = index;
    }

    LongIndex(final int length) {
      this.index = new long[length];
    }

    @Override
    public void reorder(final MutableOrder order) {
      order.reorder((lhs, rhs) ->
        Long.compare(index[lhs.intValue()], index[rhs.intValue()])
      );
    }
    
    @Override
    Object asArray() {
      return index;
    }

    @Override
    long[] asLongArray() {
      return index;
    }

    @Override
    public int size() {
      return index.length;
    }

    @Override
    public Long get(int i) {
      return getLong(i);
    }

    @Override
    public int getInt(int i) {
      return (int) index[i];
    }

    @Override
    public long getLong(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = j;
    }

    @Override
    int ranks(final Order order, Index ranks) {
      final int size = ranks.size();
      int count = 0, rank = order.rank(0);
      ranks.setInt(rank, count);
      long current = getLong(rank);
      for (int i = 1; i < size; ++i) {
        rank = order.rank(i);
        if (current != (current = getLong(rank))) {
          ++count;
        }
        ranks.setInt(rank, count);
      }
      return ++count;
    }
    
    @Override
    Range<Long> range(int count, int amount, Index result, Order order) {
      final Index indices = Index.of(count, amount);
      final long[] array = new long[count];
      count = 0;
      int rank = order.rank(0);
      int current = result.getInt(rank);
      array[0] = getLong(rank);
      for (int i = 1; i < amount; ++i) {
        rank = order.rank(i);
        if (current != (current = result.getInt(rank))) {
          indices.setInt(count++, i);
          array[count] = getLong(rank);
        }
      }
      indices.setInt(count, amount);
      Range<Long> range = new ArgsLongSet(array);
      range.indices = indices;
      return range;
      }
  
  }
}
