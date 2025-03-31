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

package io.github.composix.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.github.composix.models.Defaults;

public interface Args extends ArgsOrdinal, Order {
    @Override
    Args clone() throws CloneNotSupportedException;

    void export(Args target, int offset, int size);

    Args select(Order order);

    <T> T getValue(int index);

    default Args getArgsValue(int index) {
        return (Args) getValue(index);
    }

    long getLongValue(int index);
    
    Comparator<Ordinal> comparator(Ordinal ordinal);

    <T,K extends Comparable<K>> Comparator<Ordinal> comparator(Ordinal ordinal, Function<T,K> accessor);

    <T> Comparator<Ordinal> comparator(Ordinal ordinal, ToLongFunction<T> accessor);

    <T> Iterable<T> column(Ordinal ordinal);

    <T> Iterable<T> column(Ordinal ordinal, CharSequence header) throws NoSuchFieldException;

    <T> Stream<T> stream(Ordinal col);

    <T> Stream<T> stream(Class<T> type, int pos) throws NoSuchFieldException;

    LongStream longStream(Ordinal col);

    LongStream longStream(int pos) throws NoSuchFieldException;

    Ordinal ordinalAt(Ordinal ordinal, Object value);

    Args split(Pattern pattern);

    <T extends Defaults<T>> Args combine(T defaults) throws NoSuchFieldException;

    Keys groupBy(Ordinal col, Accessor accessor);

    default <T> Stream<T> stream(Class<T> type) throws NoSuchFieldException {
        return stream(type, 0);
    }
    
    default <T, K extends Comparable<K>> Keys groupBy(
      Ordinal col,
      Function<T, K> accessor
    ) {
      final Accessor.OfObject accessObject = Accessor.OfObject.INSTANCE;
      orderBy(col, accessor);
      accessObject.accessor(accessor);
      final Keys result = groupBy(col, accessObject).keys(col, accessObject);
      accessObject.destroy();
      return result;
    }
  
    default <T> Keys groupBy(Ordinal col, ToLongFunction<T> accessor) {
      final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
      orderBy(col, accessor);
      accessLong.accessor(accessor);
      final Keys result = groupBy(col, accessLong).keys(col, accessor);
      accessLong.destroy();
      return result;
    }

    default <T> Keys on(Ordinal col, ToLongFunction<T> accessor) {
        orderBy(col, accessor);
        final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
        accessLong.accessor(accessor);
        final Keys result = groupBy(col, accessLong).keys(col, accessLong);
        accessLong.destroy();
        return result;
    }

    default Keys on(Ordinal col) {
        orderBy(col);
        Accessor accessor = Accessor.of(typeOf(col));
        final Keys result = groupBy(col, accessor).keys(col, accessor);
        accessor.destroy();
        return result;
    }
    
    default Args orderBy(Ordinal col) {
        order().reorder(comparator(col));
        return this;
    }

    default <T,K extends Comparable<K>> Args orderBy(Ordinal ordinal, Function<T,K> accessor) {
        order().reorder(comparator(ordinal, accessor));
        return this;
    }

    default <T> Args orderBy(Ordinal ordinal, ToLongFunction<T> accessor) {
        order().reorder(comparator(ordinal, accessor));
        return this;
    }
}
