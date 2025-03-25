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
import io.github.composix.math.Row;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.models.examples.Tag;
import io.github.composix.testing.TestCase;
import io.github.composix.testing.TestData;
import io.github.composix.varargs.ArgsI;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PetstoreCsvTest
  extends TestCase {

  static TestData testData;

  @BeforeAll
  static void beforeAll() throws IOException, URISyntaxException {
    testData = DEFAULT.testData("src/test/resources/", Optional.empty());
  }

  @Test
  void testCSV()
    throws IOException, CloneNotSupportedException, NoSuchFieldException {
    ArgsI<String> petstore = F.extendA(
      "categories.csv",
      "petstore.csv",
      "photoUrls.csv",
      "tagging.csv",
      "tags.csv"
    );
    petstore.extend(
      B,
      petstore
        .streamA()
        .map(filename -> {
          try {
            final Args csv = A.extend(
              A,
              testData
                .select("~", filename)
                .refreshLines()
                .collect()
                .toArray(CharSequence[]::new)
            ).split(PATTERN);
            csv.castI(csv.size());
            csv.order().skipHeader();
            return csv;
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        })
        .toArray(Args[]::new)
    );

    Args photoUrls = petstore.getArgsValue(B.index(C));

    Args categories = petstore
      .getArgsValue(B.index(A))
      .combine(Category.DEFAULTS);

    Args tags = petstore.getArgsValue(B.index(E)).combine(Tag.DEFAULTS);

    Args tagging = petstore
      .getArgsValue(B.index(D))
      .on(B, Row::parseLong)
      .joinOne(tags.on(A, Tag::id));

    Args pets = petstore
      .getArgsValue(B.index(B))
      .on(D, Row::parseLong)
      .joinOne(categories.on(A, Category::id))
      .on(A, Row::parseLong)
      .joinMany(tagging.on(A, Row::parseLong))
      .on(A, Row::parseLong)
      .joinMany(photoUrls.on(A, Row::parseLong))
      .combine(Pet.DEFAULTS);

    assertAllEquals(
      io.github.composix.math.TestData.pets(I).stream(A).toArray(Pet[]::new),
      pets.stream(A).toArray(Pet[]::new)
    );
  }
}
