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

import io.github.composix.math.Accessor;
import io.github.composix.math.Args;
import io.github.composix.math.Keys;
import io.github.composix.math.Ordinal;
import io.github.composix.math.SafeMatrix;
import io.github.composix.math.VarArgs;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class Table<A, N extends Comparable<N>, O>
  extends SafeMatrix
  implements ArgsI<A>, KeysI2<A, N, O> {

  protected Table(final int ordinal) {
    super(ordinal);
  }

  public ArgsI<A> orderByA() {
    return (ArgsI<A>) orderBy(A);
  }

  public Iterable<A> columnA() {
    return column(A);
  }

  public Stream<A> streamA() {
    return stream(A);
  }

  public LongStream longStreamA() {
    return longStream(A);
  }

  public <KK extends Comparable<KK>> KeysI<A, KK> groupByA(
    final Function<A, KK> accessor
  ) {
    final Accessor.OfObject accessObject = Accessor.OfObject.INSTANCE;
    orderBy(A, accessor);
    accessObject.accessor(accessor);
    final Keys result = groupBy(A, accessObject).keys(A, accessObject);
    accessObject.destroy();
    return (KeysI<A, KK>) result;
  }

  public KeysI<A, long[]> groupByA(final ToLongFunction<A> accessor) {
    final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
    orderBy(A, accessor);
    accessLong.accessor(accessor);
    final Keys result = groupBy(A, accessLong).keys(A, accessLong);
    accessLong.destroy();
    return (KeysI<A, long[]>) result;
  }

  @Override
  public <B, KK> ArgsII<A, B[]> joinMany(KeysI<B, KK> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinMany'");
  }

  @Override
  public ArgsI<N> done() {
    VarArgs varargs = varArgs();
    final int mask = varargs.mask();
    final Object[] argv = varargs.argv;
    final Ordinal[] indices = (Ordinal[]) argv[-1 & mask];
    final int size = size();
    int index = size;
    while (argv[index & mask] != null) {
      ++index;
    }
    try {
      Args result = clone();
      index -= size - 1;
      result.order().reorder(NATURAL_ORDER);
      result.order().resize(OMEGA.intValue() * index + indices.length);
      export(result, size, index);
      return (ArgsI<N>) result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
