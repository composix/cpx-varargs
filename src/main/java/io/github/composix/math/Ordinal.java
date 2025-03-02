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

import java.util.ListIterator;
import java.util.function.Consumer;

public interface Ordinal extends ArgsOrdinal, ListIterator<Ordinal>, Comparable<Ordinal> {
    static Ordinal of(int index) {
        return null;
    }

    byte byteValue();
    
    short shortValue();

    int intValue();

    long longValue();

    int index(Ordinal row);

    boolean isOrdinal();

    boolean contains(Ordinal ordinal);

    void forEach(Consumer<? super Ordinal> consumer);

    // methods for array manipulation
    <T> T getValue(Object[] array, int offset, int index, Ordinal... ordinals);

    long getLongValue(Object[] array, int offset, int index, Ordinal... ordinals);

    <T> T[] newInstance(final Class<T> type);

    <T> T[] copyOf(T[] array);

    Object copyOf(Object array);
}
