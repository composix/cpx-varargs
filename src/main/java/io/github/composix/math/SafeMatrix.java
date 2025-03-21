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
import io.github.composix.varargs.ArgsI;
import io.github.composix.varargs.KeysI;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class SafeMatrix<K extends Comparable<?>, A>
  extends Matrix
  implements KeysI<K, A>, ArgsI<A> {

  static final String[] VALUES = new String[16];
  static final Cursor CURSOR = Cursor.ofRow(VALUES);

  protected SafeMatrix(int ordinal) {
    super(ordinal);
  }

  private static int MASK = 15;

  private Object[] argv = new Object[MASK + 1];

  @Override
  public ArgsI<A> clone() throws CloneNotSupportedException {
    final SafeMatrix result = (SafeMatrix) super.clone();
    result.argv = argv.clone();
    return result;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public <T> ArgsI<T> split(Function<A, Stream<T>> splitter) {
    if (size() != 1) {
      throw new UnsupportedOperationException("not yet implemented");
    }
    final int amount = amount();
    final Object[] argv = argv();
    int size = 0;
    for (int j = 0; j < amount; ++j) {
      Iterator<T> iterator = splitter.apply(((A[]) argv[0])[j]).iterator();
      int i = 0;
      while (iterator.hasNext()) {
        T item = iterator.next();
        if (i > size) {
          ++size;
          argv[i] = newInstance(argv[0].getClass().getComponentType());
        }
        ((T[]) argv[i++])[j] = item;
      }
    }
    ordinal = ++size * OMEGA.intValue() + amount;
    // TODO: operation now has side effects on the original matrix
    return (ArgsI<T>) this;
  }

  @Override
  public <T extends Defaults<T>> ArgsI<T> join(
    Class<T> dto,
    Function<A[], T> joiner
  ) {
    final int omega = OMEGA.intValue();
    final int amount = ordinal % omega;
    final T[] result = newInstance(dto);
    CURSOR.position(B, this);
    CURSOR.cols(ordinal / omega);
    for (int i = 1; i < amount; ++i) {
      if (!CURSOR.advance(B)) {
        throw new AssertionError();
      }
      result[i] = joiner.apply((A[]) VALUES);
    }
    result[0] = Defaults.of(dto);
    return A.extendA(result);
  }

  @Override
  protected Object[] argv() {
    return argv;
  }

  @Override
  protected int mask() {
    return MASK;
  }

  @Override
  protected int mask(int index) {
    return index & MASK;
  }
}
