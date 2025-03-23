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
import io.github.composix.models.DefaultsIII;

public record Pet(long id, CharSequence name, Status status, Category category, Tag[] tags, String[] photoUrls) implements DefaultsIII<Pet,Category,Tag[],String[]> {
    private static Category CATEGORY = Defaults.of(Category.class);
    private static Tag[] TAGS = new Tag[0];
    private static String[] PHOTO_URLS = new String[0];
    
    public static Pet DEFAULTS = new Pet(0, null, Status.AVAILABLE, CATEGORY, TAGS, PHOTO_URLS).defaults(); 

    public Pet {
        status = status == null ? Status.AVAILABLE : status;
        category = category == null ? CATEGORY : category;
        tags = tags == null ? TAGS : tags;
        photoUrls = photoUrls == null ? PHOTO_URLS : photoUrls;
    }

    public Comparator<Pet> byCategory() {
        return Comparator.comparing(Pet::category);
    }
    
    @Override
    public Pet combine(CharSequence[] parts) {
        return combine(parts, CATEGORY, TAGS, PHOTO_URLS);
    }

    @Override
    public Pet combine(CharSequence[] parts, Category category, Tag[] tags, String[] photoUrls) {
        return new Pet(Long.parseLong(parts[0].toString()), parts[1], Status.valueOf(parts[2].toString()), category, tags, photoUrls);
    }

    public enum Status {
        AVAILABLE, PENDING, SOLD
    };
}
