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
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongFunction;

import io.github.composix.math.Column;
import io.github.composix.math.Ordinal;
import io.github.composix.math.SafeMatrix;
import io.github.composix.models.Defaults;

class Table<A extends Defaults<A>, B, C, N, O, P>
  extends SafeMatrix
  implements Chars,ArgsIII<A, B, C>, KeysII2<A, B, N, O>, KeysIII<A, B, C, N>, LongII2<A,B,N,O> {

  Table(final int ordinal) {
    super(ordinal);
  }

  // from Chars interface 
  
  @Override
  public Chars andOf(CharSequence... column) {
    extend(A, column);
    return this;
  }

  @Override
  public Chars with(CharSequence... column) {
    if (column.length != OMEGA.amount(ordinal)) {
      throw new IllegalArgumentException(
        "invalid number of rows: " + column.length + " != " + OMEGA.amount(ordinal)
      );
    }
    extend(A, column);
    return this;
  }

  @Override
  public <KN extends Comparable<KN>> KeysIII<A, B, C, KN> groupByA(
    final Function<A, KN> accessor
  ) {
    return (KeysIII<A, B, C, KN>) groupBy(A, accessor);
  }

  @Override
  public LongIII<A, B, C> groupByA(final ToLongFunction<A> accessor) {
    return (LongIII<A, B, C>) _groupBy(A, accessor);
  }

  // from the Keys interface

  public ArgsIII<N, O, P> done() {
    return (ArgsIII<N, O, P>) $done();
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
    return (LongII1<A, B, N>) collect(A, accessor, reducer);
  }

  @Override
  public KeysII2<A, B, N, B> thenOnB() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenOnB'");
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
  public LongI1<A, N> andByA(Ordinal col, ToLongFunction<A> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'andByA'");
  }

  @Override
  public Column<B> columnB(int pos) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'columnB'");
  }

  @Override
  public Column<A> columnA(int pos) {
    return column(A, pos);
  }

  @Override
  public <B> ArgsII<A, B> joinManyB(ArgsI<B> rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinManyB'");
  }

  @Override
  public Chars attrInteger(CharSequence header) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'attrInteger'");
  }

  @Override
  public Chars attrString(CharSequence header) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'attrString'");
  }

  @Override
  public Chars attrURI(CharSequence header) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'attrURI'");
  }

  @Override
  public Attr attr() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'attr'");
  }

  @Override
  public <X> AttrI<X> attrX(Class<X> typeX) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'attrX'");
  }

  @Override
  public <X, Y> AttrII<X, Y> attrXY(Class<X> typeX, Class<Y> typeY) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'attrXY'");
  }
}
