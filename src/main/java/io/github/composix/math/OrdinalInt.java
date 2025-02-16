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
        final int limit = ordinal;
        return new OrdinalInt(-1) {
            @Override
            public boolean hasNext() {
                return ordinal < limit;
            }

            @Override
            public int nextIndex() {
                return ++ordinal;
            }
        
            @Override
            public int previousIndex() {
                return --ordinal;
            }                    
        };
    }

    @Override
    public Ordinal ordinal() {
        return ORDINALS[ordinal];       
    }

    @Override
    public int rank(int index) {
        return index;
    }

    @Override
    public Ordinal rank(Ordinal index) {
        return index;
    }

    @Override
    public <T> T getValue(Object[] array, int offset, int index, Ordinal... ordinals) {
        if (ordinals == ORDINALS) {
            return (T) Array.get(array[offset + index / ordinal], index % ordinal);
        }
        return (T) Array.get(array[offset + ordinals[index / ordinal].intValue()], index % ordinal);
    }

    @Override
    public long getLongValue(Object[] array, int offset, int index, Ordinal... ordinals) {
        // TODO: avoid boxing
        return ((Long) getValue(array, offset, index, ordinals)).longValue();
    }

    @Override
    public void permute(int target, int mask, Object[] array) {
        return; // standard order, so nothing to permute
    }

    @Override
    public <T> Stream<T> stream(T[] array) {
        return Stream.of(array);
    }

    @Override
    public LongStream stream(long[] array) {
        return LongStream.of(array);
    }
}
