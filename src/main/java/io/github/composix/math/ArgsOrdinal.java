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
import java.net.URI;
import java.util.regex.Pattern;

import io.github.composix.varargs.ArgsI;

public interface ArgsOrdinal extends Cloneable {
    static final char QUOTE = '"', DELIM = ';';
    static final Pattern PATTERN = Pattern.compile(Pattern.quote(new String(new char[] {QUOTE,DELIM,QUOTE})));
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
            Z = Constants.getInstance().ordinal(25),
            AA = Z.next(),
            AB = AA.next(),
            AC = AB.next(),
            AD = AC.next(),
            AE = AD.next(),
            AF = AE.next(),
            AG = AF.next(),
            AH = AG.next(),
            AI = AH.next(),
            AJ = AI.next(),
            AK = AJ.next(),
            AL = AK.next(),
            AM = AL.next(),
            AN = AM.next(),
            AO = AN.next(),
            AP = AO.next();

    static Class<?>[] TYPES = new Class[] {
        null, // Q
        null, // R
        String.class, // S
        null, // T
        URI.class, // U
        null, // V
        null, // W
        null, // X
        null, // Y
        null, // Z
        boolean.class, // AA
        byte.class, // AB
        char.class, // AC
        short.class, // AD
        boolean[].class, // AE
        byte[].class, // AF
        char[].class, // AG
        short[].class, // AH
        int.class, // AI
        int[].class, // AJ
        long[].class, // AK
        long.class, // AL
        null, // AM
        null, // AN
        null, // AO
        null // AP
    };
    
    static Object[] OBJECT = new Object[1];
    static Object[] OBJECTS = new Object[0];
    static String[] STRINGS = new String[0];
    static long[] LONGS = new long[0];
    static BigInteger[] INTEGERS = new BigInteger[0];

    default Args clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    @Override
    String toString();

    default Args extend(int col, int amount, int offset, int length, Object... arrays) {
        try {
            return clone().extend(col, amount, offset, length, arrays);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    default <T> Args extend(Ordinal col, T... column) {
        final int length = column.length;
        if (length > 0) {
            OBJECT[0] = column;
            return extend(col.intValue(), length, 0, 1, OBJECT);
        }
        throw new IllegalArgumentException("column must not be empty");
    }

    default Args extendLong(long... column) {
        final int length = column.length;
        if (length > 0) {
            OBJECT[0] = column;
            return extend(AL.intValue(), length, 0, 1, OBJECT);
        }
        throw new IllegalArgumentException("column must not be empty");
    }

    default Class<?> typeOf(Ordinal col) {
        return Void.class;
    }

    default ArgsI<CharSequence> castI(int cols) {
        return castI(cols, CharSequence.class);
    }

    default <T> ArgsI<T> castI(int cols, Class<T> expected) {
        if (expected.isPrimitive()) {
            throw new UnsupportedOperationException("primitives (e.g., long.class) must be boxed as wrapper (e.g., Long.class) or array (e.g., long[].class)");
        }
        Class<?> actual = typeOf(A);
        for (int i = 0; i < cols; ++i) {
            final Ordinal col = OrdinalNumber.ORDINALS[i];
            if (actual != typeOf(col)) {
                throw new ClassCastException("invalid type at column: " + col.column());
            }    
        }
        final Ordinal col = OrdinalNumber.ORDINALS[cols];
        if (actual == typeOf(col)) {
            throw new ClassCastException("amount of columns of same type exceeds: " + cols);
        }
        if (actual.isPrimitive()) {
            if (expected.getComponentType() == actual) {
                return castI();
            }
        } else {
            if (expected == actual) {
                return castI();
            }
            while (actual.isArray()) {
                actual = actual.getComponentType();
            }
            if (actual.isPrimitive() && expected.getComponentType() == actual) {
                return castI();
            }
        }
        throw new ClassCastException();
    }

    private <T> ArgsI<T> castI() {
        switch(this) {
            case ArgsI<?> result:
                return (ArgsI<T>) this;
            default:
                throw new IllegalStateException("type-safe VarArgs not configured");
        }
    }
}
