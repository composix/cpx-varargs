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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ArgsTest extends PetstoreTestCase {

  @Test
  void testClone() throws CloneNotSupportedException {
    assertClone(pets);
    assertClone(orders);
  }

  @Test
  void testExport() throws CloneNotSupportedException {
    assertExport(pets);
    assertExport(orders);
  }

  @Test
  void testColumnByType() {
    // When we get the pets column
    assertAllEquals(
      all(THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY),
      pets.column(A).toArray(Pet[]::new)
    );
  }

  @Test
  void testJoinOne() {
    Args result = orders
      .foreignKey(A, Order::petId)
      .joinOne(pets.primaryKey(A, Pet::id));

    List<Order> orderList = result.column(A);
    orderList.sort(Comparator.comparingLong(Order::id));

    assertTrue(result.isOrdinal());

    assertAllSame(
      orders.column(A).stream().toArray(Order[]::new),
      result.column(A).stream().toArray(Order[]::new)
    );
    assertAllSame(
      all(MICKEY, DUCHESS, DONALD, PLUTO, FREY, MICKEY),
      result.stream(B).toArray(Pet[]::new)
    );
  }

  @Test
  void testJoinOne_missingKeys() {
    assertEquals(
      "missing primary key on right-hand side",
      assertThrows(IllegalArgumentException.class, () -> pets.joinOne(orders)
      ).getMessage()
    );

    orders.primaryKey(A, Order::id);

    assertEquals(
      "missing foreign key on left-hand side",
      assertThrows(IllegalArgumentException.class, () -> pets.joinOne(orders)
      ).getMessage()
    );

    pets.foreignKey(A, Pet::id);

    assertEquals(
      "no primary key found matching to foreign key: 6",
      assertThrows(IllegalArgumentException.class, () -> pets.joinOne(orders)
      ).getMessage()
    );

    assertEquals(
      "missing primary key on right-hand side",
      assertThrows(IllegalArgumentException.class, () -> orders.joinOne(pets)
      ).getMessage()
    );

    pets.primaryKey(A, Pet::id);

    assertEquals(
      "missing foreign key on left-hand side",
      assertThrows(IllegalArgumentException.class, () -> orders.joinOne(pets)
      ).getMessage()
    );

    orders.foreignKey(A, Order::petId).joinOne(pets);
  }

  @Test
  @Disabled
  void testJoinMany() {
    Column<List<Order>> result = pets
      .primaryKey(A, Pet::id)
      .joinMany(orders.foreignKey(A, Order::petId))
      .collect(B);

    assertAllSame(
      PETS.column(A).stream().toArray(Pet[]::new),
      pets.column(A).stream().toArray(Pet[]::new)
    );

    List<Order> empty = List.of();
    assertAllEquals(
      all(empty, List.of(P), List.of(R), empty, List.of(S), List.of(O, T), List.of(Q), empty),
      result.toArray(List[]::new)
    );
  }

  private static Matrix assertClone(Matrix matrix)
    throws CloneNotSupportedException {
    // Given a matrix with corresponding varargs
    final VarArgs varargs = matrix.varArgs();
    final int mask = varargs.mask(), offset = matrix.offset() & mask;
    Object array = varargs.argv[offset];
    assertNotNull(array);
    assertTrue(array.getClass().isArray());

    // When the matrix is cloned
    Matrix clone = (Matrix) matrix.clone();
    VarArgs varargsClone = clone.varArgs();
    Object[] argvClone = varargsClone.argv;

    // Then the clone is not the same
    assertNotSame(matrix, clone);
    assertEquals(mask, clone.varArgs().mask()); // but the mask is

    // And then the clone starts empty
    assertEquals(0, clone.length);
    assertEquals(0, clone.source);
    assertEquals(0, clone.target);
    assertEquals(0, clone.tpos);
    assertNull(clone.pk);
    assertNull(clone.fk);

    // But with the same ordering
    assertEquals(matrix.ordinal % OMEGA.intValue(), clone.ordinal);
    assertSame(matrix.ordinals, clone.ordinals);

    // the columns are not cloned
    assertNull(argvClone[clone.offset() & mask]);

    return clone;
  }

  private static void assertExport(Matrix matrix)
    throws CloneNotSupportedException {
    // Given a matrix with corresponding varargs
    final VarArgs varargs = matrix.varArgs();
    final int mask = varargs.mask(), offset = matrix.offset() & mask;

    // And given a clone of the matrix
    Matrix clone = assertClone(matrix);
    VarArgs varargsClone = clone.varArgs();
    Object[] argvClone = varargsClone.argv;

    // When the matrix is exported to the clone
    //assertEquals(
    //  "expected to be extended at column: 1",
    //  assertThrows(IndexOutOfBoundsException.class, () ->
    //    matrix.export(clone, (byte) 0, 1)
    //  ).getMessage()
    //);
    // we get index out of bounds because it is not empty

    // So when we clear the clone...
    // clone.clear();

    // and then export again
    matrix.export(clone, (byte) 0, 1);

    // Then the data is maintained
    assertSame(varargs.argv[offset], argvClone[clone.offset() & mask]);
    assertNull(argvClone[(clone.offset() + 1) & mask]);
  }
}
