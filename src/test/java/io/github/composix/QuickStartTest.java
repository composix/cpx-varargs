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
import java.net.URI;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.composix.models.examples.Category;
import io.github.composix.models.examples.Pet;
import io.github.composix.varargs.ArgsI;

class QuickStartTest {
    static ObjectMapper MAPPER = new ObjectMapper();
    
    @Test
    void testShowListOfCategoryNames() {
        // code below is a verbatim copy of the main() method in README.md

        // Configure Jackson to accept case-insensitive enums
        MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

        // Url to OpenAPI spec of Petstore API
        URI apiUri = URI
            // .create("https://petstore.swagger.io/v2/pet/findByStatus?status=available");
            .create("https://petstore3.swagger.io/api/v3/openapi.json");

        // Retrieve all available Pets using Jackson
        ArgsI<Pet> pets;
        try {
            pets = ArgsI.of(MAPPER
                .readValue(apiUri
                    .resolve("pet/findByStatus?status=available")
                        .toURL(),
                    Pet[].class
                )
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // group by category with pet counts
        ArgsI<Category> categories = pets
            .groupByA(Pet::category)
            .collectA(x -> 1L, Long::sum);

        // group categories by name
        ArgsI<String> categoryNames = categories
            .groupByA(Category::name)
            .collectA(x -> 1L, Long::sum);

        // show a list of all category names
        for (String categoryName : categoryNames.columnA()) {
            System.out.println(categoryName);
        }
    }
}
