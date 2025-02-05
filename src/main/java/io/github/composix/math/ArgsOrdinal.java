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

import java.math.BigInteger;

import io.github.composix.varargs.Args;
import io.github.composix.varargs.ArgsI;

public interface ArgsOrdinal {
    static final Ordinal OMEGA = null;
    static final Ordinal A = null;
    static final Ordinal B = null;
    static final Ordinal C = null;

    static Object[] OBJECTS = new Object[0];
    static String[] STRINGS = new String[0];
    static long[] LONGS = new long[0];
    static BigInteger[] INTEGERS = new BigInteger[0];

    Order order();
    
    @Override
    String toString();

    default Args extend(Ordinal col, Object... arrays) {
        return null;
    }

    default <T> ArgsI<T> extendA(T... array) {
        return (ArgsI<T>) extend(A, new Object[] {array});
    }
}
