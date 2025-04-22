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

import java.util.function.Function;
import java.util.function.ToLongFunction;

import io.github.composix.math.Cols;
import io.github.composix.math.Column;
import io.github.composix.models.Defaults;

public interface ArgsI<A> extends Cols {
  static <A extends Defaults<A>> ArgsI<A> of(A... columnA) {
    return (ArgsI<A>) new Table<>(columnA.length).extend(A.all(columnA));
  }

  Column<A> columnA(int pos);

  <N extends Comparable<N>> KeysI<A, N> groupByA(Function<A, N> accessor);

  LongI<A> groupByA(ToLongFunction<A> accessor);

  <B> ArgsII<A,B> joinManyB(ArgsI<B> rhs);
}
