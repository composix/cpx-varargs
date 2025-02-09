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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.composix.math.Args;
import io.github.composix.testing.TestCase;

class OrderTest extends TestCase {

    // Arrays
    long[] idArray = {1, 2, 3};
    String[] array = {"aap", "noot", "mies" };

    // Collections
    List<Long> idList = Arrays.asList(1L,2L,3L);
    List<String> list = Arrays.asList("aap", "noot", "mies");

    // Streams
    LongStream idStream = LongStream.of(1, 2, 3);
    Stream<String> stream = Stream.of("aap", "noot", "mies");

    // VarArgs
    Args idArgs = Args.ofLongs(1L, 2L, 3L);
    Args args = Args.of("aap", "noot", "mies");
    Args matrix = Args.of(idArray.clone(), array.clone());

    @BeforeEach
    void sorting() {
        // Arrays
        Arrays.sort(idArray);
        Arrays.sort(array);

        // Collections
        idList.sort(Comparator.naturalOrder());
        list.sort(Comparator.naturalOrder());

        assertArrayEquals(LongStream.of(idArray).sorted().boxed().toArray(), idList.toArray());
        assertArrayEquals(array, list.toArray());

        // Streams
        assertArrayEquals(idArray, idStream.sorted().toArray());
        assertArrayEquals(array, stream.sorted().toArray());

        // VarArgs
        idArgs.orderBy(A);
        args.orderBy(A);
        matrix.orderBy(B); 
    }

    @Test
    void testOrdinalOf() {
        assertSame(A, idArgs.ordinalAt(A, 1L));
        assertSame(B, idArgs.ordinalAt(A, 2L));
        assertSame(C, idArgs.ordinalAt(A, 3L));

        assertSame(A, args.ordinalAt(A, "aap"));
        assertSame(C, args.ordinalAt(A, "noot"));
        assertSame(B, args.ordinalAt(A, "mies"));

        assertSame(A, matrix.ordinalAt(B, "aap"));
        assertSame(C, matrix.ordinalAt(B, "noot"));
        assertSame(B, matrix.ordinalAt(B, "mies"));
    }
}
