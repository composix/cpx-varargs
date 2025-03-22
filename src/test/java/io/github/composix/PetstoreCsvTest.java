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
import io.github.composix.math.Cursor;
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
  extends TestCase
  implements io.github.composix.math.TestData {

  static TestData testData;
  static String[] VALUES;
  static Cursor CURSOR;

  @BeforeAll
  static void beforeAll() throws IOException, URISyntaxException {
    testData = DEFAULT.testData("src/test/resources/", Optional.empty());
    VALUES = new String[4];
    CURSOR = Cursor.ofRow(VALUES);
  }

  @Test
  void testCSV() throws IOException, CloneNotSupportedException {
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
            final ArgsI<String> csv = A.extendA(
              testData
                .select("~", filename)
                .refreshLines()
                .collect()
                .toArray(CharSequence[]::new)
            ).split(PATTERN::splitAsStream);
            csv.order().skipHeader();
            return csv;
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        })
        .toArray(Args[]::new)
    );

    ArgsI<CharSequence> photoUrls = petstore.getArgsValue(B.index(C)).castI(2);

    ArgsI<Category> categories = petstore
      .getArgsValue(B.index(A))
      .castI(2)
      .combine(Category.DEFAULTS);

    ArgsI<Tag> tags = petstore
      .getArgsValue(B.index(E))
      .castI(2)
      .combine(Tag.DEFAULTS);

    ArgsI<CharSequence> tagging = petstore
      .getArgsValue(B.index(D))
      .castI(2)
      .on(B, Args::parseLong)
      .joinOne(tags.on(A, Tag::id))
      .castI(3, CharSequence.class);

    ArgsI<Pet> pets = petstore
      .getArgsValue(B.index(B))
      .castI(4)
      .onI(D, Args::parseLong)
      .joinOne(categories.on(A, Category::id))
      .on(A)
      .joinMany(tagging.on(A))
      .on(A)
      .joinMany(photoUrls.on(A))
      .castI(4)
      .combine(Pet.DEFAULTS);

    assertAllEquals(PETS, pets);
  }
}
