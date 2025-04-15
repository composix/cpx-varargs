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

import java.net.URI;

public interface Cols extends ArgsOrdinal {
  /**
   * Retrieve the column at the first type position. For example,
   * column(A) returns the first column, column(B) returns the first
   * column of type that differs from typeOf(A), column(C) returns
   * the first column of type that differs from typeOf(B), and so on.
   *
   * Note that this method is equivalent to column(tpos, 1).
   *
   * @param tpos - the type position of the column
   * @return a list of values in the column
   */
  <T> ArgsList<T> column(Ordinal type);

  /**
   * Retrieve the column at the pos-th type position. For example,
   * column(tpos, 2) return the column directly following column(tpos),
   * column(tpos, 3) returns the column directly following column(tpos, 2), and so on.
   *
   * @param tpos - the type position of the column
   * @param pos - the position within columns of same type
   * @return a list of values in the column
   * @throws IndexOutOfBoundsException - if pos is out of bounds
   */
  <T> ArgsList<T> column(Ordinal type, int pos);

  default ArgsList<Boolean> booleanColumn(int pos) {
    return column(AA, pos);
  }

  default ArgsList<Byte> byteColumn(int pos) {
    return column(AB, pos);
  }

  default ArgsList<Character> charColumn(int pos) {
    return column(AB, pos);
  }

  default ArgsList<Short> shortColumn(int pos) {
    return column(AC, pos);
  }

  default ArgsList<Integer> intColumn(int pos) {
    return column(AI, pos);
  }

  default ArgsList<Long> longColumn(int pos) {
    return column(AL, pos);
  }

  default ArgsList<String> stringColumn(int pos) {
    return column(S, pos);
  }

  default ArgsList<URI> uriColumn(int pos) {
    return column(U, pos);
  }
}
