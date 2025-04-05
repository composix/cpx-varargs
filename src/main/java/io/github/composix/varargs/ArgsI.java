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

import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.github.composix.math.Args;

public interface ArgsI<A> extends Args {
  static ArgsI<CharSequence> of(CharSequence... columnA) {
    final Table<CharSequence, ?, ?, ?, ?, ?> result = new Table<>(
      columnA.length
    );
    return (ArgsI<CharSequence>) result.extend(A, columnA);
  }

  @SafeVarargs
  static <T> ArgsI<T> of(T... columnA) {
    final Table<T, ?, ?, ?, ?, ?> result = new Table<>(columnA.length);
    return (ArgsI<T>) result.extend(A, columnA);
  }

  List<A> asListA();

  ArgsI<A> andOf(A... columnA);

  ArgsI<A> with(A... columnA);

  <T> ArgsII<A, T> extendB(T... columnB);

  ArgsI<A> withHeaders();

  ArgsI<A> orderByA();

  Iterable<A> columnA();

  Iterable<A> columnA(CharSequence header) throws NoSuchFieldException;

  LongStream longStreamA();

  KeysI<A, A> onA();

  <N extends Comparable<N>> KeysI<A, N> groupByA(Function<A, N> accessor);

  LongI<A> groupByA(ToLongFunction<A> accessor);

  default Stream<A> streamA() {
    return StreamSupport.stream(columnA().spliterator(), false);
  }

  default Stream<A> streamA(CharSequence header) throws NoSuchFieldException {
    return StreamSupport.stream(columnA(header).spliterator(), false);
  }
}
