/**
 * class Matrix
 *
 * This class provides the base implementation of the Args and Keys interfaces
 * for manipulating tabular data.
 *
 * Author: dr. ir. J. M. Valk
 * Date: April 2025
 */

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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.github.composix.models.Defaults;

public class Matrix extends OrderInt implements Keys, Args {

  private static final byte TPOS_DTO = -1;
  private static final byte[] LENGTHS = new byte[16];
  private static final Cursor CURSOR = Cursor.ofRow(LENGTHS);

  byte length, source, target, tpos;
  ArgsLongSet pk, fk;

  protected Matrix(int ordinal) {
    super(ordinal);
    length = 0;
  }

  protected VarArgs varArgs() {
    return VarArgs.VARARGS;
  }

  // from ArgsOrdinal

  @Override
  public Ordinal ordinal() {
    return this;
  }

  @Override
  public Args clone() throws CloneNotSupportedException {
    final Matrix result = (Matrix) super.clone();
    result.ordinal %= OMEGA.intValue();
    result.length = 0;
    return result;
  }

  @Override
  public void export(Args target, byte position, int size) {
    final VarArgs varargs = varArgs();
    final int mask = varargs.mask();
    int offset = offset() & mask;
    size += offset;
    if (offset < (size & mask)) {
      do {
        final Column<?> column = varargs.columns[offset];
        if (column == null) {
          target.extend(0, OMEGA.amount(ordinal), varargs.argv[offset]);
        } else {
          target.extend(column);
        }
      } while (++offset < size);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public MutableOrder order() {
    return this;
  }

  @Override
  public Class<?> typeOf(Ordinal col) {
    return typeOf(argv(col.intValue()));
  }

  private Class<?> typeOf(Object value) {
    return value == null ? Void.class : value.getClass().getComponentType();
  }

  public Args extend(Column<?> column) {
    //if (!isOrdinal()) {
    //  throw new IllegalStateException("extend not allowed after reordering");
    //}
    final VarArgs varargs = varArgs();
    final int offset = offset() & varargs.mask();
    final Index positions = varargs.positions;
    int tpos = column.getType().intValue() - SIZE, type = 1, pos = 0, i;
    if (tpos < 0) {
      tpos = TPOS_DTO & MASK;
    }
    for (i = 0; i < length; ++i) {
      type = positions.getInt(offset + i);
      if ((type & MASK) == tpos) {
        type >>>= SHIFT;
        pos = type >>> SHIFT2;
        pos += type++ & MASK2;
        break;
      }
    }
    if (i == length) {
      ++length;
      type = 1;
    }
    Object[] argv = varargs.argv;
    if (argv[pos] == null) {
      pos += offset;
    } else {
      pos = offset;
      type &= MASK2;
      int j;
      do {
        while(argv[pos++] != null);
          for (j = 1; j < type; ++j) {
            if (argv[pos++] != null) {
              break;
            }
          }
      } while (j < type);
      pos -= type;
      type |= (pos - offset) << SHIFT2;
    }
    type <<= SHIFT;
    tpos |= type;
    positions.setInt(offset + i, tpos);
    argv[pos] = column.source();
    varargs.columns[pos] = column;
    ordinal += OMEGA.intValue();
    column.attachOrder(this);
    return this;
  }

  @Override
  public Args extend(final int index, final int amount, final Object array) {
    final int omega = OMEGA.intValue();
    if (!isOrdinal()) {
      throw new IllegalStateException("extend not allowed after reordering");
    }
    final VarArgs varargs = varArgs();
    final int mask = varargs.mask();
    final Object[] argv = varargs.argv;
    int target = offset() + this.target;
    Class<?> type;
    if (this.target > 0) {
      type = argv[--target & mask].getClass();
      if (tpos == index) {
        if (array.getClass() != type) {
          throw new IndexOutOfBoundsException(
            "expected type position " + (index + 1) + "; actual=" + index
          );
        }
      } else if (tpos + 1 == index) {
        if (type == (type = array.getClass())) {
          throw new IndexOutOfBoundsException(
            "expected type position " + tpos + "; actual=" + index
          );
        }
      } else {
        throw new IndexOutOfBoundsException(
          "expected type position " + tpos + "; actual=" + index
        );
      }
      argv[++target & mask] = array;
    } else {
      if (index != 0) {
        throw new IndexOutOfBoundsException(
          "expected type position 0; actual=" + index
        );
      }
      type = array.getClass();
      argv[target & mask] = array;
    }
    ordinal = ordinal == 0
      ? omega + amount
      : omega * (index + 1) + Math.min(ordinal % omega, amount);
    this.target = (byte) (index + 1);
    return this;
  }

  // from Args interface

  @Override
  public <T> Column<T> column(Ordinal tpos) {
    return column(tpos, 1);
  }

  @Override
  public <T> Column<T> column(Ordinal tpos, int pos) {
    final VarArgs varargs = varArgs();
    final int mask = varargs.mask(), offset = offset() & mask;
    final Index positions = varargs.positions;
    int position = tpos.intValue() - SIZE;
    if (position < 0) {
      position = TPOS_DTO & MASK;
    }
    for (int i = 0; i < length; ++i) {
      int type = positions.getInt((offset + i) & mask);
      if ((type & MASK) == position) {
        type >>>= SHIFT;
        if (pos > (type & MASK2) || --pos < 0) {
          throw new IndexOutOfBoundsException();
        }
        pos += type >>> SHIFT2;
        return (Column<T>) varargs.get((offset + pos) & mask);
      }
    }
    throw new IndexOutOfBoundsException("position " + pos + " out of bounds for length 0");
  }

  @Override
  public List<CharSequence> column(CharSequence header) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'column'");
  }

  @Override
  public <T> List<T> column(CharSequence header, Class<T> type)
    throws NoSuchFieldException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'column'");
  }

