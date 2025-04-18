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

package io.github.composix.models;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import io.github.composix.math.Row;

public interface Defaults<T extends Defaults<T>> {
    Map<Class<? extends Defaults<?>>,Defaults<?>> DEFAULTS = new IdentityHashMap<>();

    static <T> List<T> empty() {
        return List.of();
    }
 
    static <T extends Defaults<T>> T register(Defaults<T> defaults) {
        T result = (T) of(defaults.getClass());
        if (result == null) {
            DEFAULTS.put((Class<? extends Defaults<?>>) defaults.getClass(), defaults);
            return (T) defaults;
        }
        return result;
    }

    static <T extends Defaults<T>> T of(Class<T> dto) {
        return (T) DEFAULTS.get(dto);
    }

    default T defaults() {
        return Defaults.register(this);
    }

    T combine(Row row);
}
