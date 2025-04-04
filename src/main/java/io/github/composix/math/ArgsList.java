/**
 * class ArgsList
 * 
 * This class provides a list implementation that is immutable in terms of its
 * contents, but the order of the elements can be changed by modifying the sort 
 * order based on the associated matrix's ordinals.
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

package io.github.composix.math;

import java.util.AbstractList;

public class ArgsList<E> extends AbstractList<E> {
    final Matrix matrix;
    final E[] source;

    ArgsList(Matrix matrix, E[] source) {
        this.matrix = matrix;
        this.source = source;
    }

    @Override
    public int size() {
        return matrix.amount();
    }

    @Override
    public E get(int index) {
        final int i = matrix.ordinals[index].intValue();
        return source[i];
    }

    // TODO: override the sort method to sort the elements based on the ordinals
}
