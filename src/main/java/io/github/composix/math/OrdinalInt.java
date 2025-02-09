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
import java.util.stream.LongStream;
import java.util.stream.Stream;

class OrdinalInt extends OrdinalNumber implements Order {
    int ordinal;

    OrdinalInt(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public int intValue() {
        return ordinal;
    }

    @Override
    public Ordinal iterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterator'");
    }

    @Override
    public Ordinal ordinal() {
        return ORDINALS[ordinal];       
    }

    @Override
    public int rank(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rank'");
    }

    @Override
    public Ordinal rank(Ordinal index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rank'");
    }

    @Override
    public <T> T getValue(Object[] array, int index, Ordinal... ordinals) {
        if (ordinals == ORDINALS) {
            return (T) Array.get(array[index / ordinal], index % ordinal);
        }
        return (T) Array.get(array[ordinals[index / ordinal].intValue()], index % ordinal);
    }

    @Override
    public long getLongValue(Object[] array, int index, Ordinal... ordinals) {
        // TODO: avoid boxing
        return ((Long) getValue(array, index, ordinals)).longValue();
    }

    @Override
    public <T> Stream<T> stream(T[] array) {
        if (isOrdinal()) {
            return Stream.of(array);
        }
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public LongStream stream(long[] array) {
        if (isOrdinal()) {
            return LongStream.of(array);
        }
        throw new UnsupportedOperationException("not yet implemented");
    }
}
