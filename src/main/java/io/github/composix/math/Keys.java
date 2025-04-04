/**
 * interface Keys
 *
 * This interface augments the Args interface with methods for collecting, joining
 * and further grouping.
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

import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;

public interface Keys {
  Args done();

  <T> Args collect(
    Ordinal col,
    ToLongFunction<T> accessor,
    LongBinaryOperator reducer
  );

  Keys joinOne(Keys rhs);

  Keys joinMany(Keys rhs);

  Keys on(Ordinal col, int pos);

  void thenBy(Ordinal col, Accessor accessor);

  default <T, K extends Comparable<K>> Keys thenBy(
    Ordinal col,
    Function<T, K> accessor
  ) {
    final Accessor.OfObject accessObject = Accessor.OfObject.INSTANCE;
    accessObject.accessor(accessor);
    thenBy(col, accessObject);
    accessObject.destroy();
    return this;
  }

  default <T> Keys thenBy(Ordinal col, ToLongFunction<T> accessor) {
    final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
    accessLong.accessor(accessor);
    thenBy(col, accessLong);
    accessLong.destroy();
    return this;
  }
}
