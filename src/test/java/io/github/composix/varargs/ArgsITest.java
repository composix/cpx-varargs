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

package io.github.composix.varargs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.composix.math.VarArgs;
import io.github.composix.testing.TestCase;
import org.junit.jupiter.api.Test;

class ArgsITest extends TestCase {

  // Verify that ArgsI can be created from an array
  // and check the proper values
  @Test
  void testOf() {
    // Given an array of strings
    String[] columnA = { "Hello", "World" };

    // When we create ArgsI<String>
    ArgsI<CharSequence> args = ArgsI.of(columnA);

    // Then
    assertEquals(
      OMEGA.intValue() + columnA.length,
      args.order().ordinal().intValue()
    );
    final VarArgs varargs = TestMatrix.varargs(args);
    int offset = args.hashCode() & varargs.mask();
    assertNull(varargs.argv[--offset & varargs.mask()]);
    assertSame(columnA, varargs.argv[++offset & varargs.mask()]);
    assertNull(varargs.argv[++offset & varargs.mask()]);
  }

  @Test
  void testOfNull() {
    // Given a null array
    String[] nullArray = null;

    // When we create ArgI<String> of null
    assertThrows(NullPointerException.class, () -> ArgsI.of(nullArray));
    // then a NullPointerException is thrown
  }

  @Test
  void testOfEmptyArray() {
    // Test that passing an empty array works correctly.
    String[] emptyArray = {};
    assertEquals(
      "column must not be empty",
      assertThrows(IllegalArgumentException.class, () -> ArgsI.of(emptyArray)
      ).getMessage()
    );
  }

  @Test
  void testAndOf() {
    // Given two arrays of strings
    String[] columnA = { "Hello", "World" };
    String[] columnB = { "Foo", "Bar" };

    // and an ArgsI<String>
    ArgsI<CharSequence> args = ArgsI.of(columnA);

    // When we call andOf
    assertSame(args, args.andOf(columnB));
    // then the andOf call is chained

    // And then
    assertEquals(
      OMEGA.intValue() * 2 + columnA.length,
      args.order().ordinal().intValue()
    );
    final VarArgs varargs = TestMatrix.varargs(args);
    int offset = args.hashCode() & varargs.mask();
    assertNull(varargs.argv[--offset & varargs.mask()]);
    assertSame(columnA, varargs.argv[++offset & varargs.mask()]);
    assertSame(columnB, varargs.argv[++offset & varargs.mask()]);
    assertNull(varargs.argv[++offset & varargs.mask()]);
  }

  @Test
  void testAndOfMultipleCalls() {
    String[] columnA = { "Hello", "World" };
    String[] columnB = { "Foo", "Bar" };
    String[] columnC = { "Baz", "Qux" };

    ArgsI<CharSequence> args = ArgsI.of(columnA);
    args = args.andOf(columnB).andOf(columnC);

    // Check if the arguments are in the correct order after calling andOf
    // The offset and masking is used to properly index into the VarArgs argv array.
    final VarArgs varargs = TestMatrix.varargs(args);
    int offset = args.hashCode() & varargs.mask();

    // Ensure the correct order and contents in argv
    assertSame(columnA, varargs.argv[offset++ & varargs.mask()]);
    assertSame(columnB, varargs.argv[offset++ & varargs.mask()]);
    assertSame(columnC, varargs.argv[offset++ & varargs.mask()]);
  }
}
