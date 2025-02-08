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
import java.util.NoSuchElementException;
import java.util.function.Consumer;

abstract class OrdinalNumber extends Number implements Ordinal {
    static Ordinal[] ORDINALS = Constants.getInstance().ordinals;
    private static Ordinal OMEGA = Constants.getInstance().omega();

    private static final NoSuchElementException NO_SUCH_ELEMENT_EXCEPTION = new NoSuchElementException("Ordinal 0 has no predecessor");

    // inherited from Object
    @Override
    public Order clone() throws CloneNotSupportedException {
        try {
            return (Order) super.clone();
        } catch(Throwable e) {
            throw new CloneNotSupportedException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    // inherited from Number
    @Override
    public long longValue() {
        return (long) intValue();
    }

    @Override
    public float floatValue() {
        return Float.intBitsToFloat(intValue());
    }

    @Override
    public double doubleValue() {
        return Double.longBitsToDouble(longValue());
    }

    //inherited from ListIterator<Ordinal>
    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Ordinal next() {
        return ORDINALS[nextIndex()];
    }

    @Override
    public boolean hasPrevious() {
        return intValue() > 0;
    }

    @Override
    public Ordinal previous() {
        if (intValue() > 0) {
            return ORDINALS[previousIndex()];
        }
        throw NO_SUCH_ELEMENT_EXCEPTION;
    }

    @Override
    public int nextIndex() {
        return intValue() + 1;
    }

    @Override
    public int previousIndex() {
        return intValue() - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Ordinal e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Ordinal e) {
        throw new UnsupportedOperationException();
    }

    // inherited from Comparable<Ordinal>
    @Override
    public int compareTo(Ordinal other) {
        return intValue() - other.intValue();
    }

    // general methods on Ordinal
    @Override
    public boolean contains(Ordinal ordinal) {
        return ordinal.intValue() < intValue();
    }

    @Override
    public boolean isOrdinal() {
        return this == ORDINALS[intValue()];
    }
    
    @Override
    public final int index(Ordinal row) {
        return OMEGA.intValue() * intValue() + row.intValue();
    }

    @Override
    public void forEach(Consumer<? super Ordinal> consumer) {
        final int length = intValue();
        for (int i = 0; i < length; ++i) {
            consumer.accept(ORDINALS[i]);
        }
    }

    @Override
    public final Object newInstance(final Class<?> type) {
        final int length = intValue();
        final Class<?> componentType = type.getComponentType();
        if (componentType != null) {
            Class<?> leafType = componentType.getComponentType();
            if (leafType == null) {
                leafType = componentType;
            } else {
                Class<?> typeB = leafType.getComponentType();
                while(typeB != null) {
                    leafType = typeB.getComponentType();
                    if (leafType == null) {
                        leafType = typeB;
                        break;
                    }
                    typeB = leafType.getComponentType();
                }    
            }
            if (leafType.isPrimitive()) {
                return Array.newInstance(componentType, length);
            }    
        }
        return Array.newInstance(type, length);
    }

    @Override
    public final Object[] copyOf(Object[] array) {
        return Arrays.copyOf(array, intValue());
    }

    @Override
    public final Object copyOf(Object array) {
        Object result = Array.newInstance(array.getClass().getComponentType(), intValue());
        System.arraycopy(array, 0, result, 0, Array.getLength(array));
        return result;
    }
}
