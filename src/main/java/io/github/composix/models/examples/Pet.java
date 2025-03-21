/**
 * MIT License
 *
 * Copyright (c) 2024-2025 ComPosiX
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

package io.github.composix.models.examples;

import java.util.Comparator;

import io.github.composix.models.Defaults;

public record Pet(long id, String name, Status status, Category category, Tag[] tags, String[] photoUrls) {
    private static Category CATEGORY = Defaults.of(Category.class);

    public Pet {
        status = status == null ? Status.AVAILABLE : status;
        category = category == null ? CATEGORY : category;
        tags = tags == null ? new Tag[0] : tags;
        photoUrls = photoUrls == null ? new String[0] : photoUrls;
    }

    public Comparator<Pet> byCategory() {
        return Comparator.comparing(Pet::category);
    }
    
    public enum Status {
        AVAILABLE, PENDING, SOLD
    };
}
