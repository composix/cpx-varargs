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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class Table<A, B, C, N, O, P>
  extends SafeMatrix
  implements ArgsIII<A, B, C>, KeysII2<A, B, N, O>, KeysIII<A, B, C, N> {

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
    return (ArgsIII<A, B, TC>) extend(C, columnC);
  }

  @Override
  public ArgsIII<A, B, C> withHeaders() {
    super.order().skipHeader();
    return this;
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
  public Iterable<A> columnA(CharSequence header) throws NoSuchFieldException {
    return column(header, A);
  }

  @Override
  public Iterable<B> columnB() {
    return column(B);
  }

  @Override
  public Iterable<B> columnB(CharSequence header) throws NoSuchFieldException {
    return column(header, B);
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'onA'");
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
    return (KeysIII<A, B, C, KN>) _groupBy(A, accessor);
  }

  @Override
  public LongIII<A, B, C> groupByA(final ToLongFunction<A> accessor) {
    return (LongIII<A, B, C>) _groupBy(A, accessor);
  }

  public ArgsIII<N, O, P> collect() {
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
      export(result, (byte) size, index);
      return (ArgsIII<N, O, P>) result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  @Override
  public List<A> asListA() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'asListA'");
  }

  @Override
  public KeysII<A, B, N> andByA(Ordinal col, Function<A, N> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'andByA'");
  }

  @Override
  public <O extends Comparable<O>> KeysII2<A, B, N, O> thenByA(
    Ordinal col,
    Function<A, O> accessor
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA'");
  }

  @Override
  public LongII1<A, B, N> thenByA(Ordinal col, ToLongFunction<A> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA'");
  }

  @Override
  public LongII1<A, B, N> collectA(
    ToLongFunction<A> accessor,
    LongBinaryOperator reducer
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'collectA'");
  }

  @Override
  public KeysII2<A, B, N, B> thenOnB() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenOnB'");
  }

  @Override
  public <B> ArgsII<A, B> join(KeysI<B, N> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'join'");
  }

  @Override
  public KeysII2<A, B, N, O> andByA2(Ordinal col, Function<A, O> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'andByA2'");
  }

  @Override
  public <P extends Comparable<P>> KeysII3<A, B, N, O, P> thenByA2(
    Ordinal col,
    Function<A, O> accessor
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA2'");
  }

  @Override
  public LongII2<A, B, N, O> thenByA2(Ordinal col, ToLongFunction<A> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenByA2'");
  }

  @Override
  public LongII2<A, B, N, O> collectA2(
    ToLongFunction<A> accessor,
    LongBinaryOperator reducer
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'collectA2'");
  }

  @Override
  public Map<A, B> asMap() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'asMap'");
  }

  @Override
  public Map<B, A> asInverseMap() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'asInverseMap'"
    );
  }

  @Override
  public <B> ArgsII<A, B> joinMany(Function<N, Stream<B>> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinMany'");
  }

  @Override
  public KeysIII2<A, B, C, N, C> andOnC() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'andOnC'");
  }
}
