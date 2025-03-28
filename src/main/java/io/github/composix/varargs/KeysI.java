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

import io.github.composix.math.Accessor;
import io.github.composix.math.ArgsOrdinal;
import io.github.composix.math.Keys;
import io.github.composix.math.Ordinal;

public interface KeysI<A,K> extends Keys {
    default <T, KK extends Comparable<KK>> KeysI2<K,KK,A> thenBy(Ordinal col, Function<T, KK> accessor) {
      final Accessor.OfObject accessObject = Accessor.OfObject.INSTANCE;
      accessObject.accessor(accessor);
      thenBy(col, accessObject);
      accessObject.destroy();
      return (KeysI2<K, KK, A>) this;
    }
  
    default <T> KeysI2<K,long[],A> thenBy(Ordinal col, ToLongFunction<T> accessor) {
      final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
      accessLong.accessor(accessor);
      thenBy(col, accessLong);
      accessLong.destroy();
      return (KeysI2<K, long[], A>) this;
    }

    default KeysI2<A,K,long[]> collectA(ToLongFunction<A> accessor, LongBinaryOperator reducer) {
      return (KeysI2<A, K, long[]>) collect(ArgsOrdinal.A, accessor, reducer);
    }

    <B,KK> ArgsII<A,B[]> joinMany(KeysI<B,KK> rhs);

    ArgsI<K> done();
}
