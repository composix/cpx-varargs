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

import io.github.composix.math.Args;
import io.github.composix.math.Ordinal;
import io.github.composix.math.SafeMatrix;
import io.github.composix.math.VarArgs;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;

class Table<A, B, C, P, O, N>
  extends SafeMatrix
  implements ArgsIII<A, B, C>, KeysII2<A, B, O, N> {

  protected Table(final int ordinal) {
    super(ordinal);
  }

  @Override
  public ArgsI<A> andOf(A... columnA) {
    extend(Ordinal.of(size()), columnA);
    return this;
  }

  @Override
  public ArgsI<A> with(A... columnA) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'with'");
  }

  @Override
  public <T> ArgsII<A, T> extendB(T... columnB) {
    return (ArgsII<A, T>) extend(B, columnB);
  }

  @Override
  public <TC> ArgsIII<A, B, TC> extendC(TC... columnC) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'extendC'");
  }

  @Override
  public ArgsI<A> orderByA() {
    return (ArgsI<A>) orderBy(A);
  }

  @Override
  public Iterable<A> columnA() {
    return column(A);
  }

  @Override
  public Iterable<A> columnA(CharSequence header) {
    return column(A, header);
  }

  @Override
  public Iterable<B> columnB() {
    return column(B);
  }

  @Override
  public Iterable<B> columnB(CharSequence header) {
    return column(B, header);
  }

  @Override
  public LongStream longStreamA() {
    return longStream(A);
  }

  @Override
  public LongStream longStreamB() {
    return longStream(B);
  }

  @Override
  public KeysIII<A, B, C, A> onA() {
    return (KeysIII<A, B, C, A>) on(A);
  }

  @Override
  public KeysIII<A, B, C, B> onB() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'onB'");
  }

  @Override
  public KeysIII<A, B, C, C> onC() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'onC'");
  }

  @Override
  public <KN extends Comparable<KN>> KeysIII<A, B, C, KN> groupByA(
    final Function<A, KN> accessor
  ) {
    return (KeysIII<A, B, C, KN>) groupBy(A, accessor);
  }

  @Override
  public KeysIII<A, B, C, long[]> groupByA(final ToLongFunction<A> accessor) {
    return (KeysIII<A, B, C, long[]>) groupBy(A, accessor);
  }

  public ArgsI<N> toArgsI() {
    return toArgs();
  }

  public ArgsII<N, O> toArgsII() {
    return toArgs();
  }

  public ArgsIII<N, O, P> toArgsIII() {
    return (ArgsIII<N, O, P>) toArgs();
  }

  private ArgsII<N, O> toArgs() {
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
      return (ArgsII<N, O>) result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  @Override
  public SortedSet<N> toSet() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'toSet'");
  }

  @Override
  public boolean retainAll(Collection<? extends N> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
  }

  @Override
  public <TB> ArgsII<A, TB[]> joinMany(KeysI<TB, N> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinMany'");
  }

  @Override
  public KeysII2<A, B, B, N> thenOnB() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenOnB'");
  }

  @Override
  public <B> ArgsII<A, B> joinOne(KeysI<B, N> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinOne'");
  }

  @Override
  public <P extends Comparable<P>> KeysI3<A, P, N, O> thenByA(
    Ordinal col,
    Function<A, P> accessor
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA'");
  }

  @Override
  public KeysI3<A, long[], N, O> thenByA(
    Ordinal col,
    ToLongFunction<A> accessor
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA'");
  }

  @Override
  public KeysI3<A, long[], N, O> collectA(
    ToLongFunction<A> accessor,
    LongBinaryOperator reducer
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'collectA'");
  }

  @Override
  public Map<N, O> toMap() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'toMap'");
  }

  @Override
  public <P extends Comparable<P>> KeysII3<A, B, P, N, O> thenByA2(
    Ordinal col,
    Function<A, P> accessor
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA2'");
  }
}
