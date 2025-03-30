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

import org.junit.jupiter.api.Test;

import io.github.composix.apis.Api;
import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.varargs.ArgsI;
import io.github.composix.varargs.ArgsII;

class QuickStartTest {

    static Api petStoreApi = Api.select(ArgsI
        // Title for the Swagger Petstore API (OpenAPI 3.0 version)
        .of("title", "Swagger Petstore - OpenAPI 3.0")  
        
        // Alternatively, use the older Swagger Petstore v2 API by uncommenting the next line:
        // .of("title", "Swagger Petstore")  // Title for the Swagger Petstore v2 API (OpenAPI 2.0)

        .andOf("urls",
            "https://petstore.swagger.io/v2/swagger.json",  // Swagger v2 API JSON
            "https://petstore3.swagger.io/api/v3/openapi.json"  // Swagger v3 API JSON
        )
    );
    
  @Test
  void testShowListOfCategoryNames() throws IOException {
        // Retrieve all available pets from the API
        ArgsI<Pet> pets = petStoreApi.resource(        
            (CharSequence) "/pet/findByStatus",  // API endpoint path
            Pet.class           // Data transfer object (DTO) for pets        
        ).get("?status=available");

        // Group pets by category and count them
        ArgsII<Category, long[]> categories = pets
            .groupByA(Pet::category)
            .collectA(x -> 1L, Long::sum)
            .toArgsII();

        // Group categories by name and count them
        ArgsII<String, long[]> categoryNames = categories
            .groupByA(Category::name)
            .collectA(x -> 1L, Long::sum)
            .toArgsII();

        // Output all category names with their counts
        System.out.println("List of available categories:");
        for (CharSequence categoryName : categoryNames.columnA()) {
            System.out.println(categoryName);
        }
  }
}
