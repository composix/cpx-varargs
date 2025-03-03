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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Matrix extends OrderInt implements Keys, Args {

  private static final Object[] ARGV = new Object[-Short.MIN_VALUE];
  private static final int MASK = Short.MAX_VALUE;
  private static final Ordinal[] ALL = new Ordinal[] { A };

  @Override
  public Ordinal ordinal() {
    return this;
  }

  @Override
  public Args clone() throws CloneNotSupportedException {
    if (!isOrdinal()) {
      throw new CloneNotSupportedException(
        "reordered varargs cannot be cloned"
      );
    }
    return (Args) super.clone();
  }

  @Override
  public MutableOrder order() {
    return this;
  }

  @Override
  public Args extend(Ordinal col, Object... arrays) {
    if (!OMEGA.contains(col)) {
      throw new IndexOutOfBoundsException();
    }
    if (!arrays[0].getClass().isArray()) {
      OBJECT[0] = arrays;
      arrays = OBJECT;
    }
    final int omega = OMEGA.intValue();
    final int index = col.intValue();
    final int length = arrays.length;
    ordinal += omega * (index + length);

    final Object[] argv = argv();
    int target = hashCode() + index;
    for (int i = 0; i < length; ++i) {
      if (argv[mask(target)] == null) {
        argv[mask(target++)] = arrays[i];
      } else {
        throw new IllegalStateException("hash collision");
      }
    }
    int amount = ordinal % omega;
    for (int i = 0; i < length; ++i) {
      amount = Math.max(amount, Array.getLength(arrays[i]));
    }
    resize(amount);
    return this;
  }

  @Override
  public Args select(Order order) {
    final int omega = OMEGA.intValue(), oldSize = ordinal / omega, newSize =
      order.ordinal().intValue();
    if (oldSize < newSize) {
      throw new IndexOutOfBoundsException();
    }
    final Object[] argv = argv();
    final int hashCode = hashCode(), mask = mask();
    order.permute(hashCode, mask, argv);
    for (int i = newSize; i < oldSize; ++i) {
      argv[(hashCode + i) & mask] = null;
    }
    ordinal = newSize * omega + (ordinal % omega);
    return this;
  }

  @Override
  public <T> T getValue(int index) {
    return OMEGA.getValue(argv(), hashCode() & MASK, index, ordinals);
  }

  @Override
  public long getLongValue(int index) {
    return OMEGA.getLongValue(argv(), hashCode() & MASK, index, ordinals);
  }

  @Override
  public Comparator<Ordinal> comparator(Ordinal ordinal) {
    return Comparator.comparing(
      Fn.of(ordinal::index).intAndThen(this::getValue)
    );
  }

  @Override
  public <T, K extends Comparable<K>> Comparator<Ordinal> comparator(
    Ordinal ordinal,
    Function<T, K> accessor
  ) {
    final T[] source = argv(ordinal.intValue());
    return (lhs, rhs) ->
      accessor
        .apply(source[lhs.intValue()])
        .compareTo(accessor.apply(source[lhs.intValue()]));
  }

  @Override
  public <T> Comparator<Ordinal> comparator(
    Ordinal ordinal,
    ToLongFunction<T> accessor
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'comparator'"
    );
  }

  @Override
  public <T> Stream<T> stream(Ordinal ordinal) {
    if (contains(ordinal)) {
      return (Stream<T>) stream((Object[]) argv(ordinal.intValue()));
    }
    throw new IndexOutOfBoundsException();
  }

  @Override
  public LongStream longStream(Ordinal ordinal) {
    if (contains(ordinal)) {
      return stream((long[]) argv(ordinal.intValue()));
    }
    throw new IndexOutOfBoundsException();
  }

  @Override
  public Ordinal ordinalAt(final Ordinal col, Object value) {
    if (isOrdinal()) {
      if (value.getClass() == Long.class) {
        final int index = Arrays.binarySearch(
          (long[]) argv(col.intValue()),
          ((Long) value).longValue()
        );
        return index < 0 ? OMEGA : ORDINALS[index];
      }
      final int index = Arrays.binarySearch(
        (Object[]) argv(col.intValue()),
        value
      );
      return index < 0 ? OMEGA : ORDINALS[index];
    }
    return ordinalAt(value, (row, key) ->
      ((Comparable<Object>) Array.get(
          argv(col.intValue()),
          ((OrdinalInt) row).ordinal
        )).compareTo(key)
    );
  }

  @Override
  public <T, K extends Comparable<K>> Keys groupBy(Ordinal col, Function<T, K> accessor) {
    final int amount = amount();
    final T[] source = argv(col.intValue());
    final int count = count(col, accessor);
    final Ordinal[] indices = new Ordinal[count];
    final K[] keys =
      ORDINALS[count].newInstance((Class<K>)accessor.apply(source[0]).getClass());
    argv(-1, indices);
    argv(size(), keys);

    int k = 0;
    K key = null;
    for (int i = 0; i < amount; ++i) {
      K current = accessor.apply(source[rank(i)]);
      if (!current.equals(key)) {
        key = current;
        indices[k] = ORDINALS[i];
        keys[k++] = current;
      }
    }
    return this;
  }

  @Override
  public <T> Keys groupBy(Ordinal col, ToLongFunction<T> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'groupBy'");
  }

  @Override
  public <T, K> Keys thenBy(Ordinal col, Function<T, K> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenBy'");
  }

  @Override
  public <T> Keys thenBy(Ordinal col, ToLongFunction<T> accessor) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'thenBy'");
  }

  @Override
  public <T> Args collect(Ordinal col, ToLongFunction<T> accessor, LongBinaryOperator reducer) {
    argv(size(), target(ofLong(col, accessor), reducer, indices(col)));
    return this;
  }

  @Override
  public Args join(Keys rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'join'");
  }

  protected Matrix() {
    super(0);
  }

  protected Object[] argv() {
    return ARGV;
  }

  protected int mask() {
    return MASK;
  }

  protected int mask(int index) {
    return index & MASK;
  }

  private <T> T argv(int index) {
    return (T) argv()[mask(hashCode() + index)];
  }

  private void argv(int index, Object value) {
    argv()[mask(hashCode() + index)] = value;
  }

  private <T, K extends Comparable<K>> int count(Ordinal col, Function<T, K> accessor) {
    final int amount = amount(); 
    final T[] source = argv(col.intValue());
    orderBy(col, accessor);
    int count = 0;
    K key = null;
    for (int i = 0; i < amount; ++i) {
      K current = accessor.apply(source[rank(i)]);
      if (!current.equals(key)) {
        key = current;
        ++count;
      }
    }
    return count;
  }

  private Ordinal[] indices(Ordinal col) {
    int index = 0;
    if (argv(--index) == null) {
      return ALL;
    } else {
      while (argv(--index) != null);
      return argv(++index);
    }
  } 

  private <T> Spliterator.OfLong ofLong(Ordinal col, ToLongFunction<T> accessor) {
    final Stream<T> stream = stream(col);
    return stream.mapToLong(accessor).spliterator();
  }

  private Object target(Spliterator.OfLong spliterator, LongBinaryOperator reducer, Ordinal... indices) {
    final int length = indices.length;
    long[] target = new long[length];
    
    int j = 0;
    for (int i = 0; i < length; ++i) {
      while (j++ < indices[i].intValue()) {
        spliterator.tryAdvance(consumer(target, i, reducer));
      }
    }
    return target;
  }

  private LongConsumer consumer(final long[] target, final int index, final LongBinaryOperator reducer) {
    return value -> {
      target[index] = reducer.applyAsLong(target[index], value);
    };
  }
}
