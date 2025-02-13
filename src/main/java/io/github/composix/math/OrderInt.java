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

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

class OrderInt extends OrdinalInt implements MutableOrder {
    Ordinal[] ordinals;

    OrderInt(int ordinal) {
        super(ordinal);
        ordinals = ORDINALS;
    }

    @Override
    public boolean isOrdinal() {
        return ordinals == ORDINALS;
    }
    
    @Override
    public int rank(int index) {
        return ordinals[index].intValue();
    }

    @Override
    public Ordinal rank(Ordinal index) {
        return ordinals[index.intValue()];
    }

    @Override
    public void resize(int ordinal) {
        if (isOrdinal()) {
            this.ordinal = ordinal;
        } else {
            throw new IllegalStateException("cannot resize a non-ordinal order");
        }
    }

    @Override
    public void reorder(Comparator<Ordinal> comparator) {
        if (ordinal > 1) {
            final Ordinal[] omega = ORDINALS;
            if (comparator == NATURAL_ORDER) {
                ordinals = omega;
            } else {
                int i = 0, n = ordinal - 1;
                while(i < n) {
                    if (comparator.compare(omega[i], omega[++i]) > 0) {
                        if (ordinals == omega) {
                            ordinals = copyOf(omega);
                        }
                        Arrays.sort(ordinals, comparator);
                        return;
                    }
                }
                ordinals = omega;
            }    
        }
    }

    @Override
    public void reorder(Ordinal... ordinals) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reorder'");
    }

    @Override
    public Ordinal ordinalAt(Object key, Comparator<Object> comparator) {
        final int index = Arrays.binarySearch((Object[]) ordinals, key, comparator);
        return index < 0 ? OMEGA : ORDINALS[index];
    }

    @Override
    public Stream<Object[]> streamArgv(int size, Object[] argv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamArgv'");
    }

    @Override
    public Object[] permute(Object... items) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'permute'");
    }
}
