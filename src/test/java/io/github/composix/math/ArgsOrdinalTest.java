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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ArgsOrdinalTest {
    static Ordinal[] ORDINALS;
    static OrdinalInt OMEGA, A, B, C, D;
    static OrderInt omega, a, b, c, d;

    @BeforeAll
    static void beforeAll() throws CloneNotSupportedException {
        ORDINALS = OrdinalNumber.ORDINALS;
        OMEGA = (OrdinalInt) ArgsOrdinal.OMEGA;
        A = (OrdinalInt) ArgsOrdinal.A;
        B = (OrdinalInt) ArgsOrdinal.B;
        C = (OrdinalInt) ArgsOrdinal.C;
        D = (OrdinalInt) ArgsOrdinal.D;
        omega = (OrderInt) OMEGA.clone();
        a = (OrderInt) A.clone();
        b = (OrderInt) B.clone();
        c = (OrderInt) C.clone();
        d = (OrderInt) D.clone();
    }

    @Test
    void testOmega() throws CloneNotSupportedException {
        assertSame(ORDINALS[-Short.MIN_VALUE], OMEGA);
        assertSame(OrdinalInt.class, OMEGA.getClass());
        assertEquals(-Short.MIN_VALUE, OMEGA.ordinal);
    }

    @Test
    void testAlphabet() {
        assertSame(ORDINALS[0], A);
        assertSame(ORDINALS[1], B);
        assertSame(ORDINALS[2], C);
        assertSame(ORDINALS[3], D);
        assertSame(OrdinalInt.class, A.getClass());
        assertSame(OrdinalInt.class, B.getClass());
        assertSame(OrdinalInt.class, C.getClass());
        assertSame(OrdinalInt.class, D.getClass());
        assertEquals(0, A.ordinal);
        assertEquals(1, B.ordinal);
        assertEquals(2, C.ordinal);
        assertEquals(3, D.ordinal);
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        assertSame(OrderInt.class, a.getClass());
        assertSame(OrderInt.class, b.getClass());
        assertSame(OrderInt.class, c.getClass());
        assertSame(OrderInt.class, d.getClass());
        assertNotSame(a, A.clone());
        assertNotSame(a, a.clone());
        assertNotSame(b, B.clone());
        assertNotSame(b, b.clone());
        assertNotSame(c, C.clone());
        assertNotSame(c, c.clone());
        assertNotSame(d, D.clone());
        assertNotSame(d, d.clone());
        assertEquals(0, a.ordinal);
        assertEquals(1, b.ordinal);
        assertEquals(2, c.ordinal);
        assertEquals(3, d.ordinal);
        assertSame(ORDINALS, a.ordinals);
        assertSame(ORDINALS, b.ordinals);
        assertSame(ORDINALS, c.ordinals);
        assertSame(ORDINALS, d.ordinals);
    }

    @Test
    void testOrder() {
        assertSame(omega, omega.order());
        assertSame(a, a.order());
        assertSame(b, b.order());
        assertSame(c, c.order());
        assertSame(d, d.order());

        assertNotSame(OMEGA, OMEGA.order());
        assertNotSame(omega, OMEGA.order());
        assertNotSame(A, A.order());
        assertNotSame(a, A.order());
        assertNotSame(B, B.order());
        assertNotSame(b, B.order());
        assertNotSame(C, C.order());
        assertNotSame(c, C.order());
        assertNotSame(D, D.order());
        assertNotSame(d, D.order());

        assertSame(OMEGA, omega.ordinal());
        assertSame(A, a.ordinal());
        assertSame(B, b.ordinal());
        assertSame(C, c.ordinal());
        assertSame(D, d.ordinal());
    }

    @Test
    void testToString() {
        assertEquals("32768", OMEGA.toString());
        assertEquals("0", A.toString());
        assertEquals("1", B.toString());
        assertEquals("2", C.toString());
        assertEquals("3", D.toString());
        for (int i = 0; i < 1 - Short.MIN_VALUE; ++i) {
            assertEquals(Integer.toString(i), ORDINALS[i].toString());
        }
    }

    @Test
    void testExtend() {
        long[] expected = {0, 1, 2, 3};
        Matrix matrix  = (Matrix) OMEGA.extend(A, expected);
        assertEquals(0, matrix.hashCode());
        assertSame(expected, matrix.varArgs().argv[0]);
        assertEquals(ORDINALS, matrix.ordinals);
        assertEquals(OMEGA.intValue() + 4, matrix.ordinal);
    }
}
