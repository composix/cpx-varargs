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
   * Currently supports {@code BitSet}, {@code byte[]}, {@code short[]}, {@code int[]},
   * and {@code long[]} representations.
   *
   * @param length - the length of the list to create
   * @param lastIndex - the maximum ordinal value to be stored
   * @return an {@code OrdinalList} instance with appropriate backing
   */
  static OrdinalList<Ordinal> of(int length, int lastIndex) {
    if (lastIndex < 2) {
      return new BitIndex(length);
    }
    if (lastIndex <= Byte.MAX_VALUE) {
      return new ByteIndex(length);
    }
    if (lastIndex <= Short.MAX_VALUE) {
      return new ShortIndex(length);
    }
    if (lastIndex <= Integer.MAX_VALUE) {
      return new IntIndex(length);
    }
    return new LongIndex(length);
  }

  public int compareTo(List<E> other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setInt(int index, int element) {
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
  public RangedList<E> range() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int count(E element) {
    throw new UnsupportedOperationException();
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

  /**
   * Converts this list into an {@link ArgsSet} for storing values of a given target
   * type. If the target type is ordinal (i.e., primitive booleans, bytes, short, ints,
   * or longs) then the values in this list are directly stored into the set. Otherwise,
   * sufficient capacity is allocated in the set to store a corresponding distinct value
   * for each ordinal in this list.
   *
   * @param tpos - the target type position in the ordinal type system
   *               (e.g., tpos = 37 for longs)
   * @return an newly created ArgsSet corresponding to this list
   * @throws IllegalArgumentException if no type is associated with the given
   * target type position.
   */
  ArgsSet<?> toArgsSet(byte tpos) {
    switch (tpos) {
      case 37: // AL
        return new ArgsLongSet(tpos, null, asLongArray());
      default:
        throw new IllegalArgumentException("Invalid tpos: " + tpos);
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
  static final class IntIndex extends OrdinalList<Ordinal> {

    private final int[] index;

    IntIndex(final int length) {
      this.index = new int[length];
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
      index[i] = j;
    }
  }

  /**
   * OrdinalList backed by a {@code long[]} index.
   */
  static final class LongIndex extends OrdinalList<Ordinal> {

    private final long[] index;

    LongIndex(final int length) {
      this.index = new long[length];
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
    public Ordinal get(int i) {
      return Ordinal.of(getInt(i));
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
  }
}
