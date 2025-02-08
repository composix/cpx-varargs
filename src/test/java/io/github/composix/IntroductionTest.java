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

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.composix.math.Args;
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
    Args idArgs = Args.ofLongs(1L, 2L, 3L);
    Args args = Args.of("aap", "noot", "mies");

    @Test
    void testGetValue() {
        assertSame("aap", args.getValue(0));
        assertSame("noot", args.getValue(1));
        assertSame("mies", args.getValue(2));
    }

    @Test
    void testGetLongValue() {
        assertEquals(1L, idArgs.getLongValue(0));
        assertEquals(2L, idArgs.getLongValue(1));
        assertEquals(3L, idArgs.getLongValue(2));
    }

    @Test
    void testStream() {
        assertArrayEquals(
            array,
            args.stream(A).toArray()
        );
    }

    @Test
    void testLongStream() {
        assertArrayEquals(
            idArray,
            idArgs.longStream(A).toArray()
        );
    }
}
