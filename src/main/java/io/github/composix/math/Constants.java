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


class Constants {
    private static Constants INSTANCE; 
    
    static Constants getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Constants();
            for (int i = 0; i <= Short.MAX_VALUE; ++i) {
                INSTANCE.ordinal(i);
            }
            INSTANCE.omega();
            INSTANCE.columns[0] = INSTANCE.ordinals[0];
            INSTANCE.columns[1] = INSTANCE.ordinals[-Short.MIN_VALUE];
            for (int i = 2; i < INSTANCE.columns.length; ++i) {
                INSTANCE.columns[i] = new OrdinalInt(i * -Short.MIN_VALUE);
            }    
        }
        return INSTANCE;
    }

    final Ordinal[] ordinals;
    final Ordinal[] columns;

    private Constants() {
        ordinals = new Ordinal[Short.MAX_VALUE - Short.MIN_VALUE];
        columns = new Ordinal[Byte.MAX_VALUE - Byte.MIN_VALUE];
    }

    Ordinal ordinal(int index) {
        Ordinal ordinal = ordinals[index];
        if (ordinal == null) {
            ordinal = new OrdinalInt(index);
            if (ordinals[index] == null) {
                ordinals[index] = ordinal;  
            } else {
                ordinal = ordinals[index];
            }
        }
        return ordinal;
    }

    Ordinal omega() {
        return ordinal(-Short.MIN_VALUE);
    }
}
