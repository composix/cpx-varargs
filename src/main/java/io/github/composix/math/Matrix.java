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

import io.github.composix.models.Defaults;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Matrix extends OrderInt implements Keys, Args {

  private static final VarArgs VARARGS = VarArgs.VARARGS;
  private static final int MASK = VARARGS.mask();
  private static final Ordinal[] ALL = new Ordinal[] { A };
  private static final byte[] LENGTHS = new byte[16];
  private static final Cursor CURSOR = Cursor.ofRow(LENGTHS);

  protected Matrix(int ordinal) {
    super(ordinal);
  }

  protected VarArgs varArgs() {
    return VARARGS;
  }

  @Override
  public Ordinal ordinal() {
    return this;
  }

  @Override
  public Args clone() throws CloneNotSupportedException {
    return (Args) super.clone();
  }

  @Override
  public void export(Args target, int offset, int size) {
    varArgs().export(hashCode() + offset, size, target.hashCode());
  }

  @Override
  public MutableOrder order() {
    return this;
  }

  @Override
  public Class<?> typeOf(Ordinal col) {
    final Object array = argv(col.intValue());
    return array == null ? Void.class : array.getClass().getComponentType();
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
    return extend(col.intValue(), arrays, true);
  }

  private Args extend(final int index, final Object[] arrays, final boolean safe) {
    final int omega = OMEGA.intValue();
    final int size = ordinal / omega;
    final int length = arrays.length;
    final Object[] argv = argv();
    int target = hashCode() + index;
    for (int i = 0; i <= length; ++i) {
      if (argv[mask(target + i)] != null) {
          if (index + i < size) {
            if (safe) {
              try {
                return ((Matrix) clone()).extend(index, arrays, false);
              } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
              }
            }
          } else {
            throw new IllegalStateException("hash collision");
          }
      }
    }  
    for (int i = 0; i < length; ++i) {
      argv[mask(target++)] = arrays[i];
    }

    int amount = ordinal % omega;
    for (int i = 0; i < length; ++i) {
      amount = Math.max(amount, Array.getLength(arrays[i]));
    }
    ordinal = omega * Math.max(index + length, size);
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
  public Comparator<Ordinal> comparator(Ordinal col) {
    switch (argv(col.intValue())) {
      case long[] longs:
        return (lhs,rhs) -> Long.compare(longs[lhs.intValue()], longs[rhs.intValue()]);
      default:
        return Comparator.comparing(
          Fn.of(col::index).intAndThen(this::getValue)
        );
    }
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
  public <T> Stream<T> stream(Class<T> type, int pos) throws NoSuchFieldException {
    final int size = size() + 1;
    for (int i = 0; i < size; ++i) {
      if (argv(i).getClass() == type.arrayType()) {
        i += pos;
        if (argv(i).getClass() == type.arrayType()) {
          return (Stream<T>) stream(ORDINALS[i + pos]);
        }
        throw new NoSuchFieldException();
      }
    }
    throw new NoSuchFieldException();
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
  public LongStream longStream(int pos) throws NoSuchFieldException {
    final int size = size() + 2;
    for (int i = 0; i < size; ++i) {
      if (argv(i).getClass() == long[].class) {
        i += pos;
        if (argv(i).getClass() == long[].class) {
          return longStream(ORDINALS[i + pos]);
        }
        throw new NoSuchFieldException();
      }
    }
    throw new NoSuchFieldException();
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
  public Args split(Pattern pattern) {
    return split(pattern::splitAsStream);
  }

  @Override
  public <T extends Defaults<T>> Args combine(T defaults)
    throws NoSuchFieldException {
    final int omega = OMEGA.intValue();
    final int amount = (ordinal % omega) - 1;
    final T[] result = (T[]) ORDINALS[amount].newInstance(defaults.getClass());
    CURSOR.position(0, ordinal, hashCode(), varArgs());
    for (int i = 0; i < amount; ++i) {
      if (!CURSOR.advance(1)) {
        throw new AssertionError();
      }
      result[i] = defaults.combine(CURSOR);
    }
    return A.extend(A, result);
  }

  @Override
  public Keys groupBy(Ordinal col, Accessor accessor) {
    final int amount = amount();
    final Object source = argv(col.intValue());
    final int count = count(amount, source, accessor);
    final Ordinal[] indices = new Ordinal[count];
    int k = 0;
    while (argv(--k) != null) {
      argv(k, null);
    }
    k = size();
    while (argv(k) != null) {
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
    return this;
  }

  @Override
  public Keys keys(Ordinal col, Accessor accessor) {
    final Ordinal[] indices = indices();
    final int count = indices.length;
    final Object source = argv(col.intValue());
    accessor.setValueAt(rank(0), source);
    final Object keys = accessor.alloc(ORDINALS[count]);
    argv(index(), keys);
    accessor.assign(0, keys);
    for (int i = 0; ++i < count; ++i) {
      accessor.setValueAt(rank(indices[--i]).intValue(), source);
      accessor.assign(i + 1, keys);
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
    final int size = size();
    int index = size;
    while (argv(index) != null) {
      ++index;
    }
    final Ordinal[] indices = indices();
    argv(index, target(ofLong(col, accessor), reducer, indices));
    return this;
  }

  @Override
  public Args joinOne(Keys rhs) {
    final Matrix matrix = (Matrix) rhs;
    final Ordinal[] indices = matrix.indices();
    if (indices.length == matrix.amount()) {
      int lastIndex = size() - 1;
      argv(
        argv(lastIndex).getClass() == CharSequence[].class
          ? lastIndex
          : ++lastIndex,
        injection(
          indices(),
          matrix.argv(0),
          order(),
          matrix.order(),
          argv(size()),
          matrix.argv(matrix.size())
        )
      );
    } else {
      throw new IllegalArgumentException();
    }
    return this;
  }

  @Override
  public Args joinMany(Keys rhs) {
    final Matrix matrix = (Matrix) rhs;
    final Ordinal[] indices = matrix.indices();
    final Object[] source = matrix.argv(0);
    argv(
      size(),
      surjection(
        indices(),
        source.getClass() == CharSequence[].class ? matrix.argv(1) : source,
        order(),
        matrix.order(),
        argv(size()),
        matrix.argv(matrix.size()),
        indices
      )
    );
    ordinal += OMEGA.intValue();
    return this;
  }

  private Args split(Function<CharSequence, Stream<String>> splitter) {
    if (size() != 1) {
      throw new UnsupportedOperationException("not yet implemented");
    }
    final int amount = amount();
    final Object[] argv = argv();
    int size = 0;
    for (int j = 0; j < amount; ++j) {
      Iterator<String> iterator = splitter.apply(((CharSequence[]) argv[0])[j]).iterator();
      int i = 0;
      while (iterator.hasNext()) {
        String item = iterator.next();
        if (i > size) {
          ++size;
          argv[i] = new CharSequence[amount];
        }
        ((CharSequence[]) argv[i++])[j] = item;
      }
    }
    ordinal = ++size * OMEGA.intValue() + amount;
    // TODO: operation now has side effects on the original matrix
    return this;
  }

  private static Object[] injection(
    final Ordinal[] indices,
    Object[] source,
    Order lhsOrder,
    Order rhsOrder,
    long[] lhs,
    long[] rhs
  ) {
    final int l = indices.length, n = rhsOrder.amount();
    int j = 0, k = -1;
    Object[] target = lhsOrder
      .ordinal()
      .newInstance(source.getClass().getComponentType());
    for (int i = 0; i < l; ++i) {
      long value = lhs[i];
      while (++k < n && rhs[k] < value);
      final int limit = indices[i].intValue();
      if (k < n && rhs[k] == value) {
        while (j < limit) {
          target[lhsOrder.rank(j++)] = source[rhsOrder.rank(k)];
        }
      } else {
        while (j < limit) {
          target[lhsOrder.rank(j++)] = null;
        }
      }
    }
    return target;
  }

  private static Object[] surjection(
    final Ordinal[] indices,
    Object[] source,
    Order lhsOrder,
    Order rhsOrder,
    long[] lhs,
    long[] rhs,
    final Ordinal[] rhsIndices
  ) {
    final int l = indices.length, n = rhsIndices.length;
    int j = 0, k = 0, m = -1;
    Class<?> componentType = source.getClass();
    if (componentType == CharSequence[].class) {
      componentType = String[].class;
    }
    final Object[] target = (Object[]) lhsOrder
      .ordinal()
      .newInstance(componentType);
    componentType = componentType.getComponentType();
    final Object empty = Array.newInstance(componentType, 0);
    for (int i = 0; i < l; ++i) {
      long value = lhs[i];
      while (++m < n && rhs[m] < value);
      final int limit = indices[i].intValue();
      if (m < n && rhs[m] == value) {
        final int length = rhsIndices[m].intValue() - k;
        while (j < limit) {
          Object[] values = (Object[]) Array.newInstance(componentType, length);
          for (int index = 0; index < length; ++index) {
            values[index] = source[rhsOrder.rank(k++)];
          }
          target[lhsOrder.rank(j++)] = values;
        }
      } else {
        --m;
        while (j < limit) {
          target[lhsOrder.rank(j++)] = empty;
        }
      }
    }
    return target;
  }

  private Object[] argv() {
    return varArgs().argv;
  }

  private int mask() {
    return varArgs().mask();
  }

  private int mask(int index) {
    return index & varArgs().mask();
  }

  private <T> T argv(int index) {
    return (T) argv()[mask(hashCode() + index)];
  }

  private void argv(int index, Object value) {
    argv()[mask(hashCode() + index)] = value;
  }

  private int count(final int amount, Object container, Accessor accessor) {
    accessor.setValueAt(rank(0), container);
    return count(0, amount, container, accessor);
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
      count += count(++offset, offset = toIndex, container, accessor);
    }
    return count;
  }

  private int index() {
    int index = size();
    while (argv(index) != null) {
      ++index;
    }
    return index;
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
