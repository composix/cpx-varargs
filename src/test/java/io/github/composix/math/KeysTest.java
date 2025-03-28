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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;

class KeysTest extends TestCase implements TestData {

  static Pet THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY;
  static Order O, P, Q, R, S, T;
  static Order[] EMPTY = new Order[0];

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
    pets = new Matrix(8);
    orders = new Matrix(6);
    PETS.export(pets, 0, 1);
    ORDERS.export(orders, 0, 1);
  }

  @Test
  void testGroupBy_PETS() throws NoSuchFieldException {
    long[] expected = { 1, 12, 15 };

    Args petsByCategory = pets
      .groupBy(A, Pet::category)
      .keys(A, Pet::category)
      .collect(A, Pet::id, Long::sum);

    // using VarArgs
    assertArrayEquals(
      new Category[] {
        new Category(0, "cats"),
        new Category(1, "dogs"),
        new Category(2, "other"),
      },
      petsByCategory.stream(B).toArray(Category[]::new)
    );
    assertArrayEquals(expected, petsByCategory.longStream(0).toArray());

    // using Streams
    assertArrayEquals(
      expected,
      Stream.of(THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY)
        .collect(
          Collectors.groupingBy(Pet::category, Collectors.summingLong(Pet::id))
        )
        .values()
        .stream()
        .mapToLong(Long::longValue)
        .toArray()
    );
  }

  @Test
  void testGroupBy_ORDERS() {
    Args ordersByQuantity = orders
      .groupBy(A, Order::quantity)
      .keys(A, Order::quantity)
      .collect(A, x -> 1L, Long::sum);
  }

  @Test
  @Disabled
  void testThenBy() {
    long[] expected = { 1, 9, 3, 11, 4 };
    assertArrayEquals(
      expected,
      pets
        .groupBy(A, Pet::category)
        .thenBy(A, Pet::status)
        .collect(A, Pet::id, Long::sum)
        .longStream(B)
        .toArray()
    );
  }

  @Test
  void testJoinOne() {
    assertThrows(IllegalArgumentException.class, () ->
      pets.on(A, Pet::id).joinOne(orders.on(A, Order::petId))
    );

    Args result = orders.on(A, Order::petId).joinOne(pets.on(A, Pet::id));
    result.orderBy(A, Order::id);
    assertTrue(result.isOrdinal());

    assertAllSame(
      orders.stream(A).toArray(Order[]::new),
      result.stream(A).toArray(Order[]::new)
    );
    assertAllSame(
      all(MICKEY, DUCHESS, DONALD, PLUTO, FREY, MICKEY),
      result.stream(B).toArray(Pet[]::new)
    );
  }

  @Test
  void testJoinMany() {
    Args result = pets.on(A, Pet::id).joinMany(orders.on(A, Order::petId));

    assertAllSame(
      pets.stream(A).toArray(Pet[]::new),
      result.stream(A).toArray(Pet[]::new)
    );
    assertAllEquals(
      all(EMPTY, all(P), all(R), EMPTY, all(S), all(O, T), all(Q), EMPTY),
      result.stream(B).toArray(Order[][]::new)
    );
  }
}
