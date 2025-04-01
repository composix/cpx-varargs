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

import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ArgsII<A, B> extends ArgsI<A> {
  Map<A, B> asMap();

  Map<B, A> asInverseMap();

  @Override
  ArgsII<A, B> withHeaders();

  Iterable<B> columnB();

  Iterable<B> columnB(CharSequence header) throws NoSuchFieldException;

  LongStream longStreamB();

  @Override
  KeysII<A, B, A> onA();

  KeysII<A, B, B> onB();

  @Override
  <N extends Comparable<N>> KeysII<A, B, N> groupByA(Function<A, N> accessor);

  @Override
  LongII<A, B> groupByA(ToLongFunction<A> accessor);

  default Stream<B> streamB() {
    return StreamSupport.stream(columnB().spliterator(), false);
  }

  default Stream<B> streamB(CharSequence header) throws NoSuchFieldException {
    return StreamSupport.stream(columnB(header).spliterator(), false);
  }

  <C> ArgsIII<A, B, C> extendC(C... swaggers);
}
