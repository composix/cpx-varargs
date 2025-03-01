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

package io.github.composix;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.composix.math.Args;
import io.github.composix.math.MutableOrder;
import io.github.composix.testing.TestCase;

class IntroductionTest extends TestCase {
    // Arrays
    long[] idArray = {1, 2, 3};
    String[] array = {"aap", "noot", "mies" };

    // Collections
    List<Long> idList = List.of(1L,2L,3L);
    List<String> list = List.of("aap", "noot", "mies");

    // Streams
    LongStream idStream = LongStream.of(1, 2, 3);
    Stream<String> stream = Stream.of("aap", "noot", "mies");

    // VarArgs
    Args idArgs = D.extend(A, idArray);
    Args args = D.extend(A, (Object) array);
    Args matrix = D.extend(A, idArray, array);

    @Test
    void testGetValue() {
        assertSame("aap", args.getValue(0));
        assertSame("noot", args.getValue(1));
        assertSame("mies", args.getValue(2));

        assertSame("aap", matrix.getValue(B.index(A)));
        assertSame("noot", matrix.getValue(B.index(B)));
        assertSame("mies", matrix.getValue(B.index(C)));
    }

    @Test
    void testGetLongValue() {
        assertEquals(1L, idArgs.getLongValue(0));
        assertEquals(2L, idArgs.getLongValue(1));
        assertEquals(3L, idArgs.getLongValue(2));

        assertEquals(1L, matrix.getLongValue(A.index(A)));
        assertEquals(2L, matrix.getLongValue(A.index(B)));
        assertEquals(3L, matrix.getLongValue(A.index(C)));
    }

    @Test
    void testStream() {
        assertArrayEquals(
            array,
            args.stream(A).toArray()
        );

        assertArrayEquals(
            array,
            matrix.stream(B).toArray()
        );
    }

    @Test
    void testLongStream() {
        assertArrayEquals(
            idArray,
            idArgs.longStream(A).toArray()
        );

        assertArrayEquals(
            idArray,
            matrix.longStream(A).toArray()
        );
    }

    @Test
    void testSelect() {
        MutableOrder order = (MutableOrder) C.order();
        order.reorder(Comparator.reverseOrder());
        Args reversed = matrix.select(order);
        //assertEquals(1L, matrix.getLongValue(A.index(A)));
        //assertSame("aap", matrix.getValue(B.index(A)));
        assertSame("aap", reversed.getValue(A.index(A)));
        assertEquals(1L, reversed.getLongValue(B.index(A)));
        assertTrue(reversed.isOrdinal());
    }

    // tests on the Order interface

    @Test
    void testOrder() {
        assertSame(idArgs, idArgs.order());
        assertSame(args, args.order());
        assertSame(matrix, matrix.order());
    }

    @Test
    void testOrdinal() {
        assertSame(idArgs, idArgs.ordinal());
        assertSame(args, args.ordinal());
        assertSame(matrix, matrix.ordinal());
    }

    @Test
    void testIsOrdinal() {
        assertTrue(idArgs.isOrdinal());
        assertTrue(args.isOrdinal());
        assertTrue(matrix.isOrdinal());
    }

    @Test
    void testIntValue() {
        final int omega = OMEGA.intValue();
        assertEquals(omega + 3, idArgs.ordinal().intValue());
        assertEquals(omega + 3, args.ordinal().intValue());
        assertEquals(2 * omega + 3, matrix.ordinal().intValue());
    }
}
