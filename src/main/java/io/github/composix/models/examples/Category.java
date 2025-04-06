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
import java.util.Objects;

import io.github.composix.math.Row;
import io.github.composix.models.Defaults;

public record Category(long id, String name) implements Comparable<Category>, Defaults<Category> {
    public static Category DEFAULTS = new Category(0, null).defaults();

    public Category {
        name = name == null ? "" : name;
    }

    @Override
    public int compareTo(Category that) {
        return Objects.compare(this, that, 
            Comparator
                .comparing(Category::name)
                .thenComparingLong(Category::id)
        );
    }

    @Override
    public Category combine(Row row) {
        return new Category(
            row.getLong(0),
            row.get(1).toString()
        );
    }
}
