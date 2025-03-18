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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.composix.math.Args;
import io.github.composix.math.Cursor;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.models.examples.Pet.Status;
import io.github.composix.models.examples.Tag;
import io.github.composix.testing.TestCase;
import io.github.composix.testing.TestData;
import io.github.composix.varargs.ArgsI;

public class PetstoreCsvTest extends TestCase {

  static TestData testData;
  static String[] VALUES;
  static Cursor CURSOR;

  @BeforeAll
  static void beforeAll()
    throws IOException, URISyntaxException {
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
            return A.extendA(
              testData
                .select("~", filename)
                .refreshLines()
                .collect()
                .toArray(CharSequence[]::new)
            ).split(PATTERN::splitAsStream);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        })
        .toArray(Args[]::new)
    );
    ArgsI<Category> categories = categories(petstore.getValue(B.index(A)));
    ArgsI<long[]> photoUrls = photoUrls(petstore.getValue(B.index(C)));
    ArgsI<Tag> tags = tags(petstore.getValue(B.index(E)));
    ArgsI<long[]> tagging = tagging(petstore.getValue(B.index(D)), tags);
    ArgsI<Pet> pets = pets(
      petstore.getValue(B.index(B)),
      categories,
      tagging,
      photoUrls
    );
  }

  static ArgsI<Category> categories(Args args)
    throws CloneNotSupportedException {
    final int amount = args.amount() - 1;
    final Category[] categories = new Category[amount];
    CURSOR.position(A, args);
    CURSOR.cols(2);
    assertTrue(CURSOR.advance(B));
    for (int i = 0; i < amount; ++i) {
      assertTrue(CURSOR.advance(B));
      categories[i] = new Category(
        Long.parseLong(VALUES[0].toString()),
        VALUES[1].toString()
      );
    }
    return A.extendA(categories);
  }

  static ArgsI<Tag> tags(Args args) {
    final int amount = args.amount() - 1;
    final Tag[] tags = new Tag[amount];
    CURSOR.position(A, args);
    CURSOR.cols(2);
    assertTrue(CURSOR.advance(B));
    for (int i = 0; i < amount; ++i) {
      assertTrue(CURSOR.advance(B));
      tags[i] = new Tag(
        Long.parseLong(VALUES[0].toString()),
        VALUES[1].toString()
      );
    }
    return A.extendA(tags);
  }

  static ArgsI<long[]> tagging(ArgsI<String> args, ArgsI<Tag> tags) {
    return A.extendA(
      args.streamA().skip(1).mapToLong(Long::parseLong).toArray()
    )
      .extend(
        B,
        args
          .stream(B)
          .skip(1)
          .map(Object::toString)
          .mapToLong(Long::parseLong)
          .toArray()
      )
      .on(B)
      .joinOne(tags.on(A, Tag::id))
      .castI(long[].class);
  }

  static ArgsI<long[]> photoUrls(ArgsI<String> args) {
    return args.extendA(
      LongStream.concat(
        LongStream.of(-1),
        args.streamA().skip(1).mapToLong(Long::parseLong)
      ).toArray()
    );
  }

  static ArgsI<Pet> pets(
    Args args,
    ArgsI<Category> categories,
    ArgsI<long[]> tagging,
    ArgsI<long[]> photoUrls
  ) {
    final int amount = args.amount() - 1;
    final Pet[] pets = new Pet[amount];
    CURSOR.position(A, args);
    CURSOR.cols(4);
    assertTrue(CURSOR.advance(B));
    for (int i = 0; i < amount; ++i) {
      CURSOR.advance(B);
      pets[i] = new Pet(
        Long.parseLong(VALUES[0]),
        VALUES[1],
        Status.valueOf(VALUES[2]),
        null,
        new Tag[0],
        STRINGS
      );
    }
    return args.extendA(pets);
  }
}
