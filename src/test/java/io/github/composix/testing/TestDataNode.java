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

package io.github.composix.testing;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.stream.Stream;

class TestDataNode extends CharSequenceNode implements TestData {
    static final short SHIFT = Byte.SIZE, MASK = (1 << SHIFT) - 1;
    
    final TestDataRoot root;
    final short range;

    private Object[] data;

    TestDataNode(CharSequence segment, TestDataRoot root, short range) {
        super(segment);
        this.root = root;
        this.range = range;
    }

    @Override
    public <R extends Record> TestData refresh(Class<R> type) throws IOException {
        final URI uri = URI.create(pathToUrl((byte) (range >> SHIFT), root.paths.get(range & MASK)));
        try {
            data = root.mapper.readValue(root.base.resolve(uri).toURL(), Object[].class);
        } catch(IOException e) {
            final URI base = URI.create(root.wm.getHttpBaseUrl());
            data = (Object[]) root.mapper.readValue(base.resolve(uri).toURL(), type.arrayType());
        }
        return this;
    }

    @Override
    public TestData select(CharSequence... path) {
        if ("~".equals(path[0].toString())) {
            root.select(path);
        }
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public <T> Stream<T> collect() {
        return (Stream<T>) Stream.of(data);
    }    

    private String pathToUrl(final byte size, final CharSequence... path) {
        final StringWriter writer = new StringWriter();
        boolean flag = false;
        for (int i = 1; i < size; ++i) {
            final CharSequence part = path[i];
            switch(part.charAt(0)) {
                case '?':
                    if (flag) {
                        writer.append('&');
                    }
                case '=':
                    writer.append(part);
                    break;
                default:
                    writer.append('/');
                    writer.append(part);
            }
        }
        return writer.toString();
    }
}