  @Override
  public LongStream longStream(int pos) {
    final VarArgs varargs = varArgs();
    return stream((long[]) varargs.argv[(hashCode() + pos) & varargs.mask()]);
  }

  @Override
  public LongStream longStream(CharSequence header) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'longStream'"
    );
  }

  @Override
  public <T> List<T> column(CharSequence header, Ordinal ordinal)
    throws NoSuchFieldException {
    int position = ordinal.intValue();
    T[] array = argv(position);
    Class<?> type = array.getClass();
    do {
      if (array[0].toString().equals(header)) {
        return Arrays.asList(array).subList(1, array.length);
      }
      array = argv(++position);
    } while (array != null && array.getClass().equals(type));
    throw new NoSuchFieldException();
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
  public <T> Stream<T> stream(Class<T> type, int pos)
    throws NoSuchFieldException {
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
  public Args split(Pattern pattern) {
    return split(pattern::splitAsStream);
  }

  @Override
  public <T extends Defaults<T>> Args combine(
    final T defaults,
    int pos,
    final int repeat
  ) {
    final int omega = OMEGA.intValue();
    final int index = omega * --pos;
    final int amount = amount();
    final int offset = offset();
    int size = ordinal / omega;
    if (pos < 0 || pos >= size) {
      throw new IndexOutOfBoundsException();
    }
    if (repeat != 1) {
      throw new UnsupportedOperationException();
    }
    final T[] result = (T[]) ORDINALS[amount].newInstance(defaults.getClass());
    final VarArgs varargs = varArgs();
    CURSOR.position(index, ordinal, offset & varargs.mask(), varargs);
    for (int j = 0; j < amount; ++j) {
      if (!CURSOR.advance(1)) {
        throw new AssertionError();
      }
      result[j] = defaults.combine(CURSOR);
    }
    return extend(A.all(result));
  }

  @Override
  public Args parse(Class<?> type, int pos, final int repeat) {
    final int omega = OMEGA.intValue();
    final int amount = ordinal % omega;
    final int skip = Math.max(0, amount - ordinals.length);
    int size = ordinal / omega;
    if (pos < 1 || pos > size) {
      throw new IndexOutOfBoundsException();
    }
    if (repeat < 1 || size < repeat) {
      throw new IndexOutOfBoundsException("repeat must be from 1 to size");
    }
    final VarArgs varargs = varArgs();
    final Object[] argv = varargs.argv;
    final int mask = varargs.mask();
    int offset = (offset() + --pos) & mask;
    for (int i = 0; i < repeat; ++i) {
      final CharSequence[] source = (CharSequence[]) argv[offset++ & mask];
      Object result;
      final int index = (offset() + size++) & mask;
      if (type == long.class) {
        long[] target = new long[amount];
        for (int j = skip; j < amount; ++j) {
          target[j] = Long.parseLong(source[rank(j - skip)].toString());
        }
        //varargs.set(index, groupBy(target));
        result = target;
      } else if (type == String.class) {
        String[] target = new String[amount];
        System.arraycopy(source, 0, target, 0, amount);
        result = target;
      } else {
        throw new UnsupportedOperationException();
      }
      if (!varargs.declare(index, result)) {
        throw new AssertionError();
      }
      ordinal += omega;
    }
    return this;
  }

  @Override
  public <T extends Defaults<T>, K extends Comparable<K>> Keys groupBy(
    Ordinal tpos,
    Function<T, K> accessor
  ) {
    if (argv(-1) != null) {
      throw new ConcurrentModificationException("grouping already in progress");
    }
    final ArgsIndexList<T> result = _groupBy(tpos, accessor);
    argv(-2, result);
    return this;
  }

  @Override
  public <T extends Defaults<T>> Keys groupBy(
    Ordinal tpos,
    ToLongFunction<T> accessor
  ) {
    if (argv(-1) != null) {
      throw new ConcurrentModificationException("grouping already in progress");
    }
    final ArgsIndexList<Long> result = _groupBy(tpos, accessor);
    argv(-2, result);
    return this;
  }

  @Override
  public <T extends Defaults<T>> Args primaryKey(
    Ordinal tpos,
    ToLongFunction<T> accessor
  ) {
    pk = (ArgsLongSet) _groupBy(tpos, accessor).elements;
    pk.indices = argv(-1);
    if (pk.array.length != pk.indices.size()) {
      throw new IllegalArgumentException("column has duplicates");
    }
    return this;
  }

  @Override
  public <T extends Defaults<T>> Args foreignKey(
    Ordinal tpos,
    ToLongFunction<T> accessor
  ) {
    fk = (ArgsLongSet) _groupBy(tpos, accessor).elements;
    fk.indices = argv(-1);
    return this;
  }

  @Override
  public Args pk(CharSequence name, Ordinal type) throws NoSuchFieldException {
    pk = (ArgsLongSet) attribute(name, type);
    if (pk.array.length != pk.indices.size()) {
      throw new IllegalArgumentException("column has duplicates");
    }
    return this;
  }

  @Override
  public Args fk(CharSequence name, Ordinal type) throws NoSuchFieldException {
    fk = (ArgsLongSet) attribute(name, type);
    return this;
  }

  @Override
  public Args attr(CharSequence name, Ordinal type)
    throws NoSuchFieldException {
    attribute(name, type);
    return this;
  }

  private ArgsSet<?> attribute(CharSequence name, Ordinal type)
    throws NoSuchFieldException {
    if (ordinals == ORDINALS) {
      throw new NoSuchFieldException(name.toString());
    }
    final VarArgs varargs = varArgs();
    final int mask = varargs.mask(), offset = offset() & mask;
    final CharSequence[] attr = (CharSequence[]) varargs.argv[(offset +
        varargs.position(offset, mask, name)) &
      mask];
    int length = attr.length;
    switch (type.intValue()) {
      case 11:
        long[] longs = new long[length];
        for (int i = 1; i < length; ++i) {
          longs[i] = Long.parseLong(attr[i].toString());
        }
        if (--length != ordinals.length) {
          throw new AssertionError();
        }
        varargs.argv[(offset + target++) & mask] = longs;
        return groupBy(longs);
      case 18:
        String[] strings = new String[length];
        for (int i = 1; i < length; ++i) {
          strings[i] = (String) attr[i];
        }
        if (--length != ordinals.length) {
          throw new AssertionError();
        }
        varargs.argv[(offset + target++) & mask] = strings;
        return groupBy(type, strings);
      default:
        throw new UnsupportedOperationException();
    }
  }

  protected <T, K extends Comparable<K>> ArgsIndexList<T> _groupBy(
    Ordinal tpos,
    Function<T, K> accessor
  ) {
    final Accessor.OfObject accessObject = Accessor.OfObject.INSTANCE;
    reorder(comparator(tpos, accessor));
    accessObject.accessor(accessor);
    final Object[] source = argv(tpos.intValue());
    final Index indices = groupBy(accessObject, source);
    final T[] target = (T[]) keys(accessObject, source, indices);
    final ArgsIndexList<T> result = new ArgsIndexList<>(
      tposOfType(target.getClass().getComponentType()),
      target
    );
    accessObject.destroy();
    return result;
  }

  private byte tposOfType(Class<?> type) {
    if (Defaults.class.isAssignableFrom(type)) {
      return TPOS_DTO;
    }
    if (type == String.class) {
      S.byteValue();
    }
    throw new UnsupportedOperationException();
  }

  protected <T> ArgsIndexList<Long> _groupBy(Ordinal tpos, ToLongFunction<T> accessor) {
    final VarArgs varargs = varArgs();
    final int mask = varargs.mask(), offset = offset() & mask;
    final Accessor.OfLong accessLong = Accessor.OfLong.INSTANCE;
    this.source = varargs.position(offset, mask, tpos.intValue(), (byte) 0);
    final Object[] source = (Object[]) varargs.argv[(offset + this.source) &
      mask];
    final Comparator<Object> comparator = (Comparator<
        Object
      >) Comparator.comparingLong(accessor);
    reorder((lhs, rhs) ->
      comparator.compare(source[lhs.intValue()], source[rhs.intValue()])
    );
    accessLong.accessor(accessor);
    final Index indices = groupBy(accessLong, source);
    final ArgsIndexList<Long> result = new ArgsIndexList<>(
      AL.byteValue(),
      (long[]) keys(accessLong, source, indices)
    );
    accessLong.destroy();
    return result;
  }

  private <T> ArgsObjSet<T> groupBy(Ordinal type, final T[] source) {
    Accessor accessor = Accessor.of(typeOf(source));
    reorder((lhs, rhs) ->
      ((Comparable<T>) source[lhs.intValue()]).compareTo(source[rhs.intValue()])
    );
    Index indices = groupBy(accessor, source);
    ArgsObjSet<T> result = new ArgsObjSet<>(
      type.byteValue(),
      indices,
      (T[]) keys(accessor, source, indices)
    );
    accessor.destroy();
    return result;
  }

  private ArgsLongSet groupBy(final long[] source) {
    Accessor accessor = Accessor.of(typeOf(source));
    reorder((lhs, rhs) ->
      Long.compare(source[lhs.intValue()], source[rhs.intValue()])
    );
    final Index indices = groupBy(accessor, source);
    ArgsLongSet result = new ArgsLongSet(
      AL.byteValue(),
      indices,
      (long[]) keys(accessor, source, indices)
    );
    accessor.destroy();
    return result;
  }

  private Index groupBy(Accessor accessor, final Object source) {
    final int amount = amount();
    final int count = count(amount, source, accessor);
    final Index indices = Index.of(count, amount);
    argv(-1, indices);
    int k = 0;
    accessor.setValueAt(rank(0), source);
    for (int i = 1; i < amount; ++i) {
      if (accessor.compareAt(rank(i), source) < 0) {
        indices.setInt(k++, i);
      }
    }
    indices.setInt(k, amount);
    return indices;
  }

  private Object keys(
    Accessor accessor,
    final Object source,
    final Index indices
  ) {
    final int count = indices.size();
    accessor.setValueAt(rank(0), source);
    final Object keys = accessor.alloc(ORDINALS[count]);
    accessor.assign(0, keys);
    for (int i = 0; ++i < count; ++i) {
      accessor.setValueAt(rank(indices.getInt(--i)), source);
      accessor.assign(i + 1, keys);
    }
    return keys;
  }

  @Override
  public void clear() {
    ordinal = 0;
    ordinals = ORDINALS;
    pk = null;
    fk = null;
    source = 0;
    target = 0;
  }

  // deprecated

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
    final int omega = OMEGA.intValue();
    final VarArgs varargs = varArgs();
    final Column<T> result = (Column<T>) varargs.get((offset() & varargs.mask()) + (index / omega));
    if (result == null) {
      return OMEGA.getValue(varargs.argv, offset() & varargs.mask(), index, ordinals);
    }
    return result.get(index % omega);
  }

  @Override
  public long getLongValue(int index) {
    final int omega = OMEGA.intValue();
    final VarArgs varargs = varArgs();
    final Column<Long> result = (Column<Long>) varargs.get((offset() & varargs.mask()) + (index / omega));
    if (result == null) {
      return OMEGA.getLongValue(varargs.argv, offset() & varargs.mask(), index, ordinals);
    }
    return result.getLong(index % omega);
  }

  @Override
  public Comparator<Ordinal> comparator(Ordinal col) {
    switch (argv(col.intValue())) {
      case long[] longs:
        return (lhs, rhs) ->
          Long.compare(longs[lhs.intValue()], longs[rhs.intValue()]);
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

  // from Keys interface

  @Override
  public void thenBy(Ordinal col, Accessor accessor) {
    final Object[] source = argv(col.intValue());
    final Index indices = indices();
    final int count = count(indices(), source, accessor);
    final int length = indices.size();
    final Ordinal[] subindices = new Ordinal[count];
    int k = 0;
    while (argv(--k) != null);
    argv(k, subindices);
    k = 0;
    int fromIndex = 0;
    for (int i = 0; i < length; ++i) {
      final int toIndex = indices.getInt(i);
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
    int index = -1;
    final Index indices = argv(index--);
    while (argv(index) != null) {
      --index;
    }
    argv(index, new ArgsIndexList<>(AL.byteValue(), (long[]) target(ofLong(col, accessor), reducer, indices)));
    return this;
  }

  @Override
  public Args joinOne(Args rhs) {
    final Matrix matrix = (Matrix) rhs;
    if (matrix.pk == null) {
      throw new IllegalArgumentException(
        "missing primary key on right-hand side"
      );
    }
    if (fk == null) {
      throw new IllegalArgumentException(
        "missing foreign key on left-hand side"
      );
    }
    final VarArgs lhsArgs = varArgs(), rhsArgs = matrix.varArgs();
    final int mask = lhsArgs.mask();
    if (rhsArgs.mask() != mask) {
      throw new AssertionError();
    }
    final Object[] result = injection(
      (Object[]) rhsArgs.argv[(matrix.offset() + matrix.source) & mask],
      this,
      matrix,
      fk,
      matrix.pk
    );
    extend(target(lhsArgs, offset()).all(result));
    return this;
  }

  private Ordinal target(VarArgs varargs, int offset) {
    Index positions = varargs.positions;
    for (int i = 0; i < length; ++i) {
      int pos = positions.getInt(i);
      if ((pos & MASK) == TPOS_DTO) {
        return ORDINALS[(pos >> SHIFT) & MASK2];
      }
    }
    return A;
  }

  private boolean cancel() {
    final VarArgs varargs = varArgs();
    final Object[] argv = varargs.argv;
    final int mask = varargs.mask();
    int i = hashCode() & mask;
    if (argv[--i & mask] == null) {
      return false;
    }
    do {
      argv[i & mask] = null;
    } while (argv[--i & mask] != null);
    return true;
  }

  @Override
  public Args joinMany(Args rhs) {
    final Matrix matrix = (Matrix) rhs;
    final Object[] source = matrix.argv(0);
    argv(
      size(),
      surjection(
        source.getClass() == CharSequence[].class ? matrix.argv(1) : source,
        this,
        matrix,
        pk,
        matrix.fk
      )
    );
    ordinal += OMEGA.intValue();
    return this;
  }

  @Override
  public Args $done() {
    final VarArgs varargs = varArgs();
    final Object[] argv = varargs.argv;
    int index = offset(); --index;
    index &= varargs.mask();
    try {
      final Args result = (Matrix) clone();
      ArgsIndexList<?> column;
      byte pos = 0;
      while ((column = (ArgsIndexList<?>) argv[--index]) != null) {
        switch (column.elements) {
          case ArgsObjSet<?> elements:
            elements.tpos = pos++;
            break;        
          default:
            break;
        }
        result.extend(column);
      }
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  protected int offset() {
    return hashCode();
  }

  private Args split(Function<CharSequence, Stream<String>> splitter) {
    if (size() != 1) {
      throw new UnsupportedOperationException("not yet implemented");
    }
    final int amount = amount();
    final Object[] argv = argv();
    int size = 0;
    for (int j = 0; j < amount; ++j) {
      Iterator<String> iterator = splitter
        .apply(((CharSequence[]) argv[0])[j])
        .iterator();
      int i = 0;
      while (iterator.hasNext()) {
        String item = iterator.next();
        if (i > size) {
          ++size;
          argv[i] = new CharSequence[amount];
        }
        ((CharSequence[]) argv[i++])[j] = j == 0 ? item + ":" : item;
      }
    }
    source = target = (byte) ++size;
    skipHeader();
    ordinal = target * OMEGA.intValue() + amount;
    // TODO: operation now has side effects on the original matrix
    return this;
  }

  private static Object[] injection(
    Object[] source,
    Order lhsOrder,
    Order rhsOrder,
    ArgsLongSet lhs,
    ArgsLongSet rhs
  ) {
    final Index indices = lhs.indices();
    final int l = indices.size(), n = rhsOrder.amount();
    int j = 0, k = -1;
    Object[] target = lhsOrder
      .ordinal()
      .newInstance(source.getClass().getComponentType());
    for (int i = 0; i < l; ++i) {
      long value = lhs.getLong(i);
      while (++k < n && rhs.getLong(k) < value);
      final int limit = indices.getInt(i);
      if (k < n && rhs.getLong(k) == value) {
        while (j < limit) {
          target[lhsOrder.rank(j++)] = source[rhsOrder.rank(k)];
        }
      } else {
        while (j < limit) {
          throw new IllegalArgumentException(
            "no primary key found matching to foreign key: " + value
          );
        }
      }
    }
    return target;
  }

  private static Object[] surjection(
    Object[] source,
    Order lhsOrder,
    Order rhsOrder,
    ArgsLongSet lhs,
    ArgsLongSet rhs
  ) {
    final Index indices = lhs.indices, rhsIndices = rhs.indices;
    final int l = indices.size(), n = rhsIndices.size();
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
      long value = lhs.getLong(i);
      while (++m < n && rhs.getLong(m) < value);
      final int limit = indices.getInt(i);
      if (m < n && rhs.getLong(m) == value) {
        final int length = rhsIndices.getInt(m) - k;
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

  private int count(final Index indices, Object container, Accessor accessor) {
    final int length = indices.size();
    int offset = indices.getInt(0);
    order().reorder(accessor.comparator(container), 0, offset);
    int count = count(offset, container, accessor);
    for (int i = 1; i < length; ++i) {
      int toIndex = indices.getInt(i);
      order().reorder(accessor.comparator(container), offset, toIndex);
      accessor.setValueAt(rank(offset), container);
      count += count(++offset, offset = toIndex, container, accessor);
    }
    return count;
  }

  private Index indices() {
    final VarArgs varargs = varArgs();
    final int mask = varargs.mask();
    int offset = offset();
    return ((ArgsSet<?>) varargs.argv[--offset & mask]).indices();
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
    Index indices
  ) {
    final int length = indices.size();
    long[] target = new long[length];

    int j = 0;
    for (int i = 0; i < length; ++i) {
      while (j++ < indices.getInt(i)) {
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

  @Override
  public <T extends Defaults<T>> Args combine(T defaults) {
    return combine(defaults, 1, 1);
  }
}
