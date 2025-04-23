/**
 * interface ArgsOrdinal
 *
 * Serves as a foundational interface that provides a rich set of constants for use with 
 * other interfaces in the package. It also defines the ArgsOrdinal::clone method as a 
 * starting point for creating Args instances that represent tabular data.
 * 
 * Additionally, the ArgsOrdinal::extend method enables seamless addition of new columns 
 * to the data structure in a type-safe manner.
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

import java.math.BigInteger;
import java.util.Objects;
import java.util.regex.Pattern;

import io.github.composix.varargs.ArgsI;

/**
 * As a foundational interface, {@code ArgsOrdinal} provides a set of useful constants 
 * for working with the other interfaces in this package. It is recommended to 
 * implement this interface in your classes if you intend to use these constants. 
 * Since all methods have default implementations, implementing this interface 
 * requires no additional effort.
 * 
 * @author dr. ir. J. M. Valk
 * @since April 2025
 */
public interface ArgsOrdinal extends Cloneable {
    static Constants CONSTANTS = Constants.getInstance();
    static final int SIZE = 16, SHIFT = 5, MASK = (1 << SHIFT) - 1, SHIFT2 = 3, MASK2 = (1 << SHIFT2) - 1;
    static final char QUOTE = '"', DELIM = ';';
    static final Pattern PATTERN = Pattern.compile(Pattern.quote(new String(new char[] {QUOTE,DELIM,QUOTE})));
    static final Ordinal OMEGA = CONSTANTS.omega();
    static final Ordinal A = CONSTANTS.ordinal(0),
            B = CONSTANTS.ordinal(1),
            C = CONSTANTS.ordinal(2),
            D = CONSTANTS.ordinal(3),
            E = CONSTANTS.ordinal(4),
            F = CONSTANTS.ordinal(5),
            G = CONSTANTS.ordinal(6),
            H = CONSTANTS.ordinal(7),
            I = CONSTANTS.ordinal(8),
            J = CONSTANTS.ordinal(9),
            K = CONSTANTS.ordinal(10),
            L = CONSTANTS.ordinal(11),
            M = CONSTANTS.ordinal(12),
            N = CONSTANTS.ordinal(13),
            O = CONSTANTS.ordinal(14),
            P = CONSTANTS.ordinal(15),
            Q = CONSTANTS.ordinal(16),
            R = CONSTANTS.ordinal(17),
            S = CONSTANTS.ordinal(18),
            T = CONSTANTS.ordinal(19),
            U = CONSTANTS.ordinal(20),
            V = CONSTANTS.ordinal(21),
            W = CONSTANTS.ordinal(22),
            X = CONSTANTS.ordinal(23),
            Y = CONSTANTS.ordinal(24),
            Z = CONSTANTS.ordinal(25),
            AA = CONSTANTS.ordinal(26),
            AB = CONSTANTS.ordinal(27),
            AC = CONSTANTS.ordinal(28),
            AD = CONSTANTS.ordinal(29),
            AE = CONSTANTS.ordinal(30),
            AF = CONSTANTS.ordinal(31),
            AG = CONSTANTS.ordinal(32),
            AH = CONSTANTS.ordinal(33),
            AI = CONSTANTS.ordinal(34),
            AJ = CONSTANTS.ordinal(35),
            AK = CONSTANTS.ordinal(36),
            AL = CONSTANTS.ordinal(37),
            AM = CONSTANTS.ordinal(38),
            AN = CONSTANTS.ordinal(39),
            AO = CONSTANTS.ordinal(40),
            AP = CONSTANTS.ordinal(41),
            AQ = CONSTANTS.ordinal(42);

    static Object[] TYPES = CONSTANTS.types;
    
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

    default Args extend(CharSequence... column) {
        if (column.length > 1) {
            try {
                return clone().extend(column);
            } catch(CloneNotSupportedException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        switch(column.length) {
            case 0: 
                throw new IllegalArgumentException("column must start with a header");
            default: 
                throw new IllegalArgumentException("column must not be empty");
        }
    }

    default Args extend(Column<?> column) {
        try {
            return clone().extend(column);
        } catch(CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Deprecated
    default Args extend(int col, int amount, Object array) {
        try {
            return clone().extend(col, amount, array);
        } catch(CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Deprecated
    default <T> Args extend(Ordinal col, T... column) {
        final int length = column.length;
        if (length > 0) {
            return extend(col.intValue(), length, column);
        }
        throw new IllegalArgumentException("column must not be empty");
    }

    @Deprecated
    default Args extendLong(long... column) {
        final int length = column.length;
        if (length > 0) {
            return extend(AL.intValue(), length, column);
        }
        throw new IllegalArgumentException("column must not be empty");
    }

    @Deprecated
    default Class<?> typeOf(Ordinal col) {
        return Void.class;
    }

    @Deprecated
    default ArgsI<CharSequence> castI(int cols) {
        return castI(cols, CharSequence.class);
    }

    @Deprecated
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
