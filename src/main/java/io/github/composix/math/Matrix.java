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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class Matrix extends OrderInt implements Args {
    static Object[] OBJECT = new Object[1];
    
    private Object[] argv;
    private Order order;

    Matrix() {
        this(0);
    }

    Matrix(int ordinal) {
        super(ordinal);
        ordinals = ORDINALS;
        argv = ArgsOrdinal.OBJECTS;
        order = (Order) ORDINALS[ordinal];
    }

    @Override
    public Args clone() {
        try {
            return (Args) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public Order order() {
        return order;
    }

    @Override
    public Args extend(Ordinal col, Object... arrays) {
        if (!OMEGA.contains(col)) {
            throw new IndexOutOfBoundsException();
        }
        final int length = arrays.length, index = col.intValue();
        if (index + length > argv.length) {
            for (int i = 0; i < length; ++i) {
                col = col.next();
            }
            argv = col.copyOf(argv);
        }
        ordinal = col.intValue();
        if (order.ordinal() != order) {
            throw new IllegalStateException("cannot extend matrix after sorting");
        }
        int amount = ((OrdinalInt) order).ordinal;
        for (int i = 0; i < length; ++i) {
            amount = Math.max(amount, Array.getLength(arrays[i]));
            if (argv[index + i] == null) {
                argv[index +i] = arrays[i];
            } else {
                throw new UnsupportedOperationException("not yet implemented");
            }
        }
        order = (Order) ORDINALS[amount];
        return this;
    }

    @Override
    public <T> T getValue(int index) {
        return OMEGA.getValue(argv, index, ordinals);
    }

    @Override
    public long getLongValue(int index) {
        return OMEGA.getLongValue(argv, index, ordinals);
    }

    @Override
    public <T> Stream<T> stream(Ordinal ordinal) {
        return (Stream<T>) order.stream((Object[]) argv[ordinal.intValue()]);
    }

    @Override
    public LongStream longStream(Ordinal ordinal) {
        return order.stream((long[]) argv[ordinal.intValue()]);
    }

    @Override
    public Args select(Order order) {
        if (order instanceof Matrix) {
            ((Matrix) order).argv = argv;
            return (Args) order;
        }
        Matrix result = new Matrix(order.ordinal().intValue());
        result.argv = argv;
        result.ordinals = ((OrderInt) order).ordinals;
        return result;
    }

    @Override
    public Args orderBy(Ordinal ordinal) {
        MutableOrder result;
        if (order instanceof MutableOrder) {
            result = (MutableOrder) order;
        } else {
            try {
                result = (MutableOrder) (order = order.ordinal().clone());
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }
        result.reorder(comparator(ordinal));
        return this;
    }

    @Override
    public Ordinal ordinalAt(final Ordinal col, Object value) {
        if (order.isOrdinal()) {
            if (value.getClass() == Long.class) {
                final int index = Arrays.binarySearch((long[]) argv[col.intValue()], ((Long) value).longValue());
                return index < 0 ? OMEGA : ORDINALS[index];    
            }
            final int index = Arrays.binarySearch((Object[]) argv[col.intValue()], value);
            return index < 0 ? OMEGA : ORDINALS[index];
        }
        return ((MutableOrder) order).ordinalAt(value, (row, key) -> 
            ((Comparable<Object>) Array.get(
                argv[col.intValue()],
                ((OrdinalInt) row).ordinal)
            ).compareTo(key)
        );
    }
}
