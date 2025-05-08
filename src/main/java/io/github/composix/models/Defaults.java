/**
 * interface Defaults
 * 
 * Base interface that all DTOs should implement. It handles registration, 
 * serialization/deserialization, hashing enhancement, etc. for DTOs.
 *
 * Author: dr. ir. J. M. Valk
 * Last updated: May 2025 
 */

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

/**
 * Interface representing a default value provider for DTOs. It provides an easy
 * way to register and retrieve default values for DTOs. It also provides a way
 * to combine a row of data into a DTO. Furthermore, it provides a way to enhance
 * the hash code of the DTO for better performance in hash-based collections.
 * Based on the improved hashing it provides a default implementation of the
 * Comparable interface.
 * 
 * @param <T> the type of the DTO
 * 
 * @author dr. ir. J. M. Valk
 * @since May 2025
 */
public interface Defaults<T extends Defaults<T>> extends Comparable<T>{
    Map<Class<? extends Defaults<?>>,Defaults<?>> DEFAULTS = new IdentityHashMap<>();

    /**
     * Convenience method to create an empty list of the specified type. To be used
     * to initialize defaults values statically in the DTO definition.
     * 
     * @return an empty list of the specified type
     */
    static <T> List<T> empty() {
        return List.of();
    }
 
    /**
     * Register a DTO to set the default values for that DTO. This method is typically
     * called in a DTO definition via the defaults() method.
     * 
     * @param <T> the type of the DTO
     * @param defaults - the DTO to register if not already registered
     * @return the registered defaults for the DTO
     */
    static <T extends Defaults<T>> T register(Defaults<T> defaults) {
        T result = (T) of(defaults.getClass());
        if (result == null) {
            DEFAULTS.put((Class<? extends Defaults<?>>) defaults.getClass(), defaults);
            return (T) defaults;
        }
        return result;
    }

    /**
     * Retrieve the default values for a DTO by its class.
     * 
     * @param <T> - the type of the DTO
     * @param dto - the class of the DTO
     * @return the registered defaults for the DTO
     */
    static <T extends Defaults<T>> T of(Class<T> dto) {
        return (T) DEFAULTS.get(dto);
    }

    /**
     * Convenience method to register the DTO to set the default values for that
     * DTO. This method is typically called in a DTO definition by instantiating an
     * new DTO with the default values and then calling this method.
     * 
     * For example: new Category(0, null).defaults();
     * 
     * @return the registered defaults for the DTO
     */
    default T defaults() {
        return Defaults.register(this);
    }

    /**
     * To be overridden by the DTO to provide a custom hash code. The default
     * implementation returns the hash code of the DTO itself widened to a long.
     * Implementations should override this method to better avoid hash collisions.
     * Alternatively, the DTO may opt to expose a unquely generated primary key
     * for the DTO as a long; this primary should then be available as a field in the
     * DTO.
     * 
     * @return the hash code of the DTO
     */
    default long primaryHashCode() {
        return hashCode();
    }

    /**
     * To be overridden by the DTO to deserialize the DTO from a row of data. DTOs can
     * used the Row interface to retrieve the data from the row.
     * 
     * @param row - the row of data to deserialize the DTO from
     * @return the deserialized DTO
     */
    T combine(Row row);

    @Override
    default int compareTo(T o) {
        return Long.compare(primaryHashCode(), o.primaryHashCode());
    }
}
