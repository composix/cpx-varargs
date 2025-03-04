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
import java.util.BitSet;
import java.util.Comparator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class OrderInt extends OrdinalInt implements MutableOrder {
    private static final BitSet MARKED = new BitSet();
 
    Ordinal[] ordinals;

    OrderInt(int ordinal) {
        super(ordinal);
        ordinals = ORDINALS;
    }

    @Override
    public MutableOrder order() {
        return this;
    }
    
    @Override
    public boolean isOrdinal() {
        return ordinals == ORDINALS;
    }
    
    @Override
    public int rank(int index) {
        try {
            return ordinals[index].intValue();
        } catch(ArrayIndexOutOfBoundsException e) {
            return index;
        }
    }

    @Override
    public Ordinal rank(Ordinal index) {
        try {
            return ordinals[index.intValue()];
        } catch(ArrayIndexOutOfBoundsException e) {
            return index;
        }
    }

    @Override
    public void permute(final int target, final int mask, final Object[] array) {
        if (isOrdinal()) {
            return;
        }
        int index = 0;
        MARKED.clear();
        while ((index = cycle(target, mask, index, array)) < ordinal);
    }

    @Override
    public <T> Stream<T> stream(T[] array) {
        if (isOrdinal()) {
            return Stream.of(array);
        }
        return Stream.of(ordinals).mapToInt(Ordinal::intValue).mapToObj(i -> array[i]);
    }

    @Override
    public LongStream stream(long[] array) {
        if (isOrdinal()) {
            return LongStream.of(array);
        }
        return Stream.of(ordinals).mapToInt(Ordinal::intValue).mapToLong(i -> array[i]);
    }

    @Override
    public void resize(int ordinal) {
        if (isOrdinal()) {
            final int omega = OMEGA.intValue();
            this.ordinal = (this.ordinal / omega) * omega + ordinal;
        } else {
            throw new IllegalStateException("cannot resize a non-ordinal order");
        }
    }

    @Override
    public void reorder(Comparator<Ordinal> comparator) {
        int amount = ordinal % OMEGA.intValue();
        if (amount-- > 1) {
            final Ordinal[] omega = ORDINALS;
            if (comparator == NATURAL_ORDER) {
                ordinals = omega;
            } else {
                int i = 0;
                while(i < amount) {
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
        this.ordinals = ordinals;
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

    private int cycle(final int target, final int mask, final int index, Object[] array) {
        final Object value = array[(target + index) & mask];
        int current = index, next = MARKED.nextClearBit(index) + 1;
        while(next <= ordinal) {
            final int rank = rank(current);
            if (rank == index) {
                array[(target + current) & mask] = value;
                return next;
            }
            if (rank == next) {
                next = MARKED.nextClearBit(++next);
            } else {
                MARKED.set(rank);
            }
            array[(target + current) & mask] = array[(target + rank) & mask];
            if (rank < ordinal) {
                current = rank;
            } else {
                current = next;
            }
        }
        return next;
    }
}
