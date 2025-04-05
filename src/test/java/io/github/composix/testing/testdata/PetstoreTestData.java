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

package io.github.composix.testing.testdata;

import java.util.Arrays;
import java.util.List;

import io.github.composix.math.Args;
import io.github.composix.math.ArgsOrdinal;
import io.github.composix.math.Ordinal;
import io.github.composix.models.Defaults;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.models.examples.Tag;

public interface PetstoreTestData extends ArgsOrdinal {
  static Args PETS = pets(I);
  static Args ORDERS = orders(H);

  static Args pets(Ordinal amount) {
    final List<Tag> EMPTY = Defaults.empty();

    final String[] IMG_THOMAS = new String[0], IMG_DUCHESS =
      new String[0], IMG_PLUTO = new String[1], IMG_FRANK =
      new String[1], IMG_FREY = new String[1], IMG_MICKEY =
      new String[1], IMG_DONALD = new String[1], IMG_GOOFY = new String[0];

    final Category CATS, DOGS, OTHER;
    final Tag[] MICE = new Tag[1], DUCKS = new Tag[1], MISC = new Tag[1];
    final Pet.Status AVAILABLE, PENDING, SOLD;
    CATS = new Category(0, "cats");
    DOGS = new Category(1, "dogs");
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

    return amount.extend(
      A,
      new Pet(0, "Thomas", SOLD, CATS, EMPTY, Arrays.asList(IMG_THOMAS)),
      new Pet(1, "Duchess", SOLD, CATS, EMPTY, Arrays.asList(IMG_DUCHESS)),
      new Pet(2, "Pluto", AVAILABLE, DOGS, EMPTY, Arrays.asList(IMG_PLUTO)),
      new Pet(3, "Frank", PENDING, DOGS, EMPTY, Arrays.asList(IMG_FRANK)),
      new Pet(4, "Frey", PENDING, OTHER, Arrays.asList(MISC), Arrays.asList(IMG_FREY)),
      new Pet(5, "Mickey", AVAILABLE, OTHER, Arrays.asList(MICE), Arrays.asList(IMG_MICKEY)),
      new Pet(6, "Donald", AVAILABLE, OTHER, Arrays.asList(DUCKS), Arrays.asList(IMG_DONALD)),
      new Pet(7, "Goofy", AVAILABLE, DOGS, EMPTY, Arrays.asList(IMG_GOOFY))
    );
  }

  static Args orders(Ordinal amount) {
    return amount.extend(
      A,
      new Order(0, 5, 1),
      new Order(1, 1, 1),
      new Order(2, 6, 1),
      new Order(3, 2, 2),
      new Order(4, 4, 2),
      new Order(5, 5, 2)
    );
  }
}
