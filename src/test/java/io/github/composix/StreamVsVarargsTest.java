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

package io.github.composix;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.composix.math.Args;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;
import io.github.composix.testing.testdata.PetstoreTestData;
import io.github.composix.varargs.ArgsI;
import io.github.composix.varargs.ArgsII;

class StreamVsVarargsTest extends TestCase implements PetstoreTestData {

  ArgsI<Pet> pets;

  @Override
  public Args clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  @BeforeEach
  void beforeEach() {
    // Given the pets from the TestData interface
    pets = ArgsI.of(PETS.stream(A).toArray(Pet[]::new));
  }

  @Test
  void testSumPetIdsByCategory() {
    // Given the stream of pets
    Stream<Pet> streamOfPets = pets.columnA(1).stream();

    // When computing the sum of the pets using streams
    Map<Category, Long> petsByCategoryMap = streamOfPets.collect(
      Collectors.groupingBy(Pet::category, Collectors.summingLong(Pet::id))
    );
    Category[] categories = petsByCategoryMap
      .keySet()
      .stream()
      .toArray(Category[]::new);
    long[] sums = petsByCategoryMap
      .values()
      .stream()
      .mapToLong(Long::longValue)
      .toArray();

    // And when computing the same result using (Var)Args
    ArgsII<long[], Category> petsByCategory = pets
      .groupByA(Pet::category)
      .collectA(Pet::id, Long::sum)
      .longs();

    // Then check that both approaches yield the same result
    assertAllEquals(
      categories,
      petsByCategory.columnA(1).stream().toArray(Category[]::new)
    );
    assertAllEquals(sums, petsByCategory.columnB(1).longStream().toArray());
  }
}
