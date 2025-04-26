/**
 * JUnit Test to demonstrate the retrieval of all available pet categories
 * from the Swagger Petstore API endpoint /pet/findByStatus.
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

package io.github.composix;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.composix.apis.Api;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;
import io.github.composix.varargs.ArgsI;
import io.github.composix.varargs.Chars;

class QuickStartTest extends TestCase {

  static Api petStoreApi;

  @BeforeAll
  static void setup() {
    petStoreApi = Api.select(
      Chars.of(
        "title:",
        "Swagger Petstore - OpenAPI 3.0" // Title for the Swagger Petstore API (OpenAPI 3.0 version)
      )// Alternatively, use the older Swagger Petstore v2 API by uncommenting the next line:
      // .of("title:", "Swagger Petstore")  // Title for the Swagger Petstore v2 API (OpenAPI 2.0)

      .andOf(
        "url:",
        "https://petstore.swagger.io/v2/swagger.json", // Swagger v2 API JSON
        "https://petstore3.swagger.io/api/v3/openapi.json" // Swagger v3 API JSON
      )
    );
  }

//  @Test
  void testAvailablePetCategories() throws IOException {
    // Given all available pets from the API based on their status
    ArgsI<Pet> pets = petStoreApi
      .resource(
        (CharSequence) "/pet/findByStatus", // API endpoint to fetch pets by status
        Pet.class // Pet class as the DTO
      )
      .get("?status=available"); // Fetch pets with the status "available"

    // When using grouping to get unique category names from the list of pets
    ArgsI<String> categoryNames = pets
      .groupByA(Pet::category) // group by pet category
      .done() // collect distinct categories into ArgsI<Category>
      .groupByA(Category::name) // Group by category name
      .done(); // collect again to get the list of distinct category names

    // Then the Stream approach yields the same results
    assertAllEquals(
      pets
        .columnA(1)
        .stream()
        .map(Pet::category)
        .map(Category::name)
        .distinct()
        .sorted()
        .toArray(String[]::new),
      categoryNames.columnA(1).stream().toArray(String[]::new)
    );
  }
}
