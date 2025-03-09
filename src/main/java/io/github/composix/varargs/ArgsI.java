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

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.github.composix.math.Accessor;
import io.github.composix.math.Args;
import io.github.composix.math.Ordinal;

public interface ArgsI<A> extends Args {
    @Override
    ArgsI<A> clone() throws CloneNotSupportedException;

    default ArgsI<A> orderByA() {
        return (ArgsI<A>) orderBy(A);
    }

    default Ordinal ordinalA(A value) {
        return ordinalAt(A, value);
    }

    default Iterable<A> columnA() {
        return column(A);
    }

    default Stream<A> streamA() {
        return stream(A);
    }

    default LongStream longStreamA() {
        return longStream(A);
    }

    default Comparator<Ordinal> comparatorA() {
        return comparator(A);
    }

    @Override
    default <T, K extends Comparable<K>> KeysI<K,A> groupBy(Ordinal col, Function<T, K> accessor) {
      final Accessor.OfObject accessObject = Accessor.OfObject.INSTANCE;
      orderBy(col, accessor);
      accessObject.accessor(accessor);
      groupBy(col, accessObject);
      accessObject.destroy();
      return (KeysI<K,A>) this;
    }

    @Override
    default <T> KeysI<long[],A> groupBy(Ordinal col, ToLongFunction<T> accessor) {
        final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
        orderBy(col, accessor);
        accessLong.accessor(accessor);
        groupBy(col, accessLong);
        accessLong.destroy();
        return (KeysI<long[],A>) this;
      }
}
