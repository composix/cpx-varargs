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
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface Args extends Cloneable, ArgsOrdinal, Order {
    static Args EMPTY = new Matrix<>();
    
    static Args of(Object... array) {
        if (array[0].getClass().isArray()) {
            return OMEGA.extend(A, array);            
        }
        Matrix.OBJECT[0] = array;
        return OMEGA.extend(A, Matrix.OBJECT);
    }

    static Args ofLongs(long... array) {
        return OMEGA.extend(A, array);
    }

    Args clone();

    <T> T getValue(int index);

    long getLongValue(int index);
    
    <T> Stream<T> stream(Ordinal ordinal);

    LongStream longStream(Ordinal ordinal);

    Args select(Order order);

    Args orderBy(Ordinal ordinal);
    
    Ordinal ordinalAt(Ordinal ordinal, Object value);

    default Comparator<Ordinal> comparator(Ordinal ordinal) {
        return Comparator.comparing(Fn.of(ordinal::index).intAndThen(this::getValue));
    }
}
