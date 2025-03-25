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

import java.util.List;

public interface Row {
    static long parseLong(CharSequence chars) {
        return Long.parseLong(chars.toString());
    }

    Object get(Ordinal kind, int pos);

    <T> T get(Class<T> type, int pos) throws NoSuchFieldException;

    <T> List<T> getMany(Class<T> type, int pos) throws NoSuchFieldException;

    long getLong(int pos) throws NoSuchFieldException;
    
    default CharSequence get(int pos) throws NoSuchFieldException {
        try {
            return (CharSequence) get(ArgsOrdinal.A, pos);
        } catch(ClassCastException e) {
            throw new NoSuchFieldException();
        }
    }

    default <T> T get(Class<T> type) throws NoSuchFieldException {
        return get(type, 0);
    }

    default <T> List<T> getMany(Class<T> type) throws NoSuchFieldException {
        return getMany(type, 0);
    }

    default <E extends Enum<E>> E getEnum(Class<E> type, int pos) throws NoSuchFieldException {
        return (E) Enum.valueOf(type, get(pos).toString());
    }

    default <E extends Enum<E>> E getENUM(Class<E> type, int pos) throws NoSuchFieldException {
        return (E) Enum.valueOf(type, get(pos).toString().toUpperCase());
    }
}
