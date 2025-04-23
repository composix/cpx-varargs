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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.composix.testing.TestCase;

class ArgsOrdinalTest extends TestCase {

  static Ordinal[] ORDINALS;

  @BeforeAll
  static void beforeAll() throws CloneNotSupportedException {
    ORDINALS = OrdinalNumber.ORDINALS;
  }

  @Test
  void testOMEGA() {
    assertSame(ORDINALS[-Short.MIN_VALUE], OMEGA);
  }

  @Test
  void testAlphabet() {
    assertSame(ORDINALS[0], A);
    assertSame(ORDINALS[1], B);
    assertSame(ORDINALS[2], C);
    assertSame(ORDINALS[3], D);
    assertSame(ORDINALS[4], E);
    assertSame(ORDINALS[5], F);
    assertSame(ORDINALS[6], G);
    assertSame(ORDINALS[7], H);
    assertSame(ORDINALS[8], I);
    assertSame(ORDINALS[9], J);
    assertSame(ORDINALS[10], K);
    assertSame(ORDINALS[11], L);
    assertSame(ORDINALS[12], M);
    assertSame(ORDINALS[13], N);
    assertSame(ORDINALS[14], O);
    assertSame(ORDINALS[15], P);
    assertSame(ORDINALS[16], Q);
    assertSame(ORDINALS[17], R);
    assertSame(ORDINALS[18], S);
    assertSame(ORDINALS[19], T);
    assertSame(ORDINALS[20], U);
    assertSame(ORDINALS[21], V);
    assertSame(ORDINALS[22], W);
    assertSame(ORDINALS[23], X);
    assertSame(ORDINALS[24], Y);
    assertSame(ORDINALS[25], Z);
  }

  @Test
  void testTYPES() {
    assertSame(Object[].class, TYPES.getClass());
    assertAllSame(all(""), TYPES[2]);
    assertAllSame(any(false), TYPES[10]);
    assertEquals(26, TYPES.length);
  }

  @Test
  void testClone() {
    assertNull(
      assertThrows(CloneNotSupportedException.class, () -> clone()).getMessage()
    );
  }

  @Test
  void testExtend() {
    assertInstanceOf(CloneNotSupportedException.class,
      assertThrows(UnsupportedOperationException.class, () ->
        extend(new ArgsIndexList<>(AL.byteValue(), any(0L, 1L, 2L)))
      ).getCause()
    );
  }

  @Override
  public Args clone() throws CloneNotSupportedException {
    return (Args) super.clone();
  }
}
