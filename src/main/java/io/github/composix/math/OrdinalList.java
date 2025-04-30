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

/**
 * Abstract base class for efficiently storing and accessing ordinal indices in a 
 * compact form. This class represents an indexed list of Ordinal elements and
 * provides methods to directly access these indices in their most efficient primitive
 * representation, such as {@code BitSet}, {@code byte[]}, {@code short[]}, etc.
 *
 * @author dr. ir. J. M. Valk
 * @since April 2025
 */
abstract class OrdinalList extends AbstractList<Ordinal> implements Index {

  /**
   * Factory method for creating an {@code OrdinalList} of the specified length,
   * choosing the most compact internal representation based on the maximum ordinal value.
   *
   * Currently supports {@code byte} and {@code short} representations.
   *
   * @param length - the length of the list to create
   * @param amount - the maximum ordinal value to be stored
   * @return an {@code OrdinalList} instance with appropriate backing
   * @throws IllegalArgumentException if amount exceeds supported range
   */
  static OrdinalList of(int length, int amount) {
    if (amount <= Byte.MAX_VALUE) {
      return new ByteIndex(length);
    }
    return new ShortIndex(length);
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
   * OrdinalList backed by a compact {@code byte[]} index.
   */
  private static final class ByteIndex extends OrdinalList {

    private final byte[] index;

    ByteIndex(final int length) {
      index = new byte[length];
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
      return Ordinal.of(index[i]);
    }

    @Override
    public int getInt(int i) {
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
  private static final class ShortIndex extends OrdinalList {

    private final short[] index;

    ShortIndex(final int length) {
      index = new short[length];
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
      return Ordinal.of(index[i]);
    }

    @Override
    public int getInt(int i) {
      return index[i];
    }

    @Override
    public void setInt(int i, int j) {
      index[i] = (short) j;
    }
  }
}
