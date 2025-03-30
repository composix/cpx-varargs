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

import io.github.composix.math.Ordinal;
import java.util.Collection;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;

public interface KeysI<A, N> {
  ArgsI<N> toArgsI();

  SortedSet<N> toSet();

  boolean retainAll(Collection<? extends N> rhs);

  <O extends Comparable<O>> KeysI2<A, O, N> thenByA(
    Ordinal col,
    Function<A, O> accessor
  );

  KeysI2<A, long[], N> thenByA(Ordinal col, ToLongFunction<A> accessor);

  KeysI2<A, long[], N> collectA(
    ToLongFunction<A> accessor,
    LongBinaryOperator reducer
  );

  <B> ArgsII<A, B> joinOne(KeysI<B, N> rhs);

  <B> ArgsII<A, B[]> joinMany(KeysI<B, N> rhs);
}
