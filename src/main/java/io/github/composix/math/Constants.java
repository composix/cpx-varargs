/**
 * class Constants
 *
 * Provides constant values used throughout the library, including a predefined
 * set of ordinals and the ordinal type system.
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

import java.util.BitSet;

/**
 * This singleton is intended to be loaded at startup and is accessible via
 * {@code ArgsOrdinal.CONSTANTS}. It can be used to verify that arrays of values
 * conform to the expected types according to the ordinal type system, e.g., 
 * {@code CONSTANTS.check(AL, 0L, 1L, 2L)}.
 *
 * @author dr. ir. J. M. Valk
 * @since April 2025
 */
final class Constants {

  static final int SIZE = 16;

  static Constants INSTANCE;

  static Constants getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Constants();
      for (int i = 0; i <= Short.MAX_VALUE; ++i) {
        INSTANCE.ordinal(i);
      }
      INSTANCE.omega();
      final Ordinal[] ordinals = INSTANCE.ordinals;
      final Ordinal[] columns = INSTANCE.columns;
      columns[0] = ordinals[0];
      columns[1] = ordinals[-Short.MIN_VALUE];
      for (int i = 2; i < columns.length; ++i) {
        columns[i] = new OrdinalInt(i * -Short.MIN_VALUE);
      }
      // type definitions
      ordinals[18].all("");
      ordinals[26].any(false);
      ordinals[27].any((byte) 0);
      ordinals[28].any(' ');
      ordinals[29].any((short) 0);
      ordinals[30].all(new BitSet());
      ordinals[31].all(new byte[0]);
      ordinals[32].all(new char[0]);
      ordinals[33].all(new short[0]);
      ordinals[34].any(0);
      ordinals[35].all(new int[0]);
      ordinals[36].all(new long[0]);
      ordinals[37].any(0L);
    }
    return INSTANCE;
  }

  final Object[] types;
  final Ordinal[] ordinals;
  final Ordinal[] columns;

  private Constants() {
    types = new Object[26];
    ordinals = new Ordinal[Short.MAX_VALUE - Short.MIN_VALUE];
    columns = new Ordinal[Byte.MAX_VALUE - Byte.MIN_VALUE];
  }

  /**
   * Check the type of the given array against the expected type for the
   * specified ordinal. If the type is not yet defined, it will be set to the
   * type of the given array. If the type is already defined and does not match
   * the type of the given array, an {@code IllegalArgumentException} will be
   * thrown.
   * 
   * @param tpos - the type position to check
   * @param array - the array to check
   * @throws IllegalArgumentException if the type of the given array does not
   *         match the expected type for the specified ordinal
   * @throws NullPointerException if the given array is null
   * @return the type position of as a single byte value
   * @since April 2025
   */
  public byte check(Ordinal tpos, Object array) {
    final byte result = tpos.byteValue();
    final int index = result - SIZE;
    Object type = types[index];
    if (type == null) {
      types[index] = array;
      return -1;
    }
    if (type.getClass() != array.getClass()) {
      throw new IllegalArgumentException(
        "type mismatch: expected=" +
        type.getClass().getComponentType() +
        "; actual=" +
        array.getClass().getComponentType()
      );
    }
    return result;
  }

  Ordinal ordinal(int index) {
    Ordinal ordinal = ordinals[index];
    if (ordinal == null) {
      ordinal = new OrdinalInt(index);
      if (ordinals[index] == null) {
        ordinals[index] = ordinal;
      } else {
        ordinal = ordinals[index];
      }
    }
    return ordinal;
  }

  Ordinal omega() {
    return ordinal(-Short.MIN_VALUE);
  }
}
