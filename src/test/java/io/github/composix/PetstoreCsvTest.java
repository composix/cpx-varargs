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

import io.github.composix.math.Args;
import io.github.composix.math.Column;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.models.examples.Tag;
import io.github.composix.testing.TestCase;
import io.github.composix.testing.TestData;
import io.github.composix.testing.testdata.PetstoreTestData;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PetstoreCsvTest extends TestCase implements PetstoreTestData {

  static TestData testData;

  Args petstore;

  @Override
  public Args clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  @BeforeAll
  static void beforeAll() throws IOException, URISyntaxException {
    testData = DEFAULT.testData("src/test/resources/", Optional.empty());
  }

  @BeforeEach
  void beforeEach() {
    petstore = F.extend(
      A,
      "categories.csv",
      "petstore.csv",
      "photoUrls.csv",
      "tagging.csv",
      "tags.csv"
    );
    petstore.extend(
      B,
      petstore
        .stream(A)
        .map(filename -> {
          CharSequence[] lines;
          try {
            lines = testData
              .select("~", (String) filename)
              .refreshLines()
              .collect()
              .toArray(CharSequence[]::new);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
          return OMEGA.extend(lines).split(PATTERN);
      })
        .toArray(Args[]::new)
    );
  }

  @Test
  void testCSV()
    throws IOException, CloneNotSupportedException, NoSuchFieldException {
    Args photoUrls = petstore
      .getArgsValue(B.index(C))
      .fk("petId:", AL)
      .attr("photoUrl:", S);

    Args categories = petstore
      .getArgsValue(B.index(A))
      .combine(Category.DEFAULTS).attach();

    categories.primaryKey(A, Category::id);

    Args tags = petstore.getArgsValue(B.index(E)).combine(Tag.DEFAULTS).attach();

    tags.primaryKey(A, Tag::id);

    Args tagging = petstore
      .getArgsValue(B.index(D))
      .fk("tagId:", AL)
      .joinOne(tags)
      .fk("petId:", AL);

    Column<Pet> pets = petstore
      .getArgsValue(B.index(B))
      .fk("categoryId:", AL)
      .joinOne(categories)
      .pk("id:", AL)
      .joinMany(tagging)
      .joinMany(photoUrls)
      .combine(Pet.DEFAULTS);

    assertAllEquals(
      PETS.column(A).stream().toArray(Pet[]::new),
      pets.stream().toArray(Pet[]::new)
    );
  }
}
