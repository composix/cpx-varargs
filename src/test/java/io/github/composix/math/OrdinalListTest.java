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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link OrdinalList} abstraction and its internal subclasses.
 *
 * This test suite verifies the correctness and memory model behavior of compact ordinal
 * index representations used throughout the ComPosiX VarArgs framework. It ensures that:
 * 
 * - The appropriate subclass is selected based on ordinal range (byte vs short).
 * - Accessor methods (`getInt`, `setInt`, `get`) behave as expected.
 * - Unsupported array access methods throw when not implemented by a subclass.
 *
 * These tests validate the robustness of internal index logic crucial for
 * large-scale tabular data processing.
 *
 * @since April 2025
 */
public class OrdinalListTest {

    // Test creation of correct backing array type based on ordinal value

    /**
     * Tests that the OrdinalList factory method creates an instance of ByteIndex when
     * the ordinal value is within the byte range.
     */
    @Test
    public void testByteIndexCreation() {
        OrdinalList<Ordinal> list = OrdinalList.of(1, (short) 100);
        assertSame(list.asByteArray(), list.asArray()); // ensure the list is backed by a byte array
        // and that it is not backed by anything else
        assertThrows(UnsupportedOperationException.class, list::asBitSet);
        assertThrows(UnsupportedOperationException.class, list::asShortArray);
        assertThrows(UnsupportedOperationException.class, list::asIntArray);
        assertThrows(UnsupportedOperationException.class, list::asLongArray);
    }

    /**
     * Tests that the OrdinalList factory method creates an instance of ShortIndex when
     * the ordinal value exceeds the byte range and is within the short range.
     */
    @Test
    public void testShortIndexCreation() {
        OrdinalList<Ordinal> list = OrdinalList.of(1, (short) 200);
        assertSame(list.asShortArray(), list.asArray()); // ensure the list is backed by a byte array
        // and that it is not backed by anything else
        assertThrows(UnsupportedOperationException.class, list::asBitSet);
        assertThrows(UnsupportedOperationException.class, list::asByteArray);
        assertThrows(UnsupportedOperationException.class, list::asIntArray);
        assertThrows(UnsupportedOperationException.class, list::asLongArray);
    }

    // Test correct list behavior for size and get/set operations

    /**
     * Tests the size and basic retrieval functionality for a ByteIndex-backed OrdinalList.
     * It ensures that the list size is correct, and that the get and set operations work as expected.
     */
    @Test
    public void testSizeAndGetByteIndex() {
        OrdinalList<Ordinal> list = OrdinalList.of(1, (short) 100);
        assertEquals(1, list.size()); // Verify size of the list
        list.setInt(0, 10); // Set a value in the list
        assertEquals(10, list.getInt(0)); // Verify that the value was set correctly
        assertSame(ArgsOrdinal.K, list.get(0)); // Verify retrieval via Ordinal
    }

    /**
     * Tests the size and basic retrieval functionality for a ShortIndex-backed OrdinalList.
     * Similar to the ByteIndex test but for short-backed lists.
     */
    @Test
    public void testSizeAndGetShortIndex() {
        OrdinalList<Ordinal> list = OrdinalList.of(5, (short) 300);
        assertEquals(5, list.size());
        list.setInt(0, 20);
        assertEquals(20, list.getInt(0));
        assertSame(ArgsOrdinal.U, list.get(0));
    }
}
