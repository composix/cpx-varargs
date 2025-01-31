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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;

class TestDataRoot extends CharSequenceNode implements TestData {
    WireMockRuntimeInfo wm;
    URI base;
    ObjectMapper mapper;

    List<CharSequence[]> paths;

    static int compare(CharSequence[] lhs, CharSequence[] rhs) {
        return Arrays.compare(lhs, rhs, Comparator.comparing(Object::toString));
    }

    TestDataRoot(WireMockRuntimeInfo wm, String baseUrl) {
        super(".");
        this.wm = wm;
        base = URI.create(baseUrl);
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        paths = new ArrayList<>();
    }

    @Override
    public TestData select(CharSequence... path) {
        final int length = path.length;
        int index = Collections.binarySearch(paths, path, TestDataRoot::compare);
        if (index < 0) {
            index = -index - 1;
            if (index == paths.size()) {
                if (!"~".equals(path[0])) {
                    throw new IllegalArgumentException();
                }    
                paths.add(path);
            }
            CharSequence[] rhs = paths.get(index);
            for (int i = 0; i < length; ++i) {
                if (!path[i].equals(rhs[i])) {
                    if (!"~".equals(path[0])) {
                        throw new IllegalArgumentException();
                    }    
                    paths.add(index, path);
                    break;
                }
            }
        }
        short range = (short) (path.length << TestDataNode.SHIFT);
        range |= index;
        final int lastIndex = path.length - 1;
        CharSequence result = path[lastIndex];
        if (!(result instanceof TestData)) {
            result = path[lastIndex] = new TestDataNode(path[lastIndex], this, range);
        }
        return (TestData) result;
    }

    @Override
    public <R extends Record> TestData refresh(Class<R> recordClass) throws IOException {
        try {
            paths.forEach(path -> {
                final int length = path.length;
                for (int i = 0; i < length; ++i) {
                    CharSequence segment = path[i];
                    if (segment instanceof TestData) {
                        try {
                            ((TestData)segment).refresh(recordClass);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            });    
        } catch(IllegalStateException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
        return this;
    }

    @Override
    public <T> Stream<T> collect() {
        return Stream.empty();
    }
}
