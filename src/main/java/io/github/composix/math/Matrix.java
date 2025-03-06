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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.ToLongBiFunction;
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'comparator'"
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
  public <T, K extends Comparable<K>> Keys groupBy(Ordinal col, Function<T, K> accessor) {
    orderBy(col, accessor);
    ACCESS_OBJECT.accessor(accessor);
    groupBy(col, ACCESS_OBJECT);
    ACCESS_OBJECT.destroy();
    return this;
  }
 
  @Override
  public <T> Keys groupBy(Ordinal col, ToLongFunction<T> accessor) {
    ACCESS_LONG.accessor(accessor);
    groupBy(col, ACCESS_LONG);
    ACCESS_LONG.destroy();
    return this;
  }

  @Override
  public <T, K> Keys keys(Ordinal col, Function<T, K> accessor) {
    final Ordinal[] indices = indices();
    final int count = indices.length;
    final T[] source = argv(col.intValue());
    final K[] keys =
      ORDINALS[count].newInstance((Class<K>)accessor.apply(source[0]).getClass());
    int index = size();
    while(argv(index) != null) {
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
    int index = size();
    while(argv(index) != null) {
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

  private void groupBy(Ordinal col, Accessor accessor) {
    final int amount = amount();
    final Object[] source = argv(col.intValue());
    final int count = count(amount, source, accessor);
    final Ordinal[] indices = new Ordinal[count];
    argv(-1, indices);
    int k = 0;
    accessor.setValueAt(rank(0), source);
    for (int i = 1; i < amount; ++i) {
      if (accessor.compareAt(rank(i), source) < 0) {
        indices[k++] = ORDINALS[i];
      }
    }
    indices[k] = ORDINALS[amount];
  }

  private int count(final int amount, Object container, Accessor accessor) {
    int count = 0;
    accessor.setValueAt(rank(0), container);
    for (int i = 1; i < amount; ++i) {
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

  private Ordinal[] indices() {
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
      --j;
    }
    return target;
  }

  private LongConsumer consumer(final long[] target, final int index, final LongBinaryOperator reducer) {
    return value -> {
      target[index] = reducer.applyAsLong(target[index], value);
    };
  }

  private static final Accessor.OfObject ACCESS_OBJECT = new Accessor.OfObject() {
    private BiFunction<Ordinal,Object,Object> accessor;
    private Comparable<Object> current;

    @Override
    public void setValueAt(int index, Object container) {
      current = (Comparable<Object>) accessor.apply(ORDINALS[index], container);
    }

    @Override
    public int compareAt(int index, Object container) {
      return current.compareTo(current = (Comparable<Object>) accessor.apply(ORDINALS[index], container));
    }

    @Override
    public void destroy() {
      accessor = null;
      current = null;
    }

    @Override
    public <T, K extends Comparable<K>> void accessor(Function<T, K> accessor) {
      this.accessor = (index, container) -> accessor.apply(((T[]) container)[index.intValue()]);
    }
  };
  private static final Accessor.OfLong ACCESS_LONG = new Accessor.OfLong() {
    private ToLongBiFunction<Ordinal,Object> accessor;
    private long current;

    @Override
    public <T> void accessor(ToLongFunction<T> accessor) {
      this.accessor = (index, container) -> accessor.applyAsLong(((T[]) container)[index.intValue()]);
    }

    @Override
    public void setValueAt(int index, Object container) {
      current = accessor.applyAsLong(ORDINALS[index], container);
    }

    @Override
    public int compareAt(int index, Object container) {
      return Long.compare(current, current = accessor.applyAsLong(ORDINALS[index], container));
    }

    @Override
    public void destroy() {
      accessor = null;
    }
  };
}
