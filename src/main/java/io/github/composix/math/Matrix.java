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
        .compareTo(accessor.apply(source[rhs.intValue()]));
  }

  @Override
  public <T> Comparator<Ordinal> comparator(
    Ordinal ordinal,
    ToLongFunction<T> accessor
  ) {
    final T[] source = argv(ordinal.intValue());
    return (lhs, rhs) ->
      Long.compare(
        accessor.applyAsLong(source[lhs.intValue()]),
        accessor.applyAsLong(source[rhs.intValue()])
      );
  }

  @Override
  public <T> Stream<T> stream(Ordinal col) {
    if (col.intValue() < size()) {
      return (Stream<T>) stream((Object[]) argv(col.intValue()));
    }
    T[] array = argv(col.intValue());
    if (array == null) {
      throw new IndexOutOfBoundsException();
    }
    return Stream.of(array);
  }

  @Override
  public LongStream longStream(Ordinal col) {
    if (col.intValue() < size()) {
      return stream((long[]) argv(col.intValue()));
    }
    long[] array = argv(col.intValue());
    if (array == null) {
      throw new IndexOutOfBoundsException();
    }
    return LongStream.of(array);
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
  public void groupBy(Ordinal col, Accessor accessor) {
    final int amount = amount();
    final Object[] source = argv(col.intValue());
    final int count = count(amount, source, accessor);
    final Ordinal[] indices = new Ordinal[count];
    int k = 0;
    while (argv(--k) != null) {
      argv(k, null);
    }
    k = size();
    while(argv(k) != null) {
      argv(k++, null);
    }
    argv(-1, indices);
    k = 0;
    accessor.setValueAt(rank(0), source);
    for (int i = 1; i < amount; ++i) {
      if (accessor.compareAt(rank(i), source) < 0) {
        indices[k++] = ORDINALS[i];
      }
    }
    indices[k] = ORDINALS[amount];
  }

  @Override
  public <T, K> Keys keys(Ordinal col, Function<T, K> accessor) {
    final Ordinal[] indices = indices();
    final int count = indices.length;
    final T[] source = argv(col.intValue());
    final K[] keys =
      ORDINALS[count].newInstance(
          (Class<K>) accessor.apply(source[0]).getClass()
        );
    int index = size();
    while (argv(index) != null) {
      ++index;
    }
    argv(index, keys);
    keys[0] = accessor.apply(source[rank(0)]);
    for (int i = 0; ++i < count; ++i) {
      keys[i] = accessor.apply(source[rank(indices[--i]).intValue()]);
    }
    return this;
  }

  @Override
  public void thenBy(Ordinal col, Accessor accessor) {
    final Object[] source = argv(col.intValue());
    final Ordinal[] indices = indices();
    final int count = count(indices(), source, accessor);
    final int length = indices.length;
    final Ordinal[] subindices = new Ordinal[count];
    int k = 0;
    while (argv(--k) != null);
    argv(k, subindices);
    k = 0;
    int fromIndex = 0;
    for (int i = 0; i < length; ++i) {
      final int toIndex = indices[i].intValue();
      accessor.setValueAt(rank(fromIndex++), source);
      for (int j = fromIndex; j < toIndex; ++j) {
        if (accessor.compareAt(rank(j), source) < 0) {
          subindices[k++] = ORDINALS[j];
        }
      }
      subindices[k++] = ORDINALS[toIndex];
      fromIndex = toIndex;
    }
  }

  @Override
  public <T> Args collect(
    Ordinal col,
    ToLongFunction<T> accessor,
    LongBinaryOperator reducer
  ) {
    int index = size();
    while (argv(index) != null) {
      ++index;
    }
    argv(index, target(ofLong(col, accessor), reducer, indices()));
    return this;
  }

  @Override
  public Args join(Keys rhs) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'join'");
  }

  protected Matrix(int ordinal) {
    super(ordinal);
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

  private int count(final int amount, Object container, Accessor accessor) {
    accessor.setValueAt(rank(0), container);
    return count(1, amount, container, accessor);
  }

  private int count(
    final int offset,
    final int amount,
    Object container,
    Accessor accessor
  ) {
    int count = 0;
    for (int i = offset; i < amount; ++i) {
      final int result = accessor.compareAt(rank(i), container);
      if (result != 0) {
        if (result > 0) {
          throw new IllegalStateException("column not sorted");
        }
        ++count;
      }
    }
    return ++count;
  }

  private int count(
    final Ordinal[] indices,
    Object container,
    Accessor accessor
  ) {
    final int length = indices.length;
    int offset = indices[0].intValue();
    order().reorder(accessor.comparator(container), 0, offset);
    int count = count(offset, container, accessor);
    for (int i = 1; i < length; ++i) {
      int toIndex = indices[i].intValue();
      order().reorder(accessor.comparator(container), offset, toIndex);
      accessor.setValueAt(rank(offset), container);
      count += count(
        ++offset,
        offset = toIndex,
        container,
        accessor
      );
    }
    return count;
  }

  private Ordinal[] indices() {
    int index = 0;
    if (argv(--index) == null) {
      return ALL;
    } else {
      while (argv(--index) != null);
      return argv(++index);
    }
  }

  private <T> Spliterator.OfLong ofLong(
    Ordinal col,
    ToLongFunction<T> accessor
  ) {
    final Stream<T> stream = stream(col);
    return stream.mapToLong(accessor).spliterator();
  }

  private Object target(
    Spliterator.OfLong spliterator,
    LongBinaryOperator reducer,
    Ordinal... indices
  ) {
    final int length = indices.length;
    long[] target = new long[length];

    int j = 0;
    for (int i = 0; i < length; ++i) {
      while (j++ < indices[i].intValue()) {
        spliterator.tryAdvance(consumer(target, i, reducer));
      }
      --j;
    }
    return target;
  }

  private LongConsumer consumer(
    final long[] target,
    final int index,
    final LongBinaryOperator reducer
  ) {
    return value -> {
      target[index] = reducer.applyAsLong(target[index], value);
    };
  }

}
