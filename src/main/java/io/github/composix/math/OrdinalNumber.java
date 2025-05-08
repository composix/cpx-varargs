/**
 * class OrdinalNumber
 * 
 * This is the base class for all ordinals that implement the Ordinal interface.
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
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

abstract class OrdinalNumber extends Number implements Ordinal {
    static final Ordinal[] ORDINALS = Constants.getInstance().ordinals;

    private static final Constants CONSTANTS = Constants.getInstance();
    private static final NoSuchElementException NO_SUCH_ELEMENT_EXCEPTION = new NoSuchElementException("Ordinal 0 has no predecessor");

    // from ArgsOrdinal

    @Override
    public Args clone() throws CloneNotSupportedException {
        switch(this) {
            case Args args:
                return (Args) super.clone();
            default:
                return new SafeMatrix(intValue());
        }
    }

    @Override
    public String toString() {
        long row = longValue();
        if (OMEGA.contains(this)) {
            return Long.toString(row);
        }
        final long omega = OMEGA.longValue();
        row %= omega;
        final String rowString = row > 0 ? Long.toString(row) : "";
        int length = rowString.length();
        int col = (int) (row / omega), i = 0;
        int offset = col / 26 + 1;
        char[] result = new char[offset + length];
        length = result.length;
        do {
            result[i++] = (char) (col % 26 + 65);
            col /= 26;
        } while (col > 0);
        --offset;
        while (++offset < length) {
            result[offset] = rowString.charAt(offset);
        }
        return new String(result);
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
    public Column<Ordinal> ordinals(OrdinalList<Ordinal> values) {
        byte tpos = CONSTANTS.check(this, values);
        if (tpos < 0) {
            return null;
        }
        return new ArgsIndexList<>(tpos, values);
    }

    @Override
    public void any(boolean... values) {
        CONSTANTS.check(this, values);
    }

    @Override
    public void any(byte... values) {
        CONSTANTS.check(this, values);
    }

    @Override
    public void any(char... values) {
        CONSTANTS.check(this, values);
    }

    @Override
    public void any(short... values) {
        CONSTANTS.check(this, values);
    }

    @Override
    public ArgsIndexList<Integer> any(int... values) {
        byte tpos = CONSTANTS.check(this, values);
        if (tpos < 0) {
            return null;
        }
        return new ArgsIndexList<>(tpos, IntStream.of(values).mapToLong(i -> i).toArray());
    }

    @Override
    public ArgsIndexList<Long> any(long... values) {
        byte tpos = CONSTANTS.check(this, values);
        if (tpos < 0) {
            return null;
        }
        return new ArgsIndexList<>(tpos, values);
    }

    @Override
    public void any(float... values) {
        CONSTANTS.check(this, values);
    }

    @Override
    public void any(double... values) {
        CONSTANTS.check(this, values);
    }

    @Override
    public <R> Column<R> any(Stream<R> values) {
        switch(intValue()) {
            case 0:
                return (Column<R>) all(values.toArray(String[]::new));
            default:
                throw new IllegalArgumentException("Invalid ordinal: " + intValue());
        }
    }

    @Override
    public <T extends Comparable<T>> Column<T> all(T... values) {
        byte tpos = CONSTANTS.check(this, values);
        if (tpos < 0) {
            return null;
        }
        return new ArgsIndexList<>(tpos, values);
    }

    @Override
    public boolean contains(Ordinal ordinal) {
        return ordinal.intValue() < intValue();
    }

    @Override
    public boolean isOrdinal() {
        return true;
    }
    
    @Override
    public final int index(Ordinal row) {
        return OMEGA.intValue() * intValue() + row.intValue();
    }

    @Override
    public final Ordinal column() {
        return CONSTANTS.columns[intValue()];
    }
    
    @Override
    public void forEach(Consumer<? super Ordinal> consumer) {
        final int length = intValue();
        for (int i = 0; i < length; ++i) {
            consumer.accept(ORDINALS[i]);
        }
    }

    @Override
    public final <T> T[] newInstance(final Class<T> type) {
        final int length = intValue() % OMEGA.intValue();
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
                return (T[]) Array.newInstance(componentType, length);
            }    
        }
        return (T[]) Array.newInstance(type, length);
    }

    @Override
    public final <T> T[] copyOf(T[] array) {
        return Arrays.copyOf(array, intValue() % OMEGA.intValue());
    }

    @Override
    public final <T> T copyOf(T array) {
        final int amount = intValue() % OMEGA.intValue();
        switch(array) {
            case Object[] objectArray:
                return (T) Arrays.copyOf(objectArray, amount);
            case int[] intArray:
                return (T) Arrays.copyOf(intArray, amount);
            case long[] longArray:
                return (T) Arrays.copyOf(longArray, amount);
            case short[] shortArray:
                return (T) Arrays.copyOf(shortArray, amount);
            case byte[] byteArray:
                return (T) Arrays.copyOf(byteArray, amount);
            case char[] charArray:
                return (T) Arrays.copyOf(charArray, amount);
            case float[] floatArray:
                return (T) Arrays.copyOf(floatArray, amount);
            case double[] doubleArray:
                return (T) Arrays.copyOf(doubleArray, amount);
            case boolean[] booleanArray:
                return (T) Arrays.copyOf(booleanArray, amount);
            default:
                throw new IllegalArgumentException("array expected");
        }
    }

    @Override
    public final <T> T copyOf(T array, int offset) {
        final int amount = intValue() % OMEGA.intValue();
        switch(array) {
            case Object[] objectArray:
                return (T) Arrays.copyOfRange(objectArray, offset, amount);
            case int[] intArray:
                return (T) Arrays.copyOfRange(intArray, offset, amount);
            case long[] longArray:
                return (T) Arrays.copyOfRange(longArray, offset, amount);
            case short[] shortArray:
                return (T) Arrays.copyOfRange(shortArray, offset, amount);
            case byte[] byteArray:
                return (T) Arrays.copyOfRange(byteArray, offset, amount);
            case char[] charArray:
                return (T) Arrays.copyOfRange(charArray, offset, amount);
            case float[] floatArray:
                return (T) Arrays.copyOfRange(floatArray, offset, amount);
            case double[] doubleArray:
                return (T) Arrays.copyOfRange(doubleArray, offset, amount);
            case boolean[] booleanArray:
                return (T) Arrays.copyOfRange(booleanArray, offset, amount);
            default:
                throw new IllegalArgumentException("array expected");
        }
    }
}
