/**
 * class ArgsList
 *
 * The ArgsList interface provides a list- and setview on the columns provided
 * by the Args interface. The listview presents the values in a given column
 * (without the optional header) ordered according the current ordering as set
 * by the Args::orderBy method. The listview is backed by the tabular structure,
 * so sorting the list using List::sort changes the row ordering on the underlying
 * tabular data. Vice versa, changing the order using Args::orderBy will appear as
 * a changed ordering of the elements in the list view.
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

import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.LongStream;

public interface ArgsList<E> extends CharSequence, List<E>, RandomAccess {
  long getLong(int index);

  LongStream longStream();

  ListSet<E> asListSet();

  default int binarySearch(Object item) {
    return Collections.binarySearch(
      (List<? extends Comparable<? super Comparable<?>>>) this,
      (Comparable<?>) item
    );
  }

  @Override
  boolean isEmpty();
}
