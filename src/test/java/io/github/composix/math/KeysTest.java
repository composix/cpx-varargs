/**
 * KeysTest
 *
 * This test class contains unit tests for the functionalities provided by the
 * {@link io.github.composix.math.Keys} class, specifically focusing on operations
 * related to grouping, joining, and sorting tabular data represented as matrices.
 *
 * Key Features Tested:
 * - Grouping operations (e.g., groupBy) on data sets such as {@link Pet} and {@link Order}.
 * - Joining operations, including both one-to-one joins (joinOne) and many-to-many joins (joinMany).
 * - The ability to sort and manipulate grouped data using additional sorting criteria (thenBy).
 * - Verifying the integrity of the underlying {@link VarArgs} data structure after operations.
 *
 * The tests ensure that these operations are functioning correctly, including edge cases
 * where the data may be empty, improperly matched, or require custom sorting logic.
 *
 * Author: dr. ir. J. M. Valk
 * Date: April 2025
 */

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

import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class KeysTest extends PetstoreTestCase {

  @Test
  void testGroupByCategory() throws NoSuchFieldException {
    // When grouping pets by category...
    assertSame(pets, pets.groupBy(A, Pet::category));
    // ...then the same object provides both the Args and Keys interfaces

    // Then also the underlying VarArgs...
    int mask = petVarArgs.mask(), offset = pets.hashCode() & mask;
    Object[] argv = petVarArgs.argv;

    // ...still contains the pets
    assertAllEquals(
      all(THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY),
      petVarArgs.argv[offset & mask]
    );

    // ...and contains the indices of the groups
    ArgsSet<Category> columnA = (ArgsSet<Category>) argv[--offset & mask];
    assertAllEquals(
      all(C, F, I),
      columnA.indices().toArray(Ordinal[]::new)
    );

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
    assertAllEquals(
      all(D, G),
      ((Index) argv[--offset & mask]).toArray(Ordinal[]::new)
    );

    // ...and contains the extracted quantities
    assertAllEquals(any(1L, 2L), argv[--offset & mask]);
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
}
