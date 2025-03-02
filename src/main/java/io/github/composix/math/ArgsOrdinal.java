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

import io.github.composix.varargs.ArgsI;

public interface ArgsOrdinal extends Cloneable {
    static final Ordinal OMEGA = Constants.getInstance().omega();
    static final Ordinal A = Constants.getInstance().ordinal(0),
            B = Constants.getInstance().ordinal(1),
            C = Constants.getInstance().ordinal(2),
            D = Constants.getInstance().ordinal(3),
            E = Constants.getInstance().ordinal(4),
            F = Constants.getInstance().ordinal(5),
            G = Constants.getInstance().ordinal(6),
            H = Constants.getInstance().ordinal(7),
            I = Constants.getInstance().ordinal(8),
            J = Constants.getInstance().ordinal(9),
            K = Constants.getInstance().ordinal(10),
            L = Constants.getInstance().ordinal(11),
            M = Constants.getInstance().ordinal(12),
            N = Constants.getInstance().ordinal(13),
            O = Constants.getInstance().ordinal(14),
            P = Constants.getInstance().ordinal(15),
            Q = Constants.getInstance().ordinal(16),
            R = Constants.getInstance().ordinal(17),
            S = Constants.getInstance().ordinal(18),
            T = Constants.getInstance().ordinal(19),
            U = Constants.getInstance().ordinal(20),
            V = Constants.getInstance().ordinal(21),
            W = Constants.getInstance().ordinal(22),
            X = Constants.getInstance().ordinal(23),
            Y = Constants.getInstance().ordinal(24),
            Z = Constants.getInstance().ordinal(25);

    static Object[] OBJECTS = new Object[0];
    static String[] STRINGS = new String[0];
    static long[] LONGS = new long[0];
    static BigInteger[] INTEGERS = new BigInteger[0];

    Order clone() throws CloneNotSupportedException;

    default MutableOrder order() {
        try {
            return (MutableOrder) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    String toString();

    <T> Args extend(Ordinal col, T... arrays);

    Args extend(Ordinal col, long... array);

    default <T> ArgsI<T> extendA(T... arrays) {
        return (ArgsI<T>) extend(A, arrays);
    }

    default ArgsI<long[]> extendA(long... array) {
        return (ArgsI<long[]>) extend(A, array);
    }
}
