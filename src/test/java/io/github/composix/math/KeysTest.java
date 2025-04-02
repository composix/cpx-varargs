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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class KeysTest extends TestCase implements TestData {

  static Pet THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY;
  static Order O, P, Q, R, S, T;
  static Order[] EMPTY = new Order[0];

  Matrix pets, orders;
  VarArgs petVarArgs, orderVarArgs;

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
    // Given two matrices...
    pets = new Matrix(8);
    orders = new Matrix(6);

    // ...populated with data from the TestData interface
    PETS.export(pets, 0, 1);
    ORDERS.export(orders, 0, 1);

    // Then the data can be found in the underlying VarArgs
    petVarArgs = pets.varArgs();
    orderVarArgs = orders.varArgs();
    assertAllEquals(
      all(THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY),
      petVarArgs.argv[pets.hashCode() & petVarArgs.mask()]
    );
    assertAllEquals(
      all(O, P, Q, R, S, T),
      orderVarArgs.argv[orders.hashCode() & orderVarArgs.mask()]
    );
  }

  @Test
  void testGroupByCategory() throws NoSuchFieldException {
    // When grouping pets by category...
    assertSame(pets, pets.groupBy(A, Pet::category));
    // ...then the same object provides both the Args and Keys intefaces

    // Then also the underlying VarArgs...
    int mask = petVarArgs.mask(), offset = pets.hashCode() & mask;
    Object[] argv = petVarArgs.argv;

    // ...still contains the pets
    assertAllEquals(
      all(THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY),
      petVarArgs.argv[offset & mask]
    );

    // ...and contains the indices of the groups
    assertAllEquals(all(C, F, I), argv[--offset & mask]);

    // ...and contains the extracted keys
    assertAllEquals(
      all(
        new Category(0, "cats"),
        new Category(1, "dogs"),
        new Category(2, "other")
      ),
      argv[--offset & mask]
    );

    // And nothing else
    assertNull(argv[--offset & mask]);
  }

  @Test
  void testGroupBy_ORDERS() {
    // When grouping orders by quantity...
    assertSame(orders, orders.groupBy(A, Order::quantity));
    // ...then the same object provides both the Args and Keys intefaces

    // Then also the underlying VarArgs...
    int mask = orderVarArgs.mask(), offset = orders.hashCode() & mask;
    Object[] argv = orderVarArgs.argv;

    // ...still contains the orders
    assertAllEquals(
      all(O, P, Q, R, S, T),
      orderVarArgs.argv[orders.hashCode() & orderVarArgs.mask()]
    );

    // ...and contains the indices of the groups
    assertAllEquals(all(D,G), argv[--offset & mask]);

    // ...and contains the extracted quantities
    assertAllEquals(any(1L,2L), argv[--offset & mask]);
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
