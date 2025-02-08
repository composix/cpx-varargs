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

import java.util.stream.LongStream;
import java.util.stream.Stream;

class Matrix<A,B,C,D,E,F,G,H,Ii,J,K,L,M> extends OrdinalInt implements Args {
    static Object[] OBJECT = new Object[1];
    
    private Object[] argv;
    private Order order;

    Matrix() {
        super(0);
        argv = ArgsOrdinal.OBJECTS;
        order = (Order) A;
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
        for (int i = 0; i < length; ++i) {
            if (argv[index + i] == null) {
                argv[index +i] = arrays[i];
            } else {
                throw new UnsupportedOperationException("not yet implemented");
            }
        }
        return this;
    }

    @Override
    public <T> T getValue(int index) {
        return OMEGA.getValue(argv, index);
    }

    @Override
    public long getLongValue(int index) {
        return OMEGA.getLongValue(argv, index);
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
    public Ordinal ordinalAt(Ordinal ordinal, Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ordinalAt'");
    }

    @Override
    public Args select(Order order) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }
}
