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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

public interface Accessor {
    static Accessor of(Class<?> type) {
      if (type.isPrimitive()) {
        return Accessor.OfLong.INSTANCE;
      }
      return Accessor.OfObject.INSTANCE;
    }

    void setValueAt(int index, Object container);

    Object alloc(Ordinal length);

    void assign(int index, Object target);
    
    int compareAt(int index, Object container);

    Comparator<Ordinal> comparator(Object container);
    
    void destroy();

    public interface OfObject extends Accessor {
        static OfObject INSTANCE = new Accessor.OfObject() {
            private static final BiFunction<Ordinal,Object,Object> ACCESSOR = (index, container) -> ((Object[]) container)[index.intValue()];

            private BiFunction<Ordinal, Object, Object> accessor = ACCESSOR;
            private Comparable<Object> current;
      
            @Override
            public void setValueAt(int index, Object container) {
              current = (Comparable<Object>) accessor.apply(
                OrdinalNumber.ORDINALS[index],
                container
              );
            }
      
            @Override
            public Object alloc(Ordinal length) {
              return length.newInstance(current.getClass());
            }

            public void assign(int index, Object target) {
              ((Object[]) target)[index] = current;
            }

            @Override
            public int compareAt(int index, Object container) {
              return current.compareTo(
                current = (Comparable<Object>) accessor.apply(
                  OrdinalNumber.ORDINALS[index],
                  container
                )
              );
            }
      
            @Override
            public Comparator<Ordinal> comparator(final Object container) {
              return (lhs, rhs) ->
                ((Comparable<Object>) accessor.apply(lhs, container)).compareTo(
                    accessor.apply(rhs, container)
                  );
            }
      
            @Override
            public void destroy() {
              accessor = ACCESSOR;
              current = null;
            }
      
            @Override
            public <T, K extends Comparable<K>> void accessor(
              Function<T, K> accessor
            ) {
              this.accessor = (index, container) ->
                accessor.apply(((T[]) container)[index.intValue()]);
            }
          };

        <T,K extends Comparable<K>> void accessor(Function<T,K> accessor);
    }

    public interface OfLong extends Accessor {
        static OfLong INSTANCE = new Accessor.OfLong() {
            private static final ToLongBiFunction<Ordinal,Object> ACCESSOR = (index, container) -> ((long[]) container)[index.intValue()];

            private ToLongBiFunction<Ordinal, Object> accessor = ACCESSOR;
            private long current;
        
            @Override
            public <T> void accessor(ToLongFunction<T> accessor) {
              this.accessor = (index, container) ->
                accessor.applyAsLong(((T[]) container)[index.intValue()]);
            }
        
            @Override
            public void setValueAt(int index, Object container) {
              current = accessor.applyAsLong(OrdinalNumber.ORDINALS[index], container);
            }
        
            @Override
            public Object alloc(Ordinal length) {
              return new long[length.intValue()];
            }

            @Override
            public void assign(int index, Object target) {
              ((long[]) target)[index] = current;
            }

            @Override
            public int compareAt(int index, Object container) {
              return Long.compare(
                current,
                current = accessor.applyAsLong(OrdinalNumber.ORDINALS[index], container)
              );
            }
        
            @Override
            public Comparator<Ordinal> comparator(final Object container) {
              return (lhs, rhs) ->
                Long.compare(
                  accessor.applyAsLong(lhs, container),
                  accessor.applyAsLong(rhs, container)
                );
            }
        
            @Override
            public void destroy() {
              accessor = ACCESSOR;
            }
          };

        <T> void accessor(ToLongFunction<T> accessor);
    }
}
