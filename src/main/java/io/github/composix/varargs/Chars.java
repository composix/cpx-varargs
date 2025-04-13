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

package io.github.composix.varargs;

import io.github.composix.math.ArgsOrdinal;

public interface Chars extends ArgsOrdinal {
  /**
   * Creates a text-based vector (as Chars instance) from a column of strings. A text-based
   * column may optionally specify a header for the column by a first element that
   * ends with a colon. The header is used to identify the column in the table.
   *
   * @param columnA - text-based column to turn into a vector
   * @return - text-based column as a vector (i.e. a table with one column)
   */
  static Chars of(CharSequence... column) {
    return (Chars) new Table<>(1).extend(A, column);
  }

  Chars andOf(CharSequence... column);

  Chars with(CharSequence... column);

  Chars attrInteger(CharSequence header);

  Chars attrString(CharSequence header);

  Chars attrURI(CharSequence header);

  Attr attr();

  <X> AttrI<X> attrX(Class<X> typeX);

  <X,Y> AttrII<X,Y> attrXY(Class<X> typeX, Class<Y> typeY);
}
