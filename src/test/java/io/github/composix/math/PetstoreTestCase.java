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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;
import io.github.composix.testing.testdata.PetstoreTestData;

class PetstoreTestCase extends TestCase implements PetstoreTestData {

  static Pet THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY;
  static Order O, P, Q, R, S, T;
  static Order[] EMPTY = new Order[0];

  Matrix pets, orders;
  VarArgs petVarArgs, orderVarArgs;

  @Override
  public Args clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  @BeforeAll
  static void beforeAll() {
    assertTrue(PETS.isOrdinal());
    
    THOMAS = PETS.getValue(0);
    DUCHESS = PETS.getValue(1);
    PLUTO = PETS.getValue(2);
    FRANK = PETS.getValue(3);
    FREY = PETS.getValue(4);
    MICKEY = PETS.getValue(5);
    DONALD = PETS.getValue(6);
    GOOFY = PETS.getValue(7);

    Column<Pet> petCol = PETS.column(A);
    THOMAS = petCol.get(0);
    DUCHESS = petCol.get(1);
    PLUTO = petCol.get(2);
    FRANK = petCol.get(3);
    FREY = petCol.get(4);
    MICKEY = petCol.get(5);
    DONALD = petCol.get(6);
    GOOFY = petCol.get(7);

    Pet[] pets = (Pet[]) petCol.source();
    THOMAS = petCol.get(0);
    DUCHESS = pets[1];
    PLUTO = pets[2];
    FRANK = pets[3];
    FREY = pets[4];
    MICKEY = pets[5];
    DONALD = pets[6];
    GOOFY = pets[7];

    O = ORDERS.getValue(0);
    P = ORDERS.getValue(1);
    Q = ORDERS.getValue(2);
    R = ORDERS.getValue(3);
    S = ORDERS.getValue(4);
    T = ORDERS.getValue(5);
  }

  @BeforeEach
  void setUp() throws CloneNotSupportedException {
    // Given two matrices...
    pets = (Matrix) PETS.clone();
    orders = (Matrix) ORDERS.clone();

    // ...populated with data from the TestData interface
    PETS.export(pets, (byte) 0, 1);
    ORDERS.export(orders, (byte) 0, 1);

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
}
