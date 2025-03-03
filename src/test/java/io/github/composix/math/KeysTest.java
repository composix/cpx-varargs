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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;

class KeysTest extends TestCase implements TestData {
  static Pet THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY;
  static Order O, P, Q, R, S, T;

  Args pets;
  Args orders;

  @BeforeAll
  static void beforeAll() {
    THOMAS = PETS.getValue(0);
    DUCHESS = PETS.getValue(1);
    PLUTO = PETS.getValue(2);
    FRANK = PETS.getValue(3);
    FREY = PETS.getValue(4);
    MICKEY = PETS.getValue(5);
    DONALD = PETS.getValue(6);
    GOOFY = PETS.getValue(7);

    O = ORDERS.getValue(0);
    P = ORDERS.getValue(1);
    Q = ORDERS.getValue(2);
    R = ORDERS.getValue(3);
    S = ORDERS.getValue(4);
    T = ORDERS.getValue(5);
  } 

  @BeforeEach
  void setUp() {
    pets = PETS;
    orders = G.extend(A, O, P, Q, R, S, T);
  }

  @Test
  void testGroupBy() {
    long[] expected = {1, 12, 15};
    assertArrayEquals(
      expected,
      pets
        .groupBy(A, Pet::category)
        .collect(Collectors.summingLong(Pet::id))
        .longStream(A)
        .toArray()
    );
  }

  @Test
  void testThenBy() {
    long[] expected = {1, 9, 3, 11, 4};
    assertArrayEquals(
      expected,
      pets
        .groupBy(A, Pet::category)
        .thenBy(A, Pet::status)
        .collect(Collectors.summingLong(Pet::id))
        .longStream(A)
        .toArray()
    );
  }

  @Test
  void testJoin() {
    Args result = pets
      .groupBy(A, Pet::id)
      .join(orders.groupBy(A, Order::petId));

    assertSame(THOMAS, result.getValue(A.index(A)));
    assertNull(result.getValue(B.index(A)));

    assertSame(DUCHESS, result.getValue(A.index(B)));
    assertSame(P, result.getValue(B.index(B)));

    assertSame(PLUTO, result.getValue(A.index(C)));
    assertSame(R, result.getValue(B.index(C)));

    assertSame(FRANK, result.getValue(A.index(D)));
    assertNull(result.getValue(B.index(D)));

    assertSame(FREY, result.getValue(A.index(E)));
    assertSame(S, result.getValue(B.index(E)));

    assertSame(MICKEY, result.getValue(A.index(F)));
    assertSame(O, result.getValue(B.index(F)));

    assertSame(MICKEY, result.getValue(A.index(G)));
    assertSame(T, result.getValue(B.index(G)));

    assertSame(DONALD, result.getValue(A.index(H)));
    assertSame(Q, result.getValue(B.index(H)));

    assertSame(GOOFY, result.getValue(A.index(I)));
    assertNull(result.getValue(B.index(I)));
  }
}
