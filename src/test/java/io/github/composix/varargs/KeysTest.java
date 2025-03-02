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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.composix.math.Args;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.models.examples.Tag;
import io.github.composix.testing.TestCase;

class KeysTest extends TestCase {
  static Pet THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY;
  static Order O, P, Q, R, S, T;
  ArgsI<Pet> pets;
  ArgsI<Order> orders;

  @BeforeAll
  static void beforeAll() {
    THOMAS = new Pet(0, "Thomas", SOLD, CATS, EMPTY, IMG_THOMAS);
    DUCHESS = new Pet(1, "Duchess", SOLD, CATS, EMPTY, IMG_DUCHESS);
    PLUTO = new Pet(2, "Pluto", AVAILABLE, DOGS, EMPTY, IMG_PLUTO);
    FRANK = new Pet(3, "Frank", PENDING, DOGS, EMPTY, IMG_FRANK);
    FREY = new Pet(4, "Frey", PENDING, OTHER, MISC, IMG_FREY);
    MICKEY = new Pet(5, "Mickey", AVAILABLE, OTHER, MICE, IMG_MICKEY);
    DONALD = new Pet(6, "Donald", AVAILABLE, OTHER, DUCKS, IMG_DONALD);
    GOOFY = new Pet(7, "Goofy", AVAILABLE, DOGS, MISC, IMG_GOOFY);

    O = new Order(0, 5, 3);
    P = new Order(1, 1, 1);
    Q = new Order(2, 6, 2);
    R = new Order(3, 2, 3);
    S = new Order(4, 4, 4);
    T = new Order(5, 5, 5);
  }

  @BeforeEach
  void setUp() {
    pets = I.extendA(THOMAS, DUCHESS, PLUTO, FRANK, FREY, MICKEY, DONALD, GOOFY);
    orders = G.extendA(O, P, Q, R, S, T);
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

  static final Tag[] EMPTY = new Tag[0];

  static final String[] IMG_THOMAS = new String[0], IMG_DUCHESS =
    new String[0], IMG_PLUTO = new String[1], IMG_FRANK =
    new String[1], IMG_FREY = new String[1], IMG_MICKEY =
    new String[1], IMG_DONALD = new String[1], IMG_GOOFY = new String[0];

  static final Category CATS, DOGS, OTHER;
  static final Tag[] MICE = new Tag[1], DUCKS = new Tag[1], MISC = new Tag[2];
  static final Pet.Status AVAILABLE, PENDING, SOLD;

  static {
    CATS = new Category(0, "cats");
    DOGS = new Category(1, "cats");
    OTHER = new Category(2, "other");

    MICE[0] = new Tag(0, "mice");
    DUCKS[0] = new Tag(1, "ducks");
    MISC[0] = new Tag(2, "misc");

    AVAILABLE = Pet.Status.AVAILABLE;
    PENDING = Pet.Status.PENDING;
    SOLD = Pet.Status.SOLD;

    IMG_PLUTO[0] =
      "https://purepng.com/public/uploads/large/purepng.com-mickey-plutomickey-mousemickeymouseanimal-cartooncharacterwalt-disneyub-iwerksstudioslarge-yellow-shoered-shortswhite-glovesnetflix-1701528649869in1cb.png";
    IMG_FRANK[0] =
      "https://i.pinimg.com/736x/26/58/36/2658362ba9e3d4fb6c76cb25d5cf1cc8.jpg";
    IMG_FREY[0] =
      "https://i.pinimg.com/736x/27/5e/98/275e9850f2cc228c4b847d32b33371c2.jpg";
    IMG_MICKEY[0] =
      "https://purepng.com/public/uploads/large/purepng.com-mickey-mousemickey-mousemickeymouseanimal-cartooncharacterwalt-disneyub-iwerksstudioslarge-yellow-shoered-shortswhite-gloves-1701528648356hyh0y.png";
    IMG_DONALD[0] =
      "https://purepng.com/public/uploads/large/purepng.com-donald-duckdonald-duckdonaldduckcartoon-character1934walt-disneywhite-duck-1701528532131iamxo.png";
  }
}
